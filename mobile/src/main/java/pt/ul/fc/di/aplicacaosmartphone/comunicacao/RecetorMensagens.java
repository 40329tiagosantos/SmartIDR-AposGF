package pt.ul.fc.di.aplicacaosmartphone.comunicacao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Map;

import pt.ul.fc.di.aplicacaosmartphone.respostas.FotografarIntruso;

public class RecetorMensagens extends WearableListenerService {

    public static boolean modoPartilha = false;
    private static boolean ativado = false;
    public static boolean comecouDesativarAplicacoes;
    public static boolean comecouRegistoAtividades;
    public static boolean comecouNotificacao;
    public static boolean comecouFotografar;
    public static boolean comecouAlarmeSonoro;
    public static boolean comecouApresentarMensagem;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String mensagem = messageEvent.getPath();
        Intent resposta = null;
        if (!mensagem.equals("AtivarModoPartilha")) {
            String pacote = "pt.ul.fc.di.aplicacaosmartphone.respostas";

            try {
                resposta = new Intent(getApplicationContext(), Class.forName(pacote + '.' + mensagem));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Mensagem mensagemResposta;
            if (mensagem.equals("DesativarAplicacoes") && !comecouDesativarAplicacoes) {
                mensagemResposta = new Mensagem("✓ Desativou as aplicações com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                comecaDesativarAplicacoes("SemLigacao");
                comecaDesativarAplicacoes("Partilha");
                comecouDesativarAplicacoes = true;
            } else if (mensagem.equals("DesativarAplicacoes") && comecouDesativarAplicacoes) {
                mensagemResposta = new Mensagem("✓ Ativou as aplicações com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                paraDesativarAplicacoes("SemLigacao");
                paraDesativarAplicacoes("Partilha");
                comecouDesativarAplicacoes = false;
            } else if (mensagem.equals("RegistoAtividades") && !comecouRegistoAtividades) {
                mensagemResposta = new Mensagem("✓ Registo de atividades do intruso ativado com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                comecaRegistarAtividades("ComLigacaoBT");
                comecouRegistoAtividades = true;
            } else if (mensagem.equals("RegistoAtividades") && comecouRegistoAtividades) {
                mensagemResposta = new Mensagem("✓ Registo de atividades do intruso desativado com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                paraRegistarAtividades("ComLigacaoBT");
                comecouRegistoAtividades = false;
            } else if (mensagem.equals("Notificacao") && !comecouNotificacao) {
                mensagemResposta = new Mensagem("✓ Notificação de Intrusão ativada com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                comecaNotificacao("ComLigacaoBT");
                comecouNotificacao = true;
            } else if (mensagem.equals("Notificacao") && comecouNotificacao) {
                mensagemResposta = new Mensagem("✓ Notificação de Intrusão desativada com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                paraNotificacao("ComLigacaoBT");
                comecouNotificacao = false;
            } else if (mensagem.equals("AlarmeSonoro") && !comecouAlarmeSonoro) {
                mensagemResposta = new Mensagem("✓ Alarme Sonoro ativado com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                comecaAlarmeSonoro("ComLigacaoBT");
                comecouAlarmeSonoro = true;
            } else if (mensagem.equals("AlarmeSonoro") && comecouAlarmeSonoro) {
                mensagemResposta = new Mensagem("✓ Alarme Sonoro desativado com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                paraAlarmeSonoro("ComLigacaoBT");
                comecouAlarmeSonoro = false;
            } else if (mensagem.equals("ExibirMensagemTexto") && !comecouApresentarMensagem) {
                if (!getApplicationContext().getFileStreamPath("MensagemPartilha").exists()) {
                    mensagemResposta = new Mensagem("Definir mensagem de texto no Modo de Partilha Controlada!", getApplication());
                    mensagemResposta.enviaMensagem();
                } else {
                    mensagemResposta = new Mensagem("✓ Exibição de Mensagem de Texto ativada com sucesso!", getApplication());
                    mensagemResposta.enviaMensagem();
                    comecaApresentarMensagem("Partilha");
                    comecouApresentarMensagem = true;
                }
            } else if (mensagem.equals("ExibirMensagemTexto") && comecouApresentarMensagem) {
                if (!getApplicationContext().getFileStreamPath("MensagemPartilha").exists()) {
                    mensagemResposta = new Mensagem("Definir mensagem de texto no Modo de Partilha Controlada!", getApplication());
                    mensagemResposta.enviaMensagem();
                } else {
                    mensagemResposta = new Mensagem("✓ Exbiição de Mensagem de Texto desativada com sucesso!", getApplication());
                    mensagemResposta.enviaMensagem();
                    paraApresentarMensagem("Partilha");
                    comecouApresentarMensagem = false;
                }
            } else if (mensagem.equals("IniciarFotografarIntruso") && !comecouFotografar) {
                mensagemResposta = new Mensagem("✓ Tirar Fotografia do intruso ativado com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                comecaFotografarIntruso("ComLigacaoBT");
                comecouFotografar = true;
            } else if (mensagem.equals("IniciarFotografarIntruso") && comecouFotografar) {
                mensagemResposta = new Mensagem("✓ Tirar Fotografia do intruso desativado com sucesso!", getApplication());
                mensagemResposta.enviaMensagem();
                paraFotografarIntruso("ComLigacaoBT");
                comecouFotografar = false;
            } else if (!mensagem.equals("GestorAlbunsFotografias") && !mensagem.equals("GestorAlbunsVideos")) {
                resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resposta.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                resposta.putExtra("estado","ComLigacaoBT");
                startService(resposta);
            } else {
                resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resposta.putExtra("estado","ComLigacaoBT");
                startService(resposta);
            }
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
                            Log.w(nomeResposta,nomeResposta);
                            if (nomeResposta.contains("FotografarIntruso")) {
                                comecaFotografarIntruso(estado);
                            } else if (nomeResposta.contains("DesativarAplicacoes")) {
                                comecaDesativarAplicacoes(estado);
                            } else if (nomeResposta.contains("MensagemTexto")) {
                                comecaApresentarMensagem(estado);
                            } else if (nomeResposta.contains("RegistoAtividades")){
                                comecaRegistarAtividades(estado);
                            } else if (nomeResposta.contains("Notificacao")) {
                                comecaNotificacao(estado);
                            } else if (nomeResposta.contains("AlarmeSonoro")) {
                                comecaAlarmeSonoro(estado);
                            }else {
                                startService(resposta);
                                modoPartilha = false;
                            }
                        }
                    }
                    ativado = true;
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
                            } else if (nomeResposta.contains("Notificacao")) {
                                paraNotificacao(estado);
                            } else if (nomeResposta.contains("AlarmeSonoro")) {
                                paraNotificacao(estado);
                            }  else {
                                startService(resposta);
                            }
                        }
                    }
                    ativado = false;
                }
            } else {
                Mensagem mensagemResposta = new Mensagem("Tem de criar um perfil no Modo de Partilha Controlada!", getApplication());
                mensagemResposta.enviaMensagem();
            }
            Mensagem estadoPartilhaControlada = new Mensagem("ModoPartilhaControlada",getApplication());
            estadoPartilhaControlada.enviaMensagem();
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

    private void comecaNotificacao(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.putString("Notificacao", "Notificacao");
        editor.commit();
    }

    private void paraAlarmeSonoro(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("AlarmeSonoro");
        editor.commit();
    }

    private void paraNotificacao(String estado) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, MODE_PRIVATE).edit();
        editor.remove("Notificacao");
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