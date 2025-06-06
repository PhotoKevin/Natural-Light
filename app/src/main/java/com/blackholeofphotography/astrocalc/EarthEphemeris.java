package com.blackholeofphotography.astrocalc;

public class EarthEphemeris
{
   // static final double[] EarthMeanAnomalyMeeus22 = {357.52772, 35999.050340, -0.0001603, -1.0/300000, 0.0};
   public static final double[] EarthMeanAnomalyMeeus25 = {357.52911, 35999.05029, -0.00011537, 0.0, 0.0};
   static final double[] EarthMeanAnomalyMeeus47 = {357.5291092, 35999.0502909, -0.0001536, 1.0/24490000, 0.0};
   public static double[] EarthMeanAnomalyCoefficients = EarthMeanAnomalyMeeus47;
}
