#!/usr/local/bin/php
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>	
<title>Cloud Trend</title>
<?php include('connect.php'); ?>

<link rel="stylesheet" type="text/css" href="cloudtrend/css/style.css" />
<script type="text/javascript" src="cloudtrend/js/image_slide.js"></script>
<script type="text/javascript" src="cloudtrend/js/script.js"></script>
<script type="text/javascript" src="cloudtrend/syntaxhighlighter/scripts/shCore.js"></script>
<script type="text/javascript" src="cloudtrend/syntaxhighlighter/scripts/shBrushJScript.js"></script>
<script type="text/javascript" src="cloudtrend/syntaxhighlighter/scripts/shBrushXml.js"></script>
<link class="include" rel="stylesheet" type="text/css" href="cloudtrend/css/jquery.jqplot.min.css" />
<link type="text/css" rel="stylesheet" href="cloudtrend/syntaxhighlighter/styles/shCoreDefault.min.css" />
<link type="text/css" rel="stylesheet" href="cloudtrend/syntaxhighlighter/styles/shThemejqPlot.min.css" />

<script class="include" type="text/javascript" src="cloudtrend/js/jquery.min.js"></script>

<link href="cloudtrend/css/jquery-ui-1.9.2.custom.css" rel="stylesheet" />
<script src="cloudtrend/js/jquery-1.8.3.js"></script>
<script src="cloudtrend/js/jquery-ui-1.9.2.custom.js"></script>
<script>
    $(function() {
        $( "#languageSearch" ).autocomplete({
		source: "languageTitles.php",
            minLength: 2,
            
        });
    });

$(function() {
        $( "#topic , #compareTopicFirst, #compareTopicSecond" ).autocomplete({
		source: "trendTitles.php",
            minLength: 2,
            
        });
    });
</script>

<script>
	$(function() {
		$( ".datepicker" ).datepicker({
			inline: true
		});
		
		// Hover states on the static widgets
		$( "#dialog-link, #icons li" ).hover(
			function() {
				$( this ).addClass( "ui-state-hover" );
			},
			function() {
				$( this ).removeClass( "ui-state-hover" );
			}
		);
	});
</script>

<?php 

displayHottopisgraph();

// This function is used to display the hot topics on the front page of cloudTrend.
function displayHottopisgraph()
{
session_start();

if (!require("connect.php"))
{
echo "connection failure";
}

$query="SELECT * FROM HOTTOPICS ORDER BY TOPIC_WEIGHT DESC";
$statement = oci_parse($connection,$query);
oci_execute($statement);

$topicNames = array();
$topicWeights = array();
$topicViews = array();

$_GLOBAL[0] = $topicNames ;
$_GLOBAL[1] = $topicWeights ;
$_GLOBAL[2] = $topicViews ;

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($topicNames , $row1['TOPIC_NAME']);
array_push($topicWeights , $row1['TOPIC_WEIGHT']);
array_push($topicViews , $row1['TOPIC_VIEWS']);
}

// GET VALUES WITH CONNECTION ----------- KUSHAL

$query="SELECT title, TO_CHAR(viewdate, 'YYYYMMDD') as viewdate, pageviews FROM PAGETITLES WHERE UPPER(title) = UPPER('".$topicNames[0]."') ORDER BY viewdate";
$statement = oci_parse($connection,$query);
oci_execute($statement);

$title = array();
$viewdate = array();
$pageviews = array();

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($title , $row1['TITLE']);
array_push($viewdate , $row1['VIEWDATE']);
array_push($pageviews , $row1['PAGEVIEWS']);
}

// GET VALUES WITH CONNECTION ----------- SOHAM

$statement = oci_parse($connectionS,$query);
oci_execute($statement);

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($title , $row1['TITLE']);
array_push($viewdate , $row1['VIEWDATE']);
array_push($pageviews , $row1['PAGEVIEWS']);
}

// GET VALUES WITH CONNECTION ----------- NEHA

$statement = oci_parse($connectionN,$query);
oci_execute($statement);

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($title , $row1['TITLE']);
array_push($viewdate , $row1['VIEWDATE']);
array_push($pageviews , $row1['PAGEVIEWS']);
}

// GET VALUES WITH CONNECTION ----------- BHARAT

$statement = oci_parse($connectionB,$query);
oci_execute($statement);

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($title , $row1['TITLE']);
array_push($viewdate , $row1['VIEWDATE']);
array_push($pageviews , $row1['PAGEVIEWS']);
}

array_multisort($viewdate , $pageviews , $title );


