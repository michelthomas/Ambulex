package com.michelthomas.ambulex.geocoding;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.michelthomas.ambulex.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Asynchronously handles an intent using a worker thread. Receives a ResultReceiver object and a
 * location through an intent. Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIS";

    /**
     * The receiver where results are forwarded from this service.
     */
    private ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a {@link android.os.ResultReceiver} in * MainActivity to process content
     * sent from this service.
     *
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(ConstantesGeocoding.RECEIVER);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(ConstantesGeocoding.LOCATION_DATA_EXTRA);

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = getString(R.string.no_location_data_provided);
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(ConstantesGeocoding.FAILURE_RESULT, errorMessage, null);
            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(ConstantesGeocoding.FAILURE_RESULT, errorMessage, null);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();
            String[] addressDB = new String[3];
            addressDB[0] = address.getThoroughfare();
            Log.v(TAG, "PEGOU A RUA");
            if(address.getSubLocality() == null){

                addressDB[1] = "CIDADE SEM BAIRRO";
                Log.v(TAG, "NÃO PEGOU O BAIRRO");
            }else{
                addressDB[1] = address.getSubLocality();
                Log.v(TAG, "PEGOU O BAIRRO");
            }
            if(address.getLocality() == null){
                addressDB[2] = "CIDADE NULA";
                Log.v(TAG, "CIDADE NULA");
            }else{
                addressDB[2] = address.getLocality();
                Log.v(TAG, "PEGOU A CIDADE");
            }


           /* Log.d("Addresses", "" + "Start to print the ArrayList");
            for (int i = 0; i < addresses.size(); i++) {
                HashMap itemAddress = new HashMap<String, String>();
                address = addresses.get(i);
                String addressline = "Addresses from getAddressLine(): ";
                for (int n = 0; n <= address.getMaxAddressLineIndex(); n++) {
                    addressline += " index n: " + n + ": " + address.getAddressLine(n) + ", ";
                }
                Log.v("Addresses: ", addressline);
                Log.v(" getAdminArea()", "" + address.getAdminArea());
                Log.v(" getCountryCode()", "" + address.getCountryCode());
                Log.v(" getCountryName()", "" + address.getCountryName());
                Log.v(" getFeatureName()", "" + address.getFeatureName());
                Log.v("Addresses getLocality()", "" + address.getLocality());
                Log.v(" getPostalCode()", "" + address.getPostalCode());
                Log.v("Addresses getPremises()", "" + address.getPremises());
                Log.v(" getSubAdminArea()", "" + address.getSubAdminArea());
                Log.v(" getSubLocality()", "" + address.getSubLocality());
                Log.v(" getSubThoroughfare()", "" + address.getSubThoroughfare());
                Log.v(" getThoroughfare()", "" + address.getThoroughfare());
            }*/
            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.no_address_found));
            deliverResultToReceiver(ConstantesGeocoding.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments), addressDB);
        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message, String[] addressDB) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantesGeocoding.RESULT_DATA_KEY, message);
        bundle.putStringArray(ConstantesGeocoding.RESULT_DATA_KEY_ARRAY, addressDB);
        mReceiver.send(resultCode, bundle);
    }
}