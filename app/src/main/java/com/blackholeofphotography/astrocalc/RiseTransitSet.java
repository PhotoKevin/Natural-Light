package com.blackholeofphotography.astrocalc;

public class RiseTransitSet
{
   final double rise;
   final double transit;
   final double set;

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
