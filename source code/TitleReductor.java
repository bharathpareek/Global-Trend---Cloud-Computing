import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/** This class file reduces the output files for same task from
 * various sources  
 * */

public class TitleReductor {

	/** TitleReducerMap takes the input from the file as :
	 * key = title+date and value = pageViews
	 * */
	public static class TitleReducerMap extends Mapper<LongWritable, Text, Text, IntWritable> 
	{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
		{
			String titleDateAndViewsAry[];
			String titleDateAndViews=value.toString();
			titleDateAndViewsAry=titleDateAndViews.split("\t");
			context.write(new Text(titleDateAndViewsAry[0]+"\t"+titleDateAndViewsAry[1]),new IntWritable(Integer.parseInt(titleDateAndViewsAry[2])));
		}
	}

	/** TitleReducerReducer adds up the no of pageViews for a particular topic at a particular date
	 */
	public static class TitleReducerReducer extends Reducer<Text, IntWritable, Text, IntWritable> 
	{
		 public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException 
		{
			int totalViews = 0;
	        for (IntWritable val : values) {
	            totalViews += val.get();
	        }
	        context.write(key, new IntWritable(totalViews));
		}
	}
	public static void main(String[] args) throws Exception{
		if(args.length<2)
		{
			System.out.println("Please specify input and output directory.");
			System.exit(0);
		}
		TitleReductor titleReducer=new TitleReductor();
		titleReducer.parseInput(args[0],args[1]);

	}
	
	/** this method runs the map reduce task specifying various properties like
	 * output key and value class, no of reducers, mapper and reducer class
	 * */
	public void parseInput(String inputPath, String outputPath) throws IOException, ClassNotFoundException, InterruptedException 
	{
		Configuration conf = new Configuration();

		Job jobInputParsing = new Job(conf,"TitleReductor");
		jobInputParsing.setJarByClass(TitleReductor.class);
		jobInputParsing.setOutputKeyClass(Text.class);
		jobInputParsing.setOutputValueClass(IntWritable.class);

		jobInputParsing.setMapperClass(TitleReducerMap.class);
		jobInputParsing.setReducerClass(TitleReducerReducer.class);

		jobInputParsing.setInputFormatClass(TextInputFormat.class);
		jobInputParsing.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(jobInputParsing, new Path(inputPath));
		FileOutputFormat.setOutputPath(jobInputParsing, new Path(outputPath));
		jobInputParsing.setNumReduceTasks(5);
		jobInputParsing.waitForCompletion(true);
	}


}