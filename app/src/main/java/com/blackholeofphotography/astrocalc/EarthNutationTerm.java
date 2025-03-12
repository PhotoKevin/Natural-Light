package com.blackholeofphotography.astrocalc;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class EarthNutationTerm
{
   final public int[] y;
   final public double a, b, c, d;

   public EarthNutationTerm (int[] y, double a, double b, double c, double d)
   {
      this.y = new int[5];
      System.arraycopy (y, 0, this.y, 0, y.length);
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
   }


   private static EarthNutationTerm parseNutationTerm (String inpline)
   {
      int[] y = new int[5];
      int a;
      double b;
      double c;
      double d;
      String[] s = inpline.split (",");
      if (s.length == 9)
      {
         for (int i=0; i<5; i++)
            y[i] = Integer.parseInt (s[i].trim ());

         a = Integer.parseInt (s[5].trim ());
         b = Double.parseDouble (s[6].trim ());
         c = Double.parseDouble (s[7].trim ());
         d = Double.parseDouble (s[8].trim ());
      }
      else
      {
         a = 0;
         b = 0;
         c = 0;
         d = 0;
      }

      return new EarthNutationTerm (y, a, b, c, d);
   }

   public static final String  ASSET_BASE_PATH = "../app/src/main/assets/";

   public static ArrayList<EarthNutationTerm> loadNutationTerms (String FileName)
   {
      ArrayList<EarthNutationTerm> NutationTerms = new ArrayList<> ();
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
               NutationTerms.add (parseNutationTerm (inpline));
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException (e);
      }

      return NutationTerms;
   }
}
