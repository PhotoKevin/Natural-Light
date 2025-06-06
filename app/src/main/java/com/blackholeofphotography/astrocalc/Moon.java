package com.blackholeofphotography.astrocalc;

import static com.blackholeofphotography.astrocalc.Earth.EclipticTrueObliquityDegrees;
// Ignore Spelling: meeus chapront

public class Moon
{
   static MoonLatitudeSineTerm[] MoonLatitudeSineTerms = null;
   static MoonLongitudeDistanceTerm[] MoonLongitudeDistanceTerms = null;


   private static void LoadTerms ()
   {
      if (MoonLatitudeSineTerms == null)
      {
         MoonLatitudeSineTerms = MoonLatitudeSineTerm.loadTerms ("MoonLatitudeSine.txt");
         MoonLongitudeDistanceTerms = MoonLongitudeDistanceTerm.loadTerms ("MoonLongitudeDistance.txt");

      }
   }


   /// <summary>
   /// Calculate the Mean Elongation of the Moon from the sun for the given date.
   /// </summary>
   /// <param name="jd">Julian date</param>
   /// <returns>The Mean Elongation in degrees</returns>
   /// <remarks>
   /// SPA 3.4.1
   /// Meeus 47.2
   /// </remarks>
   static double MoonMeanElongationDegrees (double jd)
   {
      double jce = Julian.JulianEphemerisCentury (jd);

      double d = Mathd.polynomial (jce, MoonEphemeris.MoonMeanElongationDegreesCoefficients);
      d = Normalization.NormalizeZeroTo360Degrees (d);

      return d;
   }


   /// <summary>
   /// Calculate the Mean Anomaly of the Moon
   /// </summary>
   /// <param name="jd">Julian date</param>
   /// <returns>Mean Anomaly of the Moon in degrees</returns>
   /// <remarks>
   /// SPA 3.4.3
   /// Meeus 47.4
   /// </remarks>
   static double MoonMeanAnomaly (double jd)
   {
      double jce = Julian.JulianEphemerisCentury (jd);

      double x2 = Mathd.polynomial (jce, MoonEphemeris.MoonMeanAnomalyCoefficients);
      x2 = Normalization.NormalizeZeroTo360Degrees (x2);

      return x2;
   }

   /// <summary>
   /// Calculate the Moon's Argument of Latitude
   /// </summary>
   /// <param name="jd"></param>
   /// <returns>The Argument of Latitude in degrees</returns>
   /// <remarks>
   /// SPA 3.4.4
   /// Meeus 47.5
   /// </remarks>

   static double MoonArgumentOfLatitude (double jd)
   {
      double jce = Julian.JulianEphemerisCentury (jd);

      double x3 = Mathd.polynomial (jce, MoonEphemeris.MoonArgumentOfLatitudeCoefficients);

      x3 = Normalization.NormalizeZeroTo360Degrees (x3);

      return x3;
   }


   /// <summary>
   /// Longitude of the ascending node of the Moon's mean orbit on
   /// the ecliptic, measured from the mean equinox of the date.
   /// </summary>
   /// <param name="jd">Julian Date</param>
   /// <returns>Longitude in degrees</returns>
   /// <remarks>
   /// SPA 3.4.5
   /// Meeus ch.22 Nutation and Obliquity
   /// </remarks>

   static double MoonAscendingNodeLongitude (double jd)
   {
      double jce = Julian.JulianEphemerisCentury (jd);


      double omega = Mathd.polynomial (jce, MoonEphemeris.MoonAscendingNodeLongitudeCoefficients);
      omega = Normalization.NormalizeZeroTo360Degrees (omega);

      return omega;
   }

   /// <summary>
   /// Calculate the Mean Longitude at the Mean Equinox of the Date.
   /// </summary>
   /// <param name="jd">Julian date</param>
   /// <returns>The Mean Longitude in degrees</returns>
   /// <remarks>
   /// Meeus 47.1
   /// </remarks>

   static double MoonMeanLongitude (double jd)
   {
      double t = Julian.JulianEphemerisCentury (jd);
      double lprime = Mathd.polynomial (t, MoonEphemeris.MoonMeanLongitudeCoefficients);

      lprime = Normalization.NormalizeZeroTo360Degrees (lprime);
      return lprime;
   }

   static double eccentricity (double t, int m)
   {
      double e = 1.0;
      if (Math.abs (m) == 1)
         e = 1.0 - 0.002516 * t - 0.0000074 * t * t;
      else
         if (Math.abs (m) == 2)
         {
            e = 1.0 - 0.002516 * t - 0.0000074 * t * t;
            e = e * e;
         }

      return e;
   }


