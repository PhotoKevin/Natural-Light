package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.Julian;

import org.junit.Assert;

public class MicaTools
{
   public static int extractNumber (String s, int beginIndex, int length)
   {
      try
      {
         String s2 = s.substring (beginIndex, beginIndex+length).trim ();
         if (s2.isBlank ())
            return -1;
         return Integer.parseInt (s2);
      }
      catch (Exception e)
      {
         Assert.fail (e.toString ());
         return 0;
      }
   }

   public static int MonthFromName (String monthStr)
   {
      int month = 0;
      if (monthStr.equals ("Jan"))  month = 0;
      else if (monthStr.equals ("Feb"))  month = 1;
      else if (monthStr.equals ("Mar"))  month = 2;
      else if (monthStr.equals ("Apr"))  month = 3;
      else if (monthStr.equals ("May"))  month = 4;
      else if (monthStr.equals ("Jun"))  month = 5;
      else if (monthStr.equals ("Jul"))  month = 6;
      else if (monthStr.equals ("Aug"))  month = 7;
      else if (monthStr.equals ("Sep"))  month = 8;
      else if (monthStr.equals ("Oct"))  month = 9;
      else if (monthStr.equals ("Nov"))  month = 10;
      else if (monthStr.equals ("Dec"))  month = 11;

      return month +1;
   }

   public static String extract (String s, int beginIndex, int length)
   {
      return s.substring (beginIndex, beginIndex+length);
   }

   public static double parseDate (String line)
   {
      int year = extractNumber (line, 0, 4);
      int month = MonthFromName (extract (line, 5, 3));
      int day = extractNumber (line, 9, 2);
      return Julian.JulianFromYMD (year, month, day);
   }
}
