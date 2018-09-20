package ca.bcit.assignment.assignment1;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ca.bcit.assignment.assignment1.database.DataStorageImp;
import ca.bcit.assignment.assignment1.database.IDataStore;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class InClassTest {

    @Test
    public void useAppContext() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals(4, 2 + 2);
    }

    @Test
    public void dataStorage() throws Exception {
        IDataStore idb = new DataStorageImp();
        idb.saveState("Testing");
        assertEquals("Testing", idb.getState());
    }
}
