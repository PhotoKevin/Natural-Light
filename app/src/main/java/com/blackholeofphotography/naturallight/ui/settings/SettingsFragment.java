/*
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License. To view a
 * copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.blackholeofphotography.naturallight.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.blackholeofphotography.naturallight.MainActivity;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.Settings;

import java.util.Collections;
import java.util.List;

// Consider using this:
// https://github.com/gregkorossy/Android-Support-Preference-V7-Fix

public class SettingsFragment extends PreferenceFragmentCompat
{
   /** @noinspection FieldCanBeLocal*/
   private final String LOG_TAG = "SettingsFragment";
   private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;
   private SharedPreferences mSharedPreferences;
   private boolean timeZoneEngineReloadRequired = false;

   @Override
   public void onCreatePreferences (Bundle savedInstanceState, String rootKey)
   {
      try
      {
         setPreferencesFromResource (R.xml.root_preferences, rootKey);
         mSharedPreferences = PreferenceManager.getDefaultSharedPreferences (MainActivity.getContext ());
         showHideAdvanced ();

         setInputType (Settings.KEY_COMPASS_RADIUS_PERCENT, InputType.TYPE_CLASS_NUMBER);
         setInputType (Settings.KEY_DEGREE_FONT_SIZE, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
         setInputType (Settings.KEY_SUN_RISE_SET_NEEDLE_THICKNESS, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
         setInputType (Settings.KEY_CROSSHAIR_LENGTH, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
         setInputType (Settings.KEY_CROSSHAIR_THICKNESS, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
         ListPreference regionPreference = findPreference (Settings.KEY_REGION);
         assert regionPreference != null;
         setListPreferenceData (regionPreference, MainActivity.planetaryRegions.getRegionNames (), Settings.getRegion ());

         mSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener ()
         {
            @Override
            public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key)
            {
               Settings.loadFromBackingStore ();

               if (key.equals (Settings.KEY_SHOW_ADVANCED_PREFERENCES))
                  showHideAdvanced ();

               if (key.equals (Settings.KEY_REGION))
                  timeZoneEngineReloadRequired = true;
            }
         };

         mSharedPreferences.registerOnSharedPreferenceChangeListener (mSharedPreferenceChangeListener);
      }
      catch (Exception ex)
      {
         Log.e (LOG_TAG, ex.toString ());
      }
   }


   protected static void setListPreferenceData (ListPreference lp, List<String> items, String selected)
   {
      Collections.sort (items);
      CharSequence[] itemsArray = items.toArray(new CharSequence[0]);
      lp.setEntries (itemsArray);
      lp.setDefaultValue (selected);
      lp.setEntryValues (itemsArray);
   }

   @Override
   public void onStop ()
   {
      super.onStop ();
      mSharedPreferences.unregisterOnSharedPreferenceChangeListener (mSharedPreferenceChangeListener);
      if (timeZoneEngineReloadRequired)
         MainActivity.reloadTimeEngine ();
   }

   private void showHideAdvanced ()
   {
      PreferenceManager preferenceManager = getPreferenceManager ();
      Preference showAdvanced = preferenceManager.findPreference (Settings.KEY_SHOW_ADVANCED_PREFERENCES);
      boolean advancedAvailable = Settings.getEnableAdvancedFeatures ();

      if (showAdvanced != null)
         showAdvanced.setVisible (advancedAvailable);

      boolean visibility = Settings.getShowAdvancedSettings ();
      if (!advancedAvailable)
         visibility = false;


      final PreferenceScreen preferenceScreen = preferenceManager.getPreferenceScreen ();
      for (int i=0; i<preferenceScreen.getPreferenceCount (); i++)
      {
         String key = preferenceScreen.getPreference (i).getKey ();
         if (key != null && key.startsWith ("nladv_"))
            setVisible (key, visibility);

      }
   }

   private void setVisible (String key, boolean visibility)
   {
      Preference pref = getPreferenceManager ().findPreference (key);
      if (pref != null)
         pref.setVisible (visibility);
   }

   private void setInputType (String key, int inputType)
   {
      EditTextPreference editTextPreference = getPreferenceManager ().findPreference (key); // ("nl_rise_set_needle_thickness");
      if (editTextPreference != null)
      {
         editTextPreference.setOnBindEditTextListener (new androidx.preference.EditTextPreference.OnBindEditTextListener ()
         {
            @Override
            public void onBindEditText (@NonNull EditText editText)
            {
               editText.setInputType (inputType);

            }
         });
      }
   }
}
