package com.blackholeofphotography.naturallight.ui.editlocation;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.blackholeofphotography.naturallight.ASTools;
import com.blackholeofphotography.naturallight.DisplayStatus;
import com.blackholeofphotography.naturallight.Location;
import com.blackholeofphotography.naturallight.MainActivity;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.Settings;
import com.blackholeofphotography.naturallight.databinding.EditLocationFragmentBinding;

import org.osmdroid.util.GeoPoint;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EditLocationFragment extends Fragment implements TimePicker.OnTimeChangedListener, DatePicker.OnDateChangedListener
   , CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
   private final static String LOG_TAG = "EditLocationFragment";
   private EditLocationFragmentBinding binding;
   private Location mLocation;
   TextView mTitle;
   TextView mLatitude;
   TextView mLongitude;
   ZonedDateTime mTimeStamp;
   ZoneId mZoneId;
   DatePicker mDatePicker;
   TimePicker mTimePicker;
   CheckBox mUseCurrentTime;
   TextView mTimeZone;
   ImageButton mDeleteButton;
   Spinner mZoomSpinner;

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
      mTimeZone = binding.timeZone;
      mDeleteButton = binding.btnDelete;
      mZoomSpinner = binding.spinZoomLevel;

      String[] zoomLevels = getResources ().getStringArray (R.array.zoom_entries);
      ArrayAdapter<String> zoomAdapter = new ArrayAdapter<> (context, R.layout.zoom_level_spinner_item, zoomLevels);
      mZoomSpinner.setAdapter (zoomAdapter);

      try
      {
         EditLocationFragmentArgs args = EditLocationFragmentArgs.fromBundle (getArguments ());
         String aUid = args.getAUid ();
         mLocation = Settings.getLocation (aUid);
      }
      catch (Exception ex)
      {
         Log.e (LOG_TAG, ex.toString ());
      }

      mTimeStamp = mLocation.getDateTime ();
      mZoneId = MainActivity.getZoneId (mLocation.getPoint ());
      mTimeStamp = mTimeStamp.withZoneSameLocal (mZoneId);

      setSpinnerSelection (mZoomSpinner, zoomAdapter, String.valueOf ((int) mLocation.getZoomLevel ()));

      mDeleteButton.setOnClickListener (this);
      mUseCurrentTime.setOnCheckedChangeListener (this);
      mTimePicker.setOnTimeChangedListener (this);

      return root;
   }

   @Override
   public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
   {
      mDatePicker.setEnabled (!isChecked);
      mTimePicker.setEnabled (!isChecked);
   }

   @Override
   public void onClick (View v)
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


   @Override
   public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth)
   {
      // monthOfYear comes in as 0-11
      mTimeStamp = ZonedDateTime.of (year, monthOfYear + 1, dayOfMonth, mTimeStamp.getHour (), mTimeStamp.getMinute (), 0, 0, mZoneId);
   }

   @Override
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
         mLatitude.setText (ASTools.formatGeoPoint (mLocation.getPoint ().getLatitude ()));
         mLongitude.setText (ASTools.formatGeoPoint (mLocation.getPoint ().getLongitude ()));

         mUseCurrentTime.setChecked (mLocation.getUseCurrentTime ());
         mDatePicker.setEnabled (!mLocation.getUseCurrentTime ());
         mTimePicker.setEnabled (!mLocation.getUseCurrentTime ());
         mDatePicker.init (mTimeStamp.getYear (), mTimeStamp.getMonthValue () - 1, mTimeStamp.getDayOfMonth (), this);

         mTimePicker.setHour (mTimeStamp.getHour ());
         mTimePicker.setMinute (mTimeStamp.getMinute ());


         mTimeStamp = mLocation.getDateTime ();

      if (mLocation.getUseCurrentTime ())
      {
      mTimeStamp = ZonedDateTime.now (mLocation.getDateTime ().getZone ());
      }}
   }

   @Override
   public void onStop ()
   {
      super.onStop ();
      try
      {
         final double latitude = Double.parseDouble (mLatitude.getText ().toString ());
         final double longitude = Double.parseDouble (mLongitude.getText ().toString ());
         final GeoPoint pt = new GeoPoint (latitude, longitude);
         final double zoomLevel = Double.parseDouble (mZoomSpinner.getSelectedItem ().toString ());

         Location newItem = new Location (mLocation.getUid (), mTitle.getText ().toString (), mLocation.getSnippet (), pt, mTimeStamp, mUseCurrentTime.isChecked (), zoomLevel);

         DisplayStatus.setLocation (pt);
         DisplayStatus.setZoomLevel (newItem.getZoomLevel ());
         DisplayStatus.setTimeStamp (newItem.getDateTime ());
         DisplayStatus.setUseCurrentTime (newItem.getUseCurrentTime ());

         Settings.updateLocation (newItem);
         Settings.setUseCurrentTime (mUseCurrentTime.isChecked ());
      }
      catch (NumberFormatException ex)
      {
         Log.e (LOG_TAG, ex.toString ());
      }
   }

   @Override
   public void onDestroyView ()
   {
      super.onDestroyView ();
      binding = null;
   }
}
