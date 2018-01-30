package com.michelthomas.ambulex.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.michelthomas.ambulex.R;
import com.michelthomas.ambulex.data.AmbulexContract.LocalEntry;
import com.michelthomas.ambulex.data.AmbulexContract.SolicitacaoEntry;
import com.michelthomas.ambulex.data.AmbulexContract.UsuarioEntry;

/**
 * Created by MichelT on 29/01/2018.
 */

public class SolicitacaoCursorAdapter extends CursorAdapter {
    public static final String TAG = SolicitacaoCursorAdapter.class.getSimpleName();

    public SolicitacaoCursorAdapter(Context context, Cursor c) {

        super(context, c, 0);
        Log.v(TAG, "CHEGOU NO SolicitacaoCursoAdapter");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dataTextView = view.findViewById(R.id.list_data);
        TextView usuarioTextView = view.findViewById(R.id.list_usuario);
        TextView motivoTextView = view.findViewById(R.id.list_motivo);
        TextView localTextView = view.findViewById(R.id.list_local);

        int dataColumnIndex = cursor.getColumnIndex(SolicitacaoEntry.COLUMN_SOLICITACAO_DATA);
        int usuarioColumnIndex = cursor.getColumnIndex(SolicitacaoEntry.COLUMN_SOLICITACAO_USUARIO_ID);
        int motivoColumnIndex = cursor.getColumnIndex(SolicitacaoEntry.COLUMN_SOLICITACAO_MOTIVO);
        int localColumnIndex = cursor.getColumnIndex(SolicitacaoEntry.COLUMN_SOLICITACAO_LOCAL_ID);

        int usuarioID = cursor.getInt(usuarioColumnIndex);
        int localID = cursor.getInt(localColumnIndex);

        String[] projectionUsuario = {UsuarioEntry.COLUMN_USUARIO_NOME};
        String selectionUsuario = UsuarioEntry._ID + "=?";
        String[] selectionArgsUsuario = {String.valueOf(usuarioID)};
        Cursor cursorUsuario = context.getContentResolver().query(UsuarioEntry.CONTENT_URI, projectionUsuario,
                selectionUsuario, selectionArgsUsuario, null);
        cursorUsuario.moveToFirst();
        String usuario = cursorUsuario.getString(cursorUsuario.getColumnIndex(UsuarioEntry.COLUMN_USUARIO_NOME));

        String[] projection = {LocalEntry.COLUMN_LOCAL_RUA, LocalEntry.COLUMN_LOCAL_BAIRRO, LocalEntry.COLUMN_LOCAL_CIDADE};
        String selection = LocalEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(localID)};
        Cursor cursorLocal = context.getContentResolver().query(LocalEntry.CONTENT_URI, projection,
                selection, selectionArgs, null);

        int localRuaColumnIndex = cursorLocal.getColumnIndex(LocalEntry.COLUMN_LOCAL_RUA);
        int localBairroColumnIndex = cursorLocal.getColumnIndex(LocalEntry.COLUMN_LOCAL_BAIRRO);
        int localCidadeColumnIndex = cursorLocal.getColumnIndex(LocalEntry.COLUMN_LOCAL_CIDADE);

        cursorLocal.moveToFirst();
        String local = cursorLocal.getString(localRuaColumnIndex) + ", " + cursorLocal.getString(localBairroColumnIndex) + ", "
                + cursorLocal.getString(localCidadeColumnIndex) + ".";

        String data = cursor.getString(dataColumnIndex);
        String motivo = cursor.getString(motivoColumnIndex);


        dataTextView.setText(data);
        usuarioTextView.setText(usuario);
        motivoTextView.setText(motivo);
        localTextView.setText(local);
    }
}
