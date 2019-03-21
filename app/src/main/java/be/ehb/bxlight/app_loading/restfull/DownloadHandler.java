package be.ehb.bxlight.app_loading.restfull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import be.ehb.bxlight.MainActivity;
import be.ehb.bxlight.model.ComicDatabase;
import be.ehb.bxlight.model.entities.ComicPOI;

/**
 * Created by Banaan on 20/01/2038. ;)
 */
public class DownloadHandler extends Handler {

    private Context context;

    public DownloadHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        try {
            JSONObject data = new JSONObject(msg.getData().getString("JSON"));
            JSONArray records = data.getJSONArray("records");

            int nrOfElements = records.length();
            for(int i = 0; i < nrOfElements; i++){
                ComicPOI newComicPOI = new ComicPOI();

                JSONObject currentElement = records.getJSONObject(i);
                newComicPOI.setId( currentElement.getString("recordid") );

                JSONObject fields = currentElement.getJSONObject("fields");
                newComicPOI.setAuthor( fields.getString("auteur_s") );
                newComicPOI.setBuildYear( fields.getString("annee"));
                newComicPOI.setCharacterName( fields.getString("personnage_s"));
                newComicPOI.setLatitude( fields.getJSONArray("coordonnees_geographiques").getDouble(0));
                newComicPOI.setLongitude( fields.getJSONArray("coordonnees_geographiques").getDouble(1));

                String pictureURL = "https://bruxellesdata.opendatasoft.com/explore/dataset/" +
                        currentElement.getString("datasetid") +
                        "/images/" +
                        fields.getJSONObject("photo").getString("id") +
                        "/300";

                DownloadImageTask task = new DownloadImageTask(fields.getJSONObject("photo").getString("id"));
                task.execute(pictureURL);

                newComicPOI.setImage(task.get());

                try{
                    ComicDatabase.getInstance(context).getComicDAO().insert(newComicPOI);
                } catch (SQLiteConstraintException exception) {
                    ComicDatabase.getInstance(context).getComicDAO().update(newComicPOI);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("downloaded", true).apply();

        //Done parsing, open main
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, String>{

        private String name;

        DownloadImageTask(String name) {
            this.name = name;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                InputStream inputStream = new URL(strings[0]).openStream();    // Download Image from URL
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();

                FileOutputStream foStream = context.openFileOutput(name+".jpeg", Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, foStream);
                foStream.close();

                return name+".jpeg";

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
