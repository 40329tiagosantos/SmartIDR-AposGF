package pt.ul.fc.di.aplicacaosmartphone.interfaceaplicacao;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tiasa.aplicacaosmartwatch.R;

public class Instrucoes extends Fragment  {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_instrucoes, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Help");

        TextView textoInstrucoes = (TextView) view.findViewById(R.id.textoinstrucoes);
        String texto = (
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "..............................................................." +
                "");
        textoInstrucoes.setMovementMethod(new ScrollingMovementMethod());
        textoInstrucoes.setTextSize(20);
        textoInstrucoes.setText(texto);return view;
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