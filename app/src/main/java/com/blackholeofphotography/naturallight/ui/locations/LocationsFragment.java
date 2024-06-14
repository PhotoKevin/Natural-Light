package com.blackholeofphotography.naturallight.ui.locations;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackholeofphotography.naturallight.Location;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.Settings;
import com.blackholeofphotography.naturallight.databinding.LocationsFragmentBinding;

import java.util.ArrayList;

public class LocationsFragment extends Fragment
{
   private LocationsRecycleAdapter adapter = null;

   LocationsFragmentBinding binding;

   public View onCreateView (@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
   {

      binding = LocationsFragmentBinding.inflate (inflater, container, false);
      View root = binding.getRoot ();

      RecyclerView recyclerView = root.findViewById (R.id.locationRecyclerView);

      adapter = new LocationsRecycleAdapter (this.getContext (), getActivity ());
      recyclerView.setHasFixedSize (true);
      RecyclerView.LayoutManager manager = new LinearLayoutManager (this.getContext ());
      recyclerView.setLayoutManager (manager);
      recyclerView.setAdapter (adapter);

      new Thread (this::loadLocations).start ();

      return root;
   }


   public void loadLocations ()
   {
      Activity activity = getActivity ();
      if (activity != null)
      {
         activity.runOnUiThread (new Runnable ()
         {
            @Override
            public void run ()
            {
               ArrayList<Location> dataProviders = Settings.getLocationsCopy ();
               adapter.setDataProviders (dataProviders);
            }
         });
      }

   }


   @Override
   public void onDestroyView ()
   {
      super.onDestroyView ();
      binding = null;
   }

}
