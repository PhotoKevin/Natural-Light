package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.Coordinates;
import com.blackholeofphotography.astrocalc.Julian;
import com.blackholeofphotography.astrocalc.Moon;
import com.blackholeofphotography.astrocalc.MoonRise;
import com.blackholeofphotography.astrocalc.RiseTransitSet;
import com.blackholeofphotography.astrocalc.Sun;
import com.blackholeofphotography.astrocalc.SunRiseSet;
import com.blackholeofphotography.astrocalc.TopocentricPosition;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class DisplayStatus
{
   @SuppressWarnings ("unused")
   private static final String LOG_TAG = "DisplayStatus";
   static IGeoPoint mLocation = new GeoPoint (0.0, 0.0);
   static ZoneId mZoneId;
   static ZonedDateTime mTimeStamp = ZonedDateTime.now ();
   static boolean mUseCurrentTime;
   static AstroPosition mSunPosition = new AstroPosition (0, 0);
   static ZonedDateTime mSunRise = ZonedDateTime.now ();
   static AstroPosition mSunRisePosition = new AstroPosition (0, 0);
   static ZonedDateTime mSunSet = ZonedDateTime.now ();
   static AstroPosition mSunSetPosition = new AstroPosition (0, 0);
   static TopocentricPosition mMoonPosition = new TopocentricPosition (0, 0, 0, 0);
   static ZonedDateTime mMoonRise = ZonedDateTime.now ();
   static AstroPosition mMoonRisePosition = new AstroPosition (0, 0);
   static ZonedDateTime mMoonSet = ZonedDateTime.now ();
   static AstroPosition mMoonSetPosition = new AstroPosition (0, 0);
   private static double ZoomLevel;
   private static boolean mIsDirty = true;
   public  static int calculations = 0;
   public static int lighting_bitmap_skipped = 0;
   public static int lighting_bitmap_created = 0;
   public static int astro_skipped = 0;

   public static void forceCalculation ()
   {
      mIsDirty = true;
      mLastRender = 0;
      calculatePositions ();
   }

   public static IGeoPoint getLocation ()
   {
      if (mLocation == null)
         mLocation = new GeoPoint (0.0, 0.0);
      return mLocation;
   }

   public static void setLocation (IGeoPoint aLocation)
   {
      mIsDirty = true;
      mLocation = aLocation;
      mZoneId = null;
   }

   public static void setLocation (IGeoPoint aLocation, ZoneId aZoneId)
   {
      mIsDirty = true;
      mLocation = aLocation;
      mZoneId = aZoneId;
   }

   public static ZonedDateTime getTimeStamp ()
   {
      if (useCurrentTime ())
         return ZonedDateTime.now (getDisplayZoneId ()).withSecond (0);

      return mTimeStamp.withSecond (0);
   }

   public static ZoneId getDisplayZoneId ()
   {
      if (mZoneId == null)
         mZoneId = MainActivity.getZoneId (getLocation ().getLatitude (), getLocation ().getLongitude ());

      return mZoneId;
   }

   public static ZonedDateTime getRawTimeStamp ()
   {
      return mTimeStamp.withSecond (0);
   }

   public static void setTimeStamp (ZonedDateTime aTimeStamp)
   {
      mIsDirty = true;
      mTimeStamp = aTimeStamp.withSecond (0);
   }

   public static boolean useCurrentTime ()
   {
      return mUseCurrentTime;
   }

   public static void setUseCurrentTime (boolean value)
   {
      mUseCurrentTime = value;
   }

   public static AstroPosition getSunPosition ()
   {
      calculatePositions ();
      return mSunPosition;
   }
   public static ZonedDateTime getSunRise ()
   {
      calculatePositions ();
      return mSunRise;
   }

   public static AstroPosition getSunRisePosition ()
   {
      calculatePositions ();
      return mSunRisePosition;
   }

   public static ZonedDateTime getSunSet ()
   {
      calculatePositions ();
      return mSunSet;
   }

   public static AstroPosition getSunSetPosition ()
   {
      calculatePositions ();
      return mSunSetPosition;
   }


   public static TopocentricPosition getMoonPosition ()
   {
      calculatePositions ();
      return mMoonPosition;
   }
   public static ZonedDateTime getMoonRise ()
   {
      calculatePositions ();
      return mMoonRise;
   }

   public static AstroPosition getMoonRisePosition ()
   {
      calculatePositions ();
      return mMoonRisePosition;
   }

   public static ZonedDateTime getMoonSet ()
   {
      calculatePositions ();
      return mMoonSet;
   }

   public static AstroPosition getMoonSetPosition ()
   {
      calculatePositions ();
      return mMoonSetPosition;
   }

   private static ZonedDateTime setHour (ZonedDateTime aDate, double aHour)
   {
      int hour = (int) Math.floor (aHour);
      int minute = (int) ((aHour - hour) * 60);

      ZonedDateTime newDate = aDate.withZoneSameInstant (ZoneId.of ("UTC"));
      return newDate.withHour (0).withMinute (0).plusHours (hour).plusMinutes (minute).withZoneSameInstant (getDisplayZoneId ());
   }

   private static AstroPosition calculateSunPosition (ZonedDateTime when, IGeoPoint where)
   {
      double jd = Julian.JulianFromZonedDateTime (when);
      //double[] position = Sun.SunTopocentricPosition (jd, where.getLatitude (), where.getLongitude (), 0);
      TopocentricPosition position = Sun.SunTopocentricPosition (jd, where.getLatitude (), where.getLongitude (), 0);
      return new AstroPosition ((float) position.getAzimuth (), (float) position.getElevation ());
   }

   private static AstroPosition calculateMoonPosition (ZonedDateTime when, IGeoPoint where)
   {
      double jd = Julian.JulianFromZonedDateTime (when);
      TopocentricPosition position = Moon.MoonTopocentricPosition (jd, where.getLatitude (), where.getLongitude (), 0);
      return new AstroPosition ((float) position.getAzimuth (), (float) position.getElevation ());
   }

   public static double getZoomLevel ()
   {
      return ZoomLevel;
   }

   public static void setZoomLevel (double zoomLevel)
   {
      ZoomLevel = zoomLevel;
   }

   /**
    * The set of listeners to be sent events.
    */
   private static  ArrayList<DisplayStatusListener> mListeners = null;

   /**
    * Adds a listener to the set of listeners that are sent events.
    *
    * @param listener the listener to be added to the current set of listeners.
    */
   public static  void addListener (DisplayStatusListener listener)
   {
      if (mListeners == null)
         mListeners = new ArrayList<> ();

      mListeners.add (listener);
   }

   /**
    * Removes a listener from the set listening to this animation.
    *
    * @param listener the listener to be removed from the current set of listeners for this
    *                 animation.
    */
   public static  void removeListener (DisplayStatusListener listener)
   {
      if (mListeners == null)
         return;

      mListeners.remove (listener);
      if (mListeners.isEmpty ())
         mListeners = null;
   }


   /**
    * Removes all {@link #addListener(DisplayStatusListener) listeners} from this object.
    */
   public static void removeAllListeners ()
   {
      if (mListeners != null)
      {
         mListeners.clear ();
         mListeners = null;
      }
   }


   /**
    * Calls onChange for each listener.
    */
   private static void notifyListeners ()
   {
      if (mListeners == null)
         return;

      for (DisplayStatusListener listener : mListeners)
         listener.onChange ();
   }
   private static boolean running = false;
   protected static long mLastRender = 0;
   private static int mLastRenderLag = 200;

   private static long total_calc_time = 0;
   public static long averageCalcTime ()
   {
      if (calculations > 0)
         return total_calc_time / calculations;
      return 0;
   }

   public static void calculatePositions ()
   {
      if (!mIsDirty)
         return;

      long now = System.currentTimeMillis ();
      long next = mLastRender + mLastRenderLag;
      if (next > now || running)
      {
         DisplayStatus.astro_skipped += 1;
         return;
      }
      mLastRender = System.currentTimeMillis ();


      Thread calc = new Thread ()
      {
         @Override
         public void run ()
         {
            long start = System.currentTimeMillis ();
            running = true;
            ZonedDateTime displayTime = mTimeStamp;

            final double jdNoon = Julian.JulianFromZonedDateTime (displayTime.withHour (12));
            mSunPosition = calculateSunPosition (displayTime, mLocation);

            RiseTransitSet riseSet = SunRiseSet.SunRise (jdNoon, mLocation.getLatitude (), mLocation.getLongitude ());
            mSunRise = setHour (mTimeStamp, riseSet.getRise ());
            mSunSet = setHour (mTimeStamp, riseSet.getSet ());

            mSunRisePosition = calculateSunPosition (mSunRise, mLocation);
            mSunSetPosition = calculateSunPosition (mSunSet, mLocation);

            double jd = Julian.JulianFromZonedDateTime (displayTime);
            mMoonPosition = Moon.MoonTopocentricPosition (jd, mLocation.getLatitude (), mLocation.getLongitude (), 0);

            final RiseTransitSet moonRiseSet = MoonRise.MoonRise (jdNoon, mLocation.getLatitude (), mLocation.getLongitude (), 0);
            mMoonRise = setHour (mTimeStamp, moonRiseSet.getRise ());
            mMoonSet = setHour (mTimeStamp, moonRiseSet.getSet ());

            mIsDirty = false;
            running = false;

            calculations += 1;
            long delta = System.currentTimeMillis () - start;
            total_calc_time += delta;
            notifyListeners ();
         }
      };
      calc.start ();
   }
}
