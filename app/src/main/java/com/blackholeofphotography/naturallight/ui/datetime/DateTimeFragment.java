package com.blackholeofphotography.naturallight.ui.datetime;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blackholeofphotography.naturallight.DisplayStatus;
import com.blackholeofphotography.naturallight.databinding.DateTimeFragmentBinding;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeFragment extends Fragment implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener,
      CompoundButton.OnCheckedChangeListener

{
   private final static String LOG_TAG = "DateTimeFragment";
   private DateTimeFragmentBinding binding;
   ZonedDateTime mTimeStamp;

   DatePicker mDatePicker;
   TimePicker mTimePicker;
   CheckBox mUseCurrentTime;


   public View onCreateView (@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
   {
      Context context = getContext ();
      assert context != null;

      binding = DateTimeFragmentBinding.inflate (inflater, container, false);
      View root = binding.getRoot ();

      mUseCurrentTime = binding.useCurrentTime;
      mDatePicker = binding.datePicker1;
      mTimePicker = binding.timePicker1;

      mUseCurrentTime.setChecked (DisplayStatus.useCurrentTime ());
      mDatePicker.setEnabled (!DisplayStatus.useCurrentTime ());
      mTimePicker.setEnabled (!DisplayStatus.useCurrentTime ());
      mTimeStamp = DisplayStatus.getTimeStamp ();
      mUseCurrentTime.setOnCheckedChangeListener (this);
      mDatePicker.init (mTimeStamp.getYear (), mTimeStamp.getMonthValue () - 1, mTimeStamp.getDayOfMonth (), this);

      mTimePicker.setHour (mTimeStamp.getHour ());
      mTimePicker.setMinute (mTimeStamp.getMinute ());
      mTimePicker.setOnTimeChangedListener (this);

      return root;
   }

   @Override
   public void onCheckedChanged (@NonNull CompoundButton buttonView, boolean isChecked)
   {
      mDatePicker.setEnabled (!isChecked);
      mTimePicker.setEnabled (!isChecked);
   }


   @Override
   public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth)
   {
      // monthOfYear comes in as 0-11
      ZoneId zi = mTimeStamp.getZone ();
      mTimeStamp = ZonedDateTime.of (year, monthOfYear + 1, dayOfMonth, mTimeStamp.getHour (), mTimeStamp.getMinute (), 0, 0, zi);
   }

   @Override
   public void onTimeChanged (TimePicker view, int hourOfDay, int minute)
   {
      ZoneId zi = mTimeStamp.getZone ();
      mTimeStamp = ZonedDateTime.of (mTimeStamp.getYear (), mTimeStamp.getMonthValue (), mTimeStamp.getDayOfMonth (), hourOfDay, minute, 0, 0, zi);
   }


   @Override
   public void onStop ()
   {
      super.onStop ();
      try
      {
         DisplayStatus.setTimeStamp (mTimeStamp);
         DisplayStatus.setUseCurrentTime (mUseCurrentTime.isChecked ());
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
