package com.blackholeofphotography.naturallight;
import android.annotation.SuppressLint;

import com.blackholeofphotography.timeshape.TimeZoneEngine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CachedData
{
   private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger (CachedData.class);

   private static void removeFile (File f)
   {
      if (f.exists ())
      {
         boolean deleted = f.delete ();
         if (!deleted)
            log.error ("unable to delete {}", f.getAbsolutePath ());
      }

   }
   private static File timeZoneEngineCacheFile ()
   {
      // /data/user/0/com.blackholeofphotography.naturallight/cache
      final File outputDir = MainActivity.getContext ().getCacheDir(); // context being the Activity pointer
      @SuppressLint("DefaultLocale")
      final String fileName = String.format ("TZ_%s_%d.cache", Settings.getRegion (), BuildConfig.VERSION_CODE);
      final Path saved = Paths.get (outputDir.toString (), fileName);

      return saved.toFile ();
   }

   private static File timeZoneAreasCacheFile ()
   {
      final File outputDir = MainActivity.getContext ().getCacheDir(); // context being the Activity pointer
      @SuppressLint("DefaultLocale")
      final String fileName = String.format ("TZAreas_%d.cache", BuildConfig.VERSION_CODE);
      final Path saved = Paths.get (outputDir.toString (), fileName);

      return saved.toFile ();
   }


   /**
    * Serializes an instance of {@link TimeZoneEngine} to a file
    * This is a blocking long running operation.
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
    * This is a blocking long running operation.
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
      File f = timeZoneEngineCacheFile ();
      TimeZoneEngine engine = null;
      if (f.exists ())
      {
         try
         {
            long s = System.currentTimeMillis ();
            engine = deserialize (f);
            long d = System.currentTimeMillis () - s;
            log.info ("TimeZoneEngine.deSerialize took {}ms", d);
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
      File f = timeZoneEngineCacheFile ();
      try
      {
         removeFile (f);
         serialize (f, engine);
      }
      catch (IOException e)
      {
         log.error ("Serializing TimeZoneEngine", e);
         removeFile (f);
      }
   }


   public static TimeZoneAreas loadTimeAreas ()
   {
      TimeZoneAreas timeZoneAreas;
      File f = CachedData.timeZoneAreasCacheFile ();
      return TimeZoneAreas.deSerialize (f);
   }

   public static void saveTimeAreas (TimeZoneAreas areas)
   {
      File f = CachedData.timeZoneAreasCacheFile ();
      try
      {
         removeFile (f);
         areas.serialize (f);
      }
      catch (IOException e)
      {
         removeFile (f);
      }
   }
}

