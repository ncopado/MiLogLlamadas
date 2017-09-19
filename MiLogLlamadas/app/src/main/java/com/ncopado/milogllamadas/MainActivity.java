package com.ncopado.milogllamadas;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final int CODIO_SOLICITUD_PERMISOS =1 ;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity=this;
    }

    public void mostrarLlamada(View view){
        if(checarStatusPermisos()){
            consultarCPLlamadas();
        }else{
            solicitarPermiso();
        }


    }

    public void solicitarPermiso(){

        boolean solicitarRPermiso= ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALL_LOG);
        boolean solicitarWPermiso=ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CALL_LOG);

        if(solicitarRPermiso && solicitarWPermiso)
        {
            Toast.makeText(MainActivity.this,"Ya tiene permisos",Toast.LENGTH_LONG).show();
        }
        else{
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG},CODIO_SOLICITUD_PERMISOS);
        }


    }

    public boolean checarStatusPermisos(){
        boolean RPermiso= ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)== PackageManager.PERMISSION_GRANTED;
        boolean WPermiso=ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)== PackageManager.PERMISSION_GRANTED;

        if(RPermiso && WPermiso)
            return true;
        else
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CODIO_SOLICITUD_PERMISOS:
                if(checarStatusPermisos()){
                    Toast.makeText(MainActivity.this,"Ya esta activo", Toast.LENGTH_LONG).show();
                    consultarCPLlamadas();
                }
                else{
                    Toast.makeText(MainActivity.this,"No  esta activo", Toast.LENGTH_LONG).show();
                }
        }

    }

    public void  consultarCPLlamadas(){

        TextView tvLlamadas=(TextView) findViewById(R.id.tvLlamadas);
        tvLlamadas.setText("");

        Uri uri= CallLog.Calls.CONTENT_URI;

        String[] campos={
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION
        };

        ContentResolver contentResolver= getContentResolver();

        Cursor cursor=contentResolver.query(uri,campos,null,null,CallLog.Calls.DATE +" DESC");

        while (cursor.moveToNext()){
            String numero       =cursor.getString(cursor.getColumnIndex(campos[0]));
            Long fecha          =cursor.getLong(cursor.getColumnIndex(campos[1]));
            int type            =cursor.getInt(cursor.getColumnIndex(campos[2]));
            String duracion     =cursor.getString(cursor.getColumnIndex(campos[3]));;
            String tipoLlamada="";

            switch (type){
                case CallLog.Calls.INCOMING_TYPE:
                    tipoLlamada=getResources().getString(R.string.entrada);
                    break;
                case  CallLog.Calls.MISSED_TYPE:
                    tipoLlamada=getResources().getString(R.string.perdida);
                    break;

                case  CallLog.Calls.OUTGOING_TYPE:
                    tipoLlamada=getResources().getString(R.string.salida);
                    break;

                default:
                    tipoLlamada=getResources().getString(R.string.tipo);

            }

            String detalle = getResources().getString(R.string.etiqueta_numero) + numero +"\n" +
                             getResources().getString(R.string.etiqueta_fecha)  + DateFormat.format("dd/MM/yy k:mm",fecha)+ "\n" +
                             getResources().getString(R.string.etiqueta_tipo) + tipoLlamada +"\n" +
                             getResources().getString(R.string.etiqueta_duracion) + duracion + " s.";


            tvLlamadas.append(detalle);

        }




    }
}
