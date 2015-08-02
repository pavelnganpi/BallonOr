package com.paveynganpi.ballonor;

import android.test.ActivityInstrumentationTestCase2;

import com.parse.ParseUser;
import com.paveynganpi.ballonor.ui.MainActivity;
import com.robotium.solo.Solo;

/**
 * Created by paveynganpi on 8/2/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo mSolo;
    public ParseUser mCurrentUser;
    MainActivity mMainActivity = new MainActivity();

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception {
        mSolo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        mSolo.finishOpenedActivities();
    }

//    public void testRun(){
//        assertNotNull("LoginActivity is not null", getActivity());
//        mSolo.assertCurrentActivity("current actitity is MainActivity", MainActivity.class);
//    }


}

