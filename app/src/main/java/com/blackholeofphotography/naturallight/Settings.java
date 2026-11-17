/*
 * This work is licensed under the Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. To view a 
 * copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.blackholeofphotography.naturallight;

import android.content.SharedPreferences;

import org.mapsforge.core.model.LatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Support for the preferences. 
 * @author Kevin Nickerson (kevin@blackholeofphotography.com)
 */
public final class Settings
{
   private static final Logger logger = LoggerFactory.getLogger (Settings.class);

   // Keys start with "nl" so I can add code later to reset to default values
   public static final String KEY_LOCATIONS = "nl_locations";
   private static ArrayList<Location> mLocations;
   public static final String KEY_TICK_MARK_SPACING = "nl_tick_mark_spacing";
   private static int mTickInterval = 45;
   public static final String KEY_DEGREE_SPACING = "nl_degree_spacing";
   private static int mDegreeInterval = 45;
   public static final String KEY_DEGREE_FONT_SIZE = "nl_degree_font_size";
   private static float mAngleLabelFontSize = 1.0F;
   public static final String KEY_COMPASS_RADIUS_PERCENT ="nl_compass_radius_percent";
   private static int mCompassPercentage = 90;

   public static final String KEY_ENABLE_DEVELOPER_MODE = "nl_enable_developer_mode";
   private static final String KEY_SHOW_DEBUG_DATA = "nl_show_debug_data";
   private static boolean mShowDebugData;
   private static boolean mDeveloper = false;

   public static final String KEY_TILE_PROVIDER = "nl_tile_provider";
   private static String mTileProvider = "";
   public static final String KEY_MAP_ORIENTATION = "nl_map_orientation";
   private static float mMapOrientation;
   public static final String KEY_LATITUDE = "nl_latitude";
   public static final String KEY_LONGITUDE = "nl_longitude";
   private static LatLong mMapCenter;
   public static final String KEY_ZONE_ID = "nl_zone_id";
   private static ZoneId mZoneId;
   public static final String KEY_ZOOM_LEVEL = "nl_zoom_level";
   private static double mZoomLevel;
   public static final String KEY_DEFAULT_ZOOM_LEVEL = "nl_default_zoom_level";
   private static double mDefaultZoomLevel;

   public static final String KEY_DISPLAY_TIME = "nl_display_time";
   private static ZonedDateTime mDisplayTime;
   public static final String KEY_USE_CURRENT_TIME = "nl_use_current_time";
   private static boolean mUseCurrentTime;

   public static final String KEY_SUN_RISE_SET_NEEDLE_THICKNESS = "nl_rise_set_needle_thickness";
   private static float mSunRiseSetNeedleThickness;
   public static final String KEY_SHOW_SUN_RISE_SET_NEEDLE = "nl_show_sun_rise_set_needle";
   private static boolean mShowSunRiseSetNeedle;
   public static final String KEY_SHOW_CROSSHAIR = "nl_show_crosshair";
   private static boolean mShowCrosshair;
   public static final String KEY_CROSSHAIR_THICKNESS = "nl_crosshair_thickness";
   private static float mCrosshairThickness;
   public static final String KEY_CROSSHAIR_LENGTH = "nl_crosshair_length";
   private static float mCrosshairLength;
   public static final String KEY_LOW_ACCURACY = "nl_low_accuracy";
   private static boolean mLowAccuracy;
   public static final String KEY_REGION = "nl_region";
   private static String mRegion;

   public static final String KEY_REVERSE_TIME_DRAG = "nl_reverse_time_drag";
   private static boolean mReverseTimeDrag;

   public static final String KEY_DELETE_TIMESHAPE_DATA = "nl_delete_timeshape_data";
   public static final String KEY_ALLOW_MOBILE_DATA_USAGE = "nl_allow_mobile_data_usage";
   public static final String KEY_FAKE_AS_MOBILE = "nl_fake_as_mobile";
   public static final String KEY_MOBILE_USAGE_REQUESTED = "nl_mobile_usage_requested";
   private static boolean mAllowMobileDataUsage;
   private static boolean mFakeAsMobile;
   private static boolean mMobileUsageRequested;
   private static boolean mDeleteTimeShapeData;

   public static final String KEY_LAST_DATA_CHECK = "nl_last_data_check";
   private static ZonedDateTime mLastDataCheck;

