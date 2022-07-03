package ir.binarybeast.snacky.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;


public class DimensionUtils {
    public static float density = 1;

    public static int getDisplayHeightInPixel() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidthInPixel() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }


    public static int dpToPx(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static float pxToDp(Context context ,int px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, metrics);
    }


    public static Point measureViewSize(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return new Point(view.getMeasuredWidth(),view.getMeasuredHeight());
    }

    public static int  getVisibleElements(ViewGroup view) {
        int childCount = view.getChildCount();
        int count = 0;
        for (int i = 0; i < childCount; i++) {
            if (view.getChildAt(i).getVisibility() == View.VISIBLE) {
                count++;
            }
        }
        return count;
    }

}
