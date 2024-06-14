package com.blackholeofphotography.naturallight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

            planetaryRegion.setBoundingBox (new BoundingBox (jsonRegion.getDouble ("minlatitude"), jsonRegion.getDouble ("minlongitude"),
                  jsonRegion.getDouble ("maxlatitude"), jsonRegion.getDouble ("maxlongitude")));

            JSONArray jsonZones = jsonRegion.getJSONArray ("zones");
            for (int z = 0; z < jsonZones.length (); z++)
               planetaryRegion.getTimeZones ().add (jsonZones.getJSONObject (z).getString ("name"));
            Collections.sort (planetaryRegion.getTimeZones ());

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
