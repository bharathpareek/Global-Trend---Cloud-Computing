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

/** this class insert trend titles from files in A folder into database in table - pagetitle
 * it inserts one row per insert
 * */
public class InsertTrendTitles {

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
	
	/** this method inserts pagetitles into database
	 * */
	public void migrateData(File folder)
	{
		Connection connection = null;
		PreparedStatement insertPageTitlesPS = null;
		String insertPageTitles = " insert into pagetitles(title) values(?)";
		BufferedReader reader = null;
		String line = "";
		StringTokenizer tokenizer = null;
		
		File[] listOfFiles = folder.listFiles();
		
		try
		{
			connection=getConnection();
			insertPageTitlesPS = connection.prepareStatement(insertPageTitles);
			for (File file : listOfFiles)
			{
				System.out.println(file.getName());
				reader = new BufferedReader(new FileReader(file));
				while((line=reader.readLine())!=null)
				{
					
					tokenizer = new StringTokenizer(line,"\t");
					if(tokenizer.hasMoreTokens())
						insertPageTitlesPS.setString(1,tokenizer.nextToken());
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

}
