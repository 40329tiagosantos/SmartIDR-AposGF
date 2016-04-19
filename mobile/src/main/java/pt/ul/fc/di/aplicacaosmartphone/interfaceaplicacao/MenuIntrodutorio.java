package pt.ul.fc.di.aplicacaosmartphone.interfaceaplicacao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.Timer;
import java.util.TimerTask;

public class MenuIntrodutorio extends AppCompatActivity {

    private Timer cronometro;
    private TimerTask tarefa;

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_menu_apresentacao);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        cronometro = new Timer();
        tarefa = new TimerTask() {
            @Override
            public void run() {
                Intent comecaAplicacao = new Intent(getApplicationContext(), MenuPrincipal.class);
                comecaAplicacao.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                comecaAplicacao.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(comecaAplicacao);
            }
        };
        cronometro.schedule(tarefa, 1000);
    }
}