package zarelli.biff.consultaendereco.screens.whereami;

import zarelli.biff.consultaendereco.R;
import zarelli.biff.consultaendereco.ads.FragmentAds;
import zarelli.biff.consultaendereco.persistencia.ManagerMyPlaces;
import zarelli.biff.consultaendereco.persistencia.ObLocais;
import zarelli.biff.consultaendereco.process.AsyncGeocoder;
import zarelli.biff.consultaendereco.process.AsyncGeocoder.CallbackAsyncGeo;
import zarelli.biff.consultaendereco.uteis.AlertDialogFavority;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class FragWhereAmI extends FragmentAds implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener, CallbackAsyncGeo, OnClickListener,
		OnCheckedChangeListener {

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private GoogleMap mMap;
	private LocationClient mLocationClient;
	private TextView textView;
	private CheckBox checkBox;
	private ImageButton imageButton;
	private LocationManager locationManager;
	private ManagerMyPlaces managerMyPlaces;
	private boolean lastLocation;

	@Override
	public View myOnCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.frag_where_ami, container, false);
		textView = (TextView) viewGroup.findViewById(R.id.frag_where_ami_texto);
		checkBox = (CheckBox) viewGroup.findViewById(R.id.frag_where_ami_star);
		imageButton = (ImageButton) viewGroup
				.findViewById(R.id.frag_where_ami_refresh);
		imageButton.setOnClickListener(this);
		checkBox.setOnCheckedChangeListener(this);
		managerMyPlaces = ManagerMyPlaces.getInstance(getActivity());
		return viewGroup;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {

			mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(
					R.id.mapa)).getMap();
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
				mMap.getUiSettings().setAllGesturesEnabled(true);
				mMap.setTrafficEnabled(true);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getActivity(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
			locationManager = (LocationManager) getActivity().getSystemService(
					Context.LOCATION_SERVICE);
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this);
		textView.setText(R.string.frag_whami_verifying_gps);
		checkBox.setVisibility(View.GONE);
		imageButton.setVisibility(View.GONE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			textView.setText(R.string.frag_whami_verify_gps);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.alert_attention);
			builder.setMessage(R.string.alert_msg_habilit_gps)
					.setPositiveButton(R.string.alert_title_habilit_gps,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent gpsOptionsIntent = new Intent(
											android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivity(gpsOptionsIntent);
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private void reposicionarCamera() {
		textView.setText(R.string.frag_whami_verifying);
		checkBox.setVisibility(View.GONE);
		imageButton.setVisibility(View.GONE);
		lastLocation = false;

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& mLocationClient != null) {
			Location location = mLocationClient.getLastLocation();
			if (location != null) {
				lastLocation = true;
				AsyncGeocoder asyncGeocoder = new AsyncGeocoder(getActivity(),
						this);
				asyncGeocoder.execute(location);

				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(location.getLatitude(), location
								.getLongitude()), 17));
			}

		} else {
			textView.setText(R.string.frag_whami_verify_gps);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (!lastLocation) {
			reposicionarCamera();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		System.out.println("falha ao conectar");
	}

	@Override
	public void onDisconnected() {
		System.out.println("disconnected");
	}

	@Override
	public void onPause() {
		if (mLocationClient != null) {
			mLocationClient.disconnect();
			mLocationClient.unregisterConnectionCallbacks(this);
			mLocationClient = null;
			lastLocation = false;
		}

		super.onPause();
	}

	@Override
	public boolean onMyLocationButtonClick() {
		reposicionarCamera();
		return true;
	}

	@Override
	public void resultGeo(Address address, int code) {
		imageButton.setVisibility(View.VISIBLE);
		if (address != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				sb.append(address.getAddressLine(i));
				if ((i + 1) < address.getMaxAddressLineIndex()) {
					sb.append(", ");
				}
			}
			String addres = sb.toString().trim();
			textView.setText(addres);

			try {
				boolean sucess = false;
				for (ObLocais ob : managerMyPlaces.getMyPlaces()
						.getMeus_locais()) {

					if (addres.equalsIgnoreCase((ob.getLocal().trim()))) {
						checkBox.setChecked(true);
						checkBox.setTag(ob);
						sucess = true;
						break;
					} else {
						checkBox.setChecked(false);
					}
				}
				if (!sucess) {
					checkBox.setTag(new ObLocais("", addres, "", ""));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			checkBox.setVisibility(View.VISIBLE);
		} else {
			if (code == CODE_WITHOUT_CONNECTION) {
				textView.setText(R.string.frag_whami_without_connection);
			} else {
				textView.setText(R.string.frag_whami_address_notfound);
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getVisibility() == View.VISIBLE) {
			AlertDialogFavority alFavority = new AlertDialogFavority(
					getActivity(), (ObLocais) checkBox.getTag(), checkBox,
					this, isChecked);
			alFavority.show();
		}
	}

	@Override
	public void onClick(View v) {
		reposicionarCamera();
	}

	@Override
	public void onDestroyView() {
		try {
			Fragment fragment = (getFragmentManager()
					.findFragmentById(R.id.mapa));
			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(fragment).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroyView();
	}

}
