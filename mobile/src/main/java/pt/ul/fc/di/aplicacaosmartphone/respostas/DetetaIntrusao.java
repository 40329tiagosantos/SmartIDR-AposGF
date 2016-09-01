package pt.ul.fc.di.aplicacaosmartphone.respostas;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;

public class DetetaIntrusao extends AccessibilityService {

    private String devolveTipoEvento(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                return "TYPE_VIEW_SCROLLED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                return "TYPE_WINDOW_CONTENT_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                return "TYPE_VIEW_TEXT_SELECTION_CHANGED";
        }
        return "";
    }

    private String devolveNomeEvento(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent evento) {
        SharedPreferences preferenciasComandoSmartwatch = getApplicationContext().getSharedPreferences("preferenciasUtilizadorComLigacaoBT", MODE_PRIVATE);
        SharedPreferences preferenciasSemLigacao = getApplicationContext().getSharedPreferences("preferenciasUtilizadorSemLigacao", MODE_PRIVATE);
        SharedPreferences preferenciasPartilha = getApplicationContext().getSharedPreferences("preferenciasUtilizadorPartilha", MODE_PRIVATE);
        SharedPreferences preferenciasquickLaunch = getApplicationContext().getSharedPreferences("preferenciasUtilizadorQuickLaunch", MODE_PRIVATE);

        if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
            if (preferenciasComandoSmartwatch.contains("AlarmeSonoro")) {
                iniciaAlarmeSonoro("ComLigacaoBT");
            } else if (preferenciasPartilha.contains("AlarmeSonoro")) {
                iniciaAlarmeSonoro("Partilha");
            } else if (preferenciasSemLigacao.contains("AlarmeSonoro")) {
                iniciaAlarmeSonoro("SemLigacao");
            }
        }


        if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
            if (preferenciasComandoSmartwatch.contains("ExibirMensagemTexto")) {
                iniciaExibirMensagemTexto("ComLigacaoBT");
            } else if (preferenciasPartilha.contains("ExibirMensagemTexto")) {
                iniciaExibirMensagemTexto("Partilha");
            } else if (preferenciasSemLigacao.contains("ExibirMensagemTexto")) {
                iniciaExibirMensagemTexto("SemLigacao");
            }
        }

        if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
            if (preferenciasComandoSmartwatch.contains("FotografarIntruso")) {
                iniciaFotografarIntruso("ComLigacaoBT");
            } else if (preferenciasSemLigacao.contains("FotografarIntruso")) {
                iniciaFotografarIntruso("SemLigacao");
            } else if (preferenciasPartilha.contains("FotografarIntruso")) {
                iniciaFotografarIntruso("Partilha");
            }
        }
        if (preferenciasSemLigacao.contains("DesativarAplicacoes")) {
            iniciarDesativarAplicacoes("SemLigacao", devolveNomeEvento(evento), evento.getPackageName().toString());
        }

        if (preferenciasquickLaunch.contains("DesativarAplicacoes")) {
            iniciarDesativarAplicacoes("QuickLaunch", devolveNomeEvento(evento), evento.getPackageName().toString());
        }


        if (preferenciasPartilha.contains("DesativarAplicacoes")) {
            iniciarDesativarAplicacoes("Partilha", devolveNomeEvento(evento), evento.getPackageName().toString());
        }

        if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_TEXT_SELECTION_CHANGED")|| devolveTipoEvento(evento).equals("TYPE_WINDOW_CONTENT_CHANGED")|| devolveTipoEvento(evento).equals("TYPE_VIEW_TEXT_CHANGED") || devolveTipoEvento(evento).equals("TYPE_WINDOW_STATE_CHANGED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SELECTED")) {
            if (preferenciasComandoSmartwatch.contains("RegistoAtividades")) {
                iniciaRegistoAtividades("ComLigacaoBT", evento);
            } else if (preferenciasSemLigacao.contains("RegistoAtividades")) {
                iniciaRegistoAtividades("SemLigacao", evento);
            } else if (preferenciasPartilha.contains("RegistoAtividades")) {
                iniciaRegistoAtividades("Partilha", evento);
            }
            else if (preferenciasquickLaunch.contains("RegistoAtividades")) {
                iniciaRegistoAtividades("QuickLaunch", evento);
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

    private void iniciaAlarmeSonoro(String estado) {
        Intent iniciaAlarme = new Intent(getApplicationContext(), AlarmeSonoro.class);
        iniciaAlarme.putExtra("estado", estado);
        startService(iniciaAlarme);
    }

    private void iniciaExibirMensagemTexto(String estado) {
        Intent iniciaExibirMensagem = new Intent(getApplicationContext(), ExibirMensagemTexto.class);
        iniciaExibirMensagem.putExtra("estado", estado);
        startService(iniciaExibirMensagem);
    }

    private void iniciaFotografarIntruso(String estado) {
        Intent iniciaFotografarIntruso = new Intent(getApplicationContext(), IniciarFotografarIntruso.class);
        iniciaFotografarIntruso.putExtra("estado", estado);
        startService(iniciaFotografarIntruso);
    }

    private void iniciarDesativarAplicacoes(String estado, String nomeEvento, String nomePacote) {
        Intent iniciaDesativarAplicacoes = new Intent(getApplicationContext(), DesativarAplicacoes.class);
        iniciaDesativarAplicacoes.putExtra("estado", estado);
        iniciaDesativarAplicacoes.putExtra("nomeEvento", nomeEvento);
        iniciaDesativarAplicacoes.putExtra("nomePacote", nomePacote);
        startService(iniciaDesativarAplicacoes);
    }

    private void iniciaRegistoAtividades(String estado, AccessibilityEvent evento) {
        Intent iniciarRegistarAtividades = new Intent(getApplicationContext(), RegistoAtividades.class);
        iniciarRegistarAtividades.putExtra("estado", estado);
        iniciarRegistarAtividades.putExtra("nomeEvento", devolveNomeEvento(evento));
        iniciarRegistarAtividades.putExtra("nomePacote", evento.getPackageName().toString());
        if (evento.getContentDescription() != null)
            iniciarRegistarAtividades.putExtra("descricaoEvento", evento.getContentDescription().toString());
        else
            iniciarRegistarAtividades.putExtra("descricaoEvento", "");

        iniciarRegistarAtividades.putExtra("tipoEvento", devolveTipoEvento(evento));
        iniciarRegistarAtividades.putExtra("classeEvento", evento.getClassName().toString());
        startService(iniciarRegistarAtividades);
    }
}
