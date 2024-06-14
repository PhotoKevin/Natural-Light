package com.blackholeofphotography.naturallight;

import java.io.Serializable;

public class TimeZoneArea implements Serializable
{
   private final String mTimeZoneName;
   private final BoundingBox mBoundingBox;


   public TimeZoneArea (String name, BoundingBox box)
   {
      mTimeZoneName = name;
      mBoundingBox = box;
   }

   public String getTimeZoneName ()
   {
      return mTimeZoneName;
   }

   public BoundingBox getBoundingBox ()
   {
      return mBoundingBox;
   }


}