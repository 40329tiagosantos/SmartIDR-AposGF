package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class IniciarFotografarIntruso extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String estado = intent.getStringExtra("estado");
        iniciaFotografarIntruso(estado);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void iniciaFotografarIntruso(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador"+estado, MODE_PRIVATE).edit();
        editor.remove("FotografarIntruso");
        editor.commit();
        Intent tiraFotografia = new Intent(getApplicationContext(), FotografarIntruso.class);
        tiraFotografia.putExtra("estado",estado);
        startService(tiraFotografia);
    }
}
