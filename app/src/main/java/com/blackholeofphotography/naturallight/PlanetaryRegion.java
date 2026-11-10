
package com.blackholeofphotography.naturallight;

import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Describe a single region of the planet such as "North America".
 * A region contains a list of countries, timezones, etc.
 */
public class PlanetaryRegion
{
   private static final org.slf4j.Logger logger = LoggerFactory.getLogger (PlanetaryRegion.class);

   final private  String mRegionName;
   final private  List<String> mZoneNames;
   final private  List<String> mCountries;

   
   public PlanetaryRegion (String aRegionName)
   {
      mRegionName = aRegionName;
      mZoneNames = new ArrayList<> ();
      mCountries = new ArrayList<> ();
   }

   public String getRegionName ()
   {
      return mRegionName;
   }
   
   public List<String> getTimeZoneNames ()
   {
      return this.mZoneNames;
   }

   public Set<ZoneId> getZoneIds ()
   {
      Set<ZoneId> zoneIds = new HashSet<> ();

      for (var zoneName : this.mZoneNames)
      {
         try
         {
            var id = ZoneId.of (zoneName);
            zoneIds.add (id);
         }
         catch (DateTimeException ex)
         {
            logger.error ("Unknown Time zone {}", zoneName);
         }
      }

      return zoneIds;
   }

   public List<String> getCountries ()
   {
      return this.mCountries;
   }
}
