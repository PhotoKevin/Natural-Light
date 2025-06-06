package com.blackholeofphotography.naturallight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class MicaRiseSet
{
   public MicaPosition position;
   public int year;
   public int month;
   public int day;

   //public time_t datetime;
   public double jd;
   public double latitude;
   public double longitude;
   public int altitude;

   //public time_t BeginTwilight;
   //public time_t Rise;
   //public time_t Set;
   //public time_t EndTwilight;
   double Rise;
   double Set;
   double Transit;

   public int BegTwiHH, BegTwiMM, EndTwiHH, EndTwiMM;
   public int RiseAz, TransHH, TransMM, TransAlt;
   public int SetAz;
   public int RiseHH, RiseMM;
   public int SetHH, SetMM;
   public String dataLine;

   public static List<MicaRiseSet> loadMoonRiseData (File datafile)
   {
      ArrayList<MicaRiseSet> dataset = new ArrayList<> ();
      MicaPosition position = new MicaPosition ();

      try
      {
         BufferedReader br;

         br = new BufferedReader (new InputStreamReader (new FileInputStream (datafile)));
         while (br.ready ())
         {
            String line = br.readLine ();
            if (line.contains ("Location:"))
            {
               position = MicaPosition.parseMicaLocation (line);
            }
            else if (line.length () > 0 && line.substring (0, 1).matches ("[0-9]"))
            {
               MicaRiseSet rs = new MicaRiseSet ();
               rs.dataLine = line;
               final double v = MicaTools.parseDate (line);
               rs.RiseHH = MicaTools.extractNumber (line, 25, 2);
               rs.RiseMM = MicaTools.extractNumber (line, 28, 2);
               rs.RiseAz = MicaTools.extractNumber (line, 31, 3);
               rs.TransHH = MicaTools.extractNumber (line, 42, 2);
               rs.TransMM = MicaTools.extractNumber (line, 45, 2);
               rs.TransAlt = MicaTools.extractNumber (line, 48, 2);
               rs.SetHH = MicaTools.extractNumber (line, 59, 2);
               rs.SetMM = MicaTools.extractNumber (line, 62, 2);
               rs.SetAz = MicaTools.extractNumber (line, 65, 3);
               //rs.BeginTwilight = 0;

               rs.jd = v;
               rs.position = position;
               if (rs.RiseHH >= 0)
                  rs.Rise = rs.RiseHH + rs.RiseMM/60.0;
               else
                  rs.Rise = -1;

               if (rs.TransHH >= 0)
                  rs.Transit = rs.TransHH + rs.TransMM/60.0;
               else
                  rs.Transit = -1;

               if (rs.SetHH >= 0)
                  rs.Set = rs.SetHH + rs.SetMM / 60.0;
               else
                  rs.Set = -1;

               dataset.add (rs);
            }
         }
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException (e);
      }
      catch (IOException e)
      {
         throw new RuntimeException (e);
      }
      return dataset;
   }

}
/*
            


                     //          1         2         3         4         5         6         7
                     //01234567890123456789012345678901234567890123456789012345678901234567890123456789
                     //2024 Apr 09 (Tue)        11:26  73        18:27 62S        00:24 283



   }
}
*/