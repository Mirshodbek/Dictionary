package uz.mirshodbek.dictionary_eng_ru_uz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uz.mirshodbek.dictionary_eng_ru_uz.R;
import uz.mirshodbek.dictionary_eng_ru_uz.WordMeaning;

public class FragmentFirst extends Fragment {

    public FragmentFirst() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definition, container, false);

        Context context = getActivity();
        TextView headWord = (TextView) view.findViewById(R.id.headWord);
        TextView engRead = (TextView) view.findViewById(R.id.eng_read);
        TextView text = (TextView) view.findViewById(R.id.textViewD);

        String head = ((WordMeaning) context).headWord;
        String read = ((WordMeaning) context).readWord;
        String rus = ((WordMeaning) context).russian;

        headWord.setText(head);
        engRead.setText(read);
        text.setText(rus);
        if (read == null) {
            engRead.setVisibility(View.GONE);
        }
        return view;
    }
}
