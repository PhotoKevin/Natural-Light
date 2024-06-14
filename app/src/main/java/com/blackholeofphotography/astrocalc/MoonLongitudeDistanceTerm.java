package com.blackholeofphotography.astrocalc;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MoonLongitudeDistanceTerm
{
   int d;
   int m;
   int mprime;
   int f;
   double sigl;
   double sigr;

   public MoonLongitudeDistanceTerm (int d, int m, int mprime, int f, double sigl, double sigr)
   {
      this.d = d;
      this.m = m;
      this.mprime = mprime;
      this.f = f;
      this.sigl = sigl;
      this.sigr = sigr;
   }


   private static MoonLongitudeDistanceTerm parseNutationTerm (String inpline)
   {
      int d;
      int m;
      int mprime;
      int f;
      double sigl;
      double sigr;

      String[] s = inpline.split (",");
      if (s.length == 6)
      {
         d = Integer.parseInt (s[0].trim ());
         m = Integer.parseInt (s[1].trim ());
         mprime = Integer.parseInt (s[2].trim ());
         f = Integer.parseInt (s[3].trim ());
         sigl = Double.parseDouble (s[4].trim ());
         sigr = Double.parseDouble (s[5].trim ());
      }
      else
      {
         d = 0;
         m = 0;
         mprime = 0;
         f = 0;
         sigl = 0;
         sigr = 0;
      }

      return new MoonLongitudeDistanceTerm (d, m, mprime, f, sigl, sigr);
   }

   public static final String  ASSET_BASE_PATH = "../app/src/main/assets/";

   public static MoonLongitudeDistanceTerm[] loadTerms (String FileName)
   {
      ArrayList<MoonLongitudeDistanceTerm> Terms = new ArrayList<> ();
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
               Terms.add (parseNutationTerm (inpline));
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException (e);
      }

      return Terms.toArray (new MoonLongitudeDistanceTerm[0]);
   }
}
