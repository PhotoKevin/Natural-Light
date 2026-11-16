package com.blackholeofphotography.naturallight.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.blackholeofphotography.naturallight.BuildConfig;
import com.blackholeofphotography.naturallight.Settings;
import com.blackholeofphotography.naturallight.databinding.PermissionFragmentBinding;


public class PermissionFragment extends Fragment
{
   PermissionFragmentBinding binding;

   public PermissionFragment ()
   {
      // Required empty public constructor
   }

   @Override
   public void onCreate (Bundle savedInstanceState)
   {
      super.onCreate (savedInstanceState);

   }

   @Override
   public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
   {
      binding = PermissionFragmentBinding.inflate (inflater);
      //MainActivity.setToolbarTitle ("Permission");

      binding.btnOk.setOnClickListener (this::onOK);
      binding.btnCancel.setOnClickListener (this::onCancel);

      return binding.getRoot ();
   }

   private void onCancel (View view)
   {
      NavController navController = Navigation.findNavController (view);
      navController.navigateUp ();
   }

   private void onOK (View view)
   {
      String inputText = binding.edtAccessCode.getText ().toString ().strip ();

      if (inputText.equals (BuildConfig.KEY_DEVELOPER))
         Settings.setDeveloper (true);

      NavController navController = Navigation.findNavController (view);
      navController.navigateUp ();
   }
}