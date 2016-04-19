package pt.ul.fc.di.aplicacaosmartphone.interfaceaplicacao;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.tiasa.aplicacaosmartwatch.R;

import pt.ul.fc.di.aplicacaosmartphone.configuracoes.ConfiguracoesRespostas;

public class MenuConfiguracoesRespostas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_configuracao_respostas);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Configurações");
        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconsemligacao);
        int larguraIcon = icon.getIntrinsicWidth();
        int alturaIcon = icon.getIntrinsicHeight();
        icon.setBounds(0, 0, larguraIcon / 11, alturaIcon / 11);
        Button botaoSemLigacao = (Button) findViewById(R.id.botaoSemLigacao);
        botaoSemLigacao.setCompoundDrawables(icon, null, null, null);
        botaoSemLigacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listaRespostas = new Intent(getApplicationContext(), ConfiguracoesRespostas.class);
                listaRespostas.putExtra("estado", "SemLigacao");
                startActivity(listaRespostas);
            }
        });

        icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconpartilha);
        icon.setBounds(0, 0, larguraIcon / 11, alturaIcon / 11);
        Button botaoModoPartilha = (Button) findViewById(R.id.botaoModoPartilha);
        botaoModoPartilha.setCompoundDrawables(icon, null, null, null);
        botaoModoPartilha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listaRespostas = new Intent(getApplicationContext(), ConfiguracoesRespostas.class);
                listaRespostas.putExtra("estado", "Partilha");
                startActivity(listaRespostas);
            }
        });
    }
}
