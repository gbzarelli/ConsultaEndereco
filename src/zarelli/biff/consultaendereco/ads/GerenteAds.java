package zarelli.biff.consultaendereco.ads;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GerenteAds {

	private AdView mAdView;
	private int layout;
	private View view;
	private Activity activity;

	public GerenteAds(Activity activity, int layout) {
		super();
		this.activity = activity;
		this.layout = layout;
	}

	public GerenteAds(View activity, int layout) {
		super();
		this.view = activity;
		this.layout = layout;
	}

	public void iniciar() {
		if (view == null) {
			mAdView = (AdView) activity.findViewById(layout);
		} else {
			mAdView = (AdView) view.findViewById(layout);
		}
		mAdView.setVisibility(View.GONE);

		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				mAdView.setVisibility(View.VISIBLE);
				super.onAdLoaded();
			}
		});
		mAdView.loadAd(new AdRequest.Builder().build());
	}

	public void pause() {
		if (mAdView != null) {
			mAdView.pause();
		}
	}

	public void resume() {
		if (mAdView != null) {
			mAdView.resume();
		}
	}

	public void destroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
	}

}
