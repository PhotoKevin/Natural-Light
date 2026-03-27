package com.blackholeofphotography.naturallight.ui.about;

import android.util.Log;

import org.slf4j.LoggerFactory;

public class ThirdPartyLibrary
{
   private static final org.slf4j.Logger logger = LoggerFactory.getLogger (ThirdPartyLibrary.class);
   private final String text;

   public ThirdPartyLibrary (String aText)
   {
      text = aText;
   }

   public String getText ()
   {
      logger.info ("Return {}", text);
      return text;
   }
}
