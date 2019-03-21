package be.ehb.bxlight.fragments;


import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import be.ehb.bxlight.R;
import be.ehb.bxlight.model.ComicDatabase;
import be.ehb.bxlight.model.entities.ComicPOI;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap.InfoWindowAdapter infoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View myContentView = getActivity().getLayoutInflater().inflate(
                    R.layout.image_callout, null, false);

            ImageView ivPhoto = myContentView.findViewById(R.id.iv_streetart_card);
            TextView tvTitle = myContentView.findViewById(R.id.tv_streetart_card_title);
            TextView tvAdress = myContentView.findViewById(R.id.tv_streetart_card_adres);

            ComicPOI c = (ComicPOI) marker.getTag();

            try {
                FileInputStream fis = getActivity().openFileInput(c.getImage());
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                ivPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                Address a = geocoder.getFromLocation(c.getLatitude(), c.getLongitude(), 1).get(0);
                tvAdress.setText(a.getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            tvTitle.setText(c.getCharacterName());

            return myContentView;
        }

    };

    private GoogleMap mMap;
    private MapView landenMV;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        landenMV = root.findViewById(R.id.mv_bxl);
        landenMV.onCreate(savedInstanceState);
        landenMV.getMapAsync(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        landenMV.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        landenMV.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        landenMV.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(infoWindowAdapter);

        updateCamera();
        drawMarkers();
    }

    private void updateCamera() {
        LatLng belgCoord = new LatLng(50.8440974, 4.3488076);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(belgCoord, 14);
        mMap.animateCamera(cameraUpdate);
    }

    private void drawMarkers() {
        List<ComicPOI> comicPOIList = ComicDatabase.getInstance(getActivity()).getComicDAO().getAllComicArt();
        for (ComicPOI element : comicPOIList) {
            LatLng coord = new LatLng(element.getLatitude(), element.getLongitude());
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(coord)
                    .icon(BitmapDescriptorFactory.defaultMarker(40))
                    .title(element.getCharacterName())
            );
            m.setTag(element);
        }
    }
}
