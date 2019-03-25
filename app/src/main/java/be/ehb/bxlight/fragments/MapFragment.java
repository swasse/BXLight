package be.ehb.bxlight.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import be.ehb.bxlight.DetailActivity;
import be.ehb.bxlight.R;
import be.ehb.bxlight.model.PoiDatabase;
import be.ehb.bxlight.model.entities.ComicPOI;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {


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

            if(marker.getTag() instanceof ComicPOI) {
                ComicPOI c = (ComicPOI) marker.getTag();

                try {
                    FileInputStream fis = getActivity().openFileInput(c.getImage());
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    ivPhoto.setImageBitmap(bitmap);

                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    Address a = geocoder.getFromLocation(c.getLatitude(), c.getLongitude(), 1).get(0);
                    tvAdress.setText(a.getAddressLine(0));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tvTitle.setText(c.getCharacterName());
            }
            return myContentView;
        }
    };

    private final int REQUEST_LOCATION = 42;
    private GoogleMap mMap;
    private MapView mMapView;

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

        mMapView = root.findViewById(R.id.mv_bxl);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(infoWindowAdapter);
        mMap.setOnInfoWindowClickListener(this);

        updateCamera();
        drawMarkers();
        startLocationUpdates();
    }

    private void updateCamera() {
        LatLng belgCoord = new LatLng(50.8440974, 4.3488076);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(belgCoord, 14);
        mMap.animateCamera(cameraUpdate);
    }

    private void drawMarkers() {
        List<ComicPOI> comicPOIList = PoiDatabase.getInstance(getActivity()).getPoiDao().getAllComicArt();
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

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                requestPermissions(permissions, REQUEST_LOCATION);

            } else {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            for (int result : grantResults)
                if (result == PackageManager.PERMISSION_GRANTED)
                    mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(getActivity(), DetailActivity.class);
        i.putExtra("item", (Serializable) marker.getTag());
        getActivity().startActivity(i);
    }
}