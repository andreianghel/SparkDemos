package cassandra;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.SparkContextJavaFunctions;
import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;

//https://dzone.com/articles/sparkcassandra-stack-perform
public class SimpleSparkToCassandra {

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setAppName("Cassandra application");
		SparkContext sc = new SparkContext(conf);

	}

	public static void initSpark() {
		SparkConf conf = new SparkConf(true).setMaster("local")
											.setAppName("DatastaxtTests")
											.set("spark.executor.memory", "1g")
											.set("spark.cassandra.connection.host", "localhost")
											.set("spark.cassandra.connection.native.port", "9142")
											.set("spark.cassandra.connection.rpc.port", "9171");

		SparkContext ctx = new SparkContext(conf);

		SparkContextJavaFunctions functions = CassandraJavaUtil.javaFunctions(ctx);

		// keyspace & table name?
		CassandraJavaRDD<CassandraRow> rdd = functions.cassandraTable("roadtrips", "roadtrip");

		rdd.cache();

		// JavaPairRDD<String, Integer> sizes = rdd.groupBy( new Function<CassandraRow,
		// String>() {
		// @Override
		// public String call(CassandraRow row) throws Exception {
		// return row.getString("origin_city_name");
		// }
		// }).
		// mapToPair(new PairFunction<Tuple2<String,Iterable<CassandraRow>>, String,
		// Integer>() {
		// @Override
		// public Tuple2<String, Integer> call(Tuple2<String, Iterable<CassandraRow>> t)
		// throws Exception {
		// return new Tuple2<String,Integer>(t._1(), Lists.newArrayList(t._2()).size());
		// }
		// });
		// sizes.cache();
		//
		// List<Tuple2<String, Integer>> sizesResults = sizes.collect();
		// LOGGER.info("Nb RoadTrips by origin");
		// for(Tuple2<String, Integer> tuple : sizesResults){
		// LOGGER.info(tuple._1() + " : " + tuple._2());
		// }
		//
		// JavaPairRDD<String, Integer> sums = rdd.mapToPair(new
		// PairFunction<CassandraRow, String, Integer>() {
		// @Override
		// public Tuple2<String, Integer> call(CassandraRow row)
		// throws Exception {
		// return new Tuple2(row.getString("origin_city_name"), row.getInt("distance"));
		// }
		// }).reduceByKey(new Function2<Integer, Integer, Integer>() {
		// @Override
		// public Integer call(Integer d1, Integer d2) throws Exception {
		// return Integer.valueOf(d1.intValue()+d2.intValue());
		// }
		// });
		// List<Tuple2<String,Double>> averageResults = sums.join(sizes).mapValues(new
		// Function<Tuple2<Integer,Integer>, Double>() {
		// @Override
		// public Double call(Tuple2<Integer, Integer> tuple) throws Exception {
		// return Double.valueOf((double)tuple._1() / tuple._2());
		// }
		// }).collect();
		//
		// //LOGGER.info("Average distance by origin");
		// for(Tuple2<String, Double> tuple : averageResults){
		// LOGGER.info(tuple._1() + " : " + tuple._2());
		// }

	}

}
