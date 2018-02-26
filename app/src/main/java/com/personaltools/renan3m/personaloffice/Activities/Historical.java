package com.personaltools.renan3m.personaloffice.Activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.personaltools.renan3m.personaloffice.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.personaltools.renan3m.personaloffice.Activities.DailyTask.getListFromShared;

public class Historical extends AppCompatActivity {

    private static final String TAG = "HistoricalActivity";

    private  ArrayList<ArrayList<DailyTask.IndividualTask>> listOfLists;

    private List<String> list;
    private ListView listView;
    private Date left;
    private Date right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);

        listView = findViewById(R.id.hist_list);

        if (DailyTask.getListsFromSharedSet(MainActivity.LIST_OF_LISTS_TAG) == null) return;

        left = new Date();
        right = new Date();

        list = new ArrayList<>();

        listOfLists = DailyTask.getListsFromSharedSet(MainActivity.LIST_OF_LISTS_TAG);

        // Sorteia nossa lista baseado em sua ordem de data de inserção.
        Collections.sort(listOfLists, new Comparator<ArrayList<DailyTask.IndividualTask>>() {
            @Override
            public int compare(ArrayList<DailyTask.IndividualTask> left, ArrayList<DailyTask.IndividualTask> right) {

                return left.get(0).getInsertedTime().compareTo(right.get(0).getInsertedTime());
        }});

        // Percorre nossa lista de listas criando uma section para cada lista filha com seus respectivos elementos.
        for (int a = 0; a < listOfLists.size(); a++) {

            list.clear();

            for (int i = 0; i < listOfLists.get(a).size(); i++) {
                Log.e(TAG, listOfLists.get(a).get(i).getNameTask());

                String content = listOfLists.get(a).get(i).getNameTask() + " - " +
                        String.valueOf(listOfLists.get(a).get(i).getNumDePom()) + " pomodoros";

                list.add(content);
            }

            Log.e(TAG, "new Section");
            adapter.addSection(listOfLists.get(a).get(0).getInsertedTime(),
                    new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_1,
                            new ArrayList<>(list)));

        }

        listView.setAdapter(adapter);
    }

    SectionedAdapter adapter = new SectionedAdapter() {
        protected View getHeaderView(String caption, int index, // caption = titulo =  data de inserção
                                     View convertView,
                                     ViewGroup parent) {

            TextView result = (TextView) convertView; // view reciclado
            if (convertView == null) { // caso não haja view para reciclar (primeira vez)
                result = (TextView) getLayoutInflater()
                        .inflate(R.layout.txt_view,
                                null);
            }
            result.setText(caption);
            return (result);
        }
    };

    public void clearAdapter(View view) {

        try {
            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(MainActivity.LIST_OF_LISTS_TAG);
            editor.commit();

            listOfLists.clear();
            adapter.notifyDataSetChanged();

            this.recreate();

        }catch (Exception e){} // It simply means the user already cleaned the shared and is trying to do it again...
    }
}
