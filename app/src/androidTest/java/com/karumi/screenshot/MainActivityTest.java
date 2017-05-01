/*
 * Copyright (C) 2017 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.screenshot;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.karumi.screenshot.di.MainComponent;
import com.karumi.screenshot.di.MainModule;
import com.karumi.screenshot.model.NetworkChecker;
import com.karumi.screenshot.model.Result;
import com.karumi.screenshot.model.SuperHero;
import com.karumi.screenshot.model.SuperHeroDetailError;
import com.karumi.screenshot.model.SuperHeroListError;
import com.karumi.screenshot.model.SuperHeroesRepository;
import com.karumi.screenshot.ui.view.MainActivity;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class MainActivityTest extends ScreenshotTest {

    @Rule
    public DaggerMockRule<MainComponent> daggerRule =
            new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
                    new DaggerMockRule.ComponentSetter<MainComponent>() {
                        @Override
                        public void setComponent(MainComponent component) {
                            SuperHeroesApplication app =
                                    (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                                            .getTargetContext()
                                            .getApplicationContext();
                            app.setComponent(component);
                        }
                    });

    @Rule
    public IntentsTestRule<MainActivity> activityRule =
            new IntentsTestRule<>(MainActivity.class, true, false);

    @Mock
    SuperHeroesRepository repository;
    @Mock
    NetworkChecker networkChecker;

    @Before
    public void setUp() throws Exception {
        TestConfig.RUNNING_UI_TESTS = true;
        when(networkChecker.hasNetworkConnection()).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        TestConfig.RUNNING_UI_TESTS = false;
    }

    // Test 1
    @Test
    public void showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes();

        Activity activity = startActivity();

        compareScreenshot(activity);
    }

    // Test 2
    @Test
    public void showsOneSuperHeroIfThereAreOneHero() throws Exception {
        givenThereAreSomeSuperHeroes(1, false);

        Activity activity = startActivity();

        compareScreenshot(activity);
    }

    // Test 3
    @Test
    public void testTwoSuperHeroScreen() throws Exception {
        givenThereAreSomeSuperHeroes(2, false);

        Activity activity = startActivity();

        compareScreenshot(activity);
    }

    // Test 4
    @Test
    public void testAvengersSuperHeroAvengers() throws Exception {
        givenThereAreSomeSuperHeroes(2, true);

        Activity activity = startActivity();

        compareScreenshot(activity);
    }

    // Test 5
    @Test
    public void testMultipleSuperHeroes() throws Exception {
        givenThereAreSomeSuperHeroes(10, false);

        Activity activity = startActivity();

        compareScreenshot(activity);
    }

    // Test 6
    @Test
    public void testShowNoNetworkError() throws Exception {
        givenThereIsNoNetwork();

        Activity activity = startActivity();

        compareScreenshot(activity);
    }

    // Test 7
    @Test
    public void showsProgressBarWhileLoading() throws Exception {
        // Lo hacemos con el doAnswer y el Thread.sleep porque el progressbar esta en movimiento y las
        // capturas al verificarse no coincidiran. Otra forma para solucionar esto seria hacer el progressbar
        // para los test determinado y con un valor de progreso fijo
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(2000);
                return new Result<List<SuperHero>,SuperHeroListError>(null, SuperHeroListError.NO_NETWORK);
            }
        }).when(repository).getAll();

        Activity activity = startActivity();

        compareScreenshot(activity);
    }

    private void givenThereIsNoNetwork() {
        when(repository.getAll()).thenReturn(new Result<List<SuperHero>, SuperHeroListError>(null, SuperHeroListError.NO_NETWORK));
    }


    private List<SuperHero> givenThereAreSomeSuperHeroes(int numberOfSuperHeroes, boolean avengers) {
        List<SuperHero> superHeroes = new LinkedList<>();
        for (int i = 0; i < numberOfSuperHeroes; i++) {
            String superHeroName = "SuperHero - " + i;
            String superHeroDescription = "Description Super Hero - " + i;
            SuperHero superHero = new SuperHero(superHeroName, null, avengers, superHeroDescription);
            superHeroes.add(superHero);
            when(repository.getByName(superHeroName)).thenReturn(new Result<SuperHero, SuperHeroDetailError>(superHero));
        }
        when(repository.getAll()).thenReturn(new Result<List<SuperHero>, SuperHeroListError>(superHeroes));
        return superHeroes;
    }

    private void givenThereAreNoSuperHeroes() {
        when(repository.getAll()).thenReturn(new Result<List<SuperHero>, SuperHeroListError>(Collections.<SuperHero>emptyList()));
    }

    private MainActivity startActivity() {
        return activityRule.launchActivity(null);
    }
}