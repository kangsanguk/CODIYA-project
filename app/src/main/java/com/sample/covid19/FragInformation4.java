package com.sample.covid19;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class FragInformation4 extends Fragment {

    private View view;
    public Button Corona1;
    public Button Corona2;
    public Button Corona3;
    public Button Corona4;
    public Button Corona5;

    public static FragInformation4 newinstance() {
        FragInformation4 fragInformation4 = new FragInformation4();
        return fragInformation4;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_information4, container, false);

        Corona1 = (Button)view.findViewById(R.id.corona1);
        Corona2 = (Button)view.findViewById(R.id.corona2);
        Corona3 = (Button)view.findViewById(R.id.corona3);
        Corona4 = (Button)view.findViewById(R.id.corona4);
        Corona5 = (Button)view.findViewById(R.id.corona5);

        Corona1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),StepOneActivity.class);
                startActivity(intent);
            }
        });

        Corona2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),StepOneHalfActivity.class);
                startActivity(intent);
            }
        });

        Corona3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),StepTwoActivity.class);
                startActivity(intent);
            }
        });

        Corona4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),StepTwoHalfActivity.class);
                startActivity(intent);
            }
        });

        Corona5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),StepThreeActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
