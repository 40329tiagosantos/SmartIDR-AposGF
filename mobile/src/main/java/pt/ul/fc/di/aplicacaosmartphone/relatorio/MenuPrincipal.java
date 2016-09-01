package pt.ul.fc.di.aplicacaosmartphone.relatorio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.List;

import pt.ul.fc.di.aplicacaosmartphone.configuracoes.ConfiguracoesAlbunsFotografias;
import pt.ul.fc.di.aplicacaosmartphone.configuracoes.ConfiguracoesAlbunsVideos;
import pt.ul.fc.di.aplicacaosmartphone.configuracoes.ConfiguracoesAplicacoes;
import pt.ul.fc.di.aplicacaosmartphone.configuracoes.ConfiguracoesMensagemTexto;
import pt.ul.fc.di.aplicacaosmartphone.configuracoes.ConfiguracoesQuickLaunch;
import pt.ul.fc.di.aplicacaosmartphone.configuracoes.ConfiguracoesRespostas;
import pt.ul.fc.di.aplicacaosmartphone.configuracoes.LimparPreferencias;
import pt.ul.fc.di.aplicacaosmartphone.configuracoes.ListaConfiguracoes;
import pt.ul.fc.di.aplicacaosmartphone.interfaceaplicacao.Instrucoes;
import pt.ul.fc.di.aplicacaosmartphone.respostas.AlarmeSonoro;
import pt.ul.fc.di.aplicacaosmartphone.respostas.Bloquear;
import pt.ul.fc.di.aplicacaosmartphone.respostas.PedirAutenticacao;

