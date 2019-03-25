package be.ehb.bxlight.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import be.ehb.bxlight.model.entities.ComicPOI;

/**
 * Created by Banaan on 20/01/2038. ;)
 */
@Database(entities = {ComicPOI.class}, version = 1, exportSchema = false)
public abstract class PoiDatabase extends RoomDatabase {

    private static PoiDatabase instance;

    public abstract PoiDao getPoiDao();

    public static PoiDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, PoiDatabase.class, "poi.db").allowMainThreadQueries().build();
        }
        return instance;
    }
}