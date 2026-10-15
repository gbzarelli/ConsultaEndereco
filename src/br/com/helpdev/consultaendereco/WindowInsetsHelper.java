package br.com.helpdev.consultaendereco;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public final class WindowInsetsHelper {
    private WindowInsetsHelper() { }

    public static void apply(Activity activity) {
        ViewGroup content = activity.findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) return;
        View root = content.getChildAt(0);
        root.post(() -> {
            int statusBarHeight = dimension(activity.getResources(), "status_bar_height");
            int navigationBarHeight = dimension(activity.getResources(), "navigation_bar_height");
            int actionBarHeight = activity instanceof AppCompatActivity
                    && ((AppCompatActivity) activity).getSupportActionBar() != null
                    ? ((AppCompatActivity) activity).getSupportActionBar().getHeight() : 0;
            if (root instanceof DrawerLayout) {
                DrawerLayout drawer = (DrawerLayout) root;
                for (int index = 0; index < drawer.getChildCount(); index++) {
                    View child = drawer.getChildAt(index);
                    ViewGroup.LayoutParams params = child.getLayoutParams();
                    if (params instanceof DrawerLayout.LayoutParams) {
                        DrawerLayout.LayoutParams drawerParams = (DrawerLayout.LayoutParams) params;
                        drawerParams.topMargin = statusBarHeight + actionBarHeight;
                        drawerParams.bottomMargin = navigationBarHeight;
                        child.setLayoutParams(drawerParams);
                    }
                }
            } else {
                root.setPadding(root.getPaddingLeft(), root.getPaddingTop() + statusBarHeight + actionBarHeight,
                        root.getPaddingRight(), root.getPaddingBottom() + navigationBarHeight);
            }
        });
    }

    private static int dimension(Resources resources, String name) {
        int resourceId = resources.getIdentifier(name, "dimen", "android");
        return resourceId == 0 ? 0 : resources.getDimensionPixelSize(resourceId);
    }
}
