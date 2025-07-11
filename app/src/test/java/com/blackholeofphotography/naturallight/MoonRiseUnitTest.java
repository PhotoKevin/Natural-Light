package com.blackholeofphotography.naturallight;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

import com.blackholeofphotography.astrocalc.Julian;
import com.blackholeofphotography.astrocalc.Moon;
import com.blackholeofphotography.astrocalc.MoonRise;
import com.blackholeofphotography.astrocalc.RiseTransitSet;
import com.blackholeofphotography.astrocalc.TopocentricPosition;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MoonRiseUnitTest
{

   private static File getFileFromPath (Object obj, String fileName)
   {
      ClassLoader classLoader = obj.getClass ().getClassLoader ();
      URL resource = classLoader.getResource (fileName);
      final String externalForm = resource.getFile ().substring (1);
      String name = java.net.URLDecoder.decode (externalForm, StandardCharsets.UTF_8);
      return new File (name);
   }

   @Test
   public void MoonRiseHome ()
   {
      double allowedError = 3/60.0;
      File f = getFileFromPath (this, "MoonRiseHome.txt");
      final List<MicaRiseSet> micaRiseSets = MicaRiseSet.loadMoonRiseData (f);
      for (MicaRiseSet mrs : micaRiseSets)
      {
         final RiseTransitSet riseTransitSet = MoonRise.moonRise (mrs.jd, mrs.position.latitude, mrs.position.longitude, mrs.position.altitude);
         Assert.assertEquals (mrs.Rise, riseTransitSet.getRise (), allowedError);
         Assert.assertEquals (mrs.Transit, riseTransitSet.getTransit (), allowedError);
         Assert.assertEquals (mrs.dataLine, mrs.Set, riseTransitSet.getSet (), allowedError);


         if (riseTransitSet.getRise () >= 0)
         {
            double riseJD = mrs.jd + mrs.RiseHH/24.0 + mrs.RiseMM/(60.0*24);
            final TopocentricPosition topocentricPosition = Moon.MoonTopocentricPosition (riseJD, mrs.position.latitude, mrs.position.longitude, mrs.position.altitude);
            Assert.assertEquals (mrs.dataLine, mrs.RiseAz, topocentricPosition.getAzimuth (), 1);
         }

         if (riseTransitSet.getTransit () >= 0)
         {
            double transitJD = mrs.jd + mrs.TransHH/24.0 + mrs.TransMM/(60.0*24);
            final TopocentricPosition topocentricPosition = Moon.MoonTopocentricPosition (transitJD, mrs.position.latitude, mrs.position.longitude, mrs.position.altitude);
            Assert.assertEquals (mrs.dataLine, mrs.TransAlt, topocentricPosition.getElevation (), 1);
         }
      }
   }
}
