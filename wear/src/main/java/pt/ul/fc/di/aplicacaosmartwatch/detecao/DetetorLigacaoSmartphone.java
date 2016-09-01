package pt.ul.fc.di.aplicacaosmartwatch.detecao;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Timer;
import java.util.TimerTask;

import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.ConstroiFragmentos;
import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.ListaAtividades;

public class DetetorLigacaoSmartphone extends Service implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient clienteApi;
    private static boolean iniciou = false;
    public static String estado = "";

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (!iniciou) {
            iniciou = true;
            iniciarClienteApi();
            Timer cronometro = new Timer();
            final TimerTask tarefa = new TimerTask() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(clienteApi).await();
                    Message mensagem = new Message();
                    if (nodes.getNodes().isEmpty()) {
                        mensagem.arg1 = 1;
                        if (!estado.equals("SemLigacao") && ListaAtividades.iniciouAplicacao && !ListaAtividades.iniciouAtividade) {
                            mensagens.sendMessage(mensagem);
                            estado = "SemLigacao";
                            Intent a = new Intent(getApplicationContext(), ConstroiFragmentos.class);
                            a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(a);
                        }
                        else if (!estado.equals("SemLigacao") && ListaAtividades.iniciouAplicacao && ListaAtividades.iniciouAtividade) {
                            mensagens.sendMessage(mensagem);
                            estado = "SemLigacao";
                            Intent listaAtividades = new Intent(getApplicationContext(), ConstroiFragmentos.class);
                            listaAtividades.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            listaAtividades.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            listaAtividades.putExtra("MudarFundo", "MudarFundo");
                            startActivity(listaAtividades);
                        }
                        else if(!estado.equals("SemLigacao")&&!ListaAtividades.iniciouAplicacao){
                            Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(2000);
                        }
                        estado = "SemLigacao";
                    } else {
                        for (Node node : nodes.getNodes()) {
                            if (!DetetorPartilhaControlada.iniciou) {
                                Intent servicoPartilha = new Intent(getApplicationContext(), DetetorPartilhaControlada.class);
                                startService(servicoPartilha);
                            }
                            if (node.isNearby()) {
                                mensagem.arg1 = 2;
                                if (!estado.equals("ComLigacaoBT") && ListaAtividades.iniciouAplicacao && !ListaAtividades.iniciouAtividade) {
                                    mensagens.sendMessage(mensagem);
                                    estado = "ComLigacaoBT";
                                    Intent a = new Intent(getApplicationContext(), ConstroiFragmentos.class);
                                    a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(a);
                                } else if (!estado.equals("ComLigacaoBT") && ListaAtividades.iniciouAplicacao && ListaAtividades.iniciouAtividade) {
                                    mensagens.sendMessage(mensagem);
                                    estado = "ComLigacaoBT";
                                    Intent listaAtividades = new Intent(getApplicationContext(), ConstroiFragmentos.class);
                                    listaAtividades.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    listaAtividades.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    listaAtividades.putExtra("MudarFundo", "MudarFundo");
                                    startActivity(listaAtividades);
                                }
                                estado = "ComLigacaoBT";
                            } else {
                                mensagem.arg1 = 3;
                                if (!estado.equals("ComLigacaoWF") && ListaAtividades.iniciouAplicacao && !ListaAtividades.iniciouAtividade) {
                                    mensagens.sendMessage(mensagem);
                                    estado = "ComLigacaoWF";
                                    Intent a = new Intent(getApplicationContext(), ConstroiFragmentos.class);
                                    a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(a);
                                } else if (!estado.equals("ComLigacaoWF") && ListaAtividades.iniciouAplicacao && ListaAtividades.iniciouAtividade) {
                                    mensagens.sendMessage(mensagem);
                                    estado = "ComLigacaoWF";
                                    Intent listaRespostas = new Intent(getApplicationContext(), ConstroiFragmentos.class);
                                    listaRespostas.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    listaRespostas.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    listaRespostas.putExtra("MudarFundo", "MudarFundo");
                                    startActivity(listaRespostas);
                                }
                                estado = "ComLigacaoWF";
                            }
                        }
                    }
                }
            };
            cronometro.schedule(tarefa, 0, 5000);
            return START_NOT_STICKY;
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler mensagens = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
                   Toast.makeText(getApplicationContext(), "Without Connection!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "Bluetooth Connection!", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "Wi-Fi Connection!", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void iniciarClienteApi() {
        clienteApi = new GoogleApiClient.Builder(this).addApiIfAvailable(Wearable.API).build();
        clienteApi.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}