package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.PlanetaryPeriodicTerm;

import org.junit.Assert;
import org.junit.Test;

public class PeriodicTermsTest
{
   @Test
   public void loadPeriodicTerms ()
   {
      PlanetaryPeriodicTerm[] terms = PlanetaryPeriodicTerm.loadPlanetaryTerms ("EarthPeriodicTermsL0.txt");
      Assert.assertEquals (64, terms.length);
   }
}
