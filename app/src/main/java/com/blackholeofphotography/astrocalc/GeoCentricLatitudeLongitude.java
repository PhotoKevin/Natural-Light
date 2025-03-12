package com.blackholeofphotography.astrocalc;

/**
 * Represent the position of an astronomical body using latitude and longitude.
 */
public class GeoCentricLatitudeLongitude
{
   private final double latitude; // aka beta
   private final double longitude; // aka lambda
   private final double distance;

   /**
    * Get the Latitude of the object
    * @return The latitude in degrees
    */
   public double getLatitude ()
   {
      return latitude;
   }

   /**
    * Get the Longitude of the object
    * @return The longitude in degrees
    */
   public double getLongitude ()
   {
      return longitude;
   }

   /**
    * Get the distance to the object
    * @return The distance.
    */
   public double getDistance ()
   {
      return distance;
   }

   /**
    * Construct a new GeoCentricLatitudeLongitude object
    * @param latitude Latitude of the object
    * @param longitude Longitude of the object
    * @param distance Distance to the object
    */
   GeoCentricLatitudeLongitude (double latitude, double longitude, double distance)
   {
      this.latitude = latitude;
      this.longitude = longitude;
      this.distance = distance;
   }
}
