package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @noinspection SpellCheckingInspection*/
public class MicaPosition
{
   public double jd;
   public double latitude;
   public double longitude;
   public int    altitude;

   //  Location:  W 83°47'05.7", N42°17'19.6",   256m
   final private static String LOCATION_PATTEN =
         " *Location:  (?<ewchar>[EW]) (?<londeg>[0-9][0-9]).(?<lonmin>[0-9][0-9]).(?<lonsec>[0-9][0-9]\\.[0-9])\", (?<nschar>[NS])(?<latdeg>[0-9][0-9]).(?<latmin>[0-9][0-9]).(?<latsec>[0-9][0-9]\\.[0-9])\", *(?<elevation>[0-9]*)m.*";

   public static MicaPosition parseMicaLocation (String line)
   {
      MicaPosition position = new MicaPosition ();
      Pattern p = Pattern.compile (LOCATION_PATTEN);
      final Matcher matcher = p.matcher (line);
      final boolean matches = matcher.matches ();
      if (matches)
      {
         String nschar = matcher.group ("nschar");
         int londeg = Integer.parseInt (matcher.group ("londeg"));
         int lonmin = Integer.parseInt (matcher.group ("lonmin"));
         double lonsec = Float.parseFloat (matcher.group ("lonsec"));

         String ewchar = matcher.group ("ewchar");
         int latdeg = Integer.parseInt (matcher.group ("latdeg"));
         int latmin = Integer.parseInt (matcher.group ("latmin"));
         double latsec = Float.parseFloat (matcher.group ("latsec"));

         int alt = Integer.parseInt (matcher.group ("elevation"));

         int ewSign = ewchar.equals ("E") ? 1 : -1;
         int nsSign = nschar.equals ("N") ? 1 : -1;
         position.latitude = Tools.fractionalDegrees (latdeg, latmin, latsec) * nsSign;
         position.longitude = Tools.fractionalDegrees (londeg, lonmin, lonsec) * ewSign;
         position.altitude = alt;
      }

      return position;
   }
}
