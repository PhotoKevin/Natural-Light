package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.DeltaT;
import com.blackholeofphotography.astrocalc.Earth;
import com.blackholeofphotography.astrocalc.Julian;
import com.blackholeofphotography.astrocalc.Nutation;
import com.blackholeofphotography.astrocalc.NutationDeltas;

import org.junit.Assert;
import org.junit.Test;


public class EarthTest
{
   private static final double SPA_JD = 2452930.312847;
   private final static double ONE_SECOND_ACCURACY = (1.0/(24.0*60.0*60.0));
   @Test
   public void EclipticTrueObliquityDegreesTest ()
   {
      DeltaT.UnForceDeltaT ();

      double jd = Julian.JulianFromYMDHMS (2003, 10, 17, 12 + 7, 30, 30.0);
      Assert.assertEquals (2452930.312847, jd, ONE_SECOND_ACCURACY);

      final NutationDeltas deltaPsiEpsilon = Nutation.NutationLongitude (jd);
      double deltaPsi = deltaPsiEpsilon.getDeltaPsi ();
      double deltaEpsilon = deltaPsiEpsilon.getDeltaEpsilon ();

      Assert.assertEquals ( "deltaPsi error",-0.00399840, deltaPsi, 0.000001);
      Assert.assertEquals ("deltaEpsilon error", 0.00166657, deltaEpsilon, 0.000001);

      double epsilon = Earth.EclipticTrueObliquityDegrees (jd);
      Assert.assertEquals ("epsilon error", 23.440465, epsilon, 0.1);

   }

   @Test
   public void EarthHeliocentricLongitudeTest ()
   {
      DeltaT.UnForceDeltaT ();

      double L = Earth.EarthHeliocentricEllipticalLongitude (SPA_JD);
      Assert.assertEquals (24.0182616917, L, 0.0001);
   }

   @Test
   public void EarthHeliocentricLatitudeTest ()
   {
      DeltaT.UnForceDeltaT ();

      double B = Earth.EarthHeliocentricLatitude (SPA_JD);
      Assert.assertEquals (-0.0001011219, B, 0.000001);
   }

   @Test
   public void EarthHeliocentricRadiusTest ()
   {
      double R = Earth.EarthHeliocentricRadiusAU (SPA_JD);
      Assert.assertEquals (0.9965422974, R, 0.000001);
   }

}
