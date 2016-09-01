package pt.ul.fc.di.aplicacaosmartphone.interfaceaplicacao;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.MenuPrincipal;

public class MenuIntrodutorio extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_menu_apresentacao);

        iniciaPluginsComunicacao();
        iniciaPluginsDetecao();

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        Timer cronometro = new Timer();
        TimerTask tarefa = new TimerTask() {
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

    private void iniciaPluginsDetecao() {
        Intent deteccao = new Intent(Intent.ACTION_MAIN, null);
        deteccao.addCategory("com.example.tiasa.aplicacaosmartphone.deteccao.PLUGIN");
        PackageManager gestorPacotes = getPackageManager();
        List<ResolveInfo> servicosDeteccao = gestorPacotes.queryIntentServices(deteccao, 0);
        for (ResolveInfo ri : servicosDeteccao) {
            try {
                startService(new Intent(getApplicationContext(), Class.forName(ri.serviceInfo.name)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void iniciaPluginsComunicacao() {
        Intent comunicacao = new Intent(Intent.ACTION_MAIN, null);
        comunicacao.addCategory("com.example.tiasa.aplicacaosmartphone.comunicacao.PLUGIN");
        PackageManager gestorPacotes = getPackageManager();
        List<ResolveInfo> servicosComunicacao = gestorPacotes.queryIntentServices(comunicacao, 0);
        for (ResolveInfo ri : servicosComunicacao) {
            try {
                startService(new Intent(getApplicationContext(), Class.forName(ri.serviceInfo.name)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}