
package com.blackholeofphotography.naturallight;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kevin Nickerson
 */
public class PlanetaryRegion
{
   final private  String mRegionName;
   private  BoundingBox box;
   final private  List<String> zones;
   final private  List<String> countries;

   
   public PlanetaryRegion (String aRegionName)
   {
      mRegionName = aRegionName;
      zones = new ArrayList<> ();
      countries = new ArrayList<> ();
   }

   @SuppressWarnings ("unused")
   public BoundingBox getBoundingBox ()
   {
      return box;
   }

   public void setBoundingBox (BoundingBox aBoundingBox)
   {
      box = aBoundingBox;
   }

   public BoundingBox getBigBoundingBox ()
   {
      BoundingBox bb = box;
      if (MainActivity.timeZoneAreas != null)
         for (var zone : getTimeZones ())
         {
            BoundingBox box = MainActivity.timeZoneAreas.getBoundingBox (zone);
            if (box != null)
               bb = bb.include (box);
         }

      return bb;
   }
   public String getRegionName ()
   {
      return mRegionName;
   }
   
   public List<String> getTimeZones ()
   {
      return this.zones;
   }

   public List<String> getCountries ()
   {
      return this.countries;
   }
}
