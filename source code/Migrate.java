import java.io.File;


/** migrate class migrates data from map reduce task and puts the values 
 * in the oracle database created. Table name -> PAGETITLE and PAGEVIEWS
 * 
 * There are two options:
 * insertTitles: to insert the titles 
 * insertTrend: to insert page view statistics
 */
public class Migrate {
	
	public static void main(String args[])
	{
		if(args.length<2)
		{
			System.out.println("Please specify folder name. Follow Format : Migrate <taskName:(insertTitles/insertTrend)> <folderName>");
			System.exit(0);
		}
		File folder = new File(args[1]);
		if(!folder.exists())
		{
			System.out.println("The folder "+args[1]+" does not exist.");
			System.exit(0);
		}
		
		if("insertTitles".equalsIgnoreCase(args[0]))
		{
			InsertTrendTitles titles = new InsertTrendTitles();
			titles.migrateData(folder);
		}
		else if("insertTrend".equalsIgnoreCase(args[0]))
		{
			InsertTrend trend = new InsertTrend();
			trend.migrateData(folder, "insertseq");
		}
		else
		{
			System.out.println("Wrong Task Name. Please Follow Format : Migrate <taskName:(insertTitles/insertTrend)> <folderName>");
			System.exit(0);
		}
	}

}
