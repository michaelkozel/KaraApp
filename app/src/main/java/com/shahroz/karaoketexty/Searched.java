package com.shahroz.karaoketexty;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class Searched extends AppCompatActivity {
    ListView listSearched;
    ArrayList<String> labels;
    ArrayList<String> odkazy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hledání");
        setSupportActionBar(toolbar);

        Intent intent = this.getIntent();

        odkazy = (ArrayList<String>) getIntent().getSerializableExtra("odkazy");
        labels = (ArrayList<String>) getIntent().getSerializableExtra("labels");

        listSearched = (ListView) findViewById(R.id.list_searched);
        ArrayAdapter<String> Ladapter = new ArrayAdapter<String>(this,
                R.layout.searched_item, R.id.text_searched_item, labels);
        listSearched.setAdapter(Ladapter);
        listSearched.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                //   Toast.makeText(getBaseContext(), odkazy.get(position).toString(), Toast.LENGTH_SHORT).show();
                String odkaz = odkazy.get(position);
                String jmeno_songu = labels.get(position);
                new Getlyric(odkaz, jmeno_songu).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    class Getlyric extends AsyncTask<Void, Void, Boolean> {
        String odkaz;
        String textSongu = "chyba";
        String novytext;
        String jmeno_songu;

        public Getlyric(String odkaz, String jmeno_songu) {

            this.odkaz = odkaz;
            this.jmeno_songu = jmeno_songu;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Document doc = null;

            try {
                doc = Jsoup.connect(odkaz).get();
                Elements lyric = doc.select("p.text").first().getAllElements();
                textSongu = lyric.toString();

                //odstraneni zbytku kodu, ktery neni potreba
                int zacatek = textSongu.indexOf("<p class=\"text\">") + 16;
                int konec = textSongu.indexOf("</p>");
                novytext = textSongu.substring(zacatek, konec);

                String[] s = novytext.split("<br>"); // rozdeleni na radky
                int delka = s.length;
                String tmp = "";
                for (int i = 0; i < delka; i++)  //zapsani do stringu s radky pro listview
                {


                    tmp += s[i] + "\n";

                }

                novytext = tmp;

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(Boolean result) {

            //Toast.makeText(getApplicationContext(),"post",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), DisplayLyrics.class);
            i.putExtra("text_songu", novytext);
            i.putExtra("jmeno_songu", jmeno_songu);
            i.putExtra("ulozeno", false);
            startActivity(i);


        }
    }

}