   private static boolean firstTime = true;
   public static void loadFromBackingStore ()
   {
      try
      {
         SensibleSharedPreferences sharedPreferences = new SensibleSharedPreferences (MainActivity.getContext ());

         float mLatitude = sharedPreferences.getFloat (KEY_LATITUDE, 5.0F);
         float mLongitude = sharedPreferences.getFloat (KEY_LONGITUDE, 5.0F);

         mAngleLabelFontSize = sharedPreferences.getFloat (KEY_DEGREE_FONT_SIZE, 20.0F);
         mCompassPercentage = sharedPreferences.getInteger (KEY_COMPASS_RADIUS_PERCENT, 90);
         mCrosshairLength = sharedPreferences.getFloat (KEY_CROSSHAIR_LENGTH, 30);
         mCrosshairThickness = sharedPreferences.getFloat (KEY_CROSSHAIR_THICKNESS, 1.5f);
         mDegreeInterval = sharedPreferences.getInteger (KEY_DEGREE_SPACING, 20);
         mDisplayTime = sharedPreferences.getZonedDateTime (KEY_DISPLAY_TIME, ZonedDateTime.now ());
         mLowAccuracy = sharedPreferences.getBoolean (KEY_LOW_ACCURACY, true);
         mMapCenter = new LatLong (mLatitude, mLongitude);
         mMapOrientation = sharedPreferences.getFloat (KEY_MAP_ORIENTATION, 5.0F);
         mRegion = sharedPreferences.getString (KEY_REGION, "Northern America");
         mReverseTimeDrag = sharedPreferences.getBoolean (KEY_REVERSE_TIME_DRAG, false);
         mShowCrosshair = sharedPreferences.getBoolean (KEY_SHOW_CROSSHAIR, true);


         mShowSunRiseSetNeedle = sharedPreferences.getBoolean (KEY_SHOW_SUN_RISE_SET_NEEDLE, true);
         mSunRiseSetNeedleThickness = sharedPreferences.getFloat (KEY_SUN_RISE_SET_NEEDLE_THICKNESS, 2.0f);
         mTickInterval = sharedPreferences.getInteger (KEY_TICK_MARK_SPACING, 10);
         mTileProvider = sharedPreferences.getString (KEY_TILE_PROVIDER, "");
         mUseCurrentTime = sharedPreferences.getBoolean (KEY_USE_CURRENT_TIME, true);
         String zoneName = sharedPreferences.getString (KEY_ZONE_ID, "UTC");
         mZoneId = ZoneId.of (zoneName);
         mZoomLevel = sharedPreferences.getFloat (KEY_ZOOM_LEVEL, 5.0F);
         mDefaultZoomLevel = sharedPreferences.getFloat (KEY_DEFAULT_ZOOM_LEVEL, 17);

         mShowDebugData = sharedPreferences.getBoolean (KEY_SHOW_DEBUG_DATA, false);
         mDeveloper = sharedPreferences.getBoolean (KEY_ENABLE_DEVELOPER_MODE, false);
         mAllowMobileDataUsage = sharedPreferences.getBoolean (KEY_ALLOW_MOBILE_DATA_USAGE, false);
         mFakeAsMobile = sharedPreferences.getBoolean (KEY_FAKE_AS_MOBILE, false);
         mMobileUsageRequested = sharedPreferences.getBoolean (KEY_MOBILE_USAGE_REQUESTED, false);
         mLastDataCheck = sharedPreferences.getZonedDateTime (KEY_LAST_DATA_CHECK, earliest ());
         mDeleteTimeShapeData = sharedPreferences.getBoolean (KEY_DELETE_TIMESHAPE_DATA, false);

         if (!BuildConfig.DEBUG)
            mShowDebugData = false;

         try
         {
            String json = sharedPreferences.getString (KEY_LOCATIONS, "[]");
            mLocations = Location.fromJson (json);
         }
         catch (Exception ex)
         {
            logger.error ("loadFromBackingStore", ex);
            mLocations = new ArrayList<> ();
         }

         // If any of the items ended up with the default value, make sure it's back in the shared preferences
         // so the setting screen shows the correct value.
         if (firstTime)
         {
            saveToBackingStore ();
            firstTime = false;
         }
      }
      catch (Exception ex)
      {
         logger.error ("loadFromBackingStore 2nd", ex);
      }
   }
   
