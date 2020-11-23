package com.sample.covid19;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragMask2 extends Fragment {

    public static FragMask2 newinstance(){
        FragMask2 fragMask2 = new FragMask2();
        return fragMask2;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.frag_mask2, container, false);

        WebView myWebView = myView.findViewById(R.id.mask2);
        myWebView.loadUrl("https://msearch.shopping.naver.com/search/all?query=마스크");

        myWebView.setWebViewClient(new WebViewClient() {

        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        return myView;
    }
}
