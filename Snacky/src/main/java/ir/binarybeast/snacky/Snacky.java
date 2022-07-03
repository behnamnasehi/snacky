package ir.binarybeast.snacky;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.GuardedBy;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ir.binarybeast.snacky.utils.AndroidUtilities;
import ir.binarybeast.snacky.utils.Checker;
import ir.binarybeast.snacky.utils.DimensionUtils;
import ir.binarybeast.snacky.utils.LayoutHelper;
import ir.binarybeast.snacky.utils.Theme;

public class Snacky extends FrameLayout {
    private static final String TAG = "Snack";
    private static final Object LOCK = new Object();
    public String identifier;
    private final Context applicationContext;
    @GuardedBy("LOCK")
    private static final Map<String, Snacky> INSTANCES = new ArrayMap<>();


    private TextView titleTextView;
    private TextView subinfoTextView;
    private TextView undoTextView;
    private ImageView undoImageView;
    private ImageView leftImageView;
    //    private BackupImageView avatarImageView;
    private LinearLayout undoButton;
    private LinearLayout textContainerLayout;
    private int parentViewHeight = 0;
    private Drawable backgroundDrawable;
    private int hideAnimationType = 1;

    private TextPaint textPaint;
    private Paint progressPaint;
    private RectF rect;

    private long duration;
    private int prevSeconds;
    private String timeLeftString;
    private int textWidth;

    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private long lastUpdateTime;

    private float additionalTranslationY;
    private boolean isShown;
    private float enterOffset;
    private StaticLayout timeLayout;
    private StaticLayout timeLayoutOut;
    private int textWidthOut;
    private float timeReplaceProgress = 1f;
    private boolean fromTop;
    public static final int LENGTH_INDEFINITE = -1;
    public static final int LENGTH_LONG = 4000;
    public static final int LENGTH_SHORT = 2000;
    public final static int ACTION_INFO = 82;
    private boolean hasTimer = true;
    private boolean isRTL = false;

    private DesignBuilder designBuilder;

    @NonNull
    public Context getApplicationContext() {
        return this.applicationContext;
    }

    @NonNull
    public static Snacky getInstance() {

        Snacky defaultInstance = INSTANCES.get(SharedConfig.DEFAULT_INSTANCE_NAME);
        if (defaultInstance == null) {
            throw new IllegalStateException(
                    "Default Snacky is not initialized in this "
                            + ". Make sure to call "
                            + "Snacky.initialize(Context) first.");
        }
        return defaultInstance;
    }

    @NonNull
    public static Snacky getInstance(@NonNull String name) {
        Snacky snack = INSTANCES.get(normalize(name));
        if (snack != null) {
            return snack;
        }
        List<String> availableAppNames = getAllInstanceNames();
        String availableAppNamesMessage;
        if (availableAppNames.isEmpty()) {
            availableAppNamesMessage = "";
        } else {
            availableAppNamesMessage =
                    "Available instance names: " + TextUtils.join(", ", availableAppNames);
        }
        String errorMessage =
                String.format(
                        "Instance name %s doesn't exist. %s", name, availableAppNamesMessage);
        throw new IllegalStateException(errorMessage);
    }

    public boolean isDefaultInstance() {
        return SharedConfig.DEFAULT_INSTANCE_NAME.equals(identifier);
    }

    public static Snacky createInstance(@NonNull Context context) {
        return createInstance(context, SharedConfig.DEFAULT_INSTANCE_NAME, false, null);
    }

    public static Snacky createInstance(@NonNull Context context, @NonNull String name, boolean fromTop, DesignBuilder designBuilder) {
        Context applicationContext;
        Snacky snacky;
        String normalizedName = normalize(name);
        if (context.getApplicationContext() == null) {
            applicationContext = context;
        } else {
            applicationContext = context.getApplicationContext();
        }
        Checker.checkState(!INSTANCES.containsKey(name),
                "Snacky name " + name + " already exists!");
        Checker.checkNotNull(applicationContext, "Snacky context cannot be null.");
        snacky = new Snacky(
                applicationContext,
                normalizedName,
                fromTop,
                designBuilder == null ? new DesignBuilder.Builder().build() : designBuilder
        );
        INSTANCES.put(name, snacky);
        return snacky;
    }

    private static String normalize(@NonNull String name) {
        return name.trim();
    }

