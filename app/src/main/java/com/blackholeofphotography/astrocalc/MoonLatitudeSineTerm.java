package com.blackholeofphotography.astrocalc;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MoonLatitudeSineTerm
{
   final int d;
   final int m;
   final int mprime;
   final int f;
   final int sigl;
   public MoonLatitudeSineTerm (int d, int m, int mprime, int f, int sigl)
   {
      this.d = d;
      this.m = m;
      this.mprime = mprime;
      this.f = f;
      this.sigl = sigl;
   }


   private static MoonLatitudeSineTerm parseTerm (String inpline)
   {
      int d;
      int m;
      int mprime;
      int f;
      int sigl;

      String[] s = inpline.split (",");
      if (s.length == 5)
      {
         d = Integer.parseInt (s[0].trim ());
         m = Integer.parseInt (s[1].trim ());
         mprime = Integer.parseInt (s[2].trim ());
         f = Integer.parseInt (s[3].trim ());
         sigl = Integer.parseInt (s[4].trim ());
      }
      else
      {
         d = 0;
         m = 0;
         mprime = 0;
         f = 0;
         sigl = 0;

      }

      return new MoonLatitudeSineTerm (d, m, mprime, f, sigl);
   }

   public static final String  ASSET_BASE_PATH = "../app/src/main/assets/";

   public static MoonLatitudeSineTerm[]  loadTerms (String FileName)
   {
      ArrayList<MoonLatitudeSineTerm > Terms = new ArrayList<> ();
      try
      {
         BufferedReader br;

         Path p = Paths.get (ASSET_BASE_PATH, FileName);
         if (p.toFile ().exists ())
            br = new BufferedReader (new InputStreamReader (new FileInputStream (p.toFile ())));
         else
         {
            AssetManager assetManager = com.blackholeofphotography.naturallight.MainActivity.getContext ().getAssets ();
            br = new BufferedReader (new InputStreamReader (assetManager.open (FileName)));
         }

         while (br.ready ())
         {
            String inpline = br.readLine ();
            if (inpline.length () > 0 && inpline.toCharArray ()[0] != '/')
               Terms.add (parseTerm (inpline));
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException (e);
      }

      return Terms.toArray (new MoonLatitudeSineTerm[0]);
   }
}
