package com.blackholeofphotography.naturallight;


import com.blackholeofphotography.astrocalc.*;

import org.junit.Assert;
import org.junit.Test;

public class SolarPositionAlgorithmTest
{
   private static final double SPA_JD = 2452930.312847222;
   private static final double SPA_LATITUDE = 39.742476;
   private static final double SPA_LONGITUDE = -105.1786;
   private static final double SPA_ELEVATION = 1830.14;
   private static final double SPA_SUN_DECLINATION = -9.31434; // aka delta
   private static final double SPA_SUN_RIGHT_ASCENSION = 202.22741; // aka alpha
   private static final double SPA_SUN_GEOCENTRIC_LONGITUDE = 204.0182616917; // aka cap Theta
   private static final double SPA_SUN_GEOCENTRIC_LATITUDE = 0.0001011219; // aka capBeta
   private static final double SPA_DELTA_PSI = -0.00399840;
   private static final double SPA_DELTA_EPSILON = 0.00166657;

   private static final double SPA_TRUE_OBLIQUITY_ECLIPTIC = 23.440465; // aka epsilon
   private static final double SPA_SUN_APPARENT_LONGITUDE = 204.0085519281; // aka lambda

   private static final double SPA_OBSERVER_LOCAL_HOUR_ANGLE = 11.105900; //
   private static final double SPA_TOPOCENTRIC_LOCAL_HOUR_ANGLE = 11.10629; //
   private static final double SPA_SUN_TOPOCENTRIC_RA = 202.22704;
   private static final double SPA_SUN_TOPOCENTRIC_DEC = -9.316179;

   private static final double SPA_SUN_TOPOCENTRIC_ZENITH = 50.11162; // aka cap Theta
   private static final double SPA_SUN_AZIMUTH = 194.34024; // aka cap Phi
   
   private static final double SPA_EARTH_HELIOCENTRIC_LATITUDE = -0.0001011219;
   private static final double SPA_EARTH_HELIOCENTRIC_LONGITUDE = 24.0182616917;
   private static final double SPA_EARTH_HELIOCENTRIC_RADIUS = 0.9965422974;
   private static final double SPA_SUNRISE = 6 + 12/60.0 + 43/3600.0;
   private static final double SPA_SUNSET = 17 + 20/60.0 + 19/3600.0;

   // private static final double SPA_TRANSIT

//Sunrise:       06:12:43 Local Time
//Sunset:        17:20:19 Local Time

   // private static final double SPA_



   @Test
   public void SPATest ()
   {
      DeltaT.ForceDeltaT (67.0);
      MoonEphemeris.MoonMeanElongationDegreesCoefficients = MoonEphemeris.MoonSunMeanElongationMeeus22;
      MoonEphemeris.MoonMeanAnomalyCoefficients = MoonEphemeris.MoonMeanAnomalyMeeus22;
      MoonEphemeris.MoonArgumentOfLatitudeCoefficients = MoonEphemeris.MoonArgumentOfLatitudeMeeus22;
      MoonEphemeris.MoonAscendingNodeLongitudeCoefficients = MoonEphemeris.MoonAscendingNodeLongitudeMeeus22;
      MoonEphemeris.MoonMeanLongitudeCoefficients = MoonEphemeris.MoonMeanLongitudeMeeus47;

      EarthEphemeris.EarthMeanAnomalyCoefficients = EarthEphemeris.EarthMeanAnomalyMeeus25;

//      spa.Pressure = 820;
//      spa.Temperature = 11;
//      spa.Slope = 30;
//      spa.AzmRotation = -10;
//      spa.AtmosRefract = 0.5667;

      double jd = Julian.JulianFromYMDHMS (2003, 10, 17, 12 + 7, 30, 30);
      Assert.assertEquals (SPA_JD, jd, 0.000001);

      double alpha = Sun.SunGeocentricRightAscension (SPA_JD);
      Assert.assertEquals (SPA_SUN_RIGHT_ASCENSION, alpha, 0.0001);

      double delta = Sun.SunGeocentricDeclination (jd);
      Assert.assertEquals (SPA_SUN_DECLINATION, delta, 0.0002);

      double capBeta = Sun.SunGeocentricLatitude (jd);
      Assert.assertEquals (SPA_SUN_GEOCENTRIC_LATITUDE, capBeta, 0.0001);
      double capTheta = Sun.SunGeocentricLongitude (jd);
      Assert.assertEquals (SPA_SUN_GEOCENTRIC_LONGITUDE, capTheta, 0.0001);

      final NutationDeltas deltaPsiEpsilon = Nutation.NutationLongitude (jd);
      Assert.assertEquals (SPA_DELTA_PSI, deltaPsiEpsilon.getDeltaPsi (), 0.0001);
      Assert.assertEquals (SPA_DELTA_EPSILON, deltaPsiEpsilon.getDeltaEpsilon (), 0.0001);

      double epsilon = Earth.EclipticTrueObliquityDegrees (jd);
      Assert.assertEquals (SPA_TRUE_OBLIQUITY_ECLIPTIC, epsilon, 0.0001);

      double lambda = Sun.SunApparentLongitude (jd);
      Assert.assertEquals (SPA_SUN_APPARENT_LONGITUDE, lambda, 0.0001);

      final TopocentricPosition position = Sun.SunTopocentricPosition (jd, SPA_LATITUDE, SPA_LONGITUDE, SPA_ELEVATION);
      double ra = position.getRa ();
      double dec = position.getDec ();
      //double hPrime = position[2];
      double e =  position.getElevation ();
      double capPhi = position.getAzimuth ();

      Assert.assertEquals (SPA_SUN_TOPOCENTRIC_RA, ra, 0.005);
      Assert.assertEquals (SPA_SUN_TOPOCENTRIC_DEC, dec, 0.005);
//      Assert.assertEquals (SPA_TOPOCENTRIC_LOCAL_HOUR_ANGLE, hPrime, 0.005);
      Assert.assertEquals (SPA_SUN_TOPOCENTRIC_ZENITH, 90-e, 0.005);
      Assert.assertEquals (SPA_SUN_AZIMUTH, capPhi, 0.005);


      double l = Earth.EarthHeliocentricEllipticalLongitude (jd);
      Assert.assertEquals (SPA_EARTH_HELIOCENTRIC_LONGITUDE, l, 0.001);

      double b = Earth.EarthHeliocentricLatitude (jd);
      Assert.assertEquals (SPA_EARTH_HELIOCENTRIC_LATITUDE, b, 0.00001);

      double r = Earth.EarthHeliocentricRadiusAU (jd);
      Assert.assertEquals (SPA_EARTH_HELIOCENTRIC_RADIUS, r, 0.00001);

//      double eta = spadata.eta;
//      Assert.assertEquals (spadata.eta, eta, 0.00001);

//      double deltaTau = Sun.SunAberrationCorrection (jd);
//      Assert.assertEquals (-0.005711359293251811, deltaTau, 0.00000001);

////Incidence:     25.187000 degrees

      final RiseTransitSet riseTransitSet = SunRiseSet.SunRise (jd, SPA_LATITUDE, SPA_LONGITUDE);
      Assert.assertEquals (SPA_SUNRISE, riseTransitSet.getRise ()-7, 1.0/60);
      Assert.assertEquals (SPA_SUNSET, riseTransitSet.getSet ()+24-7, 1.0/60);
//      double trans = 18 + 46/60.0 + 4.97/3600;
//      Assert.assertEquals (transit, trans, 1.0/60, L"Transit error");

   }
}
