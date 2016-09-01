package pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;

import com.example.tiasa.aplicacaosmartwatch.R;

import java.util.ArrayList;

import pt.ul.fc.di.aplicacaosmartwatch.comunicacao.Mensagem;

public class ConstroiFragmentos extends Activity {

    private GridViewPager pager;
    private boolean eListaAtividades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicia_aplicacao);
        pager = (GridViewPager) findViewById(R.id.pager);

        ArrayList<Fragment> listaFragmentos = new ArrayList<>();

        if (getIntent().getStringExtra("timestamp") == null) {
            listaFragmentos.add(new ListaAtividades());
            eListaAtividades =true;
        }

        else
            listaFragmentos.add(new DetalheAtividade());

        listaFragmentos.add(new BloquearFragment());
        listaFragmentos.add(new PedirAutenticacaoFragment());
        listaFragmentos.add(new ModoPartilhaControladaFragment());
        listaFragmentos.add(new DesativaAplicacaoFragment());
        listaFragmentos.add(new ConfiguracoesFragment());

        pager.setAdapter(new GridPagerAdapter(getFragmentManager(), listaFragmentos));
        DotsPageIndicator pontosIndicacao = (DotsPageIndicator) findViewById(R.id.page_indicator);
        pontosIndicacao.setDotFadeWhenIdle(false);
        pontosIndicacao.setPager(pager);
    }


    @Override
    public void onResume() {
        super.onResume();
        pager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(eListaAtividades){
            Mensagem iniciaRegistarAtividades = new Mensagem("ParaRegistoAtividades", getApplication());
            iniciaRegistarAtividades.enviaMensagem();
        }
    }
}
