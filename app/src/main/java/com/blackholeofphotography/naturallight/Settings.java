/*
 * This work is licensed under the Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. To view a 
 * copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.blackholeofphotography.naturallight;

import android.content.SharedPreferences;
import android.util.Log;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Support for the preferences. 
 * @author Kevin Nickerson (kevin@blackholeofphotography.com)
 */
public final class Settings
{
   private static final String LOG_TAG = "Settings";

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
   public static final String KEY_SHOW_ADVANCED_PREFERENCES = "nl_show_advanced_preferences";
   private static boolean mEnableAdvancedSettings = false;
   private static boolean mShowAdvancedSettings = false;
   public static final String KEY_TILE_PROVIDER = "nl_tile_provider";
   private static String mTileProvider = "";
   public static final String KEY_MAP_ORIENTATION = "nl_map_orientation";
   private static float mMapOrientation;
   public static final String KEY_LATITUDE = "nl_latitude";
   public static final String KEY_LONGITUDE = "nl_longitude";
   private static IGeoPoint mMapCenter;
   public static final String KEY_ZONE_ID = "nl_zone_id";
   private static ZoneId mZoneId;
   public static final String KEY_ZOOM_LEVEL = "nl_zoom_level";
   private static double mZoomLevel;
   public static final String KEY_DISPLAY_TIME = "nl_display_time";
   private static ZonedDateTime mDisplayTime;
   public static final String KEY_USE_CURRENT_TIME = "nl_use_current_time";
   private static boolean mUseCurrentTime;
   private static final String KEY_SHOW_DEBUG_DATA = "nl_show_debug_data";
   private static boolean mShowDebugData;

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
         mMapCenter = new GeoPoint (mLatitude, mLongitude);
         mMapOrientation = sharedPreferences.getFloat (KEY_MAP_ORIENTATION, 5.0F);
         mRegion = sharedPreferences.getString (KEY_REGION, "Northern America");
         mReverseTimeDrag = sharedPreferences.getBoolean (KEY_REVERSE_TIME_DRAG, false);
         mShowAdvancedSettings = sharedPreferences.getBoolean (KEY_SHOW_ADVANCED_PREFERENCES, true);
         mShowCrosshair = sharedPreferences.getBoolean (KEY_SHOW_CROSSHAIR, true);
         mShowDebugData = sharedPreferences.getBoolean (KEY_SHOW_DEBUG_DATA, false);
         mShowSunRiseSetNeedle = sharedPreferences.getBoolean (KEY_SHOW_SUN_RISE_SET_NEEDLE, true);
         mSunRiseSetNeedleThickness = sharedPreferences.getFloat (KEY_SUN_RISE_SET_NEEDLE_THICKNESS, 2.0f);
         mTickInterval = sharedPreferences.getInteger (KEY_TICK_MARK_SPACING, 10);
         mTileProvider = sharedPreferences.getString (KEY_TILE_PROVIDER, "");
         mUseCurrentTime = sharedPreferences.getBoolean (KEY_USE_CURRENT_TIME, true);
         String zoneName = sharedPreferences.getString (KEY_ZONE_ID, "UTC");
         mZoneId = ZoneId.of (zoneName);
         mZoomLevel = sharedPreferences.getFloat (KEY_ZOOM_LEVEL, 5.0F);

         if (!BuildConfig.DEBUG)
            mShowDebugData = false;

         try
         {
            String json = sharedPreferences.getString (KEY_LOCATIONS, "[]");
            mLocations = Location.fromJson (json);
         }
         catch (Exception ex)
         {
            Log.e (LOG_TAG, ex.toString ());
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
      catch (Exception e)
      {
         Log.e (LOG_TAG, e.toString ());
      }
   }
   
   public static void saveToBackingStore ()
   {
      SensibleSharedPreferences sharedPreferences = new SensibleSharedPreferences (MainActivity.getContext ());

      SharedPreferences.Editor ed = sharedPreferences.edit ();

      ed.putBoolean (KEY_LOW_ACCURACY, mLowAccuracy);
      ed.putBoolean (KEY_SHOW_ADVANCED_PREFERENCES, mShowAdvancedSettings);
      ed.putBoolean (KEY_SHOW_CROSSHAIR, mShowCrosshair);
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

   public static String getRegion ()
   {
      return mRegion;
   }

   public static boolean getEnableAdvancedFeatures ()
   {
      if (com.blackholeofphotography.naturallight.BuildConfig.DEBUG)
         mEnableAdvancedSettings = true;

      return mEnableAdvancedSettings;
   }

   public static boolean isLowAccuracy ()
   {
      return mLowAccuracy;
   }

   public static boolean showDebugData ()
   {
      return mShowDebugData;
   }

   public static void setEnableAdvancedFeatures (boolean value)
   {
      Settings.mEnableAdvancedSettings = value;
   }

   public static boolean getShowAdvancedSettings ()
   {
      return mShowAdvancedSettings;
   }

   public static String getTileProvider ()
   {
      return mTileProvider;
   }
   public static void setTileProvider (String aTileProvider)
   {
      mTileProvider = aTileProvider;
   }

   public static float getMapOrientation ()
   {
      return mMapOrientation;
   }

   public static void setMapOrientation (float aMapOrientation)
   {
      mMapOrientation = aMapOrientation;
   }

   public static IGeoPoint getMapCenter ()
   {
      return mMapCenter;
   }
   public static ZoneId getZoneId ()
   {
      return mZoneId;
   }

   @SuppressWarnings ("unused")
   public static void setMapCenter (GeoPoint mMapCenter)
   {
      Settings.mMapCenter = mMapCenter;
   }

   public static void setMapCenter (IGeoPoint aMapCenter, ZoneId aZoneId)
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
}
