package com.blackholeofphotography.astrocalc;

public class MoonEphemeris
{
   // Moon/Sun
   // Ch 22 and 25 both use Astronomical Almanac 1984
   final public static double[] MoonSunMeanElongationMeeus22 = {297.85036, 445267.111480, -0.0019142, 1.0/189474, 0.0};

   // Ch 47 uses Chapront ELP2000
   final public static double[] MoonSunMeanElongationMeeus47 = {297.8501921, 445267.1114034, -0.0018819, 1.0/545868.0, -1.0/113065000};

   final public static double[] MoonMeanAnomalyMeeus22 = {134.96298, 477198.867398, 0.0086972, 1.0/56250, 0.0};
   final public static double[] MoonMeanAnomalyMeeus47 = {134.9633964, 477198.8675055, 0.0087414, 1.0/69699, -1.0/14712000};

   final public static double[] MoonArgumentOfLatitudeMeeus22 = {93.27191, 483202.017538, -0.0036825, 1.0/327270, 0.0};
   final public static double[] MoonArgumentOfLatitudeMeeus47 = {93.2720950, 483202.0175233, -0.0036539, -1.0/3526000, 1.0/863310000};

   final public static double[] MoonAscendingNodeLongitudeMeeus22 = {125.04452, -1934.136261, 0.0020708, 1.0/450000, 0.0};
   final public static double[] MoonMeanLongitudeMeeus47 = {218.3164591, 481267.88134236,-0.0013268, 1.0/535868, -1.0/113065000};

   public static double [] MoonMeanElongationDegreesCoefficients = MoonSunMeanElongationMeeus22;
   public static double [] MoonMeanAnomalyCoefficients = MoonMeanAnomalyMeeus22;
   public static double [] MoonArgumentOfLatitudeCoefficients = MoonArgumentOfLatitudeMeeus47;
   public static double [] MoonAscendingNodeLongitudeCoefficients = MoonAscendingNodeLongitudeMeeus22;
   public static double [] MoonMeanLongitudeCoefficients = MoonMeanLongitudeMeeus47;
}
