package com.blackholeofphotography.naturallight;

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
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;


public class LightingOverlay extends Overlay implements DisplayStatusListener
{
   private static final String LOG_TAG = "LightingOverlay";
   private Paint sSmoothPaint = new Paint (Paint.FILTER_BITMAP_FLAG);
   protected MapView mMapView;
   private final Display mDisplay;
   protected Bitmap mSunMoonBitmap;
   protected long mLastRender = 0;
   private int mLastRenderLag = 200;

   private final Matrix mSunMoonMatrix = new Matrix ();


   protected final float mScale;



   // ===========================================================
   // Constructors
   // ===========================================================

   @SuppressWarnings({"unchecked", "deprecation", "RedundantSuppression"})
   public LightingOverlay (Context context,
                           MapView mapView)
   {
      super ();
      mScale = context.getResources ().getDisplayMetrics ().density * 12;

      mMapView = mapView;
      final WindowManager windowManager = (WindowManager) context
              .getSystemService (Context.WINDOW_SERVICE);
      mDisplay = windowManager.getDefaultDisplay ();
      // Requires a higher API 34 than I want
      //mDisplay = context.getDisplay ();
   }

   @Override
   public void onDetach (MapView mapView)
   {
      this.mMapView = null;
      sSmoothPaint = null;

      if (mSunMoonBitmap != null)
         mSunMoonBitmap.recycle ();
      super.onDetach (mapView);
   }

   protected void createBitmap (Context context)
   {
      DisplayStatus.lighting_bitmap_created +=1;
      final int widthPixels = context.getResources ().getDisplayMetrics ().widthPixels;
      final int heightPixels = context.getResources ().getDisplayMetrics ().heightPixels;

      final float compassPercentage = Settings.getCompassPercentage () / 100.0F;
      final int radius = (int) (compassPercentage *  Math.min (widthPixels, heightPixels) / 2);

      // Draw the current sun position
      final Paint sunPositionPaint = new Paint ();
      sunPositionPaint.setColor (Color.rgb (246,190,0));

      sunPositionPaint.setAntiAlias (true);
      sunPositionPaint.setStyle (Style.FILL);
      sunPositionPaint.setAlpha (255);
      sunPositionPaint.setStrokeWidth (3);

      // Draw the sun rise/set lines
      final Paint riseSetPaint = new Paint ();
      riseSetPaint.setColor (Color.BLUE);
      riseSetPaint.setAntiAlias (true);
      riseSetPaint.setStyle (Style.STROKE);
      riseSetPaint.setStrokeWidth (Settings.getSunRiseSetNeedleThickness ());
      riseSetPaint.setAlpha (200);
      riseSetPaint.setTextSize (Settings.getAngleLabelFontSize ());

      // Draw the current moon position
      final Paint moonPositionPaint = new Paint ();
      moonPositionPaint.setColor (Color.rgb (190,190,190));

      moonPositionPaint.setAntiAlias (true);
      moonPositionPaint.setStyle (Style.FILL);
      moonPositionPaint.setAlpha (255);
      moonPositionPaint.setStrokeWidth (3);


      final int picBorderWidthAndHeight = Math.min (widthPixels, heightPixels);
      final int center = picBorderWidthAndHeight / 2;
      if (mSunMoonBitmap != null)
         mSunMoonBitmap.recycle ();
      mSunMoonBitmap = Bitmap.createBitmap (picBorderWidthAndHeight, picBorderWidthAndHeight,
            Config.ARGB_8888);
      final Canvas canvas = new Canvas (mSunMoonBitmap);

      if (DisplayStatus.getSunPosition ().getAzimuth () > DisplayStatus.getSunRisePosition ().getAzimuth ()
            && DisplayStatus.getSunPosition ().getAzimuth () < DisplayStatus.getSunSetPosition ().getAzimuth ()
            && DisplayStatus.getSunPosition ().getElevation () > 0)
      {
         final Point point = calculatePointOnCircle (center, center, radius * (1 - DisplayStatus.getSunPosition ().getElevation () / 90f), DisplayStatus.getSunPosition ().getAzimuth ());
         canvas.drawCircle (point.x, point.y, (float) 9, sunPositionPaint);
         canvas.drawLine (point.x, point.y, center, center, sunPositionPaint);
      }

      // if not new moon?
      if (DisplayStatus.getMoonPosition ().getElevation () > 0)
      {
         final Point point = calculatePointOnCircle ((float) center, (float) center, (float) (radius * (1 - DisplayStatus.getMoonPosition ().getElevation () / 90f)), (float) DisplayStatus.getMoonPosition ().getAzimuth ());
         canvas.drawCircle (point.x, point.y, (float) 9, moonPositionPaint);
         canvas.drawLine (point.x, point.y, center, center, moonPositionPaint);
      }

      if (Settings.showSunRiseSetNeedle ())
      {
         final Point sunrisePoint = calculatePointOnCircle (center, center, radius, DisplayStatus.getSunRisePosition ().getAzimuth ());
         canvas.drawLine (center, center, sunrisePoint.x, sunrisePoint.y, riseSetPaint);

         final Point sunsetPoint = calculatePointOnCircle (center, center, radius, DisplayStatus.getSunSetPosition ().getAzimuth ());
         canvas.drawLine (center, center, sunsetPoint.x, sunsetPoint.y, riseSetPaint);
      }
   }

   protected void drawOverlay (final Canvas canvas)
   {
      if (mSunMoonBitmap == null)
         Log.d (LOG_TAG, "mSunMoonBitmap is null");

      if (mLastRender + mLastRenderLag < System.currentTimeMillis ())
      {
         createBitmap (mMapView.getContext ());
         mLastRender = System.currentTimeMillis ();
      }
      else
         DisplayStatus.lighting_bitmap_skipped += 1;

      final Projection proj = mMapView.getProjection ();
      final Rect rect = proj.getScreenRect ();
      final float centerX = rect.exactCenterX ();
      final float centerY = rect.exactCenterY ();

      float mCompassFrameCenterX = mSunMoonBitmap.getWidth () / 2f - 0.5f;
      float mCompassFrameCenterY = mSunMoonBitmap.getHeight () / 2f - 0.5f;

      mSunMoonMatrix.setTranslate (-mCompassFrameCenterX, -mCompassFrameCenterY);
      mSunMoonMatrix.postTranslate (centerX, centerY);

      proj.save (canvas, false, true);
      canvas.concat (mSunMoonMatrix);
      canvas.drawBitmap (mSunMoonBitmap, 0, 0, sSmoothPaint);
      proj.restore (canvas, true);

      mSunMoonMatrix.postTranslate (centerX, centerY);

      proj.save (canvas, false, true);
      canvas.concat (mSunMoonMatrix);
      proj.restore (canvas, true);
   }

   // ===========================================================
   // Methods from SuperClass/Interfaces
   // ===========================================================

   @Override
   public void draw (Canvas c, Projection pProjection)
   {
      drawOverlay (c);
   }

   // ===========================================================
   // Inner and Anonymous Classes
   // ===========================================================

   private Point calculatePointOnCircle (final float centerX, final float centerY,
                                         final float radius, final float degrees)
   {
      // for trigonometry, 0 is pointing east, so subtract 90
      // Also, compass degrees are the wrong way round
      final double dblRadians = Math.toRadians (-degrees + 90);

      final int intX = (int) (radius * Math.cos (dblRadians));
      final int intY = (int) (radius * Math.sin (dblRadians));

      return new Point ((int) centerX + intX, (int) centerY - intY);
   }

   @Override
   public void onChange ()
   {

   }
}
