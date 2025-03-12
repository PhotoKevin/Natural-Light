package com.blackholeofphotography.naturallight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;

import org.osmdroid.library.R;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.IOverlayMenuProvider;
import org.osmdroid.views.overlay.Overlay;


public class CompassOverlay extends Overlay implements IOverlayMenuProvider
{
   @SuppressWarnings ("unused")
   private static final String LOG_TAG = "CompassOverlay";
   private Paint sSmoothPaint = new Paint (Paint.FILTER_BITMAP_FLAG);
   protected MapView mMapView;
   private final Display mDisplay;
   protected Bitmap mCompassFrameBitmap;
   protected Bitmap mCompassRoseBitmap;
   private final Matrix mCompassMatrix = new Matrix ();
   private boolean mIsCompassEnabled;
   private boolean wasEnabledOnPause = false;

   protected final float mCompassFrameCenterX;
   protected final float mCompassFrameCenterY;
   public static final int MENU_COMPASS = getSafeMenuId ();
   private boolean mOptionsMenuEnabled = true;


   // @SuppressWarnings("deprecation")
   public CompassOverlay (Context context,
                           MapView mapView)
   {
      super ();
      mMapView = mapView;
      final WindowManager windowManager = (WindowManager) context
            .getSystemService (Context.WINDOW_SERVICE);

      mDisplay = windowManager.getDefaultDisplay ();
      // Requires a higher API (30) than I want
      //mDisplay = context.getDisplay ();

      createCompassFramePicture (context);

      mCompassFrameCenterX = mCompassFrameBitmap.getWidth () / 2f - 0.5f;
      mCompassFrameCenterY = mCompassFrameBitmap.getHeight () / 2f - 0.5f;
   }

   @Override
   public void onPause ()
   {
      wasEnabledOnPause = mIsCompassEnabled;
      super.onPause ();
   }

   @Override
   public void onResume ()
   {
      super.onResume ();
      if (wasEnabledOnPause)
      {
         this.enableCompass ();
      }
   }

   @Override
   public void onDetach (MapView mapView)
   {
      this.mMapView = null;
      sSmoothPaint = null;
      this.disableCompass ();
      mCompassFrameBitmap.recycle ();
      if (mCompassRoseBitmap != null)
         mCompassRoseBitmap.recycle ();
      super.onDetach (mapView);
   }

   private void invalidateCompass ()
   {
      Rect screenRect = mMapView.getProjection ().getScreenRect ();

      final int frameLeft = screenRect.left
            + (int) Math.ceil (screenRect.exactCenterX () - mCompassFrameCenterX);
      final int frameTop = screenRect.top
            + (int) Math.ceil (screenRect.exactCenterY () - mCompassFrameCenterY);
      final int frameRight = screenRect.left
            + (int) Math.ceil (screenRect.exactCenterX () + mCompassFrameCenterX);
      final int frameBottom = screenRect.top
            + (int) Math.ceil (screenRect.exactCenterY () + mCompassFrameCenterY);

      // Expand by 2 to cover stroke width
      mMapView.postInvalidateMapCoordinates (frameLeft - 2, frameTop - 2, frameRight + 2,
            frameBottom + 2);
   }

   protected void drawOverlay (final Canvas canvas)
   {
      final Projection proj = mMapView.getProjection ();

      final Rect rect = proj.getScreenRect ();
      final float centerX = rect.exactCenterX ();
      final float centerY = rect.exactCenterY ();


      mCompassMatrix.setTranslate (-mCompassFrameCenterX, -mCompassFrameCenterY);
      mCompassMatrix.postTranslate (centerX, centerY);

      proj.save (canvas, false, true);
      canvas.concat (mCompassMatrix);
      canvas.drawBitmap (mCompassFrameBitmap, 0, 0, sSmoothPaint);
      proj.restore (canvas, true);

      mCompassMatrix.postTranslate (centerX, centerY);

      proj.save (canvas, false, true);
      canvas.concat (mCompassMatrix);

      proj.restore (canvas, true);
   }

