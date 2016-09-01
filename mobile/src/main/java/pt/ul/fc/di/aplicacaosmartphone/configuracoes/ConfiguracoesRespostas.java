package pt.ul.fc.di.aplicacaosmartphone.configuracoes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.ul.fc.di.aplicacaosmartphone.relatorio.ItemLista;
import pt.ul.fc.di.aplicacaosmartphone.respostas.DesativarAplicacoes;
import pt.ul.fc.di.aplicacaosmartphone.respostas.GestorAlbunsFotografias;
import pt.ul.fc.di.aplicacaosmartphone.respostas.GestorAlbunsVideos;
import pt.ul.fc.di.aplicacaosmartphone.respostas.RegistoAtividades;

public class ConfiguracoesRespostas extends Fragment {

    private boolean temConfiguracoes;
    private ListView vistaListaRespostas;
    public static String estado;
    public static int numeroRespostas;
    public static ArrayList<ItemLista> listaRespostas;
    public static int posicao;
    public static AdapterView<?> vistaAdaptador;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lista_respostas, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            estado = bundle.getString("estado");
        }

        TextView descricao = (TextView) view.findViewById(R.id.descricao);

        if (estado.equals("SemLigacao")) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set out of reach mode");
            descricao.setText("Choose what happens when your watch disconnects from your phone.");
        }

        if (estado.equals("Partilha")) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Set sharing mode");
            descricao.setText("Choose what happens when you activate the sharing mode.");
        }
        iniciaListaRespostas();
        criaListaRespostas();
        adicionaClickListener();
        return view;
    }

    private void iniciaListaRespostas() {
        listaRespostas = new ArrayList<>();
        PackageManager gestorPacotes = getActivity().getPackageManager();
        Intent atividadesRespostas = new Intent(Intent.ACTION_MAIN, null);
        atividadesRespostas.addCategory("resposta.PLUGIN");
        List<ResolveInfo> respostas = gestorPacotes.queryIntentActivities(atividadesRespostas, 0);
        for (ResolveInfo ri : respostas) {
            ItemLista resposta = new ItemLista();
            resposta.atribuiNome((String) ri.loadLabel(gestorPacotes));
            resposta.atribuiNomePacote(ri.activityInfo.name);
            resposta.atribuiIcon(ri.activityInfo.loadIcon(gestorPacotes));
            resposta.colocaSeleccionado(false);
            resposta.colocaConfiguracao(true);
            listaRespostas.add(resposta);
        }
        Intent servicosRespostas = new Intent(Intent.ACTION_MAIN, null);
        servicosRespostas.addCategory("resposta.PLUGIN");
        respostas = gestorPacotes.queryIntentServices(servicosRespostas, 0);
        for (ResolveInfo ri : respostas) {
            if ((!ri.loadLabel(gestorPacotes).toString().contains("Notificação") && estado.equals("SemLigacao")) || estado.equals("Partilha")) {
                ItemLista resposta = new ItemLista();
                resposta.atribuiNome((String) ri.loadLabel(gestorPacotes));
                resposta.atribuiNomePacote(ri.serviceInfo.name);
                resposta.atribuiIcon(ri.serviceInfo.loadIcon(gestorPacotes));
                resposta.colocaSeleccionado(false);
                listaRespostas.add(resposta);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView nav = (NavigationView) getActivity().findViewById(R.id.nav_view);
        int size = nav.getMenu().size();
        for (int i = 0; i < size; i++) {
            nav.getMenu().getItem(i).setChecked(false);
        }
        Menu menuNav = nav.getMenu();
        MenuItem logoutItem = menuNav.findItem(R.id.setup);
        logoutItem.setChecked(true);
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
                TextView configuracao = (TextView) convertView.findViewById(R.id.configuracao);

                if (listaRespostas.get(position).temConfiguracao())
                    configuracao.setVisibility(View.VISIBLE);

                else
                    configuracao.setVisibility(View.INVISIBLE);

                SharedPreferences posicoes = getActivity().getApplicationContext().getSharedPreferences("posicoes" + estado, Context.MODE_PRIVATE);

                if (listaRespostas.get(position).estaSeleccionado() || posicoes.getAll().containsValue(listaRespostas.get(position).devolveNomePacote())) {
                    nomeResposta.setTextSize(19);
                    nomeResposta.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimary));
                } else {
                    nomeResposta.setTextSize(14);
                    nomeResposta.setTextColor(Color.BLACK);
                }
                return convertView;
            }
        };
        vistaListaRespostas.setAdapter(adaptador);
        vistaListaRespostas.onRestoreInstanceState(state);
    }

    private void adicionaClickListener() {
        vistaListaRespostas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                SharedPreferences posicoes = getActivity().getApplicationContext().getSharedPreferences("posicoes" + estado, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorPosicoes = posicoes.edit();
                SharedPreferences preferencias = getActivity().getApplicationContext().getSharedPreferences("preferenciasUtilizador" + estado, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferencias.edit();
                vistaAdaptador = av;
                temConfiguracoes = true;
                posicao = pos;
                Intent resposta = null;
                try {
                    resposta = new Intent(getActivity().getApplicationContext(), Class.forName(listaRespostas.get(pos).devolveNomePacote()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (listaRespostas.get(pos).devolveNomePacote().contains("AtividadesIntruso")) {
                    if (!preferencias.getAll().containsValue(listaRespostas.get(pos).devolveNomePacote())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), RegistoAtividades.class.getName());
                        editor.commit();
                        editorPosicoes.putString("posicao" + estado + String.valueOf(numeroRespostas), listaRespostas.get(pos).devolveNomePacote());
                        editorPosicoes.commit();
                        numeroRespostas++;
                        temConfiguracoes = false;
                        listaRespostas.get(pos).colocaSeleccionado(true);
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                                temConfiguracoes = true;
                            }
                        }
                    }
                } else if (listaRespostas.get(pos).devolveNomePacote().contains("AlbunsFotografias")) {
                    if (!preferencias.getAll().containsValue(GestorAlbunsFotografias.class.getName())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), GestorAlbunsFotografias.class.getName());
                        editor.commit();
                        Fragment fragment = null;
                        Class fragmentClass;
                        try {
                            fragmentClass = Class.forName(listaRespostas.get(pos).devolveNomePacote());
                            fragment = (Fragment) fragmentClass.newInstance();
                            Bundle bundle = new Bundle();
                            if (estado.equals("Partilha"))
                                bundle.putString("estado", "Partilha");
                            else if (estado.equals("SemLigacao"))
                                bundle.putString("estado", "SemLigacao");

                            fragment.setArguments(bundle);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (java.lang.InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(GestorAlbunsFotografias.class.getName())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                            }
                        }
                    }
                } else if (listaRespostas.get(pos).devolveNomePacote().contains("AlbunsVideos")) {
                    if (!preferencias.getAll().containsValue(GestorAlbunsVideos.class.getName())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), GestorAlbunsVideos.class.getName());
                        editor.commit();
                        Fragment fragment = null;
                        Class fragmentClass;
                        try {
                            fragmentClass = Class.forName(listaRespostas.get(pos).devolveNomePacote());
                            fragment = (Fragment) fragmentClass.newInstance();
                            Bundle bundle = new Bundle();
                            if (estado.equals("Partilha"))
                                bundle.putString("estado", "Partilha");
                            else if (estado.equals("SemLigacao"))
                                bundle.putString("estado", "SemLigacao");

                            fragment.setArguments(bundle);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (java.lang.InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(GestorAlbunsVideos.class.getName())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                            }
                        }
                    }
                } else if (listaRespostas.get(pos).devolveNomePacote().contains("Aplicacoes")) {
                    if (!preferencias.getAll().containsValue(DesativarAplicacoes.class.getName())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), DesativarAplicacoes.class.getName());
                        editor.commit();
                        Fragment fragment = null;
                        Class fragmentClass;
                        try {
                            fragmentClass = Class.forName(listaRespostas.get(pos).devolveNomePacote());
                            fragment = (Fragment) fragmentClass.newInstance();
                            Bundle bundle = new Bundle();
                            if (estado.equals("Partilha"))
                                bundle.putString("estado", "Partilha");
                            else if (estado.equals("SemLigacao"))
                                bundle.putString("estado", "SemLigacao");

                            fragment.setArguments(bundle);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (java.lang.InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(DesativarAplicacoes.class.getName())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                            }
                        }
                    }


                } else if (listaRespostas.get(pos).devolveNomePacote().contains("ConfiguracoesMensagemTexto")) {
                    if (!preferencias.getAll().containsValue(ConfiguracoesMensagemTexto.class.getName())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), ConfiguracoesMensagemTexto.class.getName());
                        editor.commit();
                        resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resposta.putExtra("estado", estado);
                        startActivity(resposta);
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(ConfiguracoesMensagemTexto.class.getName())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                            }
                        }
                    }
                } else {
                    if (!preferencias.getAll().containsValue(listaRespostas.get(pos).devolveNomePacote())) {
                        editor.putString("resposta" + String.valueOf(numeroRespostas), listaRespostas.get(pos).devolveNomePacote());
                        editor.commit();
                        editorPosicoes.putString("posicao" + estado + String.valueOf(numeroRespostas), listaRespostas.get(pos).devolveNomePacote());
                        editorPosicoes.commit();
                        numeroRespostas++;
                        listaRespostas.get(pos).colocaSeleccionado(true);
                        temConfiguracoes = false;
                    } else {
                        for (Map.Entry<String, ?> entry : preferencias.getAll().entrySet()) {
                            if (entry.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                editor.remove(entry.getKey());
                                editor.commit();
                                for (Map.Entry<String, ?> entrada : posicoes.getAll().entrySet()) {
                                    if (entrada.getValue().equals(listaRespostas.get(pos).devolveNomePacote())) {
                                        editorPosicoes.remove(entrada.getKey());
                                        editorPosicoes.commit();
                                    }
                                }
                                numeroRespostas--;
                                listaRespostas.get(pos).colocaSeleccionado(false);
                                temConfiguracoes = true;
                            }
                        }
                    }
                }
                if (!temConfiguracoes)
                    Toast.makeText(getActivity(), "✓ Resposta " + listaRespostas.get(pos).devolveNome() + " Definida!", Toast.LENGTH_SHORT).show();
                criaListaRespostas();
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