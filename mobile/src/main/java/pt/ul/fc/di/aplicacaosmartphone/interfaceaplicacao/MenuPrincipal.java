package pt.ul.fc.di.aplicacaosmartphone.interfaceaplicacao;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.List;

import pt.ul.fc.di.aplicacaosmartphone.configuracoes.LimparPreferencias;
import pt.ul.fc.di.aplicacaosmartphone.respostas.DeviceAdminReceiver;

public class MenuPrincipal extends AppCompatActivity {

    static final int ACTIVATION_REQUEST = 47;
    ComponentName demoDeviceAdmin;
    DevicePolicyManager devicePolicyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        demoDeviceAdmin = new ComponentName(this, DeviceAdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, demoDeviceAdmin);
        startActivityForResult(intent, ACTIVATION_REQUEST);
        iniciaPluginsComunicacao();
        iniciaPluginsDetecao();

        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconconfiguracao);
        int larguraIcon = icon.getIntrinsicWidth();
        int alturaIcon = icon.getIntrinsicHeight();
        Button botaoComSinal = (Button) findViewById(R.id.botaoconfig);
        icon.setBounds(0, 0, larguraIcon / 11, alturaIcon / 11);
        botaoComSinal.setCompoundDrawables(icon, null, null, null);
        botaoComSinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listaRespostas = new Intent(getApplicationContext(), MenuConfiguracoesRespostas.class);
                startActivity(listaRespostas);
            }
        });

        icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.icondadosintrusos);
        icon.setBounds(0, 0, larguraIcon / 11, alturaIcon / 11);
        Button botaoSemSinal = (Button) findViewById(R.id.botaoExibirDados);
        botaoSemSinal.setCompoundDrawables(icon, null, null, null);
        botaoSemSinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listaRespostas = new Intent(getApplicationContext(), MenuRelatoriosIntrusao.class);
                startActivity(listaRespostas);
            }
        });

        icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconinstrucoes);
        icon.setBounds(0, 0, larguraIcon / 11, alturaIcon / 11);
        Button botaoInstrucoes = (Button) findViewById(R.id.botaoInstrucoes);
        botaoInstrucoes.setCompoundDrawables(icon, null, null, null);
        botaoInstrucoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instrucoes = new Intent(getApplicationContext(), Instrucoes.class);
                startActivity(instrucoes);
            }
        });

        icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconlimparprefs);
        icon.setBounds(0, 0, larguraIcon / 11, alturaIcon / 11);
        Button botaoLimparPreferencias = (Button) findViewById(R.id.botaoLimparPreferencias);
        botaoLimparPreferencias.setCompoundDrawables(icon, null, null, null);
        botaoLimparPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent limparPreferencias = new Intent(getApplicationContext(), LimparPreferencias.class);
                startService(limparPreferencias);
            }
        });
    }

    public void iniciaPluginsDetecao() {
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

    public void iniciaPluginsComunicacao() {
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