// convert view date to dd-mmm-yy format
$finalDates = array();
foreach ($viewdate as &$value) 
{
array_push($finalDates ,  date("d M Y", strtotime($value))   );
}

$jsonTitle = json_encode( $title[0]) ;
$jsonViewDate = json_encode( $finalDates );
$jsonPageViews = json_encode( $pageviews );
$jsonChartArea = json_encode('hotTrendChart');
echo <<<EOT


<script >
$(document).ready(function(){
EOT;

$x = "<br/><br/><h1> TOP 20 Trending Topics on Wikipedia </h1><br/>";
$x .= "<table class='hovertable' align='center' width='600' style='margin-left:100px;'>";
$x .= "<tr onmouseover='this.style.backgroundColor='#0000A0' onmouseout='this.style.backgroundColor='#d4e3e5'><td>#</td><td>Topic Weight</td><td>Title</td><td>#Views</td></tr>";
for($i = 0; $i<count($topicNames); $i++) {
	$rank=$i+1;
	$x.= "<tr onmouseover='this.style.backgroundColor='#0000A0' onmouseout='this.style.backgroundColor='#d4e3e5'><td width='50'>$rank</td><td width='50'>$topicWeights[$i]</td><td width='200'><a href='http://en.wikipedia.org/wiki/$topicNames[$i]' target='_blank'>$topicNames[$i]</a></td><td width='50'>$topicViews[$i]</td></tr>";
}
$x .= "</table>";
echo <<<EOT

	
$('#displayGraph').html("$x");
displayLineChart($jsonTitle, $jsonViewDate, $jsonPageViews, $jsonChartArea);
});
EOT;

}
?>

</script>

<script type="text/javascript">
/***********************************************
* Dynamic Ajax Content- © Dynamic Drive DHTML code library (www.dynamicdrive.com)
* This notice MUST stay intact for legal use
* Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
***********************************************/

var bustcachevar=1 //bust potential caching of external pages after initial request? (1=yes, 0=no)
var loadedobjects=""
var rootdomain="http://"+window.location.hostname
var bustcacheparameter=""

function ajaxpage(url, containerid){
var page_request = false
if (window.XMLHttpRequest) // if Mozilla, Safari etc
page_request = new XMLHttpRequest()
else if (window.ActiveXObject){ // if IE
try {
page_request = new ActiveXObject("Msxml2.XMLHTTP")
} 
catch (e){
try{
page_request = new ActiveXObject("Microsoft.XMLHTTP")
}
catch (e){}
}
}
else
return false
page_request.onreadystatechange=function(){
loadpage(page_request, containerid)
}
if (bustcachevar) //if bust caching of external page
bustcacheparameter=(url.indexOf("?")!=-1)? "&"+new Date().getTime() : "?"+new Date().getTime()
page_request.open('GET', url+bustcacheparameter, true)
page_request.send(null)
}

function loadpage(page_request, containerid){
if (page_request.readyState == 4 && (page_request.status==200 || window.location.href.indexOf("http")==-1))
document.getElementById(containerid).innerHTML=page_request.responseText
}

function loadobjs(){
if (!document.getElementById)
return
for (i=0; i<arguments.length; i++){
var file=arguments[i]
var fileref=""
if (loadedobjects.indexOf(file)==-1){ //Check to see if this object has not already been added to page before proceeding
if (file.indexOf(".js")!=-1){ //If object is a js file
fileref=document.createElement('script')
fileref.setAttribute("type","text/javascript");
fileref.setAttribute("src", file);
}
else if (file.indexOf(".css")!=-1){ //If object is a css file
fileref=document.createElement("link")
fileref.setAttribute("rel", "stylesheet");
fileref.setAttribute("type", "text/css");
fileref.setAttribute("href", file);
}
}
if (fileref!=""){
document.getElementsByTagName("head").item(0).appendChild(fileref)
loadedobjects+=file+" " //Remember this object as being already added to page
}
}
}

</script>
<style>
a:hover{color:orange;}
</style>
</head>

<body onload="onloadFunction()">
<div id="myArea"></div>
       <div id="main">
	<div id="header">
	  <div id="banner">
	    <div id="welcome">
	      <h1>Welcome To Cloud Trend</h1>
	    </div><!--close welcome-->
	  </div><!--close banner-->
    </div><!--close header-->	
<script>

