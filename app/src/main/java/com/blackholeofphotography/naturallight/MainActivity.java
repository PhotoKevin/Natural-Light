package com.blackholeofphotography.naturallight;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.blackholeofphotography.astrocalc.Tools;
import com.blackholeofphotography.naturallight.databinding.ActivityMainBinding;
import net.iakovlev.timeshape.TimeZoneEngine;
import com.google.android.material.navigation.NavigationView;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
   //private static final String LOG_TAG = "MainActivity";
   private AppBarConfiguration mAppBarConfiguration;

   private static Context _ApplicationContext;


   public static Context getContext ()
   {
      return _ApplicationContext;
   }

   public static TimeZoneEngine timeZoneEngine;
   static TimeZoneAreas timeZoneAreas;
   public static PlanetaryRegions planetaryRegions;
   private static org.slf4j.Logger log;
   @Override
   protected void onCreate (Bundle savedInstanceState)
   {
      EdgeToEdge.enable(this);
      _ApplicationContext = getApplicationContext ();
      Settings.loadFromBackingStore ();
      DisplayStatus.setGeoPoint (Settings.getMapCenter (), Settings.getZoneId ());
      DisplayStatus.setZoomLevel (Settings.getZoomLevel ());
      if (Settings.useCurrentTime ())
         DisplayStatus.setTimeStamp (ZonedDateTime.now ());
      else
         DisplayStatus.setTimeStamp (Settings.getDisplayTime ());
      DisplayStatus.setUseCurrentTime (Settings.useCurrentTime ());
      super.onCreate (savedInstanceState);

      log = LoggerFactory.getLogger (MainActivity.class);
      log.info ("{}", "hello world");

      //int apiKey = BuildConfig.apiKey;

      String apiKey;

      try
      {
         PackageManager packageManager = getPackageManager ();
         String me = getPackageName ();
         ApplicationInfo applicationInfo = packageManager.getApplicationInfo (me, PackageManager.GET_META_DATA);
         apiKey = applicationInfo.metaData.getString ("com.facebook.sdk.API_KEY");
         log.info (apiKey);
      }
      catch (PackageManager.NameNotFoundException e)
      {
         throw new RuntimeException (e);
      }
      ActivityMainBinding binding = ActivityMainBinding.inflate (getLayoutInflater ());
      setContentView (binding.getRoot ());

      setSupportActionBar (binding.appBarMain.toolbar);

      DrawerLayout drawer = binding.drawerLayout;
      NavigationView navigationView = binding.navView;
      // Passing each menu ID as a set of Ids because each
      // menu should be considered as top level destinations.
      mAppBarConfiguration = new AppBarConfiguration.Builder (
              R.id.nav_map, R.id.nav_detail, R.id.nav_locations)
              .setOpenableLayout (drawer)
              .build ();
      NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment_content_main);
      NavigationUI.setupActionBarWithNavController (this, navController, mAppBarConfiguration);
      NavigationUI.setupWithNavController (navigationView, navController);


      // https://developers.google.com/maps/documentation/android-sdk/overview
      //https://osmdroid.github.io/osmdroid/How-to-use-the-osmdroid-library.html
      Context ctx = getApplicationContext();
      Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));
      loadTimeZoneEngine ();

      //AppCompatDelegate.setDefaultNightMode (AppCompatDelegate.MODE_NIGHT_YES);

      //noinspection RedundantIfStatement
      if (BuildConfig.DEBUG)
         Settings.setEnableAdvancedSettings (true);
      else
         Settings.setEnableAdvancedSettings (false);
   }





   public static void reloadTimeEngine ()
   {
      timeZoneEngine = null;
      loadTimeZoneEngine ();
   }

   private static void loadTimeAreas ()
   {
      timeZoneAreas = CachedData.loadTimeAreas ();

      if (timeZoneAreas == null)
      {
         timeZoneAreas = TimeZoneAreas.initialize ();
         CachedData.saveTimeAreas (timeZoneAreas);
      }
   }

   private static void loadTimeZoneEngine ()
   {
      Thread background = new Thread (null, null, "loader", 20*1024)
      {
         @Override
         public void run ()
         {
            if (planetaryRegions == null)
               planetaryRegions = PlanetaryRegions.loadRegions ();

            if (timeZoneAreas == null)
               loadTimeAreas ();

            String regionName = Settings.getRegion ();
            var region = planetaryRegions.getRegion (regionName);
            final BoundingBox boundingBox = regionName.equals ("ALL") ? null : region.getBigBoundingBox ();

            timeZoneEngine = CachedData.loadTimeZoneEngine ();
            if (timeZoneEngine == null)
            {
               long start = System.currentTimeMillis ();

               if (boundingBox == null)
                  timeZoneEngine = TimeZoneEngine.initialize (false);
               else
                  timeZoneEngine = TimeZoneEngine.initialize (boundingBox.getMinLatitude ()-1, boundingBox.getMinLongitude ()-1,
                        boundingBox.getMaxLatitude ()+1, boundingBox.getMaxLongitude ()+1, false);

               long delta = System.currentTimeMillis () - start;
               log.info ("TimeZoneEngine.initialize took {}ms", delta);
               if (! regionName.equals ("ALL"))
                  CachedData.saveTimeEngine (timeZoneEngine);
            }
            DisplayStatus.setGeoPoint (DisplayStatus.getGeoPoint ());
            DisplayStatus.forceCalculation ();
         }
      };

      background.start ();
   }

   /**
    * Get the ZoneId for the specified location
    * @param latitude Latitude of interest
    * @param longitude Longitude of interest
    * @return ZoneId for location, or UTC if it can't be figured out.
    */
   public static ZoneId getZoneId (double latitude, double longitude)
   {
      final ZoneId utc = ZoneId.of ("UTC");
      if (MainActivity.timeZoneEngine == null)
         return utc;

      final List<ZoneId> zoneIds = MainActivity.timeZoneEngine.queryAll (latitude, longitude);
      if (!zoneIds.isEmpty ())
         return zoneIds.get (0);

      return utc;
   }


   /**
    * Get the ZoneId for the specified location
    * @param pt GeoPoint of interest
    * @return ZoneId for location, or UTC if it can't be figured out.
    */
   public static ZoneId getZoneId (IGeoPoint pt)
   {
      return getZoneId (pt.getLatitude (), pt.getLongitude ());
   }

   @Override
   public boolean onCreateOptionsMenu (Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater ().inflate (R.menu.main, menu);
      return true;
   }

   @Override
   public boolean onSupportNavigateUp ()
   {
      setOptionMenuVisibility (true);

      // Without this, the menu/back button doesn't work
      NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment_content_main);
      return NavigationUI.navigateUp (navController, mAppBarConfiguration)
              || super.onSupportNavigateUp ();
   }

   public void onResume()
   {
      super.onResume();
      Tools.setReducedAccuracy (Settings.isLowAccuracy ());
      DisplayStatus.setGeoPoint (Settings.getMapCenter (), Settings.getZoneId ());
      DisplayStatus.setZoomLevel (Settings.getZoomLevel ());
      DisplayStatus.setTimeStamp (Settings.getDisplayTime ());
      DisplayStatus.setTimeStamp (Settings.getDisplayTime ());

      //DisplayStatus.forceCalculation ();
   }

   public void onPause()
   {
      super.onPause();
      DisplayStatus.removeAllListeners ();
      log.trace ("onPause");

      Settings.setMapCenter (DisplayStatus.getGeoPoint (), DisplayStatus.getDisplayZoneId ());
      Settings.setZoomLevel (DisplayStatus.getZoomLevel ());
      Settings.setDisplayTime (DisplayStatus.getTimeStamp ());
      Settings.setUseCurrentTime (DisplayStatus.useCurrentTime ());
      Settings.saveToBackingStore ();
   }

   @Override
   public boolean onPrepareOptionsMenu (Menu menu)
   {
      return true;
   }


   @Override
   public boolean onOptionsItemSelected (@NonNull MenuItem item)
   {
      try
      {
         NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment_content_main);
         int itemId = item.getItemId ();
         if (itemId == R.id.action_settings)
         {
            setOptionMenuVisibility (false);
            navController.navigate (R.id.nav_settings, null);
         }
         else if (itemId == R.id.action_import_export)
         {
            setOptionMenuVisibility (false);
            navController.navigate (R.id.nav_import_export);
         }
         else if (itemId == R.id.action_about)
         {
            setOptionMenuVisibility (false);
            navController.navigate (R.id.nav_about);
         }
         else
         {
            return super.onOptionsItemSelected (item);
         }

         return true;
      }
      catch (Exception e)
      {
//         Log.e (LOG_TAG, "onOptionsItemSelected", e);
      }

      return super.onOptionsItemSelected(item);
   }

   private void setOptionMenuVisibility (boolean visibility)
   {
      Toolbar toolbar = findViewById (R.id.toolbar);
      toolbar.getMenu().setGroupVisible (0, visibility);
   }


}