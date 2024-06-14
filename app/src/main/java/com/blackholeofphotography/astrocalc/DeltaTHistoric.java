package com.blackholeofphotography.astrocalc;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DeltaTHistoric
{
   int year;
   int month;
   int day;
   double deltat;

   private static ArrayList<DeltaTHistoric> Historical = new ArrayList<> ();

   public static ArrayList<DeltaTHistoric> getHistorical ()
   {
      return Historical;
   }

   public DeltaTHistoric (int year, int month, int day, double deltat)
   {
      this.year = year;
      this.month = month;
      this.day = day;
      this.deltat = deltat;
   }


   private static void parseDeltaTHistoric (String inpline)
   {

//      MJD	          Year	TT-UT	UT1-UTC	Error
//      59762.000	2022.50	69.29	-0.104	0.031

      String[] s = inpline.split (" +");
      int year = Integer.parseInt (s[1]);
      int month = Integer.parseInt (s[2]);
      int day = Integer.parseInt (s[3]);
      double deltat = Double.parseDouble (s[4]);

      Historical.add (new DeltaTHistoric (year, month, day, deltat));
   }

   public static final String  ASSET_BASE_PATH = "../app/src/main/assets/";
   private static final String DELTAT_PRED = "deltat.data";
   public static void load ()
   {
      if (getHistorical ().size () > 0)
         return;

      try
      {
         BufferedReader br;

         Path p = Paths.get (ASSET_BASE_PATH, DELTAT_PRED);
         if (p.toFile ().exists ())
            br = new BufferedReader (new InputStreamReader (new FileInputStream (p.toFile ())));
         else
         {
            AssetManager assetManager = com.blackholeofphotography.naturallight.MainActivity.getContext ().getAssets ();
            br = new BufferedReader (new InputStreamReader (assetManager.open (DELTAT_PRED)));
         }

         while (br.ready ())
         {
            String inpline = br.readLine ();
//            if (inpline.length () > 0 && Character.isDigit (inpline.toCharArray ()[0]))
               parseDeltaTHistoric (inpline);
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException (e);
      }

   }
}
