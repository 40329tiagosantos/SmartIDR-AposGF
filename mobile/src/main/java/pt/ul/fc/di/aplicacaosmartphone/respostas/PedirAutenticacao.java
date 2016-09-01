package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import pt.ul.fc.di.aplicacaosmartphone.comunicacao.Mensagem;

public class PedirAutenticacao extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String estado = intent.getStringExtra("estado");
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.lockNow();
        if (estado.equals("ComLigacaoBT")) {
            Mensagem mensagemPedido = new Mensagem("PedidoAutenticacaoAtivado", getApplication());
            mensagemPedido.enviaMensagem();
        }
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
