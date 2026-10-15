package br.com.helpdev.consultaendereco.ads;

import android.app.Activity;
import android.view.View;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GerenteAds {
    private AdView adView;
    private final int viewId;
    private final View rootView;
    private final Activity activity;

    public GerenteAds(Activity activity, int viewId) {
        this.activity = activity;
        this.viewId = viewId;
        this.rootView = null;
    }

    public GerenteAds(View rootView, int viewId) {
        this.activity = null;
        this.viewId = viewId;
        this.rootView = rootView;
    }

    public void iniciar() {
        adView = rootView == null ? activity.findViewById(viewId) : rootView.findViewById(viewId);
        if (adView == null) return;
        adView.setVisibility(View.GONE);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(com.google.android.gms.ads.LoadAdError error) {
                adView.setVisibility(View.GONE);
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }

    public void pause() {
        if (adView != null) adView.pause();
    }

    public void resume() {
        if (adView != null) adView.resume();
    }

    public void destroy() {
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }
}
