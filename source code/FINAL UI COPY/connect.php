<?php
putenv("ORACLE_HOME=/usr/local/libexec/oracle/app/oracle/product/11.2.0/client_1");

// KUSHAL'S CONNECTION
$connection = oci_connect($username = 'kkewlani',
                          $password = '',
                          $connection_string = '//oracle.cise.ufl.edu/orcl');

if (!$connection) {
$e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
	echo "ERROR connecting to Kushal's database";
}else
{
	//echo "SUCCESS connecting to Kushal's database";
}	

// SOHAM'S CONNECTION
$connectionS = oci_connect($username = 'soham',
                          $password = '',
                          $connection_string = '//oracle.cise.ufl.edu/orcl');

if (!$connectionS) {
$e = oci_error();
	trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
	echo "ERROR connecting to Soham's database";
}else
{
	//echo "SUCCESS connecting to Soham's database";
}

// NEHA'S CONNECTION
$connectionN = oci_connect($username = 'neha',
                          $password = '',
                          $connection_string = '//oracle.cise.ufl.edu/orcl');

if (!$connectionN) 
{
$e = oci_error();
	trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
	echo "ERROR connecting to Neha's database";
}else
{
	//echo "SUCCESS connecting to Neha's database";
}	



$connectionB = oci_connect($username = 'bpareek',
                          $password = '',
                          $connection_string = '//oracle.cise.ufl.edu/orcl');

if (!$connectionB) {
$e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
	echo "ERROR connecting to Bharat's database";
}else
{
	//echo "SUCCESS connecting to Bharat's database";
}	
	
?>

