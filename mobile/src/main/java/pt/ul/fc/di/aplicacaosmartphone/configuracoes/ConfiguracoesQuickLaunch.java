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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemLista;
import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemListaComparator;

public class ConfiguracoesQuickLaunch extends Fragment {

    private ArrayList<ItemLista> listaAplicacoes;
    private ArrayList<String> listaAplicacoesSeleccionadas;
    private String estado;
    private boolean selecionarTudo;
    private View view;
    public static boolean alterouConfig;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lista_quick_launch, container, false);

        estado = "QuickLaunch";
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set quick launch");

        iniciaListaRespostas();
        criaListaAplicacoes();

        Button botaoConfirmarAplicacoes = (Button) view.findViewById(R.id.botaoConfirmarAplicacoes);
        listaAplicacoesSeleccionadas = new ArrayList<>();

        TextView cabecalho = (TextView) view.findViewById(R.id.cabecalho);


        cabecalho.append("Quick launch");


        botaoConfirmarAplicacoes.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            alterouConfig = true;
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
                                                                        if (entry.getValue().toString().equals(listaAplicacoes.get(i).devolveNome() + " " + listaAplicacoes.get(i).devolveNomePacote())) {
                                                                            editor.remove(entry.getKey());
                                                                            editor.commit();
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            int a = 0;
                                                            for (int i = numeroAplicacoesEscolhidas; i < numeroAplicacoesEscolhidas + listaAplicacoesSeleccionadas.size(); i++) {
                                                                for (int j = 0; j < listaAplicacoes.size(); j++) {
                                                                    if (listaAplicacoesSeleccionadas.get(i - numeroAplicacoesEscolhidas).contains(listaAplicacoes.get(j).devolveNome())) {
                                                                        editor.putString("preferenciaAplicacoes" + j, listaAplicacoesSeleccionadas.get(i - numeroAplicacoesEscolhidas));
                                                                        editor.commit();
                                                                        SharedPreferences.Editor editorPos = getActivity().getApplicationContext().getSharedPreferences("preferenciasPos" + estado, Context.MODE_PRIVATE).edit();
                                                                        editorPos.putInt(String.valueOf(a), j);
                                                                        editorPos.commit();
                                                                        a++;
                                                                    }
                                                                }
                                                            }

                                                            Toast.makeText(getActivity().getApplicationContext(), "✓ Quick launch!", Toast.LENGTH_SHORT).show();
                                                            Fragment fragment = null;
                                                            Class fragmentClass;
                                                            fragmentClass = ListaConfiguracoes.class;
                                                            try {
                                                                fragment = (Fragment) fragmentClass.newInstance();
                                                            } catch (java.lang.InstantiationException | IllegalAccessException e) {
                                                                e.printStackTrace();
                                                            }
                                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                            fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                                                        }
                                                    }

        );
        return view;
    }

    private void criaListaAplicacoes() {
        ListView mainListView = (ListView) view.findViewById(R.id.listaAplicacoes);
        if (listaAplicacoes == null)
            listaAplicacoes = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

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
                        listaAplicacoesSeleccionadas.add(listaAplicacoes.get(i).devolveNome() + " " + listaAplicacoes.get(i).devolveNomePacote());
                    }
                    selecionarTudo = true;
                } else {
                    for (int i = 0; i < listaAplicacoes.size(); i++) {
                        listaAplicacoes.get(i).colocaSeleccionado(false);
                        adaptadorLista.notifyDataSetChanged();
                        listaAplicacoesSeleccionadas.remove(listaAplicacoes.get(i).devolveNome() + " " + listaAplicacoes.get(i).devolveNomePacote());
                    }
                    selecionarTudo = false;
                }
            }
        });
    }

    private void iniciaListaRespostas() {
        listaAplicacoes = new ArrayList<>();
        PackageManager gestorPacotes = getActivity().getPackageManager();
        Intent atividadesRespostas = new Intent(Intent.ACTION_MAIN, null);
        atividadesRespostas.addCategory("resposta.PLUGIN");
        List<ResolveInfo> respostas = gestorPacotes.queryIntentActivities(atividadesRespostas, 0);
        SharedPreferences preferencias = getActivity().getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, Context.MODE_PRIVATE);
        Set<? extends Map.Entry<String, ?>> conjuntoPreferencias = preferencias.getAll().entrySet();

        for (ResolveInfo ri : respostas) {
            ItemLista resposta = new ItemLista();
            resposta.atribuiNome((String) ri.loadLabel(gestorPacotes));
            resposta.atribuiNomePacote(ri.activityInfo.name);
            resposta.atribuiIcon(ri.activityInfo.loadIcon(gestorPacotes));
            resposta.colocaConfiguracao(true);
            for (Map.Entry<String, ?> entry : conjuntoPreferencias) {
                if (entry.getValue().toString().contains(resposta.devolveNome())) {
                    resposta.colocaSeleccionado(true);
                }
            }
            listaAplicacoes.add(resposta);
        }
        Intent servicosRespostas = new Intent(Intent.ACTION_MAIN, null);
        servicosRespostas.addCategory("resposta.PLUGIN");
        respostas = gestorPacotes.queryIntentServices(servicosRespostas, 0);
        for (ResolveInfo ri : respostas) {
            ItemLista resposta = new ItemLista();
            resposta.atribuiNome((String) ri.loadLabel(gestorPacotes));
            resposta.atribuiNomePacote(ri.serviceInfo.name);
            resposta.atribuiIcon(ri.serviceInfo.loadIcon(gestorPacotes));
            for (Map.Entry<String, ?> entry : conjuntoPreferencias) {
                if (entry.getValue().toString().contains(resposta.devolveNome())) {
                    resposta.colocaSeleccionado(true);
                }
            }
            listaAplicacoes.add(resposta);
        }
        Collections.sort(listaAplicacoes, new ItemListaComparator());
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
            super(context, R.layout.item_lista_quick_launcher, listaAplicacoes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ItemLista aplicacao = this.getItem(position);
            TextView nomeAplicacao;
            CheckBox checkBox;
            ImageView icon;

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_lista_quick_launcher, null);

                nomeAplicacao = (TextView) convertView.findViewById(R.id.nome_aplicacao);
                checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                icon = (ImageView) convertView.findViewById(R.id.icon_aplicacao);

                convertView.setTag(new AplicacaoViewHolder(nomeAplicacao, icon, checkBox));

                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        ItemLista aplicacao = (ItemLista) checkBox.getTag();
                        aplicacao.colocaSeleccionado(checkBox.isChecked());
                        if (aplicacao.estaSeleccionado()) {
                            listaAplicacoesSeleccionadas.add(aplicacao.devolveNome() + " " + aplicacao.devolveNomePacote());
                        } else {
                            listaAplicacoesSeleccionadas.remove(aplicacao.devolveNome() + " " + aplicacao.devolveNomePacote());
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
