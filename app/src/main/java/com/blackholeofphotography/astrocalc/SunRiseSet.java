package com.blackholeofphotography.astrocalc;

public class SunRiseSet
{
   public static final int SUN_RISE = 0;
   public static final int SUN_TRANSIT = 1;
   public static final int SUN_SET = 2;

   public static double[] SunRise (double jd, double latitude, double longitude) //, double *rise, double *transet, double *set)
   {
      double rise, transit, set;

      // Move to 0 hour UTC
      jd = Math.floor (jd - 0.5) + 0.5;

      // A.2.1
      double v = Sidereal.ApparentSiderealDegrees (jd);

      // A.2.2
      double[] alpha = new double[3];
      alpha[0] = Sun.SunGeocentricRightAscension (jd - 1.0);
      alpha[1] = Sun.SunGeocentricRightAscension (jd);
      alpha[2] = Sun.SunGeocentricRightAscension (jd + 1.0);

      double[] delta = new double[3];
      delta[0] = Sun.SunGeocentricDeclination (jd - 1.0);
      delta[1] = Sun.SunGeocentricDeclination (jd);
      delta[2] = Sun.SunGeocentricDeclination (jd + 1.0);

      // A.2.3
      // Calculate the approximate sun transit time, m0 , in fraction of day
      double sigma = longitude; // Longitude of observer in degrees
      double m0 = (alpha[1] - sigma - v) / 360.0;

      // A.2.4
      double hprime0 = -0.8333;
      double phi = latitude; // Latitude of observer in degrees
      double H0 = Mathd.sind (hprime0) - Mathd.sind (phi) * Mathd.sind (delta[1]);
      H0 /= Mathd.cosd (phi) * Mathd.cosd (delta[1]);
      if (H0 < -1.0 || H0 > 1.0)
      {
         rise = -1;
         set = -1;
         return new double[] {rise, rise, rise};
      }

      H0 = Mathd.acosd (H0);

      H0 = Tools.limit (H0, 180);
      // A.2.5
      double m1 = m0 - H0 / 360.0;

      // A.2.6
      double m2 = m0 + H0 / 360.0;

      // A.2.7
      m0 = Tools.limit (m0, 1);
      m1 = Tools.limit (m1, 1);
      m2 = Tools.limit (m2, 1);

      // A.2.8
      double v0 = v + 360.985647 * m0;
      double v1 = v + 360.985647 * m1;
      double v2 = v + 360.985647 * m2;


      // A.2.9
      int year = (int) (100.0 * Julian.JulianCentury (jd) + 2000);
      double n0 = m0 + DeltaT.DeltaT (year) / 86400;
      double n1 = m1 + DeltaT.DeltaT (year) / 86400;
      double n2 = m2 + DeltaT.DeltaT (year) / 86400;

      // A.2.10
      double a = Normalization.NormalizeZeroToOne (alpha[1] - alpha[0]);
      double aprime = delta[1] - delta[0];

      double b = Normalization.NormalizeZeroToOne (alpha[2] - alpha[1]);
      double bprime = delta[2] - delta[1];

      double c = b - a;
      double cprime = bprime - aprime;

      double alphaprime0 = alpha[1] + n0 * (a + b + c * n0) / 2.0;
      double alphaprime1 = alpha[1] + n1 * (a + b + c * n1) / 2.0;
      double alphaprime2 = alpha[1] + n2 * (a + b + c * n2) / 2.0;

      double deltaprime0 = delta[1] + n0 * (aprime + bprime + cprime * n0) / 2.0;
      double deltaprime1 = delta[1] + n1 * (aprime + bprime + cprime * n1) / 2.0;
      double deltaprime2 = delta[1] + n2 * (aprime + bprime + cprime * n2) / 2.0;

      // A2.2.11
      double Hprime0 = v0 + sigma - alphaprime0;
      double Hprime1 = v1 + sigma - alphaprime1;
      double Hprime2 = v2 + sigma - alphaprime2;

      Hprime0 = Normalization.fix180degrees (Hprime0);
      Hprime1 = Normalization.fix180degrees (Hprime1);
      Hprime2 = Normalization.fix180degrees (Hprime2);

      // A2.2.12
      double h0 = Mathd.asind (Mathd.sind (phi) * Mathd.sind (deltaprime0) + Mathd.cosd (phi) * Mathd.cosd (deltaprime0) * Mathd.cosd (Hprime0));
      double h1 = Mathd.asind (Mathd.sind (phi) * Mathd.sind (deltaprime1) + Mathd.cosd (phi) * Mathd.cosd (deltaprime1) * Mathd.cosd (Hprime1));
      double h2 = Mathd.asind (Mathd.sind (phi) * Mathd.sind (deltaprime2) + Mathd.cosd (phi) * Mathd.cosd (deltaprime2) * Mathd.cosd (Hprime2));

      // A2.2.13
      double T = m0 - Hprime0 / 360;

      // A2.2.15
      double R = m1 + ((h1 - hprime0) / (360 * Mathd.cosd (deltaprime1) * Mathd.cosd (phi) * Mathd.sind (Hprime1)));

      double S = m2 + ((h2 - hprime0) / (360 * Mathd.cosd (deltaprime2) * Mathd.cosd (phi) * Mathd.sind (Hprime2)));

      rise = R * 24.0;
      transit = T * 24.0;
      set = S * 24.0;

      double[] result = new double[3];
      result[SUN_RISE] = rise;
      result[SUN_TRANSIT] = transit;
      result[SUN_SET] = set;

      return result;
   }
}
