package com.shahroz.karaoketexty;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


public class DisplayLyrics extends AppCompatActivity {
    TextView text_lyric;
    SeekBar seekBar;

    String jmeno_songu = "jmeno_songu";
    String text_songu;

    public int id;
    public String name, interpret, text;
    public UserOperations uop;
    public Context c = this;
    int count;
    Boolean ulozeno;         // kontrola jestli je song nacitan z databaze nebo z internetu
    Boolean obsazeno = false; // kontrola jestli je song uz v databazi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        setContentView(R.layout.activity_display_lyrics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        jmeno_songu = intent.getStringExtra("jmeno_songu");
        toolbar.setTitle(jmeno_songu);
        setSupportActionBar(toolbar);

        Toast.makeText(getApplicationContext(), "Zde můžete klikem na + uložit text písničky, ten pak bude v tabu uložené", Toast.LENGTH_SHORT).show();

        text_songu = intent.getStringExtra("text_songu");
        ulozeno = intent.getBooleanExtra("ulozeno", false);
        seekBar = (SeekBar) findViewById(R.id.seekBar);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int value = (progress + 5);
                Log.d("Seekbar", String.valueOf(progress) + "  " + String.valueOf(value));

                text_lyric.setTextSize(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        Log.d("ulozeno", ulozeno.toString());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        if (ulozeno) {
            fab.setImageResource(R.drawable.ic_action_remove);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Song byl smazán", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    aktualizovatList();
                }
            });
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ulozText();
                    Snackbar.make(view, "Text uložen", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        text_lyric = (TextView) findViewById(R.id.text_lyrics);
        text_lyric.setText(text_songu);

    }

    private void aktualizovatList() {


       /* for(int i=0;i<20;i++){

pole[i]="jmenosonguop";

        }
        */
        ArrayList<String> song_lyrics = new ArrayList<String>();

        uop = new UserOperations(c);
        count = 0;

        uop.delete(uop, jmeno_songu, "interpret");
        Cursor cursor = uop.read(uop);

        while (cursor.moveToNext()) {


            id = cursor.getInt(0);
            name = cursor.getString(1);
            interpret = cursor.getString(2);
            text = cursor.getString(3);

            //tady to postupně vkládej do listview, jinak se bude pokaždé tohle všechno přeukládat a zůstane ti tam jen poslední song
            song_lyrics.add(name);
            count++;


        }

        Log.d("věcí", Integer.toString(count));
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.saved_item, R.id.list_item, song_lyrics);
        MainActivity.listSaved.setAdapter(adapter);
        MainActivity.listSaved.deferNotifyDataSetChanged();
    }

    private void ulozText() {



       /* for(int i=0;i<20;i++){

pole[i]="jmenosonguop";

        }
        */
        ArrayList<String> textysongu = new ArrayList<String>();

        uop = new UserOperations(this);
        count = 0;


        Cursor cursor = uop.read(uop);

        while (cursor.moveToNext()) {


            id = cursor.getInt(0);
            name = cursor.getString(1);
            interpret = cursor.getString(2);
            text = cursor.getString(3);
            if (name.equals(jmeno_songu)) {
                obsazeno = true;
                Toast.makeText(getApplicationContext(), "Tato píseň je již uložena", Toast.LENGTH_SHORT).show();
            }
            //tady to postupně vkládej do listview, jinak se bude pokaždé tohle všechno přeukládat a zůstane ti tam jen poslední song
            textysongu.add(name);
            count++;


        }
        if (!obsazeno) {
            uop.insert(uop, jmeno_songu, "interpret", text_songu);
            textysongu.add(jmeno_songu);
        }

        Log.d("věcí", Integer.toString(count));
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.saved_item, R.id.list_item, textysongu);
        MainActivity.listSaved.setAdapter(adapter);
    }

}
