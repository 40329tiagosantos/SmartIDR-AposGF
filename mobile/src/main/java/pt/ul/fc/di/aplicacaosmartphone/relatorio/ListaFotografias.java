package pt.ul.fc.di.aplicacaosmartphone.relatorio;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ListaFotografias extends Activity {

    private ArrayList<ItemLista> listaFotografiasIntrusos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_atividades_fotosintruso);
        TextView cabecalho = (TextView) findViewById(R.id.cabecalho);

        cabecalho.append("Fotografias");

        SpannableString cabecalhoEditado = new SpannableString(cabecalho.getText().toString());
        cabecalhoEditado.setSpan(new RelativeSizeSpan(1.2f), 0, cabecalhoEditado.length(), 0);
        cabecalho.setText(cabecalhoEditado);

        if (listaFotografiasIntrusos == null)
            listaFotografiasIntrusos = new ArrayList<>();

        File ficheiros[] = getApplicationContext().getFilesDir().listFiles();
        ItemLista registo;

        for (File ficheiroFotoIntruso : ficheiros) {
            if (ficheiroFotoIntruso.getName().contains("fotoIntruso")) {
                if (Drawable.createFromPath(ficheiroFotoIntruso.getPath()) != null) {
                    registo = new ItemLista();
                    DateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    SharedPreferences data = getApplicationContext().getSharedPreferences("DataFicheiroFotos", MODE_PRIVATE);
                    if (!data.contains(ficheiroFotoIntruso.getPath())) {
                        SharedPreferences.Editor editor = data.edit();
                        editor.putString(ficheiroFotoIntruso.getPath(), formatoData.format(ficheiroFotoIntruso.lastModified()));
                        editor.commit();
                    }
                    int numeroSessao = Character.getNumericValue(ficheiroFotoIntruso.getName().charAt(0));
                    registo.atribuiNome("Fotografia " + numeroSessao + ": " + data.getString(ficheiroFotoIntruso.getPath(), ""));
                    registo.atribuiIcon(redimensionaImagem(Drawable.createFromPath(ficheiroFotoIntruso.getPath())));
                    listaFotografiasIntrusos.add(registo);
                }
            }
        }
        Collections.reverse(listaFotografiasIntrusos);
        criaListaFotografiasIntruso();
    }

    private Drawable redimensionaImagem(Drawable imagem) {
        Bitmap bitMap;
        bitMap = ((BitmapDrawable) imagem).getBitmap();
        Bitmap imagemRedimensionada = Bitmap.createScaledBitmap(bitMap, 220, 330, false);
        return new BitmapDrawable(getResources(), imagemRedimensionada);
    }


    private void criaListaFotografiasIntruso() {
        ListView vistaFotografiasIntrusos = (ListView) findViewById(R.id.lista_fotosintruso);

        ArrayAdapter<ItemLista> adapter = new ArrayAdapter<ItemLista>(this, R.layout.item_lista_intruso, listaFotografiasIntrusos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_lista_intruso, null);

                TextView nomeFotoIntruso = (TextView) convertView.findViewById(R.id.nome_intruso);
                nomeFotoIntruso.setText(listaFotografiasIntrusos.get(position).devolveNome());
                ImageView iconFotoIntruso = (ImageView) convertView.findViewById(R.id.icon_intruso);
                iconFotoIntruso.setImageDrawable(listaFotografiasIntrusos.get(position).devolveIcon());
                return convertView;
            }
        };
        vistaFotografiasIntrusos.setAdapter(adapter);
    }
}