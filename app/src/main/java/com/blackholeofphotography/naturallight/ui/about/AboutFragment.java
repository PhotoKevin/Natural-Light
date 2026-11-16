
package com.blackholeofphotography.naturallight.ui.about;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackholeofphotography.naturallight.BuildConfig;
import com.blackholeofphotography.naturallight.R;
import com.blackholeofphotography.naturallight.databinding.AboutFragmentBinding;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class AboutFragment extends Fragment
{
   private static final org.slf4j.Logger logger = LoggerFactory.getLogger (AboutFragment.class);
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
         binding.PrivacyLayout.setOnClickListener (this::onPrivacyClick);
         binding.bypIcon.setOnClickListener (this::onIconClick);

         binding.version.setText (BuildConfig.VERSION_NAME);
         Linkify.addLinks (binding.githubLink, Linkify.WEB_URLS);

         RecyclerView recyclerView = root.findViewById (R.id.librariesList);

         adapter = new LibrariesRecyclerAdapter (this.getContext (), getActivity ());
         RecyclerView.LayoutManager manager = new LinearLayoutManager (this.getContext ());
         recyclerView.setLayoutManager (manager);
         recyclerView.setAdapter (adapter);

         new Thread (this::loadLibraries).start ();

         return  root;
      }
      catch (Exception e)
      {
         logger.error ("AboutFragment.onCreateView", e);
      }
      return null;
   }

   long pressedTime = 0;
   int pressedCount = 0;

   private void onIconClick (View view)
   {
      if (pressedTime + 2000 < System.currentTimeMillis ())
         pressedCount = 0;
      else
      {
         pressedCount += 1;
         logger.info ("pressedCount: {}", pressedCount);
         if (pressedCount > 4)
         {
            NavController navController = Navigation.findNavController (view);
            navController.navigate (R.id.permissionFragment);
         }
      }

      pressedTime = System.currentTimeMillis ();
   }

   private void onPrivacyClick (View ignoredView)
   {
      Activity activity = getActivity ();
      assert activity != null;
      showPopup (activity, new Point (0, 0));
   }

   // Source - https://stackoverflow.com/a/15153733
   // Posted by Emil Adz, modified by community. See post 'Timeline' for change history
   // Retrieved 2026-03-06, License - CC BY-SA 3.0

   private void showPopup (final Activity context, Point p)
   {
      PopupWindow popupWindow;

      // Inflate the popup_layout.xml
      ConstraintLayout viewGroup = context.findViewById (R.id.layout_privacy);
      LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
      View layout = layoutInflater.inflate (R.layout.privacy_fragment, viewGroup);

      // Creating the PopupWindow
      popupWindow = new PopupWindow (context);
      popupWindow.setContentView (layout);
      popupWindow.setWidth (ViewGroup.LayoutParams.MATCH_PARENT);
      popupWindow.setFocusable (true);

      // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
      int OFFSET_X = -20;
      int OFFSET_Y = 95;

      // Displaying the popup at the specified location, + offsets.
      popupWindow.showAtLocation (layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

      // Getting a reference to Close button, and close the popup when clicked.
      Button close = layout.findViewById (R.id.close);
      close.setOnClickListener (new View.OnClickListener ()
      {
         @Override
         public void onClick (View v)
         {
            popupWindow.dismiss ();
         }
      });

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
