package com.mapprr.gitsearch.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mapprr.gitsearch.R;
import com.mapprr.gitsearch.main.WebAppInterface;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by appigizer on 17/1/18.
 */

public class WebViewFragment extends Fragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.news_load_progress)
    ContentLoadingProgressBar loadingProgressBar;
    String webUrl;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.project_detail_fragment, container, false);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        String url = args.getString("projectLink");
        webUrl = url;
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // progress on player resize
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) loadingProgressBar.getLayoutParams();
        loadingProgressBar.measure(0, 0);
        layoutParams.setMargins(0, -(loadingProgressBar.getMeasuredHeight() / 2), 0, 0);
        loadingProgressBar.setLayoutParams(layoutParams);

        loadingProgressBar.setVisibility(View.VISIBLE);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }


        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webSettings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new WebAppInterface(getActivity()), "android");
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(webUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadingProgressBar.hide();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
