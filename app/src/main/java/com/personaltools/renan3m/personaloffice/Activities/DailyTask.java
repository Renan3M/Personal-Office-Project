package com.personaltools.renan3m.personaloffice.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.personaltools.renan3m.personaloffice.R;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.lang.String;
import java.util.Set;
import java.util.TreeSet;


// A grande gambiara nessa activity é porque eu só adiciono os Individual tasks na listOfTasks (que são enviados na intent pra
// main) depois que eu clico no botão "Go!", logo, se eu adcionar clicando no btn com simbolo de '+' apos escrever o nome da task
// ele somente adiciona aos itens do adapter (visual), mesma coisa serve para o btn com simbolo de 'x', este remove tão somente
// do adapter. Talvez uma soluçao seja já adicionar simultanemante à lista e ao adapter, criando inicialmente a task com apenas
// o nome, e depois adciona a informação referente aos pomodoros na task já criada. (Sem precisar clicar no btn p cria-las de
// fato, que é o caso agora).  Tenho q mudar essa porra toda, vai ter jeito não...

public class DailyTask extends AppCompatActivity {

    private static final String TAG = "DailyTask";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Type type = new TypeToken<ArrayList<IndividualTask>>() {}.getType();

    private static final int SHOW_CONFIRMATION_DIALOG = 1;

    private static Set<String> listOfLists;

    private AlertDialog dialogConfirmacao;

    private ArrayList<String> itens = new ArrayList<>();

    private List<Parcelable> listOfTasks;

    private Handler uiHandler;

    private ListAdapter listAdapter;

    private Boolean btnFlag;

    private EditText taskName;

    private ListView list;


    // Classe que simboliza uma tarefa definida pelo usuario
    public static class IndividualTask implements Parcelable {

        private int numDePom;
        private String nameTask;
        private String insertedTime;

        public IndividualTask(int numDePom, String nameTask) {
            this.numDePom = numDePom;
            this.nameTask = nameTask;
        }

        public IndividualTask(){}

        protected IndividualTask(Parcel in) {
            numDePom = in.readInt();
            nameTask = in.readString();
            insertedTime = in.readString();
        }