// This function is called when the html body loads
// Specific set of lines are run depending on the the page loaded.
function onloadFunction()
{
<?php 

if($_GET['do'] == 'trend')
{
?>
$("#barChartTrend").show();
$("#lineChartTrend").show();
$("#trend_contentarea").show();
$("#content_message_topic").html("Trending Topics");
$("#content_message").html("Enter topic and dates to view the trend of a topic!!! <br/ >Enter only dates to view top 5 trending topics between the entered dates!!!!");

$("#displayGraph").hide();
$("#compare_contentarea").hide();
$("#category_contentarea").hide();
$("#hotTrendChart").hide();
$("#compareChart").hide();
$("#pieChart").hide();
$("#chart3").hide();
$("#compareLineChart").hide();

<?php
}else if($_GET['do'] == 'compare')
{
?>
$("#compareChart").show();
$("#compare_contentarea").show();
$("#content_message_topic").html("Compare Trending Topics");
$("#content_message").html("Enter topics and dates to view the trend comparison of a topic!!!");

$("#displayGraph").hide();
$("#hotTrendChart").hide();
$("#trend_contentarea").hide();
$("#pieChart").hide();
$("#category_contentarea").hide();
$("#chart3").hide();
$("#barChartTrend").hide();
$("#lineChartTrend").hide();
$("#compareLineChart").show();
<?php
}else if ($_GET['do'] == 'category')
{
?>

$("#category_contentarea").show();
$("#chart3").show();
$("#pieChart").show();
$("#content_message_topic").html("Trending Languages");
$("#content_message").html("Enter topic and dates to view the trend of a language!!! <br/ >Enter only dates to view top 10 trending languages between the entered dates!!!!");

$("#displayGraph").hide();
$("#hotTrendChart").hide();
$("#trend_contentarea").hide();
$("#compare_contentarea").hide();
$("#compareChart").hide();
$("#barChartTrend").hide();
$("#lineChartTrend").hide();
$("#compareLineChart").hide();
<?php
}else
{
?>

$("#displayGraph").show();
$("#hotTrendChart").show();
$("#content_message_topic").html("Top Trending Topics");
$("#content_message").html("Trending Topics according to the searches made in Wikipedia ..");

$("#trend_contentarea").hide();
$("#pieChart").hide();
$("#chart3").hide();
$("#compare_contentarea").hide();
$("#category_contentarea").hide();
$("#compareChart").hide();
$("#barChartTrend").hide();
$("#lineChartTrend").hide();
$("#compareLineChart").hide();
<?php

}
?>

}

// This function is called to display the contents of home page
function currentTrend()
{
$("#hotTrendChart").show();
$("#displayGraph").show();

$("#trend_contentarea").hide();
$("#compare_contentarea").hide();
$("#category_contentarea").hide();
$("#chart3").hide();
$("#compareChart").hide();
$("#pieChart").hide();
$("#barChartTrend").hide();
$("#lineChartTrend").hide();
$("#compareLineChart").hide();
}

// This function is called to display the contents of trend analysis page
function trendAnalysis()
{
$("#trend_contentarea").show();
$("#barChartTrend").show();
$("#lineChartTrend").show();
$("#chart3").hide();
$("#content_message_topic").html("Trending Topics");
$("#content_message").html("Enter topic and dates to view the trend of a topic!!! <br/ >Enter only dates to view top 5 trending topics between the entered dates!!!!");

$("#hotTrendChart").hide();
$("#displayGraph").hide();
$("#compare_contentarea").hide();
$("#category_contentarea").hide();
$("#compareChart").hide();
$("#pieChart").hide();
$("#compareLineChart").hide();
}

// This function is called to display the contents of compare page
function compare()
{
$("#compare_contentarea").show();
$("#compareChart").show();
$("#compareLineChart").show();
$("#content_message_topic").html("Compare Trending Topics");
$("#content_message").html("Enter topics and dates to view the trend comparison of a topic!!!");

$("#hotTrendChart").hide();
$("#trend_contentarea").hide();
$("#displayGraph").hide();
$("#category_contentarea").hide();
$("#pieChart").hide();
$("#chart3").hide();
$("#barChartTrend").hide();
$("#lineChartTrend").hide();
}

// This function is called to display the contents of category page
function categoryAnalysis()
{
$("#category_contentarea").show();
$("#chart3").show();
$("#pieChart").show();
$("#content_message_topic").html("Trending Languages");
$("#content_message").html("Enter topic and dates to view the trend of a language!!! <br/ >Enter only dates to view top 10 trending languages between the entered dates!!!!");

$("#hotTrendChart").hide();
$("#compare_contentarea").hide();
$("#trend_contentarea").hide();
$("#displayGraph").hide();
$("#compareChart").hide();
$("#barChartTrend").hide();
$("#lineChartTrend").hide();
$("#compareLineChart").hide();
}

