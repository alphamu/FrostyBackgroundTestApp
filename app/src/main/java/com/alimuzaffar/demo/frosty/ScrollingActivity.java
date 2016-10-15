package com.alimuzaffar.demo.frosty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import static com.alimuzaffar.demo.frosty.R.id.fab;

public class ScrollingActivity extends AppCompatActivity {
    private RenderScript rs = null;

    ImageView imgBgBlur;
    View imgBg;
    Bitmap mBlurBitmap;
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(fab);
        imgBgBlur = (ImageView) findViewById(R.id.imgBgBlur);
        imgBg = findViewById(R.id.container);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScrollingActivity.this, CardViewActivity.class));
            }
        });

        final NestedScrollView nestedScrollView =
                (NestedScrollView) findViewById(R.id.nestedScrollView);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    Log.d("TAG", "Collapsed");
                    // Collapsed
                } else {
                    //Log.d("TAG", "Top for BlurImg" + imgBgBlur.getTop());
                    imgBgBlur.setVisibility(View.GONE);
                }
            }
        });

        nestedScrollView.setOnScrollChangeListener(
                new NestedScrollView.OnScrollChangeListener() {

                    @Override
                    public void onScrollChange(NestedScrollView v,
                                               int scrollX, int scrollY,
                                               int oldScrollX, int oldScrollY) {
                        imgBgBlur.setTop(v.getTop() - scrollY);
                        imgBgBlur.setVisibility(View.VISIBLE);
                    }
                });

        nestedScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mBlurBitmap == null) {
                    mBlurBitmap = createBlurBitmap();
                    imgBgBlur.setImageBitmap(mBlurBitmap);
                }
            }
        });
        rs = RenderScript.create(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap captureView(View view) {
        //Find the view we are after
        //Create a Bitmap with the same dimensions
        Bitmap image = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_4444); //reduce quality and remove opacity
        //Draw the view inside the Bitmap
        Canvas canvas = new Canvas(image);
        view.draw(canvas);

        //Make it frosty
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00222222); // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(image, 0, 0, paint);

        return image;
    }

    public Bitmap createBlurBitmap() {
        Bitmap bitmap = captureView(imgBg);
        if (bitmap != null) {
            ImageHelper.blurBitmapWithRenderscript(rs, bitmap);
        }

        return bitmap;
    }

    @Override
    protected void onDestroy() {
        if (mBlurBitmap != null) {
            mBlurBitmap.recycle();
        }
        super.onDestroy();
    }

}
