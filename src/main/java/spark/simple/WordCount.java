package spark.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * Sample Spark application that counts the words in a text file
 */
public class WordCount {

	public static void wordCountJava8(String filename) {
		// Define a configuration to use to interact with Spark
		SparkConf conf = new SparkConf().setMaster("local")
										.setAppName("Work Count App");

		// Create a Java version of the Spark Context from the configuration
		JavaSparkContext sc = new JavaSparkContext(conf);

		// Load the input data, which is a text file read from the command line
		JavaRDD<String> input = sc.textFile(filename);

		// Java 8 with lambdas: split the input string into words
		JavaRDD<String> words = input.flatMap(s -> Arrays.asList(s.split(" ")).iterator());

		// Java 8 with lambdas: transform the collection of words into pairs (word and
		// 1) and then count them
		JavaPairRDD<String, Integer> counts = words	.mapToPair(t -> new Tuple2(t, 1))
													.reduceByKey((x, y) -> (int) x + (int) y);

		// Save the word count back out to a text file, causing evaluation.
		counts.saveAsTextFile("output1");
	}

	public static void main(String[] args) {
		// FIXME get this from file on hdfs
		String input = "src/main/resources/input";

		wordCountJava8(input);

		List<String> l = new ArrayList<>();

	}
}