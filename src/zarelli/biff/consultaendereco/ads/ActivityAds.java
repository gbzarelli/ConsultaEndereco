package zarelli.biff.consultaendereco.ads;

import zarelli.biff.consultaendereco.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public abstract class ActivityAds extends ActionBarActivity {

	private GerenteAds gerenteAds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myOnCreate(savedInstanceState);
		gerenteAds = new GerenteAds(this, R.id.adView);
		gerenteAds.iniciar();
	}

	public abstract void myOnCreate(Bundle savedInstanceState);

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
	public void onDestroy() {
		gerenteAds.destroy();
		super.onDestroy();
	}
}
