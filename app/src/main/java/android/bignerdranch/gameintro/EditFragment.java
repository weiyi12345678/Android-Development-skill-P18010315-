package android.bignerdranch.gameintro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EditFragment extends Fragment {

    private Button insert,edit_delete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        //start activity to go to insert game info page
        insert = (Button) rootView.findViewById(R.id.button_insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), firebase_insert.class);
                startActivity(intent);
            }
        });

        //start activity to go to edit delete game info page
        edit_delete = (Button) rootView.findViewById(R.id.button_edit_delete);
        edit_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditDelete.class);
                startActivity(intent);
            }
        });



        return rootView;
    }
}
