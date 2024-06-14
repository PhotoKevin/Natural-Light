
package com.blackholeofphotography.naturallight.ui.about;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blackholeofphotography.naturallight.databinding.AboutFragmentBinding;

public class AboutFragment extends Fragment
{
   private static final String LOG_TAG = "AboutFragment";

   @Nullable
   @Override
   public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
   {
      try
      {
         AboutFragmentBinding binding = AboutFragmentBinding.inflate (inflater, container, false);
         View root = binding.getRoot ();
         binding.webAbout.loadUrl ("file:///android_asset/about.html");
         return  root;
      }
      catch (Exception e)
      {
         Log.e (LOG_TAG, e.toString ());
      }
      return null;
   }
}
