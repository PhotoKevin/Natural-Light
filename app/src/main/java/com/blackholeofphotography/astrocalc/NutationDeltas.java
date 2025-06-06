package com.blackholeofphotography.astrocalc;

public class NutationDeltas
{
   private final double jd;
   private final double deltaPsi;
   private final double deltaEpsilon;

   public NutationDeltas (double jd, double deltaPsi, double deltaEpsilon)
   {
      this.jd = jd;
      this.deltaPsi = deltaPsi;
      this.deltaEpsilon = deltaEpsilon;
   }

   @SuppressWarnings ("unused")
   public double getJd ()
   {
      return jd;
   }

   public double getDeltaPsi ()
   {
      return deltaPsi;
   }

   public double getDeltaEpsilon ()
   {
      return deltaEpsilon;
   }
}
