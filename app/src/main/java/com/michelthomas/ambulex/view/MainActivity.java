package com.michelthomas.ambulex.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.michelthomas.ambulex.R;
import com.michelthomas.ambulex.data.AmbulexContract.UsuarioEntry;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String PREFS_NAME = "MyPrefs";
    public static final String PREFS_IS_LOGGED = "isLogged";
    public static final String PREFS_USUARIO_LOGADO = "usuarioLogado";

    private Button mLoginButton;
    private TextView mCadastroButton;
    private EditText mEmailText;
    private EditText mSenhaText;
    private long mLastClickTime;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = getSharedPreferences(PREFS_NAME, 0);
        boolean isLogged = mPreferences.getBoolean(PREFS_IS_LOGGED, false);

        if (isLogged){
            Intent intent = new Intent(this, InicioActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);

        mLoginButton = findViewById(R.id.login_button);
        mCadastroButton = findViewById(R.id.login_cadastro_button);
        mEmailText = findViewById(R.id.login_email_text);
        mSenhaText = findViewById(R.id.login_senha_text_view);

        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (login()){
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putBoolean(PREFS_IS_LOGGED, true);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mCadastroButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent intent = new Intent(getApplicationContext(), CadastroActivity.class);
                startActivity(intent);
            }
        });




    }

    private boolean login(){
        String email = mEmailText.getText().toString().trim();
        String senha = mSenhaText.getText().toString().trim();
        boolean logou;

        String[] projection = {UsuarioEntry._ID, UsuarioEntry.COLUMN_USUARIO_EMAIL,
                UsuarioEntry.COLUMN_USUARIO_SENHA};

        String selection = UsuarioEntry.COLUMN_USUARIO_EMAIL + "=?";
        String[] selectionArgs = {email};
        Cursor cursor = getContentResolver().query(UsuarioEntry.CONTENT_URI, projection,
                selection, selectionArgs, null);

        if (cursor == null){
            Log.e(TAG, "Cursor null");
            logou = false;
        }else if (cursor.getCount() == 1 ){
            cursor.moveToFirst();
            if (cursor.getString(cursor.getColumnIndex(UsuarioEntry.COLUMN_USUARIO_SENHA)).equals(senha)){
                logou = true;
                Log.v(TAG, "LOGOU");
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(PREFS_USUARIO_LOGADO, cursor.getString(cursor.getColumnIndex(UsuarioEntry._ID)));
                editor.apply();
                cursor.close();
            }else{
                Toast.makeText(getBaseContext(), "Senha incorreta!", Toast.LENGTH_SHORT).show();
                logou = false;
            }
        }else{
            Toast.makeText(getBaseContext(), "Email não cadastrado!", Toast.LENGTH_SHORT).show();
            logou = false;
        }

        return logou;
    }
}
