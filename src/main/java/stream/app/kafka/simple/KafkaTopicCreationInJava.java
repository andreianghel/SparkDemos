package stream.app.kafka.simple;

import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import kafka.admin.AdminUtils;
//import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer;
import kafka.utils.ZkUtils;

public class KafkaTopicCreationInJava {

	public static void createTopic(String topicName, int numPartitions, int numReplication) {
		/*
		 * ZkClient zkClient = null; ZkUtils zkUtils = null;
		 * 
		 * try { String zookeeperHosts = "0.0.0.0:2181"; // If multiple zookeeper then
		 * -> String zookeeperHosts = // "192.168.20.1:2181,192.168.20.2:2181"; int
		 * sessionTimeOutInMs = 15 * 1000; // 15 secs int connectionTimeOutInMs = 10 *
		 * 1000; // 10 secs
		 * 
		 * zkClient = new ZkClient(zookeeperHosts, sessionTimeOutInMs,
		 * connectionTimeOutInMs); // Ref: https://gist.github.com/jjkoshy/3842975
		 * zkClient.setZkSerializer(new ZkSerializer() {
		 * 
		 * @Override public byte[] serialize(Object o) throws ZkMarshallingError {
		 * return ZKStringSerializer.serialize(o); }
		 * 
		 * @Override public Object deserialize(byte[] bytes) throws ZkMarshallingError {
		 * return ZKStringSerializer.deserialize(bytes); } });
		 * 
		 * zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), false);
		 * 
		 * Properties topicConfiguration = new Properties();
		 * 
		 * AdminUtils.createTopic(zkUtils, topicName, numPartitions, numReplication,
		 * topicConfiguration, RackAwareMode.Enforced$.MODULE$);
		 * 
		 * } catch (Exception ex) { ex.printStackTrace(); } finally { if (zkClient !=
		 * null) { zkClient.close(); } }
		 */
	}

	public static void main(String... args) {
		int numPartitions = 1;
		int numReplication = 1;
		String topicName = "testTopic";

		createTopic(topicName, numPartitions, numReplication);

		System.out.println("done creating topic");
	}

}