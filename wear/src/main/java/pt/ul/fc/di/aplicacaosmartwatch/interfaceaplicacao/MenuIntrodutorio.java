package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.Timer;
import java.util.TimerTask;

import pt.ul.fc.di.aplicacaosmartwatch.comunicacao.Mensagem;
import pt.ul.fc.di.aplicacaosmartwatch.detecao.DetetorLigacaoSmartphone;
import pt.ul.fc.di.aplicacaosmartwatch.detecao.DetetorPartilhaControlada;

public class MenuIntrodutorio extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_apresentacao);

        Mensagem iniciaRegistarAtividades = new Mensagem("IniciaRegistoAtividades", getApplication());
        iniciaRegistarAtividades.enviaMensagem();

        if(ListaAtividades.listaAtividades!=null)
            ListaAtividades.listaAtividades.clear();
        if(ListaAtividades.lista!=null)
            ListaAtividades.lista=null;

        Intent servicoDeteccao = new Intent(getApplicationContext(), DetetorLigacaoSmartphone.class);
        startService(servicoDeteccao);
        Intent servicoPartilhaControlada = new Intent(getApplicationContext(), DetetorPartilhaControlada.class);
        startService(servicoPartilhaControlada);
        String estado = DetetorLigacaoSmartphone.estado;
        switch (estado) {
            case "ComLigacaoWF":
                setTheme(R.style.YellowTheme);
                break;
            case "SemLigacao":
                setTheme(R.style.RedTheme);
                break;
            default:
                setTheme(R.style.BlueTheme);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_menu_apresentacao);
        String estado = DetetorLigacaoSmartphone.estado;
        switch (estado) {
            case "ComLigacaoWF":
                setTheme(R.style.YellowTheme);
                break;
            case "ComLigacaoBT":
                setTheme(R.style.BlueTheme);
                break;
            default:
                setTheme(R.style.RedTheme);
                break;
        }
        Timer cronometro = new Timer();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                Intent comecaAplicacao = new Intent(getApplicationContext(), ConstroiFragmentos.class);
                comecaAplicacao.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                comecaAplicacao.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(comecaAplicacao);
            }
        };
        cronometro.schedule(tarefa, 1000);
    }

}