    private static List<String> getAllInstanceNames() {
        List<String> allNames = new ArrayList<>();
        synchronized (LOCK) {
            for (Snacky app : INSTANCES.values()) {
                allNames.add(app.getIdentifier());
            }
        }
        Collections.sort(allNames);
        return allNames;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    protected Snacky(@NonNull Context context, @NonNull String name, boolean top, DesignBuilder designBuilder) {
        super(context);
        this.designBuilder = designBuilder == null ? new DesignBuilder.Builder().build() : designBuilder;
        this.applicationContext = Checker.checkNotNull(context, "Snacky Context Can not be null");
        this.identifier = Checker.checkNotEmpty(normalize(name), "Snack Name Can not be empty");
        this.isRTL = designBuilder.isRtl();
        AndroidUtilities.checkDisplaySize(context, null);
        fromTop = top;

        //region textContainerLayout
        textContainerLayout = Component.createTextContainerLayout(context);
        addView(textContainerLayout, LayoutHelper.createFrame(
                        LayoutHelper.WRAP_CONTENT,
                        LayoutHelper.WRAP_CONTENT,
                        Gravity.CENTER_VERTICAL | LayoutHelper.getAbsoluteGravityStart(isRTL),
                        0,
                        0,
                        0,
                        0
                )
        );
        //endregion

        //region titleTextView
        titleTextView = Component.createTitleTextView(
                context,
                designBuilder.getTitleTextSize(),
                designBuilder.getTitleTextColor()
        );
        titleTextView.setMovementMethod(new LinkMovementMethodMy());
        textContainerLayout.addView(titleTextView, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT,
                LayoutHelper.WRAP_CONTENT,
                Gravity.TOP | LayoutHelper.getAbsoluteGravityStart(isRTL),
                LayoutHelper.getAbsoluteMarginEnd(isRTL),
                0,
                LayoutHelper.getAbsoluteMarginStart(isRTL),
                0)
        );
        //endregion

        //region subinfoTextView
        subinfoTextView = Component.createSubtitleTextView(
                context,
                designBuilder.getSubtitleTextSize(),
                designBuilder.getSubtitleTextColor()
        );

        textContainerLayout.addView(subinfoTextView, LayoutHelper.createFrame(
                        LayoutHelper.WRAP_CONTENT,
                        LayoutHelper.WRAP_CONTENT,
                        Gravity.TOP | LayoutHelper.getAbsoluteGravityStart(isRTL),
                        LayoutHelper.getAbsoluteMarginEnd(isRTL),
                        0,
                        LayoutHelper.getAbsoluteMarginStart(isRTL),
                        0
                )
        );
        subinfoTextView.setVisibility(GONE);
        //endregion

        //region leftImageView
//        leftImageView = Component.createLeftImageView(context);
//        addView(leftImageView, LayoutHelper.createFrame(
//                54,
//                LayoutHelper.WRAP_CONTENT,
//                Gravity.CENTER_VERTICAL | LayoutHelper.getAbsoluteGravityStart(isRTL),
//                3,
//                0,
//                0,
//                0
//                )
//        );
//        leftImageView.setVisibility(GONE);
        //endregion

        //region undoButton
        undoButton = Component.createUndoButton(context);
        addView(undoButton, LayoutHelper.createFrame(
                        LayoutHelper.WRAP_CONTENT,
                        LayoutHelper.WRAP_CONTENT,
                        Gravity.CENTER_VERTICAL | LayoutHelper.getAbsoluteGravityEnd(isRTL),
                        0,
                        0,
                        11,
                        0
                )
        );

        undoTextView = Component.createUndoTextView(
                context,
                designBuilder.getUndoTextSize(),
                designBuilder.getUndoTextColor()
        );
        ;
        undoButton.addView(undoTextView, LayoutHelper.createLinear(
                        LayoutHelper.WRAP_CONTENT,
                        LayoutHelper.WRAP_CONTENT,
                        Gravity.CENTER_VERTICAL | LayoutHelper.getAbsoluteGravityStart(isRTL),
                        6,
                        4,
                        8,
                        4
                )
        );
        undoButton.setVisibility(VISIBLE);
        //endregion

        //region progressPaint
        rect = new RectF(
                DimensionUtils.dpToPx(15),
                DimensionUtils.dpToPx(15),
                DimensionUtils.dpToPx(15 + 18),
                DimensionUtils.dpToPx(15 + 18)
        );

        progressPaint = Component.createProgressPaint(context);

        textPaint = Component.createTextPaint(context);
        //endregion

        setWillNotDraw(false);
        backgroundDrawable = Theme.createRoundRectDrawable(
                designBuilder.getBackgroundRadius(),
                ContextCompat.getColor(
                        context,
                        designBuilder.getBackgroundColor()
                )
        );

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        setVisibility(INVISIBLE);
    }

