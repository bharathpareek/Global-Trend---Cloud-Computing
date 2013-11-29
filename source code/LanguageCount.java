import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
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
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class LanguageCount {

	/**
	 * This is the Map class of Map Reduce Job.
	 *
	 */
	public static class Map extends
	Mapper<LongWritable, Text, KeyDate, IntWritable> 
	{
		private static KeyDate keyDateObj;

		Hashtable<String, String> languageCodes = new Hashtable<String, String>();

		/**
		 * This function is used to load the distributed cache file.
		 * The file has the mapping for abbreviated language names and the complete language name.  
		 */
		protected void setup(Context context) throws java.io.IOException, InterruptedException 
		{
			Path[] cacheFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());
			BufferedReader fileReader;

			for (int cacheFileIterator = 0; cacheFileIterator < cacheFiles.length; cacheFileIterator++)
			{
				String fileLine = null;
				fileReader = new BufferedReader(new FileReader(cacheFiles[cacheFileIterator].toString()));

				while ((fileLine = fileReader.readLine()) != null)
				{
					StringTokenizer tokenizer = new StringTokenizer(fileLine);
					String languageCode = null, languageName = null;

					if(tokenizer.hasMoreTokens())
						languageCode = tokenizer.nextToken();

					if(tokenizer.hasMoreTokens())
						languageName = tokenizer.nextToken();

					languageCodes.put(languageCode, languageName);
				}
			}
		}

		/**
		 * The Map function for the map reduce job takes the input file
		 * and outputs the (key, value) pair as (KeyDateobj, IntWritable)
		 * The function outputs noofviews as the count for each language found in the input file.
		 */
		public void map(LongWritable key, Text value, Context context)
		throws IOException, InterruptedException 
		{
			try { 
				FileSplit fileSplit = (FileSplit) context.getInputSplit(); 
				String inputFileName = fileSplit.getPath().getName().toString(); 

				String line = value.toString();
				String language = "", date = "";
				StringTokenizer tokenizer = new StringTokenizer(line);

				keyDateObj = new KeyDate();

				if(tokenizer.hasMoreTokens())
					language = tokenizer.nextToken();
				
				tokenizer.nextToken();
				
				IntWritable languagePageCount = new IntWritable (Integer.parseInt(tokenizer.nextToken()));

				language = language.substring(0,2);

				date = getDateFromFileName(inputFileName);

				keyDateObj.recordKey = languageCodes.get(language);
				keyDateObj.recordDate = date;
				context.write(keyDateObj, languagePageCount);

			} catch (Exception e){
			}
		}

		/**
		 * This function returns the date from the input string.
		 * input format : pagecounts-20120901-000000.txt
		 */
		private String getDateFromFileName(String fileName) 
		{
			String yearMonthDate = fileName.substring(fileName.indexOf('-') + 1, fileName.lastIndexOf('-'));
			String hour = fileName.substring(fileName.lastIndexOf('-') +1,fileName.lastIndexOf('-') + 3 ); 
			return yearMonthDate + hour;
		}
	}

	/**
	 * The reduce function of map reduce jobs sums up the language counts for a particular language on a particular date 
	 */
	public static class Reduce extends
	Reducer<KeyDate, IntWritable, KeyDate, IntWritable> 
	{
		public void reduce(KeyDate key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			
			for (IntWritable val : values) 
			{
				sum += val.get();
			}

			context.write(key, new IntWritable(sum));
		}
	}

	/***
	 * The main function sets up the Job attributes and other variables required
	 * for map-reduce tasks
	 * The LanguageCount job gives the count of searches made on Wikipedia per languages per date.   
	 */
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		Job job = new Job(conf, "LanguageCount");

		DistributedCache.addCacheFile(new URI(args[2]), job.getConfiguration());

		job.setJarByClass(LanguageCount.class);
		job.setOutputKeyClass(KeyDate.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setNumReduceTasks(1);
		job.waitForCompletion(true);
	}
}