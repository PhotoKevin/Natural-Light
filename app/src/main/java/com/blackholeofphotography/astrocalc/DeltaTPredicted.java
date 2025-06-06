package com.blackholeofphotography.astrocalc;

import android.content.Context;
import android.content.res.AssetManager;

import com.blackholeofphotography.naturallight.MainActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DeltaTPredicted
{
   final double mjd; // MJD = JD - 2400000.5.
   final double deltat;

   private static final ArrayList<DeltaTPredicted> Predicted = new ArrayList<> ();

   public static ArrayList<DeltaTPredicted> getPredicted ()
   {
      return Predicted;
   }

   public DeltaTPredicted (double mjd, double deltat)
   {
      this.mjd = mjd;
      this.deltat = deltat;
   }

   private static void parseDeltaTPredicted (String inputLine)
   {
      double mjd;
      double deltat;

//      MJD	          Year	TT-UT	UT1-UTC	Error
//      59762.000	2022.50	69.29	-0.104	0.031

      String[] s = inputLine.split ("\t");
      mjd = Double.parseDouble (s[0]);
      deltat = Double.parseDouble (s[2]);

      Predicted.add (new DeltaTPredicted (mjd, deltat));
   }

   public static final String  ASSET_BASE_PATH = "../app/src/main/assets/";
   private static final String DELTAT_PREDICTED = "deltat.preds";
   public static void load ()
   {
      if (getPredicted ().size () > 0)
         return;

      try
      {
         BufferedReader br;
         Path p = Paths.get (ASSET_BASE_PATH, DELTAT_PREDICTED);
         if (p.toFile ().exists ())
            br = new BufferedReader (new InputStreamReader (new FileInputStream (p.toFile ())));
         else
         {
            final Context context = MainActivity.getContext ();
            AssetManager assetManager = context.getAssets ();
            br = new BufferedReader (new InputStreamReader (assetManager.open (DELTAT_PREDICTED)));
         }

         while (br.ready ())
         {
            String line = br.readLine ();
            if (line.length () > 0 && Character.isDigit (line.toCharArray ()[0]))
               parseDeltaTPredicted (line);
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException (e);
      }
   }
}
