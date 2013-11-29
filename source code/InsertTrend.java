import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringEscapeUtils;

/** This file inserts trend statistics in pageviews table
 * there are two methods for insertion
 * 1. insert using prepared statement => one insert per row
 * 2. insert by clubbing multiple rows => single insert per 200 rows
 * */
public class InsertTrend {
	
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
		if("insertall".equalsIgnoreCase(type))
			multipleInsert(folder);
		else if("insertseq".equalsIgnoreCase(type))
			prepareInsert(folder);
			
	}
	
	/**this method gets date in the oracle required format
	 * */
	public java.sql.Date getDate(String strDate)
	{
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
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
		return sqlDate;
	}
	
	/**this method insert the data using prepared statement -> i.e. one insert per row
	 * */
	public void prepareInsert(File folder)
	{
		Connection connection = null;
		PreparedStatement insertPageTitlesPS = null;
		String insertViewTrend = " insert into pageviews(page_id, viewdate, no_of_views) values((select id from pagetitles where title=?), ?, ? )";
		BufferedReader reader = null;
		String line = "";
		StringTokenizer tokenizer = null;
		
		File[] listOfFiles = folder.listFiles();
		
		try
		{
			connection=getConnection();
			insertPageTitlesPS = connection.prepareStatement(insertViewTrend.toString());
			for (File file : listOfFiles)
			{
				System.out.println(file.getName());
				reader = new BufferedReader(new FileReader(file));
				while((line=reader.readLine())!=null)
				{
					
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
					}
					catch(SQLException se)
					{
						continue;
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
	
	/** this method inserts multiple rows(200) per insert
	 * */
	public void multipleInsert(File folder)
	{
		Connection connection = null;
		PreparedStatement insertPageTitlesPS = null;
		ResultSet insertPageTitlesRS = null;
		StringBuffer insertPageTitles = null;
		BufferedReader reader = null;
		String line = "";
		StringTokenizer tokenizer = null;
		
		File[] listOfFiles = folder.listFiles();
		
		try
		{
			int strCount = 0;
			connection=getConnection();
			
			for (File file : listOfFiles)
			{
				insertPageTitles = new StringBuffer(" insert all ");
				System.out.println(file.getName());
				reader = new BufferedReader(new FileReader(file));
				while((line=reader.readLine())!=null)
				{
					
					tokenizer = new StringTokenizer(line,"\t");
					while(tokenizer.hasMoreTokens())
					{
						insertPageTitles.append(" into pagetitle(title, viewdate, pageviews) ");
						insertPageTitles.append(" values('"+StringEscapeUtils.escapeSql(tokenizer.nextToken())+"',to_date('"+tokenizer.nextToken()+"','YYYYMMDD'), "+tokenizer.nextToken()+") ");
						//.replace("'", "''")
					}
					strCount++;
					if(strCount==200)
					{
						insertPageTitles.append(" select * from dual");
						System.out.println("Insert Query formed :");
						System.out.println(insertPageTitles.toString());
						insertPageTitlesPS = connection.prepareStatement(insertPageTitles.toString());
						insertPageTitlesPS.executeUpdate();
						strCount=0;
						insertPageTitles = new StringBuffer(" insert all ");
					}
				}
				insertPageTitles.append(" select * from dual");
				System.out.println("Insert Query formed :");
				System.out.println(insertPageTitles.toString());
				insertPageTitlesPS = connection.prepareStatement(insertPageTitles.toString());
				insertPageTitlesPS.executeUpdate();
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
				if(insertPageTitlesRS!=null)
					insertPageTitlesRS.close();
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