   /// <summary>
   /// Geocentric position of the moon for the supplied Julian Date
   /// </summary>
   /// <param name="jd">Date of interest</param>
   /// <remarks>
   /// MPA 3.2
   /// Meeus Ch47 - Position of the Moon
   /// </remarks>
   static GeoCentricLatitudeLongitude MoonGeocentricPosition (double jd)
   {
      LoadTerms ();
      double t = Julian.JulianEphemerisCentury (jd);
      double l_prime = MoonMeanLongitude (jd); // Meeus 47.1
      double d = MoonMeanElongationDegrees (jd); // Meeus 47.2
      double m = Earth.EarthMeanAnomaly (jd);  // 47.3
      double mprime = MoonMeanAnomaly (jd); // 47.4
      double f = MoonArgumentOfLatitude (jd); // 47.5

      double a1 = Normalization.NormalizeZeroTo360Degrees (119.75 + 131.849*t);
      double a2 = Normalization.NormalizeZeroTo360Degrees (53.09 + 479264.290*t);
      double a3 = Normalization.NormalizeZeroTo360Degrees (313.45 + 481266.484*t);

      double summation_l = 0.0;
      double summation_r = 0.0;
      for (int i=0; i<MoonLongitudeDistanceTerms.length; i++)
      {
         MoonLongitudeDistanceTerm term = MoonLongitudeDistanceTerms[i];
         double e = eccentricity (t,  term.m);

         double argument = term.d*d + term.m*m + term.mprime*mprime + term.f*f;
         summation_l += term.sigl * e * Mathd.sind (argument);
         summation_r += term.sigr * e * Mathd.cosd (argument);
      }
      summation_l +=   3958 * Mathd.sind (a1)
            + 1962 * Mathd.sind (l_prime - f)
            +  318 * Mathd.sind (a2);


      double summation_b = 0.0;

      for (int i=0; i<MoonLatitudeSineTerms.length; i++)
      {
         MoonLatitudeSineTerm term = MoonLatitudeSineTerms[i];
         double e = eccentricity (t,  term.m);

         double argument = term.d*d + term.m*m + term.mprime*mprime + term.f*f;
         summation_b += term.sigl * e * Mathd.sind (argument);
      }
      summation_b += -2235 * Mathd.sind (l_prime)
            + 382 * Mathd.sind (a3)
            + 175 * Mathd.sind (a1-f)
            + 175 * Mathd.sind (a1+f)
            + 127 * Mathd.sind (l_prime - mprime)
            - 115 * Mathd.sind (l_prime + mprime);

      return new GeoCentricLatitudeLongitude (Normalization.fix180degrees (summation_b / 1000000),
            Normalization.NormalizeZeroTo360Degrees (l_prime + (summation_l/1000000)),
            385000.56 + (summation_r/1000));
   }

   @SuppressWarnings ("unused")
   public GeocentricPosition MoonGeocentricEquatorialPosition (double jd)
   {
      LoadTerms ();
      double lambda;
      double beta;
      double capdelta;
      var moonPosition = MoonGeocentricPosition (jd); //, &beta, &lambda, &capdelta);
      beta = moonPosition.getLatitude ();
      lambda = moonPosition.getLongitude ();
      capdelta = moonPosition.getDistance ();

      // MPA 3.5.2
      double epsilon = EclipticTrueObliquityDegrees (jd);


      var nutationalLongitude = Nutation.NutationLongitude (jd); //, &deltaPsi, &deltaEpsilon);
      double deltaPsi = nutationalLongitude.getDeltaPsi ();
      //double deltaEpsilon = nutationalLongitude.getDeltaEpsilon ();
      lambda += deltaPsi;


      // 3.8.1
      double alpha = Mathd.atan2d (Mathd.sind(lambda)*Mathd.cosd(epsilon) - Mathd.tand(beta)*Mathd.sind(epsilon), Mathd.cosd(lambda));
      alpha = Normalization.NormalizeZeroTo360Degrees (alpha);

      // 3.9 Moon Geocentric declination
      double low_delta = Mathd.asind (Mathd.sind(beta) * Mathd.cosd(epsilon) + Mathd.cosd(beta) * Mathd.sind(epsilon) * Mathd.sind(lambda));
      return new GeocentricPosition (alpha, low_delta, capdelta);
   }

   public static TopocentricPosition MoonTopocentricPosition (double jd, double latitude, double longitude, double elevation) //, double *ra, double *dec)
   {
      double lambda;
      double beta;
      double capdelta;
      var moonPosition = Moon.MoonGeocentricPosition (jd); //, &beta, &lambda, &capdelta);
      beta = moonPosition.getLatitude ();
      lambda = moonPosition.getLongitude ();
      capdelta = moonPosition.getDistance ();


      // MPA 3.5.2
      double epsilon = EclipticTrueObliquityDegrees (jd);

      var nutationalLongitude = Nutation.NutationLongitude (jd); //, &deltaPsi, &deltaEpsilon);
      double deltaPsi = nutationalLongitude.getDeltaPsi ();
      // double deltaEpsilon = nutationalLongitude.getDeltaEpsilon ();

      lambda += deltaPsi;


      // 3.8.1
      double alpha = Mathd.atan2d (Mathd.sind(lambda)*Mathd.cosd(epsilon) - Mathd.tand(beta)*Mathd.sind(epsilon), Mathd.cosd(lambda));
      alpha = Normalization.NormalizeZeroTo360Degrees (alpha);

      // 3.9 Moon Geocentric declination
      double low_delta = Mathd.asind (Mathd.sind(beta) * Mathd.cosd(epsilon) + Mathd.cosd(beta) * Mathd.sind(epsilon) * Mathd.sind(lambda));

      return Coordinates.geocentric2Topocentric (new GeocentricPosition (alpha, low_delta), capdelta, latitude, longitude, elevation, jd);
   }
}