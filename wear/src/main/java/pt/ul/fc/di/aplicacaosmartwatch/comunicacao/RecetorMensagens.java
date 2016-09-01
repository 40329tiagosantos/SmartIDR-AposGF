package pt.ul.fc.di.aplicacaosmartwatch.comunicacao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import com.example.tiasa.aplicacaosmartwatch.R;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.BloquearFragment;
import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.ConfiguracoesFragment;
import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.ConstroiFragmentos;
import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.DesativaAplicacaoFragment;
import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.ListaAtividades;
import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.ModoPartilhaControladaFragment;
import pt.ul.fc.di.aplicacaosmartwatch.interfaceaplicacao.PedirAutenticacaoFragment;

public class RecetorMensagens extends WearableListenerService {

    public static int numeroAtividades;

    @Override
    public void onMessageReceived(MessageEvent eventoMensagem) {
        String mensagem = eventoMensagem.getPath();

        if (mensagem.contains("Atividade: ")) {

            if (ListaAtividades.listaIcons == null)
                ListaAtividades.listaIcons = new ArrayList<>();
            if (ListaAtividades.listaAtividades == null)
                ListaAtividades.listaAtividades = new ArrayList<>();

            mensagem = mensagem.replace("Atividade: ", "");
            ListaAtividades.listaAtividades.add(mensagem);

            byte [] dadosMensagem = eventoMensagem.getData();
            Drawable icon = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(dadosMensagem, 0, dadosMensagem.length));
            ListaAtividades.listaIcons.add(0, icon);
            if (ListaAtividades.iniciouAtividade && ListaAtividades.iniciouAplicacao) {
                Intent listaAtividades = new Intent(getApplicationContext(), ConstroiFragmentos.class);
                listaAtividades.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                listaAtividades.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(listaAtividades);
            } else if (!ListaAtividades.iniciouAtividade) {
                numeroAtividades++;
                if (numeroAtividades == 1) {
                    apresentaMensagem(String.valueOf(numeroAtividades) + " new activity!");
                } else {
                    apresentaMensagem(String.valueOf(numeroAtividades) + " new activities!");
                }
            }
        }

        else if (mensagem.equals("Bloqueado")) {
            SharedPreferences preferencias = getApplicationContext().getSharedPreferences("NomeBotaoBloquear", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("NomeBotaoBloquear", "Unbrick");
            editor.apply();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    BloquearFragment.actionPage.setText("Unbrick");
                    BloquearFragment.actionPage.setImageResource(R.drawable.icondesbloquear);
                    BloquearFragment.progresso.setVisibility(View.INVISIBLE);
                    BloquearFragment.actionPage.getButton().setVisibility(View.VISIBLE);
                }
            });
        } else if (mensagem.equals("Desbloqueado")) {
            SharedPreferences preferencias = getApplicationContext().getSharedPreferences("NomeBotaoBloquear", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("NomeBotaoBloquear", "Brick");
            editor.apply();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    BloquearFragment.actionPage.setText("Brick");
                    BloquearFragment.actionPage.setImageResource(R.drawable.iconbloquear);
                    BloquearFragment.progresso.setVisibility(View.INVISIBLE);
                    BloquearFragment.actionPage.getButton().setVisibility(View.VISIBLE);
                }
            });
        }

        else if (mensagem.equals("PedidoAutenticacaoAtivado")) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    PedirAutenticacaoFragment.progresso.setVisibility(View.INVISIBLE);
                    PedirAutenticacaoFragment.actionPage.getButton().setVisibility(View.VISIBLE);
                }
            });
        }

        else if (mensagem.equals("DesativarAplicacao")) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    DesativaAplicacaoFragment.progresso.setVisibility(View.INVISIBLE);
                    DesativaAplicacaoFragment.actionPage.getButton().setVisibility(View.VISIBLE);
                }
            });
        }

        else if (mensagem.equals("ModoPartilhaControladaAtivado")) {
            SharedPreferences preferencias = getApplicationContext().getSharedPreferences("NomeBotaoPartilhaControlada", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("NomeBotaoPartilhaControlada", "Disable sharing mode");
            editor.commit();
            if (ModoPartilhaControladaFragment.progresso != null) {
                if (ModoPartilhaControladaFragment.progresso.getVisibility() == View.VISIBLE) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ModoPartilhaControladaFragment.actionPage.setText("Disable sharing mode");
                            ModoPartilhaControladaFragment.actionPage.setImageResource(R.drawable.icondesativarpartilha);
                            ModoPartilhaControladaFragment.progresso.setVisibility(View.INVISIBLE);
                            ModoPartilhaControladaFragment.actionPage.getButton().setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    long padrao[] = {0, 200, 200, 200};
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(padrao, -1);
                }
            } else {
                long padrao[] = {0, 200, 200, 200};
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(padrao, -1);
            }
        } else if (mensagem.equals("ModoPartilhaControladaDesativado")) {
            SharedPreferences preferencias = getApplicationContext().getSharedPreferences("NomeBotaoPartilhaControlada", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("NomeBotaoPartilhaControlada", "Enable sharing mode");
            editor.commit();
            if (ModoPartilhaControladaFragment.progresso != null) {
                if (ModoPartilhaControladaFragment.progresso.getVisibility() == View.VISIBLE) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ModoPartilhaControladaFragment.actionPage.setText("Enable sharing mode");
                            ModoPartilhaControladaFragment.actionPage.setImageResource(R.drawable.iconpartilha);
                            ModoPartilhaControladaFragment.progresso.setVisibility(View.INVISIBLE);
                            ModoPartilhaControladaFragment.actionPage.getButton().setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    long padrao[] = {0, 200, 200, 200};
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(padrao, -1);
                }
            } else {
                long padrao[] = {0, 200, 200, 200};
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(padrao, -1);
            }
        }
        else if (mensagem.equals("ModoPartilhaControladaNaoConfigurado")) {
            if (ModoPartilhaControladaFragment.progresso != null) {
                if (ModoPartilhaControladaFragment.progresso.getVisibility() == View.VISIBLE) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ModoPartilhaControladaFragment.progresso.setVisibility(View.INVISIBLE);
                            ModoPartilhaControladaFragment.actionPage.getButton().setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Setup Sharing Mode", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
        else if (mensagem.equals("AbrirConfiguracoes")) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ConfiguracoesFragment.progresso.setVisibility(View.INVISIBLE);
                    ConfiguracoesFragment.actionPage.getButton().setVisibility(View.VISIBLE);
                }
            });
        }
    }


    private void apresentaMensagem(final String mensagem) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Toast toast = Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 2500);
            }
        });
    }
}
