package com.blackholeofphotography.astrocalc;

public class Sidereal
{

   //Ignore Spelling: Meeus

   /// <summary>
   /// The Mean Sidereal time in Degrees at Greenwich
   /// </summary>
   /// <param name="jd">Julian date of interest</param>
   /// <returns>Sidereal time in degrees</returns>
   /// <remarks>
   /// SPA 3.8.1
   /// </remarks>

   static double MeanSiderealDegrees (double jd)
   {
      double jce = Julian.JulianEphemerisCentury (jd);
      double jce2 = jce * jce;
      double jce3 = jce * jce * jce;

      double v0 = 280.46061837 + 360.98564736629 * (jd - 2451545) + (0.000387933 * jce2) - (jce3 / 38710000);

      v0 = Normalization.NormalizeZeroTo360Degrees (v0);
      return v0;
   }


   /// <summary>
   /// The Apparent Sidereal time in Degrees at Greenwich
   /// </summary>
   /// <param name="jd">Julian date of interest</param>
   /// <returns>Sidereal time in degrees</returns>
   /// <remarks>
   /// SPA 3.8.3
   /// </remarks>
   public static double ApparentSiderealDegrees (double jd)
   {
      double v0 = MeanSiderealDegrees (jd);

      var  deltaPsiEpsilon = Nutation.NutationLongitude (jd);
      double deltaPsi = deltaPsiEpsilon.getDeltaPsi ();
      double epsilon = Earth.EclipticTrueObliquityDegrees (jd);

      double v = v0 + deltaPsi * Mathd.cosd (epsilon);

      return v;
   }

}
