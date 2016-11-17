package com.kar.rob.bubblemoon;

import android.test.ActivityInstrumentationTestCase2;
import android.view.WindowManager;

import com.robotium.solo.*;

public class BubbleGameActivityMultiple extends
		ActivityInstrumentationTestCase2<BubbleMoonActivity> {
	private Solo solo;

	public BubbleGameActivityMultiple() {
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
		solo.waitForActivity(BubbleMoonActivity.class, delay);

		// Set Still Mode
		solo.clickOnActionBarItem(com.kar.rob.bubblemoon.R.id.menu_still_mode);

		solo.sleep(delay);

		// Click to create a bubble
		solo.clickOnScreen(100, 100);

		solo.sleep(delay);
		
		// Assert that a bubble was displayed 
		assertEquals("The Bubble has not appeared", 1, solo.getCurrentViews(ItemView.class).size());

		// Click to create second bubble
		solo.clickOnScreen(500, 500);

		solo.sleep(delay);

		// Assert that a bubble was displayed 
		assertEquals("Second bubble has not appeared", 2, solo.getCurrentViews(ItemView.class).size());

		solo.sleep(delay);

		// Give misbehaving bubbles a chance to move off screen
		// Assert that there are two bubbles on the screen
		assertEquals(
				"There should be two bubbles on the screen",
				2,
				solo.getCurrentViews(
						ItemView.class)
						.size());
	}
}
