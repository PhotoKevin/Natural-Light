
package com.blackholeofphotography.naturallight.ui.about;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackholeofphotography.naturallight.BuildConfig;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.databinding.AboutFragmentBinding;

import java.util.ArrayList;

public class AboutFragment extends Fragment
{
   private static final String LOG_TAG = "AboutFragment";
   private LibrariesRecyclerAdapter adapter = null;

   AboutFragmentBinding binding;


   @Nullable
   @Override
   public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
   {
      try
      {
         binding = AboutFragmentBinding.inflate (inflater, container, false);
         View root = binding.getRoot ();
         final String versionName = String.format (getResources().getString(R.string.version), BuildConfig.VERSION_CODE);
         binding.versionNumber.setText (versionName);

         binding.version.setText (BuildConfig.VERSION_NAME);
         Linkify.addLinks (binding.githubLink, Linkify.WEB_URLS);

         RecyclerView recyclerView = root.findViewById (R.id.librariesList);

         adapter = new LibrariesRecyclerAdapter (this.getContext (), getActivity ());
         recyclerView.setHasFixedSize (true);
         RecyclerView.LayoutManager manager = new LinearLayoutManager (this.getContext ());
         recyclerView.setLayoutManager (manager);
         recyclerView.setAdapter (adapter);

         new Thread (this::loadLibraries).start ();

         return  root;
      }
      catch (Exception e)
      {
         Log.e (LOG_TAG, e.toString ());
      }
      return null;
   }


   public void loadLibraries ()
   {
      Activity activity = getActivity ();
      if (activity != null)
      {
         activity.runOnUiThread (new Runnable ()
         {
            @Override
            public void run ()
            {
               ArrayList<ThirdPartyLibrary> dataProviders = new ArrayList<> ();

               dataProviders.add (new ThirdPartyLibrary ("Timezone data from Roman Iakovlev's TimeShape\nhttps://github.com/RomanIakovlev/timeshape"));
               dataProviders.add (new ThirdPartyLibrary ("Map data provided by Open Street Map\nhttps://www.openstreetmap.org"));

               adapter.setDataProviders (dataProviders);
            }
         });
      }

   }
}
