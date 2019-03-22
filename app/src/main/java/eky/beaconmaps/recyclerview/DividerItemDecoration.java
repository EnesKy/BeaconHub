package eky.beaconmaps.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable dividerDrawable;
    private int mOrientation;
    private int dividerStartOffset = 0;
    private int dividerEndOffset = 1;
    private final Rect mBounds = new Rect();
    private final Rect offsetBounds;

    public DividerItemDecoration(Context context, int orientation) {
        this(context, orientation, null);
    }

    public DividerItemDecoration(Context context, int orientation, Rect offsetBounds) {
        final TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
        final Drawable dividerDrawable = typedArray.getDrawable(0);
        typedArray.recycle();

        this.offsetBounds = offsetBounds;
        this.dividerDrawable = applyOffsetToDrawable(dividerDrawable);

        setOrientation(orientation);
    }

    //region FLOW METHODS
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null)
            return;

        if (mOrientation == VERTICAL)
            drawVertical(c, parent);
        else
            drawHorizontal(c, parent);
    }

    @SuppressLint("NewApi")
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();

        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = dividerStartOffset; i < childCount - dividerEndOffset; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            int top = bottom - dividerDrawable.getIntrinsicHeight();

            dividerDrawable.setBounds(left, top, right, bottom);
            dividerDrawable.draw(canvas);
        }
        canvas.restore();
    }

    @SuppressLint("NewApi")
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();

        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        final int childCount = parent.getChildCount();
        for (int i = dividerStartOffset; i < childCount - dividerEndOffset; i++) {
            final View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
            int right = mBounds.right + Math.round(child.getTranslationX());
            int left = right - dividerDrawable.getIntrinsicWidth();

            dividerDrawable.setBounds(left, top, right, bottom);
            dividerDrawable.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOrientation == VERTICAL)
            outRect.set(0, 0, 0, dividerDrawable.getIntrinsicHeight());
        else
            outRect.set(0, 0, dividerDrawable.getIntrinsicWidth(), 0);
    }
    //endregion

    //region STORY METHODS
    private Drawable applyOffsetToDrawable(Drawable drawable) {
        if (offsetBounds != null) {
            Drawable colorDrawable = new ColorDrawable(Color.WHITE);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{colorDrawable, drawable});
            layerDrawable.setLayerInset(1, offsetBounds.left, offsetBounds.top, offsetBounds.right, offsetBounds.bottom);
            return layerDrawable;
        } else {
            return drawable;
        }
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    public void setDrawable(@NonNull Drawable drawable) {
        dividerDrawable = applyOffsetToDrawable(drawable);
    }

    /**
     * Sets the exceptions to draw divider.
     */
    public void setDividerDrawOffsets(int dividerStartOffset, int dividerEndOffset) {
        this.dividerStartOffset = dividerStartOffset;
        this.dividerEndOffset = dividerEndOffset;
    }
    //endregion
}