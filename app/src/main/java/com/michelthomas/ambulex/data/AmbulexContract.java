package com.michelthomas.ambulex.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MichelT on 18/01/2018.
 */


public final class AmbulexContract {

    public static final String CONTENT_AUTHORITY = "com.michelthomas.ambulex";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_USUARIO = "usuario";
    public static final String PATH_AMBULANCIA = "ambulancia";
    public static final String PATH_LOCAL= "local";
    public static final String PATH_SOLICITACAO = "solicitacao";

    public static final class UsuarioEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USUARIO);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USUARIO;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USUARIO;


        /*CREATE TABLE `usuario` (
                `id_usuario`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
                `cpf`	VARCHAR [15] NOT NULL UNIQUE,
                `cartao_sus`	VARCHAR [20] NOT NULL UNIQUE,
                `nome`	VARCHAR [40] NOT NULL,
        `email`	VARCHAR [30] NOT NULL UNIQUE,
                `senha`	VARCHAR [15] NOT NULL
        );*/

        public static final String TABLE_NAME = "usuario";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_USUARIO_CPF  = "cpf";
        public static final String COLUMN_USUARIO_CARTAO_SUS  = "cartao_sus";
        public static final String COLUMN_USUARIO_NOME  = "nome";
        public static final String COLUMN_USUARIO_EMAIL  = "email";
        public static final String COLUMN_USUARIO_SENHA  = "senha";

    }

    public static final class AmbulanciaEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_AMBULANCIA);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AMBULANCIA;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AMBULANCIA;

        /*CREATE TABLE `ambulancia` (
                `id_ambulancia`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
                `placa`	VARCHAR [10] NOT NULL UNIQUE,
                `modelo`	VARCHAR [20] NOT NULL,
        `numcrv`	INTEGER NOT NULL UNIQUE,
        `numcrlv`	INTEGER NOT NULL UNIQUE,
        `posicao_atual`	VARCHAR [10] NOT NULL,
        `disponivel`	BIT NOT NULL
        );*/

        public static final String TABLE_NAME = "ambulancia";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_AMBULANCIA_PLACA = "placa";
        public static final String COLUMN_AMBULANCIA_MODELO = "modelo";
        public static final String COLUMN_AMBULANCIA_NUMERO_CRV = "numero_crv";
        public static final String COLUMN_AMBULANCIA_NUMERO_CRVL = "numero_crlv";
       // public static final String COLUMN_AMBULANCIA_POSICAO_ATUAL = "posicao_atual";
        public static final String COLUMN_AMBULANCIA_DISPONIVEL = "disponivel";
    }

    public static final class LocalEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LOCAL);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCAL;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCAL;

       /* CREATE TABLE `local` (
                `id_local`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
                `coordenadas`	VARCHAR [10],
                `rua`	VARCHAR [20] NOT NULL,
        `bairro`	VARCHAR [20] NOT NULL,
        `cidade`	VARCHAR [20] NOT NULL,
        `referencia`	VARCHAR [40]
                );*/

       public static final String TABLE_NAME = "local";
       public static final String _ID = BaseColumns._ID;
       public static final String COLUMN_LOCAL_LATITUDE = "latitude";
       public static final String COLUMN_LOCAL_LONGITUDE = "longitude";
       public static final String COLUMN_LOCAL_RUA = "rua";
       public static final String COLUMN_LOCAL_BAIRRO = "bairro";
       public static final String COLUMN_LOCAL_CIDADE = "cidade";
      // public static final String COLUMN_LOCAL_REFERENCIA = "referencia";

    }

    public static final class SolicitacaoEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SOLICITACAO);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SOLICITACAO;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SOLICITACAO;

        /*

        CREATE TABLE `solicitacao` (
        `data_solicitacao`	DATETIME NOT NULL,
        `id_usuario`	INT,
        `id_ambulancia`	INT,
        `id_local`	INT,
        `motivo`	VARCHAR [50],
        FOREIGN KEY(`id_usuario`) REFERENCES `usuario`(`id_usuario`),
        FOREIGN KEY(`id_ambulancia`) REFERENCES `ambulancia`(`id_ambulancia`),
        FOREIGN KEY(`id_local`) REFERENCES `local`(`id_local`),
        PRIMARY KEY(`data_solicitacao`)
        );*/

        public static final String TABLE_NAME = "solicitacao";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SOLICITACAO_DATA = "data_solicitacao";
        public static final String COLUMN_SOLICITACAO_USUARIO_ID = "id_usuario";
        public static final String COLUMN_SOLICITACAO_AMBULANCIA_ID = "id_ambulancia";
        public static final String COLUMN_SOLICITACAO_LOCAL_ID = "id_local";
        public static final String COLUMN_SOLICITACAO_MOTIVO = "motivo";
    }

}
