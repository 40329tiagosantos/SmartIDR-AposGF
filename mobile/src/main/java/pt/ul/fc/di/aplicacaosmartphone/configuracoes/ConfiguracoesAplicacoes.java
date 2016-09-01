package pt.ul.fc.di.aplicacaosmartphone.configuracoes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemLista;
import pt.ul.fc.di.aplicacaosmartphone.relatorio.ListaAtividadesSmartIDR;

public class ConfiguracoesAplicacoes extends Fragment {

    private ArrayList<ItemLista> listaAplicacoes;
    private ArrayList<String> listaAplicacoesSeleccionadas;
    public static String estado;
    private boolean selecionarTudo;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lista_aplicacoes, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            estado = bundle.getString("estado");
        }

        criaListaAplicacoes();

        Button botaoConfirmarAplicacoes = (Button) view.findViewById(R.id.botaoConfirmarAplicacoes);
        listaAplicacoesSeleccionadas = new ArrayList<>();

        TextView cabecalho = (TextView) view.findViewById(R.id.cabecalho);

        if (estado.equals("SemLigacao"))
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set out of reach mode");


        if (estado.equals("Partilha"))
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set sharing mode");


        if (estado.equals("QuickLaunch"))
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set quick launch");

        cabecalho.append("App blocker");


        botaoConfirmarAplicacoes.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            SharedPreferences preferencias = getActivity().getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = getActivity().getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, Context.MODE_PRIVATE).edit();
                                                            int numeroAplicacoesEscolhidas = 0;

                                                            for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                                                                if (entry.getKey().contains("preferenciaAplicacoes")) {
                                                                    numeroAplicacoesEscolhidas++;
                                                                }
                                                            }

                                                            for (int i = 0; i < listaAplicacoes.size(); i++) {
                                                                if (!listaAplicacoes.get(i).estaSeleccionado()) {
                                                                    for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                                                                        if (entry.getValue().toString().equals(listaAplicacoes.get(i).devolveNome() + listaAplicacoes.get(i).devolveNomePacote())) {
                                                                            editor.remove(entry.getKey());
                                                                            editor.commit();
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            for (int i = numeroAplicacoesEscolhidas; i < numeroAplicacoesEscolhidas + listaAplicacoesSeleccionadas.size(); i++) {
                                                                for (int j = 0; j < listaAplicacoes.size(); j++) {
                                                                    if (listaAplicacoesSeleccionadas.get(i - numeroAplicacoesEscolhidas).equals(listaAplicacoes.get(j).devolveNome() + listaAplicacoes.get(j).devolveNomePacote())) {
                                                                        editor.putString("preferenciaAplicacoes" + j, listaAplicacoesSeleccionadas.get(i - numeroAplicacoesEscolhidas));
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
                                                                fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).addToBackStack(null).commit();
                                                            } else {
                                                                editor.putString("DesativarAplicacoes","DesativarAplicacoes");
                                                                editor.commit();
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
                                                                fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).addToBackStack(null).commit();
                                                            }
                                                            Toast.makeText(getActivity().getApplicationContext(), "✓ Aplicações Definidas!", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
        );
        return view;
    }

    private void criaListaAplicacoes() {
        ListView mainListView = (ListView) view.findViewById(R.id.listaAplicacoes);
        listaAplicacoes = new ArrayList<>();

        PackageManager gestorPacotes = getActivity().getPackageManager();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = gestorPacotes.queryIntentActivities(i, 0);
        ItemLista aplicacao;
        SharedPreferences preferencias = getActivity().getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, Context.MODE_PRIVATE);
        Set<? extends Map.Entry<String, ?>> conjuntoPreferencias = preferencias.getAll().entrySet();

        for (ResolveInfo ri : availableActivities) {
            aplicacao = new ItemLista();
            aplicacao.atribuiNome((String) ri.loadLabel(gestorPacotes));
            aplicacao.atribuiNomePacote(ri.activityInfo.packageName);
            aplicacao.atribuiIcon(ri.activityInfo.loadIcon(gestorPacotes));
            for (Map.Entry<String, ?> entry : conjuntoPreferencias) {
                if (entry.getValue().equals(aplicacao.devolveNome() + aplicacao.devolveNomePacote())) {
                    aplicacao.colocaSeleccionado(true);
                }
            }
            listaAplicacoes.add(aplicacao);
        }

        final ArrayAdapter<ItemLista> adaptadorLista = new AdaptadorAplicacoes(getActivity(), listaAplicacoes);
        mainListView.setAdapter(adaptadorLista);

        Switch botaoSelecionarTudo = (Switch) view.findViewById(R.id.botaoSelecionarTudo);
        botaoSelecionarTudo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!selecionarTudo) {
                    listaAplicacoesSeleccionadas.clear();
                    for (int i = 0; i < listaAplicacoes.size(); i++) {
                        listaAplicacoes.get(i).colocaSeleccionado(true);
                        adaptadorLista.notifyDataSetChanged();
                        listaAplicacoesSeleccionadas.add(listaAplicacoes.get(i).devolveNome() + listaAplicacoes.get(i).devolveNomePacote());
                    }
                    selecionarTudo = true;
                } else {
                    for (int i = 0; i < listaAplicacoes.size(); i++) {
                        listaAplicacoes.get(i).colocaSeleccionado(false);
                        adaptadorLista.notifyDataSetChanged();
                        listaAplicacoesSeleccionadas.remove(listaAplicacoes.get(i).devolveNome() + listaAplicacoes.get(i).devolveNomePacote());
                    }
                    selecionarTudo = false;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private static class AplicacaoViewHolder {

        private CheckBox checkBox;
        private TextView nomeAplicacao;
        private ImageView icon;

        public AplicacaoViewHolder(TextView nomeAplicacao, ImageView icon, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.nomeAplicacao = nomeAplicacao;
            this.icon = icon;
        }

        public CheckBox devolveCheckBox() {
            return checkBox;
        }

        public TextView devolveNomeAplicacao() {
            return nomeAplicacao;
        }

        public ImageView devolveIcon() {
            return icon;
        }
    }

    private class AdaptadorAplicacoes extends ArrayAdapter<ItemLista> {

        public AdaptadorAplicacoes(Context context, List<ItemLista> listaAplicacoes) {
            super(context, R.layout.item_lista_aplicacoes_albuns, listaAplicacoes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ItemLista aplicacao = this.getItem(position);
            TextView nomeAplicacao;
            CheckBox checkBox;
            ImageView icon;

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_lista_aplicacoes_albuns, null);

                nomeAplicacao = (TextView) convertView.findViewById(R.id.nome_aplicacao);
                checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                icon = (ImageView) convertView.findViewById(R.id.icon_aplicacao);

                convertView.setTag(new AplicacaoViewHolder(nomeAplicacao, icon, checkBox));

                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        ItemLista aplicacao = (ItemLista) checkBox.getTag();
                        aplicacao.colocaSeleccionado(checkBox.isChecked());
                        if (aplicacao.estaSeleccionado())
                            listaAplicacoesSeleccionadas.add(aplicacao.devolveNome() + aplicacao.devolveNomePacote());
                        else {
                            listaAplicacoesSeleccionadas.remove(aplicacao.devolveNome() + aplicacao.devolveNomePacote());
                        }
                    }
                });
            } else {
                AplicacaoViewHolder viewHolder = (AplicacaoViewHolder) convertView.getTag();
                checkBox = viewHolder.devolveCheckBox();
                nomeAplicacao = viewHolder.devolveNomeAplicacao();
                icon = viewHolder.devolveIcon();
            }

            checkBox.setTag(aplicacao);
            checkBox.setChecked(aplicacao.estaSeleccionado());
            nomeAplicacao.setText(aplicacao.devolveNome());
            icon.setImageDrawable(aplicacao.devolveIcon());
            return convertView;
        }
    }

    /**
     * @Override public void onBackPressed() {
     * super.onBackPressed();
     * SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
     * SharedPreferences.Editor editor = preferencias.edit();
     * for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
     * if (entry.getValue().equals(GestorAlbunsVideos.class.getName())) {
     * editor.remove(entry.getKey());
     * editor.commit();
     * }
     * if (entry.getValue().equals(DesativarAplicacoes.class.getName())) {
     * editor.remove(entry.getKey());
     * editor.commit();
     * }
     * }
     * SharedPreferences posicoes = getApplicationContext().getSharedPreferences("posicoes" + ConfiguracoesRespostas.estado, MODE_PRIVATE);
     * SharedPreferences.Editor editorPosicoes = posicoes.edit();
     * for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
     * if (entrada.getValue().equals(ConfiguracoesRespostas.listaRespostas.get(ConfiguracoesRespostas.posicao).devolveNomePacote())) {
     * editorPosicoes.remove(entrada.getKey());
     * editorPosicoes.commit();
     * }
     * }
     * finish();
     * }
     **/

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
