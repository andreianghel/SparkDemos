package stream.app.kafka.simple;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class SimpleStringConsumer {

	public static final String topicName = "testTopic";

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "mygroup");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topicName);

		boolean running = true;
		while (running) {
			Map<String, ConsumerRecords<String, String>> records = consumer.poll(100);
			for (Map.Entry<String, ConsumerRecords<String, String>> record : records.entrySet()) {

				System.out.println(record.getValue());
			}
		}

		consumer.close();
	}
}