</script>

<div id="menubar">
      <ul id="menu">
       <li id="currentTrend" ><a href="cloudtrend.php">Current Trend</a></li>
       <li id="trendAnalysis" onclick="trendAnalysis()"><a>Trend Analysis</a></li>
       <li id="compare" onclick="compare()"><a>Compare</a></li>
       <li id="categoryAnalysis" onclick="categoryAnalysis()"><a>Category Analysis</a></li>       
      </ul>
</div><!--close menubar-->	

<div id="site_content">
	<div class="sidebar_container">       
		<div class="sidebar">
			<div class="sidebar_item" id="abcd">
				<p>Welcome to the project : Cloud Trend </p>
			</div><!--close sidebar_item--> 
	</div><!--close sidebar-->     		
</div><!--close sidebar_container-->		 
	 
<div id="content">
	<div class="content_item">
		<h1 id="content_message_topic">Trending Topics</h1> 
		<p id="content_message">Trending Topics according to the searches made in Wikipedia ..</p>		
	<div id="contentarea">

<div id="trend_contentarea">
<form action="cloudtrend.php?do=trend" method="post">
<table cellpadding="10" cellspacing="5">
<tr><td><input type="text" name="name" id="topic" class="ui-autocomplete-input" placeholder="enter topic here" /></td></tr>
<tr><td><input type="text" class="datepicker" name="fromDate" placeholder="enter from date"/></td></tr>
<tr><td><input type="text" class="datepicker" name="toDate" placeholder="enter to date"/></td></tr>
<tr><td><input type="submit" value="view trend"/></td></tr>
</table>
</form>

<?php
// This code is used to display the contents of trend analysis module
if ($_GET['do'] == 'trend') {

$var1 =  $_POST["name"];
$var2 =  $_POST["fromDate"];
$var3 =  $_POST["toDate"];

if($var2 == null)
$var2 = '01/01/2010';

if($var3 == null)
$var3 = date("m/d/Y");
/*
echo $var1;
echo $var2;
echo $var3;
*/

$totalTrend = 0;
if($var1 != null)
{
$query="SELECT title, TO_CHAR(viewdate, 'YYYYMMDD') AS viewdate, pageviews FROM PAGETITLES WHERE UPPER(title) = UPPER('".$var1."') and viewdate between to_date('".$var2."','mm/dd/yyyy') and to_date('".$var3."','mm/dd/yyyy') ORDER BY viewdate";
}else
{
$totalTrend = 1;
$query="select pageviews, title from (select sum(pageviews ) as pageviews , title from PAGETITLES WHERE ViewDate between to_date('".$var2."','mm/dd/yyyy') and to_date('".$var3."','mm/dd/yyyy') group by title ORDER BY pageviews DESC) where rownum < 11";
}

// GET VALUES WITH CONNECTION ----------- KUSHAL
$statement = oci_parse($connection,$query);
oci_execute($statement);

$title = array();
$viewdate = array();
$pageviews = array();

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
	if($totalTrend == 1){
		if(in_array($row1['TITLE'], $title ))
		{
			$key1 = array_search($row1['TITLE'], $title );
			$pageviews[$key1] = $pageviews[$key1] + $row1['PAGEVIEWS'];
		}else{
			array_push($title , $row1['TITLE']);
			array_push($viewdate , $row1['VIEWDATE']);
			array_push($pageviews , $row1['PAGEVIEWS']);
		}
	}else{
		array_push($title , $row1['TITLE']);
		array_push($viewdate , $row1['VIEWDATE']);
		array_push($pageviews , $row1['PAGEVIEWS']);
	}
}

// GET VALUES WITH CONNECTION ----------- SOHAM
$statement = oci_parse($connectionS,$query);
oci_execute($statement);

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
	if($totalTrend == 1){
		if(in_array($row1['TITLE'], $title ))
		{
			$key1 = array_search($row1['TITLE'], $title );
			$pageviews[$key1] = $pageviews[$key1] + $row1['PAGEVIEWS'];
		}else{
			array_push($title , $row1['TITLE']);
			array_push($viewdate , $row1['VIEWDATE']);
			array_push($pageviews , $row1['PAGEVIEWS']);
		}
	}else{
		array_push($title , $row1['TITLE']);
		array_push($viewdate , $row1['VIEWDATE']);
		array_push($pageviews , $row1['PAGEVIEWS']);
	}
}

