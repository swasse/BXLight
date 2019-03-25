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
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

import be.ehb.bxlight.MainActivity;
import be.ehb.bxlight.model.PoiDatabase;
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

        switch (msg.what){
            case 0:
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                return;
            case 1:
                handleComics(msg.getData().getString("JSON"));
                break;
        }
    }

    private void handleComics(String dataRaw) {
        try {
            JSONObject data = new JSONObject(dataRaw);
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

                String tempName = fields.getJSONObject("photo").getString("id");
                DownloadImageTask task = new DownloadImageTask(tempName, context);
                task.execute(pictureURL);
                newComicPOI.setImage(tempName + ".jpg");

                try{
                    PoiDatabase.getInstance(context).getPoiDao().insertComic(newComicPOI);
                } catch (SQLiteConstraintException exception) {
                    PoiDatabase.getInstance(context).getPoiDao().updateComic(newComicPOI);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("downloaded_comic", true).apply();
        //Done parsing, update main
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Void>{

        private String name;
        private WeakReference<Context> contextReference;

        DownloadImageTask(String name, Context context) {
            this.name = name;
            contextReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                InputStream inputStream = new URL(strings[0]).openStream();    // Download Image from URL
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();

                FileOutputStream foStream = contextReference.get().openFileOutput(name+".jpg", Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, foStream);
                foStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
