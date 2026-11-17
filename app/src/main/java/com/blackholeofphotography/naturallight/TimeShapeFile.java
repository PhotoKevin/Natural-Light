package com.blackholeofphotography.naturallight;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeShapeFile
{
   private static final String FILE_PATTERN = "TimeShapeData-(?<version>[0-9]{4}[a-z])-(?<areas>[0-9]+)\\.tar\\.zstd";

   private final String mFilename;
   private final byte[] mContent;
   private final int mAreas;
   private final String mVersion;

   public TimeShapeFile (String aFilename, byte[] aContent)
   {
      mFilename = aFilename;
      mContent = aContent;
      int areas = 0;
      String version = null;

      Pattern pattern = Pattern.compile (FILE_PATTERN);
      Matcher matcher = pattern.matcher (aFilename);
      if (matcher.matches ())
      {
         String s = matcher.group ("areas");
         areas = (s == null) ? 0 : Integer.parseInt (s);
         version = matcher.group ("version");
      }
      mAreas = areas;
      mVersion = version;
   }

   public static boolean isTimeShapeFile (File f)
   {
      Pattern pattern = Pattern.compile (FILE_PATTERN);
      Matcher matcher = pattern.matcher (f.getName ());
      return matcher.matches ();
   }

   public static String getVersion (File f)
   {
      if (isTimeShapeFile (f))
      {
         TimeShapeFile tsf = new TimeShapeFile (f.getName (), null);
         return tsf.getVersion ();
      }
      return null;
   }

   public static int getAreas (File f)
   {
      if (isTimeShapeFile (f))
      {
         TimeShapeFile tsf = new TimeShapeFile (f.getName (), null);
         return tsf.getAreas ();
      }
      return 0;
   }


   public static File newerVersion (File f0, File f1)
   {
      String v0 = getVersion (f0);
      String v1 = getVersion (f1);
      if (v0 == null)
         return f1;
      else if (v1 == null)
         return f0;

      if (v1.compareTo (v0) > 0)
         return f1;

      return f0;
   }

   public int getAreas ()
   {
      return mAreas;
   }

   public String getVersion ()
   {
      return mVersion;
   }

   public byte[] getContent ()
   {
      return mContent;
   }

   public String getFilename ()
   {
      return mFilename;
   }
}