// GET VALUES WITH CONNECTION ----------- NEHA
$statement = oci_parse($connectionN,$query);
oci_execute($statement);

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
	if($totalTrend == 1){
		if(in_array($row1['TITLE'], $title ))
		{
			$key1 = array_search($row1['TITLE'], $title );
			$pageviews[$key1] = $pageviews[$key1] + $row1['PAGEVIEWS'];
		}else{
			array_push($title , $row1['TITLE']);
			array_push($viewdate , $row1['VIEWDATE']);
			array_push($pageviews , $row1['PAGEVIEWS']);
		}
	}else{
		array_push($title , $row1['TITLE']);
		array_push($viewdate , $row1['VIEWDATE']);
		array_push($pageviews , $row1['PAGEVIEWS']);
	}
}

// GET VALUES WITH CONNECTION ----------- BHARAT
$statement = oci_parse($connectionB,$query);
oci_execute($statement);

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
	if($totalTrend == 1){
		if(in_array($row1['TITLE'], $title ))
		{
			$key1 = array_search($row1['TITLE'], $title );
			$pageviews[$key1] = $pageviews[$key1] + $row1['PAGEVIEWS'];
		}else{
			array_push($title , $row1['TITLE']);
			array_push($viewdate , $row1['VIEWDATE']);
			array_push($pageviews , $row1['PAGEVIEWS']);
		}
	}else{
		array_push($title , $row1['TITLE']);
		array_push($viewdate , $row1['VIEWDATE']);
		array_push($pageviews , $row1['PAGEVIEWS']);
	}
}

if($totalTrend == 1){
	array_multisort($pageviews ,SORT_DESC , $viewdate, $title );
	$pageviews  = array_slice($pageviews  , 0, 5);
	$viewdate= array_slice($viewdate, 0, 5);
	$title = array_slice($title , 0, 5);
}else
{
	array_multisort($viewdate , $pageviews, $title );
}

// convert view date to dd-mmm-yy format
$finalDates = array();
foreach ($viewdate as &$value) 
{
array_push($finalDates ,  date("d M Y", strtotime($value))   );
}


$jsonTitle = json_encode( $var1 ) ;
$jsonAllTitle = json_encode( $title ) ;
$jsonViewDate = json_encode( $finalDates );
$jsonPageViews = json_encode( $pageviews );
$jsonChartTitle = json_encode( $var1 ) ;

if($totalTrend == 1)
{
//echo"totalTrend == 1";
$jsonViewDate = json_encode( $title ) ;
$jsonChartTitle = json_encode('top 5 topics');
}

$jsonChartArea = json_encode('lineChartTrend');

echo <<<EOT
<script type="text/javascript" src="cloudtrend/js/jquery-1.5.2.min.js"></script>


<script >
$(document).ready(function(){
if($totalTrend == 0)
{
displayLineChart($jsonTitle, $jsonViewDate , $jsonPageViews,$jsonChartArea);
}
//displayPieChart($jsonAllTitle , $jsonPageViews);
displayTrendBarChart($jsonTitle, $jsonViewDate , $jsonPageViews, $jsonChartTitle);
// JSON.stringify($jsonViewDate)
EOT;

echo <<<EOT

});
</script>
EOT;

}
?>
</div>

<div id="compare_contentarea" display="none">
<form action="cloudtrend.php?do=compare" method="post">
<table cellpadding="10" cellspacing="5">
<tr><td><input type="text" name="compareEntity1" id="compareTopicFirst" class="ui-autocomplete-input" placeholder="enter topic1 here" placeholder="compare title two"/></td></tr>
<tr><td><input type="text" name="compareEntity2" id="compareTopicSecond" class="ui-autocomplete-input" placeholder="enter topic2 here" placeholder="compare title one"/></td></tr>
<tr><td><input type="text" class="datepicker" name="fromDate" placeholder="enter from date"/></td></tr>
<tr><td><input type="text" class="datepicker" name="toDate" placeholder="enter to date"/></td></tr>
<tr><td><input type="radio" name="trendTopic" value="Trends" checked="checked"  /> Entities   <input type="radio" name="trendTopic" value="Language" />Language</td></tr>
<tr><td><input type="submit" value="view trend" /></td></tr>
</table>
</form>

