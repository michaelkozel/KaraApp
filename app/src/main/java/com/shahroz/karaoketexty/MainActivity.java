package com.shahroz.karaoketexty;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.shahroz.svlibrary.interfaces.onSearchListener;
import com.shahroz.svlibrary.interfaces.onSimpleSearchActionsListener;
import com.shahroz.svlibrary.widgets.MaterialSearchView;
import com.squareup.leakcanary.LeakCanary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.shahroz.karaoketexty.R.drawable.abc_list_focused_holo;
import static com.shahroz.karaoketexty.R.drawable.recording;


public class MainActivity extends AppCompatActivity implements onSimpleSearchActionsListener, onSearchListener {

    private boolean mSearchViewAdded = false;
    private MaterialSearchView sv;
    private WindowManager WinManager;
    private Toolbar tb;
    private ViewPager viewPager;
    private MenuItem searchItem;
    private boolean searchActive = false;
    TabHost host;
    Timer timer;

    int cislo;
    public int page = 0;
    static ListView listSaved;
    private String searchedItem;

    public int id;
    public String name, interpret, text;
    UserOperations uop;
    int count;

    //Hitparade
    ProgressBar progressBar;
    public String[] hitparade;
    public String[] linkynatexty;
    public Bitmap[] Thumbnails;
    CustomSwipeAdapter adapter;
    ArrayList<String> links;
    ArrayList<String> linksOnThumbnails;
    Boolean prazdnypole = false;
    //Recording
    Button play, record, stop;
    Chronometer chronometer;
    Animation anim;
    TextView tvrecording_state;
    MediaRecorder audioRecord;
    String outputFile = null;
    MediaPlayer mediaPlayer;
    String filename;
    String m_chosen;
    Dialog fileDialog;
    EditText et_filename;
    EditText edit_filename;
    static String[] textySongu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        listSaved = (ListView) findViewById(R.id.list_saved);
        play = (Button) findViewById(R.id.play);
        record = (Button) findViewById(R.id.record);
        stop = (Button) findViewById(R.id.stop);
        chronometer = (Chronometer) findViewById(R.id.chm_stopky);
        tvrecording_state = (TextView) findViewById(R.id.tv_recording);


        //TabHost
        new Hitparade().execute();
        host = (TabHost) findViewById(R.id.tabhost);

        Log.d("TABHOST", "4");
        host.setup();
        TabHost.TabSpec tab1 = host.newTabSpec("Tab One");
        tab1.setContent(R.id.tabh1);
        Log.d("TABHOST", "4,5");
        tab1.setIndicator("",
                getResources().getDrawable(R.drawable.home));
        // TabHost.TabSpec tab2 = host.newTabSpec("Tab Two");
        // tab2.setContent(R.id.tabh2);
        // tab2.setIndicator("", getResources().getDrawable(R.drawable.favourite));
        TabHost.TabSpec tab3 = host.newTabSpec("Tab Three");
        tab3.setContent(R.id.tabh3);
        tab3.setIndicator("",
                getResources().getDrawable(R.drawable.saved));
        TabHost.TabSpec tab4 = host.newTabSpec("Tab four");
        tab4.setContent(R.id.tabh4);
        tab4.setIndicator("",
                getResources().getDrawable(recording));


        host.addTab(tab1);
        // host.addTab(tab2);
        host.addTab(tab3);
        host.addTab(tab4);
        setTabColor(host);
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

                setTabColor(host);
            }
        });

//animation of textview
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(750);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        tvrecording_state.setVisibility(View.INVISIBLE);
        play.setEnabled(true);
        stop.setEnabled(true);
        chronometer.setVisibility(View.INVISIBLE);
        record.setEnabled(false);
        tvrecording_state.setTextColor(getResources().getColor(R.color.red));
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        } else {

            record.setEnabled(true);

        }
