package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.Julian;
import com.blackholeofphotography.astrocalc.Sun;
import com.blackholeofphotography.astrocalc.Moon;

import org.junit.Assert;
import org.junit.Test;

public class EclipseTest
{

   @Test
   public void PositionTest ()
   {
     /*
      Per Wikipedia:
      Maximum eclipse
      Duration	268 s (4 min 28 s)
      Location	Nazas, Durango, Mexico
      Coordinates	25.3°N 104.1°W
      Max. width of band	198 km (123 mi)
      Times (UTC)
      (P1) Partial begin	15:42:07
      (U1) Total begin	16:38:44
      Greatest eclipse	18:18:29
      (U4) Total end	19:55:29
      (P4) Partial end	20:52:14
      */

      double latitude = 25.3;
      double longitude = -104.1;
      double elevation = 115;
      double jd = Julian.JulianFromYMDHMS (2024, 4, 8, 18, 18, 29);

      var sunPosition = Sun.SunTopocentricPosition (jd, latitude, longitude, elevation);
      var moonPosition = Moon.MoonTopocentricPosition (jd, latitude, longitude, elevation);
      Assert.assertEquals (sunPosition.getRa (), moonPosition.getRa (), 0.01);  // 17.908969524947626
      Assert.assertEquals (sunPosition.getDec (), moonPosition.getDec (), 0.01); // 7.5962499348548311
   }
}
