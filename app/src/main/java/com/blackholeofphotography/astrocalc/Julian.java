package com.blackholeofphotography.astrocalc;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Julian
{
   // Convert to/from Julian Day Numbers. Julian Day Numbers are used by
// astronomers since they bypass all the other calendar nonsense and allow
// simple addition and subtraction. Julian Day Numbers are simply the number
// of days since 4713 BCE January 1, 12 hours (noon) UTC (Julian proleptic Calendar)
// aka 4714 BCE November 24, 12 hours GMT (Gregorian proleptic Calendar).

// https://en.wikipedia.org/wiki/Julian_day

// The equations are from
// Solar Position Algorithm for Solar Radiation Applications from
// National Renewable Energy Laboratory
// NREL/TP-560-34302 Appendix A.3


   /// <summary>
/// Calculate the Julian Day Number from a year, month, day.
/// The Timezone is UTC and the time is 00:00:00 of that that day
/// </summary>
/// <param name="year">Full year i.e. 2022</param>
/// <param name="month">Month 1-12</param>
/// <param name="day">Day 1-31</param>
/// <returns>The Julian Day Number.</returns>
/// <remarks>
/// The calculation is from the calendar FAQ
/// https://www.stason.org/TULARC/society/calendars/2-15-1-Is-there-a-formula-for-calculating-the-Julian-day-nu.html
/// </remarks>
   public static double JulianFromYMD (int year, int month, int day)
   {
      int a = (14-month)/12;
      long y = year+4800-a;
      long m = month + 12*a - 3;


      long jd = day + (153*m+2)/5 + y*365L + y/4 - y/100 + y/400 - 32045;

      // The above get's the jd for noon, but we want it for 00:00:00 so remove 0.5
      return jd - 0.5;
   }

/// <summary>
/// Calculate the Julian Day Number from a year, month, day, hour, minute, second.
/// The Timezone is UTC.
/// </summary>
/// <param name="year">Full year i.e. 2022</param>
/// <param name="month">Month 1-12</param>
/// <param name="day">Day 1-31</param>
/// <param name="hour">Hour 0-23</param>
/// <param name="minute">Minute 0-59</param>
/// <param name="second">Second 0.0-60.0</param>
/// <returns>The Julian Day Number.</returns>
/// <remarks>

   public static double JulianFromYMDHMS (int year, int month, int day, int hour, int minute, double second)
   {
      double jd = JulianFromYMD (year, month, day);
      jd += hour    / 24.0;
      jd += minute  / (24.0*60.0);
      jd += second /  (24.0*60.0*60.0);

      return jd;
   }

   public static double JulianFromZonedDateTime (ZonedDateTime zonedDateTime)
   {
      ZonedDateTime utc = zonedDateTime.withZoneSameInstant (ZoneId.of ("UTC"));
      return JulianFromYMDHMS (utc.getYear (), utc.getMonthValue (), utc.getDayOfMonth (), utc.getHour (), utc.getMinute (), utc.getSecond ());
   }
/*

   /// <summary>
/// Convert a Julian Day Number to a time_t
/// </summary>
/// <param name="jd">JDN of interest</param>
/// <returns>The date/time as a time_t</returns>
   time_t JDtoDate (double jd)
   {
      struct tm event_date;
      long a, z;
      double f, day;

      jd += 0.5;
      z = (long) jd;
      f = jd - z;

      if (z < 2299161)
         a = z;
      else
      {
         long a1 = (long) ((z - 1867216.25) / 36524.25);
         a = z + 1 + a1 - (long) (a1 / 4);
      }

      long c = a + 1524;
      long d = (long) ((c - 122.1) / 365.25);
      long g = (long) (365.25 * d);

      long i = (long) ((c - g)/30.6001);

      day = c - g - (long) (30.6001 * i) + f;
      int month;

      if (i < 14)
         month = (int) (i - 1);
      else
         month = (int) (i - 13);

      int year;
      if (month > 2)
         year = d - 4716;
      else
         year = d - 4715;


      event_date.tm_year = year - 1900;
      event_date.tm_mon = month - 1;

      event_date.tm_mday = (int) day;
      day -= event_date.tm_mday;
      day *= 24;
      event_date.tm_hour = (int) day;
      day -= event_date.tm_hour;
      day *= 60;
      event_date.tm_min = (int) day;
      day -= event_date.tm_min;
      day *= 60;
      event_date.tm_sec = (int) day;

      event_date.tm_isdst = -1;

      // Danger. There is no official tm as UTC to time_t
      // Microsoft supplies _mkgmtime.
      // GCC has timegm (?)

      return _mkgmtime (&event_date);
   }
*/
// http://asa.hmnao.com

// 86400 = seconds per day
// 2451545 = JD for Jan 1, 2000

//#define DELTA_T 0

   /// <summary>
/// Calculate the Julian Ephemeris Day for the Julian Day Number
/// </summary>
/// <param name="jd">Julian Day Number</param>
/// <returns>Julian Ephemeris Day</returns>
/// <remarks>
/// A Julian Day is calculated in Terrestrial Time. The Julian Ephemeris Day applies
/// DeltaT to that to handle the leap seconds.
/// </remarks>
   public static double JulianEphemerisDay (double jd)
   {
      double year = 100.0 * JulianCentury (jd) + 2000;
      return jd + DeltaT.DeltaT ((int) year) / 86400;
   }


/// <summary>
/// Calculate the Julian Century for the Julian Day Number
/// </summary>
/// <param name="jd">Julian Day Number</param>
/// <returns>Julian Century</returns>
/// <remarks>
/// The Julian Century is the number of fractional centuries since Jan 1, 2000 12:00
/// </remarks>

   public static double JulianCentury (double jd)
   {
      return (jd - 2451545.0) / 36525.0;
   }

/// <summary>
/// Calculate the Julian Ephemeris Century for the Julian Day Number
/// </summary>
/// <param name="jd">Julian Day Number</param>
/// <returns>Julian Ephemeris Century</returns>

   public static double JulianEphemerisCentury (double jd)
   {
      double jde = JulianEphemerisDay (jd);
      return (jde - 2451545.0) / 36525.0;
   }

/// <summary>
/// Calculate the Julian Ephemeris Millennium for the Julian Day Number
/// </summary>
/// <param name="jd">Julian Day Number</param>
/// <returns>Julian Ephemeris Millennium</returns>

   public static double JulianEphemerisMillennium (double jd)
   {
      return (JulianEphemerisCentury (jd) / 10);
   }

}
