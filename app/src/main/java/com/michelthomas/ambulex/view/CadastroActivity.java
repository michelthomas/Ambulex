package com.michelthomas.ambulex.view;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.michelthomas.ambulex.R;

import static com.michelthomas.ambulex.data.AmbulexContract.UsuarioEntry;

public class CadastroActivity extends AppCompatActivity {

    public static final String LOG = CadastroActivity.class.getSimpleName();

    private EditText mNomeText;
    private EditText mCpfText;
    private EditText mCartaoSusText;
    private EditText mEmailText;
    private EditText mSenhaText;

    private Button mCadastrarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        final SharedPreferences preferences = getSharedPreferences(MainActivity.PREFS_NAME, 0);

        mNomeText = findViewById(R.id.cadastro_nome_text);
        mCpfText = findViewById(R.id.cadastro_cpf_text);
        mCartaoSusText = findViewById(R.id.cadastro_cartao_sus_text);
        mEmailText = findViewById(R.id.cadastro_email_text);
        mSenhaText = findViewById(R.id.cadastro_senha_text);

        mCadastrarButton = findViewById(R.id.cadastrar_button);

        mCadastrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarDados()){
                    Uri uri = inserir();
                    if (uri == null){
                        Toast.makeText(getApplicationContext(), "Não foi possível realizar o cadastro!",
                                Toast.LENGTH_SHORT).show();
                    } else{
                        Log.v(LOG, uri.toString());
                        Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso!",
                                Toast.LENGTH_SHORT).show();

                        registrarLogin(preferences, uri);
                        trocarActivity();
                    }
                }
            }
        });

    }

    private boolean validarDados(){
        String nome = mNomeText.getText().toString().trim();
        String cpf = mCpfText.getText().toString().trim();
        String cSus = mCartaoSusText.getText().toString().trim();
        String email = mEmailText.getText().toString().trim();
        String senha = mSenhaText.getText().toString().trim();


        if (nome.isEmpty() || cpf.isEmpty() || cSus.isEmpty() || email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this,"Entre com todos os dados para continuar!",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }

    }

    private Uri inserir(){
        ContentValues values = new ContentValues();

        String nome = mNomeText.getText().toString().trim();
        String cpf = mCpfText.getText().toString().trim();
        String cSus = mCartaoSusText.getText().toString().trim();
        String email = mEmailText.getText().toString().trim();
        String senha = mSenhaText.getText().toString().trim();


        values.put(UsuarioEntry.COLUMN_USUARIO_NOME, nome);
        values.put(UsuarioEntry.COLUMN_USUARIO_CPF, cpf);
        values.put(UsuarioEntry.COLUMN_USUARIO_CARTAO_SUS, cSus);
        values.put(UsuarioEntry.COLUMN_USUARIO_EMAIL, email);
        values.put(UsuarioEntry.COLUMN_USUARIO_SENHA, senha);

        return getContentResolver().insert(UsuarioEntry.CONTENT_URI, values);
    }

    private void registrarLogin(SharedPreferences preferences, Uri uri){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainActivity.PREFS_IS_LOGGED, true);
        editor.putString(MainActivity.PREFS_USUARIO_LOGADO, uri.getLastPathSegment());
        editor.apply();
    }

    private void trocarActivity() {
        Intent intent = new Intent(this, InicioActivity.class);
        startActivity(intent);
        finish();
    }
}
