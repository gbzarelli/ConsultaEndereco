package zarelli.biff.consultaendereco.ads;

import zarelli.biff.consultaendereco.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class FragmentAds extends Fragment {

	private GerenteAds gerenteAds;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = myOnCreateView(inflater, container, savedInstanceState);
		gerenteAds = new GerenteAds(view, R.id.adView);
		gerenteAds.iniciar();
		return view;
	}

	public abstract View myOnCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

	@Override
	public void onPause() {
		gerenteAds.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		gerenteAds.resume();
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		gerenteAds.destroy();
		super.onDestroyView();
	}
}
