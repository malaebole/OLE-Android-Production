package ae.oleapp.owner;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityMapBinding;
import ae.oleapp.util.LocationHelperFragment;

public class OleMapActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener {

    private OleactivityMapBinding binding;
    private GoogleMap googleMap;
    private double lat = 0, lng = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleBar.toolbarTitle.setText(R.string.location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            ///if play services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                    requestCode);
            dialog.show();

        } else {
            mapFragment.getMapAsync(this);
        }

        binding.titleBar.backBtn.setOnClickListener(this);
        binding.btnDone.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnCameraIdleListener(this);
        this.googleMap.setOnMyLocationButtonClickListener(this);
        getLocation();
    }

    private void getLocation() {
        LocationHelperFragment helper = LocationHelperFragment.getInstance(getSupportFragmentManager());
        helper.startLocationRequest(new LocationHelperFragment.LocationCallback() {
                @Override
                public void onLocationRetrieved(Location loc) {
                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

                    // Move camera to current location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }

                    lat = latLng.latitude;
                    lng = latLng.longitude;

                    // Zoom in the map
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                }

                @Override
                public void onLocationError(String message) {
                    // Handle failure here — maybe show a message or fallback behavior
                    // You can log or toast the error message
                }
            });
    }


//    private void getLocation() {
//        new AirLocation(this, true, true, new AirLocation.Callbacks() {
//            @Override
//            public void onSuccess(Location loc) {
//                // do something
//                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
//
//                // Showing the current location in Google Map
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)); //checkx
//                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                googleMap.setMyLocationEnabled(true);
//
//                lat = latLng.latitude;
//                lng = latLng.longitude;
//                // Zoom in the Google Map
//                googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
//            }
//
//            @Override
//            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
//                // do something
//
//            }
//        });
//    }

    @Override
    public void onCameraIdle() {
        lat = googleMap.getCameraPosition().target.latitude;
        lng = googleMap.getCameraPosition().target.longitude;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        lat = googleMap.getCameraPosition().target.latitude;
        lng = googleMap.getCameraPosition().target.longitude;
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.titleBar.backBtn) {
            finish();
        }
        else if (v == binding.btnDone) {
            doneClicked();
        }
    }

    private void doneClicked() {
        if (lat == 0 || lng == 0) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        setResult(RESULT_OK, intent);
        finish();
    }


}
