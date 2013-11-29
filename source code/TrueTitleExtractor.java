import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Document;


public class TrueTitleExtractor {

/*
The Mapper finds the true title corresponding to each search topic. The output of the mapper are 
key: true title and search date seperated by tab and value: number of searches.
*/
	public static class TrueTitleMap extends Mapper<LongWritable, Text, Text, IntWritable> 
	{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
		{
			String titleDateAndViewsAry[];
			String titleDateAndViews=value.toString();
			
			titleDateAndViewsAry=titleDateAndViews.split("\t");
			String trueTitle = findTrueTitle(titleDateAndViewsAry[0]);
			if(trueTitle!=null && !"".equals(trueTitle))
			{
				context.write(new Text(trueTitle+"\t"+titleDateAndViewsAry[1]),new IntWritable(Integer.parseInt(titleDateAndViewsAry[2])));
			}
			
		}

/*
This function uses wikipedia api to find true title of wikipedia corresponding to each search term. 
Wikipedia api returns an xml file which is parsed using DOM parser. This function returns wikipedia 
truetitle corresponding to each search term.
*/

		public String findTrueTitle(String page){
			if(page==null || "".equals(page) )
				return null;
			String searchTitle = new String();
			searchTitle=page;
			String trueTitle=null;
			String searchUrlTerm=new String();
			StringTokenizer tokenizer = new StringTokenizer(searchTitle);
			searchUrlTerm=tokenizer.nextToken();
			while(tokenizer.hasMoreTokens()){
				searchUrlTerm=searchUrlTerm+'+'+tokenizer.nextToken();
			}
			String urlName = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch="+searchUrlTerm+"&srprop=&format=xml&srlimit=1&srinfo=&sroffset=0";
			
			try {
				URL url=new URL(urlName);
				URLConnection conn = url.openConnection();
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(conn.getInputStream());
				trueTitle=doc.getElementsByTagName("p").item(0).getAttributes().getNamedItem("title").getNodeValue();
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
			return trueTitle;
		}
		
	}

/*
The reducer groups together the newly found true title by adding up their number of searches.
*/
	public static class TrueTitleReducer extends Reducer<Text, IntWritable, Text, IntWritable> 
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
		TrueTitleExtractor extractor=new TrueTitleExtractor();
		//extractor.parseInput("data/inputTrueTitle","outputTruetitle");
		extractor.parseInput(args[0],args[1]);

	}
	
	public void parseInput(String inputPath, String outputPath) throws IOException, ClassNotFoundException, InterruptedException 
	{
		Configuration conf = new Configuration();

		Job jobInputParsing = new Job(conf,"TrueTitle");
		jobInputParsing.setJarByClass(TrueTitleExtractor.class);
		jobInputParsing.setOutputKeyClass(Text.class);
		jobInputParsing.setOutputValueClass(IntWritable.class);

		jobInputParsing.setMapperClass(TrueTitleMap.class);
		jobInputParsing.setReducerClass(TrueTitleReducer.class);

		jobInputParsing.setInputFormatClass(TextInputFormat.class);
		jobInputParsing.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(jobInputParsing, new Path(inputPath));
		FileOutputFormat.setOutputPath(jobInputParsing, new Path(outputPath));
		jobInputParsing.setNumReduceTasks(20);
		jobInputParsing.waitForCompletion(true);
	}


}
