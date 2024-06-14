package com.blackholeofphotography.astrocalc;

public class RiseTransitSet
{
   double rise;
   double transit;
   double set;

   public RiseTransitSet (double rise, double transit, double set)
   {
      this.rise = rise;
      this.transit = transit;
      this.set = set;
   }

   public double getRise ()
   {
      return rise;
   }

   public double getTransit ()
   {
      return transit;
   }

   public double getSet ()
   {
      return set;
   }
}
