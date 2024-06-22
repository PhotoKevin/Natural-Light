package com.blackholeofphotography.astrocalc;

import static com.blackholeofphotography.astrocalc.Moon.MoonTopocentricPosition;
// Ignore Spelling: meeus chapront

public class MoonRise
{
   static double interpolate (double jd0, double jd1, double target, double latitude, double longitude, double elevation)
   {
      TopocentricPosition v0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
      TopocentricPosition v1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);
      double delta = v1.getElevation () - v0.getElevation ();
      double fractional = (target - v0.getElevation ()) / delta;

      return (jd1 - jd0) * fractional + jd0;
   }

   public static RiseTransitSet MoonRise (double jd, double latitude, double longitude, double elevation)
   {
      RiseTransitSet rts = new RiseTransitSet (-1, -1, -1);

      double moon_h0 = -0.125;

      // Move to 0 hour UTC
      jd = Math.floor (jd - 0.5) + 0.5;
      for (int hour = 0; hour < 24; hour++)
      {
         double jd0 = jd + (hour) / 24.0;
         double jd1 = jd + (hour + 1) / 24.0;

         TopocentricPosition tc0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
         TopocentricPosition tc1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);


         int s0 = (tc0.getElevation () + moon_h0 < 0) ? -1 : 1;
         int s1 = (tc1.getElevation () + moon_h0 < 0) ? -1 : 1;
         if (s0 != s1)
         {
            boolean isrise = s0 < 0;
            double jdc = 0;
            for (int i = 0; i < 40; i++)
            {
               tc0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
               tc1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);

               jdc = interpolate (jd0, jd1, moon_h0, latitude, longitude, elevation);
               TopocentricPosition tc = MoonTopocentricPosition (jdc, latitude, longitude, elevation);
               if ((tc.getElevation () + moon_h0) > 0 && (tc0.getElevation () + moon_h0) > 0)
                  jd1 = jdc;
               else
                  jd0 = jdc;

               if (Math.abs (tc.getElevation () + moon_h0) < 0.001)
                  break;
            }

            double hour2 = (jdc - jd) * 24;
            if (isrise)
               rts.rise = hour2;
            else
               rts.set = hour2;
         }
         else if (tc0.getAzimuth () <= 180 && tc1.getAzimuth () >= 180)
         {
            double jdc = 0;
            for (int i = 0; i < 40; i++)
            {
               tc0 = MoonTopocentricPosition (jd0, latitude, longitude, elevation);
               tc1 = MoonTopocentricPosition (jd1, latitude, longitude, elevation);

               jdc = jd0 + (jd1 - jd0) / 2;
               TopocentricPosition tc = MoonTopocentricPosition (jdc, latitude, longitude, elevation);
               if (tc.getAzimuth () >= 180)
                  jd1 = jdc;
               else
                  jd0 = jdc;

               if (Math.abs (tc.getAzimuth () - 180) < 0.001)
                  break;
            }


            double hour2 = (jdc - jd) * 24;
            rts.transit = hour2;
         }
      }

      return rts;
   }
}