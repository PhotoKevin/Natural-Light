package com.blackholeofphotography.astrocalc;

/**
 * Represent the Topocentric position of an object
 */
public class TopocentricPosition
{
   final private double ra;
   final private double dec;
   final private double azimuth;
   final private double elevation;

   /**
    * Create a new Topocentric position object
    * @param ra Right Ascension in degrees
    * @param dec Declination in degrees
    * @param elevation Elevation in degrees
    * @param azimuth Azimuth in degrees
    */
   public TopocentricPosition (double ra, double dec, double elevation, double azimuth)
   {
      this.ra = ra;
      this.dec = dec;
      this.azimuth = azimuth;
      this.elevation = elevation;
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


   /**
    * Get the Azimuth
    * @return Azimuth in degrees
    */
   public double getAzimuth ()
   {
      return azimuth;
   }

   /**
    * Get the Elevation
    * @return Elevation in degrees
    */
   public double getElevation ()
   {
      return elevation;
   }
}