    public Snacky make(@NonNull String title, String subtitle, int duration, String undoTitle) {
        if (duration == 0) {
            duration = LENGTH_LONG;
        }
        this.duration = duration;
        title = Checker.checkNotEmpty(title, "title cant be null or empty");
        if (currentActionRunnable != null) {
            currentActionRunnable.run();
        }
        isShown = true;
        currentActionRunnable = null;
        currentCancelRunnable = null;
        lastUpdateTime = SystemClock.elapsedRealtime();
//        titleTextView.setMovementMethod(null);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        titleTextView.setText(title);
        titleTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        titleTextView.setMinHeight(0);
        titleTextView.setSingleLine(true);

        LinearLayout.LayoutParams titleTextViewLayoutParams = (LinearLayout.LayoutParams) titleTextView.getLayoutParams();
        LinearLayout.LayoutParams subInfoTextViewLayoutParams = (LinearLayout.LayoutParams) subinfoTextView.getLayoutParams();

        titleTextViewLayoutParams.gravity = (isRTL ? Gravity.END : Gravity.START) | Gravity.CENTER_VERTICAL;
        subInfoTextViewLayoutParams.gravity = (isRTL ? Gravity.END : Gravity.START) | Gravity.CENTER_VERTICAL;

        titleTextViewLayoutParams.height = LayoutHelper.WRAP_CONTENT;
        titleTextViewLayoutParams.leftMargin = DimensionUtils.dpToPx(LayoutHelper.getSideMargin());
        titleTextViewLayoutParams.rightMargin = DimensionUtils.dpToPx(LayoutHelper.getSideMargin());
        titleTextViewLayoutParams.topMargin = DimensionUtils.dpToPx(LayoutHelper.TOP_MARGIN);
        titleTextViewLayoutParams.bottomMargin = DimensionUtils.dpToPx(LayoutHelper.BOTTOM_MARGIN);

        setOnClickListener(null);
        setOnTouchListener((view, motionEvent) -> true);

        if (!Checker.checkIsNullOrEmpty(subtitle)) {
            subInfoTextViewLayoutParams.bottomMargin = DimensionUtils.dpToPx(LayoutHelper.BOTTOM_MARGIN);
            if (isRTL) {
                subInfoTextViewLayoutParams.leftMargin = DimensionUtils.dpToPx(LayoutHelper.SIDE_MARGIN);
            } else {
                subInfoTextViewLayoutParams.rightMargin = DimensionUtils.dpToPx(LayoutHelper.SIDE_MARGIN);
            }
            titleTextViewLayoutParams.bottomMargin = DimensionUtils.dpToPx(0);
            subinfoTextView.setText(subtitle);
            subinfoTextView.setVisibility(VISIBLE);
            LayoutHelper.applySingleLineConfiguration(subinfoTextView);
        } else {
            subinfoTextView.setVisibility(GONE);
        }

        if (!Checker.checkIsNullOrEmpty(undoTitle)) {
            undoTextView.setText(undoTitle);
            Point undoButtonSize = DimensionUtils.measureViewSize(undoButton);
            LayoutParams undoButtonViewLayoutParams = (LayoutParams) undoButton.getLayoutParams();
            undoButtonViewLayoutParams.leftMargin = DimensionUtils.dpToPx(11);
            if (isRTL) {
                subInfoTextViewLayoutParams.leftMargin = undoButtonSize.x + undoButtonViewLayoutParams.leftMargin + undoButtonViewLayoutParams.rightMargin;
                titleTextViewLayoutParams.leftMargin = undoButtonSize.x + undoButtonViewLayoutParams.leftMargin + undoButtonViewLayoutParams.rightMargin;
            } else {
                subInfoTextViewLayoutParams.rightMargin = undoButtonSize.x + undoButtonViewLayoutParams.leftMargin + undoButtonViewLayoutParams.rightMargin;
                titleTextViewLayoutParams.rightMargin = undoButtonSize.x + undoButtonViewLayoutParams.leftMargin + undoButtonViewLayoutParams.rightMargin;
            }
            undoButton.setOnClickListener(v -> {
                hide(false, 1);
            });
        }

        if (hasTimer()) {
            subInfoTextViewLayoutParams.leftMargin = DimensionUtils.dpToPx(40);
            titleTextViewLayoutParams.leftMargin = DimensionUtils.dpToPx(40);
        }
        LayoutHelper.applySingleLineConfiguration(titleTextView);

        titleTextView.setDrawingCacheEnabled(true);

        ViewGroup parent = (ViewGroup) getParent();
        int width = parent.getMeasuredWidth();
        if (width == 0) {
            width = AndroidUtilities.displaySize.x;
        }
        width -= DimensionUtils.dpToPx(16);
        measureChildWithMargins(textContainerLayout, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), 0, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0);
        parentViewHeight = textContainerLayout.getMeasuredHeight();
        if (parentViewHeight == 0 || parentViewHeight < LayoutHelper.getParentDefaultHeight()) {
            parentViewHeight = LayoutHelper.getParentDefaultHeight();
        }
        return this;
    }

    public void start() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
            setEnterOffset((fromTop ? -1.0f : 1.0f) * (enterOffsetMargin + parentViewHeight));
            getStartAnimation().start();
        }
    }

    private AnimatorSet getStartAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "enterOffset", (fromTop ? -1.0f : 1.0f) * (enterOffsetMargin + parentViewHeight), (fromTop ? 1.0f : -1.0f)));
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(180);
        return animatorSet;
    }

    public Snacky setHasTimer(boolean hasTimer) {
        this.hasTimer = hasTimer;
        return this;
    }

    public boolean hasTimer() {
        return hasTimer;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(parentViewHeight, MeasureSpec.EXACTLY));
        backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void hide(boolean apply, int animated) {
        if (getVisibility() != VISIBLE || !isShown) {
            return;
        }
        isShown = false;
        if (currentActionRunnable != null) {
            if (apply) {
                currentActionRunnable.run();
            }
            currentActionRunnable = null;
        }
        if (currentCancelRunnable != null) {
            if (!apply) {
                currentCancelRunnable.run();
            }
            currentCancelRunnable = null;
        }
        if (animated != 0) {
            AnimatorSet animatorSet = new AnimatorSet();
            if (animated == 1) {
                animatorSet.playTogether(ObjectAnimator.ofFloat(this, "enterOffset", (fromTop ? -1.0f : 1.0f) * (enterOffsetMargin + parentViewHeight)));
                animatorSet.setDuration(250);
            } else {
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(this, View.SCALE_X, 0.8f),
                        ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.8f),
                        ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f));
                animatorSet.setDuration(180);
            }
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(INVISIBLE);
                    setScaleX(1.0f);
                    setScaleY(1.0f);
                    setAlpha(1.0f);
                }
            });
            animatorSet.start();
        } else {
            setEnterOffset((fromTop ? -1.0f : 1.0f) * (enterOffsetMargin + parentViewHeight));
            setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (additionalTranslationY != 0) {
            canvas.save();
//            float bottom = getMeasuredHeight() - enterOffset + enterOffsetMargin + DimensionUtils.dpToPx(1);
            float bottom = getMeasuredHeight()/2;
            Toast.makeText(applicationContext, ""+bottom, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onDraw: "+bottom );
            if (bottom > 0) {
                canvas.clipRect(0, 0, getMeasuredWidth(), bottom);
                super.dispatchDraw(canvas);
            }
            backgroundDrawable.draw(canvas);
            canvas.restore();
        } else {
            backgroundDrawable.draw(canvas);
        }

        if (hasTimer() && duration != -1) {
            int newSeconds = duration > 0 ? (int) Math.ceil(duration / 1000.0f) : 0;
            if (prevSeconds != newSeconds) {
                prevSeconds = newSeconds;
                timeLeftString = String.format("%d", Math.max(1, newSeconds));
                if (timeLayout != null) {
                    timeLayoutOut = timeLayout;
                    timeReplaceProgress = 0;
                    textWidthOut = textWidth;
                }
                textWidth = (int) Math.ceil(textPaint.measureText(timeLeftString));
                timeLayout = new StaticLayout(timeLeftString, textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }

            if (timeReplaceProgress < 1f) {
                timeReplaceProgress += 16f / 150f;
                if (timeReplaceProgress > 1f) {
                    timeReplaceProgress = 1f;
                } else {
                    invalidate();
                }
            }

            int alpha = textPaint.getAlpha();

            if (timeLayoutOut != null && timeReplaceProgress < 1f) {
                textPaint.setAlpha((int) (alpha * (1f - timeReplaceProgress)));
                canvas.save();
                canvas.translate(
                        rect.centerX() - textWidth / 2,
                        DimensionUtils.dpToPx(17.2f) + DimensionUtils.dpToPx(10) * timeReplaceProgress);
                timeLayoutOut.draw(canvas);
                textPaint.setAlpha(alpha);
                canvas.restore();
            }

            if (timeLayout != null) {
                if (timeReplaceProgress != 1f) {
                    textPaint.setAlpha((int) (alpha * timeReplaceProgress));
                }
                canvas.save();
                canvas.translate(rect.centerX() - textWidth / 2,
                        DimensionUtils.dpToPx(17.2f) - DimensionUtils.dpToPx(10) * (1f - timeReplaceProgress));
                timeLayout.draw(canvas);
                if (timeReplaceProgress != 1f) {
                    textPaint.setAlpha(alpha);
                }
                canvas.restore();
            }

//            canvas.drawText(timeLeftString, rect.centerX() - textWidth / 2, DimensionUtils.dpToPx(28.2f), textPaint);
//            canvas.drawText(timeLeftString, , textPaint);
            canvas.drawArc(rect, -90, -360 * (duration / 5000.0f), false, progressPaint);
        }

        long newTime = SystemClock.elapsedRealtime();
        long dt = newTime - lastUpdateTime;
        duration -= dt;
        lastUpdateTime = newTime;
        if (duration <= 0) {
            hide(true, hideAnimationType);
        }
        if (duration != -1) {
            invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (additionalTranslationY != 0) {
            canvas.save();

            float bottom = getMeasuredHeight() - enterOffset + DimensionUtils.dpToPx(9);
            if (bottom > 0) {
                canvas.clipRect(0, 0, getMeasuredWidth(), bottom);
                super.dispatchDraw(canvas);
            }
            canvas.restore();
        } else {
            super.dispatchDraw(canvas);
        }
    }

    private int enterOffsetMargin = DimensionUtils.dpToPx(8);

    public void setEnterOffsetMargin(int enterOffsetMargin) {
        this.enterOffsetMargin = enterOffsetMargin;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        titleTextView.invalidate();
//        leftImageView.invalidate();
    }

    public void setHideAnimationType(int hideAnimationType) {
        this.hideAnimationType = hideAnimationType;
    }

    public float getEnterOffset() {
        return enterOffset;
    }

    @Keep
    public void setEnterOffset(float enterOffset) {
        if (this.enterOffset != enterOffset) {
            this.enterOffset = enterOffset;
            updatePosition();
        }
    }

    private void updatePosition() {
        setTranslationY(enterOffset - enterOffsetMargin + DimensionUtils.dpToPx(8) - additionalTranslationY);
        invalidate();
    }

    @Override
    public Drawable getBackground() {
        return backgroundDrawable;
    }

    protected void onRemoveDialogAction(long currentDialogId, int action) {

    }

    public static class LinkMovementMethodMy extends LinkMovementMethod {
        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CharacterStyle[] links = buffer.getSpans(widget.getSelectionStart(), widget.getSelectionEnd(), CharacterStyle.class);
                    if (links == null || links.length == 0) {
                        return false;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CharacterStyle[] links = buffer.getSpans(widget.getSelectionStart(), widget.getSelectionEnd(), CharacterStyle.class);
                    if (links != null && links.length > 0) {
//                        didPressUrl(links[0]);
                    }
                    Selection.removeSelection(buffer);
                    result = true;
                } else {
                    result = super.onTouchEvent(widget, buffer, event);
                }
                return result;
            } catch (Exception e) {
                Log.e(TAG, "onTouchEvent: ", e);
            }
            return false;
        }
    }

}
