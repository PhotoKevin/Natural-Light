package com.blackholeofphotography.astrocalc;

public class Tools
{
   private static boolean mReducedAccuracy;

   static double fraction (double x)
   {
      if (x < 0)
         return x - Math.ceil (x);

      return x - Math.floor (x);
   }

   // SPA 3.2.6
   public static double limit (double l, int max)
   {
      double f = Math.abs (fraction (l / max));
      if (l >= 0.0)
         l = max * f;
      else
         l = max - max*f;
      return l;
   }

   public static double fractionalDegrees (int degrees, int minutes, double seconds)
   {
      return degrees + minutes/60.0 + seconds/3600.0;
   }

   public static void setReducedAccuracy (boolean value)
   {
      mReducedAccuracy = value;
   }

   public static boolean ismReducedAccuracy ()
   {
      return mReducedAccuracy;
   }
}