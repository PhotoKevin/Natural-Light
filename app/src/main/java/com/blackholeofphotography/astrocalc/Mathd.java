package com.blackholeofphotography.astrocalc;

public class Mathd
{
   // Various trig functions that work in degrees

   static double Degrees (double radians)
   {
      return Math.toDegrees (radians);
   }

   static double Radians (double degrees)
   {
      return Math.toRadians (degrees);
   }

   public static double sind (double degrees)
   {
      return Math.sin (Radians (degrees));
   }

   public static double cosd (double degrees)
   {
      return Math.cos (Radians (degrees));
   }

   static double tand (double degrees)
   {
      return Math.tan (Radians (degrees));
   }

   public static double asind (double X)
   {
      return Degrees (Math.asin (X));
   }

   public static double acosd (double X)
   {
      return Degrees (Math.acos (X));
   }

   static double atan2d (double Y, double X)
   {
      return Degrees (Math.atan2 (Y, X));
   }

   public static double polynomial (double argument, double[] coefficients)
   {
      double result = 0.0;
      for (int i = coefficients.length-1; i >= 0; i--)
      {
         result *= argument;
         result += coefficients[i];
      }

      return result;
   }
}