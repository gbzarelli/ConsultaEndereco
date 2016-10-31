package zarelli.biff.consultaendereco.process;

import java.util.ArrayList;

import zarelli.biff.consultaendereco.MyApplication;
import zarelli.biff.consultaendereco.consultas.cep.ConsultaCEP;
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

		public void retorno(ArrayList<String> address);
	}

	public static final int TYPE_ADDRESS = 0;
	public static final int TYPE_ZIPCODE = 1;

	private int tipo;
	private Activity activity;
	private CallbackRequestAddress requestAddress;

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
		try {
			if (tipo == TYPE_ZIPCODE) {
				ConsultaCEP consultaCEP = new ConsultaCEP();
				retorno.add(consultaCEP.getEndereco(value));
			} else {
				Geocoder geocoder = new Geocoder(activity);
				for (Address address : geocoder.getFromLocationName(value, 20)) {
					if (address.getMaxAddressLineIndex() > 0) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
							sb.append(address.getAddressLine(i));
							if ((i + 1) < address.getMaxAddressLineIndex()) {
								sb.append(", ");
							}
						}
						retorno.add(sb.toString().trim());
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
		requestAddress.retorno(result);
		super.onPostExecute(result);
	}

	public void setActivity(Activity atividade) {
		this.activity = atividade;
	}

}
