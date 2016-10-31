package zarelli.biff.consultaendereco.screens.edit;

import zarelli.biff.consultaendereco.MyApplication;
import zarelli.biff.consultaendereco.R;
import zarelli.biff.consultaendereco.ads.ActivityAds;
import zarelli.biff.consultaendereco.persistencia.ManagerMyPlaces;
import zarelli.biff.consultaendereco.persistencia.ObLocais;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ScreenEditMyPlace extends ActivityAds implements OnClickListener {

	public static final String IT_OBLOCAL = "oblocal";
	public static final String IT_ISEDIT = "isEDIT";
	private ObLocais ob;
	private EditText name, tel, note;

	@Override
	public void myOnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.edit_infos);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		ob = (ObLocais) getIntent().getSerializableExtra(IT_OBLOCAL);
		((TextView) findViewById(R.id.edit_inf_end)).setText(ob.getLocal());

		name = (EditText) findViewById(R.id.edit_inf_name);
		tel = (EditText) findViewById(R.id.edit_inf_tel);
		note = (EditText) findViewById(R.id.edit_inf_note);

		if (getIntent().hasExtra(IT_ISEDIT)) {
			name.setText(ob.getNome());
			tel.setText(ob.getTelefone());
			note.setText(ob.getObs());
		}

		findViewById(R.id.edit_inf_bt_save).setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		ObLocais newOb = new ObLocais(ob.getNome(), ob.getLocal(),
				ob.getTelefone(), ob.getObs());

		newOb.setNome(name.getText().toString());
		newOb.setTelefone(tel.getText().toString());
		newOb.setObs(note.getText().toString());

		try {
			ManagerMyPlaces mp = ManagerMyPlaces.getInstance(this);

			if (getIntent().hasExtra(IT_ISEDIT)) {
				int index = getIntent().getIntExtra(IT_ISEDIT, -1);
				if (index > -1) {
					mp.getMyPlaces().getMeus_locais().remove(index);
				}
			}
			mp.getMyPlaces().addItem(newOb);
			mp.persistir();
		} catch (Exception e) {
			Log.e(MyApplication.TAG, "write", e);
		}
		finish();
	}
}
