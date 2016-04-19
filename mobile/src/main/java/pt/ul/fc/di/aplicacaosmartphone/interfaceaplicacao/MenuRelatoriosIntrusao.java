package pt.ul.fc.di.aplicacaosmartphone.interfaceaplicacao;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.tiasa.aplicacaosmartwatch.R;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ListaFotografias;
import pt.ul.fc.di.aplicacaosmartphone.relatorio.ListaRegistosAtividades;

public class MenuRelatoriosIntrusao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_dados_intruso);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Relatório de Intrusões");
        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconregistoatividades);
        int larguraIcon = icon.getIntrinsicWidth();
        int alturaIcon = icon.getIntrinsicHeight();
        icon.setBounds(0, 0, larguraIcon / 11, alturaIcon / 11);
        Button botaoVisualizarRegistoAtividades = (Button) findViewById(R.id.botaoVisualizarRegistoAtividades);
        botaoVisualizarRegistoAtividades.setCompoundDrawables(icon, null, null, null);
        botaoVisualizarRegistoAtividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listaAtividadesIntruso = new Intent(getApplicationContext(), ListaRegistosAtividades.class);
                startActivity(listaAtividadesIntruso);
            }
        });

        icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconfotografiasintruso);
        icon.setBounds(0, 0, larguraIcon / 11, alturaIcon / 11);
        Button botaoFotografias = (Button) findViewById(R.id.botaoFotografias);
        botaoFotografias.setCompoundDrawables(icon, null, null, null);
        botaoFotografias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instrucoes = new Intent(getApplicationContext(), ListaFotografias.class);
                startActivity(instrucoes);
            }
        });
    }
}
