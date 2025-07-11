package com.blackholeofphotography.naturallight.ui.locations;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.blackholeofphotography.naturallight.ASTools;
import com.blackholeofphotography.naturallight.Location;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.Settings;

import java.util.ArrayList;

public class LocationsRecycleAdapter
      extends RecyclerView.Adapter<LocationsRecycleAdapter.RecycleViewHolder>

{
   private static final String LOG_TAG = "LocationsRecycleAdapter";
   private ArrayList<Location> dataProviders = new ArrayList<> ();
   private final LayoutInflater mInflater;

   public LocationsRecycleAdapter (Context context, Activity ignoredActivity)
   {
      mInflater = LayoutInflater.from (context);
   }

   @SuppressLint("NotifyDataSetChanged")
   public void setDataProviders (ArrayList<Location> dataProviders)
   {
      this.dataProviders = dataProviders;
      notifyDataSetChanged ();
   }

   @NonNull
   @Override
   public LocationsRecycleAdapter.RecycleViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType)
   {
      View view = mInflater.inflate (R.layout.location_view_item, parent, false);
      return new RecycleViewHolder (view);
   }

   @SuppressLint("DefaultLocale")
   @Override
   public void onBindViewHolder (LocationsRecycleAdapter.RecycleViewHolder holder, int position)
   {
      Location dataProvider = dataProviders.get (position);
      holder.locationTitle.setText (dataProvider.getTitle ());
      holder.latitudeLongitude.setText (ASTools.formatGeoPoint (dataProvider.getPoint ()));
      if (dataProvider.getUseCurrentTime ())
         holder.dateTime.setText ("");
      else
         holder.dateTime.setText (ASTools.formatDateTime (dataProvider.getDateTime ()));
   }

   @Override
   public int getItemCount ()
   {
      if (dataProviders == null)
         return 0;
      return dataProviders.size ();
   }

   // stores and recycles views as they are scrolled off screen
   public class RecycleViewHolder extends RecyclerView.ViewHolder
   {
      final TextView locationTitle;
      final TextView latitudeLongitude;
      final TextView dateTime;

      public RecycleViewHolder (View itemView)
      {
         super (itemView);
         locationTitle = itemView.findViewById (R.id.locationName);
         latitudeLongitude = itemView.findViewById (R.id.latitudeLongitude);
         dateTime = itemView.findViewById (R.id.datetime);

         final View deleteButton = itemView.findViewById (R.id.buttonDelete);


         deleteButton.setOnClickListener (new View.OnClickListener ()
         {
            @Override
            public void onClick (View v)
            {
               Context context = v.getContext ();

               final String dialogMessage = context.getResources ().getString (R.string.delete_message);
               final String dialogPositive = context.getResources ().getString (R.string.delete_positive);
               final String dialogNegative = context.getResources ().getString (R.string.delete_negative);

               AlertDialog.Builder builder = new AlertDialog.Builder (context);
               builder.setMessage (dialogMessage)
                     .setPositiveButton (dialogPositive, new DialogInterface.OnClickListener ()
                     {
                        public void onClick (DialogInterface dialog, int id)
                        {
                           int position = getAdapterPosition ();
                           final Location location = dataProviders.get (position);
                           Settings.removeLocation (location.getUid ());
                           // Note: The dataProviders is the same list as Settings. Removing it
                           // from Settings removes it from the dataProviders.
                           notifyItemChanged (position);

                           dialog.dismiss ();
                        }
                     })
                     .setNegativeButton (dialogNegative, new DialogInterface.OnClickListener ()
                     {
                        public void onClick (DialogInterface dialog, int id)
                        {
                           dialog.dismiss ();
                        }
                     })
                     .show ();
            }
         });

         ConstraintLayout ll = itemView.findViewById (R.id.locationViewItem);
         ll.setOnLongClickListener (new View.OnLongClickListener ()
         {
            @Override
            public boolean onLongClick (View view)
            {
               final Location location = dataProviders.get (getAdapterPosition ());
               NavController navController = Navigation.findNavController (itemView);
               LocationsFragmentDirections.ActionNavLocationsToEditLocationFragment action =
                  LocationsFragmentDirections.actionNavLocationsToEditLocationFragment (location.getUid ());
               navController.navigate (action);

               return true;
            }
         });
         ll.setOnClickListener (new View.OnClickListener ()
         {
            @Override
            public void onClick (View view)
            {
               Log.d (LOG_TAG, "OnClick");
               final Location location = dataProviders.get (getAdapterPosition ());
               NavController navController = Navigation.findNavController (itemView);

               final LocationsFragmentDirections.ActionNavLocationsToNavMap action = LocationsFragmentDirections.actionNavLocationsToNavMap ();
               action.setAUid (location.getUid ());
               navController.navigate (action);
            }
         });
      }
   }
   
}
