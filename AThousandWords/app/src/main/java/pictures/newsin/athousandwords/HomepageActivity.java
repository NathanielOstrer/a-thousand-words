package pictures.newsin.athousandwords;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
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
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomepageActivity extends AppCompatActivity implements View.OnLongClickListener {

    public static Map<String, Bitmap> imageCache = new HashMap<>();

    public static String NEWSORDER_FILENAME = "newsorder";

    public static List<String> newsList;
    public static Map<String, String> newsKeys = new HashMap<>();
    static{
        newsKeys.put("The New York Times", "the-new-york-times");
        newsKeys.put("CNN", "cnn");
        newsKeys.put("The Washington Post", "the-washington-post");
        newsKeys.put("The Wall Street Journal", "the-wall-street-journal");
        newsKeys.put("Associated Press", "associated-press");
        newsKeys.put("BBC", "bbc-news");
        newsKeys.put("Independent", "independent");
        newsKeys.put("Engadget", "engadget");
        newsKeys.put("Reddit.com/r/all", "reddit-r-all");
        newsKeys.put("Business Insider", "business-insider");
        newsKeys.put("National Geographic", "national-geographic");
        newsKeys.put("The Economist", "the-economist");
        newsKeys.put("IGN", "ign");
    }

    private static final int UI_ANIMATION_DELAY = 0;
    private static final int FADE_TIME = 350;
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
    public int imageState = 0;


    public static void saveNewsList(Activity activity) {
        FileOutputStream fos = null;
        try {
            fos = activity.openFileOutput(NEWSORDER_FILENAME, MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(fos);
            for(String news : newsList) {
                pw.println(news);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_homepage);

        newsList = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(NEWSORDER_FILENAME);
            Scanner s = new Scanner(fis);
            while(s.hasNext()) {
                newsList.add(s.nextLine());
            }
            s.close();
        }catch(FileNotFoundException e) {
            newsList.addAll(newsKeys.keySet());
        }

        //createProgressBarLayout();
        // PULL THE NEWSIN.PICTURES CONTENT
        //new LoadNewsTask(this).execute();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        hide();
    }

    @Override
    public void onResume() {

        super.onResume();
        new LoadNewsTask(this).execute();
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


    private void removeActive() {
        activePressed.animate().alpha(1f).setDuration(FADE_TIME).setInterpolator(new DecelerateInterpolator());
        NewsStructure structure = (NewsStructure)activePressed.getTag();
        View v = structure.titleView;
        v.animate().alpha(0f).setDuration(FADE_TIME).setInterpolator(new DecelerateInterpolator());
        v = structure.descriptionView;
        v.animate().alpha(0f).setDuration(FADE_TIME).setInterpolator(new DecelerateInterpolator());
        v = structure.iconView;
        v.animate().alpha(0f).setDuration(FADE_TIME).setInterpolator(new DecelerateInterpolator());
    }

    public void newsClick(View v) {

        NewsStructure news = (NewsStructure) v.getTag();

        if(activePressed != v) {

            if(activePressed != null) {
                removeActive();
            }
            activePressed = (ImageButton) v;
            activePressed.animate().alpha(0.5f).setDuration(FADE_TIME).setInterpolator(new AccelerateInterpolator());
            news.titleView.animate().alpha(1f).setDuration(FADE_TIME).setInterpolator(new DecelerateInterpolator());
            news.iconView.animate().alpha(1f).setDuration(FADE_TIME).setInterpolator(new DecelerateInterpolator());
            imageState = 1;
        }else {
            switch(imageState) {
                case 1:
                    imageState = 2;
                    TextView tv = news.titleView;
                    tv.animate().alpha(0f).setDuration(FADE_TIME).setInterpolator(new DecelerateInterpolator());
                    tv = news.descriptionView;
                    tv.animate().alpha(1f).setDuration(FADE_TIME).setInterpolator(new DecelerateInterpolator());
                    break;

                case 2:
                    removeActive();
                    imageState = 0;
                    activePressed = null;
//                    Intent viewIntent =
//                            new Intent(Intent.ACTION_VIEW,
//                                    Uri.parse(news.url));
//                    startActivity(viewIntent);
                    WebpageActivity.loadWebpage(this, news.url);
                    break;

            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (activePressed != null) {
            removeActive();
            activePressed = null;
        }
        Intent intent = new Intent(this, NewSourcesActivity.class);
        startActivity(intent);
        return true;
    }

    public class NewsStructure {
        public String url;
        public String title;
        public String description;
        public String image;
        public String icon;

        public TextView titleView;
        public TextView descriptionView;
        public ImageView iconView;

        public NewsStructure(JSONObject newsJSON) {
            try {
                this.url = newsJSON.getString("url");
                this.title = newsJSON.getString("title");
                this.description = newsJSON.getString("description");
                this.image = newsJSON.getString("image");
                this.icon = newsJSON.getString("icon");
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class NewsLoad {
        public NewsStructure structure;
        public Bitmap image;
        public Bitmap icon;
        public NewsLoad(NewsStructure structure, Bitmap image, Bitmap icon) {
            this.structure = structure;
            this.image = image;
            this.icon = icon;
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
            if(result == null) {
                return null;
            }
            List<NewsLoad> articles = new ArrayList<>();
            List<String> cacheKeys = new ArrayList<>(imageCache.keySet());
            for (String source : newsList) {
                try {
                    String key = newsKeys.get(source);
                    JSONObject article = result.getJSONObject(key);
                    NewsStructure structure = new NewsStructure(article);

                    Bitmap image = imageCache.get(structure.image);
                    cacheKeys.remove(structure.image);
                    if(image == null) {
                        image = getBitmapFromURL(structure.image);
                        imageCache.put(structure.image, image);
                    }

                    Bitmap icon = imageCache.get(structure.icon);
                    cacheKeys.remove(structure.icon);
                    if(icon == null) {
                        icon = getBitmapFromURL(structure.icon);
                        imageCache.put(structure.icon, icon);
                    }

                    articles.add(new NewsLoad(structure, image, icon));
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
            for(String key : cacheKeys) {
                imageCache.remove(key);
            }

            return articles;
        }


        protected void onPostExecute(List<NewsLoad> articles) {
            if(articles == null) {
                Toast.makeText(activity, "Error: No Internet", Toast.LENGTH_SHORT).show();
                return;
            }
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            LinearLayout imageView = (LinearLayout) activity.findViewById(R.id.images_layer);
            imageView.removeAllViews();
            for (int i = 0; i < articles.size(); i++) {
                RelativeLayout newsLayout = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.newsbutton, null);
                ImageButton newsButton = (ImageButton) newsLayout.getChildAt(0);
                // or LinearLayout buttonView = (LinearLayout)this.getLayoutInflater().inflate(R.layout.my_button, null);
//                newsButton.setBackgroundDrawable(null);
//                newsButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                newsButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1000));
                newsButton.setMinimumHeight((int)(300*metrics.density));
                NewsLoad article = articles.get(i);
                if(article.image != null)
                    newsButton.setImageBitmap(article.image);
                newsButton.setTag(article.structure);
                newsButton.setOnLongClickListener(activity);

                TextView text = (TextView) newsLayout.getChildAt(1);
                text.setText(article.structure.title);
                article.structure.titleView = text;

                text = (TextView) newsLayout.getChildAt(2);
                text.setText(article.structure.description);
                article.structure.descriptionView = text;

                ImageView iconView = (ImageView) newsLayout.getChildAt(3);
                if(article.icon != null)
                    iconView.setImageBitmap(article.icon);
                article.structure.iconView = iconView;


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
