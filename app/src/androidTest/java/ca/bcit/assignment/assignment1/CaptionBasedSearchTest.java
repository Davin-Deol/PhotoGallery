package ca.bcit.assignment.assignment1;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CaptionBasedSearchTest {

    private AppDatabase db;
    private CaptionDao dao;
    private ArrayList<String> fileNames = null;
    File directory;
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void createDb() {
        File file = null;
        Bitmap icon = null;
        FileOutputStream fos = null;

        Context context = InstrumentationRegistry.getTargetContext();
        ContextWrapper cw = new ContextWrapper(context);

        directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        db = AppDatabase.getInstance(context);
        dao = db.captionDao();

        fileNames = new ArrayList<>();

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

            fileNames.add(fileName);
            dao.insertCaptions(new Caption(fileName));

        }
        Intent i = mActivityTestRule.getActivity().getIntent();
        mActivityTestRule.finishActivity();
        mActivityTestRule.launchActivity(i);
    }

    @After
    public void closeDb() throws IOException {
        File file = null;
        Caption caption = null;

        for (String fileName : fileNames) {
            caption = dao.findCaptionByImage(fileName);
            file = new File(directory, fileName);
            file.delete();
            dao.deleteCaptions(caption);
        }

        db.close();
    }

    @Test
    public void captionBasedSearchTest() {
        ViewInteraction textInputEditText = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.captionInputLayout),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("YMkEjgkT"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.updateCaptionButton), withText("Update"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction rightButton = onView(
                allOf(withId(R.id.rightButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        rightButton.perform(click());

        ViewInteraction textInputEditText2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.captionInputLayout),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("dCtXMqnZ"), closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.updateCaptionButton), withText("Update"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
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
        textInputEditText3.perform(replaceText("QpCgCzQz"), closeSoftKeyboard());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.updateCaptionButton), withText("Update"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        appCompatButton6.perform(click());

        rightButton.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.searchButton), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction textInputEditText4 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.captionInputLayout),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText4.perform(replaceText("dCtXMqnZ"), closeSoftKeyboard());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.applyFiltersButton), withText("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButton8.perform(click());

        ViewInteraction editText = onView(
                allOf(withText("dCtXMqnZ"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.captionInputLayout),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("dCtXMqnZ")));
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
