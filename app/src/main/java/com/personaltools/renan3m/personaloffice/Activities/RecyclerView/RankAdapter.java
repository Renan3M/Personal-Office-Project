package com.personaltools.renan3m.personaloffice.Activities.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.personaltools.renan3m.personaloffice.R;

import java.util.ArrayList;

/**
 * Created by renan on 21/03/2018.
 */

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {

    private Context mCtx;

    private static int positionReq;
    private static int currentProgress;
    private int[] values;

    private String rankList[] = {"Camponês", "Cavaleiro", "Nobre", "Baronete", "Barão", "Visconde", "Conde",
            "Marquês", "Duque", "Grão-duque", "Principe real", "Principe imperial", "Regente", "Rei", "Imperador"};

    public RankAdapter(Context mCtx) {

        this.mCtx = mCtx;

    }

    @Override
    public RankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { // vai inflar nosso layout
        LayoutInflater inflater_ = LayoutInflater.from(mCtx);
        View view = inflater_.inflate(R.layout.list_of_ranks, parent, false);

        return new RankViewHolder(view);
    }

    public void setData(int pos, int max, int[] values){
        this.positionReq = pos;  // Position requested by user to be altered.. how do I alter layout structure? I can touch rank_name, etc.. but layout components? <- Não vou precisar fazer isso, graças a Deus! =)
        this.currentProgress = max;
        this.values = values;
    }


    @Override
    public void onBindViewHolder(RankViewHolder holder, int position) { // vai rodar p/ cada um dos itens   (método auto-invocado)
        // O que muda em cada chamada desse método é a posição do item. 0,1,2,3,4,5...

        String rank = rankList[position];

        if (position <= positionReq ){
            holder.rank_name.setText(rank);
            holder.people_at_rank.setText("Parabéns!");
        } else{
            holder.rank_name.setText("bloqueado");
            holder.people_at_rank.setText(String.valueOf(values[position]) + " pomodoros para desbloquear!");
        }

        if (position==0){
            holder.rank_progress.setMax(1);
            holder.rank_progress.setProgress(1);
        }else {
            holder.rank_progress.setMax(values[position]);
            holder.rank_progress.setProgress(currentProgress);
        }

        // O progresso real deve verificar no db os usuario que possuem tal rank
        holder.people_at_rank.setTextSize(12);


    }


    @Override
    public int getItemCount() {
        return rankList.length;
    }


    public class RankViewHolder extends RecyclerView.ViewHolder{

        TextView rank_name;
        TextView people_at_rank;
        ProgressBar rank_progress;

        public RankViewHolder(View itemView) {
            super(itemView);

            rank_name = itemView.findViewById(R.id.name_of_the_rank);
            people_at_rank = itemView.findViewById(R.id.people_at_this_rank);
            rank_progress = itemView.findViewById(R.id.rank_progress_bar);
        }
    }

    public String[] getRankList() {
        return rankList;
    }
}
