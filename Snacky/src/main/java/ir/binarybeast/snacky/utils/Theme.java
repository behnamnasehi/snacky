package ir.binarybeast.snacky.utils;


import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.StateSet;


import ir.binarybeast.snacky.R;

public class Theme {
    private final static Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final static int DEFAULT_BG_COLOR = R.color.colorBlackEbonyClay;
    private final static int DEFAULT_TEXT_COLOR = R.color.colorWhite;
    private final static int DEFAULT_BACKGROUND_RADIUS = 6;
    private final static int DEFAULT_TEXT_SIZE = 15;
    private final static boolean DEFAULT_IS_RTL = false;

    public static int getDefaultTextColor() {
        return DEFAULT_TEXT_COLOR;
    }

    public static int getDefaultBackgroundColor() {
        return DEFAULT_BG_COLOR;
    }

    public static int getDefaultTextSize() {
        return DEFAULT_TEXT_SIZE;
    }

    public static boolean getDefaultDirection() {
        return DEFAULT_IS_RTL;
    }

    public static int getDefaultBackgroundRadius() {
        return DEFAULT_BACKGROUND_RADIUS;
    }

    public static Drawable createRadSelectorDrawable(int color, int topRad, int bottomRad) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(0xffffffff);
            Drawable maskDrawable = new RippleRadMaskDrawable(topRad, bottomRad);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{color}
            );
            return new RippleDrawable(colorStateList, null, maskDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(color));
            stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0x00000000));
            return stateListDrawable;
        }
    }

    public static Drawable createRoundRectDrawable(int rad, int defaultColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        return defaultDrawable;
    }

    public static class RippleRadMaskDrawable extends Drawable {
        private Path path = new Path();
        private RectF rect = new RectF();
        private float[] radii = new float[8];

        public RippleRadMaskDrawable(float top, float bottom) {
            radii[0] = radii[1] = radii[2] = radii[3] = DimensionUtils.dpToPx(top);
            radii[4] = radii[5] = radii[6] = radii[7] = DimensionUtils.dpToPx(bottom);
        }
        public RippleRadMaskDrawable(float topLeft, float topRight, float bottomRight, float bottomLeft) {
            radii[0] = radii[1] = DimensionUtils.dpToPx(topLeft);
            radii[2] = radii[3] = DimensionUtils.dpToPx(topRight);
            radii[4] = radii[5] = DimensionUtils.dpToPx(bottomRight);
            radii[6] = radii[7] = DimensionUtils.dpToPx(bottomLeft);
        }

        public void setRadius(float top, float bottom) {
            radii[0] = radii[1] = radii[2] = radii[3] = DimensionUtils.dpToPx(top);
            radii[4] = radii[5] = radii[6] = radii[7] = DimensionUtils.dpToPx(bottom);
            invalidateSelf();
        }
        public void setRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
            radii[0] = radii[1] = DimensionUtils.dpToPx(topLeft);
            radii[2] = radii[3] = DimensionUtils.dpToPx(topRight);
            radii[4] = radii[5] = DimensionUtils.dpToPx(bottomRight);
            radii[6] = radii[7] = DimensionUtils.dpToPx(bottomLeft);
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            rect.set(getBounds());
            path.addRoundRect(rect, radii, Path.Direction.CW);
            canvas.drawPath(path, maskPaint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }
}
