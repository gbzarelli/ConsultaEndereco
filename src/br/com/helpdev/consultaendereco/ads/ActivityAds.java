package br.com.helpdev.consultaendereco.ads;

import br.com.helpdev.consultaendereco.R;
import br.com.helpdev.consultaendereco.WindowInsetsHelper;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class ActivityAds extends AppCompatActivity {

	private GerenteAds gerenteAds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myOnCreate(savedInstanceState);
		WindowInsetsHelper.apply(this);
		gerenteAds = new GerenteAds(this, R.id.adView);
		gerenteAds.iniciar();
	}

	public abstract void myOnCreate(Bundle savedInstanceState);

	@Override
	public void onPause() {
		if (gerenteAds != null) gerenteAds.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		if (gerenteAds != null) gerenteAds.resume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (gerenteAds != null) gerenteAds.destroy();
		super.onDestroy();
	}
}
