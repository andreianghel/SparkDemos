package full.app;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;

import kafka.serializer.StringDecoder;

/**
 * Program that generates a stream of messages to be put in Kafka, read by Spark
 * streaming and persisted in Cassandra.
 * 
 * @author andrei
 *
 */
public class Main {

	public static final String KEYSPACE_NAME = "test_keyspace_numbers";

	public static final String TABLE_1_NAME = "table_odd_numbers";
	public static final String TABLE_2_NAME = "table_even_numbers";

	private static final int NR_MESSAGES = 1000;
	public static final String TOPIC_NAME = "topic_name_numbers";

	public static void main(String[] args) throws InterruptedException {

		SparkConf conf = new SparkConf();
		conf.setAppName("Big App Name");
		conf.setMaster("local[*]");
		conf.set("spark.cassandra.connection.host", "localhost");
		conf.set("spark.cassandra.connection.port", "9042");

		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(2000));

		createCassandraKeyspaceAndTables(sc);

		KafkaProducer<String, String> kafkaProducer = createKafkaProducer(sc);

		// this must be started on a new thread
		Thread th = startKafkaProducer(kafkaProducer, NR_MESSAGES, TOPIC_NAME);

		th.start();

		processDataAndPersistToCassandra(ssc);

		th.join();

		sc.stop();
	}

	/**
	 * 
	 * @param sc
	 */
	private static void createCassandraKeyspaceAndTables(JavaSparkContext sc) {
		CassandraConnector connector = CassandraConnector.apply(sc.getConf());

		// Prepare the schema
		try (Session session = connector.openSession()) {
			session.execute("DROP KEYSPACE IF EXISTS " + KEYSPACE_NAME);
			session.execute("CREATE KEYSPACE " + KEYSPACE_NAME
					+ " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");

			session.execute("CREATE TABLE " + KEYSPACE_NAME + "." + TABLE_1_NAME + " (id INT PRIMARY KEY, value TEXT)");
			session.execute("CREATE TABLE " + KEYSPACE_NAME + "." + TABLE_2_NAME + " (id INT PRIMARY KEY, value TEXT)");
		}
	};

	/**
	 * 
	 * @param sc
	 * @return
	 */
	private static KafkaProducer<String, String> createKafkaProducer(JavaSparkContext sc) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		KafkaProducer<String, String> producer = new KafkaProducer<>(props);

		return producer;

	}

	/**
	 * Sends a message on a Kafka topic every 250ms until the nrMessages nr had been reached
	 * 
	 * @param kafkaProducer
	 * @param nrMessages
	 * @param topicName
	 */
	// FIXME this must be started on a separated thread
	private static Thread startKafkaProducer(KafkaProducer<String, String> kafkaProducer, int nrMessages,
			String topicName) {

		Thread th = new Thread(() -> {

			for (int i = 0; i < nrMessages; i++) {
				ProducerRecord<String, String> record = new ProducerRecord<>(topicName, "value-" + i);

				kafkaProducer.send(record);

				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			kafkaProducer.close();
		});

		return th;
	}

	/**
	 * 
	 * @param sparkStreamingContext
	 */
	private static void processDataAndPersistToCassandra(JavaStreamingContext ssc) {
		// TODO: processing pipeline

		Map<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", "localhost:9092");
		Set<String> topics = Collections.singleton(TOPIC_NAME);

		JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc, String.class,
				String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);

		directKafkaStream.foreachRDD(rdd -> {
			JavaRDD<MyRowEntry> myRowEntryRDDOdd = rdd	.map(record -> {
				String value = record._2;
				int id = Integer.parseInt(value.substring(value.indexOf("-") + 1));

				return new MyRowEntry(id, value);
			})
														.filter(rE -> rE.getId() % 2 != 0);

			JavaRDD<MyRowEntry> myRowEntryRDDEven = rdd	.map(record -> {
				String value = record._2;
				int id = Integer.parseInt(value.substring(value.indexOf("-") + 1));

				return new MyRowEntry(id, value);
			})
														.filter(rE -> rE.getId() % 2 == 0);

			// 4 values should be written here
			javaFunctions(myRowEntryRDDOdd)	.writerBuilder(KEYSPACE_NAME, TABLE_1_NAME, mapToRow(MyRowEntry.class))
											.saveToCassandra();
			// 4 values should be written here
			javaFunctions(myRowEntryRDDEven).writerBuilder(KEYSPACE_NAME, TABLE_2_NAME, mapToRow(MyRowEntry.class))
											.saveToCassandra();
		});

		ssc.start();

		try {
			ssc.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
