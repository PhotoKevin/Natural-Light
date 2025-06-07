package com.blackholeofphotography.astrocalc;

import static com.blackholeofphotography.astrocalc.Moon.MoonTopocentricPosition;
import static java.lang.Math.abs;
// Ignore Spelling: meeus chapront

public class MoonRise
{
   private final static double STANDARD_ALTITUDE = -0.125; // Altitude of moon at rise or set.


   static double interpolate_elevation (double jd0, double jd1, double target, double latitude, double longitude, double elevation)
   {
      var v0 = Moon.MoonTopocentricPosition (jd0, latitude, longitude, elevation);
      var v1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);
      double delta = v1.getElevation () - v0.getElevation ();
      double fractional = (target - v0.getElevation ()) / delta;

      return (jd1 - jd0) * fractional + jd0;
   }

   static double interpolate_azimuth (double jd0, double jd1, double target, double latitude, double longitude, double elevation)
   {
      var v0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
      var v1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);
      double delta = v1.getAzimuth () - v0.getAzimuth ();
      double fractional = (target - v0.getAzimuth ()) / delta;

      return (jd1 - jd0) * fractional + jd0;
   }



   static double successive_interpolation_elevation (double jd0, double jd1, double target, double latitude, double longitude, double elevation)
   {
      double jdc = 0;

      TopocentricPosition tc0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
      TopocentricPosition tc1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);
      for (int i=0; i<40; i++)
      {
         jdc = interpolate_elevation (jd0, jd1, target, latitude, longitude, elevation);

         double delta = jd1 - jd0;
         if (delta < 0.5/(24 * 60.0))
            break;

         double half0 = abs ((jdc - jd0)/2);
         double half1 = abs ((jd1 - jdc)/2);
         double half_window = (half0 < half1) ? half0 : half1;
         // Move jd0 and jd1 to halfway between jdc and the closest original value
         jd0 = jdc - half_window;
         jd1 = jdc + half_window;

         tc0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
         tc1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);
      }


      return jdc;
   }


   static double successive_interpolation_azimuth (double jd0, double jd1, double target, double latitude, double longitude, double elevation)
   {
      double jdc = 0;

      TopocentricPosition tc0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
      TopocentricPosition tc1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);
      for (int i=0; i<40; i++)
      {
         jdc = interpolate_azimuth (jd0, jd1, target, latitude, longitude, elevation);
         //char s[200];
         //snprintf (s, sizeof s, "%f, %f -> %f\n", jd0, jd1, jdc);
         TopocentricPosition tcc = MoonTopocentricPosition (jdc, latitude, longitude, elevation);

         double delta = jd1 - jd0;
         if (delta < 0.5/(24 * 60.0))
            break;

         double half0 = abs ((jdc - jd0)/2);
         double half1 = abs ((jd1 - jdc)/2);
         double half_window = (half0 < half1) ? half0 : half1;
         // Move jd0 and jd1 to halfway between jdc and the closest original value
         jd0 = jdc - half_window;
         jd1 = jdc + half_window;

         tc0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
         tc1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);
      }


      return jdc;
   }

   public static RiseTransitSet moonRise (double jd, double latitude, double longitude, double elevation)
   {
   double Rise = -1;
   double Transit = -1;
   double Set = -1;

   // Move to 0 hour UTC
   jd = Math.floor (jd-0.5) + 0.5;
   for (int hour=0; hour<24; hour++)
   {
      double jd0 = jd + (hour)/24.0;
      double jd1 = jd + (hour+1)/24.0;

      TopocentricPosition tc0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
      TopocentricPosition tc1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);

      int sign0 = (tc0.getElevation () < STANDARD_ALTITUDE) ? -1 : 1;
      int sign1 = (tc1.getElevation () < STANDARD_ALTITUDE) ? -1 : 1;

      // If the signs are different, the moon crossed the horizon.
      if (sign0 != sign1)
      {
         double jdc = 0;
         jdc = successive_interpolation_elevation (jd0, jd1, STANDARD_ALTITUDE, latitude, longitude, elevation);

         double hour2 = (jdc-jd) * 24;

         if (sign0 < 0)
            Rise = hour2;
         else
            Set  = hour2;
      }

      if (tc0.getAzimuth () <= 180 && tc1.getAzimuth () >= 180 && Transit == -1)
      {

         double jdt = successive_interpolation_azimuth (jd0, jd1, 180, latitude, longitude, elevation);
         double hour2 = (jdt-jd) * 24;
         Transit = hour2;
      }
   }

   return new RiseTransitSet (Rise, Transit, Set);
}

}