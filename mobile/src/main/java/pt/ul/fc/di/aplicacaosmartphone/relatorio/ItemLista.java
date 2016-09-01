package pt.ul.fc.di.aplicacaosmartphone.relatorio;

import android.graphics.drawable.Drawable;

public class ItemLista{

    private String nome;
    private String nomePacote;
    private String descricao;
    private Drawable icon;
    private boolean seleccionado;
    private boolean configuracao;
    private long data;

    public ItemLista() {
    }

    public String devolveNome() {
        return nome;
    }

    public void atribuiNome(String nomeAplicacao) {
        this.nome = nomeAplicacao;
    }

    public long devolveData() {
        return data;
    }

    public void atribuiData(long data) {
        this.data = data;
    }

    public String devolveNomePacote() {
        return nomePacote;
    }

    public void atribuiNomePacote(String nomePacote) {
        this.nomePacote = nomePacote;
    }

    public boolean estaSeleccionado() {
        return seleccionado;
    }

    public void colocaSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public void colocaConfiguracao(boolean configuracao) {
        this.configuracao = configuracao;
    }

    public boolean temConfiguracao() {
        return configuracao;
    }

    public void atribuiIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable devolveIcon() {
        return icon;
    }

    public String devolveDescricaco() {
        return descricao;
    }

    public void atribuiDescricao(String descricao) {
        this.descricao = descricao;
    }

}

