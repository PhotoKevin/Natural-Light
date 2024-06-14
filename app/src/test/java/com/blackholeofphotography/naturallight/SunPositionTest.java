package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.Julian;
import com.blackholeofphotography.astrocalc.Sun;
import com.blackholeofphotography.astrocalc.SunRiseSet;
import com.blackholeofphotography.astrocalc.Tools;
import com.blackholeofphotography.astrocalc.TopocentricPosition;

import org.junit.Assert;
import org.junit.Test;

public class SunPositionTest
{
   @Test
   public void PositionTest ()
   {
      double jd = Julian.JulianFromYMDHMS (2000, 8, 1, 12, 0, 0);
      double homeLongitude = - Tools.fractionalDegrees (83, 47,5.7);
      double homeLatitude = Tools.fractionalDegrees (42, 17, 19.6);
      double homeAltitude = 256;

      double sunRise = Tools.fractionalDegrees (10, 28, 0);
      double transit = Tools.fractionalDegrees (17, 41, 0);
      double sunSet = Tools.fractionalDegrees (0, 55, 0);
      double riseAzimuth = 65;
      double setAzimuth = 296;
      double one_minute = 1.0/60.0; // 0.01667

      double riseSetTransit[] = SunRiseSet.SunRise (jd,homeLatitude, homeLongitude);
      Assert.assertEquals (sunRise, riseSetTransit[0], one_minute);
      Assert.assertEquals (transit, riseSetTransit[1], one_minute);
      Assert.assertEquals (sunSet, riseSetTransit[2], one_minute);

      // 2000 Aug 01 12:00:00.0      8.7981391        + 17.864070       1.014915860
      TopocentricPosition position = Sun.SunTopocentricPosition (jd, homeLatitude, homeLongitude, homeAltitude);
      double alphaPrime = position.getRa ();;
      double deltaPrime = position.getDec ();
//      double hPrime = position[2];
      double e = position.getElevation ();
      double capPhi = position.getAzimuth ();

      Assert.assertEquals (8.7981391 * 15, alphaPrime, 0.001);
      Assert.assertEquals (17.864070 , deltaPrime, 0.001);

      TopocentricPosition mileHigh = Sun.SunTopocentricPosition (jd, homeLatitude, homeLongitude, 5000.0);
      Assert.assertEquals (8.7981391 * 15, mileHigh.getRa (), 0.001);

   }
}
