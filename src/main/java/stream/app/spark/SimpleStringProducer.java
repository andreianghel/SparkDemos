package stream.app.spark;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import stream.app.util.MyAppUtils;

public class SimpleStringProducer {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		KafkaProducer<String, String> producer = new KafkaProducer<>(props);
		for (int i = 0; i < 1000; i++) {
			ProducerRecord<String, String> record = new ProducerRecord<>(MyAppUtils.TOPIC_NAME, "value-" + i);
			producer.send(record);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		producer.close();
	}
}