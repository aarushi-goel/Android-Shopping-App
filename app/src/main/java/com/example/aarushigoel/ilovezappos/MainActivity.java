package com.example.aarushigoel.ilovezappos;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.aarushigoel.ilovezappos.databinding.ActivityMainBinding;

import com.example.aarushigoel.ilovezappos.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private String TAG = MainActivity.class.getSimpleName();
    String url1 = "https://api.zappos.com/Search?term=";
    String url2 = "&key=b743e26728e16b81da139182bb2094357c31d331";
    String url;
    String query;
    ActivityMainBinding binding;
    Brand b;
    public FloatingActionButton fab;
    Animation rotateAnim;
    LinearLayout full_description;
    MenuItem item;
    TextView help_title_gest;
    HashMap<String,Brand> map;
    SearchView searchView;
    TextView introduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = new HashMap<>();
        Log.e("before handle",url1);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        TextView strike = (TextView) findViewById(R.id.originalPrice);
        strike.setPaintFlags(strike.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                rotateAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
                fab.startAnimation(rotateAnim);

                if(map.containsKey(query)){
                    map.remove(query);
                    fab.setImageResource(R.drawable.ic_mycart);
                    Toast.makeText(getBaseContext(), "Removed from cart!" , Toast.LENGTH_SHORT ).show();
                }
                else{
                    map.put(query,b);
                    fab.setImageResource(R.drawable.added_to_cart);
                    Toast.makeText(getBaseContext(), "Added to cart!" , Toast.LENGTH_SHORT ).show();
                }

            }
        });

        full_description = (LinearLayout) findViewById(R.id.full_description);
        full_description.setVisibility(View.GONE);

        full_description.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                toggle_contents(view);
            }
        });

       handleIntent(getIntent());
        introduction = (TextView) findViewById(R.id.introduction);
    }

    public void toggle_contents(View v){
        full_description.setVisibility(full_description.isShown() ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
         searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        this.invalidateOptionsMenu();
        item = menu.findItem(R.id.cart);
        item.setVisible(false);

        help_title_gest = (TextView)findViewById(R.id.help_title_gest);


        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Log.e("query",query);
            //use the query to search your data somehow
            url = url1 + query + url2;
            searchView.onActionViewCollapsed();
            fab.setImageResource(!map.containsKey(query) ? R.drawable.ic_mycart : R.drawable.added_to_cart);
            new GetBrand().execute();
        }
    }

    private class GetBrand extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray brands = jsonObj.getJSONArray("results");
                    JSONObject c = brands.getJSONObject(0);
                    String brandName = c.getString("brandName");
                    String imgurl = c.getString("thumbnailImageUrl");
                    String productId = c.getString("productId");
                    String originalPrice = c.getString("originalPrice");
                    String styleId = c.getString("styleId");
                    String colorId = c.getString("colorId");
                    String price = c.getString("price");
                    String percentOff = c.getString("percentOff");
                    String productUrl = c.getString("productUrl");
                    String productName = c.getString("productName");

                    b = new Brand(brandName, imgurl, productId, originalPrice, styleId, colorId, price, percentOff, productUrl,productName);

                    Log.e("aarushi", brandName + " " + imgurl + " " + productId + " " + originalPrice + " " + styleId + " " + colorId + " " + price + " " + percentOff + " " + productUrl+ " "+ productName);



                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "No product found",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(b!=null) {
                new ProductImage().execute(b.imgurl);
                item.setVisible(true);
                help_title_gest.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                introduction.setVisibility(View.GONE);

            }
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

    }
    private class ProductImage extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... b) {
            String urldisplay = b[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            b.image = new BitmapDrawable(result);
            binding.setB(b);
        }
    }
    }
