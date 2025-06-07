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
         System.out.println (e);
         return 0;
      }
   }

   public static int MonthFromName (String monthStr)
   {
      int month = 0;
      if (monthStr.equals ("Jan"))  month = 0;
      if (monthStr.equals ("Feb"))  month = 1;
      if (monthStr.equals ("Mar"))  month = 2;
      if (monthStr.equals ("Apr"))  month = 3;
      if (monthStr.equals ("May"))  month = 4;
      if (monthStr.equals ("Jun"))  month = 5;
      if (monthStr.equals ("Jul"))  month = 6;
      if (monthStr.equals ("Aug"))  month = 7;
      if (monthStr.equals ("Sep"))  month = 8;
      if (monthStr.equals ("Oct"))  month = 9;
      if (monthStr.equals ("Nov"))  month = 10;
      if (monthStr.equals ("Dec"))  month = 11;

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
