package ir.binarybeast.snacky.utils;

import android.annotation.SuppressLint;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import org.w3c.dom.Text;

import java.util.concurrent.TransferQueue;

@SuppressWarnings({"WeakerAccess"})
public class LayoutHelper {
    private static boolean isRTL = true;
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;
    public static final int SIDE_MARGIN = 20;
    public static final int TOP_MARGIN = 10;
    public static final int BOTTOM_MARGIN = 10;
    private static final int DEFAULT_PARENT_HEIGHT = DimensionUtils.dpToPx(56);

    public static int getParentDefaultHeight() {
        return DEFAULT_PARENT_HEIGHT;
    }

    public static int getSideMargin() {
        return SIDE_MARGIN;
    }

    private static int getSize(float size) {
        return (int) (size < 0 ? size : DimensionUtils.dpToPx(size));
    }

    private static int getAbsoluteGravity(int gravity) {
        return Gravity.getAbsoluteGravity(gravity, isRTL ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR);
    }

    @SuppressLint("RtlHardcoded")
    public static int getAbsoluteMarginStart(boolean rtl) {
        return rtl ? SIDE_MARGIN : 0;
    }

    @SuppressLint("RtlHardcoded")
    public static int getAbsoluteMarginEnd(boolean rtl) {
        return rtl ? 0 : SIDE_MARGIN;
    }

    @SuppressLint("RtlHardcoded")
    public static int getAbsoluteGravityStart(boolean rtl) {
        return rtl ? Gravity.RIGHT : Gravity.LEFT;
    }

    @SuppressLint("RtlHardcoded")
    public static int getAbsoluteGravityEnd(boolean rtl) {
        return rtl ? Gravity.LEFT : Gravity.RIGHT;
    }

    public static boolean isMultiLineTextView(TextView textView) {
        Layout textViewLayout = textView.getLayout();
        if (textViewLayout != null) {
            int lines = textViewLayout.getLineCount();
            if (lines > 0)
                return textViewLayout.getEllipsisCount(lines - 1) > 0;
        }
        return false;
    }

    public static void applySingleLineConfiguration(TextView textView) {
        ViewTreeObserver titleVto = textView.getViewTreeObserver();
        titleVto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (LayoutHelper.isMultiLineTextView(textView))
                    textView.setSingleLine(false);

            }
        });
    }


    //region FrameLayout

    public static FrameLayout.LayoutParams createFrame(int width, float height, int gravity, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
        layoutParams.setMargins(
                DimensionUtils.dpToPx(leftMargin),
                DimensionUtils.dpToPx(topMargin),
                DimensionUtils.dpToPx(rightMargin),
                DimensionUtils.dpToPx(bottomMargin)
        );
        return layoutParams;
    }

    public static FrameLayout.LayoutParams createFrame(int width, int height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
    }

    public static FrameLayout.LayoutParams createFrame(int width, float height) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height));
    }

    public static FrameLayout.LayoutParams createFrame(float width, float height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
    }

    public static FrameLayout.LayoutParams createFrameRelatively(float width, float height, int gravity, float startMargin, float topMargin, float endMargin, float bottomMargin) {
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
        layoutParams.leftMargin = DimensionUtils.dpToPx(isRTL ? endMargin : startMargin);
        layoutParams.topMargin = DimensionUtils.dpToPx(topMargin);
        layoutParams.rightMargin = DimensionUtils.dpToPx(isRTL ? startMargin : endMargin);
        layoutParams.bottomMargin = DimensionUtils.dpToPx(bottomMargin);
        return layoutParams;
    }

    public static FrameLayout.LayoutParams createFrameRelatively(float width, float height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
    }

    //endregion

    //region RelativeLayout

    public static RelativeLayout.LayoutParams createRelative(float width, float height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignParent, int alignRelative, int anchorRelative) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getSize(width), getSize(height));
        if (alignParent >= 0) {
            layoutParams.addRule(alignParent);
        }
        if (alignRelative >= 0 && anchorRelative >= 0) {
            layoutParams.addRule(alignRelative, anchorRelative);
        }
        layoutParams.leftMargin = DimensionUtils.dpToPx(leftMargin);
        layoutParams.topMargin = DimensionUtils.dpToPx(topMargin);
        layoutParams.rightMargin = DimensionUtils.dpToPx(rightMargin);
        layoutParams.bottomMargin = DimensionUtils.dpToPx(bottomMargin);
        return layoutParams;
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        return createRelative(width, height, leftMargin, topMargin, rightMargin, bottomMargin, -1, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignParent) {
        return createRelative(width, height, leftMargin, topMargin, rightMargin, bottomMargin, alignParent, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(float width, float height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignRelative, int anchorRelative) {
        return createRelative(width, height, leftMargin, topMargin, rightMargin, bottomMargin, -1, alignRelative, anchorRelative);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignParent, int alignRelative, int anchorRelative) {
        return createRelative(width, height, 0, 0, 0, 0, alignParent, alignRelative, anchorRelative);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height) {
        return createRelative(width, height, 0, 0, 0, 0, -1, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignParent) {
        return createRelative(width, height, 0, 0, 0, 0, alignParent, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignRelative, int anchorRelative) {
        return createRelative(width, height, 0, 0, 0, 0, -1, alignRelative, anchorRelative);
    }

    //endregion

    //region LinearLayout

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
        layoutParams.setMargins(
                DimensionUtils.dpToPx(leftMargin),
                DimensionUtils.dpToPx(topMargin),
                DimensionUtils.dpToPx(rightMargin),
                DimensionUtils.dpToPx(bottomMargin)
        );
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
        layoutParams.setMargins(
                DimensionUtils.dpToPx(leftMargin),
                DimensionUtils.dpToPx(topMargin),
                DimensionUtils.dpToPx(rightMargin),
                DimensionUtils.dpToPx(bottomMargin)
        );
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height));
        layoutParams.setMargins(
                DimensionUtils.dpToPx(leftMargin),
                DimensionUtils.dpToPx(topMargin),
                DimensionUtils.dpToPx(rightMargin),
                DimensionUtils.dpToPx(bottomMargin)
        );
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height));
        layoutParams.setMargins(
                DimensionUtils.dpToPx(leftMargin),
                DimensionUtils.dpToPx(topMargin),
                DimensionUtils.dpToPx(rightMargin),
                DimensionUtils.dpToPx(bottomMargin)
        );
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height));
    }

    public static LinearLayout.LayoutParams createLinearRelatively(float width, float height, int gravity, float startMargin, float topMargin, float endMargin, float bottomMargin) {
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
        layoutParams.leftMargin = DimensionUtils.dpToPx(isRTL ? endMargin : startMargin);
        layoutParams.topMargin = DimensionUtils.dpToPx(topMargin);
        layoutParams.rightMargin = DimensionUtils.dpToPx(isRTL ? startMargin : endMargin);
        layoutParams.bottomMargin = DimensionUtils.dpToPx(bottomMargin);
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinearRelatively(float width, float height, int gravity) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
    }

    //endregion
}