<?php
if ($_GET['do'] == 'compare') {
// This code is used to display the contents of compare module
$var1 =  $_POST["compareEntity1"];
$var2 =  $_POST["compareEntity2"];
$var3 =  $_POST["fromDate"];
$var4 =  $_POST["toDate"];
$var5 =  $_POST["trendTopic"];


if($var3 == null)
$var3 = '01/01/2010';

if($var4 == null)
$var4 = date("m/d/Y");


$displayTitles = array($var1, $var2);
$query= "";
if($var5 == 'Trends'){
$query="select t3.title as pagetitle1, t3.pageviews as noofviews1, t4.title as pagetitle2, t4.pageviews as noofviews2, TO_CHAR(t3.viewdate, 'YYYYMMDD') as viewdate1,  TO_CHAR(t4.viewdate, 'YYYYMMDD') as viewdate2 from
(SELECT title, viewdate, pageviews FROM pagetitles WHERE UPPER(title) = UPPER('".$var1."') and viewdate between to_date('".$var3."','mm/dd/yyyy') and to_date('".$var4."','mm/dd/yyyy')) t3
full outer join
(SELECT title, viewdate, pageviews FROM pagetitles WHERE UPPER(title) = UPPER('".$var2."') and viewdate between to_date('".$var3."','mm/dd/yyyy') and to_date('".$var4."','mm/dd/yyyy')) t4
on t3.viewdate = t4.viewdate
";
}else{
$query="select l3.languagetitle as pagetitle1, l3.noofviews as noofviews1, l4.languagetitle as pagetitle2, l4.noofviews as noofviews2, TO_CHAR(l3.viewdate, 'YYYYMMDD') as viewdate1,  TO_CHAR(l4.viewdate, 'YYYYMMDD') as viewdate2 
from
(select l1.languagetitle,l2.viewdate,l2.noofviews from languagetitles l1, languagedata l2 where l1.languageid = l2.languageid  and UPPER(l1.languagetitle) = UPPER('".$var1."')   and l2.ViewDate between to_date('".$var3."','mm/dd/yyyy') and to_date('".$var4."','mm/dd/yyyy')) l3
full outer join
(select l1.languagetitle,l2.viewdate,l2.noofviews from languagetitles l1, languagedata l2 where l1.languageid = l2.languageid  and UPPER(l1.languagetitle) = UPPER('".$var2."')   and l2.ViewDate between to_date('".$var3."','mm/dd/yyyy') and to_date('".$var4."','mm/dd/yyyy')) l4
on l3.viewdate = l4.viewdate
";
}

// GET VALUES WITH CONNECTION ----------- KUSHAL
$statement = oci_parse($connection,$query);
oci_execute($statement);

$title1 = array();
$title2 = array();
$viewdate1 = array();
$viewdate2 = array();
$pageviews1 = array();
$pageviews2 = array();

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($title1 , $row1['PAGETITLE1']);
array_push($title2 , $row1['PAGETITLE2']);
array_push($viewdate1 , $row1['VIEWDATE1']);
array_push($viewdate2 , $row1['VIEWDATE2']);
array_push($pageviews1 , $row1['NOOFVIEWS1']);
array_push($pageviews2 , $row1['NOOFVIEWS2']);
}

// GET VALUES WITH CONNECTION ----------- SOHAM

$statement = oci_parse($connectionS,$query);
oci_execute($statement);

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($title1 , $row1['PAGETITLE1']);
array_push($title2 , $row1['PAGETITLE2']);
array_push($viewdate1 , $row1['VIEWDATE1']);
array_push($viewdate2 , $row1['VIEWDATE2']);
array_push($pageviews1 , $row1['NOOFVIEWS1']);
array_push($pageviews2 , $row1['NOOFVIEWS2']);
}

// GET VALUES WITH CONNECTION ----------- NEHA
$statement = oci_parse($connectionN,$query);
oci_execute($statement);

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($title1 , $row1['PAGETITLE1']);
array_push($title2 , $row1['PAGETITLE2']);
array_push($viewdate1 , $row1['VIEWDATE1']);
array_push($viewdate2 , $row1['VIEWDATE2']);
array_push($pageviews1 , $row1['NOOFVIEWS1']);
array_push($pageviews2 , $row1['NOOFVIEWS2']);
}

// GET VALUES WITH CONNECTION ----------- BHARAT
$statement = oci_parse($connectionB,$query);
oci_execute($statement);

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($title1 , $row1['PAGETITLE1']);
array_push($title2 , $row1['PAGETITLE2']);
array_push($viewdate1 , $row1['VIEWDATE1']);
array_push($viewdate2 , $row1['VIEWDATE2']);
array_push($pageviews1 , $row1['NOOFVIEWS1']);
array_push($pageviews2 , $row1['NOOFVIEWS2']);
}


