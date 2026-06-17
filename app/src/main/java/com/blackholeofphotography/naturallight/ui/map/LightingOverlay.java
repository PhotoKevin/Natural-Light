package com.blackholeofphotography.naturallight.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;

import com.blackholeofphotography.naturallight.DisplayStatus;
import com.blackholeofphotography.naturallight.Settings;


public class LightingOverlay
{
   public static Bitmap getmSunMoonBitmap (Context context)
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

      Bitmap mSunMoonBitmap = Bitmap.createBitmap (picBorderWidthAndHeight, picBorderWidthAndHeight,
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

      return mSunMoonBitmap;
   }


   private static Point calculatePointOnCircle (final float centerX, final float centerY,
                                         final float radius, final float degrees)
   {
      // for trigonometry, 0 is pointing east, so subtract 90
      // Also, compass degrees are the wrong way round
      final double dblRadians = Math.toRadians (-degrees + 90);

      final int intX = (int) (radius * Math.cos (dblRadians));
      final int intY = (int) (radius * Math.sin (dblRadians));

      return new Point ((int) centerX + intX, (int) centerY - intY);
   }

}
