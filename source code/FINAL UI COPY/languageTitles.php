#!/usr/local/bin/php
<?php include('connect.php'); ?>
<?php
//header('Content-type: application/json');
$conn = oci_connect($username = 'kkewlani', $password = '', $connection_string = '//oracle.cise.ufl.edu/orcl');
putenv("ORACLE_HOME=/usr/local/libexec/oracle/app/oracle/product/11.2.0/client_1");
$searchTerm = $_GET['term'];
$results=array();
$query = "SELECT DISTINCT languagetitle FROM languagetitles where upper(languagetitle) like upper('" . $searchTerm . "%') and rownum < 11";
$stid = oci_parse($connection, $query);
$r=oci_execute($stid);
//echo "hello";
//array_push($results,"hello");
while ($row = oci_fetch_object($stid)) {
array_push($results,$row->LANGUAGETITLE);  
}
echo json_encode($results);
?>