$finalviewdate = array();
$count1 = count($viewdate1 );
$count2 = count($viewdate2 );

// add all dates to the final array
foreach ($viewdate1 as &$value) {
if($value != "")
{
if (in_array($value, $finalviewdate )) {
}else{

array_push($finalviewdate , $value);
}
}
}

foreach ($viewdate2 as &$value) {
if($value != "")
{
if (in_array($value, $finalviewdate )) {
}else{
array_push($finalviewdate , $value);
}
}
}

//print_r($finalviewdate);
sort($finalviewdate);

//echo("<br />");
//print_r($finalviewdate);
//echo("<br />");

$finalarrayone = array();
$finalarraytwo = array();

// create multi dimentional array with date and two pageviews
for ($i = 0; $i <= (count($finalviewdate) - 1) ; $i++) 
{
	$key1 = array_search($finalviewdate[$i], $viewdate1 );

	if(in_array($finalviewdate[$i], $viewdate1 ))
	{
		array_push($finalarrayone, $pageviews1[$key1]);
	}else
	{
		array_push($finalarrayone, 0);
	}

	$key2 = array_search($finalviewdate[$i], $viewdate2 );

	if(in_array($finalviewdate[$i], $viewdate2 ))
	{
		array_push($finalarraytwo, $pageviews2[$key2]);
	}else
	{
		array_push($finalarraytwo, 0);
	}
}


//print_r($finalarrayone);
//echo("<br />");
//print_r($finalarraytwo);
// sort multi dimentional array

// convert view date to dd-mmm-yy format
$finalDates = array();
foreach ($finalviewdate as &$value) 
{
array_push($finalDates ,  date("d M Y", strtotime($value))   );
}


$jsonViewDate = json_encode( $finalDates );
$jsoncompareone = json_encode( $finalarrayone);
$jsoncomparetwo = json_encode( $finalarraytwo);
$jsonDisplayTitles = json_encode( $displayTitles);
//echo $jsonDisplayTitles;

echo <<<EOT
<script type="text/javascript" src="cloudtrend/js/jquery-1.5.2.min.js"></script>

<script >
$(document).ready(function(){
displayCompareChart($jsonViewDate, $jsoncompareone, $jsoncomparetwo, $jsonDisplayTitles);
displayCompareLineChart($jsonViewDate, $jsoncompareone, $jsoncomparetwo, $jsonDisplayTitles);
EOT;

echo <<<EOT

});
</script>
EOT;

}
?>
</div>

<div id="category_contentarea" display="none">

<form action="cloudtrend.php?do=category" method="post">
<table>
<tr><td><input type="text" name="countryName" id="languageSearch" class="ui-autocomplete-input" placeholder="enter topic here" /></td></tr>
<tr><td><input type="text" class="datepicker" name="fromDate" placeholder="enter from date"/></td></tr>
<tr><td><input type="text" class="datepicker" name="toDate" placeholder="enter to date"/></td></tr>
<tr><td><input type="submit" value="view trend"/></td></tr>
</table>
</form>


<?php
if ($_GET['do'] == 'category') {
// This code is used to display the contents of category module
$countryName =  $_POST["countryName"];
$fromDateCountry =  $_POST["fromDate"];
$toDateCountry =  $_POST["toDate"];

if($fromDateCountry == null)
$fromDateCountry = '01/01/2010';

if($toDateCountry == null)
$toDateCountry = date("m/d/Y");

$completeTrend = 0;
if($countryName != null)
{
$query="select l1.languagetitle, l2.viewdate, l2.noofviews from languagetitles l1, languagedata l2 where l1.languageid = l2.languageid and UPPER(l1.languagetitle) = UPPER('".$countryName."')  and l2.ViewDate between to_date('".$fromDateCountry."','mm/dd/yyyy') and to_date('".$toDateCountry."','mm/dd/yyyy')  order by l2.viewdate";
}else
{
$completeTrend = 1;
$query="select * from (
select  sum(l2.noofviews) as noofviews, l1.languagetitle
from languagetitles l1, languagedata l2
where l1.languageid = l2.languageid 
and l2.ViewDate between to_date('".$fromDateCountry."','mm/dd/yyyy') and to_date('".$toDateCountry."','mm/dd/yyyy')
group by l1.languagetitle 
order by noofviews desc)
where
rownum < 11";
}
$statement = oci_parse($connection,$query);
oci_execute($statement);

