package eky.beaconmaps.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private static final float DEFAULT_SIZE_IN_DP = 8;

    private final int spaceSizeInPx;
    private final int orientation;

    public SpaceItemDecoration(Context context, @RecyclerView.Orientation int orientation) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        this.spaceSizeInPx = (int) (DEFAULT_SIZE_IN_DP * metrics.density);
        this.orientation = orientation;
    }

    public SpaceItemDecoration(@RecyclerView.Orientation int orientation, int spaceSizeInPx) {
        this.spaceSizeInPx = spaceSizeInPx;
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (orientation == RecyclerView.VERTICAL)
            outRect.set(0, 0, 0, spaceSizeInPx);
        else if (orientation == RecyclerView.HORIZONTAL)
            outRect.set(0, 0, spaceSizeInPx, 0);
    }
}