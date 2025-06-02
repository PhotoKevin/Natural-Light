package com.blackholeofphotography.naturallight;

import androidx.annotation.Nullable;

import net.iakovlev.timeshape.TimeZoneEngine;
import com.github.luben.zstd.ZstdInputStream;

import net.iakovlev.timeshape.proto.Geojson;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TimeZoneAreas
{
   final private List<TimeZoneArea> mTimeZoneAreas;


   public TimeZoneAreas (List<TimeZoneArea> aTimeZoneAreas)
   {
      mTimeZoneAreas = aTimeZoneAreas;
   }
   final private static org.slf4j.Logger log = LoggerFactory.getLogger (TimeZoneAreas.class);

   public static TimeZoneAreas initialize ()
   {
      long s = System.currentTimeMillis ();
      ArrayList<TimeZoneArea> zones = new ArrayList<> ();
      try (ZstdInputStream unzipStream = new ZstdInputStream (TimeZoneEngine.class.getResourceAsStream ("/data.tar.zstd")))
      {
         try (BufferedInputStream bufferedStream = new BufferedInputStream (unzipStream))
         {
            try (TarArchiveInputStream shapeInputStream = new TarArchiveInputStream (bufferedStream))
            {
               TarArchiveEntry entry;
               while ((entry = shapeInputStream.getNextEntry ()) != null)
               {
                  if (entry.isFile ())
                  {
                     byte[] content = new byte[(int) entry.getSize ()];
                     final int read = shapeInputStream.read (content);
                     if (read != entry.getSize ())
                        log.error ("{} != {}", read, entry.getSize ());
                     final Geojson.Feature f = Geojson.Feature.parseFrom (content);
                     final Geojson.Geometry geometry = f.getGeometry ();
                     List<Geojson.Polygon> polygons;
                     if (geometry.hasMultiPolygon ())
                     {
                        final Geojson.MultiPolygon multiPolygon = geometry.getMultiPolygon ();
                        polygons = multiPolygon.getCoordinatesList ();
                     }
                     else
                     {
                        polygons = new ArrayList<> ();
                        polygons.add (geometry.getPolygon ());
                     }

                     final BoundingBox box = getBoundingBox (polygons);
                     log.trace ("{}{}", entry.getName (), box);
                     zones.add (new TimeZoneArea (entry.getName (), box));
                  }
               }
            }
         }
      }

      catch (NullPointerException | IOException e)
      {
         log.error ("Unable to read resource file", e);
         throw new RuntimeException (e);
      }

      long d = System.currentTimeMillis () - s;
      log.error ("TimeZoneAreas.initialize took {}ms", d);
      return new TimeZoneAreas (zones);
   }

   @Nullable
   private static BoundingBox getBoundingBox (List<Geojson.Polygon> polygons)
   {
      BoundingBox box = null;
      for (var polygon : polygons)
      {
         final List<Geojson.LineString> coordinatesList = polygon.getCoordinatesList ();
         for (Geojson.LineString ll : coordinatesList)
         {
            for (var l : ll.getCoordinatesList ())
            {
               if (box == null)
                  box = new BoundingBox (l.getLat (), l.getLon (), l.getLat (), l.getLon ());
               box = box.include (l.getLat (), l.getLon ());
            }
         }
      }
      return box;
   }


   public void serialize (File f) throws IOException
   {
      FileOutputStream fileOutputStream = new FileOutputStream (f, false);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream (new BufferedOutputStream (fileOutputStream));
      objectOutputStream.writeObject (mTimeZoneAreas);
      objectOutputStream.flush ();
      objectOutputStream.close ();
   }


   @SuppressWarnings ("unchecked")
   public static TimeZoneAreas deSerialize (File f)
   {
      try
      {
         if (f.exists ())
         {
            FileInputStream fileInputStream = new FileInputStream (f);
            ObjectInputStream objectInputStream = new ObjectInputStream (fileInputStream);
            ArrayList<TimeZoneArea> entries = (ArrayList<TimeZoneArea>) objectInputStream.readObject ();
            objectInputStream.close ();
            return new TimeZoneAreas (entries);
         }
      }
      catch (IOException | ClassNotFoundException e)
      {
         log.error ("TimeZoneAreas.deSerialize", e);
      }

      return null;
   }

   public BoundingBox getBoundingBox (String region)
   {
      for (var area : mTimeZoneAreas)
         if (area.getTimeZoneName ().equals (region))
            return area.getBoundingBox ();

      return null;
   }
}