        public static final Creator<IndividualTask> CREATOR = new Creator<IndividualTask>() {
            @Override
            public IndividualTask createFromParcel(Parcel in) {
                return new IndividualTask(in);
            }

            @Override
            public IndividualTask[] newArray(int size) {
                return new IndividualTask[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(numDePom);
            dest.writeString(nameTask);
            dest.writeString(insertedTime);
        }

        public void setNumDePom(int n){
            numDePom = n;
        }

        public void setNameTask(String n){
            nameTask = n;
        }

        public int getNumDePom() {
            return numDePom;
        }

        public String getNameTask() {
            return nameTask;
        }

        public void decreasePom() {
            this.numDePom--;
        }

        public String getInsertedTime() {
            return insertedTime;
        }

        public void setInsertedTime(String insertedTime) {
            this.insertedTime = insertedTime;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_task);

        sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.PREFS_NAME,0);
        editor = sharedPreferences.edit();

        btnFlag = false;

        taskName = findViewById(R.id.edt_name_of_task_daily);

        list = findViewById(R.id.list_itens_daily);

        listAdapter = new ListAdapter(this);

        list.setAdapter(listAdapter);

        listOfTasks = new ArrayList<>();


        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_CONFIRMATION_DIALOG:
                        dialogConfirmacao.show();
                        break;

                    default:
                        break;
                }
            }
        };

        dialogConfirmacao = criaDialogConfirmacao();
    }

    public static ArrayList<IndividualTask> getListFromShared(Context ctx, String key){
        Gson gson = new Gson();
        ArrayList<IndividualTask> productFromShared = new ArrayList<>();

        if (sharedPreferences == null) // Será q é necessário?
                sharedPreferences = ctx.getSharedPreferences(MainActivity.PREFS_NAME,0);

        String jsonPreferences = sharedPreferences.getString(key,"");

        productFromShared = gson.fromJson(jsonPreferences,type);

        return productFromShared;
    }

    public static <T> void setListToShared(String key, List<T> list){

        Gson gson = new Gson();
        String json = gson.toJson(list);

        editor.putString(key,json);
        editor.commit();
    }

    public static <T> void setListToSharedSet(Context ctx, String setKey){ // Esse método deve ser chamado apenas qndo ja tiver terminado as tarefas todas da lista.

        if (sharedPreferences == null) // Será q é necessário?
                sharedPreferences = ctx.getSharedPreferences(MainActivity.PREFS_NAME,0);

        // Adcionando o json (lista atual) ao nosso set. Esse set não está persistindo.. (dando reset)
        String listOfTasks = sharedPreferences.getString(MainActivity.LIST_TAG,"");

        if (listOfLists == null){listOfLists = new TreeSet<>(); }

        if (!listOfTasks.equals("")) listOfLists.add(listOfTasks); // em Json

        // Adcionando o set ao sharedPreferences
        if (editor == null)
                editor = sharedPreferences.edit();

        if (sharedPreferences.getStringSet(setKey, null) == null) { // First time creating the shared
            editor.putStringSet(setKey, listOfLists);
            editor.commit();
        } else {
          Set<String> list = sharedPreferences.getStringSet(setKey,null);
          list.addAll(listOfLists);

            editor.putStringSet(setKey, list);
            editor.commit();
        }
    }

    public static ArrayList<ArrayList<IndividualTask>> getListsFromSharedSet(String setKey){

        Set<String> set = sharedPreferences.getStringSet(setKey, null);

        ArrayList<ArrayList<IndividualTask>> setOfLists = new ArrayList<>();

        Gson gson = new Gson();

        // Percorrendo o sharedSet e adcionando seu objeto json já convertido para o nosso array.

        if (set == null){
            Log.e(TAG,"Set = null, error!");
            return null;
        }

        //Até aqui tudo ocorre que nem o desejado


        for (String taskJson : set) {
            setOfLists.add((ArrayList<IndividualTask>) gson.fromJson(taskJson,type));
        }

         return setOfLists;
    }


    private AlertDialog criaDialogConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirmação do arranjo das tarefas?");

        builder.setPositiveButton("enviar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (!listOfTasks.isEmpty()) {

                            Date data = new Date();
                            data.setTime(System.currentTimeMillis());
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
                            String dataFormated = sdf.format(data);

                            // Já q n consigo armazenar no objeto lista..
                            ((IndividualTask)listOfTasks.get(0)).setInsertedTime(dataFormated);

                            setListToShared(MainActivity.LIST_TAG,listOfTasks);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            intent.putExtra(MainActivity.CURRENT_TASK_FLAG, "");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);

                            listOfTasks.clear();
                            listAdapter.clear();
                        }
                    }
                }
        );
        builder.setNegativeButton("apagar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogConfirmacao.dismiss();

                recreate();
            }
        });


        return builder.create();
    }

    public void addOnClick(View view) {
        if (taskName.getText().length() != 0 && taskName.getText().length() < 35) { // not empty

            Toast.makeText(this, "Dados inseridos", Toast.LENGTH_SHORT).show();

            IndividualTask task = new IndividualTask();
            task.setNameTask(taskName.getText().toString());

            listOfTasks.add(task);

            ListAdapter listAdapter = (ListAdapter) list.getAdapter();
            listAdapter.add(taskName.getText().toString());
            listAdapter.notifyDataSetChanged();

        } else if (taskName.getText().length() == 0) {

            Toast.makeText(this, "Favor inserir algum texto", Toast.LENGTH_SHORT).show();
        } else if (taskName.getText().length() >= 35) {

            Toast.makeText(this, "Não é permitido inserir mais de 35 caracteres ", Toast.LENGTH_SHORT).show();
        }
    }


    public void removeItem(View view) {


        if (!(view.getTag() instanceof Integer)) {
            Toast.makeText(this, "operação falhou", Toast.LENGTH_SHORT).show();

            return;
        }


        Toast.makeText(this, "Dados removidos", Toast.LENGTH_LONG).show();

        ListAdapter listAdapter = (ListAdapter) list.getAdapter();

        listOfTasks.remove((int) view.getTag());

        listAdapter.remove(listAdapter.getItem((int) view.getTag()));
        listAdapter.notifyDataSetChanged();


    }

    public void goBtn(View view) {

        btnFlag = true;

        listAdapter.notifyDataSetChanged();

    }


    // Custom adapter
    class ListAdapter extends ArrayAdapter {

        public ListAdapter(@NonNull Context context) {
            super(context, R.layout.daily_listview_item, itens);
        }


        @Override
        public View getView(int position, View convertView,   // Que mecanismo de reciclagem de view super interessante!
                            ViewGroup parent) {  // convertView = row
            Wrapper wrapper;

            int pomCounter = 0;

            if (convertView == null) { // Se não houver view antiga para re-usar.
                convertView = getLayoutInflater().inflate(R.layout.daily_listview_item,
                        null);
                wrapper = new Wrapper(convertView);
                convertView.setTag(wrapper); // Para poder reciclar essa view (já que são todas iguais) [ñ ter q instanciar wraper dnv]
            } else {
                wrapper = (Wrapper) convertView.getTag(); // Usa a linha anterior (convertView)
            }

            wrapper.getLabel().setText(itens.get(position).toString());
            wrapper.getButton().setTag(position);


            for (int i = 0; i < wrapper.getPomors().length; i++) { // Vou percorrer cada check e verificar se ta checked, se estiver aumento um contador e depois crio um IndividualTask com esse contador + conteudo do label.
                if (wrapper.getPomors()[i].isChecked()) {
                    pomCounter++;
                }
            }

            ((IndividualTask)listOfTasks.get(position)).setNumDePom(pomCounter);

            if (btnFlag){

                    if (((IndividualTask) listOfTasks.get(position)).getNumDePom() == 0) { // P/ todos do ultimo refresh
                        Toast.makeText(getApplicationContext(), "Tarefa sem pomodoro é mole!", Toast.LENGTH_LONG).show();
                        btnFlag = false;
                        return convertView;
                    }

                    if (position == (listOfTasks.size()-1)) { // No ultimo, se até aqui n tiver dado merda
                        Message msg = uiHandler.obtainMessage(SHOW_CONFIRMATION_DIALOG, 0, 0, null);
                        uiHandler.sendMessage(msg);
                        btnFlag = false;
                    }
                }

            return (convertView);
        }


        @Override
        public long getItemId(int position) {

            //the ids keep they stable and unique for adding only
            final long id = position;

            return id;
        }

        @Override
        public final boolean hasStableIds() {
            return true;
        }

    }

    class Wrapper {
        View row = null;
        TextView label = null;
        ImageButton button = null;
        CheckBox[] pomodors = null; // <-- NÃO POSSO RECICLAR ESSA PORRA... SE NÃO É OBVIO QUE VAI FICAR SEMPRE IGUAL AO ANTERIOR...

        Wrapper(View row) {
            this.row = row;
        }

        TextView getLabel() {
            if (label == null) {

                label = row.findViewById(R.id.name_of_task_item);
            }
            return (label);
        }

        ImageButton getButton() {
            button = row.findViewById(R.id.btn_remove_item);

            return (button);
        }

        CheckBox[] getPomors() {
            pomodors = new CheckBox[]{row.findViewById(R.id.pomor1), row.findViewById(R.id.pomor2),
                    row.findViewById(R.id.pomor3), row.findViewById(R.id.pomor4), row.findViewById(R.id.pomor5)};

            return pomodors;
        }
    }

}
