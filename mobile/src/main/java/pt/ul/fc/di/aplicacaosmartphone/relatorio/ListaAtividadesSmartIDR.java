package pt.ul.fc.di.aplicacaosmartphone.relatorio;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
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

import com.example.tiasa.aplicacaosmartwatch.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import pt.ul.fc.di.aplicacaosmartphone.configuracoes.LimparPreferencias;
import pt.ul.fc.di.aplicacaosmartphone.respostas.DeviceAdminReceiver;

public class ListaAtividadesSmartIDR extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<ItemLista> listaAtividades;
    private ListView vistaListaAtividades;
    private ArrayAdapter<ItemLista> adaptador;
    private static final int ACTIVATION_REQUEST = 47;
    public static boolean iniciouListaSmartIDR;
    private View view;
    private TextView textoSemMensagens;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lista_atividades, container, false);

        iniciouListaSmartIDR = true;
        ComponentName demoDeviceAdmin = new ComponentName(getActivity(), DeviceAdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, demoDeviceAdmin);
        startActivityForResult(intent, ACTIVATION_REQUEST);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("SmartIDR activity");

        vistaListaAtividades = (ListView) view.findViewById(R.id.lista);
        try {
            apresentaLista();
        } catch (IOException e) {
            e.printStackTrace();
        }
        criaListaRegistoAtividades();
        adicionaClickListener();
        return view;
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
        adaptador.notifyDataSetChanged();
        if (listaAtividades.size() == 0) {
            textoSemMensagens = (TextView) view.findViewById(R.id.textoSemAtividades);
            textoSemMensagens.setVisibility(View.VISIBLE);
        }
        NavigationView nav = (NavigationView) getActivity().findViewById(R.id.nav_view);
        int size = nav.getMenu().size();
        for (int i = 0; i < size; i++) {
            nav.getMenu().getItem(i).setChecked(false);
        }
        Menu menuNav = nav.getMenu();
        MenuItem logoutItem = menuNav.findItem(R.id.recentactivity);
        logoutItem.setChecked(true);
    }

    private void apresentaLista() throws IOException {
        if (listaAtividades == null)
            listaAtividades = new ArrayList<ItemLista>();

        InputStream inputStream = null;
        try {
            inputStream = getActivity().openFileInput("atividadesSmartIDR.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String linhaFicheiro = "";


            try {
                while ((linhaFicheiro = bufferedReader.readLine()) != null) {
                    long agora = System.currentTimeMillis();
                    long passados = Long.parseLong(linhaFicheiro.substring(0, linhaFicheiro.indexOf(" ")));
                    linhaFicheiro = linhaFicheiro.replace(linhaFicheiro.substring(0, linhaFicheiro.indexOf(" ")), "");
                    linhaFicheiro = linhaFicheiro.replaceFirst(" ", "");
                    long segundosPassados = agora - (agora - passados);
                    String tempoDecorrido = (DateUtils.getRelativeDateTimeString(getActivity().getApplicationContext(), segundosPassados, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_SHOW_TIME)).toString();
                    ItemLista registo = new ItemLista();

                    if (linhaFicheiro.contains("Remote") && !linhaFicheiro.contains("Stop Monitoring") && !linhaFicheiro.contains("Start Monitoring")) {
                        registo.atribuiData(passados);
                        registo.atribuiNome(tempoDecorrido + "\n" + linhaFicheiro);
                        registo.atribuiIcon(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.iconprincipal));
                        listaAtividades.add(registo);
                    } else if (linhaFicheiro.contains("Automatic") && !linhaFicheiro.contains("Stop Monitoring") && !linhaFicheiro.contains("Start Monitoring")) {
                        registo.atribuiData(passados);
                        registo.atribuiNome(tempoDecorrido + "\n" + linhaFicheiro);
                        registo.atribuiIcon(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.iconautomaticaction));
                        listaAtividades.add(registo);
                    }
                    if (linhaFicheiro.contains("Stop Monitoring") || linhaFicheiro.contains("Start Monitoring")) {
                        registo.atribuiData(passados);
                        registo.atribuiNome(tempoDecorrido + "\n" + linhaFicheiro);
                        registo.atribuiIcon(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.iconatividades));
                        listaAtividades.add(registo);
                        registo.colocaConfiguracao(true);
                    }
                    textoSemMensagens = (TextView) view.findViewById(R.id.textoSemAtividades);
                    if (textoSemMensagens != null) {
                        textoSemMensagens.setVisibility(View.INVISIBLE);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            Collections.sort(listaAtividades, new ItemListaComparator());
            inputStream.close();
        }
    }

    private void criaListaRegistoAtividades() {
        adaptador = new ArrayAdapter<ItemLista>(getActivity(), R.layout.item_lista_atividades, listaAtividades) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.item_lista_atividades, null);
                TextView nomeAtividade = (TextView) convertView.findViewById(R.id.nome_atividade);
                nomeAtividade.setText(listaAtividades.get(position).devolveNome());
                ImageView iconAtividade = (ImageView) convertView.findViewById(R.id.icon_atividade);
                iconAtividade.setImageDrawable(listaAtividades.get(position).devolveIcon());
                TextView configuracao = (TextView) convertView.findViewById(R.id.configuracao);
                configuracao.setVisibility(View.VISIBLE);
                if (listaAtividades.get(position).temConfiguracao()) {
                    configuracao.setVisibility(View.VISIBLE);
                } else {
                    configuracao.setVisibility(View.INVISIBLE);
                }
                return convertView;
            }
        };
        vistaListaAtividades.setAdapter(adaptador);
    }

    private void adicionaClickListener() {
        vistaListaAtividades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                if (listaAtividades.get(pos).devolveNome().contains("Monitoring")) {
                    SharedPreferences editor = getActivity().getApplicationContext().getSharedPreferences("nome", Context.MODE_PRIVATE);
                    String nomeFicheiro = editor.getAll().get(String.valueOf(listaAtividades.get(pos).devolveData())).toString();

                    Fragment fragment = null;
                    Class fragmentClass;
                    fragmentClass = ListaAtividades.class;

                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (java.lang.InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("nomeFicheiro", nomeFicheiro);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iniciouListaSmartIDR = false;
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (item.getItemId()) {
            case R.id.cleandata:
                Intent limparDados = new Intent(getActivity(), LimparPreferencias.class);
                getActivity().startService(limparDados);
                return true;
            case R.id.recentactivity:
                fragmentClass = ListaAtividadesSmartIDR.class;

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (java.lang.InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
        item.setChecked(true);
        getActivity().setTitle(item.getTitle());
        return true;
    }

}
