package pictures.newsin.athousandwords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebpageActivity extends AppCompatActivity {

    private static String TAG = "WebpageActivity";

    public static String url;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WebView webview = new WebView(this);
        setContentView(webview);

        WebSettings settings = webview.getSettings();
        //settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        final Activity activity = this;
//        progressBar = new ProgressBar(this);
//        webview.addView(progressBar);
//        progressBar.
//
        webview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " +url);
//                if (progressBar.isShowing()) {
//                    progressBar.dismiss();
//                }
            }
        });
        webview.loadUrl(url);
    }

    public static void loadWebpage(Context context, String url) {
        Intent intent = new Intent(context, WebpageActivity.class);
        WebpageActivity.url = url;
        context.startActivity(intent);
    }
}
