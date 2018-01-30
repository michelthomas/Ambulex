package com.michelthomas.ambulex.view;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.michelthomas.ambulex.R;
import com.michelthomas.ambulex.adapters.SolicitacaoCursorAdapter;
import com.michelthomas.ambulex.model.Ambulancia;

import static com.michelthomas.ambulex.data.AmbulexContract.*;

public class InicioActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = InicioActivity.class.getSimpleName();

    private Button mSolicitarButton;
    private ListView mHistoricoListView;
    private SolicitacaoCursorAdapter mCursorAdapter;
    private SharedPreferences mPreferences;

    /** Identifier for the pet data loader */
    private static final int SOLICITACAO_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        mPreferences = getSharedPreferences(MainActivity.PREFS_NAME, 0);


        mHistoricoListView = findViewById(R.id.historico_list_view);


        View emptyView = findViewById(R.id.empty_view);
        mHistoricoListView.setEmptyView(emptyView);


        mCursorAdapter = new SolicitacaoCursorAdapter(this, null);
        mHistoricoListView.setAdapter(mCursorAdapter);

        mSolicitarButton = findViewById(R.id.inicio_solicitar_button);
        mSolicitarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserirDadosFicticiosDaAmbulancia();
                trocarActivity(SolicitacaoActivity.class);
            }
        });

        getLoaderManager().initLoader(SOLICITACAO_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu_item:
                SharedPreferences preferences = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                registrarLogout(preferences);
                trocarActivity(MainActivity.class);
                break;
            case R.id.insert_ambulancia_dummy_data:
                inserirDadosFicticiosDaAmbulancia();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inserirDadosFicticiosDaAmbulancia(){
        Ambulancia[] ambulancia = new Ambulancia[2];
        ambulancia[0] = new Ambulancia("123456", "Fiat Ducato", "1111111111", "9999999999");
        ambulancia[0].setDisponivel(true);
        ambulancia[1] = new Ambulancia("234567", "Mercedes Sprinter", "2222222222", "8888888888");
        ambulancia[1].setDisponivel(true);
        for (int i = 0; i < ambulancia.length; i++) {
            Uri uri = insertAmbulancia(ambulancia[i]);
            if (uri == null) {
                /*Toast.makeText(getApplicationContext(), "Não foi possível realizar o cadastro!",
                        Toast.LENGTH_SHORT).show();*/
            } else {
                Log.v(TAG, uri.toString());
                Toast.makeText(getApplicationContext(), "Cadastro de ambulancias realizado com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri insertAmbulancia(Ambulancia ambulancia) {


        ContentValues values = new ContentValues();

        values.put(AmbulanciaEntry.COLUMN_AMBULANCIA_PLACA, ambulancia.getPlaca());
        values.put(AmbulanciaEntry.COLUMN_AMBULANCIA_MODELO, ambulancia.getModelo());
        values.put(AmbulanciaEntry.COLUMN_AMBULANCIA_NUMERO_CRV, ambulancia.getNumCRV());
        values.put(AmbulanciaEntry.COLUMN_AMBULANCIA_NUMERO_CRVL, ambulancia.getNumCRVL());
        values.put(AmbulanciaEntry.COLUMN_AMBULANCIA_DISPONIVEL, ambulancia.isDisponivel());

        return getContentResolver().insert(AmbulanciaEntry.CONTENT_URI, values);
    }

    private void registrarLogout(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainActivity.PREFS_IS_LOGGED, false);
        editor.putString(MainActivity.PREFS_USUARIO_LOGADO, "");
        editor.apply();
    }

    private void trocarActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "CHEGOU NO ONCREATELOADER");
        String[] projection = {
                SolicitacaoEntry._ID,
                SolicitacaoEntry.COLUMN_SOLICITACAO_USUARIO_ID,
                SolicitacaoEntry.COLUMN_SOLICITACAO_DATA,
                SolicitacaoEntry.COLUMN_SOLICITACAO_MOTIVO,
                SolicitacaoEntry.COLUMN_SOLICITACAO_LOCAL_ID};

        String selection = SolicitacaoEntry.COLUMN_SOLICITACAO_USUARIO_ID + "=?";
        String[] selectionArgs = {mPreferences.getString(MainActivity.PREFS_USUARIO_LOGADO, "default")};


        return new CursorLoader(this,
                SolicitacaoEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
