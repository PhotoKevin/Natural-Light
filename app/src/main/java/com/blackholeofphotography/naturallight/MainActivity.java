package com.blackholeofphotography.naturallight;

import android.content.Context;
import android.content.DialogInterface;
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
import com.blackholeofphotography.bhtools.ASTools;
import com.blackholeofphotography.bhtools.CrashLogger;
import com.blackholeofphotography.naturallight.databinding.ActivityMainBinding;
import com.github.luben.zstd.ZstdInputStream;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import net.iakovlev.timeshape.TimeZoneEngine;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.mapsforge.core.model.LatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity
{
   private static Logger logger;

   private AppBarConfiguration mAppBarConfiguration;
   private static Context mApplicationContext;
   private static boolean mTimeZoneDataDownloaded = false;

   public static Context getContext ()
   {
      return mApplicationContext;
   }
   NetworkTransport networkTransport;

   public static TimeZoneEngine timeZoneEngine;
   public static PlanetaryRegions planetaryRegions;

   @Override
   protected void onCreate (Bundle savedInstanceState)
   {
      super.onCreate (savedInstanceState);
//      if (Build.HARDWARE.contains ("ranchuX"))
//         disableLogToDownload ();

      final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler ();
      if (!ASTools.isEmulator ())
      {
         Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler ()
         {
            @Override
            public void uncaughtException (@NonNull Thread paramThread, @NonNull Throwable paramThrowable)
            {
               logger.error ("Global catch: ", paramThrowable);
               CrashLogger.logException (paramThrowable);
               if (oldHandler != null)
                  oldHandler.uncaughtException (paramThread, paramThrowable); //Delegates to Android's error handling
               else
                  System.exit (1);
            }
         });
      }
      logger = LoggerFactory.getLogger (MainActivity.class);

      if (ASTools.isEmulator ())
         ASTools.setupStrictMode ();

      EdgeToEdge.enable (this);
      mApplicationContext = getApplicationContext ();
      Settings.loadFromBackingStore ();
      DisplayStatus.setGeoPoint (Settings.getMapCenter (), Settings.getZoneId ());
      DisplayStatus.setZoomLevel (Settings.getZoomLevel ());
      if (Settings.useCurrentTime ())
         DisplayStatus.setTimeStamp (ZonedDateTime.now ());
      else
         DisplayStatus.setTimeStamp (Settings.getDisplayTime ());
      DisplayStatus.setUseCurrentTime (Settings.useCurrentTime ());

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

      networkTransport = new NetworkTransport (getApplicationContext ());
      CachedData.clearObsoleteCache ();

//      Settings.fakeAsMobile (true);
//      Settings.allowMobileDataUsage (true);
//      Settings.setMobileUsageRequested (false);
      File timeShapeDataFile = DataFetcher.getLocalTimeShapeDataFile ();
      if (timeShapeDataFile == null)
      {
         Settings.setLastDataCheck (Settings.earliest ());
         promptToDownload ();
      }

      loadTimeZoneEngine ();
   }

   public static void reloadTimeEngine ()
   {
      timeZoneEngine = null;
      loadTimeZoneEngine ();
   }

   private static Semaphore semaphore = new Semaphore(1);

   private static void loadTimeZoneEngine ()
   {
      Thread background = new Thread (null, null, "loader", 30 * 1024)
      {
         @Override
         public void run ()
         {
            try
            {
               semaphore.acquire ();

               if (planetaryRegions == null)
                  planetaryRegions = PlanetaryRegions.loadRegions ();

               updateTimeShapeDataFile ();

               String regionName = Settings.getRegion ();
               var region = planetaryRegions.getRegion (regionName);
               if (region == null)
               {
                  regionName = "ALL";
                  Settings.setRegion (regionName);
               }
               final Set<ZoneId> zoneIds = regionName.equals ("ALL") ? getAllZoneIds () : region.getZoneIds ();

               File timeShapeDataFile = DataFetcher.getLocalTimeShapeDataFile ();
               mTimeZoneDataDownloaded = timeShapeDataFile != null;
               if (timeShapeDataFile != null)
                  logger.info (timeShapeDataFile.getName ());
               // Load the cached (serialized) engine data, if it exists.
               timeZoneEngine = CachedData.loadTimeZoneEngine ();
               if (timeZoneEngine == null && timeShapeDataFile != null)
               {
                  // No cached data, do it the slow way.
                  long start = System.currentTimeMillis ();

                  try (InputStream resourceAsStream = timeShapeDataFile.toURI ().toURL ().openStream ();
                       ZstdInputStream unzipStream = new ZstdInputStream (resourceAsStream);
                       BufferedInputStream bufferedStream = new BufferedInputStream (unzipStream);
                       TarArchiveInputStream shapeInputStream = new TarArchiveInputStream (bufferedStream))
                  {
                     timeZoneEngine = TimeZoneEngine.initialize (zoneIds, false, TimeShapeFile.getAreas (timeShapeDataFile), shapeInputStream);
                  }
                  catch (IOException | OutOfMemoryError ex)
                  {
                     timeZoneEngine = null;
                     logger.error ("Can't initialize TimeZoneEngine: ", ex);
                  }

                  long delta = System.currentTimeMillis () - start;
                  logger.info ("TimeZoneEngine.initialize took {}ms", delta);
                  if (!regionName.equals ("ALL"))
                     CachedData.saveTimeEngine (timeZoneEngine);
               }

               ZoneId tz = getZoneId (DisplayStatus.getGeoPoint ());
               DisplayStatus.setGeoPoint (DisplayStatus.getGeoPoint (), tz);
               DisplayStatus.forceCalculation ();
            }
            catch (InterruptedException e)
            {
               Thread.currentThread().interrupt();
             }
            finally
            {
               semaphore.release ();
            }
         }
      };

      background.start ();
   }

   private static void updateTimeShapeDataFile ()
   {
      File dataFile = DataFetcher.getLocalTimeShapeDataFile ();
      String version = "";
      if (dataFile != null)
         version = TimeShapeFile.getVersion (dataFile);

      DataFetcher.getNewerTimeShapeData (version);
   }

   private static Set<ZoneId> getAllZoneIds ()
   {
      Set<ZoneId> zoneIds = new HashSet<> ();
      for (var z : ZoneId.getAvailableZoneIds ())
         zoneIds.add (ZoneId.of (z));

      return zoneIds;
   }


   /**
    * Get the ZoneId for the specified location
    *
    * @param latitude  Latitude of interest
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
    *
    * @param pt GeoPoint of interest
    * @return ZoneId for location, or UTC if it can't be figured out.
    */
   public static ZoneId getZoneId (LatLong pt)
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

   public void onResume ()
   {
      super.onResume ();
      if (networkTransport == null)
         networkTransport = new NetworkTransport (getApplicationContext ());

      Tools.setReducedAccuracy (Settings.isLowAccuracy ());
      DisplayStatus.setGeoPoint (Settings.getMapCenter (), Settings.getZoneId ());
      DisplayStatus.setZoomLevel (Settings.getZoomLevel ());
      DisplayStatus.setTimeStamp (Settings.getDisplayTime ());
      DisplayStatus.setTimeStamp (Settings.getDisplayTime ());
   }

   public void onPause ()
   {
      super.onPause ();
      DisplayStatus.removeAllListeners ();
      logger.trace ("onPause");

      if (networkTransport != null)
         networkTransport.shutdown ();
      networkTransport = null;

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
            setOptionMenuVisibility (true);
            return super.onOptionsItemSelected (item);
         }

         return true;
      }
      catch (Exception e)
      {
//         Log.e (LOG_TAG, "onOptionsItemSelected", e);
      }

      return super.onOptionsItemSelected (item);
   }

   private void setOptionMenuVisibility (boolean visibility)
   {
      Toolbar toolbar = findViewById (R.id.toolbar);
      toolbar.getMenu ().setGroupVisible (0, visibility);
   }

   public static boolean isTimeZoneDataDownloaded ()
   {
      return mTimeZoneDataDownloaded;
   }

   private void promptToDownload ()
   {
      if (!DataFetcher.okToDownload () && !Settings.mobileUsageRequested ())
      {
         MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder (this);
         b.setTitle ("May I download the timezone boundaries on WiFi?")
               .setPositiveButton ("OK", this::downloadTimeShapeData)
               .setNegativeButton ("No", null)
               .setCancelable (false)
               .create ().show ();
         Settings.setMobileUsageRequested (true);
      }
   }

   private void downloadTimeShapeData (DialogInterface dialogInterface, int i)
   {
      Settings.allowMobileDataUsage (true);
      loadTimeZoneEngine ();
   }

}