package be.ehb.bxlight.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ehb.bxlight.model.entities.ComicPOI;

/**
 * Created by Banaan on 20/01/2038. ;)
 */
@Database(entities = {ComicPOI.class,}, version = 1, exportSchema = false)
public abstract class ComicDatabase extends RoomDatabase {

    private static ComicDatabase instance;

    public abstract ComicDAO getComicDAO();

    public static ComicDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, ComicDatabase.class, "comics.db").allowMainThreadQueries().build();
        }
        return instance;
    }

}