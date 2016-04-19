package pt.ul.fc.di.aplicacaosmartphone.relatorio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ListaRegistosAtividades extends Activity {

    private ArrayList<ItemLista> listaRegistoAtividades;
    private ListView vistaListaRegistoAtividades;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_atividades_fotosintruso);
        TextView cabecalho = (TextView)findViewById(R.id.cabecalho);

        cabecalho.append("Atividades");

        SpannableString cabecalhoEditado = new SpannableString(cabecalho.getText().toString());
        cabecalhoEditado.setSpan(new RelativeSizeSpan(1.2f), 0, cabecalhoEditado.length(), 0);
        cabecalho.setText(cabecalhoEditado);

        if ( listaRegistoAtividades == null )
            listaRegistoAtividades = new ArrayList<>();

        File ficheiros[] = getApplicationContext().getFilesDir().listFiles();
        ItemLista registo;

        for (File ficheiroAtividadesIntruso:ficheiros) {
            if (ficheiroAtividadesIntruso.getName().contains("atividadesIntruso")) {
                registo = new ItemLista();
                DateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                SharedPreferences data = getApplicationContext().getSharedPreferences("DataFicheiroAtividades", MODE_PRIVATE);
                if(!data.contains(ficheiroAtividadesIntruso.getPath())) {
                    SharedPreferences.Editor editor = data.edit();
                    editor.putString(ficheiroAtividadesIntruso.getPath(), formatoData.format(ficheiroAtividadesIntruso.lastModified()));
                    editor.commit();
                }
                registo.atribuiNome("Exibir Atividades: " + data.getString(ficheiroAtividadesIntruso.getPath(),""));
                registo.atribuiNomePacote(ficheiroAtividadesIntruso.getPath());
                listaRegistoAtividades.add(registo);
            }
        }
        Collections.reverse(listaRegistoAtividades);
        criaListaRegistoAtividades();
        adicionaClickListener();
    }

    private void criaListaRegistoAtividades(){
        vistaListaRegistoAtividades = (ListView)findViewById(R.id.lista_atividades);

        ArrayAdapter<ItemLista> adapter = new ArrayAdapter<ItemLista>(this, R.layout.item_lista_respostas_registos, listaRegistoAtividades) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_lista_respostas_registos, null);

                TextView nomeRegisto = (TextView)convertView.findViewById(R.id.nome_ficheiro_atividades);
                nomeRegisto.setText(listaRegistoAtividades.get(position).devolveNome());
                return convertView;
            }
        };
        vistaListaRegistoAtividades.setAdapter(adapter);
    }

    private void adicionaClickListener(){
        vistaListaRegistoAtividades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Intent browserIntent = new Intent(getApplicationContext(),ListaAtividades.class);
                browserIntent.putExtra("nomeFicheiro", listaRegistoAtividades.get(pos).devolveNomePacote());
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(browserIntent);
            }
        });
    }
}