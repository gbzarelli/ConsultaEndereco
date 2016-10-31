package zarelli.biff.consultaendereco.screens.consult;

import java.util.ArrayList;

import zarelli.biff.consultaendereco.R;
import zarelli.biff.consultaendereco.adapter.AdapterResult;
import zarelli.biff.consultaendereco.ads.ActivityAds;
import zarelli.biff.consultaendereco.process.AsyncRequestAddress;
import zarelli.biff.consultaendereco.process.AsyncRequestAddress.CallbackRequestAddress;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class ScreenResult extends ActivityAds implements
		CallbackRequestAddress, OnClickListener {

	public static final String IT_ADDRESS = "end";
	public static final String IT_ZIPCODE = "ZIP";
	private ListView listView;
	private String value;
	private int type;
	private AdapterResult adapterResult;

	@Override
	public void myOnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.find_address_result);
		type = -1;
		value = "";
		if (getIntent().hasExtra(IT_ADDRESS)) {
			value = getIntent().getStringExtra(IT_ADDRESS);
			type = AsyncRequestAddress.TYPE_ADDRESS;
		} else if (getIntent().hasExtra(IT_ZIPCODE)) {
			value = getIntent().getStringExtra(IT_ZIPCODE);
			type = AsyncRequestAddress.TYPE_ZIPCODE;
		} else {
			finish();
			return;
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.find_result_listview);
		findViewById(R.id.find_result_recarregar).setOnClickListener(this);
		consultar();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void consultar() {
		AsyncRequestAddress asyncRequestAddress = new AsyncRequestAddress(type,
				this, this);
		asyncRequestAddress.execute(value);
	}

	@Override
	public void retorno(ArrayList<String> address) {
		findViewById(R.id.find_result_progressbar).setVisibility(View.GONE);
		findViewById(R.id.find_result_listview).setVisibility(View.VISIBLE);
		findViewById(R.id.find_result_error).setVisibility(View.GONE);

		if (address != null) {
			if (address.size() > 0) {
				adapterResult = new AdapterResult(this, address);
				listView.setAdapter(adapterResult);
			} else {
				findViewById(R.id.find_result_noresults).setVisibility(
						View.VISIBLE);
			}
		} else {
			findViewById(R.id.find_result_progressbar).setVisibility(View.GONE);
			findViewById(R.id.find_result_listview).setVisibility(View.GONE);
			findViewById(R.id.find_result_error).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.find_result_recarregar:
			findViewById(R.id.find_result_progressbar).setVisibility(
					View.VISIBLE);
			findViewById(R.id.find_result_listview).setVisibility(View.GONE);
			findViewById(R.id.find_result_error).setVisibility(View.GONE);
			consultar();
			break;
		default:
			break;
		}
	}

}
