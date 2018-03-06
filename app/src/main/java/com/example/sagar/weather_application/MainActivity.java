package com.example.sagar.weather_application;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    EditText input_query;
    TextView output_query;
    TextView show_query;
    TextView error_message_display;
    ProgressBar loading_indicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input_query=findViewById(R.id.input_query);
        output_query=findViewById(R.id.query_output);
        show_query=findViewById(R.id.show_query);
        error_message_display=findViewById(R.id.error_message);
        loading_indicator=findViewById(R.id.loading_progress);
    }

    public void ShowJasonView()
    {
        output_query.setVisibility(View.VISIBLE);
        error_message_display.setVisibility(View.INVISIBLE);
    }
    public void ShowErrorMessage()
    {
        error_message_display.setVisibility(View.VISIBLE);
        output_query.setVisibility(View.INVISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public URL NetworkUtils(String GitHub_SearchQuery)
    {
        Uri uri=Uri.parse(query_constants.GITHUB_BASE_URL).buildUpon()
                .appendQueryParameter(query_constants.PARAM_QUERY,GitHub_SearchQuery)
                .appendQueryParameter(query_constants.PARAM_SORT,query_constants.sort_by)
                .build();
        URL url=null;
        try
        {
            url=new URL(uri.toString());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        return url;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.Github_Query) {
            show_query.setText(input_query.getText().toString());
            URL url = NetworkUtils(input_query.getText().toString());
            new AsyncUsedGithubQueryTask().execute(url);
        }
        return super.onOptionsItemSelected(item);
    }
    class AsyncUsedGithubQueryTask extends AsyncTask<URL,Void,String>//<Sent,Progress_return,Result>
    {
        @Override
        protected String doInBackground(URL... urls) {
            URL search_url = urls[0];
            String githubSearchUrl = null;
            try {
                githubSearchUrl = GetResponseFromHttpsUrl(search_url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchUrl;
        }

        @Override
        protected void onPreExecute() {
            loading_indicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            loading_indicator.setVisibility(View.INVISIBLE);
            if(s!=null && s!="")
            {
                output_query.setText(getHttpsFromJason(s));
                ShowJasonView();
            }
            else
            {
                ShowErrorMessage();
            }
        }
    }

    public static String GetResponseFromHttpsUrl (URL url) throws IOException
    {
        HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
        try
        {
            InputStream in=urlConnection.getInputStream();
            Scanner scanner =new Scanner(in);
            scanner.useDelimiter("\\A.");
            boolean hasString=scanner.hasNext();
            if(hasString)
                return scanner.next();
            else
                return null;
        }
        finally {
            urlConnection.disconnect();
        }
    }
    public String getHttpsFromJason(String jason)
    {
        String https="";
        try {
            JSONObject jason_object = new JSONObject(jason);
            JSONObject items=jason_object.getJSONObject("items");
            JSONObject owner=items.getJSONObject("owner");
            https=owner.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return https;
    }
}
