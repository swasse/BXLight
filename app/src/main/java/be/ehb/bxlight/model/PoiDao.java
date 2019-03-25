package be.ehb.bxlight.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import be.ehb.bxlight.model.entities.ComicPOI;

/**
 * Created by Banaan on 20/01/2038. ;)
 */
@Dao
public interface PoiDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertComic(ComicPOI item);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateComic(ComicPOI item);

    @Query("SELECT * FROM ComicPOI")
    List<ComicPOI> getAllComicArt();
}
