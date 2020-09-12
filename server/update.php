<?php

//constants
$lastglobal = "2.7.7";

$lastprealpha = "2.6.11";
$lastalpha = "2.6.18";
$lastprebeta = "2.7.7";
$lastclosedbeta = "none";
$lastopenbeta = "none";
$lastprerelease = "none";
$lastupdatebeta = "none";
$lastrelease = "none";
$lastlts = "none";
$lastupdatebetacantupd = "none";

$releaseupdated == false;

$v = $_GET["v"];
$e = $_GET["e"];

$a = explode(".", $v);
if($e == "prealpha")
{
	if($v != $lastprealpha)
	{
		echo "outdated"
	} else
	{
		echo "uptodate";
	}
} else
if($e == "release")
{
	if($releaseupdated == true)
	{
		echo "outdated";
	} else
	{
		echo "uptodate";
	}
} else
if($e == "alpha")
{
	if($v != $lastalpha)
	{
		echo "outdated";
	} else
	{
		echo "uptodate";
	}
} else
if($e == "prebeta")
{
	if($v != $lastprebeta)
	{
		echo "outdated";
	} else
	{
		echo "uptodate";
	}
} else
if($e == "closedbeta")
{
	
	if($v != $lastclosedbeta)
	{
		echo "outdated";
	} else
	{
		echo "uptodate";
	}
} else
if($e == "openbeta")
{
	if($v != $lastopenbeta)
	{
		echo "outdated";
	} else
	{
		echo "uptodate";
	}
} else
if($e == "releaseupdate")
{
	if($v != $lastrelease)
	{
		echo "outdated";
	} else
	{
		echo "uptodate";
	}
} else
if($e == "releaseupdatebeta")
{
	if($v != $lastupdatebeta || ($v == $lastupdatebeta && $v != $lastrelease && $v != $lastupdatebetacantupd))
	{
		echo "outdated";
	} else
	{
		echo "uptodate";
	}
if($e == "longtimesupport")
{
	if($v != $lastlts)
	{
		echo "outdated";
	} else
	{
		echo "uptodate";
	}
} else
?>