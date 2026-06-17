package com.blackholeofphotography.naturallight.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.blackholeofphotography.astrocalc.Tools;
import com.blackholeofphotography.bhtools.ASTools;
import com.blackholeofphotography.naturallight.DisplayStatus;
import com.blackholeofphotography.naturallight.DisplayStatusListener;
import com.blackholeofphotography.naturallight.Location;
import com.blackholeofphotography.naturallight.MainActivity;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.Settings;
import com.blackholeofphotography.naturallight.databinding.MapFragmentBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidBitmap;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.android.view.MapView;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

// May need to move to MapsForge
public class MapDisplayFragment extends Fragment implements DisplayStatusListener
{
   @SuppressWarnings ("unused")
   private static final org.slf4j.Logger logger = LoggerFactory.getLogger (MapDisplayFragment.class);

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
   private FusedLocationProviderClient fusedLocationClient;
   protected final List<TileCache> tileCaches = new ArrayList<>();
   private TileDownloadLayer downloadLayer;
   private Context context;


   private final Handler taskHandler = new android.os.Handler (Looper.getMainLooper ());

   public View onCreateView (@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
   {
      context = getContext ();
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
      binding.jumpToLocation.setOnClickListener (this::toCurrentLocation);

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

      final Bundle arguments = getArguments ();
      if (arguments != null)
      {
         MapDisplayFragmentArgs args = MapDisplayFragmentArgs.fromBundle (arguments);
         if (args.getAUid () != null)
            currentLocation = Settings.getLocation (args.getAUid ());
      }

      if (currentLocation != null)
         moveTo (currentLocation);
      else
      {
       //  setCenter (DisplayStatus.getGeoPoint ());
         setZoom (Settings.getZoomLevel ());
         setCenter (Settings.getMapCenter ());
      }

      ViewCompat.setOnApplyWindowInsetsListener(binding.addLocation, (v, windowInsets) -> {
         Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
         // Apply the insets as a margin to the view. This solution sets only the
         // bottom, left, and right dimensions, but you can apply whichever insets are
         // appropriate to your layout. You can also update the view padding if that's
         // more appropriate.
         ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
         mlp.leftMargin = insets.left;
         mlp.bottomMargin = insets.bottom;
         mlp.rightMargin = insets.right;
         v.setLayoutParams(mlp);

         // Return CONSUMED if you don't want the window insets to keep passing
         // down to descendant views.
         return WindowInsetsCompat.CONSUMED;
      });

      fusedLocationClient = LocationServices.getFusedLocationProviderClient (MainActivity.getContext ());
      return root;
   }

   @SuppressLint("ClickableViewAccessibility")
   private void setupMap (Context ctx)
   {
      mMapView.getMapZoomControls ().setShowMapZoomControls (false);
      createTileCaches (ctx);
      createLayers ();

      binding.compass.setImageBitmap (CompassOverlay.getCompassFrameBitmap (ctx));
      mMapView.getModel ().mapViewPosition.addObserver (this::onMapViewChanged);

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

      lightingDate.setOnTouchListener (this::onDateTimeTouch);

      setupLocationOverlay ();
   }

   private float previousX;
   @SuppressWarnings("SameReturnValue")
   private boolean onDateTimeTouch (View ignored, MotionEvent event)
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
         DisplayStatus.calculatePositionsAsync ();

         previousX = event.getX ();
      }

