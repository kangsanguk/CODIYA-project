package com.sample.covid19;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragInformation2 extends Fragment {

    private View view;

    public static FragInformation2 newinstance(){
        FragInformation2 fragInformation2 = new FragInformation2();
        return fragInformation2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_information2,container,false);

        VideoView videoView = view.findViewById(R.id.VideoView);
        videoView.setVideoPath("android.resource://"+getActivity().getPackageName()+"/"+R.raw.video);

        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        return view;
    }
}