//Recording


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
                mediaPlayer = new MediaPlayer();


                /////////////////////////////////////////////////////////////////////////////////////////////////
                //Create FileOpenDialog and register a callback
                /////////////////////////////////////////////////////////////////////////////////////////////////
                SimpleFileDialog FileOpenDialog = new SimpleFileDialog(MainActivity.this, "FileOpen",
                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onChosenDir(String chosenDir) {
                                // The code in this function will be executed when the dialog OK button is pushed
                                File sdCard = Environment.getExternalStorageDirectory();
                                File dir = new File(sdCard.getAbsolutePath() + "/KaraokeTexty");
                                dir.mkdirs();
                                m_chosen = chosenDir;
                                Log.d(MainActivity.this.getClass().getSimpleName(), m_chosen);
                                prehrat();


                            }
                        });

                //You can change the default filename using the public variable "Default_File_Name"
                FileOpenDialog.Default_File_Name = "";
                FileOpenDialog.chooseFile_or_Dir();


            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(MainActivity.this, "Recording Starting...", Toast.LENGTH_SHORT).show();

                createAndShowFileDialog();
                fileDialog.show();


                //  Toast.makeText(MainActivity.this, "Zahajuji nahrávání...", Toast.LENGTH_SHORT).show();
            }


        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }

                //zastavení stopek
                chronometer.stop();
                //textview
                tvrecording_state.setVisibility(View.INVISIBLE);
                tvrecording_state.clearAnimation();
                chronometer.setVisibility(View.INVISIBLE);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                play.setEnabled(true);
                record.setEnabled(true);
                //      Toast.makeText(MainActivity.this, "Úspěšně nahráno", Toast.LENGTH_SHORT).show();
            }
        });


        listSaved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String item = (String) listSaved.getAdapter().getItem(position);
                //  Toast.makeText(getApplicationContext(), item, Toast.LENGTH_SHORT).show();
                uop = new UserOperations(getApplicationContext());

                int count = 0;


                Cursor cursor = uop.read(uop);
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                    name = cursor.getString(1);
                    interpret = cursor.getString(2);
                    text = cursor.getString(3);

                    if (name.equals(item)) {
                        Intent a = new Intent(MainActivity.this, DisplayLyrics.class);
                        a.putExtra("ulozeno", true);
                        a.putExtra("jmeno_songu", item);
                        a.putExtra("text_songu", text);
                        startActivity(a);

                    }
                }
                while (cursor.moveToNext()) {


                    id = cursor.getInt(0);
                    name = cursor.getString(1);
                    interpret = cursor.getString(2);
                    text = cursor.getString(3);

                    if (name.equals(item)) {
                        Intent a = new Intent(MainActivity.this, DisplayLyrics.class);
                        a.putExtra("ulozeno", true);
                        a.putExtra("jmeno_songu", item);
                        a.putExtra("text_songu", text);
                        startActivity(a);
                        break;
                    }
                    //tady to postupně vkládej do listview, jinak se bude pokaždé tohle všechno přeukládat a zůstane ti tam jen poslední song


                }


            }
        });


//load saved textů (databáze)
        ArrayList<String> textysongu = new ArrayList<String>();
        uop = new UserOperations(this);

        count = 0;

        Cursor cursor = uop.read(uop);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
            name = cursor.getString(1);
            interpret = cursor.getString(2);
            text = cursor.getString(3);

            //tady to postupně vkládej do listview, jinak se bude pokaždé tohle všechno přeukládat a zůstane ti tam jen poslední song
            textysongu.add(name);
            count++;
        }
