package com.blackholeofphotography.astrocalc;

public class Nutation
{
   public static final int DELTA_PSI_DEGREES = 0;
   public static final int DELTA_EPSILON_DEGREES = 1;
   // https://de.zxc.wiki/wiki/Nutation_(Astronomie)#Die_IAU_2000_Theory_of_Nutation
   ///
   ///<summary>
   /// Calculate the NutationalLongitude in Degrees.
   ///</summary>
   /// Section 3.4
   ///
   public static NutationDeltas NutationLongitude (double jd)
   {
      if (Tools.ismReducedAccuracy ())
         return new NutationDeltas (jd, 0, 0);

      double deltaPsiDegrees;
      double deltaEpsilonDegrees;
      double x[] = new double[5];
      x[0] = Moon.MoonMeanElongationDegrees (jd);
      x[1] = Earth.EarthMeanAnomaly (jd);
      x[2] = Moon.MoonMeanAnomaly (jd);
      x[3] = Moon.MoonArgumentOfLatitude (jd); // Middle argument of the perigee
      x[4] = Moon.MoonAscendingNodeLongitude (jd);

      for (int i = 0; i < 5; i++)
         x[i] = Normalization.NormalizeZeroTo360Degrees (x[i]);

      deltaPsiDegrees = 0.0;
      deltaEpsilonDegrees = 0.0;
      double jce = Julian.JulianEphemerisCentury (jd);

      EarthNutationTerm[] nutationTerms = EarthNutationTerm.loadNutationTerms ("EarthNutation.txt").toArray (new EarthNutationTerm[0]);
      for (int i = 0; i < nutationTerms.length; i++)
      {
         double jsum = 0.0;
         for (int j = 0; j < 5; j++)
            jsum += x[j] * nutationTerms[i].y[j];

         deltaPsiDegrees += (nutationTerms[i].a + nutationTerms[i].b * jce) * Mathd.sind (jsum);
         deltaEpsilonDegrees += (nutationTerms[i].c + nutationTerms[i].d * jce) * Mathd.cosd (jsum);
      }

      deltaPsiDegrees /= 36000000;
      deltaEpsilonDegrees /= 36000000;

      return new NutationDeltas (jd, deltaPsiDegrees, deltaEpsilonDegrees);
   }
}