      return true;
   }


   protected void createTileCaches (Context context)
   {
      this.tileCaches.add (AndroidUtil.createTileCache (context, this.getClass().getSimpleName(),
            mMapView.getModel ().displayModel.getTileSize (), 1.0F,
            mMapView.getModel ().frameBufferModel.getOverdrawFactor (), true));
   }

   protected void createLayers ()
   {
      OpenStreetMapMapnik tileSource = OpenStreetMapMapnik.INSTANCE;
      tileSource.setUserAgent ("LocationPlayback/1.0 (contact: https://github.com/PhotoKevin)");
      this.downloadLayer = new TileDownloadLayer (this.tileCaches.get (0),
            mMapView.getModel ().mapViewPosition, tileSource,
            AndroidGraphicFactory.INSTANCE)
      {
         public boolean onLongPress(LatLong tapLatLong, Point thisXY, Point tapXY)
         {
            //onLongMapClick (tapLatLong);
            return true;
         }
      };
      mMapView.getLayerManager ().getLayers ().add (this.downloadLayer);

      mMapView.setZoomLevelMin (tileSource.getZoomLevelMin ());
      mMapView.setZoomLevelMax (tileSource.getZoomLevelMax ());
   }



   private void moveTo (Location location)
   {
      logger.debug ("moveTo: {}, {} Current: {}", location.getPoint ().getLatitude (), location.getPoint ().getLongitude (), location.getUseCurrentTime ());
      DisplayStatus.setGeoPoint (location.getPoint ());
      DisplayStatus.setZoomLevel (location.getZoomLevel ());
      //mMapView.removeMapListener (null);

      setCenter (location.getPoint ());
      setZoom (location.getZoomLevel ());

      DisplayStatus.setUseCurrentTime (location.getUseCurrentTime ());
      if (!location.getUseCurrentTime ())
      {
         logger.info ("set time: {}}", ASTools.formatDateTime (location.getDateTime ()));
         DisplayStatus.setTimeStamp (location.getDateTime ());
      }

      DisplayStatus.calculatePositionsAsync ();
   }



   void moveTo (android.location.Location location)
   {
      logger.debug ("moveToX: {}, {}", location.getLatitude (), location.getLongitude ());
      LatLong point = new LatLong (location.getLatitude (), location.getLongitude ());
      DisplayStatus.setGeoPoint (point);
      DisplayStatus.setZoomLevel (Settings.getDefaultZoomLevel ());
      //mMapView.removeMapListener (null);

      setCenter (point);
      setZoom (Settings.getDefaultZoomLevel ());

      DisplayStatus.calculatePositionsAsync ();
   }


   private final List<Layer> favoritesLayer = new ArrayList<> ();

   void setupLocationOverlay ()
   {
      while (!favoritesLayer.isEmpty ())
      {
         Layer l = favoritesLayer.get (0);
         mMapView.getLayerManager ().getLayers ().remove (l);
         favoritesLayer.remove (0);
      }

      final Drawable drawableFavorite = ContextCompat.getDrawable (context, R.drawable.map_pin_heart_48px);
      Bitmap bitmapFavorite = new AndroidBitmap (drawableToBitmap (drawableFavorite));

      List<Location> locations = new ArrayList<> (Settings.getLocationsCopy ());
      for (Location l : locations)
      {
         Marker marker = new FavoriteMarker (l, bitmapFavorite);
         favoritesLayer.add (marker);
      }
      mMapView.getLayerManager ().getLayers ().addAll (favoritesLayer);

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

   public static android.graphics.Bitmap drawableToBitmap (Drawable drawable)
   {
      if (drawable instanceof BitmapDrawable)
         return ((BitmapDrawable) drawable).getBitmap ();

      final int width = Math.min (drawable.getIntrinsicWidth (), 35);
      int height = Math.min (drawable.getIntrinsicHeight (), 35);

      android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap (width, height, android.graphics.Bitmap.Config.ARGB_8888);
      android.graphics.Canvas canvas = new android.graphics.Canvas (bitmap);
      drawable.setBounds (0, 0, canvas.getWidth (), canvas.getHeight ());
      drawable.draw (canvas);

      return bitmap;
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
      if (!getLifecycle ().getCurrentState ().isAtLeast (Lifecycle.State.RESUMED))
         return;

      if (!ASTools.isRunningOnMainThread ())
      {
         new Handler (Looper.getMainLooper ()).post (this::updateTextDisplay);
         return;
      }

      ZoneId displayZone = DisplayStatus.getDisplayZoneId ();

      if (MainActivity.timeZoneEngine != null && mProgressBar.getVisibility () != View.GONE)
         mProgressBar.setVisibility (View.GONE);

      ZonedDateTime displayTime = DisplayStatus.getTimeStamp ().withZoneSameLocal (displayZone);
      DisplayStatus.setTimeStamp (displayTime);

      lightingDate.setText (ASTools.formatDateTime (displayTime));


      // https://xkcd.com/2170/
      // 3 decimal - Specific cul-de-sac
      // 4 decimal - Particular corner of a house

      mTextLocation.setText (String.format ("%s %.2f", Location.formatGeoPoint (DisplayStatus.getGeoPoint ()), DisplayStatus.getZoomLevel ()));
      mTextDebug.setText (String.format ("Astro run(skip) %d(%d), Light %d %d",
            DisplayStatus.calculations, DisplayStatus.astro_skipped, DisplayStatus.lighting_bitmap_created, DisplayStatus.averageCalcTime ()));
      String zone = DisplayStatus.getDisplayZoneId ().toString ();
      mTimeZone.setText (zone);
      mSunRise.setText (String.format ("Rise: %s", ASTools.formatTime (DisplayStatus.getSunRise ())));
      mSunSet.setText (String.format ("Set: %s", ASTools.formatTime (DisplayStatus.getSunSet ())));
   }

   public void updateMapDisplay ()
   {
      if (!ASTools.isRunningOnMainThread ())
      {
         new Handler (Looper.getMainLooper ()).post (this::updateMapDisplay);
         return;
      }

      if (getLifecycle().getCurrentState().isAtLeast (Lifecycle.State.RESUMED))
      {
//         DisplayStatus.setGeoPoint (mMapView.getModel ().mapViewPosition.getCenter ());
//         DisplayStatus.setZoomLevel (mMapView.getModel ().mapViewPosition.getZoom ());

         binding.riseSet.setImageBitmap (LightingOverlay.getmSunMoonBitmap (context));
         mMapView.repaint ();
      }
   }

   public void onMapViewChanged ()
   {
      LatLong center = mMapView.getModel ().mapViewPosition.getCenter ();
      DisplayStatus.setGeoPoint (center);
   }

   @Override
   public void onChange ()
   {

      if (getLifecycle ().getCurrentState ().isAtLeast (Lifecycle.State.RESUMED))
      {
         updateTextDisplay ();
         updateMapDisplay ();
      }
   }


   public void onClick (View ignored)
   {
      final Location newLocation = new Location ("", "", mMapView.getModel ().mapViewPosition.getCenter (), DisplayStatus.getTimeStamp (), DisplayStatus.useCurrentTime (), DisplayStatus.getZoomLevel ());

      Settings.addLocation (newLocation);
      NavController navController = Navigation.findNavController (binding.getRoot ());

      MapDisplayFragmentDirections.ActionNavMapToEditLocationFragment action =
            MapDisplayFragmentDirections.actionNavMapToEditLocationFragment (newLocation.getUid ());
      navController.navigate (action);
   }

   private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
         new ActivityResultContracts.RequestPermission(),
         new ActivityResultCallback<> ()
         {
            @Override
            public void onActivityResult(Boolean result)
            {
               if (result)
                  toCurrentLocation (null);
            }
         }
   );

   public void toCurrentLocation (View ignored)
   {

      if (ActivityCompat.checkSelfPermission (MainActivity.getContext (), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission (MainActivity.getContext (), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
      {
         requestPermissionLauncher.launch (Manifest.permission.ACCESS_FINE_LOCATION);
         return;
      }

      fusedLocationClient.getLastLocation ()
            .addOnSuccessListener (new OnSuccessListener<> ()
            {
               @Override
               public void onSuccess (android.location.Location location)
               {
                  if (location != null)
                     moveTo (location);
               }
            });

   }

   @Override
   public void onPause ()
   {
      stopHandler ();
//      Settings.setTileProvider (mMapView.getTileProvider ().getTileSource ().name ());
//      Settings.setMapOrientation (mMapView.getMapOrientation ());

      Settings.setMapCenter (DisplayStatus.getGeoPoint (), DisplayStatus.getDisplayZoneId ());
      Settings.setZoomLevel (DisplayStatus.getZoomLevel ());

      Settings.saveToBackingStore ();
      DisplayStatus.removeListener (this);

      this.downloadLayer.onPause ();
      super.onPause ();
   }


   @Override
   public void onResume ()
   {
      super.onResume ();
      Tools.setReducedAccuracy (Settings.isLowAccuracy ());
      downloadLayer.onResume ();

      DisplayStatus.addListener (this);
      DisplayStatus.forceCalculation ();
      startHandler ();
      onChange ();
   }

   @Override
   public void onDestroyView ()
   {
      super.onDestroyView ();
      //this part terminates all the overlays and background threads for osmdroid
      //only needed when you programmatically create the map
      //mMapView.onDetach ();
      binding = null;
   }

   private void setCenter (LatLong center)
   {
      mMapView.getModel ().mapViewPosition.setCenter (center);
   }

   private void setZoom (double zoom)
   {
      if (zoom == 0.0)
         logger.error ("Zoom is zero");

      mMapView.getModel ().mapViewPosition.setZoom (zoom);
   }

   private class FavoriteMarker extends Marker
   {
      private final Location mLocation;
      public FavoriteMarker (@NonNull Location location, Bitmap bitmap)
      {
         super (location.getPoint (), bitmap, 0, 0);
         mLocation = location;
      }

      @Override
      public boolean onTap (LatLong tapLatLong, Point layerXY, Point tapXY)
      {
         boolean good = this.contains (layerXY, tapXY, mMapView);
         if (good)
            moveTo (mLocation);
         return good;
      }

      @Override
      public boolean onLongPress (LatLong tapLatLong, Point layerXY, Point tapXY)
      {
         boolean good = this.contains (layerXY, tapXY, mMapView);
         if (good)
         {
            NavController navController = Navigation.findNavController (binding.getRoot ());

            MapDisplayFragmentDirections.ActionNavMapToEditLocationFragment action =
                  MapDisplayFragmentDirections.actionNavMapToEditLocationFragment (mLocation.getUid ());

            navController.navigate (action);
         }

         return good;
      }
   }

}