package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Map;

public class DesativarAplicacoes extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String estado = intent.getStringExtra("estado");
        String nomeEvento = intent.getStringExtra("nomeEvento");
        String nomePacote =  intent.getStringExtra("nomePacote");
        desativaAplicacoes(estado,nomeEvento,nomePacote);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void desativaAplicacoes(String estado, String nomeEvento, String nomePacote) {
        SharedPreferences preferencias;
        preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE);

        Map<String, ?> conjuntoPreferencias = preferencias.getAll();
        Intent minimiza = new Intent(Intent.ACTION_MAIN);
        minimiza.addCategory(Intent.CATEGORY_HOME);
        minimiza.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        for (Map.Entry<String, ?> entry : conjuntoPreferencias.entrySet()) {
            if (!nomeEvento.equals("") && (entry.getKey().contains("preferenciaAplicacoes") && !nomeEvento.equals("")
                    && entry.getValue().toString().contains(nomeEvento))) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(minimiza);
                if (entry.getValue().toString().contains(nomePacote) && !nomeEvento.equals("")) {
                    startActivity(minimiza);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(minimiza);
                }
            }
        }
    }
}

