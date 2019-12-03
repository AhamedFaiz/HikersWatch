package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    TextView infoTextView;


    /**What to do if permission is granted only when app is started
     *
     * @param requestCode : we can check which permission is given using this. But its irrelevant
     *                   bcz only one permission is  required
     * @param permissions
     * @param grantResults we check this to find if permission is granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length >0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
                //listening for location

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10,locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                reverseGeocoder(location);
            }
        }
    }


    /** Fuction to decode latitude and longitude of the location
     * Get the address from location
     * Append all that in a textView
     * @param location
     */
    public  void reverseGeocoder (Location location){


        infoTextView.setText("");
        infoTextView.append("Latitude : " + location.getLatitude() + "\n" + "\n");
        infoTextView.append("Longitude : " + location.getLongitude() + "\n" + "\n");
        infoTextView.append("Accuracy : " + location.getAccuracy() + "\n" + "\n");
        infoTextView.append("Altitude :" + location.getAltitude() + "\n" + "\n");

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            Log.i("Address", "onLocationChanged: " + addressList.get(0));
            if(addressList!= null && addressList.size()> 0){
                infoTextView.append("Address : " + "\n");
                if(addressList.get(0).getSubThoroughfare() != null){
                    infoTextView.append(addressList.get(0).getSubThoroughfare());
                    infoTextView.append("\n");
                }
                if(addressList.get(0).getThoroughfare() != null){
                    infoTextView.append(addressList.get(0).getThoroughfare());
                    infoTextView.append("\n");
                }
                if(addressList.get(0).getLocality() != null){
                    infoTextView.append(addressList.get(0).getLocality());
                    infoTextView.append("\n");
                }
                if(addressList.get(0).getSubAdminArea() != null){
                    infoTextView.append(addressList.get(0).getSubAdminArea());
                    infoTextView.append("\n");
                }

                if(addressList.get(0).getPostalCode() != null){
                    infoTextView.append(addressList.get(0).getPostalCode() + " P.O");
                    infoTextView.append("\n");
                }
                if(addressList.get(0).getAdminArea() != null){
                    infoTextView.append(addressList.get(0).getAdminArea());
                    infoTextView.append("\n");
                }
                if(addressList.get(0).getCountryName() != null){
                    infoTextView.append(addressList.get(0).getCountryName());
                    infoTextView.append("\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoTextView = findViewById(R.id.infoTextView);

        /** Looking weather there is a change in location of the user
         * if there is change the changed address is added to a address list
         * That address list details then shown as a textView
         */


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                reverseGeocoder(location);
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
        };


        /** Checking if Location accessing permission is given
         * if not given asking user for location permission with a request code 1
         * if user has already given permission then updating that location
         * works only on the startup
         */
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            // Asking for permission

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {
            // if we have permission listening for location change

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10,locationListener);
           Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           reverseGeocoder(lastKnownLocation);
        }
    }
}
