import java.io.File;


/** MigrateLanguage class migrates data from map reduce task and puts the values 
* in the oracle database created. Table name -> LANGUAGE TITLES and LANGUAGEDATA
* 
* There are two options:
* insertTitles: to insert the titles 
* insertTrend: to insert language view statistics
*/
public class MigrateLanguage {
       
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
                       InsertLanguageTitles titles = new InsertLanguageTitles();
                       titles.migrateData(folder);
               }
               else if("insertTrend".equalsIgnoreCase(args[0]))
               {
                       InsertLanguage trend = new InsertLanguage();
                       trend.migrateData(folder, "insertseq");
               }
               else
               {
                       System.out.println("Wrong Task Name. Please Follow Format : Migrate <taskName:(insertTitles/insertTrend)> <folderName>");
                       System.exit(0);
               }
       }

}