   public static void saveToBackingStore ()
   {
      SensibleSharedPreferences sharedPreferences = new SensibleSharedPreferences (MainActivity.getContext ());
      SharedPreferences.Editor ed = sharedPreferences.edit ();

      ed.putBoolean (KEY_LOW_ACCURACY, mLowAccuracy);
      ed.putBoolean (KEY_SHOW_CROSSHAIR, mShowCrosshair);

      ed.putBoolean (KEY_ENABLE_DEVELOPER_MODE, mDeveloper);
      ed.putBoolean (KEY_SHOW_DEBUG_DATA, mShowDebugData);

      ed.putBoolean (KEY_SHOW_SUN_RISE_SET_NEEDLE, mShowSunRiseSetNeedle);
      ed.putString (KEY_COMPASS_RADIUS_PERCENT, String.valueOf (mCompassPercentage));
      ed.putString (KEY_CROSSHAIR_LENGTH, String.valueOf (mCrosshairLength));
      ed.putString (KEY_CROSSHAIR_THICKNESS, String.valueOf (mCrosshairThickness));
      ed.putString (KEY_DEGREE_FONT_SIZE, String.valueOf (getAngleLabelFontSize ()));
      ed.putString (KEY_DEGREE_SPACING, String.valueOf (getAngleLabelInterval ()));
      ed.putString (KEY_DISPLAY_TIME, getDisplayTime ().toString ());
      ed.putString (KEY_LATITUDE, String.valueOf (mMapCenter.getLatitude ()));
      ed.putString (KEY_LOCATIONS, Location.toJson (mLocations));
      ed.putString (KEY_LONGITUDE, String.valueOf (mMapCenter.getLongitude ()));
      ed.putString (KEY_MAP_ORIENTATION, String.valueOf (getMapOrientation ()));
      ed.putString (KEY_REGION, mRegion);
      ed.putString (KEY_SUN_RISE_SET_NEEDLE_THICKNESS, String.valueOf (mSunRiseSetNeedleThickness));
      ed.putString (KEY_TICK_MARK_SPACING, String.valueOf (getTickMarkInterval ()));
      ed.putString (KEY_TILE_PROVIDER, getTileProvider ());
      ed.putString (KEY_USE_CURRENT_TIME, String.valueOf (mUseCurrentTime));
      ed.putString (KEY_ZONE_ID, mZoneId.toString ());
      ed.putString (KEY_ZOOM_LEVEL, String.valueOf (getZoomLevel ()));
      ed.putString (KEY_DEFAULT_ZOOM_LEVEL, String.valueOf ((int) getDefaultZoomLevel ()));
      ed.putBoolean (KEY_FAKE_AS_MOBILE, fakeAsMobile ());
      ed.putBoolean (KEY_ALLOW_MOBILE_DATA_USAGE, mAllowMobileDataUsage);
      ed.putBoolean (KEY_MOBILE_USAGE_REQUESTED, mMobileUsageRequested);
      ed.putString (KEY_LAST_DATA_CHECK, mLastDataCheck.format (DateTimeFormatter.ISO_DATE_TIME));
      ed.putBoolean (KEY_DELETE_TIMESHAPE_DATA, mDeleteTimeShapeData);

      ed.apply ();
   }   


   public static ZonedDateTime getDisplayTime ()
   {
      return mDisplayTime;
   }

   public static void setDisplayTime (ZonedDateTime displayTime)
   {
      mDisplayTime = displayTime;
   }

   public static boolean useCurrentTime ()
   {
      return mUseCurrentTime;
   }

   public static void setUseCurrentTime (boolean mUseCurrentTime)
   {
      Settings.mUseCurrentTime = mUseCurrentTime;
   }

   public static float getAngleLabelFontSize ()
   {
      return mAngleLabelFontSize;
   }

   /**
    * Get the Tick Mark Degrees value
    * @return Number of degrees for each tick mark
    */
   public static int getTickMarkInterval ()
   {
      return mTickInterval;
   }

   public static int getAngleLabelInterval ()
   {
      return mDegreeInterval;
   }

   public static boolean showSunRiseSetNeedle ()
   {
      return mShowSunRiseSetNeedle;
   }

