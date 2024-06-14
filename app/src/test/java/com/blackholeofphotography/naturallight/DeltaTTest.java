package com.blackholeofphotography.naturallight;

import com.blackholeofphotography.astrocalc.DeltaT;

import org.junit.Assert;
import org.junit.Test;
public class DeltaTTest
{
   @Test
   public void CheckHistoric ()
   {
      final double v = DeltaT.DeltaT (1990);
      Assert.assertEquals (57.1738, v, 0.01);
   }

   @Test
   public void CheckFuture ()
   {
      final double v = DeltaT.DeltaT (2030);
      Assert.assertEquals (70.14, v, 0.01);
   }
}
