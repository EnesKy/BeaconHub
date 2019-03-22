package eky.beaconmaps.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by KGTSDEMIR on 25.07.2017.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    private static final float DEFAULT_SIZE_IN_DP = 15;

    private final int spaceSizeInPx;
    private int orientation;

    public SpaceItemDecoration(Context context, int orientation) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        spaceSizeInPx = (int) (DEFAULT_SIZE_IN_DP * metrics.density);
        setOrientation(orientation);
    }

    public SpaceItemDecoration(int orientation, int spaceSizeInPx) {
        this.spaceSizeInPx = spaceSizeInPx;
        setOrientation(orientation);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (orientation == VERTICAL)
            outRect.set(0, 0, 0, spaceSizeInPx);
        else
            outRect.set(0, 0, spaceSizeInPx, 0);
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL)
            throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL.");
        this.orientation = orientation;
    }
}