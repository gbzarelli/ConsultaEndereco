package zarelli.biff.consultaendereco.process;

import java.io.IOException;
import java.util.List;

import zarelli.biff.consultaendereco.MyApplication;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncGeocoder extends AsyncTask<Location, Void, Address> {

	public interface CallbackAsyncGeo {
		public static final int CODE_SUCESS = 0;
		public static final int CODE_WITHOUT_RESULTS = 1;
		public static final int CODE_WITHOUT_CONNECTION = 2;

		public void resultGeo(Address address, int code);
	}

	private Context context;
	private CallbackAsyncGeo callbackAsyncGeo;
	private int code;

	public AsyncGeocoder(Context context, CallbackAsyncGeo callbackAsyncGeo) {
		this.context = context;
		this.callbackAsyncGeo = callbackAsyncGeo;
	}

	@Override
	protected Address doInBackground(Location... params) {
		Location location = params[0];
		Geocoder geocoder = new Geocoder(context);
		Address addrs = null;
		code = CallbackAsyncGeo.CODE_SUCESS;
		try {
			List<Address> addrss = geocoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 1);
			if (addrss != null && addrss.size() > 0) {
				addrs = addrss.get(0);
			} else {
				code = CallbackAsyncGeo.CODE_WITHOUT_RESULTS;
			}
		} catch (IOException e) {
			Log.e(MyApplication.TAG, "IOException", e);
			;
			code = CallbackAsyncGeo.CODE_WITHOUT_CONNECTION;
		}
		return addrs;
	}

	@Override
	protected void onPostExecute(Address result) {
		super.onPostExecute(result);
		if (callbackAsyncGeo != null) {
			callbackAsyncGeo.resultGeo(result, code);
		}
	}

}
