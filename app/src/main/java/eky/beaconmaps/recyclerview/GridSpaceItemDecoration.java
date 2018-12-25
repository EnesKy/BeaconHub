package eky.beaconmaps.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private static final int DEFAULT_SIZE_IN_DP = 8;

    private final boolean includeEdge;
    private final int spanCount;
    private int spaceSizeInPx;

    public GridSpaceItemDecoration(int spanCount, int spaceSizeInPx, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spaceSizeInPx = spaceSizeInPx;
        this.includeEdge = includeEdge;
    }

    public GridSpaceItemDecoration(Context context, int spanCount, boolean includeEdge) {
        this(spanCount, 0, includeEdge);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        spaceSizeInPx = (int) (DEFAULT_SIZE_IN_DP * metrics.density);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        if (includeEdge) {
            outRect.left = spaceSizeInPx - column * spaceSizeInPx / spanCount;
            outRect.right = (column + 1) * spaceSizeInPx / spanCount;

            if (position < spanCount)
                outRect.top = spaceSizeInPx;
            outRect.bottom = spaceSizeInPx;
        } else {
            outRect.left = column * spaceSizeInPx / spanCount;
            outRect.right = spaceSizeInPx - (column + 1) * spaceSizeInPx / spanCount;

            if (position >= spanCount)
                outRect.top = spaceSizeInPx;
        }
    }
}
