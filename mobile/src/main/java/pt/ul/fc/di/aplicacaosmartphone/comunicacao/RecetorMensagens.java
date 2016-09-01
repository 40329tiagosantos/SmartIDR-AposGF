package pt.ul.fc.di.aplicacaosmartphone.comunicacao;

import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Map;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ListaAtividadesSmartIDR;
import pt.ul.fc.di.aplicacaosmartphone.relatorio.MenuPrincipal;
import pt.ul.fc.di.aplicacaosmartphone.respostas.Bloquear;
import pt.ul.fc.di.aplicacaosmartphone.respostas.FotografarIntruso;

public class RecetorMensagens extends WearableListenerService {

    public static boolean modoPartilha;
    private static boolean ativado;
    public static boolean comecouRegistoAtividades;
    private static String nome;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String mensagem = messageEvent.getPath();

        Intent resposta = null;
        Intent listaSmartIDR = new Intent(getApplicationContext(), MenuPrincipal.class);
        listaSmartIDR.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        listaSmartIDR.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (!mensagem.equals("AtivarModoPartilha")) {
            Mensagem mensagemResposta;
            String pacote = "pt.ul.fc.di.aplicacaosmartphone.respostas";

            if (mensagem.equals("ParaRegistoAtividades") && comecouRegistoAtividades) {
                paraRegistarAtividades("ComLigacaoBT");
                escreveFicheiroAtividadesSmartIDR("Remote Action: Stop Monitoring");
                comecouRegistoAtividades = false;
            } else if (mensagem.equals("IniciaRegistoAtividades") && !comecouRegistoAtividades) {
                comecaRegistarAtividades("ComLigacaoBT");
                escreveFicheiroAtividadesSmartIDR("Remote Action: Start Monitoring");
                comecouRegistoAtividades = true;
            } else if (mensagem.equals("Bloquear")) {
                try {
                    resposta = new Intent(getApplicationContext(), Class.forName(pacote + '.' + mensagem));
                    resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    resposta.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    resposta.putExtra("estado", "ComLigacaoBT");
                    startService(resposta);
                    if (Bloquear.bloqueado) {
                        escreveFicheiroAtividadesSmartIDR("Remote Action: Unbrick");
                    } else
                        escreveFicheiroAtividadesSmartIDR("Remote Action: Brick");

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (mensagem.equals("DesativarAplicacao")) {
                Intent minimiza = new Intent(Intent.ACTION_MAIN);
                minimiza.addCategory(Intent.CATEGORY_HOME);
                minimiza.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(minimiza);
                mensagemResposta = new Mensagem("DesativarAplicacao", getApplication());
                mensagemResposta.enviaMensagem();
                escreveFicheiroAtividadesSmartIDR("Remote Action: Home");
            } else if (mensagem.equals("PedirAutenticacao")) {
                try {
                    resposta = new Intent(getApplicationContext(), Class.forName(pacote + '.' + mensagem));
                    resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    resposta.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    resposta.putExtra("estado", "ComLigacaoBT");
                    startService(resposta);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                escreveFicheiroAtividadesSmartIDR("Remote Action: Lock");

            } else if (mensagem.equals("AbrirConfiguracoes")) {
                mensagemResposta = new Mensagem("AbrirConfiguracoes", getApplication());
                mensagemResposta.enviaMensagem();
                escreveFicheiroAtividadesSmartIDR("Remote Action: Setup Sharing Mode");
                resposta = new Intent(getApplicationContext(), MenuPrincipal.class);
                resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resposta.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                resposta.putExtra("estado", "Partilha");
                startActivity(resposta);
               }
            if (ListaAtividadesSmartIDR.iniciouListaSmartIDR&&!mensagem.equals("DesativarAplicacao"))
                startActivity(listaSmartIDR);
        } else {
            String estado = "Partilha";
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE);
            Map<String, ?> conjuntoPreferencias = prefs.getAll();
            if (!conjuntoPreferencias.isEmpty()) {
                if (!ativado) {
                    for (Map.Entry<String, ?> entry : conjuntoPreferencias.entrySet()) {
                        if (entry.getKey().contains("resposta")) {

                            modoPartilha = true;
                            try {
                                resposta = new Intent(getApplicationContext(), Class.forName(entry.getValue().toString()));
                                resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                resposta.putExtra("estado", estado);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            String nomeResposta = entry.getValue().toString();

                            if (nomeResposta.contains("FotografarIntruso")) {
                                comecaFotografarIntruso(estado);
                            } else if (nomeResposta.contains("DesativarAplicacoes")) {
                                comecaDesativarAplicacoes(estado);
                            } else if (nomeResposta.contains("MensagemTexto")) {
                                comecaApresentarMensagem(estado);
                            } else if (nomeResposta.contains("RegistoAtividades")) {
                                comecaRegistarAtividades(estado);
                            }  else if (nomeResposta.contains("AlarmeSonoro")) {
                                comecaAlarmeSonoro(estado);
                            } else {
                                startService(resposta);
                                modoPartilha = false;
                            }
                        }
                    }
                    ativado = true;
                    Mensagem estadoPartilhaControlada = new Mensagem("ModoPartilhaControladaAtivado", getApplication());
                    estadoPartilhaControlada.enviaMensagem();
                    escreveFicheiroAtividadesSmartIDR("Remote Action: Enable Sharing Mode");
                } else {
                    for (Map.Entry<String, ?> entry : conjuntoPreferencias.entrySet()) {
                        if (entry.getKey().contains("resposta")) {
                            try {
                                resposta = new Intent(getApplicationContext(), Class.forName(entry.getValue().toString()));
                                resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                resposta.putExtra("estado", estado);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            modoPartilha = false;
                            String nomeResposta = entry.getValue().toString();
                            if (nomeResposta.contains("FotografarIntruso")) {
                                paraFotografarIntruso(estado);
                            } else if (nomeResposta.contains("DesativarAplicacoes")) {
                                paraDesativarAplicacoes(estado);
                            } else if (nomeResposta.contains("MensagemTexto")) {
                                paraApresentarMensagem(estado);
                            } else if (nomeResposta.contains("RegistoAtividades")) {
                                paraRegistarAtividades(estado);
                            } else if (nomeResposta.contains("AlarmeSonoro")) {
                                paraAlarmeSonoro(estado);
                            } else {
                                startService(resposta);
                            }
                        }
                    }
                    ativado = false;
                    Mensagem estadoPartilhaControlada = new Mensagem("ModoPartilhaControladaDesativado", getApplication());
                    estadoPartilhaControlada.enviaMensagem();
                    escreveFicheiroAtividadesSmartIDR("Remote Action: Disable Sharing Mode");
                }
            } else {
                Mensagem mensagemResposta = new Mensagem("ModoPartilhaControladaNaoConfigurado", getApplication());
                mensagemResposta.enviaMensagem();
            }
        }

    }

    private void escreveFicheiroAtividadesSmartIDR(String atividade) {
        FileOutputStream outStream = null;
        try {
            outStream = openFileOutput("atividadesSmartIDR.txt", MODE_APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date data = new Date();
        BufferedWriter escritor = new BufferedWriter(new OutputStreamWriter(outStream));
        try {
            escritor.write(data.getTime() + " " + atividade);
            escritor.newLine();
            escritor.close();
            outStream.close();
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("nome", MODE_PRIVATE).edit();
            editor.putString(String.valueOf(data.getTime()),nome);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        FileOutputStream outStream;
        try {
            Date date = new Date();
            nome= String.valueOf(date.getTime()) + " atividadesIntruso.txt";
            outStream = openFileOutput(nome, MODE_PRIVATE);
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void comecaAlarmeSonoro(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.putString("AlarmeSonoro", "AlarmeSonoro");
        editor.commit();
    }

    private void comecaApresentarMensagem(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.putString("ExibirMensagemTexto", "ExibirMensagemTexto");
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

    private void paraApresentarMensagem(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("ExibirMensagemTexto");
        editor.commit();
    }

    private void paraFotografarIntruso(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("FotografarIntruso");
        stopService(new Intent(getApplicationContext(), FotografarIntruso.class));
        editor.commit();
    }
}