// cyklus
        while (cursor.moveToNext()) {


            id = cursor.getInt(0);
            name = cursor.getString(1);
            interpret = cursor.getString(2);
            text = cursor.getString(3);

            //tady to postupně vkládej do listview, jinak se bude pokaždé tohle všechno přeukládat a zůstane ti tam jen poslední song
            textysongu.add(name);
            count++;


        }

        Log.d("load věcí", Integer.toString(count));
        ArrayAdapter Ladapt = new ArrayAdapter<String>(getApplicationContext(), R.layout.saved_item, R.id.list_item, textysongu);
        listSaved.setAdapter(Ladapt);
        listSaved.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) listSaved.getAdapter().getItem(position);
                //      Toast.makeText(getApplicationContext(), "Long click " + item, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(getApplication());


        Log.d("TABHOST", "1");
        //actionbar and searchview
        tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("");
        tb.setBackgroundResource(R.drawable.logo);
        setSupportActionBar(tb);

        Log.d("TABHOST", "2");
        WinManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        sv = new MaterialSearchView(this);
        sv.hide();
        sv.setOnSearchListener(this);
        sv.setSearchResultsListener(this);

        Log.d("TABHOST", "3");
        if (tb != null) {
            // Delay adding SearchView until Toolbar has finished loading
            tb.post(new Runnable() {
                @Override
                public void run() {
                    if (!mSearchViewAdded && WinManager != null) {
                        WinManager.addView(sv,
                                MaterialSearchView.getSearchViewLayoutParams(MainActivity.this));
                        mSearchViewAdded = true;
                    }
                }
            });
        }


    }


    public void prehrat() {


        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/KaraokeTexty");
            dir.mkdirs();

            mediaPlayer.setDataSource(m_chosen);
            mediaPlayer.prepare();
        } catch (FileNotFoundException e) {
            Log.e("NONO", "Notfound");
            return;
        } catch (IOException e) {
            Log.e("ddd", "prepare failed");
            Toast.makeText(getApplicationContext(), "Soubor nenalezen", Toast.LENGTH_SHORT).show();
            return;
        }


        mediaPlayer.start();
        tvrecording_state.setTextColor(getResources().getColor(R.color.green));
        tvrecording_state.setText("Playing");
        tvrecording_state.startAnimation(anim);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setVisibility(View.VISIBLE);
        chronometer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                chronometer.stop();
                chronometer.setVisibility(View.INVISIBLE);
                tvrecording_state.clearAnimation();
                tvrecording_state.setVisibility(View.INVISIBLE);
            }
        });


    }

    public void prepareAndStartRecording() {


        try {

            {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/KaraokeTexty");
                dir.mkdirs();
                outputFile = dir.toString() + "/" + filename + ".3gp";

                audioRecord = new MediaRecorder();
                audioRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
                audioRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                audioRecord.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                audioRecord.setOutputFile(outputFile);
                audioRecord.prepare();
                audioRecord.start();
                Log.d("Mainactivity", "recorduji");
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.setVisibility(View.VISIBLE);
                chronometer.start();
                tvrecording_state.setText("Recording");
                tvrecording_state.setTextColor(getResources().getColor(R.color.red));
                tvrecording_state.setVisibility(View.VISIBLE);
                tvrecording_state.startAnimation(anim);
                record.setEnabled(false);
                stop.setEnabled(true);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(MainActivity.this.getClass().getSimpleName(), "Nullpointer");


        } catch (RuntimeException e) {
            Log.e(MainActivity.this.getClass().getSimpleName(), "Runtime exception");


        }
    }


    public void createAndShowFileDialog() {

        fileDialog = new Dialog(MainActivity.this, android.R.style.Theme_Holo_Dialog);
        fileDialog.setContentView(R.layout.file_name_dialog);
        Button buttonOk = (Button) fileDialog.findViewById(R.id.okButtonDialog);
        Button buttonCancel = (Button) fileDialog.findViewById(R.id.cancelButtonDialog);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_filename = (EditText) fileDialog.findViewById(R.id.createFileName);
                filename = edit_filename.getText().toString();
                Toast.makeText(getApplicationContext(), "Saved As : " + filename, Toast.LENGTH_SHORT).show();
                Log.d(MainActivity.this.getClass().getSimpleName(), filename);
                prepareAndStartRecording();
                fileDialog.dismiss();


            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileDialog.dismiss();
            }
        });
        fileDialog.setTitle("Enter File Name");
        fileDialog.show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    record.setEnabled(true);

                } else {


                    Toast.makeText(MainActivity.this, "Permission denied ", Toast.LENGTH_SHORT).show();
                }
                return;
            }


        }
    }


    public static void setTabColor(TabHost tabhost) {

        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.color.colorUnselected);
        }
        tabhost.getTabWidget().setCurrentTab(0);
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundResource(R.color.colorSelected);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        searchItem = menu.findItem(R.id.search);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sv.display();
                openKeyboard();
                return true;
            }
        });
        if (searchActive)
            sv.display();
        return true;

    }

    private void openKeyboard() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                sv.getSearchView().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                sv.getSearchView().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 10000);
    }

    private void openUrl(String url) {


        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_Officialwebsite:
                openUrl("http://www.karaoketexty.cz");
                break;
            case R.id.action_Exit:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    protected void onDestroy() {

        MainActivity.listSaved = null;

    }

    @Override
    public void onSearch(String query) {
        // Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        sv.hide();


        new AsyncClass(query).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void searchViewOpened() {

    }

    @Override
    public void searchViewClosed() {

    }

    @Override
    public void onItemClicked(String item) {
        new AsyncClass(item).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        sv.hide();
    }

    @Override
    public void onScroll() {

    }

    @Override
    public void error(String localizedMessage) {

    }

    @Override
    public void onCancelSearch() {
        searchActive = false;
        sv.hide();
    }


    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000);
        // delay
        // in
        // milliseconds
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {


                    if (page > 12) {
                        page = 0;
                        // Showing a toast for just testing purpose
                        Toast.makeText(getApplicationContext(), "Timer reached the end",
                                Toast.LENGTH_LONG).show();
                    } else {
                        viewPager.setCurrentItem(page++);
                    }
                }
            });

        }
    }

    class AsyncClass extends AsyncTask<Void, Void, Boolean> {
        public ArrayList<String> plabels;
        public ArrayList<String> podkazy;
        public ArrayList<String> labels;
        public ArrayList<String> odkazy;
        String searchedItem;

        public AsyncClass(String searchedItem) {

            this.searchedItem = searchedItem;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            Document doc = null;
            plabels = new ArrayList<>();
            podkazy = new ArrayList<>();
            try {
                doc = Jsoup.connect("http://www.karaoketexty.cz/search?q=" + searchedItem).get();
                Elements jedna = doc.select("ul.title").first().getAllElements();
                cislo = 0;
                for (Element e : jedna) {
                    {

                        plabels.add(e.select("li a").text());
                        podkazy.add(e.select("a[href]").attr("abs:href"));
                        cislo++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(Boolean result) {
            labels = new ArrayList<>();
            odkazy = new ArrayList<>();

            if (plabels.size() > 0) {
                for (int k = 1; k < cislo; k++) {
                    if (plabels.get(k) != "") {
                        labels.add(plabels.get(k));
                        odkazy.add(podkazy.get(k));
                    }


                }

                Intent i = new Intent(getApplicationContext(), Searched.class);
                i.putExtra("odkazy", odkazy);
                i.putExtra("labels", labels);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "Nic nenalezeno", Toast.LENGTH_SHORT).show();
            }


        }
    }

    class Hitparade extends AsyncTask<Void, Void, Boolean> {
        public String[] texty = new String[13];
        public String[] nazvy = new String[13];


        protected void onPreExecute() {
            viewPager = (ViewPager) findViewById(R.id.view_pager);
            viewPager.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);


        }


        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("INTERNET", "1");
            loadHitparade();
            // loadHitLyrics();
            Log.d("INTERNET", "4");
            if (prazdnypole) {
                prazdnypole = false;
                Log.d("NO POLE", "Nenahralo se nic do pole hitparady");
                Log.d("INTERNET", "5");
                return false;
            }
            Log.d("INTERNET", "6");
            loadImages();
            Log.d("INTERNET", "7");
            return true;
        }

        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);
            if (result) {
                //View pager ******************************************************
                viewPager.setVisibility(View.VISIBLE);
                adapter = new CustomSwipeAdapter(getApplicationContext(), hitparade, Thumbnails);
                viewPager.setAdapter(adapter);


                // pageSwitcher(4);
                //***************************************************
            }


        }


        public String getSource(String u) {
            try {
                URL url = new URL(u);
                URLConnection yc = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        yc.getInputStream()));
                String inputLine;
                StringBuilder sb = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine + "\n");
                in.close();
                String lastSource;
                lastSource = sb.toString();
                return lastSource;
            } catch (Exception e) {
                e.printStackTrace();
                String lastSource;
                lastSource = "error";
                return lastSource;
            }

        }


        public void loadHitparade() {

            Log.d("INTERNET", "2");
            ArrayList<String> hits = new ArrayList<>();
            links = new ArrayList<>();
            Element doc;
            try {
                doc = Jsoup.connect("http://www.karaoketexty.cz/").get();
                Log.d("INTERNET", "3");

                Element table = doc.select("table#hitparade").get(0);
                Elements rows = table.select("tr");
                Log.d("d", "1");
                for (int i = 0; i < rows.size(); i++) {
                    Log.d("d", "cyklus");
                    Element row = rows.get(i);
                    Elements cols = row.select("td.left");
                    hits.add(row.select("td.left a[href]").text());
                    links.add(row.select("a[href]").attr("abs:href"));
                    nazvy[i] = row.select("td.left").text();
                    runOnUiThread(new Runnable() {
                        public void run() {

                            // Toast.makeText(getApplicationContext(),nazvy[2],Toast.LENGTH_SHORT).show();

                        }
                    });
                    Log.d("Hity", "hit" + i + "link" + links.get(i));
                }

            } catch (java.net.UnknownHostException e) {
                prazdnypole = true;
                // Log.e("unknowneeeeee",e.toString());
            } catch (java.net.SocketTimeoutException e) {
                Toast.makeText(getApplicationContext(), "Velka odezva internetu", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.e("ERR", e.toString());
                e.printStackTrace();
            } catch (Exception e) {

                Log.d("ERROR", e.toString());

            }

            hitparade = new String[hits.size()];
            linkynatexty = new String[hits.size()];
            if (hits.size() == 0) {
                prazdnypole = true;
            }
            for (int i = 0; i < hits.size(); i++) {

                hitparade[i] = (i + 1) + ". " + hits.get(i);
                linkynatexty[i] = links.get(i);

                Log.d("Hit" + i, hitparade[i]);
            }
            Log.d("INTERNET", "4");
        }


        public void loadImages() {
            linksOnThumbnails = new ArrayList<String>();
            Thumbnails = new Bitmap[links.size()];
            System.out.println("LoadingImages ");
            for (int i = 0; i < links.size(); i++) {
                System.out.println("jedu cyklus image");
                Document doc = Jsoup.parse(links.get(i));
                System.out.println("parsuji: " + links.get(i));
                String code = getSource(links.get(i));
                String value = " id =\"tiscali-video\" ";
                int start = code.indexOf("id=\"tiscali-video\"");
                int end = code.indexOf(">", start);
                String urlObrazku = code.substring(start, end);
                start = urlObrazku.indexOf("data-youtube=\"");

                end = urlObrazku.indexOf("\"", start);
                urlObrazku = urlObrazku.substring(start + 14, end + 12);
                System.out.println("data-youtube=\"" + " " + start + " " + end);
                urlObrazku = "https://i.ytimg.com/vi/" + urlObrazku + "/mqdefault.jpg";
                System.out.println(urlObrazku);
                int file_length = 0;
                try {
                    URL url = new URL(urlObrazku);
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();


                    InputStream is = url.openStream();
                    Bitmap bmp = BitmapFactory.decodeStream(is);

                    Thumbnails[i] = bmp;


                } catch (java.io.FileNotFoundException r) {
                    Log.d("exception", "filenotfound");
                    r.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }

        public void loadHitLyrics() {

            //todo loadHitLyrics
            Document doc = null;
            textySongu = new String[13];
            String textSongu;
            String novytext;
            for (int i = 0; i < linkynatexty.length; i++) {
                try {
                    doc = Jsoup.connect(linkynatexty[i]).get();
                    Elements lyric = doc.select("p.text").first().getAllElements();
                    textSongu = lyric.toString();

                    //odstraneni zbytku kodu, ktery neni potreba
                    int zacatek = textSongu.indexOf("<p class=\"text\">") + 16;
                    int konec = textSongu.indexOf("</p>");
                    novytext = textSongu.substring(zacatek, konec);

                    String[] s = novytext.split("<br>"); // rozdeleni na radky
                    int delka = s.length;
                    String tmp = "";
                    for (int k = 0; k < delka; k++)  //zapsani do stringu s radky pro listview
                    {


                        tmp += s[k] + "\n";

                    }

                    novytext = tmp;
                    textySongu[i] = novytext;
                    novytext = "";
                    Log.d(MainActivity.this.getClass().getSimpleName(), textySongu[i]);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }


    }
}
