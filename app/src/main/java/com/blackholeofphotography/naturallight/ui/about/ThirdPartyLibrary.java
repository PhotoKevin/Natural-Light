package com.blackholeofphotography.naturallight.ui.about;

import android.util.Log;

public class ThirdPartyLibrary
{
   private static final String LOG_TAG = "ThirdPartyLibrary";
   private final String text;

   public ThirdPartyLibrary (String aText)
   {
      text = aText;
   }

   public String getText ()
   {
      Log.v (LOG_TAG, String.format ("Return %s", text));
      return text;
   }
}
