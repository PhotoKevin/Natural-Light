package com.blackholeofphotography.naturallight;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

/**
 * A wrapper for SharedPreferences that deals with the fact that
 * many, or most, preferences aren't really strings.
 */
public class SensibleSharedPreferences
{
   private final static String LOG_TAG = "SensibleSharedPreferences";
   private final SharedPreferences mSharedPreferences;
   public SensibleSharedPreferences (Context context)
   {
      mSharedPreferences = PreferenceManager.getDefaultSharedPreferences (context);
   }

   public boolean getBoolean (String key, boolean  defaultValue)
   {
      try
      {
         String s = mSharedPreferences.getString (key, String.valueOf (defaultValue));
         return Boolean.parseBoolean (s);
      }
      catch (Exception ex)
      {
         try
         {
            return mSharedPreferences.getBoolean (key, defaultValue);
         }
         catch (Exception e)
         {
            Log.d (LOG_TAG, e.toString ());
         }
         return defaultValue;
      }
   }
   /**
    * Get a value from the preferences as an Integer
    * @param key Preference Key
    * @param defaultValue Default value
    * @return preference as an integer
    */

   public int getInteger (String key, int defaultValue)
   {
      try
      {
         String s = mSharedPreferences.getString (key, String.valueOf (defaultValue));
         return Integer.parseInt (s);
      }
      catch (NumberFormatException ex)
      {
         return defaultValue;
      }
   }
   /**
    * Get a value from the preferences as a float
    * @param key Preference Key
    * @param defaultValue Default value
    * @return preference as an integer
    */

   public float getFloat (String key, float defaultValue)
   {
      try
      {
         String s = mSharedPreferences.getString (key, String.valueOf (defaultValue));
         return Float.parseFloat (s);
      }
      catch (NumberFormatException ex)
      {
         return defaultValue;
      }
   }


   public String getString (String key, String  defaultValue)
   {
      return mSharedPreferences.getString (key, String.valueOf (defaultValue));
   }

   public ZonedDateTime getZonedDateTime (String key, ZonedDateTime defaultValue)
   {
      try
      {
         String s = mSharedPreferences.getString (key, String.valueOf (defaultValue));
         return ZonedDateTime.parse (s);
      }
      catch (DateTimeParseException ex)
      {
         return defaultValue;
      }
   }

   SharedPreferences.Editor edit ()
   {
      return mSharedPreferences.edit ();
   }


}
