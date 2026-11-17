package com.blackholeofphotography.naturallight;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

/**
 * The NetworkTransport class monitors whether the current connection is Wi-Fi or not. This is
 * kept in a static variable so that download functions can decide if they can run or not without
 * needing to pass Contexts all around.
 */
public class NetworkTransport
{
   private static boolean onWifi = false;
   private final ConnectivityManager connManager;
   private final ConnectivityManager.NetworkCallback callback;

   NetworkTransport (Context context)
   {
      connManager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
      callback = new  ConnectivityManager.NetworkCallback ()
      {
         @Override
         public void onAvailable (@NonNull Network network)
         {

         }

         @Override
         public void onLost (@NonNull Network network)
         {

         }

         @Override
         public void onCapabilitiesChanged (@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities)
         {
            onWifi = networkCapabilities.hasTransport (NetworkCapabilities.TRANSPORT_WIFI);
         }

         @Override
         public void onLinkPropertiesChanged (@NonNull Network network, @NonNull LinkProperties linkProperties)
         {

         }
      };

      NetworkCapabilities nc = connManager.getNetworkCapabilities (connManager.getActiveNetwork ());
      if (nc != null)
         onWifi = nc.hasTransport (NetworkCapabilities.TRANSPORT_WIFI);

      connManager.registerDefaultNetworkCallback (callback);
   }

   /**
    * Release the callback thus stopping monitoring
    */

   public void shutdown ()
   {
      connManager.unregisterNetworkCallback (callback);
   }

   public static boolean isUnmetered ()
   {
      if (Settings.fakeAsMobile ())
         return false;

      return onWifi;
   }
}
