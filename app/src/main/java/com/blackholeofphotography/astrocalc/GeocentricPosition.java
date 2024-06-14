package com.blackholeofphotography.astrocalc;

/**
 * Represent the Geocentric position of an object in
 * Right Ascension and Declination
 */
public class GeocentricPosition
{
   private final double ra;
   private final double dec;

   /**
    * Construct a new GeocentricPosition object
    * @param ra Right Ascension
    * @param dec Declination
    */
   public GeocentricPosition (double ra, double dec)
   {
      this.ra = ra;
      this.dec = dec;
   }

   /**
    * Get the Right Ascension
    * @return Right Ascension in degrees
    */
   public double getRa ()
   {
      return ra;
   }

   /**
    * Get the Declination
    * @return Declination in degrees
    */
   public double getDec ()
   {
      return dec;
   }
}
