package pictures.newsin.athousandwords;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomepageActivity extends AppCompatActivity {
    private static final int UI_ANIMATION_DELAY = 0;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            /*mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    public ImageButton activePressed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_homepage);

        //createProgressBarLayout();
        // PULL THE NEWSIN.PICTURES CONTENT
        new LoadNewsTask(this).execute();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void newsClick(View v) {

        if(activePressed != null) {
            activePressed.animate().alpha(1f).setDuration(350).setInterpolator(new DecelerateInterpolator());
            TextView tv = ((NewsStructure) activePressed.getTag()).textView;
            tv.animate().alpha(0f).setDuration(350).setInterpolator(new DecelerateInterpolator());
        }
        if(activePressed != v) {
            NewsStructure news = (NewsStructure) v.getTag();
            activePressed = (ImageButton) v;
            activePressed.animate().alpha(0.5f).setDuration(350).setInterpolator(new AccelerateInterpolator());
            TextView tv = ((NewsStructure) activePressed.getTag()).textView;
            tv.animate().alpha(1f).setDuration(350).setInterpolator(new DecelerateInterpolator());
        }else {
            activePressed = null;
        }
    }

    public class NewsStructure {
        public String url;
        public String title;
        public String description;
        public String image;
        public TextView textView;

        public NewsStructure(JSONObject newsJSON) {
            try {
                this.url = newsJSON.getString("url");
                this.title = newsJSON.getString("title");
                this.description = newsJSON.getString("description");
                this.image = newsJSON.getString("image");
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class NewsLoad {
        public NewsStructure structure;
        public Bitmap image;
        public NewsLoad(NewsStructure structure, Bitmap image) {
            this.structure = structure;
            this.image = image;
        }
    }

    public class LoadNewsTask extends AsyncTask<String, Void, List<NewsLoad>> {

        public HomepageActivity activity;

        public LoadNewsTask(HomepageActivity activity) {
            this.activity = activity;
        }

        @Override
        protected List<NewsLoad> doInBackground(String... params) {

            JSONParser parser = new JSONParser();
            JSONObject result = parser.getJSONFromUrl("https://thousand-words.appspot.com/getnews.json");
            List<NewsLoad> articles = new ArrayList<NewsLoad>();
            try {
                Iterator<String> keys = result.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject article = result.getJSONObject(key);
                    NewsStructure structure = new NewsStructure(article);
                    Bitmap image = getBitmapFromURL(structure.image);

                    articles.add(new NewsLoad(structure, image));
                }
            }catch(JSONException e) {
                e.printStackTrace();
            }
            return articles;
        }


        protected void onPostExecute(List<NewsLoad> articles) {
            LinearLayout imageView = (LinearLayout) activity.findViewById(R.id.images_layer);
            for (int i = 0; i < articles.size(); i++) {
                RelativeLayout newsLayout = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.newsbutton, null);
                ImageButton newsButton = (ImageButton) newsLayout.getChildAt(0);
                // or LinearLayout buttonView = (LinearLayout)this.getLayoutInflater().inflate(R.layout.my_button, null);
//                newsButton.setBackgroundDrawable(null);
//                newsButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                newsButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1000));
                newsButton.setMinimumHeight(1000);
                NewsLoad article = articles.get(i);
                newsButton.setImageBitmap(article.image);
                newsButton.setTag(article.structure);

                TextView text = (TextView) newsLayout.getChildAt(1);
                text.setText(article.structure.title);
                article.structure.textView = text;

                //imageView.addView(newsButton);
                imageView.addView(newsLayout);
                activity.finishRefresh();
            }
        }

    }

    ProgressBar progress;
    LinearLayout content_rel_layout;
    int topMargin, dragLength;
    boolean refreshing = true;

    private void createProgressBarLayout() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Point windowSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(windowSize);
        topMargin = -Math.round(6 * metrics.density);
        dragLength = Math.round(windowSize.y / 2.5f);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.TOP);
        top.setOrientation(LinearLayout.HORIZONTAL);

        content_rel_layout = (LinearLayout) findViewById(R.id.images_layer);
        content_rel_layout.addView(top);
        ViewGroup.LayoutParams topParams = top.getLayoutParams();
        topParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        topParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        top.setLayoutParams(topParams);

        FrameLayout left = new FrameLayout(this);
        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setProgress(100);
        progress.setIndeterminate(false);
        // progress.setBackgroundResource(R.drawable.progress_bar);
        FrameLayout right = new FrameLayout(this);

        top.addView(left);
        top.addView(progress);
        top.addView(right);

        LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) left.getLayoutParams();
        leftParams.weight = 1;
        leftParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        leftParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        leftParams.topMargin = topMargin;
        left.setLayoutParams(leftParams);

        LinearLayout.LayoutParams progressParams = (LinearLayout.LayoutParams) progress.getLayoutParams();
        progressParams.weight = 2;
        progressParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        progressParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        progressParams.topMargin = topMargin;

        progress.setLayoutParams(progressParams);

        LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) right.getLayoutParams();
        rightParams.weight = 1;
        rightParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        rightParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        rightParams.topMargin = topMargin;

        right.setLayoutParams(rightParams);

        ScrollView sv = (ScrollView) findViewById(R.id.news_scroll_view);
        sv.setOnTouchListener(new RefreshTouchListener(this));
    }

    public void finishRefresh() {
        if(progress != null) {
            progress.setIndeterminate(false);
            progress.postInvalidate();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progress.getLayoutParams();
            params.weight = 2;
            progress.setLayoutParams(params);
        }
        refreshing = false;
    }

    class RefreshTouchListener implements View.OnTouchListener {

        public HomepageActivity activity;

        public RefreshTouchListener(HomepageActivity activity) {
            this.activity = activity;
        }

        float startY, lastY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ScrollView scroll = (ScrollView) v;
            if (scroll.getScrollY() == 0) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        lastY = startY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!refreshing && event.getY() > lastY) {

                            lastY = event.getY();
                            if (event.getY() - startY <= dragLength) {
                                double percent = 1 - (event.getY() - startY) / dragLength;
                                double weight;
                                weight = 2 * Math.pow(percent, 0.8);
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progress.getLayoutParams();
                                params.weight = (float) weight;
                                progress.setLayoutParams(params);
                                progress.setIndeterminate(false);
                                progress.setPadding(0, 0, 0, 0);
                                return true;
                            } else {
                                refreshing = true;

                                new LoadNewsTask(activity).execute();
                                startY = 100000f;
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progress.getLayoutParams();
                                params.weight = 0;
                                progress.setIndeterminate(true);
                                progress.postInvalidate();
                                progress.setLayoutParams(params);
                            }
                        }
                    case MotionEvent.ACTION_UP:
                        startY = 100000f;
                        if (!refreshing) {
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progress.getLayoutParams();
                            params.weight = 2;
                            progress.setLayoutParams(params);
                        }
                }
            }
            return false;
        }
    }
}
