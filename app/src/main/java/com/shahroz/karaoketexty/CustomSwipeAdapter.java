package com.shahroz.karaoketexty;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by michael on 17. 8. 2016.
 */
public class CustomSwipeAdapter extends PagerAdapter {
    private int[] image_resources = {R.drawable.video1, R.drawable.video2, R.drawable.video3, R.drawable.video4,
            R.drawable.video5, R.drawable.video1, R.drawable.video1, R.drawable.video1, R.drawable.video1, R.drawable.video1,
            R.drawable.video1, R.drawable.video1, R.drawable.video1,
    };

    private Bitmap[] Thumbnails;
    private String[] nazvyoffline = {"Offline"};
    private String[] image_names = {"Offline", "Offline", "Offline", "Offline", "Offline", "Offline",
            "Offline", "Offline", "Offline", "Offline", "Offline", "Offline", "Offline",};
    private Context ctx;
    private LayoutInflater layoutInflater;



    public CustomSwipeAdapter(Context ctx) {


        this.ctx = ctx;

    }

    public CustomSwipeAdapter(Context ctx, String[] image_names, Bitmap[] Thumbnails) {
        this.image_names = image_names;
        this.Thumbnails = Thumbnails;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return image_resources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        final ImageView imageView = (ImageView) item_view.findViewById(R.id.image_view);
        TextView textView = (TextView) item_view.findViewById(R.id.image_count);
        if (image_names == null) {
            textView.setText(nazvyoffline[0]);
        } else {
            textView.setText(image_names[position]);
        }
        if (Thumbnails == null) {
            imageView.setImageResource(image_resources[position]);
        } else {
            imageView.setImageBitmap(Thumbnails[position]);
        }

        item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(ctx, "clicked" + position, Toast.LENGTH_SHORT).show();






            }
        });
        container.addView(item_view);
        return item_view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


    public void neco()
    {


    }
}
