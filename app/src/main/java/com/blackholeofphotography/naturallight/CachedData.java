package com.blackholeofphotography.naturallight;
import android.annotation.SuppressLint;

import net.iakovlev.timeshape.TimeZoneEngine;

import org.slf4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CachedData
{
   private static final Logger logger = org.slf4j.LoggerFactory.getLogger (CachedData.class);
   static final String CACHE_FILE_REGEX = "TZ_(?<region>.*)_(?<version>[0-9]+)\\.cache";

   private static void removeFile (File f)
   {
      try
      {
         if (f.exists ())
         {
            boolean deleted = f.delete ();
            if (!deleted)
               logger.error ("unable to delete {}", f.getAbsolutePath ());
         }
      }
      catch (SecurityException ex)
      {
         logger.error ("Security error: ", ex);
      }
   }

   /**
    * Delete all the cached TimeShape serialization files.
    */
   public static void clearAllCache ()
   {
      Pattern cachePattern = Pattern.compile (CACHE_FILE_REGEX);

      final File outputDir = MainActivity.getContext ().getCacheDir (); // context being the Activity pointer
      final File[] cacheFiles = outputDir.listFiles (new FilenameFilter ()
      {
         @Override
         public boolean accept (File dir, String name)
         {
            Matcher matcher = cachePattern.matcher (name);
            return matcher.matches ();
         }
      });

      if (cacheFiles != null)
      {
         for (var f : cacheFiles)
            removeFile (f);
      }
   }

   /**
    * Delete cached files from older versions.
    */
   public static void clearObsoleteCache ()
   {
      Pattern cachePattern = Pattern.compile (CACHE_FILE_REGEX);

      final File outputDir = MainActivity.getContext ().getCacheDir(); // context being the Activity pointer
      final File[] cacheFiles = outputDir.listFiles (new FilenameFilter ()
      {
         @Override
         public boolean accept (File dir, String name)
         {
            Matcher matcher = cachePattern.matcher (name);
            return matcher.matches ();
         }
      });

      if (cacheFiles != null)
      {
         for (var f : cacheFiles)
         {
            Matcher matcher = cachePattern.matcher (f.getName ());
            if (matcher.matches ())
            {
               String version = matcher.group ("version");
               if (version != null)
               {
                  int v = Integer.parseInt (version);
                  if (v < BuildConfig.VERSION_CODE && v > 0)
                     removeFile (f);
               }
            }
         }
      }
   }

   /**
    * Get the current TimeShape data cached file. Note that it
    * may not exist yet, this is also used when saving a new one.
    * @return The current file.
    */

   private static File currentTimeShapeDataCacheFile ()
   {
      // /data/user/0/com.blackholeofphotography.naturallight/cache
      final File outputDir = MainActivity.getContext ().getCacheDir();
      @SuppressLint("DefaultLocale")
      final String fileName = String.format ("TZ_%s_%d.cache", Settings.getRegion (), BuildConfig.VERSION_CODE);
      final Path saved = Paths.get (outputDir.toString (), fileName);

      return saved.toFile ();
   }

   /**
    * Serializes an instance of {@link TimeZoneEngine} to a file
    * This is a blocking long-running operation.
    *
    * @param f Destination File.
    * @param engine Instance of TimeZoneEngine to serialize
    */

   public static void serialize (File f, TimeZoneEngine engine) throws IOException
   {
      FileOutputStream fileOutputStream = new FileOutputStream (f, false);
      try (ObjectOutputStream objectOutputStream = new ObjectOutputStream (new BufferedOutputStream (fileOutputStream))) {
         objectOutputStream.writeObject (engine);
         objectOutputStream.flush ();
      }
   }


   /**
    * Creates a new instance of {@link TimeZoneEngine} from previously serialized data.
    * This is a blocking long-running operation.
    *
    * @return an initialized instance of {@link TimeZoneEngine}
    */
   public static TimeZoneEngine deserialize (File f) throws IOException, ClassNotFoundException
   {
      FileInputStream fileInputStream = new FileInputStream(f);
      try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
         return (TimeZoneEngine) objectInputStream.readObject ();
      }
   }

   public static TimeZoneEngine loadTimeZoneEngine ()
   {
      File f = currentTimeShapeDataCacheFile ();
      TimeZoneEngine engine = null;
      if (f.exists ())
      {
         try
         {
            long s = System.currentTimeMillis ();
            engine = deserialize (f);
            long d = System.currentTimeMillis () - s;
            logger.info ("TimeZoneEngine.deSerialize took {}ms", d);
         }
         catch (IOException | ClassNotFoundException | ClassCastException e)
         {
            removeFile (f);
         }
      }

      return engine;
   }

   public static void saveTimeEngine (TimeZoneEngine engine)
   {
      if (engine != null)
      {
         File f = currentTimeShapeDataCacheFile ();
         try
         {
            removeFile (f);
            serialize (f, engine);
         }
         catch (IOException e)
         {
            logger.error ("Serializing TimeZoneEngine", e);
            removeFile (f);
         }
      }
   }
}

