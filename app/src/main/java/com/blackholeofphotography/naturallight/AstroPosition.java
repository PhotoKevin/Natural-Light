package com.blackholeofphotography.naturallight;

/**
 * Describes the position of an astronomical object by its
 * Topocentric Azimuth and Elevation.
 */
public class AstroPosition
{
   private final float mAzimuth;
   private final float mElevation;

   public AstroPosition (float aAzimuth, float aElevation)
   {
      mAzimuth = aAzimuth;
      mElevation = aElevation;
   }

   public float getAzimuth ()
   {
      return mAzimuth;
   }

   public float getElevation ()
   {
      return mElevation;
   }

}
