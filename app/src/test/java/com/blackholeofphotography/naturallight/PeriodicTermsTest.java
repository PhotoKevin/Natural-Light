package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.PlanataryPeriodicTerm;

import org.junit.Assert;
import org.junit.Test;

public class PeriodicTermsTest
{
   @Test
   public void loadPeriodicTerms ()
   {
      PlanataryPeriodicTerm[] terms = PlanataryPeriodicTerm.loadPlanataryTerms ("EarthPeriodicTermsL0.txt");
      Assert.assertEquals (64, terms.length);
   }
}
