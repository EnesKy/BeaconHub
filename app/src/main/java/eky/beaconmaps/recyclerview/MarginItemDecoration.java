package eky.beaconmaps.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by kgtsdemir on 2.04.2018.
 */

public class MarginItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    private static final int DEFAULT_SIZE_IN_DP = 15;

    private final Rect marginBounds;
    private int orientation;

    public MarginItemDecoration(Context context, int orientation) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int marginSizeInPx = (int) (DEFAULT_SIZE_IN_DP * metrics.density);
        marginBounds = new Rect(marginSizeInPx, marginSizeInPx, marginSizeInPx, marginSizeInPx);
        setOrientation(orientation);
    }

    public MarginItemDecoration(int orientation, int marginSizeInPx) {
        marginBounds = new Rect(marginSizeInPx, marginSizeInPx, marginSizeInPx, marginSizeInPx);
        setOrientation(orientation);
    }

    public MarginItemDecoration(int orientation, Rect marginBounds) {
        this.marginBounds = marginBounds;
        setOrientation(orientation);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int viewPosition = parent.getChildAdapterPosition(view);
        int lastItemPosition = parent.getAdapter().getItemCount() - 1;

        int left = 0, right = 0, top = 0, bottom = 0;
        if (orientation == HORIZONTAL) {
            if (viewPosition == 0)
                left = marginBounds.left;
            else if (viewPosition == lastItemPosition)
                right = marginBounds.right;

            top = marginBounds.top;
            bottom = marginBounds.bottom;
        }
        if (orientation == VERTICAL) {
            if (viewPosition == 0)
                top = marginBounds.top;
            else if (viewPosition == lastItemPosition)
                bottom = marginBounds.bottom;

            left = marginBounds.left;
            right = marginBounds.right;
        }
        outRect.set(left, top, right, bottom);
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL)
            throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL, VERTICAL.");
        this.orientation = orientation;
    }
}