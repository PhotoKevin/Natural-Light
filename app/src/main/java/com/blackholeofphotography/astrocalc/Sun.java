package com.blackholeofphotography.astrocalc;

public class Sun
{
// Ignore Spelling: Meeus jce

   /// <summary>
   /// Calculate the Right Ascension (alpha) of the Sun
   /// </summary>
   /// <param name="jd">Julian date</param>
   /// <returns>The Right Ascension in degrees</returns>
   /// <remarks>
   /// Right Ascension is the East/West angle
   /// Is this the True, or Mean RA?
   /// SPA 3.9
   /// </remoarks>
   public static double SunGeocentricRightAscension (double jd)
   {
      double lambda = Sun.SunApparentLongitude (jd);
      double beta = Earth.EarthHeliocentricLatitude (jd);
      double epsilon = Earth.EclipticTrueObliquityDegrees (jd);

      double sigma = Mathd.atan2d (Mathd.sind (lambda) * Mathd.cosd (epsilon) - Mathd.tand (beta) * Mathd.sind (epsilon), Mathd.cosd (lambda));
      return Normalization.NormalizeZeroTo360Degrees (sigma);
   }

/// <summary>
/// Calculate the Declination (delta) of the Sun
/// </summary>
/// <param name="jd">Julian date</param>
/// <returns>The Declination in degrees</returns>
/// <remarks>The Declination is the North/South angle.
/// SPA 3.10
/// </remarks>

   public static double SunGeocentricDeclination (double jd)
   {
      double beta = Earth.EarthHeliocentricLatitude (jd);
      double lambda = SunApparentLongitude (jd);
      double epsilon = Earth.EclipticTrueObliquityDegrees (jd);

      double delta = Mathd.asind (Mathd.sind (beta) * Mathd.cosd (epsilon) + Mathd.cosd (beta) * Mathd.sind (epsilon) * Mathd.sind (lambda));
      return delta;
   }

/// <summary>
/// Calculate the Aberration Correction (delta Tau) in degrees
/// </summary>
/// <param name="jd">Julian date</param>
/// <returns>The Aberration Correction in degrees</returns>
/// <remarks>
/// Meeus 24 Solar Coordinates 24.10
/// </remarks>

   public static double SunAberrationCorrection (double jd)
   {
      double deltaTau = -20.4898 / (3600 * Earth.EarthHeliocentricRadiusAU (jd));
      return deltaTau;
   }

   /// <summary>
/// Calculate the Apparent Longitude of the Sun (lambda)
/// </summary>
/// <param name="jd">Julian date</param>
/// <returns>Apparent Longitude in degrees</returns>
   public static double SunApparentLongitude (double jd)
   {

      double theta = Sun.SunGeocentricLongitude (jd);

      var nutation = Nutation.NutationLongitude (jd);

      double deltaTau = Sun.SunAberrationCorrection (jd);
      double lambda = theta + nutation.getDeltaPsi () + deltaTau;

      return lambda;
   }


   /// <summary>
   /// Calculate the Geocentric Longitude of the Sun ()
   /// </summary>
   /// <param name="jd">Julian date</param>
   /// <returns>Geocentric Longitude in degrees</returns>
   /// <remarks>
   /// SPA 3.3
   /// Meeus 25 - Solar Coordinates
   /// </remarks>
   public static double SunGeocentricLongitude (double jd)
   {
      double sigma = Earth.EarthHeliocentricEllipticalLongitude (jd) + 180;
      sigma = Normalization.NormalizeZeroTo360Degrees (sigma);
      return sigma;
   }

/// <summary>
/// Calculate the Geocentric Latitude of the Sun (beta)
/// </summary>
/// <param name="jd">Julian date</param>
/// <returns>Geocentric Latitude in degrees</returns>

   public static double SunGeocentricLatitude (double jd)
   {
      double beta = 0 - Earth.EarthHeliocentricLatitude (jd);
      return beta;
   }

   /// <summary>
   /// Compute the Observer's Local Hour Angle in Degrees to the body
   /// </summary>
   /// <param name="jd">Julian Date</param>
   /// <param name="longitude">Longitude of observer (aka sigma σ)</param>
   /// <param name="rightAscention">The Right Ascension of the body (aka alpha α)</param>
   /// <returns>The hour angle in degrees</returns>
   public static double ObserverLocalHourAngleDegrees (double jd, double longitude, double rightAscension)
   {
      double nu = Sidereal.ApparentSiderealDegrees (jd);

      double H = nu + longitude - rightAscension;

      H = Normalization.NormalizeZeroTo360Degrees (H);
      return H;
   }


   public static double AtmosphericRefractionCorrection (double trueAltitude)
   {
      double deltaE = 0;
      final double SunRadius = 0.26667;
      final double AtmosphericRefraction = 0.5667;
      final double Temperature = 20.0;  // Temperature in Degrees C.
      final double Pressure = 1000; // Pressure in millibars

      if (trueAltitude >= -1 * (SunRadius + AtmosphericRefraction))
      {
         deltaE = Pressure / 1010.0;
         deltaE *= 283 / (273 + Temperature); // Meeus unlabeled formula call it 16.5
         deltaE *= 1.02 / (60 * Mathd.tand ((trueAltitude + 10.3 / (trueAltitude + 5.11)))); // Meeus 16.4
      }

      return deltaE;
   }


   static double TopocentricAzimuthAngle (double hPrime, double latitude, double deltaPrime)
   {
      double AstronomersAzimuth = Mathd.atan2d (Mathd.sind(hPrime),
                                 Mathd.cosd (hPrime) * Mathd.sind (latitude) - Mathd.tand (deltaPrime) * Mathd.cosd(latitude));

      return Normalization.NormalizeZeroTo360Degrees (AstronomersAzimuth + 180.0);
   }

   public static TopocentricPosition SunTopocentricPosition (double jd, double latitude, double longitude, double elevation)
   {
      double delta = SunGeocentricDeclination (jd);
      double alpha = SunGeocentricRightAscension (jd);
      double R = Earth.EarthHeliocentricRadiusAU (jd);

      R *= 149597870.700; // AU to kilometers
      return Coordinates.geocentric2Topocentric (new GeocentricPosition (alpha, delta), R, latitude, longitude, elevation, jd);
   }

}
