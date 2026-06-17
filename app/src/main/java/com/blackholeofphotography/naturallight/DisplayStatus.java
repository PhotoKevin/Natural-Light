package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.Julian;
import com.blackholeofphotography.astrocalc.Moon;
import com.blackholeofphotography.astrocalc.MoonRise;
import com.blackholeofphotography.astrocalc.RiseTransitSet;
import com.blackholeofphotography.astrocalc.Sun;
import com.blackholeofphotography.astrocalc.SunRiseSet;
import com.blackholeofphotography.astrocalc.TopocentricPosition;

import org.mapsforge.core.model.LatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class DisplayStatus
{
   @SuppressWarnings("unused")
   private static final Logger logger = LoggerFactory.getLogger (DisplayStatus.class);
   static LatLong mGeoPoint = new LatLong (0.0, 0.0);
   static ZoneId mZoneId;
   static ZonedDateTime mTimeStamp = ZonedDateTime.now ().withSecond (0).withNano (0);
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
   public static int calculations = 0;
   public static int lighting_bitmap_created = 0;
   public static int astro_skipped = 0;

   public static void forceCalculation ()
   {
      mIsDirty = true;
      mLastRender = 0;
      calculatePositions ();
      notifyListeners ();
   }

   public static LatLong getGeoPoint ()
   {
      if (mGeoPoint == null)
         mGeoPoint = new LatLong (0.0, 0.0);
      return mGeoPoint;
   }

   public static void setGeoPoint (LatLong aPoint)
   {
      if (!aPoint.equals (mGeoPoint))
      {
         mIsDirty = true;
         mLastRender = 0;
         mGeoPoint = aPoint;
         mZoneId = null;
         notifyListeners ();
      }
   }

   public static void setGeoPoint (LatLong aPoint, ZoneId aZoneId)
   {
      if (!aPoint.equals (mGeoPoint) || !aZoneId.equals (mZoneId))
      {
         mIsDirty = true;
         mLastRender = 0;
         mGeoPoint = aPoint;
         mZoneId = aZoneId;
         notifyListeners ();
      }
   }

   public static ZonedDateTime getTimeStamp ()
   {
      if (useCurrentTime ())
         return ZonedDateTime.now (getDisplayZoneId ()).withSecond (0).withNano (0);

      return mTimeStamp.withSecond (0);
   }

   public static ZoneId getDisplayZoneId ()
   {
      if (mZoneId == null)
         mZoneId = MainActivity.getZoneId (getGeoPoint ().getLatitude (), getGeoPoint ().getLongitude ());

      return mZoneId;
   }

   @SuppressWarnings("unused")
   public static ZonedDateTime getRawTimeStamp ()
   {
      return mTimeStamp.withSecond (0);
   }

   public static void setTimeStamp (ZonedDateTime aTimeStamp)
   {
      ZonedDateTime zeroed = aTimeStamp.withSecond (0).withNano (0);
      if (!zeroed.equals (mTimeStamp))
      {
         mIsDirty = true;
         mTimeStamp = zeroed;
         notifyListeners ();
      }
   }

   public static boolean useCurrentTime ()
   {
      return mUseCurrentTime;
   }

   public static void setUseCurrentTime (boolean value)
   {
      if (value != mUseCurrentTime)
      {
         mUseCurrentTime = value;
         mIsDirty = true;
      }
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

   @SuppressWarnings("unused")
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

   @SuppressWarnings("unused")
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

   private static AstroPosition calculateSunPosition (ZonedDateTime when, LatLong where)
   {
      double jd = Julian.JulianFromZonedDateTime (when);
      TopocentricPosition position = Sun.SunTopocentricPosition (jd, where.getLatitude (), where.getLongitude (), 0);
      return new AstroPosition ((float) position.getAzimuth (), (float) position.getElevation ());
   }

   @SuppressWarnings("unused")
   private static AstroPosition calculateMoonPosition (ZonedDateTime when, LatLong where)
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
   private static ArrayList<DisplayStatusListener> mListeners = null;

   /**
    * Adds a listener to the set of listeners that are sent events.
    *
    * @param listener the listener to be added to the current set of listeners.
    */
   public static void addListener (DisplayStatusListener listener)
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
   public static void removeListener (DisplayStatusListener listener)
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

   protected static long mLastRender = 0;
   final private static int mLastRenderLag = 200;

   private static long total_calc_time = 0;

   public static long averageCalcTime ()
   {
      if (calculations > 0)
         return total_calc_time / calculations;
      return 0;
   }

   public static void calculatePositionsAsync ()
   {
      if (!mIsDirty)
         return;

      long now = System.currentTimeMillis ();
      long next = mLastRender + mLastRenderLag;
      if (next > now || running.availablePermits () <= 0)
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
            calculatePositions ();
            mLastRender = 0;
            notifyListeners ();
         }
      };
      calc.start ();
   }

   private static final Semaphore running = new Semaphore (1);
   public static void calculatePositions ()
   {
      try
      {
         running.acquire ();

         if (!mIsDirty)
            return;

         long start = System.currentTimeMillis ();
         ZonedDateTime displayTime = mTimeStamp;

         final double jdNoon = Julian.JulianFromZonedDateTime (displayTime.withHour (12));
         mSunPosition = calculateSunPosition (displayTime, mGeoPoint);

         RiseTransitSet riseSet = SunRiseSet.SunRise (jdNoon, mGeoPoint.getLatitude (), mGeoPoint.getLongitude ());
         mSunRise = setHour (mTimeStamp, riseSet.getRise ());
         mSunSet = setHour (mTimeStamp, riseSet.getSet ());

         mSunRisePosition = calculateSunPosition (mSunRise, mGeoPoint);
         mSunSetPosition = calculateSunPosition (mSunSet, mGeoPoint);

         double jd = Julian.JulianFromZonedDateTime (displayTime);
         mMoonPosition = Moon.MoonTopocentricPosition (jd, mGeoPoint.getLatitude (), mGeoPoint.getLongitude (), 0);

         final RiseTransitSet moonRiseSet = MoonRise.moonRise (jdNoon, mGeoPoint.getLatitude (), mGeoPoint.getLongitude (), 0);
         mMoonRise = setHour (mTimeStamp, moonRiseSet.getRise ());
         mMoonSet = setHour (mTimeStamp, moonRiseSet.getSet ());

         mMoonRisePosition = calculateMoonPosition (mMoonRise, mGeoPoint);
         mMoonSetPosition = calculateMoonPosition (mMoonSet, mGeoPoint);

         calculations += 1;
         long delta = System.currentTimeMillis () - start;
         total_calc_time += delta;

         mIsDirty = false;
      }
      catch (InterruptedException e)
      {
         logger.error ("calculatePosition", e);
      }
      finally
      {
         running.release ();
      }
   }
}
