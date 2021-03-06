package pt.ul.fc.di.aplicacaosmartphone.configuracoes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemLista;
import pt.ul.fc.di.aplicacaosmartphone.relatorio.ListaAtividadesSmartIDR;
import pt.ul.fc.di.aplicacaosmartphone.respostas.GestorAlbunsFotografias;

public class ConfiguracoesAlbunsFotografias extends Fragment {

    private ArrayList<ItemLista> listaAlbuns;
    private ArrayList<String> listaAlbunsSeleccionados;
    public static String estado;
    private SharedPreferences preferenciasAlbunsFotografias;
    private Map<String, ?> conjuntoPreferenciasAlbunsFotografias;
    private boolean selecionarTudo;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lista_albuns, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            estado = bundle.getString("estado");
        }

        listaAlbunsSeleccionados = new ArrayList<>();
        Button botaoConfirmarAlbuns = (Button) view.findViewById(R.id.botaoConfirmarAlbuns);

        TextView cabecalho = (TextView) view.findViewById(R.id.cabecalho);

        if (estado.equals("SemLigacao"))
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set out of reach mode");


        if (estado.equals("Partilha"))
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set sharing mode");

        if (estado.equals("QuickLaunch"))
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set quick launch");

        cabecalho.append("Hide photo albums");

        preferenciasAlbunsFotografias = getActivity().getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, Context.MODE_PRIVATE);
        conjuntoPreferenciasAlbunsFotografias = preferenciasAlbunsFotografias.getAll();

        criaListaAlbunsFotografias();

        botaoConfirmarAlbuns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferencias = getActivity().getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = getActivity().getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, Context.MODE_PRIVATE).edit();

                int numeroAlbunsEscolhidos = 0;

                for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                    if (entry.getKey().contains("preferenciaAlbunsFotografias")) {
                        numeroAlbunsEscolhidos++;
                    }
                }

                for (int i = 0; i < listaAlbuns.size(); i++) {
                    if (!listaAlbuns.get(i).estaSeleccionado()) {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().toString().equals(listaAlbuns.get(i).devolveNomePacote())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                            }
                        }
                    }
                }

                for (int i = numeroAlbunsEscolhidos; i < numeroAlbunsEscolhidos + listaAlbunsSeleccionados.size(); i++) {
                    for (int j = 0; j < listaAlbuns.size(); j++) {
                        if (listaAlbunsSeleccionados.get(i - numeroAlbunsEscolhidos).contains(listaAlbuns.get(j).devolveNomePacote())) {
                            editor.putString("preferenciaAlbunsFotografias" + j, listaAlbunsSeleccionados.get(i - numeroAlbunsEscolhidos));
                            editor.commit();
                        }
                    }
                }
                if (!estado.equals("QuickLaunch")) {

                    SharedPreferences posicoes = getActivity().getApplicationContext().getSharedPreferences("posicoes" + ConfiguracoesRespostas.estado, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorPosicoes = posicoes.edit();
                    editorPosicoes.putString("posicao" + ConfiguracoesRespostas.estado + String.valueOf(ConfiguracoesRespostas.numeroRespostas), ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).devolveNomePacote());
                    editorPosicoes.commit();
                    ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).colocaSeleccionado(true);
                    ConfiguracoesRespostas.numeroRespostas++;
                    Fragment fragment = null;
                    Class fragmentClass;
                    fragmentClass = ConfiguracoesRespostas.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("estado", estado);
                        fragment.setArguments(bundle);
                    } catch (java.lang.InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                } else {
                    Intent a = new Intent(getActivity().getApplicationContext(), GestorAlbunsFotografias.class);
                    a.putExtra("estado", "QuickLaunch");
                    getActivity().startService(a);
                    Fragment fragment = null;
                    Class fragmentClass;
                    fragmentClass = ListaAtividadesSmartIDR.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("estado", estado);
                        fragment.setArguments(bundle);
                    } catch (java.lang.InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                }
                Toast.makeText(getActivity().getApplicationContext(), "✓ Álbuns de Fotografias Definidos!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void criaListaAlbunsFotografias() {
        ListView mainListView = (ListView) view.findViewById(R.id.listaAlbuns);
        listaAlbuns = new ArrayList<>();

        File pastaAlbunsUm = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_PICTURES + File.separator);
        File pastaAlbunsDois = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_DCIM + File.separator);

        if (pastaAlbunsUm.exists())
            listaAlbuns(pastaAlbunsUm);
        if (pastaAlbunsDois.exists())
            listaAlbuns(pastaAlbunsDois);

        final ArrayAdapter<ItemLista> adaptadorLista = new AdaptadorAlbuns(getActivity(), listaAlbuns);
        mainListView.setAdapter(adaptadorLista);
        Switch botaoSelecionarTudo = (Switch) view.findViewById(R.id.botaoSelecionarTudo);
        botaoSelecionarTudo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!selecionarTudo) {
                    listaAlbunsSeleccionados.clear();
                    for (int i = 0; i < listaAlbuns.size(); i++) {
                        listaAlbuns.get(i).colocaSeleccionado(true);
                        adaptadorLista.notifyDataSetChanged();
                        listaAlbunsSeleccionados.add(listaAlbuns.get(i).devolveNomePacote());
                    }
                    selecionarTudo = true;
                } else {
                    for (int i = 0; i < listaAlbuns.size(); i++) {
                        listaAlbuns.get(i).colocaSeleccionado(false);
                        adaptadorLista.notifyDataSetChanged();
                        listaAlbunsSeleccionados.remove(listaAlbuns.get(i).devolveNomePacote());
                    }
                    selecionarTudo = false;
                }
            }
        });
    }

    private boolean eFotografia(File ficheiro) {
        return (ficheiro.getName().contains(".jpg") || ficheiro.getName().contains(".jpeg") || ficheiro.getName().contains(".gif") || ficheiro.getName().contains(".png") ||
                ficheiro.getName().contains(".bmp") || ficheiro.getName().contains(".webP"));
    }

    private void listaAlbuns(File ficheiroAlbum) {
        for (File pastaAlbum : ficheiroAlbum.listFiles()) {
            if (pastaAlbum.isDirectory() && pastaAlbum.listFiles().length != 0 && !pastaAlbum.isHidden()) {
                for (int j = 0; j < pastaAlbum.listFiles().length; j++) {
                    if (eFotografia(pastaAlbum.listFiles()[j]) && !pastaAlbum.listFiles()[j].isHidden()) {
                        ItemLista album = new ItemLista();
                        album.atribuiNome(pastaAlbum.getName() + "\n" + numeroFotografiasAlbum(pastaAlbum));
                        album.atribuiNomePacote(pastaAlbum.getPath());
                        album.atribuiIcon(redimensionaFoto(Drawable.createFromPath(pastaAlbum.listFiles()[j].getPath())));
                        for (Map.Entry<String, ?> entry : conjuntoPreferenciasAlbunsFotografias.entrySet()) {
                            if (entry.getValue().equals(album.devolveNomePacote()))
                                album.colocaSeleccionado(true);
                        }
                        listaAlbuns.add(album);
                        break;
                    }
                }
            }
        }
    }

    private int numeroFotografiasAlbum(File album) {
        int numeroFotos = 0;
        for (File foto : album.listFiles()) {
            if (eFotografia(foto))
                numeroFotos++;
        }
        return numeroFotos;
    }

    private Drawable redimensionaFoto(Drawable foto) {
        Bitmap b = ((BitmapDrawable) foto).getBitmap();
        Bitmap imagemRedimensionada = Bitmap.createScaledBitmap(b, 220, 330, false);
        return new BitmapDrawable(getResources(), imagemRedimensionada);
    }

    private static class AlbumViewHolder {
        private CheckBox checkBox;
        private TextView nomeAlbum;
        private ImageView icon;

        public AlbumViewHolder(TextView nomeAlbum, ImageView icon, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.nomeAlbum = nomeAlbum;
            this.icon = icon;
        }

        public CheckBox devolveCheckBox() {
            return checkBox;
        }

        public TextView devolveNomeAlbum() {
            return nomeAlbum;
        }

        public ImageView devolveIcon() {
            return icon;
        }

    }

    private class AdaptadorAlbuns extends ArrayAdapter<ItemLista> {

        public AdaptadorAlbuns(Context context, List<ItemLista> listaAlbuns) {
            super(context, R.layout.item_lista_aplicacoes_albuns, listaAlbuns);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ItemLista album = this.getItem(position);
            TextView nomeAlbum;
            CheckBox checkBox;
            ImageView icon;

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_lista_aplicacoes_albuns, null);

                nomeAlbum = (TextView) convertView.findViewById(R.id.nome_album);
                checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                icon = (ImageView) convertView.findViewById(R.id.icon_album);

                convertView.setTag(new AlbumViewHolder(nomeAlbum, icon, checkBox));

                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        ItemLista album = (ItemLista) checkBox.getTag();
                        album.colocaSeleccionado(checkBox.isChecked());
                        if (album.estaSeleccionado())
                            listaAlbunsSeleccionados.add(album.devolveNomePacote());
                        else
                            listaAlbunsSeleccionados.remove(album.devolveNomePacote());
                    }
                });
            } else {
                AlbumViewHolder viewHolder = (AlbumViewHolder) convertView.getTag();
                checkBox = viewHolder.devolveCheckBox();
                nomeAlbum = viewHolder.devolveNomeAlbum();
                icon = viewHolder.devolveIcon();
            }

            checkBox.setTag(album);
            checkBox.setChecked(album.estaSeleccionado());
            nomeAlbum.setText(album.devolveNome());
            icon.setImageDrawable(album.devolveIcon());
            return convertView;
        }
    }

    /**@Override public void onBackPressed() {
    super.onBackPressed();
    SharedPreferences.Editor editor = preferenciasAlbunsFotografias.edit();
    for (Map.Entry<String, ?> entry : conjuntoPreferenciasAlbunsFotografias.entrySet()) {
    if (entry.getValue().equals(GestorAlbunsFotografias.class.getName())) {
    editor.remove(entry.getKey());
    editor.commit();
    }
    }
    SharedPreferences posicoes = getApplicationContext().getSharedPreferences("posicoes" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
    SharedPreferences.Editor editorPosicoes = posicoes.edit();
    for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
    if (entrada.getValue().equals(ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).devolveNomePacote())) {
    editorPosicoes.remove(entrada.getKey());
    editorPosicoes.commit();
    }
    }
    finish();
    }**/
}
