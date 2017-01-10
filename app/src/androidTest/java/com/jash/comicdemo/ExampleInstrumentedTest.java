package com.jash.comicdemo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.jash.comicdemo.entities.Comic;
import com.jash.comicdemo.entities.ComicDao;
import com.jash.comicdemo.entities.DaoMaster;
import com.jash.comicdemo.entities.DaoSession;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.jash.comicdemo", appContext.getPackageName());
    }
}
