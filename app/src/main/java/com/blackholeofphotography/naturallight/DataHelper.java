package com.blackholeofphotography.naturallight;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DataHelper
{
   @SuppressWarnings ("unused")
   public static String getResourceContents (String resourceName)
   {
      StringBuilder contents = new StringBuilder ();
      try
      {
         ClassLoader classloader = Thread.currentThread ().getContextClassLoader ();
         if (classloader != null)
         {
            URL u = classloader.getResource (resourceName);
            if (u != null)
            {
               BufferedReader in = new BufferedReader (new InputStreamReader (u.openStream (), StandardCharsets.UTF_8));
               while (in.ready ())
                  contents.append (in.readLine ());
            }
         }
      }
      catch (IOException ex)
      {
         // Logger.getLogger (Database.class.getName()).log (Level.SEVERE, null, ex);
      }
      return contents.toString ();
   }


   public static String getAssetContents (String resourceName)
   {
      StringBuilder contents = new StringBuilder ();
      try
      {
         AssetManager assetManager = com.blackholeofphotography.naturallight.MainActivity.getContext ().getAssets ();
         assetManager.open (resourceName);
         BufferedReader in = new BufferedReader (new InputStreamReader (assetManager.open (resourceName), StandardCharsets.UTF_8));
         while (in.ready ())
            contents.append (in.readLine ());
      }
      catch (IOException ex)
      {
         // Logger.getLogger (Database.class.getName()).log (Level.SEVERE, null, ex);
      }
      return contents.toString ();
   }
}
