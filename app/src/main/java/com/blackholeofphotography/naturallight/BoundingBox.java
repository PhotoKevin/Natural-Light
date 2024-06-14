package com.blackholeofphotography.naturallight;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

public class BoundingBox implements Serializable
{
   private final double mMinLatitude;
   private final double mMaxLatitude;
   private final double mMinLongitude;
   private final double mMaxLongitude;
   public BoundingBox (double minLatitude, double minLongitude, double maxLatitude, double maxLongitude)
   {
      mMinLatitude = Math.min (minLatitude, maxLatitude);
      mMaxLatitude = Math.max (minLatitude, maxLatitude);
      mMinLongitude = Math.min (minLongitude, maxLongitude);
      mMaxLongitude = Math.max (minLongitude, maxLongitude);
   }

   public double getMinLatitude ()
   {
      return mMinLatitude;
   }

   public double getMaxLatitude ()
   {
      return mMaxLatitude;
   }

   public double getMinLongitude ()
   {
      return mMinLongitude;
   }

   public double getMaxLongitude ()
   {
      return mMaxLongitude;
   }

   public BoundingBox include (double latitude, double longitude)
   {
      return new BoundingBox (
            Math.min (mMinLatitude,  latitude),
            Math.min (mMinLongitude, longitude),
            Math.max (mMaxLatitude,  latitude),
            Math.max (mMaxLongitude, longitude)
      );
   }

   public BoundingBox include (BoundingBox box)
   {
      return new BoundingBox (
            Math.min (mMinLatitude,  box.getMaxLatitude ()),
            Math.min (mMinLongitude, box.getMinLongitude ()),
            Math.max (mMaxLatitude,  box.getMaxLatitude ()),
            Math.max (mMaxLongitude, box.getMaxLongitude ())
      );
   }

   @NonNull
   @Override
   public String toString ()
   {
      return String.format (Locale.getDefault (),"%f,%f:%f,%f", mMinLatitude, mMinLongitude, mMaxLatitude, mMaxLongitude);
   }

   public boolean intersects (BoundingBox box)
   {
      if (this.getMinLongitude () < box.getMaxLongitude () && this.getMaxLongitude () > box.getMinLongitude () &&
         this.getMaxLatitude () > box.getMinLatitude () && this.getMinLatitude () < box.getMaxLatitude ())
         return true;
      return false;
   }
}
