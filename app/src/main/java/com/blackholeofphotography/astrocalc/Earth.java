package com.blackholeofphotography.astrocalc;

public class Earth
{
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsL0 = null;
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsL1 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsL2 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsL3 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsL4 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsL5 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsR0 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsR1 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsR2 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsR3 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsR4 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsB0 = new PlanetaryPeriodicTerm[2];
   static PlanetaryPeriodicTerm[] EarthPeriodicTermsB1 = new PlanetaryPeriodicTerm[2];

   private static void LoadTerms ()
   {
      if (EarthPeriodicTermsL0 == null)
      {
         EarthPeriodicTermsL0 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsL0.txt");
         EarthPeriodicTermsL1 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsL1.txt");
         EarthPeriodicTermsL2 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsL2.txt");
         EarthPeriodicTermsL3 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsL3.txt");
         EarthPeriodicTermsL4 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsL4.txt");
         EarthPeriodicTermsL5 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsL5.txt");

         EarthPeriodicTermsR0 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsR0.txt");
         EarthPeriodicTermsR1 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsR1.txt");
         EarthPeriodicTermsR2 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsR2.txt");
         EarthPeriodicTermsR3 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsR3.txt");
         EarthPeriodicTermsR4 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsR4.txt");

         EarthPeriodicTermsB0 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsB0.txt");
         EarthPeriodicTermsB1 = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsB1.txt");
      }
   }

   /// <summary>
/// Calculate the Mean Anomaly of the Earth
/// </summary>
/// <param name="jd">Julian date</param>
/// <returns>Mean Anomaly of the Earth in degrees</returns>
/// <remarks>
/// SPA 3.4.2
/// </remarks>
   public static double EarthMeanAnomaly (double jd)
   {
      double jce = Julian.JulianEphemerisCentury (jd);
      double m = Mathd.polynomial (jce, EarthEphemeris.EarthMeanAnomalyCoefficients);

      m = Normalization.NormalizeZeroTo360Degrees (m);
      return m;
   }


/// <summary>
/// Calculate the True Obliquity of the Ecliptic in degrees for the given date.
/// </summary>
/// <param name="jd">Julian date</param>
/// <returns>Obliquity of the Ecliptic in degrees</returns>
/// <remarks>
/// SPA 3.5.2
/// Laskar's tenth-degree polynomial fit (J. Laskar, Astronomy and Astrophysics, Vol. 157, page 68 [1986])
/// </remarks>

   public static double EclipticTrueObliquityDegrees (double jd)
   {
      double U = Julian.JulianEphemerisMillennium (jd) / 10.0;
      double pow = U;
      double e0;

      // Meeus 22.3 - Note: These terms are in seconds.
      double[] terms = {-4680.93, -1.55, 1999.25, -51.38, -249.67, -39.05, 7.12, 27.87, 5.79, 2.45};

      e0 = 84381.448; // 23 degrees 26' 21.448" converted to seconds.
      // let e0 = 23 + 26.0/60 + 21.448 / 3600;
      // let e0 *= 3600;
      for (int i = 0; i < terms.length; i++)
      {
         e0 += terms[i] * pow;
         pow *= U;
      }
      e0 /= 3600; // Convert back to degrees


      var deltaPsiEpsilon = Nutation.NutationLongitude (jd);
      double deltaEpsilon = deltaPsiEpsilon.getDeltaEpsilon ();
      double epsilon = e0 + deltaEpsilon;

      return epsilon;
   }

   static double EarthPowerSeries (double jme, PlanetaryPeriodicTerm[] terms)
   {
      double summation = 0.0;
      for (int i = 0; i < terms.length; i++)
      {
         double next = terms[i].a * Math.cos (terms[i].b + terms[i].c * jme);
         summation += next;
      }

      return summation;
   }

   /// <summary>
   /// Calculate a simple power series with
   /// (a0*x^0) + (a1*x^1) + ...
   /// </summary>
   /// <param name="x">The power term</param>
   /// <param name="terms">Array of coefficients</param>
   /// <param name="nterms">Number of coefficients</param>
   /// <returns>The series result</returns>

   static double Polynomial (double x, double[] terms)
   {
      double power = 1.0;
      double result = 0.0;
      for (int i = 0; i < terms.length; i++)
      {
         result += terms[i] * power;
         power *= x;
      }

      return result;
   }

   /// <summary>
   /// Calculate the true longitude of the Earth relative to the Sun (lambda)
   /// </summary>
   /// <param name="jd">Julian date</param>
   /// <returns>
   /// The mean longitude in degrees.
   /// </returns>
   /// <remarks>
   /// SPA 3.2
   /// Meeus Chapter 32 - Positions of the Planets
   /// Mean Dynamical Ecliptic. Meeus p. 219
   /// </remarks>

   public static double EarthHeliocentricEllipticalLongitude (double jd)
   {
      LoadTerms ();
      double jme = Julian.JulianEphemerisMillennium (jd);

      double[] L = new double[6];

      // 3.2.3
      L[0] = EarthPowerSeries (jme, EarthPeriodicTermsL0);
      L[1] = EarthPowerSeries (jme, EarthPeriodicTermsL1);
      L[2] = EarthPowerSeries (jme, EarthPeriodicTermsL2);
      L[3] = EarthPowerSeries (jme, EarthPeriodicTermsL3);
      L[4] = EarthPowerSeries (jme, EarthPeriodicTermsL4);
      L[5] = EarthPowerSeries (jme, EarthPeriodicTermsL5);

      double series = Polynomial (jme, L) / 1e8;

      return Normalization.NormalizeZeroTo360Degrees (Mathd.Degrees (series));
   }

   /// <summary>
   /// Calculate the latitude of the Earth relative to the Sun (beta)
   /// </summary>
   /// <param name="jd">Julian date</param>
   /// /// <remarks>
   /// Meeus 32 - Positions of the planets
   /// Mean Dynamical Equinox Meeus p. 219
   /// </remarks>


   public static double EarthHeliocentricLatitude (double jd)
   {
      double jme = Julian.JulianEphemerisMillennium (jd);

      double[] B = new double[2];

      LoadTerms ();

      B[0] = EarthPowerSeries (jme, EarthPeriodicTermsB0);
      B[1] = EarthPowerSeries (jme, EarthPeriodicTermsB1);

      return Mathd.Degrees ((Polynomial (jme, B) / 1e8));
   }

   /// <summary>
   /// Calculate the radius vector of the Earth relative to the Sun aka R in AU
   /// </summary>
   /// <param name="jd">Julian date</param>
   /// <remarks>
   /// Meeus 31 - Positions of the planets
   /// </remarks>

   public static double EarthHeliocentricRadiusAU (double jd)
   {
      double jme = Julian.JulianEphemerisMillennium (jd);

      double[] R = new double[5];
      LoadTerms ();

      R[0] = EarthPowerSeries (jme, EarthPeriodicTermsR0);
      R[1] = EarthPowerSeries (jme, EarthPeriodicTermsR1);
      R[2] = EarthPowerSeries (jme, EarthPeriodicTermsR2);
      R[3] = EarthPowerSeries (jme, EarthPeriodicTermsR3);
      R[4] = EarthPowerSeries (jme, EarthPeriodicTermsR4);

      return  Polynomial (jme, R) / 1e8;
   }
}