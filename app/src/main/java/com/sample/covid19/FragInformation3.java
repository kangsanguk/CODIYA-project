package com.sample.covid19;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class FragInformation3 extends Fragment {

    private View view;
    public ImageButton fragbtn;

    public static FragInformation3 newinstance() {
        FragInformation3 fragInformation3 = new FragInformation3();
        return fragInformation3;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_information3, container, false);


        fragbtn = (ImageButton)view.findViewById(R.id.frag_btn);

        fragbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=kcRdxaV0Sz0"));
                startActivity(browserIntent);
            }
        });

        return view;
    }
}
