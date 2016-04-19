package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import pt.ul.fc.di.aplicacaosmartphone.comunicacao.Mensagem;

public class Notificacao extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String estado = intent.getStringExtra("estado");
        iniciarNotificacao(estado);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void iniciarNotificacao(String estado) {
        Mensagem mensagemPedido = new Mensagem("Intrusao", getApplication());
        mensagemPedido.enviaMensagem();
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("Notificacao");
        editor.commit();
    }
}

