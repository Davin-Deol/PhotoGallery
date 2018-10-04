package ca.bcit.assignment.assignment1.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import ca.bcit.assignment.assignment1.DAO.CaptionDao;
import ca.bcit.assignment.assignment1.TypeConverters.Converters;
import ca.bcit.assignment.assignment1.models.Caption;

@Database(entities = {Caption.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract CaptionDao captionDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app-database")
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Caption "
                    + " ADD COLUMN location TEXT");
        }
    };

    public static void destroyInstance() {
        instance = null;
    }
}
