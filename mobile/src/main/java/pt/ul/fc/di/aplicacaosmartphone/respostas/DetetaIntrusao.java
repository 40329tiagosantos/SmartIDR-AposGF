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
        
        if (preferenciasComandoSmartwatch.contains("AlarmeSonoro")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaAlarme = new Intent(getApplicationContext(), AlarmeSonoro.class);
                iniciaAlarme.putExtra("estado", "ComLigacaoBT");
                startService(iniciaAlarme);
            }
        } else if (preferenciasPartilha.contains("AlarmeSonoro")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaAlarme = new Intent(getApplicationContext(), AlarmeSonoro.class);
                iniciaAlarme.putExtra("estado", "Partilha");
                startService(iniciaAlarme);
            }
        } else if (preferenciasSemLigacao.contains("AlarmeSonoro")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaAlarme = new Intent(getApplicationContext(), AlarmeSonoro.class);
                iniciaAlarme.putExtra("estado", "SemLigacao");
                startService(iniciaAlarme);
            }
        }


        if (preferenciasComandoSmartwatch.contains("Notificacao")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaNotificacao = new Intent(getApplicationContext(), Notificacao.class);
                iniciaNotificacao.putExtra("estado", "ComLigacaoBT");
                startService(iniciaNotificacao);
            }
        } else if (preferenciasPartilha.contains("Notificacao")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaNotificacao = new Intent(getApplicationContext(), Notificacao.class);
                iniciaNotificacao.putExtra("estado", "Partilha");
                startService(iniciaNotificacao);
            }
        }

        if (preferenciasComandoSmartwatch.contains("ExibirMensagemTexto")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaExibirMensagem = new Intent(getApplicationContext(), ExibirMensagemTexto.class);
                iniciaExibirMensagem.putExtra("estado", "ComLigacaoBT");
                startService(iniciaExibirMensagem);
            }
        } else if (preferenciasPartilha.contains("ExibirMensagemTexto")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaExibirMensagem = new Intent(getApplicationContext(), ExibirMensagemTexto.class);
                iniciaExibirMensagem.putExtra("estado", "Partilha");
                startService(iniciaExibirMensagem);
            }
        } else if (preferenciasSemLigacao.contains("ExibirMensagemTexto")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaExibirMensagem = new Intent(getApplicationContext(), ExibirMensagemTexto.class);
                iniciaExibirMensagem.putExtra("estado", "SemLigacao");
                startService(iniciaExibirMensagem);
            }
        }

        if (preferenciasComandoSmartwatch.contains("FotografarIntruso")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaFotografarIntruso = new Intent(getApplicationContext(), IniciarFotografarIntruso.class);
                iniciaFotografarIntruso.putExtra("estado", "ComLigacaoBT");
                startService(iniciaFotografarIntruso);
            }
        } else if (preferenciasSemLigacao.contains("FotografarIntruso")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaFotografarIntruso = new Intent(getApplicationContext(), IniciarFotografarIntruso.class);
                iniciaFotografarIntruso.putExtra("estado", "SemLigacao");
                startService(iniciaFotografarIntruso);
            }
        } else if (preferenciasPartilha.contains("FotografarIntruso")) {
            if (devolveTipoEvento(evento).equals("TYPE_VIEW_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_LONG_CLICKED") || devolveTipoEvento(evento).equals("TYPE_VIEW_SCROLLED")) {
                Intent iniciaFotografarIntruso = new Intent(getApplicationContext(), IniciarFotografarIntruso.class);
                iniciaFotografarIntruso.putExtra("estado", "Partilha");
                startService(iniciaFotografarIntruso);
            }
        }

        if (preferenciasSemLigacao.contains("DesativarAplicacoes")) {
            Intent iniciaFotografarIntruso = new Intent(getApplicationContext(), DesativarAplicacoes.class);
            iniciaFotografarIntruso.putExtra("estado", "SemLigacao");
            iniciaFotografarIntruso.putExtra("nomeEvento", devolveNomeEvento(evento));
            iniciaFotografarIntruso.putExtra("nomePacote", evento.getPackageName().toString());
            startService(iniciaFotografarIntruso);
        }
        if (preferenciasPartilha.contains("DesativarAplicacoes")) {
            Intent iniciaFotografarIntruso = new Intent(getApplicationContext(), DesativarAplicacoes.class);
            iniciaFotografarIntruso.putExtra("estado", "Partilha");
            iniciaFotografarIntruso.putExtra("nomeEvento", devolveNomeEvento(evento));
            iniciaFotografarIntruso.putExtra("nomePacote", evento.getPackageName().toString());
            startService(iniciaFotografarIntruso);
        }

        if (preferenciasComandoSmartwatch.contains("RegistoAtividades")) {

            Intent iniciarRegistarAtividades = new Intent(getApplicationContext(), RegistoAtividades.class);
            iniciarRegistarAtividades.putExtra("estado", "ComLigacaoBT");
            iniciarRegistarAtividades.putExtra("nomeEvento", devolveNomeEvento(evento));
            iniciarRegistarAtividades.putExtra("nomePacote", evento.getPackageName().toString());
            if (evento.getContentDescription() != null)
                iniciarRegistarAtividades.putExtra("descricaoEvento", evento.getContentDescription().toString());
            else
                iniciarRegistarAtividades.putExtra("descricaoEvento", "");

            iniciarRegistarAtividades.putExtra("tipoEvento", devolveTipoEvento(evento));
            iniciarRegistarAtividades.putExtra("textoEvento", evento.getText().toString());
            iniciarRegistarAtividades.putExtra("classeEvento", evento.getClassName().toString());
            startService(iniciarRegistarAtividades);

        } else if (preferenciasSemLigacao.contains("RegistoAtividades")) {
            Intent iniciarRegistarAtividades = new Intent(getApplicationContext(), RegistoAtividades.class);
            iniciarRegistarAtividades.putExtra("estado", "SemLigacao");
            iniciarRegistarAtividades.putExtra("nomeEvento", devolveNomeEvento(evento));
            iniciarRegistarAtividades.putExtra("nomePacote", evento.getPackageName().toString());
            if (evento.getContentDescription() != null)
                iniciarRegistarAtividades.putExtra("descricaoEvento", evento.getContentDescription().toString());
            else
                iniciarRegistarAtividades.putExtra("descricaoEvento", "");

            iniciarRegistarAtividades.putExtra("tipoEvento", devolveTipoEvento(evento));
            iniciarRegistarAtividades.putExtra("textoEvento", evento.getText().toString());
            iniciarRegistarAtividades.putExtra("classeEvento", evento.getClassName().toString());
            startService(iniciarRegistarAtividades);
        } else if (preferenciasPartilha.contains("RegistoAtividades")) {
            Intent iniciarRegistarAtividades = new Intent(getApplicationContext(), RegistoAtividades.class);
            iniciarRegistarAtividades.putExtra("estado", "Partilha");
            iniciarRegistarAtividades.putExtra("nomeEvento", devolveNomeEvento(evento));
            iniciarRegistarAtividades.putExtra("nomePacote", evento.getPackageName().toString());
            if (evento.getContentDescription() != null)
                iniciarRegistarAtividades.putExtra("descricaoEvento", evento.getContentDescription().toString());
            else
                iniciarRegistarAtividades.putExtra("descricaoEvento", "");

            iniciarRegistarAtividades.putExtra("tipoEvento", devolveTipoEvento(evento));
            iniciarRegistarAtividades.putExtra("textoEvento", evento.getText().toString());
            iniciarRegistarAtividades.putExtra("classeEvento", evento.getClassName().toString());
            startService(iniciarRegistarAtividades);
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
}
