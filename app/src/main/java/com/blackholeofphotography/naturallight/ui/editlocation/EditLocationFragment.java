package com.blackholeofphotography.naturallight.ui.editlocation;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.blackholeofphotography.naturallight.DisplayStatus;
import com.blackholeofphotography.naturallight.Location;
import com.blackholeofphotography.naturallight.MainActivity;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.Settings;
import com.blackholeofphotography.naturallight.databinding.EditLocationFragmentBinding;

import org.mapsforge.core.model.LatLong;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EditLocationFragment extends Fragment
{
   private static final org.slf4j.Logger logger = LoggerFactory.getLogger (EditLocationFragment.class);
   private EditLocationFragmentBinding binding;
   private Location mLocation;
   private TextView mTitle;
   private TextView mLatitude;
   private TextView mLongitude;
   private ZonedDateTime mTimeStamp;
   private ZoneId mZoneId;
   private DatePicker mDatePicker;
   private TimePicker mTimePicker;
   private CheckBox mUseCurrentTime;
   private Spinner mZoomSpinner;

   public View onCreateView (@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
   {
      Context context = getContext ();
      assert context != null;

      binding = EditLocationFragmentBinding.inflate (inflater, container, false);
      View root = binding.getRoot ();

      mTitle = binding.textTitle;
      mLatitude = binding.textLatitude;
      mLongitude = binding.textLongitude;
      mUseCurrentTime = binding.useCurrentTime;
      mDatePicker = binding.datePicker1;
      mTimePicker = binding.timePicker1;
      mZoomSpinner = binding.spinZoomLevel;

      String[] zoomLevels = getResources ().getStringArray (R.array.zoom_entries);
      ArrayAdapter<String> zoomAdapter = new ArrayAdapter<> (context, R.layout.zoom_level_spinner_item, zoomLevels);
      mZoomSpinner.setAdapter (zoomAdapter);

      try
      {
         final Bundle arguments = getArguments ();
         if (arguments == null)
            mLocation = new Location ("New location", "snippet", new LatLong (0, 0.0), ZonedDateTime.now (), true, 1);
         else
         {
            EditLocationFragmentArgs args = EditLocationFragmentArgs.fromBundle (arguments);
            String aUid = args.getAUid ();
            mLocation = Settings.getLocation (aUid);
         }
      }
      catch (Exception ex)
      {
         logger.error ("EditLocationFragment.onCreateView ", ex);
      }

      mTimeStamp = mLocation.getDateTime ();
      mZoneId = MainActivity.getZoneId (mLocation.getPoint ());
      mTimeStamp = mTimeStamp.withZoneSameLocal (mZoneId);

      setSpinnerSelection (mZoomSpinner, zoomAdapter, String.valueOf ((int) mLocation.getZoomLevel ()));

      binding.btnDelete.setOnClickListener (this::onDeleteClick);
      mUseCurrentTime.setOnCheckedChangeListener (this::onUseCurrentTimeChanged);
      mTimePicker.setOnTimeChangedListener (this::onTimeChanged);

      return root;
   }

   @SuppressWarnings("unused")
   public void onUseCurrentTimeChanged (@NonNull CompoundButton buttonView, boolean isChecked)
   {
      mDatePicker.setEnabled (!isChecked);
      mTimePicker.setEnabled (!isChecked);
   }

   public void onDeleteClick (View v)
   {
      AlertDialog.Builder builder = new AlertDialog.Builder (v.getContext ());
      builder.setMessage ("Delete location?")
            .setPositiveButton ("Delete", new DialogInterface.OnClickListener ()
            {
               public void onClick (DialogInterface dialog, int id)
               {
                  // Handle Ok
                  Settings.removeLocation (mLocation.getUid ());
                  NavController navController = Navigation.findNavController (binding.getRoot ());

                  navController.navigate (R.id.nav_map);
                  dialog.dismiss ();
               }
            })
            .setNegativeButton ("Cancel", new DialogInterface.OnClickListener ()
            {
               public void onClick (DialogInterface dialog, int id)
               {
                  dialog.dismiss ();
               }
            })
            .show ();
   }

   @SuppressWarnings("unused")
   public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth)
   {
      // monthOfYear comes in as 0-11
      mTimeStamp = ZonedDateTime.of (year, monthOfYear + 1, dayOfMonth, mTimeStamp.getHour (), mTimeStamp.getMinute (), 0, 0, mZoneId);
   }

   @SuppressWarnings("unused")
   public void onTimeChanged (TimePicker view, int hourOfDay, int minute)
   {
      mTimeStamp = ZonedDateTime.of (mTimeStamp.getYear (), mTimeStamp.getMonthValue (), mTimeStamp.getDayOfMonth (), hourOfDay, minute, 0, 0, mZoneId);
   }

   private void setSpinnerSelection (Spinner spinner, ArrayAdapter<String> adapter, String selected)
   {
      for (int i = 0; i < adapter.getCount (); i++)
      {
         Object item = adapter.getItem (i);
         if (item != null && item.equals (selected))
            spinner.setSelection (i);
      }
   }

   @Override
   public void onResume ()
   {
      super.onResume ();

      if (mLocation != null)
      {
         mTitle.setText (mLocation.getTitle ());
         mLatitude.setText (Location.formatGeoPoint (mLocation.getPoint ().getLatitude ()));
         mLongitude.setText (Location.formatGeoPoint (mLocation.getPoint ().getLongitude ()));

         mUseCurrentTime.setChecked (mLocation.getUseCurrentTime ());
         mDatePicker.setEnabled (!mLocation.getUseCurrentTime ());
         mTimePicker.setEnabled (!mLocation.getUseCurrentTime ());
         mDatePicker.init (mTimeStamp.getYear (), mTimeStamp.getMonthValue () - 1, mTimeStamp.getDayOfMonth (), this::onDateChanged);

         mTimePicker.setHour (mTimeStamp.getHour ());
         mTimePicker.setMinute (mTimeStamp.getMinute ());

         mTimeStamp = mLocation.getDateTime ();

         if (mLocation.getUseCurrentTime ())
            mTimeStamp = ZonedDateTime.now (mLocation.getDateTime ().getZone ());
      }
   }

   @Override
   public void onStop ()
   {
      super.onStop ();
      try
      {
         final double latitude = Double.parseDouble (mLatitude.getText ().toString ());
         final double longitude = Double.parseDouble (mLongitude.getText ().toString ());
         final LatLong pt = new LatLong (latitude, longitude);
         final double zoomLevel = Double.parseDouble (mZoomSpinner.getSelectedItem ().toString ());

         Location newItem = new Location (mLocation.getUid (), mTitle.getText ().toString (), mLocation.getSnippet (), pt, mTimeStamp, mUseCurrentTime.isChecked (), zoomLevel);

         DisplayStatus.setGeoPoint (pt);
         DisplayStatus.setZoomLevel (newItem.getZoomLevel ());
         DisplayStatus.setTimeStamp (newItem.getDateTime ());
         DisplayStatus.setUseCurrentTime (newItem.getUseCurrentTime ());

         Settings.updateLocation (newItem);
         Settings.setUseCurrentTime (mUseCurrentTime.isChecked ());
         Settings.saveToBackingStore ();
      }
      catch (NumberFormatException ex)
      {
         logger.error ("EditLocationFragment.onSteop ", ex);
      }
   }

   @Override
   public void onDestroyView ()
   {
      super.onDestroyView ();
      binding = null;
   }
}
