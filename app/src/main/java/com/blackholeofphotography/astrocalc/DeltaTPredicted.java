package com.blackholeofphotography.astrocalc;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.blackholeofphotography.naturallight.MainActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class DeltaTPredicted
{
   double mjd; // MJD = JD - 2400000.5.
   double year;
   double deltat;
   double ut1_minus_utc;

   private static ArrayList<DeltaTPredicted> Predicted = new ArrayList<> ();

   public static ArrayList<DeltaTPredicted> getPredicted ()
   {
      return Predicted;
   }

   public DeltaTPredicted (double mjd, double year, double deltat, double ut1_minus_utc)
   {
      this.mjd = mjd;
      this.year = year;
      this.deltat = deltat;
      this.ut1_minus_utc = ut1_minus_utc;
   }

   private static void parseDeltaTPredicted (String inpline)
   {
      double mjd;
      double year;
      double deltat;

//      MJD	          Year	TT-UT	UT1-UTC	Error
//      59762.000	2022.50	69.29	-0.104	0.031

      String[] s = inpline.split ("\t");
      mjd = Double.parseDouble (s[0]);
      year = Double.parseDouble (s[1]);
      deltat = Double.parseDouble (s[2]);

      Predicted.add (new DeltaTPredicted (mjd, year, deltat, 0));

   }

   public static final String  ASSET_BASE_PATH = "../app/src/main/assets/";
   private static final String DELTAT_PRED = "deltat.preds";
   public static void load ()
   {
      if (getPredicted ().size () > 0)
         return;

      try
      {
         BufferedReader br;
         Path p = Paths.get (ASSET_BASE_PATH, DELTAT_PRED);
         if (p.toFile ().exists ())
            br = new BufferedReader (new InputStreamReader (new FileInputStream (p.toFile ())));
         else
         {
            final Context context = MainActivity.getContext ();
            AssetManager assetManager = context.getAssets ();
            br = new BufferedReader (new InputStreamReader (assetManager.open (DELTAT_PRED)));
         }

         while (br.ready ())
         {
            String inpline = br.readLine ();
            if (inpline.length () > 0 && Character.isDigit (inpline.toCharArray ()[0]))
               parseDeltaTPredicted (inpline);
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException (e);
      }

   }
}
