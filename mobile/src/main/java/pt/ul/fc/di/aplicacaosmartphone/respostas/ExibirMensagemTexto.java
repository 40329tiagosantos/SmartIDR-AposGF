package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ExibirMensagemTexto extends Service {

    public static boolean ocorreuIntrusaoExibMen;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String estado = intent.getStringExtra("estado");
        iniciaExibirMensagem(estado);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void iniciaExibirMensagem(String estado) {
        ocorreuIntrusaoExibMen = true;
        try {
            FileInputStream ficheiroMensagem = openFileInput("Mensagem" + estado);
            BufferedReader leitorFicheiroMensagem = new BufferedReader(new InputStreamReader(ficheiroMensagem));
            String mensagem = "";
            String linhaActual;
            while ((linhaActual = leitorFicheiroMensagem.readLine()) != null) {
                mensagem += linhaActual;
            }
            for (int i = 0; i < 3; i++)
                Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
            ficheiroMensagem.close();
            leitorFicheiroMensagem.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("ExibirMensagemTexto");
        editor.commit();
    }
}

