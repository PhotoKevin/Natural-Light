package com.blackholeofphotography.naturallight;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Location extends OverlayItem
{
   private static final String LOG_TAG = "Location";
   private final boolean mUseCurrentTime;
   private final ZonedDateTime mDateTime;
   private final double ZoomLevel;

   public Location (String aTitle, String aSnippet, IGeoPoint aGeoPoint, ZonedDateTime dateTime, boolean aUseCurrentTime, double aZoomLevel)
   {
      super (UUID.randomUUID ().toString (), aTitle, aSnippet, aGeoPoint);
      mDateTime = dateTime;
      mUseCurrentTime = aUseCurrentTime;
      ZoomLevel = aZoomLevel;
   }

   public Location (String aUid, String aTitle, String aSnippet, IGeoPoint aGeoPoint, ZonedDateTime dateTime, boolean aUseCurrentTime, double aZoomLevel)
   {
      super (aUid != null ? aUid : UUID.randomUUID ().toString (), aTitle, aSnippet, aGeoPoint);
      mDateTime = dateTime;
      mUseCurrentTime = aUseCurrentTime;
      ZoomLevel = aZoomLevel;
   }

   public ZonedDateTime getDateTime ()
   {
      return mDateTime;
   }

   public boolean getUseCurrentTime ()
   {
      return mUseCurrentTime;
   }
   public double getZoomLevel ()
   {
      return ZoomLevel;
   }
   public JSONObject toJson ()
   {
      try
      {
         JSONObject jo = new JSONObject ();
         jo.put ("uid", getUid ());
         jo.put ("title", getTitle ());
         jo.put ("snippet", getSnippet ());
         jo.put ("latitude", getPoint ().getLatitude ());
         jo.put ("longitude", getPoint ().getLongitude ());
         jo.put ("timestamp", getDateTime ());
         jo.put ("useCurrentTime", getUseCurrentTime ());
         jo.put ("zoom", getZoomLevel ());
         if (mDateTime != null)
            jo.put ("timestamp", mDateTime.toString ());
         return jo;
      }
      catch (JSONException ex)
      {
         Log.d (LOG_TAG, ex.toString ());
      }
      return null;
   }


   public static Location fromJson (JSONObject jo)
   {
      try
      {
         GeoPoint point = new GeoPoint (jo.getDouble ("latitude"), jo.getDouble ("longitude"));
         ZonedDateTime timestamp = ZonedDateTime.parse (jo.getString ("timestamp"));
         boolean useCurrentTime = jo.getBoolean ("useCurrentTime");
         double zoomLevel = jo.getDouble ("zoom");
         return new Location (jo.getString ("uid"), jo.getString ("title"), jo.getString ("snippet"), point, timestamp, useCurrentTime, zoomLevel);
      }
      catch (JSONException ex)
      {
         return null;
      }
   }

   public static String toJson (List<Location> items)
   {
      JSONArray ja = new JSONArray ();
      for (Location item : items)
         ja.put (item.toJson ());

      return ja.toString ();
   }


   public static ArrayList<Location> fromJson (String s)
   {
      try
      {
         JSONArray ja = new JSONArray (s);
         return fromJson (ja);
      }
      catch (JSONException ex)
      {
         return null;
      }
   }

   public static ArrayList<Location> fromJson (JSONArray ja)
   {
      ArrayList<Location> items = new ArrayList<> ();
      try
      {
         for (int i = 0; i < ja.length (); i++)
         {
            Location item = fromJson (ja.getJSONObject (i));
            if (item != null)
               items.add (item);
         }
      }
      catch (JSONException ex)
      {
         return null;
      }
      return items;
   }

   @NonNull
   @Override
   public String toString ()
   {
      return ASTools.formatGeoPoint (getPoint ());
   }
}
