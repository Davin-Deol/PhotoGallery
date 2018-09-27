package ca.bcit.assignment.assignment1;


import android.app.Activity;
import android.app.Instrumentation;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ca.bcit.assignment.assignment1.DAO.CaptionDao;
import ca.bcit.assignment.assignment1.database.AppDatabase;
import ca.bcit.assignment.assignment1.models.Caption;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchByCaptionTest {

    private AppDatabase db;
    private CaptionDao dao;
    private ArrayList<String> filePaths = null;
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void createDb() {
        File file = null;
        String path = null;
        Bitmap icon = null;
        FileOutputStream fos = null;

        Context context = InstrumentationRegistry.getTargetContext();
        ContextWrapper cw = new ContextWrapper(context);
        //File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Android/data/ca.bcit.assignment.assignment1/files/Pictures");

        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        db = AppDatabase.getInstance(context);
        dao = db.captionDao();

        filePaths = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            String fileName = i + ".jpg";
            file = new File(directory, fileName);

            icon = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);

            try {
                fos = new FileOutputStream(file);
                icon.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                Log.d("Exception Caught: ", e.getMessage());
            }

            filePaths.add(file.getAbsolutePath());
            dao.insertCaptions(new Caption(fileName));

        }
        Intent i = mActivityTestRule.getActivity().getIntent();
        mActivityTestRule.finishActivity();
        mActivityTestRule.launchActivity(i);
    }

    @After
    public void closeDb() throws IOException {
        File file = null;
        Caption[] captions = dao.loadAllCaptions();

        for (String filePath : filePaths) {
            file = new File(filePath);
            file.delete();
        }

        dao.deleteCaptions(captions);
        db.close();
    }

    @Test
    public void searchByCaptionTest() {
        Caption[] captions = dao.loadAllCaptions();
        ViewInteraction rightButton = onView(withId(R.id.rightButton));

        ViewInteraction textInputEditText = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.captionInputLayout),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("Red"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.updateCaptionButton), withText("Update"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        appCompatButton2.perform(click());

        rightButton.perform(click());

        ViewInteraction textInputEditText2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.captionInputLayout),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("Red2"), closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.updateCaptionButton), withText("Update"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        appCompatButton4.perform(click());

        rightButton.perform(click());

        ViewInteraction textInputEditText3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.captionInputLayout),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("Green"), closeSoftKeyboard());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.updateCaptionButton), withText("Update"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.searchButton), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction textInputEditText4 = onView(
                childAtPosition(
                        childAtPosition(
                                withId(R.id.captionInputLayout),
                                0),
                        0));
        textInputEditText4.perform(scrollTo(), replaceText("Red"), closeSoftKeyboard());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.applyFiltersButton), withText("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton8.perform(click());

        ViewInteraction editText = onView(
                allOf(withText("Red"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.captionInputLayout),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("Red")));

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.rightButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withText("Red2"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.captionInputLayout),
                                        0),
                                0),
                        isDisplayed()));
        editText2.check(matches(withText("Red2")));

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.rightButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction editText3 = onView(
                allOf(withText("Red"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.captionInputLayout),
                                        0),
                                0),
                        isDisplayed()));
        editText3.check(matches(withText("Red")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
