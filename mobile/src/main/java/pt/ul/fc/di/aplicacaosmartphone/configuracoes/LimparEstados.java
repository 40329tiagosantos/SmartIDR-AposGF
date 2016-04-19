package pt.ul.fc.di.aplicacaosmartphone.configuracoes;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;

public class LimparEstados extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> estados = new ArrayList<>();
        estados.add("ComLigacaoBT");
        estados.add("SemLigacao");
        estados.add("Partilha");
        for (int i = 0; i < estados.size(); i++) {
            String estado = estados.get(i);
            SharedPreferences.Editor posicoesEditor = getApplicationContext().getSharedPreferences("posicoes" + estado, MODE_PRIVATE).edit();
            posicoesEditor.clear();
            posicoesEditor.apply();
            getApplicationContext().deleteFile("PIN" + estado);
            getApplicationContext().deleteFile("Mensagem" + estado);
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
        }

        Toast.makeText(getApplicationContext(), "✓ Limpeza de Preferências Efetuada com Sucesso!", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
