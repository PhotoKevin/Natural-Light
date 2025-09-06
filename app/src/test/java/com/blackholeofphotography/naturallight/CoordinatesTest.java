package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.Coordinates;
import com.blackholeofphotography.astrocalc.GeocentricPosition;
import com.blackholeofphotography.astrocalc.Julian;
import com.blackholeofphotography.astrocalc.Sidereal;
import com.blackholeofphotography.astrocalc.Tools;

import org.junit.Assert;
import org.junit.Test;

public class CoordinatesTest
{
   @Test
   public void Geocentric2TopocentricTest ()
   {
      // Meeus 13.b
      double lon = -Tools.fractionalDegrees (77, 3, 56);
      // double lat = Tools.fractionalDegrees (38, 55, 17);

      double jd = Julian.JulianFromYMDHMS (1987, 4, 10, 19, 21, 0);
      GeocentricPosition geocentric = new GeocentricPosition (Tools.fractionalDegrees (23, 9, 16.641) * 15, -Tools.fractionalDegrees (6, 43, 11.61));

      double nu = Sidereal.ApparentSiderealDegrees (jd);
      double expectedNu = Tools.fractionalDegrees (8, 34, 56.853) * 15;
      Assert.assertEquals (expectedNu, nu, 0.00001);

      double H = Coordinates.ObserverLocalHourAngleDegrees (jd, lon, geocentric.getRa ());
      double expectedH = 64.352133;
      Assert.assertEquals (expectedH, H, 0.01);
   }
}
