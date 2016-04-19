package pt.ul.fc.di.aplicacaosmartphone.detecao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pt.ul.fc.di.aplicacaosmartphone.interfaceaplicacao.MenuPrincipal;
import pt.ul.fc.di.aplicacaosmartphone.respostas.AlarmeSonoro;
import pt.ul.fc.di.aplicacaosmartphone.respostas.ExibirMensagemTexto;
import pt.ul.fc.di.aplicacaosmartphone.respostas.FotografarIntruso;
import pt.ul.fc.di.aplicacaosmartphone.respostas.RegistoAtividades;

public class DetetorLigacaoSmartwatch extends Service implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient clienteApi;
    public static String estado = "";
    public boolean iniciouResposta;
    public static boolean iniciou = false;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (!iniciou) {
            iniciou = true;
            Timer cronometro = new Timer();
            iniciarClienteApi();

            TimerTask tarefa = new TimerTask() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(clienteApi).await();
                    Message mensagem = new Message();

                    if (nodes.getNodes().isEmpty()) {
                        mensagem.arg1 = 1;
                        mensagens.sendMessage(mensagem);
                        if (!estado.equals("SemLigacao")) {
                            estado = "SemLigacao";
                            if (!iniciouResposta) {
                                iniciouResposta = true;
                                iniciaResposta();
                            }
                        } else
                            estado = "SemLigacao";
                    } else {
                        for (final Node node : nodes.getNodes()) {
                            if (node.isNearby()) {
                                apresentarNotificacao();
                                mensagem.arg1 = 2;
                                mensagens.sendMessage(mensagem);
                                if (!estado.equals("ComLigacaoBT")) {
                                    estado = "ComLigacaoBT";
                                    if (iniciouResposta) {
                                        iniciouResposta = false;
                                        paraResposta();
                                    }
                                } else
                                    estado = "ComLigacaoBT";
                            } else {
                                mensagem.arg1 = 3;
                                mensagens.sendMessage(mensagem);
                                estado = "ComLigacaoWF";
                            }
                        }
                    }
                    if (ReiniciaDetecao.reiniciou) {
                        ReiniciaDetecao.reiniciou = false;
                        if (!iniciouResposta)
                            iniciaResposta();
                    }
                }
            };
            cronometro.schedule(tarefa, 5000, 5000);
            return START_STICKY;
        }
        return START_STICKY;
    }

    public void apresentarNotificacao() {

        boolean notifica = false;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.iconprincipalnotificacao);
        builder.setTicker("Ocorreu uma Intrusão!");
        builder.setContentTitle("Ocorreu uma Intrusão!");
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.iconprincipal));
        builder.setDefaults(Notification.DEFAULT_LIGHTS);

        StringBuilder descricao = new StringBuilder();

        if (ExibirMensagemTexto.ocorreuIntrusaoExibMen) {
            descricao.append("A resposta Exibir Mensagem foi Ativada.\n");

            ExibirMensagemTexto.ocorreuIntrusaoExibMen = false;
            notifica = true;
        }

        if (FotografarIntruso.ocorreuIntrusaoFotogIntr) {
            descricao.append("A resposta Fotografar Intruso foi Ativada.\n");

            FotografarIntruso.ocorreuIntrusaoFotogIntr = false;
            notifica = true;
        }
        if (RegistoAtividades.ocorreuIntrusaoRegisAt) {
            descricao.append("A resposta Registar Atividades Intruso foi Ativada.\n");

            RegistoAtividades.ocorreuIntrusaoRegisAt = false;
            notifica = true;
        }
        if (AlarmeSonoro.ocorreuIntrusaoAlarSon) {

            descricao.append("A resposta Alarme Sonoro foi Ativada.\n");
            AlarmeSonoro.ocorreuIntrusaoAlarSon = false;
            notifica = true;
        }

        Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
        if (descricao.toString().contains("Fotografar") || descricao.toString().contains("Registar")) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentIntent(pendingIntent);
        }

        if (notifica) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(descricao.toString()));
            notificationManager.notify(0, builder.build());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void iniciaResposta() {
        String estado = "SemLigacao";
        SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE);
        Map<String, ?> conjuntoPreferencias = preferencias.getAll();
        if (conjuntoPreferencias != null) {
            for (Map.Entry<String, ?> entry : conjuntoPreferencias.entrySet()) {
                if (entry.getKey().contains("resposta")) {
                    Intent resposta = null;
                    try {
                        resposta = new Intent(getApplicationContext(), Class.forName(entry.getValue().toString()));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    resposta.putExtra("estado", estado);
                    String nomeResposta = entry.getValue().toString();
                    if (nomeResposta.contains("RegistoAtividades")) {
                        comecaRegistarAtividades(estado);
                    } else if (nomeResposta.contains("DesativarAplicacoes")) {
                        comecaDesativarAplicacoes(estado);
                    } else if (nomeResposta.contains("FotografarIntruso")) {
                        comecaFotografarIntruso(estado);
                    } else if (nomeResposta.contains("AlarmeSonoro")) {
                        comecaAlarmeSonoro(estado);
                    } else if (nomeResposta.contains("ConfiguracoesMensagemTexto")) {
                        comecaApresentarMensagem(estado);
                    } else {
                        resposta.putExtra("inicia", true);
                        startService(resposta);
                    }
                }
            }
        }
    }

    public void paraResposta() {
        String estado = "SemLigacao";
        SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE);
        Map<String, ?> conjuntoPreferencias = preferencias.getAll();
        if (conjuntoPreferencias != null) {
            for (Map.Entry<String, ?> entry : conjuntoPreferencias.entrySet()) {
                if (entry.getKey().contains("resposta")) {
                    Intent resposta;
                    try {
                        resposta = new Intent(getApplicationContext(), Class.forName(entry.getValue().toString()));
                        resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resposta.putExtra("estado", estado);
                        String nomeResposta = entry.getValue().toString();
                        if (nomeResposta.contains("RegistoAtividades")) {
                            paraRegistarAtividades(estado);
                        } else if (nomeResposta.contains("DesativarAplicacoes")) {
                            paraDesativarAplicacoes(estado);
                        } else if (nomeResposta.contains("FotografarIntruso")) {
                            paraFotografarIntruso(estado);
                        } else if (nomeResposta.contains("AlarmeSonoro")) {
                            paraAlarmeSonoro(estado);
                        } else if (nomeResposta.contains("ConfiguracoesMensagemTexto")) {
                            paraApresentarMensagem(estado);
                        } else if (nomeResposta.contains("Albuns")) {
                            startService(resposta);
                        } else {
                            stopService(resposta);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void comecaApresentarMensagem(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.putString("ExibirMensagemTexto", "ExibirMensagemTexto");
        editor.commit();
    }

    private void comecaDesativarAplicacoes(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.putString("DesativarAplicacoes", "DesativarAplicacoes");
        editor.commit();
    }

    private void comecaFotografarIntruso(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.putString("FotografarIntruso", "FotografarIntruso");
        editor.commit();
    }

    private void comecaRegistarAtividades(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.putString("RegistoAtividades", "RegistoAtividades");
        editor.commit();
    }

    private void comecaAlarmeSonoro(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.putString("AlarmeSonoro", "AlarmeSonoro");
        editor.commit();
    }

    private void paraApresentarMensagem(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("ExibirMensagemTexto");
        editor.commit();
    }

    private void paraAlarmeSonoro(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("AlarmeSonoro");
        editor.commit();
    }

    private void paraRegistarAtividades(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("RegistoAtividades");
        editor.commit();
    }

    private void paraDesativarAplicacoes(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("DesativarAplicacoes");
        editor.commit();
    }

    private void paraFotografarIntruso(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("FotografarIntruso");
        stopService(new Intent(getApplicationContext(), FotografarIntruso.class));
        editor.commit();
    }

    Handler mensagens = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 1)
                Toast.makeText(getApplicationContext(), "Sem Ligação!", Toast.LENGTH_SHORT).show();
            else if (msg.arg1 == 2)
                Toast.makeText(getApplicationContext(), "Com Ligação Bluetooth!", Toast.LENGTH_SHORT).show();
            else if (msg.arg1 == 3)
                Toast.makeText(getApplicationContext(), "Com Ligação Wi-Fi!", Toast.LENGTH_SHORT).show();
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
