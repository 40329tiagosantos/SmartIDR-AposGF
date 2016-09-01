package pt.ul.fc.di.aplicacaosmartphone.relatorio;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListaAtividades extends Fragment  {

    private ArrayList<ItemLista> listaAtividades;
    public static boolean iniciouListaAtividades;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lista_atividades, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Log");;

        File ficheiros[] = getActivity().getFilesDir().listFiles();

        for (File ficheiroAtividadesIntruso : ficheiros) {
            String nomeFicheiro = ficheiroAtividadesIntruso.getName();
            if (nomeFicheiro.contains("fotoIntruso")) {
                Bitmap myBitmap = BitmapFactory.decodeFile(ficheiroAtividadesIntruso.getAbsolutePath());
                ImageView myImage = (ImageView) view.findViewById(R.id.fotografia);
                myImage.setImageBitmap(myBitmap);
                myImage.getLayoutParams().height=350;
                myImage.getLayoutParams().width=300;
            }
        }
        iniciouListaAtividades = true;

        try {
            apresentaLista();
        } catch (IOException e) {
            e.printStackTrace();
        }
        criaListaRegistoAtividades();
        return view;
    }

    private void apresentaLista() throws IOException {
        if (listaAtividades == null)
            listaAtividades = new ArrayList<ItemLista>();

        InputStream inputStream = null;
        try {
            Bundle bundle = this.getArguments();
            String  nomeFicheiro = bundle.getString("nomeFicheiro");
            inputStream = getActivity().openFileInput(nomeFicheiro);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String linhaFicheiro = bufferedReader.readLine();

            try {
                while (linhaFicheiro != null) {
                    if (!linhaFicheiro.equals("")) {
                        long agora = System.currentTimeMillis();
                        long passados = Long.parseLong(linhaFicheiro.substring(0, linhaFicheiro.indexOf(" ")));
                        linhaFicheiro = linhaFicheiro.replace(linhaFicheiro.substring(0, linhaFicheiro.indexOf(" ")), "");
                        long segundosPassados = agora - ((agora - passados));
                        String tempoDecorrido = (DateUtils.getRelativeDateTimeString(getActivity().getApplicationContext(), segundosPassados, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_TIME)).toString();

                        ItemLista registo = new ItemLista();

                        Intent aplicacoesLauncher = new Intent(Intent.ACTION_MAIN, null);
                        aplicacoesLauncher.addCategory(Intent.CATEGORY_LAUNCHER);
                        PackageManager gestorPacotes = getActivity().getPackageManager();
                        List<ResolveInfo> availableActivities = gestorPacotes.queryIntentActivities(aplicacoesLauncher, 0);

                        if (linhaFicheiro.contains("Wrote")) {
                            registo.atribuiIcon(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.iconescrita));
                        } else {
                            for (ResolveInfo ri : availableActivities) {
                                if (linhaFicheiro.contains(ri.activityInfo.packageName) && !linhaFicheiro.contains("Closed")) {
                                    Drawable icon = ri.activityInfo.loadIcon(gestorPacotes);
                                    registo.atribuiIcon(icon);
                                    break;
                                } else if (linhaFicheiro.contains(ri.loadLabel(gestorPacotes).toString())) {
                                    Drawable icon = ri.activityInfo.loadIcon(gestorPacotes);
                                    registo.atribuiIcon(icon);
                                    break;
                                }
                            }
                        }

                        linhaFicheiro = linhaFicheiro.replace(linhaFicheiro.substring(linhaFicheiro.lastIndexOf(" "), linhaFicheiro.length()), "");
                        linhaFicheiro = linhaFicheiro.replaceFirst(" ", "");
                        registo.atribuiNome(tempoDecorrido + "\n" + linhaFicheiro);
                        listaAtividades.add(registo);
                        linhaFicheiro = bufferedReader.readLine();
                    }
                }
                TextView textoSemMensagens = (TextView) view.findViewById(R.id.textoSemAtividades);
                if (textoSemMensagens != null) {
                    textoSemMensagens.setVisibility(View.INVISIBLE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        listaAtividades.clear();
        try {
            apresentaLista();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(listaAtividades);
        criaListaRegistoAtividades();
    }

    private void criaListaRegistoAtividades() {
        ListView vistaListaAtividades = (ListView) view.findViewById(R.id.lista);
        if (vistaListaAtividades != null) {
            vistaListaAtividades.setSelector(android.R.color.transparent);
        }
        ArrayAdapter<ItemLista> adapter = new ArrayAdapter<ItemLista>(getActivity(), R.layout.item_lista_atividades, listaAtividades) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.item_lista_atividades, null);
                TextView nomeAtividade = (TextView) convertView.findViewById(R.id.nome_atividade);
                nomeAtividade.setText(listaAtividades.get(position).devolveNome());
                ImageView iconAtividade = (ImageView) convertView.findViewById(R.id.icon_atividade);
                iconAtividade.setImageDrawable(listaAtividades.get(position).devolveIcon());
                return convertView;
            }
        };
        if (vistaListaAtividades != null) {
            vistaListaAtividades.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iniciouListaAtividades = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
