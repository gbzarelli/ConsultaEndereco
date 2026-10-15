package br.com.helpdev.consultaendereco.screens.whereami;

import br.com.helpdev.consultaendereco.R;
import br.com.helpdev.consultaendereco.ads.FragmentAds;
import br.com.helpdev.consultaendereco.persistencia.ManagerMyPlaces;
import br.com.helpdev.consultaendereco.persistencia.ObLocais;
import br.com.helpdev.consultaendereco.process.AsyncGeocoder;
import br.com.helpdev.consultaendereco.process.AsyncGeocoder.CallbackAsyncGeo;
import br.com.helpdev.consultaendereco.uteis.AlertDialogFavority;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class FragWhereAmI extends FragmentAds implements OnMapReadyCallback,
        OnMyLocationButtonClickListener, CallbackAsyncGeo, OnClickListener,
        OnCheckedChangeListener {

    private static final int LOCATION_PERMISSION_REQUEST = 42;
    private GoogleMap mMap;
    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
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
        textView = viewGroup.findViewById(R.id.frag_where_ami_texto);
        checkBox = viewGroup.findViewById(R.id.frag_where_ami_star);
        imageButton = viewGroup.findViewById(R.id.frag_where_ami_refresh);
        imageButton.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(this);
        managerMyPlaces = ManagerMyPlaces.getInstance(requireActivity());
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                Location location = result == null ? null : result.getLastLocation();
                if (location != null && !lastLocation) {
                    showLocation(location);
                }
            }
        };
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return viewGroup;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        updateMyLocationLayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasLocationPermission()) {
            startLocationUpdates();
        } else {
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSION_REQUEST);
        }
    }

    private boolean hasLocationPermission() {
        Context context = getContext();
        return context != null && (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void updateMyLocationLayer() {
        if (mMap != null && hasLocationPermission()) {
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException ignored) {
                // Permission can be revoked between the check and the Maps call.
            }
        }
    }

    private void startLocationUpdates() {
        updateMyLocationLayer();
        if (!isLocationEnabled()) {
            showLocationSettingsPrompt();
            return;
        }
        LocationRequest request = new LocationRequest.Builder(5000L)
                .setMinUpdateIntervalMillis(1000L)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build();
        try {
            locationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
            reposicionarCamera();
        } catch (SecurityException ignored) {
            textView.setText(R.string.frag_whami_verify_gps);
        }
    }

    private boolean isLocationEnabled() {
        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void showLocationSettingsPrompt() {
        textView.setText(R.string.frag_whami_verify_gps);
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.alert_attention)
                .setMessage(R.string.alert_msg_habilit_gps)
                .setPositiveButton(R.string.alert_title_habilit_gps, (dialog, id) ->
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (hasLocationPermission()) {
                startLocationUpdates();
            } else {
                textView.setText(R.string.frag_whami_verify_gps);
                if (mMap != null) {
                    try {
                        mMap.setMyLocationEnabled(false);
                    } catch (SecurityException ignored) { }
                }
            }
        }
    }

    private void reposicionarCamera() {
        if (!hasLocationPermission()) {
            return;
        }
        textView.setText(R.string.frag_whami_verifying);
        checkBox.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        lastLocation = false;
        try {
            locationClient.getLastLocation().addOnSuccessListener(location -> {
                if (!isAdded()) return;
                if (location != null) {
                    showLocation(location);
                } else {
                    textView.setText(R.string.frag_whami_verifying_gps);
                }
            });
        } catch (SecurityException ignored) {
            textView.setText(R.string.frag_whami_verify_gps);
        }
    }

    private void showLocation(Location location) {
        if (!isAdded()) return;
        lastLocation = true;
        AsyncGeocoder asyncGeocoder = new AsyncGeocoder(requireActivity(), this);
        asyncGeocoder.execute(location);
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 17));
        }
    }

    @Override
    public void onPause() {
        if (locationClient != null && locationCallback != null) {
            locationClient.removeLocationUpdates(locationCallback);
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
        if (!isAdded()) return;
        imageButton.setVisibility(View.VISIBLE);
        if (address != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                if (address.getAddressLine(i) != null) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(address.getAddressLine(i));
                }
            }
            String resultAddress = sb.toString().trim();
            textView.setText(resultAddress);
            try {
                boolean found = false;
                for (ObLocais place : managerMyPlaces.getMyPlaces().getMeus_locais()) {
                    if (resultAddress.equalsIgnoreCase(place.getLocal().trim())) {
                        checkBox.setChecked(true);
                        checkBox.setTag(place);
                        found = true;
                        break;
                    }
                    checkBox.setChecked(false);
                }
                if (!found) checkBox.setTag(new ObLocais("", resultAddress, "", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            checkBox.setVisibility(View.VISIBLE);
        } else if (code == CODE_WITHOUT_CONNECTION) {
            textView.setText(R.string.frag_whami_without_connection);
        } else {
            textView.setText(R.string.frag_whami_address_notfound);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getVisibility() == View.VISIBLE) {
            AlertDialogFavority dialog = new AlertDialogFavority(requireActivity(),
                    (ObLocais) checkBox.getTag(), checkBox, this, isChecked);
            dialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        reposicionarCamera();
    }

    @Override
    public void onDestroyView() {
        locationCallback = null;
        mMap = null;
        Fragment mapFragment = getChildFragmentManager().findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            getChildFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
        super.onDestroyView();
    }
}
