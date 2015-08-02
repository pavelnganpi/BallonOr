package com.paveynganpi.ballonor;

import android.test.ActivityInstrumentationTestCase2;

import com.paveynganpi.ballonor.ui.LoginActivity;
import com.paveynganpi.ballonor.ui.MainActivity;
import com.robotium.solo.Solo;

/**
 * Created by paveynganpi on 8/2/15.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo mSolo;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    public void setUp() throws Exception {
        mSolo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        mSolo.finishOpenedActivities();
    }

    public void testRun(){
        assertNotNull("LoginActivity is not null", getActivity());
        mSolo.assertCurrentActivity("current actitity is Login activity", LoginActivity.class);
        mSolo.clickOnButton(mSolo.getString(R.string.login_twitter));
        mSolo.waitForActivity(MainActivity.class);
        mSolo.assertCurrentActivity("current actitity is Main activity", MainActivity.class);
    }


}
