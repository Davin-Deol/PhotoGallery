package ca.bcit.assignment.assignment1.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

import ca.bcit.assignment.assignment1.models.Caption;

@Dao
public interface CaptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCaptions(Caption... captions);

    @Query("SELECT * FROM Caption")
    public Caption[] loadAllCaptions();

    @Query("SELECT * FROM Caption WHERE image = :image LIMIT 1")
    public Caption findCaptionByImage(String image);

    @Query("SELECT * FROM Caption WHERE (whenCreated BETWEEN :from AND :to) AND (caption LIKE :search)")
    public List<Caption> findImagesWithCaptionBetweenDates(Date from, Date to, String search);

    @Query("SELECT * FROM Caption WHERE whenCreated BETWEEN :from AND :to")
    public List<Caption> findImagesBetweenDates(Date from, Date to);

    @Update
    public void updateCaptions(Caption... captions);

    @Delete
    public void deleteCaptions(Caption... captions);
}
