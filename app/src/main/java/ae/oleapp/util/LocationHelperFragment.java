package ae.oleapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.Task;

public class LocationHelperFragment extends Fragment {

    public interface LocationCallback {
        void onLocationRetrieved(Location location);
        void onLocationError(String message);
    }

    private LocationCallback callback;
    private FusedLocationProviderClient fusedClient;
    private SettingsClient settingsClient;

    private ActivityResultLauncher<String[]> permissionLauncher;
    private ActivityResultLauncher<IntentSenderRequest> settingsLauncher;

    private static final String TAG = "LocationHelperFragment";

    private boolean isReady = false;
    private boolean pendingStartRequest = false;

    public LocationHelperFragment() {
        // Required empty public constructor
    }

    public static LocationHelperFragment getInstance(FragmentManager fm) {
        LocationHelperFragment fragment = (LocationHelperFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new LocationHelperFragment();
            fm.beginTransaction().add(fragment, TAG).commit();
        }
        return fragment;
    }

    // Called externally by your fragment/activity
    public void startLocationRequest(LocationCallback callback) {
        this.callback = callback;

        if (isReady) {
            checkPermissionsAndStart();
        } else {
            // Fragment not ready yet, remember to start after attachment
            pendingStartRequest = true;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Initialize here, now that context is available
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        settingsClient = LocationServices.getSettingsClient(requireActivity());

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean granted = false;
                    for (Boolean b : result.values()) {
                        granted = granted || b;
                    }
                    if (granted) {
                        checkLocationSettings();
                    } else {
                        if (callback != null) callback.onLocationError("Location permission denied.");
                    }
                });

        settingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        fetchLastLocation();
                    } else {
                        if (callback != null) callback.onLocationError("Location not enabled by user.");
                    }
                });

        isReady = true;

        // If start request was called before fragment was ready, start now
        if (pendingStartRequest) {
            pendingStartRequest = false;
            checkPermissionsAndStart();
        }
    }

    // Remove initialization from onCreate and do it here (or leave onCreate empty)

    private void checkPermissionsAndStart() {
        if (!hasLocationPermissions()) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            checkLocationSettings();
        }
    }

    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)
                .setFastestInterval(1000);

        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build();

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(request);
        task.addOnSuccessListener(locationSettingsResponse -> fetchLastLocation())
                .addOnFailureListener(e -> {
                    if (e instanceof ResolvableApiException rae) {
                        try {
                            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(rae.getResolution()).build();
                            settingsLauncher.launch(intentSenderRequest);
                        } catch (Exception ex) {
                            if (callback != null) callback.onLocationError("Failed to prompt location settings.");
                        }
                    } else {
                        if (callback != null) callback.onLocationError("Location settings not satisfied.");
                    }
                });
    }

    @SuppressWarnings("MissingPermission")
    private void fetchLastLocation() {
        if (!hasLocationPermissions()) {
            if (callback != null) callback.onLocationError("Missing location permission.");
            return;
        }

        fusedClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        if (callback != null) callback.onLocationRetrieved(location);
                    } else {
                        requestNewLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onLocationError("Failed to retrieve location.");
                });
    }

    @SuppressWarnings("MissingPermission")
    private void requestNewLocation() {
        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdates(1)
                .build();

        final com.google.android.gms.location.LocationCallback locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                if (result.getLastLocation() != null) {
                    if (callback != null) {
                        requireActivity().runOnUiThread(() -> callback.onLocationRetrieved(result.getLastLocation()));
                    }
                } else {
                    if (callback != null) callback.onLocationError("No location received.");
                }
                fusedClient.removeLocationUpdates(this);
            }
        };

        fusedClient.requestLocationUpdates(
                request,
                locationCallback,
                requireActivity().getMainLooper()
        );
    }
}

