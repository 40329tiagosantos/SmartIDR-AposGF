package pt.ul.fc.di.aplicacaosmartphone.interfaceaplicacao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.tiasa.aplicacaosmartwatch.R;

public class MenuInstruccoes extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private float valorUltimoX;
    private ImageView deslizar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_instruccoes);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Bem-Vindo");
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        deslizar = (ImageView) findViewById(R.id.imageView5);
        textView = (TextView) findViewById(R.id.textView2);
        textView.setText("Esta aplicação fornece um sistema que permite detetar e responder a intrusões em" +
                " dispositivos móveis.");
        textView = (TextView) findViewById(R.id.textView4);
        textView.setText("Poderá configurar no Menu - Configurações as respostas que pretende ativar automaticamente, caso seja detetada " +
                "uma intrusão.\nAs respostas, também, poderão ser ativadas, através da aplicação no seu smartwatch.");
        textView = (TextView) findViewById(R.id.textView5);
        textView.setText("Posteriormente, poderá visualizar no Menu - Exibir Dados Intrusos os dados recolhidos durante a intrusão.");
        deslizar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconpontoa));
        deslizar.invalidate();
    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                valorUltimoX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float atualX = touchevent.getX();

                if (valorUltimoX < atualX) {
                    if (viewFlipper.getDisplayedChild() == 0) {
                        break;
                    } else if (viewFlipper.getDisplayedChild() == 1) {
                        viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
                        viewFlipper.showNext();
                        deslizar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconpontob));
                        deslizar.invalidate();
                    } else if (viewFlipper.getDisplayedChild() == 2) {
                        viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
                        viewFlipper.showNext();
                        deslizar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconpontoa));
                        deslizar.invalidate();
                    } else if (viewFlipper.getDisplayedChild() == 3) {
                        viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
                        viewFlipper.showNext();
                        deslizar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconpontoc));
                        deslizar.invalidate();
                    }
                }

                if (valorUltimoX > atualX) {

                    if (viewFlipper.getDisplayedChild() == 1) {
                        Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;
                    } else if (viewFlipper.getDisplayedChild() == 0) {
                        viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
                        viewFlipper.showPrevious();
                        deslizar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconpontob));
                        deslizar.invalidate();
                    } else if (viewFlipper.getDisplayedChild() == 2) {
                        viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
                        viewFlipper.showPrevious();
                        deslizar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.iconpontoc));
                        deslizar.invalidate();
                    }

                }
                break;
        }
        return false;
    }
}
