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
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.blackholeofphotography.naturallight.CachedData;
import com.blackholeofphotography.naturallight.DataFetcher;
import com.blackholeofphotography.naturallight.MainActivity;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.Settings;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat
{
   /** */
   private static final org.slf4j.Logger logger = LoggerFactory.getLogger (SettingsFragment.class);
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
         showHideDeveloperSettings ();

         setInputType (Settings.KEY_COMPASS_RADIUS_PERCENT, InputType.TYPE_CLASS_NUMBER);
         setInputType (Settings.KEY_DEGREE_FONT_SIZE, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
         setInputType (Settings.KEY_SUN_RISE_SET_NEEDLE_THICKNESS, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
         setInputType (Settings.KEY_CROSSHAIR_LENGTH, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
         setInputType (Settings.KEY_CROSSHAIR_THICKNESS, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
         ListPreference regionPreference = findPreference (Settings.KEY_REGION);
         assert regionPreference != null;
         setListPreferenceData (regionPreference, MainActivity.planetaryRegions.getRegionNames (), Settings.getRegion ());
         regionPreference.setSummary (regionPreference.getValue ());
         Settings.deleteTimeShapeData (false);

         mSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener ()
         {
            @Override
            public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key)
            {
               Settings.loadFromBackingStore ();

               if (key.equals (Settings.KEY_ENABLE_DEVELOPER_MODE))
                  showHideDeveloperSettings ();

               if (key.equals (Settings.KEY_REGION))
               {
                  timeZoneEngineReloadRequired = true;
                  regionPreference.setSummary (regionPreference.getValue ());
               }

               if (key.equals ("nl_delete_timeshape_data") && sharedPreferences.getBoolean (key, false))
               {
                  final File localTimeShapeDataFile = DataFetcher.getLocalTimeShapeDataFile ();
                  if (localTimeShapeDataFile != null && localTimeShapeDataFile.exists ())
                  {
                     boolean deleted = localTimeShapeDataFile.delete ();
                     if (! deleted)
                        logger.error ("Can't delete {}", localTimeShapeDataFile);
                  }

                  CachedData.clearAllCache ();
               }
            }
         };

         mSharedPreferences.registerOnSharedPreferenceChangeListener (mSharedPreferenceChangeListener);
      }
      catch (Exception ex)
      {
         logger.error ("SettingsFragment.onCreatePreferences ", ex);
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

   /**
    * Show or hide the Developer section based on the current settings.
    */
   private void showHideDeveloperSettings ()
   {
      PreferenceManager preferenceManager = getPreferenceManager ();
      Preference showAdvanced = preferenceManager.findPreference (Settings.KEY_ENABLE_DEVELOPER_MODE);
      boolean advancedAvailable = Settings.isDeveloper ();

      if (showAdvanced != null)
         showAdvanced.setVisible (advancedAvailable);

      boolean visibility = Settings.isDeveloper ();
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

   /**
    * Set the visibility of a preference by key name
    * @param key Name of the preference
    * @param visibility Visible or not
    */
   private void setVisible (String key, boolean visibility)
   {
      Preference pref = getPreferenceManager ().findPreference (key);
      if (pref != null)
         pref.setVisible (visibility);
   }

   /**
    * Set the input type of the preference.
    * @param key Key of preference
    * @param inputType Input type such as InputType.TYPE_CLASS_NUMBER
    */
   private void setInputType (String key, int inputType)
   {
      EditTextPreference editTextPreference = getPreferenceManager ().findPreference (key);
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
