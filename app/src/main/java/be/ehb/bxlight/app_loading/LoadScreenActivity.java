package be.ehb.bxlight.app_loading;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import be.ehb.bxlight.MainActivity;
import be.ehb.bxlight.R;
import be.ehb.bxlight.app_loading.restfull.DownloadHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoadScreenActivity extends AppCompatActivity {

    DownloadHandler mDownloadHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);

        mDownloadHandler = new DownloadHandler(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (!sp.getBoolean("downloaded", false)) {
            startDownload();
        } else {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    private void startDownload() {
        Thread downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("https://bruxellesdata.opendatasoft.com/api/records/1.0/search/?dataset=comic-book-route&rows=100")
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();

                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("JSON", response.body() != null ? response.body().string() : null);
                    msg.setData(data);
                    mDownloadHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        downloadThread.start();
    }
}
