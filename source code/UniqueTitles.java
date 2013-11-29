import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/** This class takes input as all the output files of various days to be used for finding 
 * top trending topics and get unique titles from this files.
 * */
public class UniqueTitles
{

	/** UniqueTitlesMap takes the input from the file as :
	 * key = title and value = date+pageViews and 
	 * gives output as key = title value = null
	 * */
	public static class UniqueTitlesMap extends Mapper<LongWritable, Text, Text, NullWritable> 
	{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
		{
			String titleDateAndViewsAry[];
			String titleDateAndViews=value.toString();
			titleDateAndViewsAry=titleDateAndViews.split("\t");
			context.write(new Text(titleDateAndViewsAry[0]),NullWritable.get());
		}
	}

	/** UniqueTitlesReducer reduces the titles and give output as
	 * key = title(Unique) value = null
	 */
	public static class UniqueTitlesReducer extends Reducer<Text, NullWritable, Text, NullWritable> 
	{
		 public void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException 
		{
			context.write(key, NullWritable.get());
		}
	}
	public static void main(String[] args) throws Exception{
		if(args.length<2)
		{
			System.out.println("Please specify input and output directory.");
			System.exit(0);
		}
		UniqueTitles titles=new UniqueTitles();
		titles.parseInput(args[0],args[1]);

	}
	
	/** this method runs the map reduce task specifying various properties like
	 * output key and value class, no of reducers, mapper and reducer class
	 * */
	public void parseInput(String inputPath, String outputPath) throws IOException, ClassNotFoundException, InterruptedException 
	{
		Configuration conf = new Configuration();

		Job jobInputParsing = new Job(conf,"UniqueTitles");
		jobInputParsing.setJarByClass(UniqueTitles.class);
		jobInputParsing.setOutputKeyClass(Text.class);
		jobInputParsing.setOutputValueClass(NullWritable.class);

		jobInputParsing.setMapperClass(UniqueTitlesMap.class);
		jobInputParsing.setReducerClass(UniqueTitlesReducer.class);

		jobInputParsing.setInputFormatClass(TextInputFormat.class);
		jobInputParsing.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(jobInputParsing, new Path(inputPath));
		FileOutputFormat.setOutputPath(jobInputParsing, new Path(outputPath));
		jobInputParsing.setNumReduceTasks(5);
		jobInputParsing.waitForCompletion(true);
	}


}