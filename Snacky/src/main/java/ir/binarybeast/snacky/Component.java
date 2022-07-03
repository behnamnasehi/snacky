package ir.binarybeast.snacky;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import ir.binarybeast.snacky.utils.AndroidUtilities;
import ir.binarybeast.snacky.utils.DimensionUtils;
import ir.binarybeast.snacky.utils.Theme;

public class Component {


    public static TextView createTitleTextView(Context context , int textSize , int textColor) {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        textView.setTextColor(ContextCompat.getColor(context, textColor));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setLinkTextColor(ContextCompat.getColor(context,textColor));
        return textView;
    }

    public static TextView createSubtitleTextView(Context context , int textSize , int textColor) {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        textView.setTextColor(ContextCompat.getColor(context, textColor));
        textView.setLinkTextColor(ContextCompat.getColor(context, textColor));
        textView.setHighlightColor(0);
        textView.setSingleLine(false);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        return textView;
    }

    public static TextView createUndoTextView(Context context , int textSize , int textColor) {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        textView.setTextColor(ContextCompat.getColor(context, textColor));
        return textView;
    }

    public static ImageView createLeftImageView(Context context) {
        ImageView imageView = new ImageView(context);
//        imageView.setImageResource(R.drawable.ic_undo);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        return imageView;
    }

    public static ImageView createUndoImageView(Context context) {
        ImageView imageView = new ImageView(context);
//        imageView.setImageResource(R.drawable.ic_undo);
        imageView.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, Theme.getDefaultTextColor()), PorterDuff.Mode.MULTIPLY));
        return imageView;
    }

    public static LinearLayout createUndoButton(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackground(
                Theme.createRadSelectorDrawable(
                        ContextCompat.getColor(
                                context,
                                Theme.getDefaultTextColor()) & 0x22ffffff,
                        DimensionUtils.dpToPx(2),
                        DimensionUtils.dpToPx(2)
                )
        );
        return linearLayout;
    }

    public static LinearLayout createTextContainerLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackground(
                Theme.createRadSelectorDrawable(
                        ContextCompat.getColor(
                                context,
                                Theme.getDefaultTextColor()) & 0x22ffffff,
                        DimensionUtils.dpToPx(2),
                        DimensionUtils.dpToPx(2)
                )
        );
        return linearLayout;
    }

    public static Paint createProgressPaint(Context context) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DimensionUtils.dpToPx(2));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(ContextCompat.getColor(context, Theme.getDefaultTextColor()));
        return paint;
    }

    public static TextPaint createTextPaint(Context context) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(DimensionUtils.dpToPx(10));
        textPaint.setTypeface(AndroidUtilities.getTypeface(context, "fonts/rmedium.ttf"));
        textPaint.setColor(ContextCompat.getColor(context, Theme.getDefaultTextColor()));
        return textPaint;
    }
}
