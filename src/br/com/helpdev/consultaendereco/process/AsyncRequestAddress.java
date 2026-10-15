package br.com.helpdev.consultaendereco.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.helpdev.consultaendereco.MyApplication;
import br.com.helpdev.consultaendereco.consultas.cep.ConsultaCEP;
import br.com.helpdev.consultaendereco.consultas.endereco.GeocodingResultParser;
import br.com.helpdev.consultaendereco.consultas.endereco.NominatimGeocoder;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncRequestAddress extends
		AsyncTask<String, Integer, ArrayList<String>> {

	public interface CallbackRequestAddress {

		public static final int ACTION_SUCCESS = 1;
		public static final int ACTION_ERROR = 2;

		public void retorno(ArrayList<String> address, boolean usedOpenStreetMap);
	}

	public static final int TYPE_ADDRESS = 0;
	public static final int TYPE_ZIPCODE = 1;

	private int tipo;
	private Activity activity;
	private CallbackRequestAddress requestAddress;
	private boolean usedOpenStreetMap;

	public AsyncRequestAddress(int tipo, Activity activity,
			CallbackRequestAddress requestAddress) {
		super();
		this.tipo = tipo;
		this.activity = activity;
		this.requestAddress = requestAddress;
	}

	@Override
	protected ArrayList<String> doInBackground(String... params) {
		String value = params[0];
		ArrayList<String> retorno = new ArrayList<String>();
		usedOpenStreetMap = false;
		try {
			if (tipo == TYPE_ZIPCODE) {
				ConsultaCEP consultaCEP = new ConsultaCEP();
				String address = consultaCEP.getEndereco(value);
				if (address != null && !address.trim().isEmpty()) {
					retorno.add(address);
				}
			} else {
				Exception platformFailure = null;
				try {
					Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
					List<Address> platformResults = geocoder.getFromLocationName(value, 20);
					Log.d(MyApplication.TAG, "Platform geocoder results: "
							+ (platformResults == null ? "null" : platformResults.size()));
					if (platformResults != null) {
						for (Address address : platformResults) {
							int maxLine = address.getMaxAddressLineIndex();
							List<String> lines = new ArrayList<>();
							for (int i = 0; i <= maxLine; i++) lines.add(address.getAddressLine(i));
							String formatted = GeocodingResultParser.formatAddressLines(lines);
							if (!formatted.isEmpty() && !retorno.contains(formatted)) retorno.add(formatted);
						}
					}
				} catch (Exception exception) {
					platformFailure = exception;
				}
				if (retorno.isEmpty()) {
					try {
						retorno.addAll(NominatimGeocoder.search(value));
						usedOpenStreetMap = true;
						Log.d(MyApplication.TAG, "OpenStreetMap geocoder results: " + retorno.size());
					} catch (Exception fallbackFailure) {
						if (platformFailure != null) fallbackFailure.addSuppressed(platformFailure);
						throw fallbackFailure;
					}
				}
			}
		} catch (Exception e) {
			Log.e(MyApplication.TAG, "consult", e);
			return null;
		}

		return retorno;
	}

	@Override
	protected void onPostExecute(ArrayList<String> result) {
		requestAddress.retorno(result, usedOpenStreetMap);
		super.onPostExecute(result);
	}

	public void setActivity(Activity atividade) {
		this.activity = atividade;
	}

}
