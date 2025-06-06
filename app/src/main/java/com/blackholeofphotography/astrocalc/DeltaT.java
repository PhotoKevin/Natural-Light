package com.blackholeofphotography.astrocalc;

public class DeltaT
{
   //https://en.wikipedia.org/wiki/%CE%94T_(timekeeping)
   //https://webspace.science.uu.nl/~gent0113/deltat/deltat.htm
   // https://maia.usno.navy.mil/products/deltaT
   // https://maia.usno.navy.mil/ser7/deltat.data
   // https://maia.usno.navy.mil/ser7/deltat.preds

   private static double forced_deltat = 0.0;
   private static boolean force_deltat = false;
   // private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger (DeltaT.class);

   public static void ForceDeltaT (double value)
   {
      force_deltat = true;
      forced_deltat = value;
   }

   public static void UnForceDeltaT ()
   {
      force_deltat = false;
   }

   static double historic_jd (DeltaTHistoric hist)
   {
      return Julian.JulianFromYMD (hist.year, hist.month, hist.day);
   }

   static double interpolate (double jd, DeltaTHistoric hprev, DeltaTHistoric hafter)
   {
      if (jd == historic_jd (hprev))
         return hprev.deltat;

      if (jd == historic_jd (hafter))
         return hafter.deltat;


      double deltajd = historic_jd (hafter) - historic_jd (hprev);
      double delta_deltat = hafter.deltat - hprev.deltat;
      double fraction = (jd - historic_jd (hprev)) / deltajd;

      double deltaT = hprev.deltat + fraction * delta_deltat;

      return deltaT;
   }



   static double interpolatePrediction (double jd, DeltaTPredicted hprev, DeltaTPredicted hafter)
   {
      double mjd = jd - 2400000.5;

      if (mjd == hprev.mjd)
         return hprev.deltat;

      if (mjd == hafter.mjd)
         return hafter.deltat;

      double deltajd = hafter.mjd - hprev.mjd;
      double delta_deltat = hafter.deltat - hprev.deltat;
      double fraction = (mjd - hprev.mjd) / deltajd;

      double deltaT = hprev.deltat + fraction * delta_deltat;

      return deltaT;
   }

   /// <summary>
/// Calculate DeltaT for the Julian Date
/// </summary>
/// <param name="year">JD of interest</param>
/// <returns>DeltaT in seconds</returns>
/// <remarks>
/// These equations come from
/// https://eclipse.gsfc.nasa.gov/SEhelp/deltatpoly2004.html
/// </remarks>
   static double DeltaTD (double jd)
   {
      if (force_deltat)
         return forced_deltat;

      DeltaTHistoric hprev = null;
      DeltaTHistoric hafter = null;

      DeltaTHistoric[] historic = DeltaTHistoric.getHistorical ().toArray (new DeltaTHistoric[1]);


      for (int i = 0; i < historic.length; i++)
      {
         double hjd = historic_jd (historic[i]);
         if (hjd <= jd && (hprev == null || hjd > historic_jd (hprev)))
            hprev = historic[i];

         if (hjd >= jd && (hafter == null || hjd < historic_jd (hafter)))
            hafter = historic[i];
      }

      if (hprev != null && hafter != null)
         return interpolate (jd, hprev, hafter);


      DeltaTPredicted pprev = null;
      DeltaTPredicted pafter = null;

      DeltaTPredicted[] predictions = DeltaTPredicted.getPredicted ().toArray (new DeltaTPredicted[0]);

      double mjd = jd - 2400000.5;
      for (int i = 0; i < predictions.length; i++)
      {
         double hjd = predictions[i].mjd;
         if (hjd <= mjd && (pprev == null || hjd > pprev.mjd))
            pprev = predictions[i];

         if (hjd >= mjd && (pafter == null || hjd < pafter.mjd))
            pafter = predictions[i];
      }

      if (pprev != null && pafter != null)
         return interpolatePrediction (jd, pprev, pafter);
      else if (pprev != null)
         return pprev.deltat;
      else if (pafter != null)
         return pafter.deltat;
      else
         return 71.25; // Last known prediction.
   }

   public static double deltaT (int year)
   {
      if (force_deltat)
         return forced_deltat;

      DeltaTPredicted.load ();
      DeltaTHistoric.load ();

      double jd = Julian.JulianFromYMD (year, 6, 1);
      return DeltaTD (jd);
   }

}
