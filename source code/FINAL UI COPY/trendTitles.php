#!/usr/local/bin/php
<?php include('connect.php'); ?>
<?php
//header('Content-type: application/json');
putenv("ORACLE_HOME=/usr/local/libexec/oracle/app/oracle/product/11.2.0/client_1");
$searchTerm = $_GET['term'];
$results=array();
$query = "SELECT DISTINCT TITLE FROM (SELECT title FROM pagetitles where upper(title) like upper('" . $searchTerm . "%') order by pageviews desc) WHERE rownum < 11";
$stid = oci_parse($connection, $query);
$r=oci_execute($stid);
while ($row = oci_fetch_object($stid)) {
array_push($results,$row->TITLE);  
}
echo json_encode($results);
?>
