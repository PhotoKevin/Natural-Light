package com.blackholeofphotography.astrocalc;

public class Normalization
{
   // A.2.11
   public static double fix180degrees (double degrees)
   {
      degrees /= 360.0;
      degrees = 360.0 * (degrees - Math.floor (degrees));

      if (degrees <= -180.0)
         degrees += 180.0;
      else
         if (degrees >= 180.0)
            degrees -= 360.0;

      return degrees;
   }


   /// <summary>
   /// Normalize an angle into the range of 0..360
   /// </summary>
   /// <param name="angle">Angle in degrees</param>
   /// <returns>The normalized angle</returns>
   /// <remarks>
   /// </remarks>

   public static double NormalizeZeroTo360Degrees (double angle)
   {
      while (angle < 0)
         angle += 360;

      while (angle >= 360.0)
         angle -= 360.0;

      return angle;
   }

   /// <summary>
   /// Normalize an angle into the range of 0..360
   /// </summary>
   /// <param name="angle">Angle in degrees</param>
   /// <returns>The normalized angle</returns>
   /// <remarks>
   /// SPA 3.2.6
   /// Limit L to the range from 0 to 360. That can be accomplished by dividing L by
   /// 360 and recording the decimal fraction of the division as F. If L is positive, then
   /// the limited L = 360 * F. If L is negative, then the limited L = 360 - 360 * F.
   /// </remarks>

   public static double NormalizeZeroTo360DegreesX (double angle)
   {
      double frac = Tools.fraction (angle / 360.0);
      if (angle >= 0.0)
         angle = 360.0 * frac;
      else
         angle = 360.0 - (360.0 * frac);

      return angle;
   }


   /// <summary>
   /// Normalize the value into the range zero .. one,
   /// but only if the absolute value was > 2.0
   /// </summary>
   /// <param name="value"></param>
   /// <returns></returns>
   /// <remarks>
   /// This is from SPA A.2.10 as a note. Why only normalize if > 2.0?
   /// </remarks>
   public static double NormalizeZeroToOne (double value)
   {
      if (Math.abs (value) > 2.0)
      {
         double limited = value - Math.floor (value);
         if (limited < 0)
            limited += 1.0;

         return limited;
      }
      return value;
   }
}