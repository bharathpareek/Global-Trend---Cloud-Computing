import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
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

public class TopTrendingTopics {

    /*
	This mapper calculates the weight corresponding to each search term. 
	Output are key:true title value: weight for each search term
     */
    public static class trendTitleMap extends Mapper<LongWritable, Text, Text, DoubleWritable> 
    {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
        {
            try
            {
            	String dateAndViews[];
                long dayDiff;
                double weight;
                dateAndViews=value.toString().split("\t");
                dayDiff=dateDifference(Integer.parseInt(dateAndViews[1]));
                weight=weightCalculate(dayDiff,Integer.parseInt(dateAndViews[2]));
                context.write(new Text(dateAndViews[0]),new DoubleWritable(weight));
            }
            catch(Exception e)
            {
            	
            }
        }

/*
	This function finds the age of each search term by converting the date from integer 
	format to java date format and finding the difference with the latest date	
*/
        public long dateDifference(int inputDate){
            int currentDate=20121115;
            int currentDateSplit[]=new int[3];
            int inputDateSplit[]=new int[3];
            currentDateSplit=dateSplit(currentDate);
            inputDateSplit=dateSplit(inputDate);
            @SuppressWarnings("deprecation")
            Date dateCurrent=new Date(currentDateSplit[2],currentDateSplit[1],currentDateSplit[0]);
            @SuppressWarnings("deprecation")
            Date dateInput=new Date(inputDateSplit[2],inputDateSplit[1],inputDateSplit[0]);
            //System.out.println("Difference"+dateCurrent);
            long difference=(dateCurrent.getTime()-dateInput.getTime())/(24*3600*1000);
            //System.out.println(difference);        
            return difference;
        }
        
/*
this function split the date in integer to an array with day year and month as elements
*/
        public int[] dateSplit(int date){
            int ddmmyy[]=new int[3];
            ddmmyy[0]=date%100;
            date=date/100;
            ddmmyy[1]=date%100;
            date=date/100;
            ddmmyy[2]=date;
            //System.out.println("Date"+ddmmyy[0]+" "+ddmmyy[1]+" "+ddmmyy[2]);
            return ddmmyy;
            
        } 

/*
This function calculates the weight corresponding each search term and returns the weight in double format
*/
        public double weightCalculate(long dayDiff,int views){
            double weight=0;
            weight=Math.exp(-1*dayDiff/5)*views;
            return weight;
            
        }
        
        
    }

    /*
	The reducer sums up the weight corresponding to each search term to find the total weight. 
	Output of the reducer is to find true 	title and is total weight
     */
    public static class trendTitleReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> 
    {
         public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException 
        {
            double totalWeight = 0;
            for (DoubleWritable val : values) {
                totalWeight += val.get();
            }
            //System.out.println(key+": "+sum);
            context.write(key, new DoubleWritable(totalWeight));
        }
    }
    
/*
This job is used to sort the output file of the previous job based on the total weight. 
The result of this job is top 20 trending topics
*/

    public static class TopTrendMapper extends Mapper<LongWritable, Text, NullWritable, Text> 
	{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
		{
			String []keyValue = value.toString().split("\t");
			context.write(NullWritable.get(),new Text(keyValue[1].toString()+":"+keyValue[0].toString()));
		}
	}
    
    public static class TopTrendReducer extends Reducer<NullWritable, Text, Text, Text>  
	{
		
		public void reduce(NullWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
		{
			TreeMap<Float,String> topTrendNodes = new TreeMap<Float,String>(new FloatComparator());
			int count = 0;
			String weightPageTitle = "";
			String[] weightPageTitleAry = null;
			Float weight  = null;
			for(Text value:values)
			{
				weightPageTitle = value.toString();
				weightPageTitleAry = weightPageTitle.split(":");
				weight = Float.parseFloat(weightPageTitleAry[0]);
				topTrendNodes.put(weight, weightPageTitle);
			}
			for(String value:topTrendNodes.values())
			{
				if(count==25)
					return;
				weightPageTitle = value.toString();
				weightPageTitleAry = weightPageTitle.split(":");
				context.write(new Text(weightPageTitleAry[1]), new Text(weightPageTitleAry[0]));
				count++;
			}
		}
	}
    
    public static void main(String[] args) throws Exception
    {
    	if(args.length<2)
    	{
    		System.out.println("Please specify input and output directory.");
    		System.exit(0);
    	}
        TopTrendingTopics title=new TopTrendingTopics();
        //title.parseInput("trendInput","trendOutput");
        //title.topTrending("trendOutput", "rankOutput");
        title.parseInput(args[0],"data/trendOutput");
        title.topTrending("data/trendOutput", args[1]);


    }
    
    public void parseInput(String inputPath, String outputPath) throws IOException, ClassNotFoundException, InterruptedException 
    {
        Configuration conf = new Configuration();

        Job jobInputParsing = new Job(conf,"trending");
        jobInputParsing.setJarByClass(TopTrendingTopics.class);
        jobInputParsing.setOutputKeyClass(Text.class);
        jobInputParsing.setOutputValueClass(DoubleWritable.class);

        jobInputParsing.setMapperClass(trendTitleMap.class);
        jobInputParsing.setReducerClass(trendTitleReducer.class);

        jobInputParsing.setInputFormatClass(TextInputFormat.class);
        jobInputParsing.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(jobInputParsing, new Path(inputPath));
        FileOutputFormat.setOutputPath(jobInputParsing, new Path(outputPath));
        jobInputParsing.setNumReduceTasks(2);
        jobInputParsing.waitForCompletion(true);
    }
    
    private void topTrending(String inputPath, String outputPath)
	throws IOException, ClassNotFoundException, InterruptedException 
	{
    	
		Configuration conf = new Configuration();
		Job jobRankOrdering = new Job(conf, "RankOrdering");
		
		jobRankOrdering.setJarByClass(TopTrendingTopics.class);
		
		jobRankOrdering.setOutputKeyClass(NullWritable.class);
		jobRankOrdering.setOutputValueClass(Text.class);

		jobRankOrdering.setMapperClass(TopTrendMapper.class);
		jobRankOrdering.setReducerClass(TopTrendReducer.class);
		
		jobRankOrdering.setInputFormatClass(TextInputFormat.class);
		jobRankOrdering.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(jobRankOrdering, new Path(inputPath));
		FileOutputFormat.setOutputPath(jobRankOrdering, new Path(outputPath));
		
		jobRankOrdering.setNumReduceTasks(2);
		jobRankOrdering.waitForCompletion(true);
	}
    
    public static class FloatComparator implements Comparator<Float>
	{

		public FloatComparator() 
		{
			super();
		}

		public int compare(Float rank1,Float rank2)
		{
			if(rank1==rank2)
				return 1;
			else if(rank1<rank2)
				return 1;
			else
				return -1;
		}
	}

}
