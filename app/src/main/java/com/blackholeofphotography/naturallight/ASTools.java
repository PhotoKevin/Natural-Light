/*
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License. To view a
 * copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.blackholeofphotography.naturallight;

import android.os.Build;

import org.osmdroid.api.IGeoPoint;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ASTools
{
   public static boolean isEmulator()
   {
      return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
              || Build.FINGERPRINT.startsWith("generic")
              || Build.FINGERPRINT.startsWith("unknown")
              || Build.HARDWARE.contains("goldfish")
              || Build.HARDWARE.contains("ranchu")
              || Build.MODEL.contains("google_sdk")
              || Build.MODEL.contains("Emulator")
              || Build.MODEL.contains("Android SDK built for x86")
              || Build.MANUFACTURER.contains("Genymotion")
              || Build.PRODUCT.contains("sdk_google")
              || Build.PRODUCT.contains("google_sdk")
              || Build.PRODUCT.contains("sdk")
              || Build.PRODUCT.contains("sdk_x86")
              || Build.PRODUCT.contains("vbox86p")
              || Build.PRODUCT.contains("emulator")
              || Build.PRODUCT.contains("simulator");
   }

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

   public static String formatGeoPoint (double latLon)
   {
      return String.format (Locale.getDefault (), "%.4f", latLon);
   }
   public static String formatGeoPoint (IGeoPoint pt)
   {
      Locale l = Locale.getDefault ();
      DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(l);
      DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
      char groupingChar = symbols.getGroupingSeparator();
      final char listSeparator = (groupingChar == ',')  ? ';' : ',';

      return String.format (l, "%.4f%c %.4f", pt.getLatitude (), listSeparator, pt.getLongitude ());
   }
}
