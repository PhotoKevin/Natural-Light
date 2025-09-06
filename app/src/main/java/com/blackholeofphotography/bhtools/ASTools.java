/*
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License. To view a
 * copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.blackholeofphotography.bhtools;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ASTools
{
   private static final String LOG_TAG = "ASTools";

   @SuppressWarnings("unused")
   public static boolean isEmulator ()
   {
      return (Build.BRAND.startsWith ("generic") && Build.DEVICE.startsWith ("generic"))
              || Build.FINGERPRINT.startsWith ("generic")
              || Build.FINGERPRINT.startsWith ("unknown")
              || Build.HARDWARE.contains ("goldfish")
              || Build.HARDWARE.contains ("ranchu")
              || Build.MODEL.contains ("google_sdk")
              || Build.MODEL.contains ("Emulator")
              || Build.MODEL.contains ("Android SDK built for x86")
              || Build.MANUFACTURER.contains ("Genymotion")
              || Build.PRODUCT.contains ("sdk_google")
              || Build.PRODUCT.contains ("google_sdk")
              || Build.PRODUCT.contains ("sdk")
              || Build.PRODUCT.contains ("sdk_x86")
              || Build.PRODUCT.contains ("vbox86p")
              || Build.PRODUCT.contains ("emulator")
              || Build.PRODUCT.contains ("simulator");
   }

   @SuppressWarnings("unused")
   public static void sleep (int milliseconds)
   {
      try
      {
         Thread.sleep (milliseconds);
      }
      catch (InterruptedException ignored)
      {
      }
   }

   public static String formatTime (ZonedDateTime zonedDateTime)
   {
      return zonedDateTime.format (DateTimeFormatter.ofPattern ("HH:mm"));
   }

   public static String formatDateTime (ZonedDateTime zonedDateTime)
   {
      return zonedDateTime.format (DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm"));
   }

//
//   /**
//    * Return the contents of the resource as a string.
//    * @param id - Resource ID
//    * @return String with the contents
//    * @throws IOException - If the json file can't be opened
//    */
//   public static String getResourceContents (int id) throws IOException
//   {
//      InputStream is = MainActivity.getContext().getResources().openRawResource (id);
//      return getStreamContents (is);
//   }


   /**
    * Read the contents of the stream into a string
    *
    * @param stream - Stream to read
    * @return - String with the contents
    * @throws IOException - If the json file can't be opened
    */
   public static String getStreamContents (InputStream stream) throws IOException
   {
      StringBuilder contents = new StringBuilder ();
      try
      {
         BufferedReader in = new BufferedReader (new InputStreamReader (stream));
         while (in.ready ())
         {
            String line = in.readLine ();
            if (line != null)
               contents.append (line);

            contents.append ("\n");
         }
      }
      catch (IOException ex)
      {
         Log.e (LOG_TAG, "getResourceContents", ex);
         throw ex;
      }
      return contents.toString ();
   }



   public static String getResourceContents (Context context, String resourceName)
   {
      AssetManager assetManager = context.getAssets ();
      try (final InputStream is = assetManager.open (resourceName))
      {

         StringBuilder sb = new StringBuilder ();
         try
         {
            BufferedReader in = new BufferedReader (new InputStreamReader (is));
            while (in.ready ())
            {
               String s = in.readLine ();
               sb.append (s);
            }
         }
         catch (Exception ex)
         {
            Log.e (LOG_TAG, "", ex);
         }
         return sb.toString ();
      }
      catch (IOException exception)
      {
         throw new RuntimeException (exception);
      }
   }

}
