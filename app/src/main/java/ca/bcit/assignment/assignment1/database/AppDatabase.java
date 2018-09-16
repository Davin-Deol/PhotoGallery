package ca.bcit.assignment.assignment1.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import ca.bcit.assignment.assignment1.DAO.CaptionDao;
import ca.bcit.assignment.assignment1.TypeConverters.Converters;
import ca.bcit.assignment.assignment1.models.Caption;

@Database(entities = {Caption.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract CaptionDao captionDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app-database")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }
}
