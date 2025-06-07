package com.blackholeofphotography.astrocalc;

public class Coordinates
{
   /// <summary>
   /// Convert a Geocentric RA, Dec coordinate to Topocentric
   /// </summary>
   /// <param name="geora">Geocentric Right Ascension </param>
   /// <param name="geodec">Geocentric Dec</param>
   /// <param name="radius">Distance in km between Earth and object</param>
   /// <param name="latitude">Latitude of observer</param>
   /// <param name="longitude">Longitude of observer</param>
   /// <param name="altitude">Altitude of observer</param>
   /// <param name="jd">Julian date</param>
   /// <param name="topora">Topocentric Right Ascension</param>
   /// <param name="topodec">Topocentric Declination</param>
   public static TopocentricPosition geocentric2Topocentric (GeocentricPosition geocentricPosition, double radius, double latitude, double longitude, double altitude, double jd)
   {
      // Meeus Ch 11 p 82. MPA 3.11.1, 3.11.2, 3.11.3
      double u = Math.atan (0.99664719 * Mathd.tand (latitude));
      double x = Math.cos (u) + (altitude / 6378140 * Mathd.cosd (latitude));
      double y = 0.99664719 * Math.sin (u) + (altitude / 6378140 * Mathd.sind (latitude));

      // Meeus Ch 47. Note: 6378.14 is the nominal radius of the Earth in kilometers.
      // MPA 3.3 has a typo. It appears to say alpha sin, but it's
      // really asin as shown in Meeus 47.a
      double pi = Mathd.asind (6378.14 / radius);

      // MPA 3.7
      double nu = Sidereal.ApparentSiderealDegrees (jd);

      // MPA 3.10
      double H = (nu + longitude - geocentricPosition.getRa ());
      H = Normalization.NormalizeZeroTo360Degrees (H);

      // Meeus 40.2. Note that he has the calculation of x embedded in his formula as p*cos(phi')
      // MPA 3.11.4
      double dra = Mathd.atan2d (-x * Mathd.sind (pi) * Mathd.sind (H), Mathd.cosd (geocentricPosition.getDec ()) - x * Mathd.sind (pi) * Mathd.cosd (H));
      double rap = Normalization.NormalizeZeroTo360Degrees (geocentricPosition.getRa () + dra);

      // Meeus 4.03. As above, he shows x embedded as well as y as p*sin(phi')
      // MPA 3.11.6 says the x in the denominator is a y. That is wrong.
      double decp = Mathd.atan2d ((Mathd.sind (geocentricPosition.getDec ()) - y * Mathd.sind (pi)) * Mathd.cosd (dra), Mathd.cosd (geocentricPosition.getDec ()) - x * Mathd.sind (pi) * Mathd.cosd (H));

      double hPrime = H - dra;
      double eZero = Mathd.asind (Mathd.sind (latitude) * Mathd.sind (decp) + Mathd.cosd (latitude) * Mathd.cosd (decp) * Mathd.cosd (hPrime));
      double e = eZero + Sun.AtmosphericRefractionCorrection (eZero); // Topocentric Elevation Angle
      final double capPhi = Sun.TopocentricAzimuthAngle (hPrime, latitude, decp);

      TopocentricPosition topocentricPosition = new TopocentricPosition (rap, decp, e, capPhi);

      return topocentricPosition;
   }


   /// <summary>
   /// Compute the Observer's Local Hour Angle in Degrees to the body
   /// </summary>
   /// <param name="jd">Julian Date</param>
   /// <param name="longitude">Longitude of observer (aka sigma σ)</param>
   /// <param name="rightAscension">The Right Ascension of the body (aka alpha α)</param>
   /// <returns>The hour angle in degrees</returns>

   public static double ObserverLocalHourAngleDegrees (double jd, double longitude, double rightAscension)
   {
      double nu = Sidereal.ApparentSiderealDegrees (jd);

      double H = nu + longitude - rightAscension;

      H = Normalization.NormalizeZeroTo360Degrees (H);
      return H;
   }
}

