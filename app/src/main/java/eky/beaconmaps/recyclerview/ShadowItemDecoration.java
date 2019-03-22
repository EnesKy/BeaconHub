package eky.beaconmaps.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by KGTSDEMIR on 18.05.2017.
 */

public class ShadowItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    private static final int SHADOW_COLOR = 654773320;
    private static final int SHADOW_SIZE_IN_DP = 4;
    private static final int SHADOW_MARGIN_IN_DP = 16;

    private int shadowSize;
    private int shadowMargin;
    private int shadowInset;
    private final Rect bounds = new Rect();
    private Drawable headerShadowDrawable;
    private Drawable footerShadowDrawable;
    private int orientation;

    public ShadowItemDecoration(Context context, int orientation) {
        setOrientation(orientation);
        initializeDimensions(context);
        initializeDrawables();
    }

    public ShadowItemDecoration(Context context, int orientation, Drawable headerShadowDrawable, Drawable footerShadowDrawable) {
        setOrientation(orientation);
        initializeDimensions(context);

        this.headerShadowDrawable = headerShadowDrawable;
        this.footerShadowDrawable = footerShadowDrawable;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null)
            return;

        drawDecoration(c, parent);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int viewPosition = parent.getChildAdapterPosition(view);
        int lastItemPosition = parent.getAdapter().getItemCount() - 1;

        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        if (viewPosition == 0)
            if (orientation == VERTICAL)
                top = shadowInset;
            else
                left = shadowInset;

        if (viewPosition == lastItemPosition)
            if (orientation == VERTICAL)
                bottom = shadowInset;
            else
                right = shadowInset;

        outRect.set(left, top, right, bottom);
    }

    private Drawable buildShadowDrawable(GradientDrawable.Orientation orientation) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setOrientation(orientation);
        gradientDrawable.setColors(new int[]{SHADOW_COLOR, Color.TRANSPARENT});
        return gradientDrawable;
    }

    private void initializeDimensions(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float density = metrics.density;
        shadowSize = SHADOW_SIZE_IN_DP * (int) density;
        shadowMargin = SHADOW_MARGIN_IN_DP * (int) density;
        shadowInset = shadowSize + shadowMargin;
    }

    private void initializeDrawables() {
        if (this.orientation == VERTICAL) {
            headerShadowDrawable = buildShadowDrawable(GradientDrawable.Orientation.BOTTOM_TOP);
            footerShadowDrawable = buildShadowDrawable(GradientDrawable.Orientation.TOP_BOTTOM);
        } else {
            headerShadowDrawable = buildShadowDrawable(GradientDrawable.Orientation.LEFT_RIGHT);
            footerShadowDrawable = buildShadowDrawable(GradientDrawable.Orientation.RIGHT_LEFT);
        }
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        this.orientation = orientation;
    }

    private void drawDecoration(Canvas canvas, RecyclerView parent) {
        if (parent.getLayoutManager() == null)
            return;

        canvas.save();

        if (orientation == VERTICAL)
            drawVertical(canvas, parent);
        else
            drawHorizontal(canvas, parent);

        canvas.restore();
    }

    @SuppressLint("NewApi")
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int left;
        int right;
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        final int lastItemPosition = parent.getAdapter().getItemCount() - 1;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int viewPosition = parent.getChildAdapterPosition(child);
            parent.getDecoratedBoundsWithMargins(child, bounds);

            if (viewPosition == 0) {
                top = bounds.top + shadowMargin + Math.round(child.getTranslationY());
                bottom = top + shadowSize;
                headerShadowDrawable.setBounds(left, top, right, bottom);
                headerShadowDrawable.draw(canvas);
            }

            if (viewPosition == lastItemPosition) {
                top = bounds.bottom - shadowInset + Math.round(child.getTranslationY());
                bottom = top + shadowSize;
                footerShadowDrawable.setBounds(left, top, right, bottom);
                footerShadowDrawable.draw(canvas);
            }
        }
    }

    @SuppressLint("NewApi")
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int left;
        int right;
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        final int childCount = parent.getChildCount();
        final int lastItemPosition = parent.getAdapter().getItemCount() - 1;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int viewPosition = parent.getChildAdapterPosition(child);
            parent.getDecoratedBoundsWithMargins(child, bounds);

            if (viewPosition == 0) {
                left = bounds.left + shadowMargin + Math.round(child.getTranslationX());
                right = left + shadowSize;
                headerShadowDrawable.setBounds(left, top, right, bottom);
                headerShadowDrawable.draw(canvas);

            } else if (viewPosition == lastItemPosition) {
                left = bounds.right - shadowInset + Math.round(child.getTranslationX());
                right = left + shadowSize;
                footerShadowDrawable.setBounds(left, top, right, bottom);
                footerShadowDrawable.draw(canvas);
            }
        }
    }
}