package pt.ul.fc.di.aplicacaosmartphone.relatorio;

import java.util.Comparator;


public class ItemListaComparator implements Comparator<ItemLista> {


    public ItemListaComparator() {
    }

   /** public int compare(ItemLista itemLista, ItemLista outroItemLista) {
        return itemLista.devolveNome().compareTo(outroItemLista.devolveNome());
    }**/

    public int compare(ItemLista itemLista, ItemLista outroItemLista) {
        return Long.compare(itemLista.devolveData(),outroItemLista.devolveData());
    }
}

