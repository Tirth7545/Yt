package com.example.yt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
 private WebView web;
 private ProgressBar progressBar;
 private SwipeRefreshLayout swipeRefreshLayout;
 AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        web=(WebView)findViewById(R.id.web);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        builder = new AlertDialog.Builder(this);

        if(savedInstanceState==null){
            web.post(new Runnable() {
                @Override
                public void run() {
                    if(amIConnected())
                    loadWebsite();

                }
            });
        }


        web.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        web.getSettings().setAppCacheEnabled(true);
        web.getSettings().setMediaPlaybackRequiresUserGesture(true);

        WebSettings webSettings = web.getSettings();

        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setEnableSmoothTransition(true);


        web.getSettings().setLoadsImagesAutomatically(true);
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
//        web.loadUrl("https://www.youtube.com/");
        web.setWebChromeClient(new MyChrome());

        //For The Refreshing the webView

//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pullT oRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(amIConnected())
                web.reload();
                else
                {
                    web.setVisibility(View.GONE);
                    builder.setMessage("Oppssss!!!!! No Internet Connection")
                            .setCancelable(false)
                            .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    web.reload();
                                    web.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(),"Refresh",Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.setTitle("No Connection");
                    alert.show();

                }
                swipeRefreshLayout.setRefreshing(false);



            }
        });

//        web.setWebViewClient(new WebViewClient()
//                             {
//                                 @Override
//                                 public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                                     web.loadUrl("file:///android_asset/error.html");
//
//                                     if (amIConnected())
//                                         web.reload();
//                                     //super.onReceivedError(view, request, error);
//                                 }
//                             }
//        );


      web.setWebViewClient(new WebViewClient()
      {
          @Override
          public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
              Toast.makeText(view.getContext(), "HTTP error "+errorResponse.getStatusCode(), Toast.LENGTH_LONG).show();
              super.onReceivedHttpError(view, request, errorResponse);
          }

          @Override
          public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
              web.setVisibility(View.GONE);
              if(!amIConnected()) {
                  builder.setMessage("Some Thing Went Wrong Please Check Internet Connection or Restart The App.")
                          .setCancelable(false)
                          .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {

                                  web.reload();
                                  web.setVisibility(View.VISIBLE);
                                  Toast.makeText(getApplicationContext(), "Refresh ....", Toast.LENGTH_SHORT).show();
                                  dialog.cancel();

                              }
                          });

                  AlertDialog alert = builder.create();
                  alert.setTitle("No Connection");
                  alert.show();
                  if(amIConnected())
                      alert.cancel();

              }
              else
              {
                  web.reload();
              }
              super.onReceivedError(view, request, error);
          }
      });



    }



    private void loadWebsite() {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            web.loadUrl("https://www.youtube.com/");
        } else {
            web.setVisibility(View.GONE);
            builder.setMessage("Oppssss!!!!! No Internet Connection")
                    .setCancelable(false)
                    .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            web.reload();
                            web.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Refresh",Toast.LENGTH_SHORT).show();
                            dialog.cancel();

                        }
                    });

            AlertDialog alert = builder.create();
            alert.setTitle("No Connection");
            alert.show();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        web.restoreState(savedInstanceState);
    }
    boolean doubleBackPressed=false;
    @Override
    public void onBackPressed() {
       //  super.onBackPressed();
        if(web.canGoBack())
        {
            web.goBack();
        }
        else
        {
            if(doubleBackPressed)
            {
                super.onBackPressed();
            }
            else
            {
                doubleBackPressed=true;
                final SwipeRefreshLayout relativeLayout=(SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

                Snackbar.make(relativeLayout,getString(R.string.stringexit),Snackbar.LENGTH_SHORT).show();

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackPressed=false;
                    }
                },2000);

            }
        }

    }

//    class Browser_home extends WebViewClient
//    {
//        Browser_home(){
//
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//            super.onPageStarted(view, url, favicon);
//        }
//
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//
//            setTitle(view.getTitle());
//            progressBar.setVisibility(View.GONE);
//            super.onPageFinished(view, url);
//        }
//    }


    private class MyChrome extends WebChromeClient
    {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }

        public boolean  amIConnected()
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }


}