$title = array();
$viewdate = array();
$pageviews = array();

while(($row1 = oci_fetch_array($statement , OCI_BOTH)))
{
array_push($title , $row1['LANGUAGETITLE']);
array_push($viewdate , $row1['VIEWDATE']);
array_push($pageviews , $row1['NOOFVIEWS']);
}

/*
print_r($title);
echo "<br/>";
print_r($viewdate);
echo "<br/>";
print_r($pageviews);
*/

$jsonTitle = json_encode( $countryName ) ;
$jsonAllTitle = json_encode( $title ) ;
$jsonViewDate = json_encode( $viewdate );
$jsonPageViews = json_encode( $pageviews );

if($completeTrend == 1)
{
$jsonViewDate = $jsonAllTitle;
$jsonTitle = json_encode( 'languages' ) ;
}

echo <<<EOT
<script type="text/javascript" src="cloudtrend/js/jquery-1.5.2.min.js"></script>

<script>
$(document).ready(function(){
if($completeTrend == 1){
	displayPieChart($jsonAllTitle , $jsonPageViews);
}
displayBarChart($jsonTitle, $jsonViewDate , $jsonPageViews);
EOT;

echo <<<EOT

});
</script>
EOT;
}

?>

</div>

<div id="compareChart" style="height:300px; width:800px;"></div>
<div id="chart3" style="height:300px; width:800px;"></div>
<div id="barChartTrend" style="height:300px; width:800px;"></div>
<style type="text/css">
    
    .note {
        font-size: 0.8em;
    }
    .jqplot-yaxis-tick {
      white-space: nowrap;
    }
  </style>
        
    <div id="hotTrendChart" style="width:800px; height:250px;"></div>
    <div id="compareLineChart" style="width:800px; height:250px;"></div>
    <div id="lineChartTrend" style="width:800px; height:250px;"></div>

<pre class="code prettyprint brush: js"></pre>
<!--    <pre class="code brush: js"></pre> -->


<style type="text/css">
    
    .note {
        font-size: 0.8em;
    }
    .jqplot-yaxis-tick {
      white-space: nowrap;
    }
  </style>
        
    <div id="pieChart" style="width:700px; height:350px;"></div>

<pre class="code prettyprint brush: js"></pre>



<div id="displayGraph" align="center">
</div>
</div>
<!-- End example scripts -->


</div><!--close content_item-->
      </div><!--close content-->   
	</div><!--close site_content--> 
<!-- Don't touch this! -->
    <script class="include" type="text/javascript" src="cloudtrend/js/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="cloudtrend/syntaxhighlighter/scripts/shCore.min.js"></script>
    <script type="text/javascript" src="cloudtrend/syntaxhighlighter/scripts/shBrushJScript.min.js"></script>
    <script type="text/javascript" src="cloudtrend/syntaxhighlighter/scripts/shBrushXml.min.js"></script>
<!-- End Don't touch this! -->

<!-- Additional plugins go here -->

	<script class="include" language="javascript" type="text/javascript" src="plugins/jqplot.barRenderer.min.js"></script>
	<script class="include" language="javascript" type="text/javascript" src="plugins/jqplot.categoryAxisRenderer.min.js"></script>
	<script class="include" language="javascript" type="text/javascript" src="plugins/jqplot.pointLabels.min.js"></script>
	<script class="include" type="text/javascript" src="plugins/jqplot.canvasTextRenderer.min.js"></script>
	<script class="include" type="text/javascript" src="plugins/jqplot.canvasAxisLabelRenderer.min.js"></script>
	<script class="include" language="javascript" type="text/javascript" src="plugins/jqplot.pieRenderer.min.js"></script>
	<script class="include" language="javascript" type="text/javascript" src="plugins/jqplot.donutRenderer.min.js"></script>
	<script class="include" language="javascript" type="text/javascript" src="plugins/jqplot.highlighter.min.js"></script>
	<script class="include" language="javascript" type="text/javascript" src="plugins/jqplot.cursor.min.js"></script>
	<script class="include" language="javascript" type="text/javascript" src="plugins/jqplot.dateAxisRenderer.min.js"></script>
<!-- End additional plugins -->


	 </div>	<!--close main-->

<div id="footer">
	  <a href="">Contact</a> | © Copyright 2012 CloudTrend |  EEL6935: 022A - Cloud Comp & Storage, Fall 2012
</div><!--close footer--> 
<script type="text/javascript" src="cloudtrend/js/drawchart.js"></script>
</body>
</html>