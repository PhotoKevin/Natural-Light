package com.blackholeofphotography.naturallight.ui.about;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackholeofphotography.naturallight.R;

import java.util.ArrayList;


public class LibrariesRecyclerAdapter
      extends RecyclerView.Adapter<LibrariesRecyclerAdapter.RecycleViewHolder>
{
   private static final String LOG_TAG = "LibrariesRecyclerAdapter";
   private ArrayList<ThirdPartyLibrary> dataProviders = new ArrayList<> ();
   private final LayoutInflater mInflater;

   public LibrariesRecyclerAdapter (Context context, Activity ignoredActivity)
   {
      mInflater = LayoutInflater.from (context);
   }

   @SuppressLint("NotifyDataSetChanged")
   public void setDataProviders (ArrayList<ThirdPartyLibrary> dataProviders)
   {
      this.dataProviders = dataProviders;
      Log.v (LOG_TAG, String.format ("%d entries", dataProviders.size ()));
      notifyDataSetChanged ();
   }

   @NonNull
   @Override
   public com.blackholeofphotography.naturallight.ui.about.LibrariesRecyclerAdapter.RecycleViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType)
   {
      View view = mInflater.inflate (R.layout.third_party_library_view_item, parent, false);
      return new RecycleViewHolder (view);
   }

   @SuppressLint("DefaultLocale")
   @Override
   public void onBindViewHolder (com.blackholeofphotography.naturallight.ui.about.LibrariesRecyclerAdapter.RecycleViewHolder holder, int position)
   {
      ThirdPartyLibrary dataProvider = dataProviders.get (position);
      holder.locationTitle.setText (dataProvider.getText ());
      Linkify.addLinks (holder.locationTitle, Linkify.WEB_URLS);
      Log.v (LOG_TAG, String.format ("Set text at %d to %s", position, dataProvider.getText ()));
   }

   @Override
   public int getItemCount ()
   {
      if (dataProviders == null)
         return 0;
      return dataProviders.size ();
   }

   // stores and recycles views as they are scrolled off screen
   public static class RecycleViewHolder extends RecyclerView.ViewHolder
   {
      final TextView locationTitle;

      public RecycleViewHolder (View itemView)
      {
         super (itemView);
         Log.v (LOG_TAG, "new item");
         locationTitle = itemView.findViewById (R.id.libraryText);
         locationTitle.setText ("Huh");
      }
   }

}