   public static float getSunRiseSetNeedleThickness ()
   {
      return mSunRiseSetNeedleThickness;
   }

   public static boolean showCrosshair ()
   {
      return mShowCrosshair;
   }

   public static float getCrosshairThickness ()
   {
      return mCrosshairThickness;
   }

   public static float getCrosshairLength ()
   {
      return mCrosshairLength;
   }

   public static boolean reverseTimeDrag ()
   {
      return mReverseTimeDrag;
   }

   public static ArrayList<Location> getLocationsCopy ()
   {
      return mLocations;
   }

   public static Location getLocation (String aUid)
   {
      final Optional<Location> first = mLocations.stream ().filter (p -> p.getUid ().equals (aUid)).findFirst ();
      return first.orElse (null);
   }
   public static void addLocation (Location item)
   {
      removeLocation (item.getUid ());
      mLocations.add (item);
   }

   public static void removeLocation (String aUid)
   {
      for (int i=mLocations.size ()-1; i>=0; i--)
      {
         if (mLocations.get (i).getUid ().equals (aUid))
            mLocations.remove (i);
      }
   }

   /**
    * Update the location item in the list. Locations are immutable,
    * so we have to find the position in the list, remove it and
    * re-add at the same place.
    * @param item Location to update
    */
   public static void updateLocation (Location item)
   {
      int position = -1;
      for (int i=0; i<mLocations.size (); i++)
         if (mLocations.get (i).getUid ().equals (item.getUid ()))
            position = i;

      if (position >= 0)
      {
         mLocations.remove (position);
         mLocations.add (position, item);
      }
   }


   public static void setRegion (String regionName)
   {
      mRegion = regionName;
   }

   public static String getRegion ()
   {
      return mRegion;
   }

   public static boolean isLowAccuracy ()
   {
      return mLowAccuracy;
   }

   /**
    * Determine if additional debug data should be shown on the main display
    * Note: The only way to turn this on is by stepping through in the debugger and
    * manually changing the variable.
    * @return True if the debug data should be shown.
    */
   public static boolean showDebugData ()
   {
      return mShowDebugData;
   }

   public static void setDeveloper (boolean developer)
   {
      mDeveloper = developer;
   }
   public static boolean isDeveloper ()
   {
      return mDeveloper;
   }


   public static String getTileProvider ()
   {
      return mTileProvider;
   }
   public static float getMapOrientation ()
   {
      return mMapOrientation;
   }
   public static LatLong getMapCenter ()
   {
      return mMapCenter;
   }
   public static ZoneId getZoneId ()
   {
      return mZoneId;
   }

   @SuppressWarnings ("unused")
   public static void setMapCenter (LatLong mMapCenter)
   {
      Settings.mMapCenter = mMapCenter;
   }

   public static void setMapCenter (LatLong aMapCenter, ZoneId aZoneId)
   {
      mMapCenter = aMapCenter;
      mZoneId = aZoneId;
   }

   public static int getCompassPercentage ()
   {
      return mCompassPercentage;
   }

   public static double getZoomLevel ()
   {
      return mZoomLevel;
   }

   public static void setZoomLevel (double aZoomLevel)
   {
      mZoomLevel = aZoomLevel;
   }

   public static double getDefaultZoomLevel ()
   {
      return mDefaultZoomLevel;
   }

   public static void allowMobileDataUsage (boolean allow)
   {
      mAllowMobileDataUsage = allow;
   }

   public static boolean fakeAsMobile ()
   {
      return mFakeAsMobile;
   }
   public static boolean allowMobileDataUsage ()
   {
      return mAllowMobileDataUsage;
   }

   public static void setMobileUsageRequested (boolean requested)
   {
      mMobileUsageRequested = requested;
   }

   public static boolean mobileUsageRequested ()
   {
      return mMobileUsageRequested;
   }

   public static void deleteTimeShapeData (boolean delete)
   {
      mDeleteTimeShapeData = delete;
   }

   public static ZonedDateTime getLastDataCheck ()
   {
      return mLastDataCheck;
   }

   public static void setLastDataCheck (ZonedDateTime mLastDataCheck)
   {
      Settings.mLastDataCheck = mLastDataCheck;
   }

   public static ZonedDateTime earliest ()
   {
      return ZonedDateTime.of (2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault ());
   }
}
