package com.blackholeofphotography.astrocalc;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PlanetaryPeriodicTerm
{
   final double  a;
   final double  b, c;

   PlanetaryPeriodicTerm (double a, double b, double c)
   {
      this.a = a;
      this.b = b;
      this.c = c;
   }


   private static PlanetaryPeriodicTerm parsePeriodicTerm (String inpline)
   {
      double a;
      double b;
      double c;
      String[] s = inpline.split (",");
      if (s.length == 3)
      {
         a = Double.parseDouble (s[0].trim ());
         b = Double.parseDouble (s[1].trim ());
         c = Double.parseDouble (s[2].trim ());
      }
      else if (s.length == 4)
      {
         a = Double.parseDouble (s[1].trim ());
         b = Double.parseDouble (s[2].trim ());
         c = Double.parseDouble (s[3].trim ());
      }
      else
      {
         a = 0;
         b = 0;
         c = 0;
      }

      return new PlanetaryPeriodicTerm (a, b, c);
   }

   public static final String  ASSET_BASE_PATH = "../app/src/main/assets/";

   public static PlanetaryPeriodicTerm[] loadPlanetaryTerms (String FileName)
   {
      ArrayList<PlanetaryPeriodicTerm> PeriodicTerms = new ArrayList<> ();
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
            if (inpline.length () > 0 && Character.isDigit (inpline.toCharArray ()[0]))
               PeriodicTerms.add (parsePeriodicTerm (inpline));
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException (e);
      }

      return PeriodicTerms.toArray (new PlanetaryPeriodicTerm[0]);
   }
}
