package com.blackholeofphotography.naturallight.ui.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.blackholeofphotography.astrocalc.Tools;
import com.blackholeofphotography.naturallight.ASTools;
import com.blackholeofphotography.naturallight.CompassOverlay;
import com.blackholeofphotography.naturallight.DisplayStatus;
import com.blackholeofphotography.naturallight.DisplayStatusListener;
import com.blackholeofphotography.naturallight.LightingOverlay;
import com.blackholeofphotography.naturallight.Location;
import com.blackholeofphotography.naturallight.MainActivity;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.Settings;
import com.blackholeofphotography.naturallight.databinding.MapFragmentBinding;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class MapDisplayFragment extends Fragment implements DisplayStatusListener
{
   @SuppressWarnings ("unused")
   private static final String LOG_TAG = "MapDisplayFragment";

   private MapFragmentBinding binding;
   private static MapView mMapView;
   private TextView lightingDate;
   private TextView mSunRise;
   private TextView mSunSet;
   private TextView mTextLocation;
   private TextView mTextDebug;
   private TextView mTimeZone;
   private ImageButton mSetDateTime;
   private Location currentLocation;
   Runnable repeatativeTaskRunnable;
   private ProgressBar mProgressBar;


   private final Handler taskHandler = new android.os.Handler (Looper.getMainLooper ());

   // https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/StarterMapFragment.java
   public View onCreateView (@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
   {
      binding = MapFragmentBinding.inflate (inflater, container, false);
      View root = binding.getRoot ();

      mMapView = binding.map;
      mTimeZone = binding.displayTimezone;
      mTextLocation = binding.mapCenter;
      mTextDebug = binding.textDebug;

      lightingDate = binding.lightingDate;
      mSetDateTime = binding.buttonSetDateTime;
      mSunRise = binding.textSunLeft;
      mSunSet = binding.textSunRight;

      mProgressBar = binding.progressBar;
      mProgressBar.setIndeterminate (true);
      binding.addLocation.setOnClickListener (this::onClick);

      if (!Settings.showDebugData ())
      {
         mTextDebug.setVisibility (View.GONE);
         mTextLocation.setVisibility (View.GONE);
         binding.guidelineThird.setVisibility (View.GONE);

         ConstraintLayout.LayoutParams mapParams = (ConstraintLayout.LayoutParams)
               binding.map.getLayoutParams ();
         mapParams.bottomToTop = R.id.guidelineSecond;
      }

      setupMap (MainActivity.getContext ());

      currentLocation = null;
      final Bundle arguments = getArguments ();
      MapDisplayFragmentArgs args = MapDisplayFragmentArgs.fromBundle (arguments);
      if (args.getAUid () != null)
         currentLocation = Settings.getLocation (args.getAUid ());

      if (currentLocation != null)
         moveTo (currentLocation);

      return root;
   }

   @SuppressLint("ClickableViewAccessibility")
   private void setupMap (Context ctx)
   {
      mMapView.setTileSource (TileSourceFactory.MAPNIK);
      mMapView.setMultiTouchControls (true);

      final CompassOverlay compassOverlay = new CompassOverlay (ctx, mMapView);
      compassOverlay.enableCompass ();
      mMapView.getOverlays ().add (compassOverlay);

      final LightingOverlay lightingOverlay = new LightingOverlay (ctx, mMapView);
      mMapView.getOverlays ().add (lightingOverlay);


      mMapView.addMapListener (new MapListener ()
      {
         @Override
         public boolean onScroll (ScrollEvent event)
         {
            DisplayStatus.setLocation (mMapView.getMapCenter ());
            return true;
         }


         @Override
         public boolean onZoom (ZoomEvent event)
         {
            DisplayStatus.setZoomLevel (mMapView.getZoomLevelDouble ());
            return true;
         }
      });

      mSetDateTime.setOnClickListener (new View.OnClickListener ()
      {
         @Override
         public void onClick (View v)
         {
            NavController navController = Navigation.findNavController (binding.getRoot ());
            NavDirections action =
                  MapDisplayFragmentDirections.actionNavMapToDateTimeFragment ();
            navController.navigate (action);
         }
      });


      lightingDate.setOnTouchListener (new View.OnTouchListener ()
      {
         private float previousX;

         @Override
         public boolean onTouch (View v, MotionEvent event)
         {
            int action = event.getActionMasked ();
            if (action == MotionEvent.ACTION_DOWN)
               previousX = event.getX ();
            else if (action == MotionEvent.ACTION_MOVE)
            {
               float distance = previousX - event.getX ();
               if (Settings.reverseTimeDrag ())
                  distance = -distance;

               DisplayStatus.setUseCurrentTime (false);
               DisplayStatus.setTimeStamp (DisplayStatus.getTimeStamp ().minusMinutes ((long) distance));
               DisplayStatus.calculatePositions ();

               previousX = event.getX ();
            }

            return true;
         }
      });

      setupLocationOverlay ();
   }

   void moveTo (Location location)
   {
      DisplayStatus.setLocation (location.getPoint ());
      DisplayStatus.setZoomLevel (location.getZoomLevel ());
      mMapView.removeMapListener (null);

      mMapView.getController ().setCenter (location.getPoint ());
      mMapView.getController ().setZoom (location.getZoomLevel ());
      DisplayStatus.calculatePositions ();
   }

   void setupLocationOverlay ()
   {
      List<Location> locations = new ArrayList<> (Settings.getLocationsCopy ());
      ItemizedIconOverlay<Location> mLocationOverlay = new ItemizedIconOverlay<> (locations,
            new ItemizedIconOverlay.OnItemGestureListener<> ()
            {
               @Override
               public boolean onItemSingleTapUp (final int index, final Location item)
               {
                  moveTo (item);
                  currentLocation = item;
                  return true;
               }

               @Override
               public boolean onItemLongPress (final int index, final Location item)
               {
                  NavController navController = Navigation.findNavController (binding.getRoot ());

                  MapDisplayFragmentDirections.ActionNavMapToEditLocationFragment action =
                        MapDisplayFragmentDirections.actionNavMapToEditLocationFragment (item.getUid ());
                  navController.navigate (action);

                  return true;
               }
            }, MainActivity.getContext ());
      mMapView.getOverlayManager ().add (mLocationOverlay);

      repeatativeTaskRunnable = new Runnable ()
      {
         public void run ()
         {
            if (DisplayStatus.useCurrentTime ())
            {
               ZoneId zi = DisplayStatus.getDisplayZoneId ();
               String displayedTime = ASTools.formatDateTime (ZonedDateTime.now (zi));
               if (!displayedTime.contentEquals (lightingDate.getText ()))
               {
                  onChange ();
               }
            }
            startHandler ();
         }
      };
   }

   void startHandler ()
   {
      taskHandler.postDelayed (repeatativeTaskRunnable, 800);
   }

   void stopHandler ()
   {
      taskHandler.removeCallbacks (repeatativeTaskRunnable);
   }

   @SuppressLint("DefaultLocale")
   private void updateTextDisplay ()
   {
      if (!getLifecycle ().getCurrentState ().isAtLeast (Lifecycle.State.STARTED))
         return;

      ZoneId displayZone = DisplayStatus.getDisplayZoneId ();

      if (MainActivity.timeZoneEngine != null && mProgressBar.getVisibility () != View.GONE)
         mProgressBar.setVisibility (View.GONE);

      ZonedDateTime displayTime = DisplayStatus.getTimeStamp ().withZoneSameLocal (displayZone);
      DisplayStatus.setTimeStamp (displayTime);

      lightingDate.setText (ASTools.formatDateTime (displayTime));


      // https://xkcd.com/2170/
      // 3 decimal - Specific cul-de-sac
      // 4 decimal - Particular corner of a house

      mTextLocation.setText (String.format ("%s %.2f", ASTools.formatGeoPoint (DisplayStatus.getLocation ()), DisplayStatus.getZoomLevel ()));
      mTextDebug.setText (String.format ("Astro run(skip) %d(%d), Light %d(%d) %d",
            DisplayStatus.calculations, DisplayStatus.astro_skipped, DisplayStatus.lighting_bitmap_created, DisplayStatus.lighting_bitmap_skipped, DisplayStatus.averageCalcTime ()));
      String zone = DisplayStatus.getDisplayZoneId ().toString ();
      final int maxZoneLength = 16;
      if (zone.length () > maxZoneLength)
         zone = zone.substring (0, maxZoneLength);
      mTimeZone.setText (zone);
      mSunRise.setText (String.format ("Rise: %s", ASTools.formatTime (DisplayStatus.getSunRise ())));
      mSunSet.setText (String.format ("Set: %s", ASTools.formatTime (DisplayStatus.getSunSet ())));
   }


   @Override
   public void onChange ()
   {
      Activity activity = getActivity ();
      if (activity != null)
      {
         activity.runOnUiThread (new Runnable ()
         {
            @Override
            public void run ()
            {
               updateTextDisplay ();
               mMapView.invalidate ();
            }
         });
      }
   }

   /** @noinspection unused*/
   public void onClick (View v)
   {
      final Location newLocation = new Location ("", "", mMapView.getMapCenter (), DisplayStatus.getTimeStamp (), DisplayStatus.useCurrentTime (), DisplayStatus.getZoomLevel ());

      Settings.addLocation (newLocation);
      NavController navController = Navigation.findNavController (binding.getRoot ());

      MapDisplayFragmentDirections.ActionNavMapToEditLocationFragment action =
            MapDisplayFragmentDirections.actionNavMapToEditLocationFragment (newLocation.getUid ());
      navController.navigate (action);
   }

   @Override
   public void onPause ()
   {
      stopHandler ();
      Settings.setTileProvider (mMapView.getTileProvider ().getTileSource ().name ());
      Settings.setMapOrientation (mMapView.getMapOrientation ());

      Settings.setMapCenter (DisplayStatus.getLocation (), DisplayStatus.getDisplayZoneId ());
      Settings.setZoomLevel (DisplayStatus.getZoomLevel ());

      Settings.saveToBackingStore ();
      DisplayStatus.removeListener (this);

      mMapView.onPause ();
      super.onPause ();
   }


   @Override
   public void onResume ()
   {
      super.onResume ();
      Tools.setReducedAccuracy (Settings.isLowAccuracy ());
      //mMapView.setTileSource (Settings.getTileProvider ());
      mMapView.setTileSource (TileSourceFactory.MAPNIK);

      mMapView.getController ().setZoom (DisplayStatus.getZoomLevel ());
      mMapView.getController ().setCenter (DisplayStatus.getLocation ());
      mMapView.onResume ();

      // Setting the map location triggers scroll events and kills
      // the ZoneId. Put it back to the correct value
      DisplayStatus.setLocation (DisplayStatus.getLocation (), Settings.getZoneId ());

      DisplayStatus.addListener (this);
      DisplayStatus.forceCalculation ();
      startHandler ();
   }

   @Override
   public void onDestroyView ()
   {
      super.onDestroyView ();
      //this part terminates all of the overlays and background threads for osmdroid
      //only needed when you programmatically create the map
      mMapView.onDetach ();
      binding = null;
   }
}