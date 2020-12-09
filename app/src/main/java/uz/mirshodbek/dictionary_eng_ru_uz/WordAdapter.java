package uz.mirshodbek.dictionary_eng_ru_uz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.MyViewHolder> {
    private ArrayList<Word> words = new ArrayList<>();
    private Context context;

    public WordAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Word> words) {
        this.words = words;
    }

    public void removeWord(int position) {
        this.words.remove(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eng_word;
        TextView definition;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.eng_word = (TextView) itemView.findViewById(R.id.textView);
            this.definition = (TextView) itemView.findViewById(R.id.textViewS);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String text = words.get(position).get_eng_word();

                    Intent intent = new Intent(context, WordMeaning.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("eng_word", text);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.eng_word.setText(words.get(position).get_eng_word());
        holder.definition.setText(words.get(position).getDefinition());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}

