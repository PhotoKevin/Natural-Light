package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.*;

import org.junit.Assert;
import org.junit.Test;

public class SunTest
{
   private static final double SPA_JD = 2452930.312847222;
   @Test
   public void SunRightAscensionTest ()
   {
      DeltaT.ForceDeltaT (67);
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus47;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus47;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus47;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      MoonEphemeris.MoonMeanLongitudeCoefficients = MoonEphemeris.MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;

      double alpha = Sun.SunGeocentricRightAscension (SPA_JD); // Uses SunApparentLongitude
      Assert.assertEquals (202.22741, alpha, 0.0001);
   }
   @Test
   public void SunDeclinationTest ()
   {
      DeltaT.ForceDeltaT (67);
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus47;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus47;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus47;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      MoonEphemeris.MoonMeanLongitudeCoefficients = MoonEphemeris.MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;
      double delta = Sun.SunGeocentricDeclination (SPA_JD);
      Assert.assertEquals (-9.31434, delta, 0.0002);
   }
   @Test
   public void SunApparentLongitudeTest ()
   {
      DeltaT.ForceDeltaT (67);
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus47;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus47;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus47;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      MoonEphemeris.MoonMeanLongitudeCoefficients = MoonEphemeris.MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;
      double lambda = Sun.SunApparentLongitude (SPA_JD); // Uses SunGeocentricLongitude
      Assert.assertEquals (204.0085519281, lambda, 0.02);
   }

   @Test
   public void SunGeocentricLongitudeTestMeeus ()
   {
      // Meeus 25.a
      DeltaT.ForceDeltaT (0.00); // Meeus ignored deltaT in this example
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus22;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus22;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus22;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      MoonEphemeris.MoonMeanLongitudeCoefficients = MoonEphemeris.MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;

      double jd = 2448908.5;

      double expected = -0.072183436;
      double jc = Julian.JulianCentury (jd);
      Assert.assertEquals (expected, jc, 0.000001);

      // Example 25.a
      double M = Earth.EarthMeanAnomaly (jd);
      Assert.assertEquals (278.99397, M, 0.00001);

      //  Assert failed. Expected:<199.906> Actual:<199.918> - lambda error

      // Example 25.b
      double Theta = Sun.SunGeocentricLongitude (jd); // Uses EarthHeliocentricLongitude
      Assert.assertEquals (199.907372, Theta, 0.000001);
   }


   @Test
   public void SunApparentLongitudeTestMeeus ()
   {
      double jd = 2448908.5;
      double expected = 199.0 + 54.0 / 60 + 21.818 / 3600; // 199 54'21".818

      // Expected:<199.906> Actual:<199.918> - lambda error
      // From Example 25.b
      double lambda = Sun.SunApparentLongitude (jd); // Uses SunGeocentricLongitude, NutationLongitude, SunAberrationCorrection
      Assert.assertEquals (expected, lambda, 0.02);
   }
//
//   @Test
//   public void MeanSunAnomalyTest()
//   {
//      // I don't have  a test, but if Nutation passes, this is ok.
////			double ma = SunMeanAnomaly (SPA_JD);
////			Assert.assertEquals (spadata.lambda, ma, 0.02, L"ma error");
//   }

   @Test
   public void SunGeocentricLongitudeTest ()
   {
      // SPA
      DeltaT.ForceDeltaT (67.0);
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus22;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus22;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus22;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      MoonEphemeris.MoonMeanLongitudeCoefficients = MoonEphemeris.MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;

      double Theta = Sun.SunGeocentricLongitude (SPA_JD); // Uses EarthHeliocentricLongitude
      Assert.assertEquals (204.0182616917, Theta, 0.0001);
   }

   @Test
   public void SunGeocentricLatitudeTestMeeus ()
   {
      // Example 25.b
      double jd = 2448908.5;
      double expected = 0.000179; // Degrees. Meeus also show minutes.
      double capBeta = Sun.SunGeocentricLatitude (jd);
      Assert.assertEquals (expected, capBeta, 0.000001);
   }

   @Test
   public void SunGeocentricLongitudeSPATest ()
   {
      // Meeus 25.b
      DeltaT.ForceDeltaT (0.0);
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus22;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus22;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus22;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      MoonEphemeris.MoonMeanLongitudeCoefficients = MoonEphemeris.MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;

      double jd = 2448908.5;

      double expected = 199.0 + 54.0/60 + 26.45/3600;

      double Theta = Sun.SunGeocentricLongitude (jd); // Uses EarthHeliocentricLongitude
      Assert.assertEquals (expected, Theta, 0.0001);
   }

   @Test
   public void SunGeocentricLatitudeTest ()
   {
      double capBeta = Sun.SunGeocentricLatitude (SPA_JD);
      Assert.assertEquals ( 0.0001011219, capBeta, 0.000001);
   }

   @Test
   public void SunTopocentricPositionTest ()
   {
      // Meeus 25.b
      DeltaT.UnForceDeltaT ();
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus47;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus47;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus47;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      MoonEphemeris.MoonMeanLongitudeCoefficients = MoonEphemeris.MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;

      // 1990 Jan 01 00:00:00.0     18.7471348        - 23.043740       0.983349966
      double jd = Julian.JulianFromYMDHMS (1990, 1, 1, 0, 0, 0);

      double lon = -Tools.fractionalDegrees (83, 47, 5.7);
      double lat = Tools.fractionalDegrees (42, 17, 19.6);

      TopocentricPosition radec = Sun.SunTopocentricPosition (jd, lat, lon, 263);
      double ra = radec.getRa ();
      double dec = radec.getDec ();

      ra /= 15.0;  // MICA uses Hour Angle, not Degrees
      Assert.assertEquals ( 18.7471348, ra, 0.005);
      Assert.assertEquals ( - 23.043740, dec, 0.005);
   }

   @Test
   public void ApparentTopocentricTest ()
   {

      //Computation path of the sun for:
      //Ann Arbor, Michigan
      //23.Feb.2024
      // 10:18 UTC-5   LIVE
      //Solar data for the selected location
      //Dawn:	06:51:56
      //Sunrise:	07:20:00
      //Culmination:	12:48:21
      //Sunset:	18:17:21
      //Dusk:	18:45:25
      //Daylight duration:	10h57m21s
      //Distance [km]:	148,006,092
      //Altitude:	27.57°
      //Azimuth:	137.33°
      //Shadow length [m]:	1.92
      //at an object level [m]:
      //1
      //Geodata for the selected location
      //Height:	255m	Set Lat/Lon
      //Lat:	N 42°16'55.56''	42.28210°
      //Lng:	W 83°44'54.49''	-83.74847°
      //UTM:	17T 273378 4684757
      //TZ: America/Detroit  EST

      double lat = Tools.fractionalDegrees (42, 16, 55.56);
      double lon = -Tools.fractionalDegrees (83, 44, 54.49);


      // double ra, dec;
      double jd = Julian.JulianFromYMDHMS (2024, 2, 23, 10+5, 18, 0);
      TopocentricPosition position = Sun.SunTopocentricPosition (jd, lat, lon, 263);
      Assert.assertEquals (336.36, position.getRa (), 0.01);
      Assert.assertEquals (-9.863, position.getDec (), 0.001);

      // ra -> 336.36
      // dec -> -9.863

      // MICA ra 22.4241099 (hours) dec -9.863589
      // MICA zenith 62.48589 Azimuth 137.29652
      //    90 - 62.48 => 27.52

      // MICA Local Hour Angle  -2.508484
      //    15 * -2.5 => 37.5
   }
}
