package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.Timer;
import java.util.TimerTask;

import pt.ul.fc.di.aplicacaosmartwatch.detecao.DetetorLigacaoSmartphone;

public class MenuIntrodutorio extends Activity {

    private Timer cronometro;
    private TimerTask tarefa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_apresentacao);
        Intent servicoDeteccao = new Intent(getApplicationContext(), DetetorLigacaoSmartphone.class);
        startService(servicoDeteccao);
        Intent servicoPartilhaControlada = new Intent(getApplicationContext(), DetetorPartilhaControlada.class);
        startService(servicoPartilhaControlada);
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
        cronometro = new Timer();
        tarefa = new TimerTask() {
            @Override
            public void run() {
                Intent comecaAplicacao = new Intent(getApplicationContext(), ListaRespostas.class);
                comecaAplicacao.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                comecaAplicacao.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(comecaAplicacao);
            }
        };
        cronometro.schedule(tarefa, 1000);
    }
}
