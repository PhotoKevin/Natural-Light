package com.blackholeofphotography.naturallight.ui.importexport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blackholeofphotography.bhtools.ASTools;
import com.blackholeofphotography.naturallight.Location;
import com.blackholeofphotography.naturallight.MainActivity;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.Settings;
import com.blackholeofphotography.naturallight.databinding.ImportExportFragmentBinding;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class ImportExportFragment extends Fragment
{
   private static final String LOG_TAG = "ImportExportFragment";

   ImportExportFragmentBinding binding;
   ActivityResultLauncher<Intent> mExportLocationsResult;
   ActivityResultLauncher<Intent> mImportLocationsResult;

   @Nullable
   @Override
   public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
   {
      try
      {
         binding = ImportExportFragmentBinding.inflate (inflater, container, false);
         View root = binding.getRoot ();

         binding.exportLocations.setOnClickListener (new View.OnClickListener ()
         {
            @Override
            public void onClick (View v)
            {
               exportLocations ();
            }
         });

         binding.importLocations.setOnClickListener (new View.OnClickListener ()
         {
            @Override
            public void onClick (View v)
            {
               importLocations ();
            }
         });

         mImportLocationsResult = registerForActivityResult (new ActivityResultContracts.StartActivityForResult (), this::onImportResult);
         mExportLocationsResult = registerForActivityResult (new ActivityResultContracts.StartActivityForResult (), this::onExportResult);
         return root;
      }
      catch (Exception ex)
      {
         Log.e (LOG_TAG, ex.toString ());
      }
      return null;
   }

   private void invalidImportAlert ()
   {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setCancelable (true);
      //builder.setTitle (R.string.invalid_import);
      builder.setMessage (R.string.invalid_import);
      AlertDialog dialog = builder.create ();
      dialog.show ();
   }

   public void onImportResult (ActivityResult result)
   {
      if (result.getResultCode () == Activity.RESULT_OK)
      {
         ContentResolver resolver = getResolver (result);
         Uri contentUri = getUri (result);
         if (resolver != null && contentUri != null)
         {
            try (InputStream in = resolver.openInputStream (contentUri))
            {
               final String streamContents = ASTools.getStreamContents (in);
               final ArrayList<Location> locations = Location.fromJson (streamContents);
               if (locations != null)
               {
                  for (var l : locations)
                     Settings.addLocation (l);
               }
            }
            catch (IOException e)
            {
               invalidImportAlert ();
            }
         }
      }
   }

   public void onExportResult (ActivityResult result)
   {
      if (result.getResultCode () == Activity.RESULT_OK)
      {
         ContentResolver resolver = getResolver (result);
         Uri contentUri = getUri (result);
         if (resolver != null && contentUri != null)
         {
            try (ParcelFileDescriptor pfd = resolver.openFileDescriptor (contentUri, "w"))
            {
               assert pfd != null;
               try (FileOutputStream fileOutputStream = new FileOutputStream (pfd.getFileDescriptor ()))
               {
                  final String json = Location.toJson (Settings.getLocationsCopy ());
                  fileOutputStream.write (json.getBytes ());
               }
            }
            catch (IOException e)
            {
               Toast.makeText (MainActivity.getContext (), R.string.invalid_import, Toast.LENGTH_LONG).show();
            }
         }
      }
   }

   private Uri getUri (ActivityResult result)
   {
      Intent intent = result.getData ();
      if (intent != null)
         return intent.getData ();

      return null;
   }

   private ContentResolver getResolver (ActivityResult result)
   {
      Intent intent = result.getData ();
      if (intent != null)
      {
         Uri contentUri = intent.getData ();
         if (contentUri != null)
         {
            final FragmentActivity activity = getActivity ();
            if (activity != null)
               return getActivity ().getContentResolver ();
         }

      }
      return null;
   }

   private void exportLocations ()
   {
      Intent saveFile = new Intent (Intent.ACTION_CREATE_DOCUMENT);

      saveFile.setType ("*/*");
      saveFile.putExtra (Intent.EXTRA_LOCAL_ONLY, true);
      saveFile.addCategory (Intent.CATEGORY_OPENABLE);

      String now = ASTools.formatDateTime (ZonedDateTime.now ());

      saveFile.putExtra (Intent.EXTRA_TITLE, String.format ("%s.nat-light", now));
      mExportLocationsResult.launch (Intent.createChooser (saveFile, "Save location"));
   }

   private void importLocations ()
   {

      Intent chooseFile = new Intent (Intent.ACTION_GET_CONTENT);
      chooseFile.setType ("*/*");
      chooseFile.putExtra (Intent.EXTRA_LOCAL_ONLY, true);
      Intent chooser = Intent.createChooser (chooseFile, "Choose a file");

      mImportLocationsResult.launch (chooser);
   }
}
