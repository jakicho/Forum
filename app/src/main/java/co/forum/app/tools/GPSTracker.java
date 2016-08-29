package co.forum.app.tools;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import co.forum.app.MainActivity;
import co.forum.app.SharedPref;

public class GPSTracker extends Service implements LocationListener {

    private final Context context;

    boolean user_authorisation;
    boolean isGPSenabled = false;
    boolean isNetworkedEnabled = false;
    boolean canGetLocation = false;

    Location location;

    double latitude;
    double longitude;

    // pour updater la position
    private static final long MIN_DISTANCE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    //long each5min = 1000 * 60 * 5;
    //float distance500m = 500f;

    protected LocationManager locationManager;

    public GPSTracker(Context context, boolean user_authorisation) {
        this.context = context;
        this.user_authorisation = user_authorisation;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkedEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkedEnabled) { //if (!isGPSenabled && !isNetworkedEnabled) {
                // on ne peut pas localiser
                showSettingAlert();

            } else {
                // on peut localiser
                this.canGetLocation = true;


                if (isNetworkedEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_FOR_UPDATES, this);


                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }


                /*
                if (isGPSenabled) {
                    if(location == null){
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_FOR_UPDATES, this);
                        if(locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if(location != null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                */


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void stopUsingGPS(){
        if(locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    public void showSettingAlert(){

        if(user_authorisation) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            //alertDialog.setTitle("GPS SETTINGS");
            alertDialog.setMessage("Your GPS is not enabled.\n\nIt's more fun & fair if everyone shares his location (country and city) \uD83D\uDE0A \n\nYou can enable it on your settings menu");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ((MainActivity)MainActivity.context).localData.setUserData(SharedPref.KEY_GPS_AUTHORISATION, false);

                    dialog.cancel();
                }
            });

            alertDialog.show();
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
