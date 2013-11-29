import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
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
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.w3c.dom.Document;



public class TitleCleaner {

	/**
	 * TitleCleaner cleans the raw input and identifies valid data with the help of regular expressions.  
	 */
	public static class TitleCleanerMap extends Mapper<LongWritable, Text, Text, IntWritable> 
	{
		/*
		 * TitleCleanerMap filters the input on the basis of regular expressions. The Map task generates
		 * the decoded title for the search term and the number of views.
		 * */
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
		{
			String page= new String();
			String pageTitleAndViews[];
			String date=new String();
			String titleAndDate=new String();
			page=filterWikiEnglishPage(value.toString());
			
			
			if(page != null)
			{

				pageTitleAndViews=page.split("\t");
				FileSplit fileSplit = (FileSplit)context.getInputSplit();
				String fileName = new String();
				fileName=fileSplit.getPath().getName().toString();
				date = getDateFromFileName(fileName);
				titleAndDate=pageTitleAndViews[0]+"\t"+date;
			if (pageTitleAndViews[1]!=null && pageTitleAndViews[1]!="" && pageTitleAndViews[1]!=" "){
				try
				{
					IntWritable views=new IntWritable(Integer.parseInt(pageTitleAndViews[1]));
					context.write(new Text(titleAndDate),views);
				}
				catch(Exception e)
				{
					System.out.println("Error at :"+page);
				}
				
				}
				
			}
		}
		/* filterWikiEnglishPage reads the line from the input and initially filters the search performed 
		 * in english language and follow the specified format.  
		 * */
		public String filterWikiEnglishPage(String fileline){

			String wiki_en_regex=new String();
			String page=new String();
			long viewNumber=0;
			int count=0;

			wiki_en_regex="en (.*) ([0-9]+) ([0-9]+)";
			if(fileline.matches(wiki_en_regex)){
				String line = fileline;
				String word=new String();
				StringTokenizer tokenizer = new StringTokenizer(line);
				while(tokenizer.hasMoreTokens()){
					count++;
					word=tokenizer.nextToken();
					switch(count){
					case 2: page=word.toString();
					break;
					case 3:viewNumber=Integer.parseInt(word.toString());
					break;
					}

				}
				
				//function to validate page name.
				if(isPageValid(page)&& viewNumber>=0){
					String pageDecoded= new String();
					String pageTrueTitle= new String();
					String titleCountID= new String();
					String pageID= new String();
					pageDecoded=pageDecode(page);
					if(pageDecoded!=null && !"".equals(pageDecoded))
					{						
						titleCountID=pageDecoded+"\t"+viewNumber;
						return titleCountID;
						
					}
				}
			}
			return null;
		}
		public String findTrueTitle(String page){
			String searchTitle = new String();
			searchTitle=page;
			String trueTitle=new String();
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
				// TODO Auto-generated catch block
				
			}
			
			return trueTitle;
		}
		/*pageDecode decodes the URL-encoded search term. 
		 * */
		public String pageDecode(String pageName){
			String specialCharRegex=new String();
			String decodePageTitle=new String();
			specialCharRegex="([A-Z]|[a-z]|[0-9])+.*";


			try {
				decodePageTitle=URLDecoder.decode(pageName,"UTF-8");
				if(decodePageTitle.matches(specialCharRegex)){
					return decodePageTitle;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
		
			}
			return null;
		}
		
		/*isPageValid removes the search term which include redirects, images and some special categories.
		 * */
		public boolean isPageValid(String page){
			String namespace0=new String();
			String imageRegex=new String();
			String rejectionList=new String();

			namespace0="(Media|Special|Talk|User|User_talk|Project|Project_talk|File|File_talk|MediaWiki|MediaWiki_talk|Template|Template_talk|Help|Help_talk|Category|Category_talk|Portal|Wikipedia|Wikipedia_talk)"+"\\:"+"(.*)";
			imageRegex="(.*).(jpg|gif|png|JPG|GIF|PNG|txt|ico)";
			rejectionList="404_error/|,Main_Page|Hypertext_Transfer_Protocol|Favicon.ico|Search";

			if(page.matches(namespace0)||page.matches(imageRegex)||page.matches(rejectionList)){
				return false;
			}
			return true;
		}
		
		/*getDateFromFileName extracts the date and time when the search was performed from the filename.
		 *For example, filename pagecounts-20120901-000000.txt denotes that the date is 1 September 2012 
		 *and time is 12 A.M.
		 * */
		private String getDateFromFileName(String fileName) {
			String yearMonthDate = fileName.substring(fileName.indexOf('-') + 1, fileName.lastIndexOf('-'));
			String hour = fileName.substring(fileName.lastIndexOf('-') +1,fileName.lastIndexOf('-') + 3 ); 
			return yearMonthDate;
		}
	}

	/***
	 * The Reducer of this job sums up the number of views for each decoded search term.
	 */
	public static class TitleCleanerReducer extends Reducer<Text, IntWritable, Text, IntWritable> 
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
		// TODO Auto-generated method stub
		TitleCleaner regex=new TitleCleaner();
		regex.parseInput("data/inputPageTitle","outputPageTitle");

	}
	
	public void parseInput(String inputPath, String outputPath) throws IOException, ClassNotFoundException, InterruptedException 
	{
		Configuration conf = new Configuration();

		Job jobInputParsing = new Job(conf,"TitleCleaner");
		jobInputParsing.setJarByClass(TitleCleaner.class);
		jobInputParsing.setOutputKeyClass(Text.class);
		jobInputParsing.setOutputValueClass(IntWritable.class);

		jobInputParsing.setMapperClass(TitleCleanerMap.class);
		jobInputParsing.setReducerClass(TitleCleanerReducer.class);

		jobInputParsing.setInputFormatClass(TextInputFormat.class);
		jobInputParsing.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(jobInputParsing, new Path(inputPath));
		FileOutputFormat.setOutputPath(jobInputParsing, new Path(outputPath));
		jobInputParsing.setNumReduceTasks(20);
		jobInputParsing.waitForCompletion(true);
	}


}