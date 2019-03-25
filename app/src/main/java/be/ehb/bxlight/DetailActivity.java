package be.ehb.bxlight;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

import be.ehb.bxlight.model.entities.ComicPOI;

public class DetailActivity extends AppCompatActivity {

    ImageView ivBackground;
    TextView tvCharacter, tvAuthor, tvAddress, tvYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ivBackground = findViewById(R.id.iv_details_bg);
        tvCharacter = findViewById(R.id.tv_details_character);
        tvAuthor = findViewById(R.id.tv_details_author);
        tvAddress = findViewById(R.id.tv_details_address);
        tvYear = findViewById(R.id.tv_details_year);

        Serializable tempItem = getIntent().getSerializableExtra("item");

        if(tempItem instanceof ComicPOI)
            drawComic((ComicPOI) tempItem);
    }

    private void drawComic(ComicPOI item) {
        try {
            FileInputStream fis = getApplicationContext().openFileInput(item.getImage());
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            ivBackground.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        tvCharacter.setText(item.getCharacterName());
        tvAuthor.setText(item.getAuthor());
        tvYear.setText(item.getBuildYear());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            Address a = geocoder.getFromLocation(item.getLatitude(), item.getLongitude(), 1).get(0);
            tvAddress.setText(a.getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
