Global-Trend---Cloud-Computing
==============================

Determining the Global Trend according to the searches made in the Wikipedia

Global Trend is a project which takes Wikipedia datasets for page view counts and gives trend for various topics 
searched on Wikipedia over a period of time.

Wikipedia is a plethora of web pages used to read articles and extract information of entities. This wide usage of Wikipedia can help in determining the current trend in the world by analyzing the Wikipedia datasets. In this project, cloud computing services like Amazon S3, Elastic MapReduce and Futuregrid are used to perform computation and determine the trends. Since Wikipedia is available in several languages across the world, trends of Wikipages in different languages can be determined as well.



Source files contains

Java Files:

Map Reduce Tasks

TitleCleaner.java - It cleans the raw input and identifies valid data with the help of regular expressions.

TrueTileExtractor.java -The Mapper finds the true title corresponding to each search topic. The output of the mapper 
is key: true title and search date separated by tab and value: number of searches.

TitleReductor.java - This class file reduces the output files for same task from various sources

UniqueTitles.java - This class takes input as all the output files of various days to be used for finding top trending 
topics and get unique titles from these files.

TopTrendingTopics.java - This mapper calculates the weight corresponding to each search term Output is key: 
true title value: weight for each search term

Data Migration

Migrate.java - Migrate class migrates data from map reduce task and puts the values in the oracle database created.

InsertTrend.java - This file inserts trend statistics in pageviews table

InsertTrendTitles.java - This class insert trend titles from files in a folder into database in table – pagetitle it inserts one row per insert

MigrateLanguage.java – It migrates data from map reduce task and puts the values in the oracle database created.

InsertLanguage.java - This file inserts trend statistics in languagetitles table.

InsertLanguageTitles.java - This class insert language titles from files in A folder into database in table.

