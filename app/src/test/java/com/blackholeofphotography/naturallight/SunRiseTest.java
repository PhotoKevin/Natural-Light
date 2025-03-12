package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.DeltaT;
import com.blackholeofphotography.astrocalc.EarthEphemeris;
import com.blackholeofphotography.astrocalc.Mathd;
import com.blackholeofphotography.astrocalc.MoonEphemeris;
import com.blackholeofphotography.astrocalc.Normalization;
import com.blackholeofphotography.astrocalc.RiseTransitSet;
import com.blackholeofphotography.astrocalc.Sidereal;
import com.blackholeofphotography.astrocalc.Sun;
import com.blackholeofphotography.astrocalc.SunRiseSet;
import com.blackholeofphotography.astrocalc.Tools;

import org.junit.Assert;
import org.junit.Test;

public class SunRiseTest
{
   @Test

   public void SunRiseTestSPADetailed ()
   {
      DeltaT.ForceDeltaT (67.0);
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus22;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus22;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus22;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      //MoonEphemeris.MoonMeanLongitudeCoefficients = MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;


      double jd = 2452930.3128472222;
      double latitude = 39.742476;
      double longitude = -105.1786;

      // Move to 0 hour UTC
      jd = Math.floor (jd-0.5) + 0.5;
      Assert.assertEquals (2452929.5, jd, 0.01);


      // A.2.1
      double v = Sidereal.ApparentSiderealDegrees (jd);

      // A.2.2
      double[] alpha = new double[3];
      alpha[0] = Sun.SunGeocentricRightAscension (jd-1.0);
      alpha[1] = Sun.SunGeocentricRightAscension (jd);
      alpha[2] = Sun.SunGeocentricRightAscension (jd+1.0);

      Assert.assertEquals (200.53590258903108, alpha[0], 0.002);
      Assert.assertEquals (201.46755783685305, alpha[1], 0.002);
      Assert.assertEquals (202.40170355547738, alpha[2], 0.002);


      double[] delta = new double[3];
      delta[0] = Sun.SunGeocentricDeclination (jd-1.0);
      delta[1] = Sun.SunGeocentricDeclination (jd);
      delta[2] = Sun.SunGeocentricDeclination (jd+1.0);

      Assert.assertEquals (-8.648145125065572, delta[0], 0.02);
      Assert.assertEquals (-9.016328513305018, delta[1], 0.02);
      Assert.assertEquals (-9.382403452582901, delta[2], 0.02);

      // A.2.3
      // Calculate the approximate sun transit time, m0 , in fraction of day
      double sigma = longitude; // Longitude of observer in degrees
      double m0 = (alpha[1] - sigma - v) / 360.0;

      Assert.assertEquals (0.782112340255676, m0, 0.0001);

      // A.2.4
      double hprime0 = -0.8333;
      double phi = latitude; // Latitude of observer in degrees
      double H0 = Mathd.sind (hprime0) - Mathd.sind (phi) * Mathd.sind (delta[1]);
      H0 /= Mathd.cosd (phi) * Mathd.cosd (delta[1]);
      H0 = Mathd.acosd (H0);

      H0 = Tools.limit (H0, 180);
      Assert.assertEquals (83.524252814781832, H0, 0.02);

      // A.2.5
      double m1 = m0 - H0/360.0;

      // A.2.6
      double m2 = m0 + H0/360.0;

      // A.2.7
      m0 = Tools.limit (m0, 1);
      m1 = Tools.limit (m1, 1);
      m2 = Tools.limit (m2, 1);

      // mRts
      Assert.assertEquals (0.782112340255676, m0, 0.0001);
      Assert.assertEquals (0.550100526881282, m1, 0.0001);
      Assert.assertEquals (0.014124153630070024, m2, 0.0001);


      Assert.assertEquals (25.085715344809657, v, 0.001);
      // A.2.8
      double v0 = v + 360.985647 * m0;
      double v1 = v + 360.985647 * m1;
      double v2 = v + 360.985647 * m2;

      // nuRts
      Assert.assertEquals (307.417044518689, v0, 0.002);
      Assert.assertEquals (223.6641099560901, v1, 0.002);
      Assert.assertEquals (30.184332081287884, v2, 0.002);


//      time_t date = JDtoDate (jd);

      // A.2.9
      //int year = localtime (&date)->tm_year + 1900;
      double deltaT = 67;
      double n0 = m0 + deltaT/86400;
      double n1 = m1 + deltaT/86400;
      double n2 = m2 + deltaT/86400;

      Assert.assertEquals (0.78288780321863893, n0, 0.02);
      Assert.assertEquals (0.55087598984424491, n1, 0.02);
      Assert.assertEquals (0.014899616593032987, n2, 0.02);

      // A.2.10
      double a = alpha[1] - alpha[0];
      double aprime = delta[1] - delta[0];

      double b = alpha[2] - alpha[1];
      double bprime = delta[2] - delta[1];

      double c = b - a;
      double cprime = bprime - aprime;

      double alphaprime0 = alpha[1] + n0 * (a + b + c*n0)/2.0;
      double alphaprime1 = alpha[1] + n1 * (a + b + c*n1)/2.0;
      double alphaprime2 = alpha[1] + n2 * (a + b + c*n2)/2.0;

      Assert.assertEquals (202.19867746813972, alphaprime0, 0.02);
      Assert.assertEquals (201.9818481985341, alphaprime1, 0.02);
      Assert.assertEquals (201.48145797281302, alphaprime2, 0.02);


      double deltaprime0 = delta[1] + n0 * (aprime + bprime + cprime*n0)/2.0;
      double deltaprime1 = delta[1] + n1 * (aprime + bprime + cprime*n1)/2.0;
      double deltaprime2 = delta[1] + n2 * (aprime + bprime + cprime*n2)/2.0;
      Assert.assertEquals (-9.303103309598976, deltaprime0, 0.02);
      Assert.assertEquals (-9.218251235237926, deltaprime1, 0.02);
      Assert.assertEquals (-9.021798363048811, deltaprime2, 0.02);


      // A2.2.11
      double Hprime0 = v0 + sigma - alphaprime0;
      double Hprime1 = v1 + sigma - alphaprime1;
      double Hprime2 = v2 + sigma - alphaprime2;

      Hprime0 = Normalization.fix180degrees (Hprime0);
      Hprime1 = Normalization.fix180degrees (Hprime1);
      Hprime2 = Normalization.fix180degrees (Hprime2);

      Assert.assertEquals (0.03976705054927265, Hprime0, 0.02);
      Assert.assertEquals (-83.49633824244398, Hprime1, 0.02); // hPrime[(int)TERM5.SUN_RISE];
      Assert.assertEquals (83.52427410847486, Hprime2, 0.02); // hPrime[(int)TERM5.SUN_SET];


      // A2.2.12 - RtsSunAltitude
      double h0 = Mathd.asind (Mathd.sind(phi) * Mathd.sind (deltaprime0) + Mathd.cosd (phi) * Mathd.cosd (deltaprime0) * Mathd.cosd (Hprime0));
      double h1 = Mathd.asind (Mathd.sind(phi) * Mathd.sind (deltaprime1) + Mathd.cosd (phi) * Mathd.cosd (deltaprime1) * Mathd.cosd (Hprime1));
      double h2 = Mathd.asind (Mathd.sind(phi) * Mathd.sind (deltaprime2) + Mathd.cosd (phi) * Mathd.cosd (deltaprime2) * Mathd.cosd (Hprime2));

      // hRts
      Assert.assertEquals (40.954406824491464, h0, 0.02);
      Assert.assertEquals (-0.9425723958174944, h1, 0.02);
      Assert.assertEquals (-0.8369146510569684, h2, 0.02);


      // A2.2.13
      double T = m0 - Hprime0 / 360;

      // A2.2.15
      double R = m1 + ((h1 - hprime0) / (360 * Mathd.cosd (deltaprime1) * Mathd.cosd (phi) * Mathd.sind (Hprime1)));
      Assert.assertEquals (0.55050277538685921, R, 0.001);

      double S = m2 + ((h2 - hprime0) / (360 * Mathd.cosd (deltaprime2) * Mathd.cosd (phi) * Mathd.sind (Hprime2)));


      double rise = R * 24.0;
      double riseExpected = 13 + 12/60.0 + 43.46/3600;
      Assert.assertEquals (riseExpected, rise, 0.001);


      double transit = T * 24.0;
      double transitExpected = 18 + 46/60.0 + 4.97/3600;
      Assert.assertEquals (transitExpected, transit, 0.001);

      double set = S * 24.0;
      double setExpected = 0 + 20/60.0 + 19.19/3600;
      Assert.assertEquals (setExpected, set, 0.001);
   }

   @Test
   public void SunRiseTestSPA ()
   {
      DeltaT.ForceDeltaT (67.0);
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus22;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus22;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus22;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      //MoonEphemeris.MoonMeanLongitudeCoefficients = MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;


      double jd = 2452930.312847;
      double latitude = 39.742476;
      double longitude = -105.1786;

      final RiseTransitSet results = SunRiseSet.SunRise (jd, latitude, longitude);



      double rise = results.getRise ();
      double riseExpected = 13 + 12/60.0 + 43.46/3600;
      Assert.assertEquals (riseExpected, rise, 0.001);


      double transit = results.getTransit ();
      double transitExpected = 18 + 46/60.0 + 4.97/3600;
      Assert.assertEquals (transitExpected, transit, 0.001);

      double set = results.getSet ();
      double setExpected = 0 + 20/60.0 + 19.19/3600;
      Assert.assertEquals (setExpected, set, 0.001);

   }
}

