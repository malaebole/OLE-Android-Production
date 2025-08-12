package ae.oleapp.shop;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityShopMapBinding;
import ae.oleapp.util.LocationHelperFragment;

public class ShopMapActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener {

    private ActivityShopMapBinding binding;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private double lat = 0, lng = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
applyEdgeToEdge(binding.getRoot());

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

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

        binding.relBack.setOnClickListener(this);
        binding.tvSearch.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnCameraIdleListener(this);
        this.googleMap.setOnMyLocationButtonClickListener(this);
        setLocationButtonMargin();
        getLocation();
    }

    private void setLocationButtonMargin() {
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        rlp.setMargins(0, (int)getResources().getDimension(R.dimen._70sdp), (int)getResources().getDimension(R.dimen._15sdp), 0);
        locationButton.setLayoutParams(rlp);
    }

//    private void getLocation() {
//        new AirLocation(this, true, true, new AirLocation.Callbacks() {
//            @Override
//            public void onSuccess(Location loc) {
//                // do something
//                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
//
//                // Showing the current location in Google Map
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                googleMap.setMyLocationEnabled(true);
//                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
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

    private void getLocation() {
        LocationHelperFragment helper = LocationHelperFragment.getInstance(getSupportFragmentManager());
        helper.startLocationRequest(new LocationHelperFragment.LocationCallback() {
                @Override
                public void onLocationRetrieved(Location location) {
                    if (location == null || googleMap == null) return;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // Enable location features on the map
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }

                    // Move and zoom to current location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                    lat = latLng.latitude;
                    lng = latLng.longitude;
                }

                @Override
                public void onLocationError(String message) {
                    // Optional: Show an error or retry
                    Log.e("LocationError", message);
                }
            });
    }



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
        if (v == binding.relBack) {
            finish();
        }
        else if (v == binding.tvSearch) {
            searchClicked();
        }
        else if (v == binding.btnConfirm) {
            confirmClicked();
        }
    }

    private void searchClicked() {
        binding.tvSearch.setText(R.string.search_for_address);
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, 1110);
    }

    private void confirmClicked() {
        if (lat == 0 || lng == 0) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("AddAddressFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        AddAddressFragment addressFragment = new AddAddressFragment(lat, lng);
        addressFragment.setFragmentCallback(new AddAddressFragment.AddAddressFragmentCallback() {
            @Override
            public void addressAdded(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
                ShopMapActivity.this.finish();
            }
        });
        addressFragment.show(fragmentTransaction, "AddAddressFragment");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1110) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;
                binding.tvSearch.setText(place.getName());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("MAP", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}