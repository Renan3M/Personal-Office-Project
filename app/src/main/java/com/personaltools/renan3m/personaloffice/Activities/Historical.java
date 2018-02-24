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
import java.util.List;

import static com.personaltools.renan3m.personaloffice.Activities.DailyTask.getListFromShared;

public class Historical extends AppCompatActivity {

    private static final String TAG = "HistoricalActivity";

    List<String> list;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);

        listView = findViewById(R.id.hist_list);

        list = new ArrayList<>();

        ArrayList<ArrayList<DailyTask.IndividualTask>> listOfLists =
                DailyTask.getListsFromSharedSet(MainActivity.LIST_OF_LISTS_TAG);

        Log.e(TAG, String.valueOf(listOfLists.size()));


        // tenho q adicionar 1 section para cada lista, e atribuir a data de inserção ao caption.


        // Isso cria uma section para todas as listas de até agora

        for (ArrayList<DailyTask.IndividualTask> listArray : listOfLists) {

            list.clear();

            for (int i = 0; i < listArray.size(); i++) {
                Log.e(TAG, listArray.get(i).getNameTask());

                String content = listArray.get(i).getNameTask() + " - " +
                        String.valueOf(listArray.get(i).getNumDePom()) + " pomodoros";

                list.add(content);
            }
            Log.e(TAG, "new Section");

            adapter.addSection(listArray.get(0).getInsertedTime(),
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
}