   @Override
   public void draw (Canvas c, Projection pProjection)
   {
      if (isCompassEnabled ())
         drawOverlay (c);
   }

   // ===========================================================
   // Menu handling methods
   // ===========================================================

   @Override
   public void setOptionsMenuEnabled (final boolean pOptionsMenuEnabled)
   {
      this.mOptionsMenuEnabled = pOptionsMenuEnabled;
   }

   @Override
   public boolean isOptionsMenuEnabled ()
   {
      return this.mOptionsMenuEnabled;
   }

   @SuppressLint("UseCompatLoadingForDrawables")
   @Override
   public boolean onCreateOptionsMenu (final Menu pMenu, final int pMenuIdOffset,
                                       final MapView pMapView)
   {
      pMenu.add (0, MENU_COMPASS + pMenuIdOffset, Menu.NONE,
                  pMapView.getContext ().getResources ().getString (R.string.compass))
            .setIcon (pMapView.getContext ().getResources ().getDrawable (R.drawable.ic_menu_compass, null))
            .setCheckable (true);

      return true;
   }

   @Override
   public boolean onPrepareOptionsMenu (final Menu pMenu, final int pMenuIdOffset,
                                        final MapView pMapView)
   {
      pMenu.findItem (MENU_COMPASS + pMenuIdOffset).setChecked (this.isCompassEnabled ());
      return false;
   }

   @Override
   public boolean onOptionsItemSelected (final MenuItem pItem, final int pMenuIdOffset,
                                         final MapView pMapView)
   {
      final int menuId = pItem.getItemId () - pMenuIdOffset;
      if (menuId == MENU_COMPASS)
      {
         if (this.isCompassEnabled ())
         {
            this.disableCompass ();
         } else
         {
            this.enableCompass ();
         }
         return true;
      } else
      {
         return false;
      }
   }

   // ===========================================================
   // Methods
   // ===========================================================


   /**
    * Enable receiving orientation updates from the provided IOrientationProvider and show a
    * compass on the map. You will likely want to call enableCompass() from your Activity's
    * Activity.onResume() method, to enable the features of this overlay. Remember to call the
    * corresponding disableCompass() in your Activity's Activity.onPause() method to turn off
    * updates when in the background.
    */
   public void enableCompass ()
   {
      mIsCompassEnabled = true;

      // Update the screen to see changes take effect
      if (mMapView != null)
      {
         this.invalidateCompass ();
      }
   }

   /**
    * Disable orientation updates.
    * <p>
    * Note the behavior has changed since v6.0.0. This method no longer releases
    * references to the orientation provider. Instead, that happens in the onDetached
    * method.
    */
   public void disableCompass ()
   {
      mIsCompassEnabled = false;
   }

   /**
    * If enabled, the map is receiving orientation updates and drawing your location on the map.
    *
    * @return true if enabled, false otherwise
    */
   public boolean isCompassEnabled ()
   {
      return mIsCompassEnabled;
   }

   // ===========================================================
   // Inner and Anonymous Classes
   // ===========================================================

   private Point calculatePointOnCircle (final float centerX, final float centerY,
                                         final float radius, final float degrees)
   {
      // for trigonometry, 0 is pointing east, so subtract 90
      // compass degrees are the wrong way round
      final double dblRadians = Math.toRadians (-degrees + 90);

      final int intX = (int) (radius * Math.cos (dblRadians));
      final int intY = (int) (radius * Math.sin (dblRadians));

      return new Point ((int) centerX + intX, (int) centerY - intY);
   }


   private int getDisplayOrientation ()
   {
      switch (mDisplay.getRotation ())
      {
      case Surface.ROTATION_90:
         return 90;
      case Surface.ROTATION_180:
         return 180;
      case Surface.ROTATION_270:
         return 270;
      case Surface.ROTATION_0:
      default:
         return 0;
      }
   }

