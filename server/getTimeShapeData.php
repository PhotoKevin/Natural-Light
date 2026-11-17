<?php

// Config file for the environment if exists
$userConfig = __DIR__ . "/config.inc.php";

if (file_exists ($userConfig))
   include $userConfig;

if ($argv == null)
   $argv = [0 => ''];

function returnFile ($file)
{
   if (file_exists($file)) 
   {
      header ('Content-Description: File Transfer');
      header ('Content-Type: application/octet-stream');
      header ('Content-Disposition: attachment; filename="' . basename($file) . '"');
      header ('Content-Transfer-Encoding: binary');
      header ('Expires: 0');
      header ('Cache-Control: must-revalidate, post-check=0, pre-check=0');
      header ('Pragma: public');
      header ('Content-Length: ' . filesize($file));

      ob_clean();
      flush();
      readfile($file);
    } 
}

function returnNotModified ()
{
   http_response_code (304);

   ob_clean();
   flush();
}

function returnForbidden ()
{
   http_response_code (401);

   ob_clean();
   flush();
}


function findFile ($data_dir, $version)
{
   $pattern = "/TimeShapeData-(?<version>[0-9]{4}[a-z])-(?<areas>[0-9]+).tar.zstd/";

   $files = scandir ($data_dir, SCANDIR_SORT_ASCENDING);
   $prefix = "TimeShapeData-$version-";
   foreach ($files as $f)
   {
      if (preg_match ($pattern, $f, $matches) && $matches["version"] == $version)
         return realpath ($data_dir . '/' . $f);
   }

   return null;
}

function findNewer ($data_dir, $version)
{
   if (PHP_SAPI == 'cli')
      print 'findNewer' . PHP_EOL;

   $answer = null;
   $pattern = "/TimeShapeData-(?<version>[0-9]{4}[a-z])-(?<areas>[0-9]+).tar.zstd/";

   $files = scandir ($data_dir, SCANDIR_SORT_ASCENDING);
   $prefix = "TimeShapeData-$version-";
   foreach ($files as $f)
   {
      if (PHP_SAPI == 'cli')
      {
         print 'Check ' . $f . ' against pattern ==> ';
         print preg_match ($pattern, $f, $matches) . PHP_EOL;
         // print_r ($matches);
      }


      if (preg_match ($pattern, $f, $matches) && $matches["version"] > $version)
         return realpath ($data_dir . '/' . $f);
   }

   return null;
}



$files = scandir ($data_dir, SCANDIR_SORT_ASCENDING);

if (PHP_SAPI === 'cli') 
{
   if ((count ($argv) > 2) && ($argv[1] == 'get'))
      $getfile = $argv[2];

   if ((count ($argv) > 2) && ($argv[1] == 'newer'))
      $newer = $argv[2];
}
else 
{
   if ($_GET["get"] != null)
      $getfile = $_GET["get"];

   if (isset ($_GET["newer"]))
      $newer = $_GET["newer"];
}

$timeShapeFile = null;

if (isset ($getfile))
{
//   print 'get ' . $getfile . PHP_EOL;
   $timeShapeFile = findFile ($data_dir, $getfile);
   //print 'timeShapeFile is ' . $timeShapeFile . PHP_EOL;
}

if (isset ($newer))
{
   if (PHP_SAPI == 'cli') 
      print 'newer than ' . $newer . PHP_EOL;

   $timeShapeFile = findNewer ($data_dir, $newer);
//   print 'timeShapeFile is ' . $timeShapeFile . PHP_EOL;
}

if (PHP_SAPI != 'cli')
{
   $apikey = $_SERVER['HTTP_X_API_KEY'] ?? null;

   if (!isset ($apikey) || $apikey != $shape_api_key)
      returnForbidden ();
   else if (isset ($timeShapeFile))
      returnFile ($timeShapeFile);
   else
      returnNotModified ();
}

?>
