package com.blackholeofphotography.naturallight.ui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;

import com.blackholeofphotography.naturallight.Settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CompassOverlay
{
   private static final Logger logger = LoggerFactory.getLogger (CompassOverlay.class);

   public CompassOverlay (Context context)
   {
      createCompassFramePicture (context);
   }

   public static Bitmap getCompassFrameBitmap (Context context)
   {
      return createCompassFramePicture (context);
   }


   private static Point calculatePointOnCircle (final float centerX, final float centerY,
                                         final float radius, final float degrees)
   {
      // for trigonometry, 0 is pointing east, so subtract 90
      // compass degrees are the wrong way round
      final double dblRadians = Math.toRadians (-degrees + 90);

      final int intX = (int) (radius * Math.cos (dblRadians));
      final int intY = (int) (radius * Math.sin (dblRadians));

      return new Point ((int) centerX + intX, (int) centerY - intY);
   }

   // Called from constructor
   private static Bitmap createCompassFramePicture (Context context)
   {
      Bitmap mCompassFrameBitmap;

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

      final int picBorderWidthAndHeight = Math.min (widthPixels, heightPixels);
      final int center = picBorderWidthAndHeight / 2;
      mCompassFrameBitmap = Bitmap.createBitmap (picBorderWidthAndHeight, picBorderWidthAndHeight,
            Config.ARGB_8888);
      final Canvas canvas = new Canvas (mCompassFrameBitmap);

      // draw compass inner circle and border
      logger.debug ("drawCircle {}, {}", center, radius);
      canvas.drawCircle (center, center, (float) radius, innerPaint);
      canvas.drawCircle (center, center, (float) radius, outerPaint);

      final int tickInterval = Settings.getTickMarkInterval ();
      for (int degrees=0; degrees<360; degrees+=tickInterval)
      {
         final Point point1 = calculatePointOnCircle (center, center, radius, degrees);
         final Point point2 = calculatePointOnCircle (center, center, radius-20, degrees);

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

      return mCompassFrameBitmap;
   }
}
