package com.blackholeofphotography.burnyourplan;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.Nullable;

import com.blackholeofphotography.bhtools.DBTools;
import com.blackholeofphotography.burnyourplan.database.DBDataStatus;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

@SuppressWarnings("FieldCanBeLocal")
public class DataFetcher
{
   private static final Logger logger = LoggerFactory.getLogger (DataFetcher.class);
   private static final int HTTP_NOT_MODIFIED = 304;
   private static final int HTTP_INTERNAL_SERVER_ERROR = 501;
   private static final int HTTP_SUCCESS = 200;

   @SuppressWarnings("BooleanMethodIsAlwaysInverted")
   public static boolean okToDownloadJson ()
   {
      if (Settings.allowMobileDataUsageJson ())
         return true;

      return NetworkTransport.isUnmetered ();
   }

   public static boolean okToDownloadImages ()
   {
      if (Settings.allowMobileDataUsageImages ())
         return true;

      return NetworkTransport.isUnmetered ();
   }


   public static JSONObject fetchArt (int year)
   {
      return getJSON ("art", year);
   }

   public static JSONObject fetchCamp (int year)
   {
      return getJSON ("camp", year);
   }

   public static JSONObject fetchEvent (int year)
   {
      return getJSON ("event", year);
   }

   /**
    * Create a JSON object for an exception status
    * @param code Status code
    * @param text Status text
    * @return The new JSONObject
    */
   private static JSONObject exceptionStatus (int code, String text)
   {
      JSONObject jo = new JSONObject ();
      try
      {
         jo.put ("status", code);
         jo.put ("statusmessage", text);
      }
      catch (JSONException e)
      {
         logger.error ("DataFetcher.exceptionStatus: ", e);
      }
      return jo;
   }


   private static String createUrl (String dataType, int year, String since)
   {
      @SuppressLint("DefaultLocale")
      String query;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
         query = String.format (Locale.getDefault (), "year=%d&modified-since=%s", year, URLEncoder.encode (since, StandardCharsets.UTF_8));
      else
         query = String.format (Locale.getDefault (), "year=%d&modified-since=%s", year, since);
      return String.format ("%s/%s?%s", BuildConfig.BYP_BASE_URL, dataType, query);
   }

   @Nullable
   public static JSONObject getJSON (String dataType, int year)
   {
      String since = "2000-01-01T00:00:00";
      final DBDataStatus dataStatus = DBDataStatus.retrieveByDataTypeYear (DBTools.getConnection (), dataType, year);
      if (dataStatus != null)
         since = dataStatus.getTimeStamp ();

      String url = createUrl (dataType,  year, since);
      return getJSON (url);
   }

   /**
    * Fetch JSON data from a URL
    * @param urlString The URL to fetch
    * @return a JSONObject with the content of the URL or an object with an exception status
    */
   @Nullable
   public static JSONObject getJSON (String urlString)
   {
      if (!okToDownloadJson ())
         return null;

      HttpsURLConnection connection;
      try
      {
         logger.info ("Fetching {}", urlString);
         StringBuilder result = new StringBuilder();
         connection = (HttpsURLConnection) new URL (urlString).openConnection();
         connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
         connection.setRequestProperty ("X-API-Key", BuildConfig.KEY_BYP_API);
         connection.setReadTimeout (10000);
         int responseCode = connection.getResponseCode ();
         if (responseCode == HTTP_NOT_MODIFIED)
         {
            logger.info ("304 - Not modified");
            return null;
         }
         else if (responseCode != HTTP_SUCCESS)
            return exceptionStatus (responseCode, connection.getResponseMessage ());
         else
         {
            InputStream response = connection.getInputStream ();
            try (BufferedReader reader = new BufferedReader (new InputStreamReader (response)))
            {
               String line;
               while ((line = reader.readLine ()) != null)
                  result.append (line);
            }
            catch (IOException ex)
            {
               logger.error ("DataFetcher.getJSON IOException: ", ex);
            }
            JSONObject jo = new JSONObject (result.toString ());
            jo.put ("status", HTTP_SUCCESS);
            return jo;
         }
      }
      catch (IOException | JSONException ex)
      {
         logger.error ("DataFetcher.getJSON", ex);
         return exceptionStatus (HTTP_INTERNAL_SERVER_ERROR, ex.getMessage ());
      }
      catch (Exception ex)
      {
         logger.error ("Major exception DataFetcher.getJSON", ex);
         throw ex;
      }
   }

   @Nullable
   public static byte[] getThumbnailByteContent (String thumbUrl)
   {
      if (!okToDownloadImages ())
         return null;

      String bypUrl = String.format ("https://byp.blackholeofphotography.com/api/thumbnail?url=%s", thumbUrl);
      HttpsURLConnection connection;
      try
      {
         URL url = new URL (bypUrl);
         logger.debug ("getThumbnailByteContent: {}", url);
         connection = (HttpsURLConnection) url.openConnection();
         connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
         connection.setRequestProperty ("X-API-Key", BuildConfig.KEY_BYP_API);
         connection.setReadTimeout (10000);
         int responseCode = connection.getResponseCode ();
         logger.debug ("getThumbnailByteContent Response: {}", responseCode);

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

            return byteBuffer.toByteArray ();
         }
      }
      catch (IOException  ex)
      {
         logger.error ("DataFetcher.getThumbnailByteContent: ", ex);
      }

      return null;
   }

   @Nullable
   public static String getFileStringContent (int year, String filename, ZonedDateTime modifiedSince)
   {
      try
      {
         String since = BRCWhen.formatSinceTime (modifiedSince);
         @SuppressLint("DefaultLocale")
         String bypUrl = String.format ("https://byp.blackholeofphotography.com/api/file?filename=%s&year=%d&modified-since=%s", filename, year, since);
         final String content = getUrlStringContent (new URL (bypUrl));
         if (content != null)
            DBDataStatus.createOrUpdate (DBTools.getConnection (), filename, year, ZonedDateTime.now ());
         return content;
      }
      catch (MalformedURLException e)
      {
         logger.error ("MalformedURLException fetching {}: ", filename, e);
      }
      return null;
   }

   @Nullable
   private static byte[] getUrlByteContent (URL url)
   {
      if (!okToDownloadJson ())
         return null;

      HttpsURLConnection connection;
      try
      {
         logger.debug ("getUrlByteContent {}", url.toString ());
         connection = (HttpsURLConnection) url.openConnection();
         connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
         connection.setRequestProperty ("X-API-Key", BuildConfig.KEY_BYP_API);
         connection.setReadTimeout (10000);
         int responseCode = connection.getResponseCode ();
         if (responseCode == HTTP_NOT_MODIFIED)
         {
            logger.info ("getUrlByteContent: 304 - Not modified");
            return null;
         }

         else if (responseCode != HTTP_SUCCESS)
            logger.debug ("getUrlByteContent: {}", responseCode);

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

            return byteBuffer.toByteArray ();
         }
      }
      catch (Exception  ex)
      {
         logger.error ("DataFetcher.getUrlByteContent: ", ex);
      }

      return null;
   }


   @Nullable
   private static String  getUrlStringContent (URL url)
   {

      final byte[] urlByteContent = getUrlByteContent (url);
      if (urlByteContent != null)
         return new String (urlByteContent, StandardCharsets.UTF_8);
      return null;
   }
}
