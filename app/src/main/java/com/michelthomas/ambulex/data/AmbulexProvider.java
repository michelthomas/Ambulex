package com.michelthomas.ambulex.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.michelthomas.ambulex.data.AmbulexContract.UsuarioEntry;
import com.michelthomas.ambulex.data.AmbulexContract.AmbulanciaEntry;
import com.michelthomas.ambulex.data.AmbulexContract.LocalEntry;
import com.michelthomas.ambulex.data.AmbulexContract.SolicitacaoEntry;

/**
 * Created by MichelT on 18/01/2018.
 */

public class AmbulexProvider extends ContentProvider {

    public static final String LOG_TAG = AmbulexProvider.class.getSimpleName();

    private AmbulexDbHelper mDbHelper;

    public static final int USUARIOS = 100;
    public static final int USUARIO_ID = 101;
    public static final int AMBULANCIAS = 200;
    public static final int AMBULANCIA_ID = 201;
    public static final int LOCAIS = 300;
    public static final int LOCAL_ID = 301;
    public static final int SOLICITACOES = 400;
    public static final int SOLICITACAO_ID = 401;

    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AmbulexContract.CONTENT_AUTHORITY, AmbulexContract.PATH_USUARIO, USUARIOS);
        sUriMatcher.addURI(AmbulexContract.CONTENT_AUTHORITY, AmbulexContract.PATH_USUARIO + "/#", USUARIO_ID);

        sUriMatcher.addURI(AmbulexContract.CONTENT_AUTHORITY, AmbulexContract.PATH_AMBULANCIA, AMBULANCIAS);
        sUriMatcher.addURI(AmbulexContract.CONTENT_AUTHORITY, AmbulexContract.PATH_AMBULANCIA + "/#", AMBULANCIA_ID);

        sUriMatcher.addURI(AmbulexContract.CONTENT_AUTHORITY, AmbulexContract.PATH_LOCAL, LOCAIS);
        sUriMatcher.addURI(AmbulexContract.CONTENT_AUTHORITY, AmbulexContract.PATH_LOCAL + "/#", LOCAL_ID);

        sUriMatcher.addURI(AmbulexContract.CONTENT_AUTHORITY, AmbulexContract.PATH_SOLICITACAO, SOLICITACOES);
        sUriMatcher.addURI(AmbulexContract.CONTENT_AUTHORITY, AmbulexContract.PATH_SOLICITACAO + "/#", SOLICITACAO_ID);

    }

    @Override
    public boolean onCreate() {
        mDbHelper = new AmbulexDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor cursor;

        switch (match){
            case USUARIOS:
                cursor = database.query(UsuarioEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case USUARIO_ID:
                selection = UsuarioEntry._ID + "=?";
                selectionArgs = new  String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(UsuarioEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case AMBULANCIAS:
                cursor = database.query(AmbulanciaEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case AMBULANCIA_ID:
                selection = AmbulanciaEntry._ID + "=?";
                selectionArgs = new  String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(AmbulanciaEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LOCAIS:
                cursor = database.query(LocalEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LOCAL_ID:
                selection = LocalEntry._ID + "=?";
                selectionArgs = new  String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(LocalEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SOLICITACOES:
                Log.v(LOG_TAG, "CHEGOU NO PROVIDER ");
                cursor = database.query(SolicitacaoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SOLICITACAO_ID:
                selection = SolicitacaoEntry._ID + "=?";
                selectionArgs = new  String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(SolicitacaoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case USUARIOS:
                return UsuarioEntry.CONTENT_LIST_TYPE;
            case USUARIO_ID:
                return UsuarioEntry.CONTENT_ITEM_TYPE;
            case AMBULANCIAS:
                return AmbulanciaEntry.CONTENT_LIST_TYPE;
            case AMBULANCIA_ID:
                return AmbulanciaEntry.CONTENT_ITEM_TYPE;
            case LOCAIS:
                return LocalEntry.CONTENT_LIST_TYPE;
            case LOCAL_ID:
                return LocalEntry.CONTENT_ITEM_TYPE;
            case SOLICITACOES:
                return SolicitacaoEntry.CONTENT_LIST_TYPE;
            case SOLICITACAO_ID:
                return SolicitacaoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);

        switch (match){
            case USUARIOS:
                return insertUsuario(uri, contentValues);
            case AMBULANCIAS:
                return insertAmbulancia(uri, contentValues);
            case LOCAIS:
                return insertLocal(uri, contentValues);
            case SOLICITACOES:
                return insertSolicitacao(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertSolicitacao(Uri uri, ContentValues contentValues) {

        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        long id = sqLiteDatabase.insert(SolicitacaoEntry.TABLE_NAME, null, contentValues);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertLocal(Uri uri, ContentValues contentValues) {

        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        long id = sqLiteDatabase.insert(LocalEntry.TABLE_NAME, null, contentValues);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertAmbulancia(Uri uri, ContentValues contentValues) {

        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        long id = sqLiteDatabase.insert(AmbulanciaEntry.TABLE_NAME, null, contentValues);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertUsuario(Uri uri, ContentValues contentValues) {

        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        long id = sqLiteDatabase.insert(UsuarioEntry.TABLE_NAME, null, contentValues);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        switch (match){
            case USUARIOS:
                return sqLiteDatabase.delete(UsuarioEntry.TABLE_NAME, selection, selectionArgs);
            case USUARIO_ID:
                selection = UsuarioEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return sqLiteDatabase.delete(UsuarioEntry.TABLE_NAME, selection, selectionArgs);

            case AMBULANCIAS:
                return sqLiteDatabase.delete(AmbulanciaEntry.TABLE_NAME, selection, selectionArgs);
            case AMBULANCIA_ID:
                selection = AmbulanciaEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return sqLiteDatabase.delete(AmbulanciaEntry.TABLE_NAME, selection, selectionArgs);

            case LOCAIS:
                return sqLiteDatabase.delete(LocalEntry.TABLE_NAME, selection, selectionArgs);
            case LOCAL_ID:
                selection = LocalEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return sqLiteDatabase.delete(LocalEntry.TABLE_NAME, selection, selectionArgs);

            case SOLICITACOES:
                return sqLiteDatabase.delete(SolicitacaoEntry.TABLE_NAME, selection, selectionArgs);
            case SOLICITACAO_ID:
                selection = SolicitacaoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return sqLiteDatabase.delete(SolicitacaoEntry.TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case USUARIOS:
                return updateUsuario(uri, contentValues, selection, selectionArgs);
            case USUARIO_ID:
                selection = UsuarioEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateUsuario(uri, contentValues, selection, selectionArgs);

            case AMBULANCIAS:
                return updateAmbulancia(uri, contentValues, selection, selectionArgs);
            case AMBULANCIA_ID:
                selection = AmbulanciaEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateAmbulancia(uri, contentValues, selection, selectionArgs);

            case LOCAIS:
                return updateLocal(uri, contentValues, selection, selectionArgs);
            case LOCAL_ID:
                selection = LocalEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateLocal(uri, contentValues, selection, selectionArgs);

            case SOLICITACOES:
                return updateSolicitacao(uri, contentValues, selection, selectionArgs);
            case SOLICITACAO_ID:
                selection = SolicitacaoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateSolicitacao(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    private int updateUsuario(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    private int updateAmbulancia(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    private int updateLocal(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    private int updateSolicitacao(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }
}