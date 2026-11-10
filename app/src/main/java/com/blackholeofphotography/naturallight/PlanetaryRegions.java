package com.blackholeofphotography.naturallight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Works with the various regions of the planet. The regions are defined in a JSON file.
 * <br />
 * The purpose of the regions is to allow the user to reduce the memory footprint by
 * selecting the region of interest.
 *
 */
public class PlanetaryRegions
{
   final private List<PlanetaryRegion> mPlanetaryRegions;

   public PlanetaryRegions (List<PlanetaryRegion> aPlanetaryRegions)
   {
      mPlanetaryRegions = aPlanetaryRegions;
   }


   public List<String> getRegionNames ()
   {
      List<String> regionNames = new ArrayList<> ();
      for (var region : mPlanetaryRegions)
      {
         if (!regionNames.contains (region.getRegionName ()))
            regionNames.add (region.getRegionName ());
      }

      return regionNames;
   }

   public PlanetaryRegion getRegion (String regionName)
   {
      for (var region : mPlanetaryRegions)
      {
         if (regionName.equals (region.getRegionName ()))
            return region;
      }

      return null;
   }
   public static PlanetaryRegions loadRegions ()
   {
      final String resourceContents = DataHelper.getAssetContents ("PlanetaryRegions.json");
      return deSerialize (resourceContents);
   }

   public static PlanetaryRegions deSerialize (String jsonString)
   {
      List<PlanetaryRegion> regions = new ArrayList<> ();

      try
      {
         JSONArray jsonSubRegions = new JSONArray (jsonString);
         for (int i = 0; i < jsonSubRegions.length (); i++)
         {
            JSONObject jsonRegion = jsonSubRegions.getJSONObject (i);

            String regionName = jsonRegion.getString ("sub-region");
            PlanetaryRegion planetaryRegion = new PlanetaryRegion (regionName);
            regions.add (planetaryRegion);

            JSONArray jsonZones = jsonRegion.getJSONArray ("zones");
            for (int z = 0; z < jsonZones.length (); z++)
               planetaryRegion.getTimeZoneNames ().add (jsonZones.getJSONObject (z).getString ("name"));
            Collections.sort (planetaryRegion.getTimeZoneNames ());

            JSONArray jsonCountries = jsonRegion.getJSONArray ("countries");
            for (int z = 0; z < jsonCountries.length (); z++)
               planetaryRegion.getCountries ().add (jsonCountries.getJSONObject (z).getString ("name"));
            Collections.sort (planetaryRegion.getCountries ());
         }
      }
      catch (JSONException e)
      {
         throw new RuntimeException (e);
      }

      return new PlanetaryRegions (regions);
   }
}
