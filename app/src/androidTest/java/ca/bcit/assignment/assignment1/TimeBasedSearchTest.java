package ca.bcit.assignment.assignment1;


import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.bcit.assignment.assignment1.DAO.CaptionDao;
import ca.bcit.assignment.assignment1.database.AppDatabase;
import ca.bcit.assignment.assignment1.models.Caption;

import static org.junit.Assert.assertEquals;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TimeBasedSearchTest {
    private AppDatabase db;
    private CaptionDao dao;
    private ArrayList<String> fileNames = null;
    File directory;
    private ArrayList<Date> dates = null;
    private ArrayList<Date> fromDates = null;
    private ArrayList<Date> afterDates = null;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        ContextWrapper cw = new ContextWrapper(context);
        Caption caption;
        SimpleDateFormat sdf;
        directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        db = AppDatabase.getInstance(context);
        dao = db.captionDao();

        fileNames = new ArrayList<>();
        fromDates = new ArrayList<>();
        dates = new ArrayList<>();
        afterDates = new ArrayList<>();

        for (int i = 0; i < 9; i+=3) {
            String fileName = (i/3) + ".jpg";
            fileNames.add(fileName);
            try {
                sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                fromDates.add(sdf.parse("31-01-1982 10:"+(i)+":56"));
                sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                dates.add(sdf.parse("31-01-1982 10:"+(i+1)+":56"));
                sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                afterDates.add(sdf.parse("31-01-1982 10:"+(i+2)+":56"));
            } catch (ParseException pe) {
                Log.d("Parse Exception Caught: ", pe.getMessage());
            } catch (Exception e) {
                Log.d("Exception Caught: ", e.getMessage());
            }
            caption = new Caption(fileName);
            caption.setWhenCreated(dates.get(i/3));
            dao.insertCaptions(caption);
        }
    }

    @After
    public void closeDb() throws IOException {
        Caption caption = null;

        for (String fileName : fileNames) {
            caption = dao.findCaptionByImage(fileName);
            dao.deleteCaptions(caption);
        }

        db.close();
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void timeBasedSearchTest() {
        List<Caption> captions = null;
        Caption caption = null;
        for (int i = 0; i < 3; i++) {
            captions = dao.findImagesByDates(fromDates.get(i), afterDates.get(i));
            assertEquals(captions.size(), 1);
            caption = captions.get(0);
            assertEquals(caption.getImage(), i + ".jpg");
        }
    }

}
