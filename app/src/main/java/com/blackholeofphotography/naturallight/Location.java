package com.blackholeofphotography.naturallight;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Location extends OverlayItem
{
   private static final String LOG_TAG = "Location";
   public static final String UID = "uid";
   public static final String TITLE = "title";
   public static final String SNIPPET = "snippet";
   public static final String LATITUDE = "latitude";
   public static final String LONGITUDE = "longitude";
   public static final String TIMESTAMP = "timestamp";
   public static final String USE_CURRENT_TIME = "useCurrentTime";
   public static final String ZOOM = "zoom";
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
         jo.put (UID, getUid ());
         jo.put (TITLE, getTitle ());
         jo.put (SNIPPET, getSnippet ());
         jo.put (LATITUDE, getPoint ().getLatitude ());
         jo.put (LONGITUDE, getPoint ().getLongitude ());
         jo.put (USE_CURRENT_TIME, getUseCurrentTime ());
         jo.put (ZOOM, getZoomLevel ());
         jo.put (TIMESTAMP, mDateTime.toString ());
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
         GeoPoint point = new GeoPoint (jo.getDouble (LATITUDE), jo.getDouble (LONGITUDE));
         ZonedDateTime timestamp = ZonedDateTime.parse (jo.getString (TIMESTAMP));
         boolean useCurrentTime = jo.getBoolean (USE_CURRENT_TIME);
         double zoomLevel = jo.getDouble (ZOOM);
         if (jo.has (UID))
            return new Location (jo.getString (UID), jo.getString (TITLE), jo.getString (SNIPPET), point, timestamp, useCurrentTime, zoomLevel);
         else
            return new Location (jo.getString (TITLE), jo.getString (SNIPPET), point, timestamp, useCurrentTime, zoomLevel);
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

   public static String formatGeoPoint (double latLon)
   {
      return String.format (Locale.getDefault (), "%.4f", latLon);
   }

   public static String formatGeoPoint (IGeoPoint pt)
   {
      Locale l = Locale.getDefault ();
      DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(l);
      DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
      char groupingChar = symbols.getGroupingSeparator();
      final char listSeparator = (groupingChar == ',')  ? ';' : ',';

      return String.format (l, "%.4f%c %.4f", pt.getLatitude (), listSeparator, pt.getLongitude ());
   }


   @NonNull
   @Override
   public String toString ()
   {
      return formatGeoPoint (getPoint ());
   }
}