public class MenuPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConfiguracoesRespostas.OnFragmentInteractionListener, ListaAtividadesSmartIDR.OnFragmentInteractionListener, ConfiguracoesAplicacoes.OnFragmentInteractionListener, ListaAtividades.OnFragmentInteractionListener, ListaConfiguracoes.OnFragmentInteractionListener, ConfiguracoesQuickLaunch.OnFragmentInteractionListener, Instrucoes.OnFragmentInteractionListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        ConfiguracoesQuickLaunch.alterouConfig = true;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(null);
            navigationView.getMenu().getItem(0).setChecked(true);
        }


        Menu menu = navigationView.getMenu();
        final SubMenu subMenu = menu.addSubMenu("Quick Launch");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            String pos;

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                for (int i = 0; i < subMenu.size(); i++) {
                    if (subMenu.getItem(i).isChecked()) {
                        pos = subMenu.getItem(i).getTitle().toString();
                    }
                }
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (ConfiguracoesQuickLaunch.alterouConfig) {
                    subMenu.clear();
                    SharedPreferences preferencias = getApplicationContext().getSharedPreferences("preferenciasUtilizador" + "QuickLaunch", Context.MODE_PRIVATE);
                    PackageManager gestorPacotes = getPackageManager();
                    Intent atividadesRespostas = new Intent(Intent.ACTION_MAIN, null);
                    atividadesRespostas.addCategory("resposta.PLUGIN");
                    List<ResolveInfo> respostas = gestorPacotes.queryIntentActivities(atividadesRespostas, 0);
                    SharedPreferences editorPos = getApplicationContext().getSharedPreferences("preferenciasPos" + "QuickLaunch", Context.MODE_PRIVATE);
                    int o = 0;
                    for (int a = 0; a < editorPos.getAll().size(); a++) {
                        int itemQuickLaunch = editorPos.getInt(String.valueOf(a), 0);
                        o = itemQuickLaunch;
                    }

                    for (int i = 0; i <= o; i++) {

                        String itemQuickLaunch = preferencias.getString("preferenciaAplicacoes" + i, "");
                        if (!itemQuickLaunch.equals("")) {

                            for (ResolveInfo ri : respostas) {
                                if (ri.activityInfo.name.equals(itemQuickLaunch.substring(itemQuickLaunch.lastIndexOf(" ") + 1))) {
                                    MenuItem item = subMenu.add(R.id.aa, Menu.NONE, Menu.NONE, itemQuickLaunch.substring(0, itemQuickLaunch.lastIndexOf(" ")));
                                    item.setIcon(ri.activityInfo.loadIcon(gestorPacotes));
                                    if (pos != null) {
                                        if (pos.equals(itemQuickLaunch.lastIndexOf(" ")))
                                            item.setChecked(true);
                                    }
                                }
                            }
                        }
                    }
                    Intent servicosRespostas = new Intent(Intent.ACTION_MAIN, null);
                    servicosRespostas.addCategory("resposta.PLUGIN");
                    respostas = gestorPacotes.queryIntentServices(servicosRespostas, 0);
                    for (int i = 0; i <= o; i++) {
                        String itemQuickLaunch = preferencias.getString("preferenciaAplicacoes" + i, "");
                        for (ResolveInfo ri : respostas) {
                            if (ri.serviceInfo.name.equals(itemQuickLaunch.substring(itemQuickLaunch.lastIndexOf(" ") + 1))) {
                                MenuItem item = subMenu.add(R.id.aa, Menu.NONE, Menu.NONE, itemQuickLaunch.substring(0, itemQuickLaunch.lastIndexOf(" ")));
                                item.setIcon(ri.serviceInfo.loadIcon(gestorPacotes));
                                if (pos != null) {
                                    if (pos.equals(itemQuickLaunch.lastIndexOf(" ")))
                                        item.setChecked(true);
                                }
                            }
                        }
                    }

                    for (int i = 0, count = navigationView.getChildCount(); i < count; i++) {
                        final View child = navigationView.getChildAt(i);
                        if (child != null && child instanceof ListView) {
                            final ListView menuView = (ListView) child;
                            final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                            final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                            wrapped.notifyDataSetChanged();
                        }
                    }
                    subMenu.setGroupCheckable(R.id.aa, true, true);
                    ConfiguracoesQuickLaunch.alterouConfig = false;
                }
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        String estado = getIntent().getStringExtra("estado");
        if (estado != null) {
            if (estado.equals("Partilha")) {
                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = ConfiguracoesRespostas.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString("estado", "Partilha");
                    fragment.setArguments(bundle);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
            }
        } else {
            Fragment fragment = null;
            Class fragmentClass;
            fragmentClass = ListaAtividadesSmartIDR.class;

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.layoutSeparador);
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
            navigationView.getMenu().findItem(R.id.bb).getSubMenu().getItem(i).setChecked(false);
        }

        if (f instanceof ListaConfiguracoes) {
            Menu menuNav = navigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.setup);
            logoutItem.setChecked(true);
        } else if (f instanceof ConfiguracoesRespostas) {
            Menu menuNav = navigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.setup);
            logoutItem.setChecked(true);
        } else if (f instanceof ConfiguracoesAlbunsFotografias && !ConfiguracoesAlbunsFotografias.estado.equals("QuickLaunch")) {
            Menu menuNav = navigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.setup);
            logoutItem.setChecked(true);
        } else if (f instanceof ConfiguracoesAlbunsVideos && !ConfiguracoesAlbunsVideos.estado.equals("QuickLaunch")) {
            Menu menuNav = navigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.setup);
            logoutItem.setChecked(true);
        } /**else if (f instanceof ConfiguracoesAlbunsVideos && ConfiguracoesAlbunsVideos.estado.equals("QuickLaunch")) {
         Menu menuNav = navigationView.getMenu();
         for (int i = 0; i < menuNav.findItem(R.id.aa).getSubMenu().size(); i++) {
         if (menuNav.findItem(R.id.aa).getSubMenu().getItem(i).getTitle().toString().contains("video")) {
         MenuItem logoutItem = menuNav.findItem(R.id.aa).getSubMenu().getItem(i);
         logoutItem.setChecked(true);
         break;
         }
         }
         }**/
        else if (f instanceof ConfiguracoesAplicacoes && !ConfiguracoesAplicacoes.estado.equals("QuickLaunch")) {
            Menu menuNav = navigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.setup);
            logoutItem.setChecked(true);
        } else if (f instanceof ListaAtividades) {
            Menu menuNav = navigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.recentactivity);
            logoutItem.setChecked(true);
        } else if (f instanceof Instrucoes) {
            Menu menuNav = navigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.help);
            logoutItem.setChecked(true);
        } else if (f instanceof ListaAtividadesSmartIDR) {
            Menu menuNav = navigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(R.id.recentactivity);
            logoutItem.setChecked(true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String estado = intent.getStringExtra("estado");
        if (estado != null) {
            if (estado.equals("Partilha")) {
                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = ConfiguracoesRespostas.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString("estado", "Partilha");
                    fragment.setArguments(bundle);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        for (int i = 0; i < navigationView.getMenu().findItem(R.id.bb).getSubMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
            navigationView.getMenu().findItem(R.id.bb).getSubMenu().getItem(i).setChecked(false);
        }

        Fragment fragment = null;
        Class fragmentClass;
        switch (item.getItemId()) {
            case R.id.cleandata:
                Intent limparDados = new Intent(getApplicationContext(), LimparPreferencias.class);
                startService(limparDados);

                if (navigationView.getMenu().getItem(0).isChecked()) {
                    fragmentClass = ListaAtividadesSmartIDR.class;

                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
                    setTitle(item.getTitle());
                }
                ConfiguracoesQuickLaunch.alterouConfig = true;
                return true;
            case R.id.help:
                fragmentClass = Instrucoes.class;

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.recentactivity:
                fragmentClass = ListaAtividadesSmartIDR.class;

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.setup:
                fragmentClass = ListaConfiguracoes.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    Bundle bundle = new Bundle();
                    fragment.setArguments(bundle);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            /**  case R.id.sharing_mode:
             fragmentClass = ConfiguracoesRespostas.class;
             try {
             fragment = (Fragment) fragmentClass.newInstance();
             Bundle bundle = new Bundle();
             bundle.putString("estado", "Partilha");
             fragment.setArguments(bundle);
             } catch (InstantiationException | IllegalAccessException e) {
             e.printStackTrace();
             }
             break;**/
        }
        if (item.getTitle().toString().equals("Brick")) {
            Intent servico = new Intent(getApplicationContext(), Bloquear.class);
            servico.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            servico.putExtra("estado", "QuickLaunch");

            startService(servico);
        } else if (item.getTitle().toString().equals("Alarm")) {
            Intent servico = new Intent(getApplicationContext(), AlarmeSonoro.class);
            servico.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            servico.putExtra("estado", "QuickLaunch");
            startService(servico);
        } else if (item.getTitle().toString().equals("Lock")) {
            Intent servico = new Intent(getApplicationContext(), PedirAutenticacao.class);
            servico.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            servico.putExtra("estado", "QuickLaunch");
            startService(servico);
        }
        else if (item.getTitle().toString().equals("Logger")||item.getTitle().toString().contains("Face")) {
          /**  SharedPreferences.Editor preferenciasquickLaunch = getApplicationContext().getSharedPreferences("preferenciasUtilizadorQuickLaunch", MODE_PRIVATE).edit();
            preferenciasquickLaunch.putString("RegistoAtividades","RegistoAtividades");
            preferenciasquickLaunch.commit();**/
        } else if (item.getTitle().toString().contains("text")) {
            Intent servico = new Intent(getApplicationContext(), ConfiguracoesMensagemTexto.class);
            servico.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            servico.putExtra("estado", "QuickLaunch");
            startActivity(servico);
        } else if (item.getTitle().toString().contains("App")) {
            fragmentClass = ConfiguracoesAplicacoes.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("estado", "QuickLaunch");
                fragment.setArguments(bundle);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
            drawer.closeDrawers();
        } else if (item.getTitle().toString().contains("video")) {
            fragmentClass = ConfiguracoesAlbunsVideos.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("estado", "QuickLaunch");
                fragment.setArguments(bundle);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
            drawer.closeDrawers();
        } else if (item.getTitle().toString().contains("photo")) {
            fragmentClass = ConfiguracoesAlbunsFotografias.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("estado", "QuickLaunch");
                fragment.setArguments(bundle);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
            drawer.closeDrawers();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.layoutSeparador, fragment).addToBackStack(null).commit();
            item.setChecked(true);
            setTitle(item.getTitle());
            drawer.closeDrawers();
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
