package com.blackholeofphotography.naturallight.ui.details;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blackholeofphotography.naturallight.ASTools;
import com.blackholeofphotography.naturallight.DisplayStatus;
import com.blackholeofphotography.naturallight.databinding.DetailFragmentBinding;

import java.time.format.DateTimeFormatter;

public class DetailFragment extends Fragment
{
   private DetailFragmentBinding binding;
   TextView textSunRise;
   TextView textLatitude;
   TextView textLongitude;
   TextView textSunSet ;
   TextView textSunAzimuth;
   TextView textDateTime;
   TextView textSunElevation;
   TextView textTimeZone;
   TextView textMoonAzimuth;
   TextView textMoonElevation;
   TextView textMoonRise;
   TextView textMoonSet;


   public View onCreateView (@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
   {
      binding = DetailFragmentBinding.inflate (inflater, container, false);
      View root = binding.getRoot ();

      textSunRise = binding.textSunRise;
      textLatitude = binding.textLatitude;
      textLongitude = binding.textLongitude;
      textSunSet = binding.textSunSet;
      textSunAzimuth = binding.textSunAzimuth;
      textDateTime = binding.textDateTime;
      textSunElevation = binding.textSunElevation;
      textTimeZone = binding.textTimeZone;
      textMoonAzimuth = binding.textMoonAzimuth;
      textMoonElevation = binding.textMoonElevation;
      textMoonRise = binding.textMoonRise;
      textMoonSet = binding.textMoonSet;

      return root;
   }
   @SuppressLint("DefaultLocale")
   @Override
   public void onResume ()
   {
      super.onResume ();
      textLatitude.setText (ASTools.formatGeoPoint (DisplayStatus.getLocation ().getLatitude ()));
      textLongitude.setText (ASTools.formatGeoPoint (DisplayStatus.getLocation ().getLongitude ()));

      textDateTime.setText (DisplayStatus.getTimeStamp ().format (DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm")));
      textTimeZone.setText (DisplayStatus.getDisplayZoneId ().toString ());
      textSunAzimuth.setText (String.format ("%.0f°", DisplayStatus.getSunPosition ().getAzimuth ()));
      textSunElevation.setText (String.format ("%f°", DisplayStatus.getSunPosition ().getElevation ()));

      textSunRise.setText (DisplayStatus.getSunRise ().format (DateTimeFormatter.ofPattern ("HH:mm")));
      textSunSet.setText (DisplayStatus.getSunSet ().format (DateTimeFormatter.ofPattern ("HH:mm")));

      textMoonRise.setText (DisplayStatus.getMoonRise ().format (DateTimeFormatter.ofPattern ("HH:mm")));
      textMoonSet.setText (DisplayStatus.getMoonSet ().format (DateTimeFormatter.ofPattern ("HH:mm")));

      textMoonAzimuth.setText (String.format ("%f", DisplayStatus.getMoonPosition ().getAzimuth ()));
      textMoonElevation.setText (String.format ("%f", DisplayStatus.getMoonPosition ().getElevation ()));
   }

   @Override
   public void onDestroyView ()
   {
      super.onDestroyView ();
      binding = null;
   }
}
