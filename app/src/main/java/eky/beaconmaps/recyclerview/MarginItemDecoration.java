package eky.beaconmaps.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarginItemDecoration extends RecyclerView.ItemDecoration {
    private static final int DEFAULT_SIZE_IN_DP = 8;

    private final Rect marginBounds;
    private final int orientation;

    public MarginItemDecoration(Context context, @RecyclerView.Orientation int orientation) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int marginSizeInPx = (int) (DEFAULT_SIZE_IN_DP * metrics.density);

        this.marginBounds = new Rect(marginSizeInPx, marginSizeInPx, marginSizeInPx, marginSizeInPx);
        this.orientation = orientation;
    }

    public MarginItemDecoration(@RecyclerView.Orientation int orientation, int marginSizeInPx) {
        this.marginBounds = new Rect(marginSizeInPx, marginSizeInPx, marginSizeInPx, marginSizeInPx);
        this.orientation = orientation;
    }

    public MarginItemDecoration(@RecyclerView.Orientation int orientation, Rect marginBounds) {
        this.marginBounds = marginBounds;
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int viewPosition = parent.getChildAdapterPosition(view);
        if (parent.getAdapter() == null)
            return;

        int lastItemPosition = parent.getAdapter().getItemCount() - 1;

        int left = 0, right = 0, top = 0, bottom = 0;
        if (orientation == RecyclerView.HORIZONTAL) {
            if (viewPosition == 0)
                left = marginBounds.left;
            else if (viewPosition == lastItemPosition)
                right = marginBounds.right;

            top = marginBounds.top;
            bottom = marginBounds.bottom;
        }
        if (orientation == RecyclerView.VERTICAL) {
            if (viewPosition == 0)
                top = marginBounds.top;
            else if (viewPosition == lastItemPosition)
                bottom = marginBounds.bottom;

            left = marginBounds.left;
            right = marginBounds.right;
        }
        outRect.set(left, top, right, bottom);
    }
}