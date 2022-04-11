package com.example.bikerx.ui.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.bikerx.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.koin.core.Koin;
import org.koin.test.AutoCloseKoinTest;

import java.util.ArrayList;

import io.mockk.MockK;

@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest extends AutoCloseKoinTest {

    private FragmentScenario<HomeFragment> scenario;
    private HomeViewModel viewModel;

    @Before
    public void setUp() {
        scenario = FragmentScenario.launchInContainer(HomeFragment.class);
        scenario.moveToState(Lifecycle.State.STARTED);
    }

    @Test
    public void navigation() {
        scenario.onFragment(new FragmentScenario.FragmentAction<HomeFragment>() {
            @Override
            public void perform(@NonNull HomeFragment fragment) {

            }
        });
        onView(withId(R.id.recommended_route_see_all)).check(matches(withText("See all")));
    }

}
