package com.personaltools.renan3m.personaloffice.Activities;

/**
 * Created by renan on 22/02/2018.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class SectionedAdapter extends BaseAdapter {

    /* SectionedAdapter contém uma Lista de objetos de Seção, onde uma Seção é simplesmente um nome e um Adaptador que contém
       o conteúdo dessa seção da lista. Você pode dar a SectionedAdapter os detalhes de uma Seção via addSection () - as seções
       aparecerão na ordem em que foram adicionadas. */

    class Section {
        String caption;
        Adapter adapter;

        Section(String caption, Adapter adapter) {
            this.caption = caption; // Titulo
            this.adapter = adapter; // Conteudo
        }
    }

    abstract protected View getHeaderView(String caption, int index,
                                          View convertView, ViewGroup parent);

    private List<Section> sections = new ArrayList<Section>(); // Uma Section nada mais é do que um Adapter (conteudo) e um nome.

    private static int TYPE_SECTION_HEADER = 0;

    public SectionedAdapter() {
        super();
    }

    public void addSection(String caption, Adapter adapter) {
        sections.add(new Section(caption, adapter));
    }

    public Object getItem(int position) {
        for (Section section : this.sections) {
            if (position == 0) {
                return (section); // retorna o primeiro section
            }
            int size = section.adapter.getCount() + 1; // +1 p/ poder usar o operador position < size em vez de <=.
            if (position < size) {
                return (section.adapter.getItem(position - 1));
            }
            position -= size; // Se a posição extrapolar o tamanho da lista, você faz
            // como que em um circulo para pegar o quadrante de um angulo,
            // até que o angulo (posição) seja menor que o tamanho total.
        }

        return (null);
    }

    public int getCount() {
        int total = 0;
        for (Section section : this.sections) {
            total += section.adapter.getCount() + 1; // adiciona 1 devido ao Header de cada seção. (caption ñ está no data set.
        }

        return (total); // Total de itens de todas as seções.
    }

    /* Both getViewTypeCount() and getItemViewType() are being used by BaseAdapter's getView method to find out which type of a view
    should it be fetch, recycled and returned. (as explained in the top answer within the thread). But if you don't implement these
    two methods intuitively according to the Android API Doc, then you might get into the problem I mentioned about.

    Summarized Guideline for the implementation:

    To implement multiple types of Views for ListView's rows we have to essentially implement, getItemViewType() and getViewTypeCount()
    methods. And getItemViewType() documentation gives us a Note as follows: Note: Integers must be in the range 0 to getViewTypeCount()
    - 1. IGNORE_ITEM_VIEW_TYPE can also be returned. So in your getItemViewType() you should return values for the View Type, starting
    from 0, to the last type as (number of types - 1). For example, let's say you only have three types of views? So depending on the
    data object for the view, you could only return 0 or 1 or 2 from the getItemViewType() method, like a zero-based array index. And
    as you have three types of views used, your getViewTypeCount() method must return 3. */

    public int getViewTypeCount() { // Número de views
        int total = 1; // assume that headers count as one, then total all sections (um para o header + sections sem header)

        for (Section section : this.sections) {
            total += section.adapter.getViewTypeCount(); // Views -> header + adapter1 + adapter2 + ... Logo, o header das outras
            // seções é tomado por um item de um adapter e não por um header mesmo,
            // o que não faz mal pois nós vamos tratar as seções individualmente.
        }

        return (total); // 2     // Sendo que 0 pertencerá sempre ao titulo e os demais ao content adapter
       /*
        Garante que o conjunto de elementos de exibição do cabeçalho da seção
        seja reciclado separadamente da fila de objetos de exibição de cada seção
        (lembrar que ListViews trabalham com reciclagem de views anteriores p/ reuso).
                                                                                     */
    }

    public int getItemViewType(int position) {
        int typeOffset = TYPE_SECTION_HEADER + 1; // typeOffSet nunca vai ser zero. Pois zero pertence ao titulo,

           for (Section section : this.sections) {
            // check if position inside this section
            if (position == 0) {
                return (TYPE_SECTION_HEADER); // retorna HEADER para posição 0.
            }
            int size = section.adapter.getCount() + 1; // tamanho da seção atual + 1

            if (position < size) {  // Retorna != header p/ posição maior que zero e menor que o tamanho da seção
                return (typeOffset + section.adapter.getItemViewType(position - 1)); // typeOffSet + 0
            }

            // otherwise jump into next section
            position -= size;
            typeOffset += section.adapter.getViewTypeCount(); // typeOffSet += 1
        }

        return (-1);
    }

    public boolean areAllItemsSelectable() {
        return (false);
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER); // Headers não estarão habilitados
    }

    @Override
    public View getView(int position, View convertView, // The old view to reuse, if possible. Heterogeneous lists can specify their
                        ViewGroup parent) {       // number of view types, so that this View is always of the right type.
        int sectionIndex = 0;
        for (Section section : this.sections) {
            if (position == 0) { // O primeiro elemento vai ser sempre o header
                return (getHeaderView(section.caption, sectionIndex,
                        convertView, parent)); // Devolve o header que foi implementado pela instancia nesse metodo abstrato.
            }

            int size = section.adapter.getCount() + 1;
            if (position < size) { // Os demais elementos vão ser adapter
                return (section.adapter.getView(position - 1, // Array index
                        convertView,
                        parent));
            }
            position -= size;
            sectionIndex++;
        }

        return (null);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

}
