package pt.ul.fc.di.aplicacaosmartphone.configuracoes;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.ArrayList;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemLista;

public class ListaConfiguracoes extends Fragment {

    private ListView vistaListaRespostas;
    public static String estado;
    public static ArrayList<ItemLista> listaRespostas;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lista_respostas, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Setup");;

        iniciaListaRespostas();
        criaListaRespostas();
        adicionaClickListener();

        return view;
    }

    private void iniciaListaRespostas() {
        listaRespostas = new ArrayList<>();
        ItemLista resposta = new ItemLista();
        resposta.atribuiNome("Set out of reach mode");
        resposta.atribuiDescricao("\n\nChoose what happens when your watch disconnects from your phone.");
        resposta.colocaConfiguracao(true);
        resposta.atribuiIcon(ContextCompat.getDrawable(getActivity(),R.drawable.iconsemligacao));
        listaRespostas.add(resposta);
        resposta = new ItemLista();
        resposta.atribuiNome("Set sharing mode");
        resposta.atribuiDescricao("\nChoose what happens when you activate the sharing mode.");
        resposta.colocaConfiguracao(true);
        resposta.atribuiIcon(ContextCompat.getDrawable(getActivity(),R.drawable.iconpartilha));
        listaRespostas.add(resposta);
        resposta = new ItemLista();
        resposta.atribuiNome("Set quick launch");
        resposta.atribuiDescricao("\n\nChoose which defenses you can quick launch from your phone.");
        resposta.colocaConfiguracao(true);
        resposta.atribuiIcon(ContextCompat.getDrawable(getActivity(),R.drawable.iconconfiguracao));
        listaRespostas.add(resposta);
    }

    private void criaListaRespostas() {
            vistaListaRespostas = (ListView) view.findViewById(R.id.listaRespostas);
        Parcelable state = vistaListaRespostas.onSaveInstanceState();
        ArrayAdapter<ItemLista> adaptador = new ArrayAdapter<ItemLista>(getActivity(), R.layout.item_lista_respostas_registos, listaRespostas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.item_lista_respostas_registos, null);
                ImageView iconResposta = (ImageView) convertView.findViewById(R.id.icon_resposta);
                iconResposta.setImageDrawable(listaRespostas.get(position).devolveIcon());
                TextView nomeResposta = (TextView) convertView.findViewById(R.id.nome_resposta);
                nomeResposta.setText(listaRespostas.get(position).devolveNome());
                TextView descricao = (TextView) convertView.findViewById(R.id.descricao);
                descricao.setText(listaRespostas.get(position).devolveDescricaco());

                TextView configuracao = (TextView) convertView.findViewById(R.id.configuracao);

                configuracao.setVisibility(View.VISIBLE);

                return convertView;
            }
        };
        vistaListaRespostas.setAdapter(adaptador);
    }

    private void adicionaClickListener() {
        vistaListaRespostas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                if(pos==0){
                    Class fragmentClass;
                    Fragment fragment = null;
                    fragmentClass = ConfiguracoesRespostas.class;
                    try {
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (java.lang.InstantiationException e) {
                            e.printStackTrace();
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("estado", "SemLigacao");
                        fragment.setArguments(bundle);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                }
                if(pos==1){
                    Class fragmentClass;
                    Fragment fragment = null;
                    fragmentClass = ConfiguracoesRespostas.class;
                    try {
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (java.lang.InstantiationException e) {
                            e.printStackTrace();
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("estado", "Partilha");
                        fragment.setArguments(bundle);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                }
                if(pos==2){
                    Class fragmentClass;
                    Fragment fragment = null;
                    fragmentClass = ConfiguracoesQuickLaunch.class;
                    try {
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (java.lang.InstantiationException e) {
                            e.printStackTrace();
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("estado", "Partilha");
                        fragment.setArguments(bundle);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                }
            }
        });
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