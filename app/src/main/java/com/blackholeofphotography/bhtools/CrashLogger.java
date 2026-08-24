package com.blackholeofphotography.bhtools;

import android.os.Environment;
import android.os.StrictMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CrashLogger
{
   private static final Logger logger = LoggerFactory.getLogger (CrashLogger.class);

   public static void logException (Exception ex)
   {
      final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites ();
      try
      {
         String ymd = ZonedDateTime.now ().format (DateTimeFormatter.ofPattern ("yyyy-MM-dd_HH-mm-ss"));
         String backupFileName = String.format ("crash%s.byp", ymd);
         final File downloadsDirectory = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS);
         File backupFile = new File (downloadsDirectory, backupFileName);

         StringBuilder sb = new StringBuilder ();
         sb.append (ex.toString ());
         sb.append ("\n");
         for (var trace : ex.getStackTrace ())
         {
            sb.append ("    at ");
            sb.append (trace.toString ());
            sb.append ("\n");
         }

         StandardOpenOption mode = StandardOpenOption.CREATE;
         if (backupFile.exists ())
            mode = StandardOpenOption.APPEND;

         Files.write (backupFile.toPath (), sb.toString ().getBytes (), mode);
      }
      catch (IOException e)
      {
         logger.error ("CrashLogger: ", ex);
      }
      finally
      {
         StrictMode.setThreadPolicy (oldPolicy);
      }
   }

   public static void logException (Throwable ex)
   {
      final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites ();
      try
      {
         String ymd = ZonedDateTime.now ().format (DateTimeFormatter.ofPattern ("yyyy-MM-dd_HH-mm-ss"));
         String backupFileName = String.format ("crash%s.byp", ymd);
         final File downloadsDirectory = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS);
         File backupFile = new File (downloadsDirectory, backupFileName);

         StringBuilder sb = new StringBuilder ();
         sb.append (ex.toString ());
         sb.append ("\n");
         for (var trace : ex.getStackTrace ())
         {
            sb.append ("    at ");
            sb.append (trace.toString ());
            sb.append ("\n");
         }

         StandardOpenOption mode = StandardOpenOption.CREATE;
         if (backupFile.exists ())
            mode = StandardOpenOption.APPEND;

         Files.write (backupFile.toPath (), sb.toString ().getBytes (), mode);
      }
      catch (IOException e)
      {
         logger.error ("CrashLogger: ", ex);
      }
      finally
      {
         StrictMode.setThreadPolicy (oldPolicy);
      }
   }

   public static String getLastCrash ()
   {
      final File downloadsDirectory = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS);
      File[] files = downloadsDirectory.listFiles (new FilenameFilter ()
      {
         @Override
         public boolean accept (File dir, String name)
         {
            return name.startsWith ("crash") && name.endsWith (".byp");
         }
      });

      if (files != null && files.length > 0)
      {
         File latest = files[0];
         for (File f : files)
            if (f.compareTo (latest) > 0)
               latest = f;
         try (InputStream is = new FileInputStream (latest))
         {
            return ASTools.getStreamContents (is);
         }
         catch (IOException ex)
         {
            logger.error ("getLastCrash: ", ex);
         }
      }
      return null;
   }
}
