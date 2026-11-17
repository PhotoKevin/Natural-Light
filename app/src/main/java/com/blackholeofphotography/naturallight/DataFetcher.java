package com.blackholeofphotography.naturallight;

import androidx.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

@SuppressWarnings("FieldCanBeLocal")
public class DataFetcher
{
   private static final Logger logger = LoggerFactory.getLogger (DataFetcher.class);
   private static final int HTTP_NOT_MODIFIED = 304;
   private static final int HTTP_SUCCESS = 200;

   @SuppressWarnings("BooleanMethodIsAlwaysInverted")
   public static boolean okToDownload ()
   {
      if (Settings.allowMobileDataUsage ())
         return true;

      return NetworkTransport.isUnmetered ();
   }

   @Nullable
   private static TimeShapeFile downloadTimeShapeFile (URL url)
   {
      if (!okToDownload ())
         return null;

      HttpsURLConnection connection;
      try
      {
         logger.debug ("getUrlByteContent {}", url.toString ());
         connection = (HttpsURLConnection) url.openConnection();
         connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
         connection.setRequestProperty ("X-API-Key", BuildConfig.SHAPE_API_KEY);
         connection.setReadTimeout (10000);
         int responseCode = connection.getResponseCode ();
         if (responseCode == HTTP_NOT_MODIFIED)
         {
            logger.info ("downloadTimeShapeFile: 304 - Not modified.");
            return null;
         }

         else if (responseCode != HTTP_SUCCESS)
            logger.debug ("downloadTimeShapeFile: {}", responseCode);

         if (responseCode == HTTP_SUCCESS)
         {
            InputStream response = connection.getInputStream ();
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            // we need to know how may bytes were read to write them to the byteBuffer
            int len;
            while ((len = response.read(buffer)) != -1)
               byteBuffer.write(buffer, 0, len);

            String disposition = connection.getHeaderField ("Content-Disposition");
            String filename = getFilename (disposition);
            return new TimeShapeFile (filename, byteBuffer.toByteArray ());
         }
      }
      catch (Exception  ex)
      {
         logger.error ("DataFetcher.getUrlByteContent: ", ex);
      }

      return null;
   }

   public static File getLocalTimeShapeDataFile ()
   {
      File result = new File ("");
      File[] files = MainActivity.getContext ().getFilesDir ().listFiles ();
      if (files != null)
      {
         for (File f : files)
         {
            boolean isTimeShapeFile = TimeShapeFile.isTimeShapeFile (f);
            if (isTimeShapeFile)
               result = TimeShapeFile.newerVersion (result, f);
         }
      }

      if (result.getName ().isEmpty ())
         return null;

      return result;
   }

   public static synchronized void getNewerTimeShapeData (String version)
   {
      if (Settings.getLastDataCheck ().isAfter (ZonedDateTime.now ().minus (Duration.ofDays (14))))
         return;

      try
      {
         String url = String.format ("%s?newer=%s", BuildConfig.SHAPE_DATA_URL, version);
         URL u = new URL (url);
         TimeShapeFile timeShapeFile = downloadTimeShapeFile (u);
         if (timeShapeFile != null)
         {
            logger.info ("Content is {}", timeShapeFile.getFilename ());

            final File outputDir = MainActivity.getContext ().getFilesDir ();
            Path outputFile = Paths.get (outputDir.toString (), timeShapeFile.getFilename ());
            try (OutputStream writer = Files.newOutputStream (outputFile))
            {
               writer.write (timeShapeFile.getContent ());
            }
         }
         Settings.setLastDataCheck (ZonedDateTime.now ());
      }
      catch (MalformedURLException ex)
      {
         logger.error ("Internal error bad URL: ", ex);
      }
      catch (IOException ex)
      {
         logger.error ("Internal error: ", ex);
      }
   }

   /**
    * Minimal parser of the Content-Disposition header. This is not a full
    * implementation of RFC6266. It's just enough to handle what my script is
    * producing.
    * @param contentDisposition A Content-Disposition header.
    * @return The filename portion or null if anything doesn't match.
    */

   public static String getFilename (String contentDisposition)
   {
      if (contentDisposition == null)
         return null;

      final String PATTERN = " *(?<dispositionType>((inline)|(attachment))) *; *((filename)|(filename\\*)) *= *\"(?<name>.*)\"";
      Pattern pattern = Pattern.compile (PATTERN);
      Matcher matcher = pattern.matcher (contentDisposition);
      if (matcher.matches ())
         return matcher.group ("name");

      return null;

   }
}
