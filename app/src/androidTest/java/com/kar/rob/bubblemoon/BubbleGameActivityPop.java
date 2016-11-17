package com.kar.rob.bubblemoon;

import android.test.ActivityInstrumentationTestCase2;
import android.view.WindowManager;

import com.robotium.solo.*;

public class BubbleGameActivityPop extends
		ActivityInstrumentationTestCase2<BubbleMoonActivity> {
	private Solo solo;

	public BubbleGameActivityPop() {
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

		int delay = 2000;

		// Wait for activity: BubbleMoonActivity
		solo.waitForActivity(BubbleMoonActivity.class,
				delay);

		// Set Still Mode
		solo.clickOnActionBarItem(com.kar.rob.bubblemoon.R.id.menu_still_mode);

		solo.sleep(delay);
		
		// Click to create a bubble
		solo.clickOnScreen(250, 250);

		solo.sleep(delay);

		// Assert that a bubble was displayed
		assertEquals(
				"Bubble hasn't appeared",
				1,
				solo.getCurrentViews(
						ItemView.class)
						.size());

		// Click to remove the same bubble
		solo.clickOnScreen(250, 250);

		solo.sleep(delay);

		// Assert that there are no more bubbles
		assertEquals(
				"The bubble was not popped",
				0,
				solo.getCurrentViews(
						ItemView.class)
						.size());

	}
}
