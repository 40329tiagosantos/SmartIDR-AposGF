package pt.ul.fc.di.aplicacaosmartphone.relatorio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListaAtividades extends AppCompatActivity {

    private ArrayList<ItemLista> listaAtividades;
    private ListView vistaListaAtividades;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apresenta_actividades);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        apresentaLista();
        Collections.reverse(listaAtividades);
        criaListaRegistoAtividades();
    }

    private void apresentaLista() {
        if (listaAtividades == null)
            listaAtividades = new ArrayList<ItemLista>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(getIntent().getStringExtra("nomeFicheiro")));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        String linha = null;
        try {
            if (br != null)
                linha = br.readLine();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (linha != null) {
            ItemLista registo = new ItemLista();
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager gestorPacotes = getPackageManager();
            List<ResolveInfo> availableActivities = gestorPacotes.queryIntentActivities(i, 0);
            for (ResolveInfo ri : availableActivities) {
                if (linha.contains(ri.loadLabel(gestorPacotes).toString())) {
                    registo.atribuiIcon(ri.activityInfo.loadIcon(gestorPacotes));
                } else if (linha.contains(ri.activityInfo.packageName)) {
                    registo.atribuiIcon(ri.activityInfo.loadIcon(gestorPacotes));
                }
            }
            String palavrasLinha[] = linha.split(" ");
            if (palavrasLinha.length >= 2)
                registo.atribuiNome(linha.replace(palavrasLinha[1], " "));
            if (palavrasLinha.length >= 3) {
                listaAtividades.add(registo);
            }
            try {
                linha = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            if (br != null)
                br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void criaListaRegistoAtividades() {
        vistaListaAtividades = (ListView) findViewById(R.id.lista);

        ArrayAdapter<ItemLista> adapter = new ArrayAdapter<ItemLista>(this, R.layout.item_lista_atividades, listaAtividades) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_lista_atividades, null);
                TextView nomeAtividade = (TextView) convertView.findViewById(R.id.nome_atividade);
                nomeAtividade.setText(listaAtividades.get(position).devolveNome());
                ImageView iconAtividade = (ImageView) convertView.findViewById(R.id.icon_atividade);
                iconAtividade.setImageDrawable(listaAtividades.get(position).devolveIcon());
                return convertView;
            }
        };
        vistaListaAtividades.setAdapter(adapter);
    }
}
