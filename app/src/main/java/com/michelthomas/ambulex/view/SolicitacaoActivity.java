package com.michelthomas.ambulex.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.michelthomas.ambulex.BuildConfig;
import com.michelthomas.ambulex.R;
import com.michelthomas.ambulex.geocoding.ConstantesGeocoding;
import com.michelthomas.ambulex.geocoding.FetchAddressIntentService;
import com.michelthomas.ambulex.model.Solicitacao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.michelthomas.ambulex.data.AmbulexContract.*;


public class SolicitacaoActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = SolicitacaoActivity.class.getSimpleName();

    private MapView mMapView;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     */
    private boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    private String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    /**
     * Displays the location address.
     */
    private TextView mLocationAddressTextView;

    /**
     * Visible while the address is being fetched.
     */
    private ProgressBar mProgressBar;

    /**
     * Kicks off the request to fetch an address when pressed.
     */
    private Button mConfirmarLocalButton;
    private Button mSolicitarButton;
    private Spinner mMotivoSpinner;
    private Solicitacao mSolicitacao;
    private GoogleMap mMap;
    private SharedPreferences mPreferences;
    private String[] mAddressArray;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitacao);

        mAddressArray = new String[3];

        mPreferences = getSharedPreferences(MainActivity.PREFS_NAME, 0);



        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mResultReceiver = new AddressResultReceiver(new Handler());

        mMotivoSpinner = findViewById(R.id.spinner_motivo);
        mLocationAddressTextView = (TextView) findViewById(R.id.local_text_view);
        mConfirmarLocalButton = (Button) findViewById(R.id.confirmar_button);
        mSolicitarButton = findViewById(R.id.motivo_solicitar_button);
        mSolicitacao = new Solicitacao();

        mAddressRequested = false;
        mAddressOutput = "";
        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupSpinner();
        fetchAddress();

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getAddress();
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    private void setupSpinner() {
        ArrayAdapter motivoSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_motivos, android.R.layout.simple_spinner_item);
        motivoSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mMotivoSpinner.setAdapter(motivoSpinnerAdapter);

        mMotivoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selecionado = (String) adapterView.getItemAtPosition(position);
                if (!selecionado.isEmpty()){
                    mSolicitacao.setMotivo(selecionado);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSolicitacao.setMotivo("");
            }

        });


    }

    /**
     * Runs when user clicks the Fetch Address button.
     */
    public void fetchAddress() {
        if (mLastLocation != null) {
            startIntentService();
            return;
        }

        // If we have not yet retrieved the user location, we process the user's request by setting
        // mAddressRequested to true. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;

    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(ConstantesGeocoding.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(ConstantesGeocoding.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");
                            return;
                        }

                        mLastLocation = location;
                        LatLng local = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        //LatLng sydney = new LatLng(-34, 151);
                        mMap.addMarker(new MarkerOptions().position(local).title("Sua localização"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 17f));

                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            showSnackbar(getString(R.string.no_geocoder_available));
                            return;
                        }

                        // If the user pressed the fetch address button before we had the location,
                        // this will be set to true indicating that we should kick off the intent
                        // service after fetching the location.
                        if (mAddressRequested) {
                            startIntentService();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);
                    }
                });
    }

    /**
     * Updates the address in the UI.
     */
    private void displayAddressOutput() {
        mLocationAddressTextView.setText(mAddressOutput);
    }



    /**
     * Shows a toast with the given text.
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void confirmarLocal(View view) {
        mSolicitarButton.setVisibility(View.VISIBLE);
        mSolicitarButton.setEnabled(true);
    }

    public void solicitarAmbulancia(View view) {
        int usuarioID = getUsuarioLogadoID();
        int ambulanciaID = getAmbulanciaDisponivelID();
        int localID = getLocalID();

        ContentValues values = new ContentValues();
        values.put(SolicitacaoEntry.COLUMN_SOLICITACAO_USUARIO_ID, usuarioID);
        values.put(SolicitacaoEntry.COLUMN_SOLICITACAO_LOCAL_ID, localID);
        values.put(SolicitacaoEntry.COLUMN_SOLICITACAO_AMBULANCIA_ID, ambulanciaID);
        values.put(SolicitacaoEntry.COLUMN_SOLICITACAO_MOTIVO, mSolicitacao.getMotivo());
        values.put(SolicitacaoEntry.COLUMN_SOLICITACAO_DATA, getDateTime());
        Uri uri = getContentResolver().insert(SolicitacaoEntry.CONTENT_URI, values);
        String[] projection = {SolicitacaoEntry.COLUMN_SOLICITACAO_DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        String data = cursor.getString(cursor.getColumnIndex(SolicitacaoEntry.COLUMN_SOLICITACAO_DATA));
        if (uri != null) {
            Log.v(TAG, "Uri da solicitacao: " + uri.toString());
            Log.v(TAG, "Data: " + data);

        }else{
            Log.v(TAG, "Uri da solicitacao null");
        }
    }

    private int getLocalID() {
        Uri local = inserirLocal();
        int localID = Integer.parseInt(local.getLastPathSegment());
        Log.v(TAG, "localID: " + localID);
        return localID;
    }

    private Uri inserirLocal() {
        ContentValues values = new ContentValues();

        Log.v(TAG, mAddressOutput.trim());
        /*for (String value : mAddressArray) {
            Log.v(TAG, value.trim());//VER AQUI NO LOG!!!
        }*/

        /*String[] projection = {LocalEntry._ID, LocalEntry.COLUMN_LOCAL_LATITUDE, LocalEntry.COLUMN_LOCAL_LONGITUDE};

        Cursor cursor = getContentResolver().query(LocalEntry.CONTENT_URI, projection, null, null, null);
        if(cursor.getColumnIndex(LocalEntry._ID) > 0){
            cursor.moveToFirst();
            if(!cursor.isNull(cursor.getColumnIndex(LocalEntry._ID))){
                do{
                    if(cursor.getString(cursor.getColumnIndex(LocalEntry.COLUMN_LOCAL_LATITUDE)).equals(String.valueOf(mLastLocation.getLatitude())) &&
                            cursor.getString(cursor.getColumnIndex(LocalEntry.COLUMN_LOCAL_LONGITUDE)).equals(String.valueOf(mLastLocation.getLongitude()))){
                        Log.v(TAG, "ENTROU NO IF DO LOCAL IGUAL");
                        return Uri.withAppendedPath(LocalEntry.CONTENT_URI, cursor.getString(cursor.getColumnIndex(LocalEntry._ID)));
                    }
                }while (cursor.moveToNext());
                cursor.close();
            }

        }else {
            Log.v(TAG, "CURSOR COUNT: " + cursor.getCount());*/


            values.put(LocalEntry.COLUMN_LOCAL_RUA, mAddressArray[0]);
            values.put(LocalEntry.COLUMN_LOCAL_BAIRRO, mAddressArray[1]);
            values.put(LocalEntry.COLUMN_LOCAL_CIDADE, mAddressArray[2]);
            values.put(LocalEntry.COLUMN_LOCAL_LATITUDE, Double.toString(mLastLocation.getLatitude()));
            values.put(LocalEntry.COLUMN_LOCAL_LONGITUDE, Double.toString(mLastLocation.getLongitude()));



        return getContentResolver().insert(LocalEntry.CONTENT_URI, values);
    }

    private int getUsuarioLogadoID(){
        /*String[] projection = {UsuarioEntry._ID};
        Cursor cursor = getContentResolver().query(usuario, projection, null, null, null);
        cursor.moveToFirst();
        int usuarioID = cursor.getInt(cursor.getColumnIndex(UsuarioEntry._ID));
        cursor.close();*/
        int usuarioID = Integer.parseInt(mPreferences.getString(MainActivity.PREFS_USUARIO_LOGADO, "default"));
        Log.v(TAG, "usuarioID: " + usuarioID);
        return usuarioID;
    }
    private int getAmbulanciaDisponivelID(){
        String projection[] = {AmbulanciaEntry._ID};
        String selection = AmbulanciaEntry.COLUMN_AMBULANCIA_DISPONIVEL + "=?";
        String[] selectionArgs = {"1"};
        Cursor cursor = getContentResolver().query(AmbulanciaEntry.CONTENT_URI, projection, selection, selectionArgs,null );
        cursor.moveToFirst();
        int ambulanciaID = cursor.getInt(cursor.getColumnIndex(AmbulanciaEntry._ID));
        cursor.close();
        Log.v(TAG, "ambulanciaID" + ambulanciaID);
        return ambulanciaID;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
 //       LatLng local = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(ConstantesGeocoding.RESULT_DATA_KEY);
            mAddressArray = resultData.getStringArray(ConstantesGeocoding.RESULT_DATA_KEY_ARRAY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == ConstantesGeocoding.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permissao_necessaria, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(SolicitacaoActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(SolicitacaoActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getAddress();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permissao_necessaria, R.string.configuracoes,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}
