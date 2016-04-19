package pt.ul.fc.di.aplicacaosmartwatch.comunicacao;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

public class Mensagem implements GoogleApiClient.ConnectionCallbacks {

    private static final long CONNECTION_TIME_OUT_MS = 100;
    private String mensagem;
    private Application aplicacao;

    public Mensagem(String mensagem, Application aplicacao) {
        this.mensagem = mensagem;
        this.aplicacao = aplicacao;
    }

    public void enviaMensagem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GoogleApiClient clienteApi = new GoogleApiClient.Builder(aplicacao.getApplicationContext())
                        .addApiIfAvailable(Wearable.API)
                        .build();
                clienteApi.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult listaNos = Wearable.NodeApi.getConnectedNodes(clienteApi).await();
                if (listaNos.getNodes().isEmpty())
                    Log.i("Sem Ligação! ", "Sem Ligação!");
                else {
                    for (Node no : listaNos.getNodes()) {
                        Wearable.MessageApi.sendMessage(clienteApi, no.getId(), mensagem, mensagem.getBytes()).await();
                    }
                }
                clienteApi.disconnect();
            }
        }).start();
    }


    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}