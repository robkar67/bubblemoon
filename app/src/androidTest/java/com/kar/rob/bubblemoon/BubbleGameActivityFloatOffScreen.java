package com.kar.rob.bubblemoon;

import android.test.ActivityInstrumentationTestCase2;
import android.view.WindowManager;

import com.robotium.solo.*;

public class BubbleGameActivityFloatOffScreen extends
		ActivityInstrumentationTestCase2<BubbleMoonActivity> {
	private Solo solo;

	public BubbleGameActivityFloatOffScreen() {
		super(BubbleMoonActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		getInstrumentation().runOnMainSync(new Runnable() {
			@Override
			public void run() {
				getActivity().getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			}
		});
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	public void testRun() {

		int shortDelay = 250, delay = 2000;

		// Wait for activity: BubbleMoonActivity
		solo.waitForActivity(BubbleMoonActivity.class,
				delay);

		// Click on action bar item
		solo.clickOnActionBarItem(com.kar.rob.bubblemoon.R.id.menu_restart);

		solo.sleep(delay);

		// Click to create a bubble
		solo.clickOnScreen(250.0f, 250.0f);

		// Check whether bubble appears
		boolean bubbleAppeared = solo.getCurrentViews(
				ItemView.class).size() > 0;
		for (int i = 0; i < 8 && !bubbleAppeared; i++) {
			solo.sleep(shortDelay);
			bubbleAppeared = solo.getCurrentViews(
					ItemView.class)
					.size() > 0;
		}

		// Assert that a bubble was displayed
		assertTrue("The bubble has not appeared", bubbleAppeared);

		solo.sleep(delay);

		// Assert that the bubble has left the screen
		assertEquals(
				"Bubble has not left the screen",
				0,
				solo.getCurrentViews(
						ItemView.class)
						.size());

	}
}
