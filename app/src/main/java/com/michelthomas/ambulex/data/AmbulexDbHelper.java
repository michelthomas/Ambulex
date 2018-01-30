package com.michelthomas.ambulex.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.michelthomas.ambulex.model.Local;

import static com.michelthomas.ambulex.data.AmbulexContract.*;

/**
 * Created by MichelT on 18/01/2018.
 */

public class AmbulexDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ambulex.db";

    public static final String LOG_TAG = "AmbulexDbHelper";

    public AmbulexDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_USUARIO_TABLE = "CREATE TABLE " + UsuarioEntry.TABLE_NAME + " ( "
                                            + UsuarioEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
                                            + UsuarioEntry.COLUMN_USUARIO_CPF + " VARCHAR [15] NOT NULL UNIQUE, "
                                            + UsuarioEntry.COLUMN_USUARIO_CARTAO_SUS + " VARCHAR [20] NOT NULL UNIQUE, "
                                            + UsuarioEntry.COLUMN_USUARIO_NOME + " VARCHAR [40] NOT NULL,"
                                            + UsuarioEntry.COLUMN_USUARIO_EMAIL + " VARCHAR [30] NOT NULL UNIQUE, "
                                            + UsuarioEntry.COLUMN_USUARIO_SENHA + " VARCHAR [15] NOT NULL );";


        String SQL_CREATE_AMBULANCIA_TABLE = "CREATE TABLE " + AmbulanciaEntry.TABLE_NAME + " ( "
                                            + AmbulanciaEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
                                            + AmbulanciaEntry.COLUMN_AMBULANCIA_PLACA + " VARCHAR [10] NOT NULL UNIQUE, "
                                            + AmbulanciaEntry.COLUMN_AMBULANCIA_MODELO + " VARCHAR [20] NOT NULL, "
                                            + AmbulanciaEntry.COLUMN_AMBULANCIA_NUMERO_CRV + " INTEGER NOT NULL UNIQUE, "
                                            + AmbulanciaEntry.COLUMN_AMBULANCIA_NUMERO_CRVL + " INTEGER NOT NULL UNIQUE, "
                                         //   + AmbulanciaEntry.COLUMN_AMBULANCIA_POSICAO_ATUAL + " VARCHAR [10] NOT NULL, "
                                            + AmbulanciaEntry.COLUMN_AMBULANCIA_DISPONIVEL + " BIT NOT NULL );";


       String SQL_CREATE_LOCAL_TABLE = "CREATE TABLE " + LocalEntry.TABLE_NAME + " ( "
                                           + LocalEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
                                           + LocalEntry.COLUMN_LOCAL_LATITUDE + " VARCHAR [15], "
                                            + LocalEntry.COLUMN_LOCAL_LONGITUDE + " VARCHAR [15], "
                                           + LocalEntry.COLUMN_LOCAL_RUA + " VARCHAR [20] NOT NULL, "
                                           + LocalEntry.COLUMN_LOCAL_BAIRRO + " VARCHAR [20] NOT NULL, "
                                           + LocalEntry.COLUMN_LOCAL_CIDADE + " VARCHAR [20] NOT NULL );";


       String SQL_CREATE_SOLICITACAO_TABLE = "CREATE TABLE " + SolicitacaoEntry.TABLE_NAME + " ( "
                                            + SolicitacaoEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
                                            + SolicitacaoEntry.COLUMN_SOLICITACAO_USUARIO_ID + " INTEGER NOT NULL, "
                                            + SolicitacaoEntry.COLUMN_SOLICITACAO_AMBULANCIA_ID + " INTEGER, "
                                            + SolicitacaoEntry.COLUMN_SOLICITACAO_LOCAL_ID + " INTEGER NOT NULL, "
                                            + SolicitacaoEntry.COLUMN_SOLICITACAO_DATA + " DATETIME NOT NULL, "
                                            + SolicitacaoEntry.COLUMN_SOLICITACAO_MOTIVO + " VARCHAR [50], "
                                            + " FOREIGN KEY (" + SolicitacaoEntry.COLUMN_SOLICITACAO_USUARIO_ID
                                                    + ") REFERENCES " + UsuarioEntry.TABLE_NAME + " (" + UsuarioEntry._ID + "), "
                                            + " FOREIGN KEY (" + SolicitacaoEntry.COLUMN_SOLICITACAO_AMBULANCIA_ID
                                                    + ") REFERENCES " + AmbulanciaEntry.TABLE_NAME + " (" + AmbulanciaEntry._ID + "), "
                                            + " FOREIGN KEY (" + SolicitacaoEntry.COLUMN_SOLICITACAO_LOCAL_ID
                                                     + ") REFERENCES " + LocalEntry.TABLE_NAME + " (" + LocalEntry._ID + "));";

       sqLiteDatabase.execSQL(SQL_CREATE_USUARIO_TABLE);
       Log.v(LOG_TAG, SQL_CREATE_USUARIO_TABLE);

       sqLiteDatabase.execSQL(SQL_CREATE_AMBULANCIA_TABLE);
       Log.v(LOG_TAG, SQL_CREATE_AMBULANCIA_TABLE);

       sqLiteDatabase.execSQL(SQL_CREATE_LOCAL_TABLE);
       Log.v(LOG_TAG, SQL_CREATE_LOCAL_TABLE);

       sqLiteDatabase.execSQL(SQL_CREATE_SOLICITACAO_TABLE);
       Log.v(LOG_TAG, SQL_CREATE_SOLICITACAO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