   // Called from constructor
   private void createCompassFramePicture (Context context)
   {
      final int widthPixels = context.getResources ().getDisplayMetrics ().widthPixels;
      final int heightPixels = context.getResources ().getDisplayMetrics ().heightPixels;

      final float compassPercentage = Settings.getCompassPercentage () / 100.0F;
      final int radius = (int) (compassPercentage *  Math.min (widthPixels, heightPixels) / 2);


      // The inside of the compass is white and transparent
      final Paint innerPaint = new Paint ();
      innerPaint.setColor (Color.WHITE);
      innerPaint.setAntiAlias (true);
      innerPaint.setStyle (Style.FILL);
      innerPaint.setAlpha (70);

      // The outer part (circle and little triangles) is gray and transparent
      final Paint outerPaint = new Paint ();
      outerPaint.setColor (Color.BLACK);
      outerPaint.setAntiAlias (true);
      outerPaint.setStyle (Style.STROKE);
      outerPaint.setStrokeWidth (2.0f);
      outerPaint.setAlpha (200);
      outerPaint.setTextSize (Settings.getAngleLabelFontSize ());


      final int picBorderWidthAndHeight = (int) (Math.min (widthPixels, heightPixels) * 1.1);
      final int center = picBorderWidthAndHeight / 2;
      if (mCompassFrameBitmap != null)
         mCompassFrameBitmap.recycle ();
      mCompassFrameBitmap = Bitmap.createBitmap (picBorderWidthAndHeight, picBorderWidthAndHeight,
            Config.ARGB_8888);
      final Canvas canvas = new Canvas (mCompassFrameBitmap);

      // draw compass inner circle and border
      canvas.drawCircle (center, center, (float) radius, innerPaint);
      canvas.drawCircle (center, center, (float) radius, outerPaint);

      final int tickInterval = Settings.getTickMarkInterval ();
      for (int degrees=0; degrees<360; degrees+=tickInterval)
      {
         final Point point1 = this.calculatePointOnCircle (center, center, radius, degrees);
         final Point point2 = this.calculatePointOnCircle (center, center, radius-20, degrees);

         canvas.drawLine (point1.x, point1.y, point2.x, point2.y, outerPaint);
      }

      Rect bounds0 = new Rect ();
      String s0 = "W";
      outerPaint.getTextBounds (s0, 0, s0.length (), bounds0);
      int margin = bounds0.right - bounds0.left;

      final int labelInterval = Settings.getAngleLabelInterval ();
      for (int degrees=0; degrees<360; degrees+=labelInterval)
      {
         canvas.save ();
         Rect bounds = new Rect ();
         @SuppressLint("DefaultLocale")
         String s = String.format ("%3d", degrees);
         outerPaint.getTextBounds (s, 0, s.length (), bounds);
         int width = bounds.right - bounds.left;
         //int height = bounds.top - bounds.centerY ();

         if (degrees < 180)
         {
            canvas.rotate (-90f, center, center);
            canvas.rotate (degrees, center, center);
            canvas.drawText (s, center + radius - width - margin, center - bounds.centerY (), outerPaint); // was -5
         }
         else
         {
            canvas.rotate (90f, center, center);
            canvas.rotate (degrees, center, center);
            canvas.drawText (s, center - (radius - margin), center - bounds.centerY (), outerPaint);

                  //- bounds.bottom - 5, outerPaint);
         }
         canvas.restore ();
      }

      if (Settings.showCrosshair ())
      {
         final Paint crosshairPaint = new Paint ();
         crosshairPaint.setColor (Color.BLACK);
         crosshairPaint.setAntiAlias (true);
         crosshairPaint.setStyle (Style.STROKE);
         crosshairPaint.setStrokeWidth (Settings.getCrosshairThickness ());
         crosshairPaint.setAlpha (200);
         crosshairPaint.setTextSize (Settings.getAngleLabelFontSize ());

         canvas.drawLine (center-Settings.getCrosshairLength (), center, center+Settings.getCrosshairLength () , center, crosshairPaint);
         canvas.drawLine (center, center-Settings.getCrosshairLength (), center, center+Settings.getCrosshairLength (), crosshairPaint);


      }
   }
}
