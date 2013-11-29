import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

/** This file inserts trend statistics in languagetitles table
 * there are two methods for insertion
 * 1. insert using prepared statement => one insert per row
 * 2. insert by clubbing multiple rows => single insert per 200 rows
 * */
public class InsertLanguage {
	
	/** This method gets connection to database  
	 * */
	public Connection getConnection()
	{
		Connection connection = null;
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			System.out.println("oracle jdbc driver registered");
			connection= DriverManager.getConnection("jdbc:oracle:thin:@oracle1.cise.ufl.edu:1521:orcl","kkewlani","cloudtrend");
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		return connection;		
	}
	
	/**This method identifies the method to be used for insertion
	 * */
	public void migrateData(File folder, String type)
	{
		if("insertseq".equalsIgnoreCase(type))
			prepareInsert(folder);
			
	}
	
	/**this method gets date in the oracle required format
	 * */
	public java.sql.Date getDate(String strDate)
	{
		DateFormat df = new SimpleDateFormat("yyyyMMddHH");
		java.util.Date date = null;
		try
		{
			date = df.parse(strDate);
		}
		catch(java.text.ParseException pe)
		{
			pe.printStackTrace();
		}
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		System.out.println("date input :"+strDate+" util Date:"+date.toString()+" SQl Date:"+sqlDate.toString());
		return sqlDate;
	}
	
	/**this method insert the data using prepared statement -> i.e. one insert per row
	 * */
	public void prepareInsert(File folder)
	{
		Connection connection = null;
		PreparedStatement insertPageTitlesPS = null;
		int i = 1;
		
		BufferedReader reader = null;
		String line = "";
		StringTokenizer tokenizer = null;
		
		File[] listOfFiles = folder.listFiles();
		
		try
		{
			connection=getConnection();
			
			for (File file : listOfFiles)
			{
				System.out.println(file.getName());
				reader = new BufferedReader(new FileReader(file));
				while((line=reader.readLine())!=null)
				{
					String insertViewTrend = " insert into LANGUAGEDATA(ID, LANGUAGEID, VIEWDATE, NOOFVIEWS) values("+i+", (select LANGUAGEID from LANGUAGETITLES where LANGUAGETITLE=?), ?, ? )";
					insertPageTitlesPS = connection.prepareStatement(insertViewTrend.toString());
					tokenizer = new StringTokenizer(line,"\t");
					while(tokenizer.hasMoreTokens())
					{
						insertPageTitlesPS.setString(1,tokenizer.nextToken());
						insertPageTitlesPS.setDate(2,getDate(tokenizer.nextToken()));
						insertPageTitlesPS.setInt(3,Integer.parseInt(tokenizer.nextToken()));
					}
					try
					{
						insertPageTitlesPS.executeUpdate();
						insertPageTitlesPS.close();
						i++;
					}
					catch(SQLException se)
					{
						throw se;
					}
				}
				System.out.println("Inserted Successfully");
			}
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch(FileNotFoundException fe)
		{
			fe.printStackTrace();
		}
		catch(IOException ie)
		{
			ie.printStackTrace();
		}
		finally
		{
			try
			{
				if(insertPageTitlesPS!=null)
					insertPageTitlesPS.close();
				if(reader!=null)
					reader.close();
				if(connection!=null)
					connection.close();
				
			}
			catch(SQLException se)
			{
				se.printStackTrace();
			}
			catch(IOException ie)
			{
				ie.printStackTrace();
			}
			
		}
	}	
}
