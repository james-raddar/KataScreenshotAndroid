package com.karumi.screenshot;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.karumi.screenshot.di.MainComponent;
import com.karumi.screenshot.di.MainModule;
import com.karumi.screenshot.model.NetworkChecker;
import com.karumi.screenshot.model.Result;
import com.karumi.screenshot.model.SuperHero;
import com.karumi.screenshot.model.SuperHeroDetailError;
import com.karumi.screenshot.model.SuperHeroesRepository;
import com.karumi.screenshot.ui.view.SuperHeroDetailActivity;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class SuperHeroDetailActivityTest extends ScreenshotTest {

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
    public ActivityTestRule<SuperHeroDetailActivity> activityRule =
            new ActivityTestRule<>(SuperHeroDetailActivity.class, true, false);

    @Mock
    SuperHeroesRepository repository;
    @Mock
    NetworkChecker networkChecker;

    @Before
    public void setUp() throws Exception {
        when(networkChecker.hasNetworkConnection()).thenReturn(true);
    }

    @Test
    public void showsRegularSuperHero() throws Exception {
        SuperHero hero = givenThereIsASuperHero(false);

        Activity activity = startActivity(hero);

        compareScreenshot(activity);
    }

    @Test
    public void showsAvengerSuperHero() throws Exception {
        SuperHero hero = givenAnAvenger();

        Activity activity = startActivity(hero);

        compareScreenshot(activity);
    }

    @Test
    public void showsASuperHeroWithLongName() throws Exception {
        SuperHero hero = givenThereIsASuperHeroWithLargeName(false);

        Activity activity = startActivity(hero);

        compareScreenshot(activity);
    }

    @Test
    public void showsASuperHeroWithLongDescription() throws Exception {
        SuperHero hero = givenThereIsASuperHeroWithLargeDescription(false);

        Activity activity = startActivity(hero);

        compareScreenshot(activity);
    }

    @Test
    public void showsASuperHeroWithEmptyName() throws Exception {
        SuperHero hero = givenThereIsASuperHeroWithoutName(false);

        Activity activity = startActivity(hero);

        compareScreenshot(activity);
    }

    @Test
    public void showsASuperHeroWithEmptyDescription() throws Exception {
        SuperHero hero = givenThereIsASuperHeroWithoutDescription();

        Activity activity = startActivity(hero);

        compareScreenshot(activity);
    }

    @Test
    public void showsNotFoundError() throws Exception {
        String heroName = givenAnUnexistingSuperHeroName();

        Activity activity = startActivity(heroName);

        compareScreenshot(activity);
    }

    @Test
    public void showsNoNetworkError() throws Exception {
        String heroName = givenANoNetworkCase();

        Activity activity = startActivity(heroName);

        compareScreenshot(activity);
    }

    private String givenAnUnexistingSuperHeroName() {

        String fakeName = "Fatman";
        when(repository.getByName(fakeName)).thenReturn(new Result<SuperHero, SuperHeroDetailError>(null, SuperHeroDetailError.NOT_FOUND));
        return fakeName;
    }

    private String givenANoNetworkCase() {

        String fakeName = "Fatman";
        when(repository.getByName(fakeName)).thenReturn(new Result<SuperHero, SuperHeroDetailError>(null, SuperHeroDetailError.NO_NETWORK));
        return fakeName;
    }

    private SuperHero givenAnAvenger() {
        return givenThereIsASuperHero(true);
    }

    private SuperHero givenThereIsASuperHero(boolean isAvenger) {
        String superHeroName = "SuperHero";
        String superHeroDescription = "Super Hero Description";
        SuperHero superHero = new SuperHero(superHeroName, null, isAvenger, superHeroDescription);
        when(repository.getByName(superHeroName)).thenReturn(new Result<SuperHero, SuperHeroDetailError>(superHero));
        return superHero;
    }

    private SuperHero givenThereIsASuperHeroWithoutDescription() {
        String superHeroName = "SuperHero";
        String superHeroDescription = "";
        SuperHero superHero = new SuperHero(superHeroName, null, false, superHeroDescription);
        when(repository.getByName(superHeroName)).thenReturn(new Result<SuperHero, SuperHeroDetailError>(superHero));
        return superHero;
    }

    private SuperHero givenThereIsASuperHeroWithoutName(boolean isAvenger) {
        String superHeroName = "";
        String superHeroDescription = "Super Hero Description";
        SuperHero superHero = new SuperHero(superHeroName, null, isAvenger, superHeroDescription);
        when(repository.getByName(superHeroName)).thenReturn(new Result<SuperHero, SuperHeroDetailError>(superHero));
        return superHero;
    }

    private SuperHero givenThereIsASuperHeroWithLargeDescription(boolean isAvenger) {
        String superHeroName = "SuperHero";
        String superHeroDescription = "Super Hero Description";
        superHeroDescription += superHeroDescription;
        superHeroDescription += superHeroDescription;
        superHeroDescription += superHeroDescription;
        superHeroDescription += superHeroDescription;
        SuperHero superHero = new SuperHero(superHeroName, null, isAvenger, superHeroDescription);
        when(repository.getByName(superHeroName)).thenReturn(new Result<SuperHero, SuperHeroDetailError>(superHero));
        return superHero;
    }

    private SuperHero givenThereIsASuperHeroWithLargeName(boolean isAvenger) {
        String superHeroName = "SuperHero";
        String superHeroDescription = "Super Hero Description";
        superHeroName += superHeroName;
        superHeroName += superHeroName;
        superHeroName += superHeroName;
        superHeroName += superHeroName;
        superHeroName += superHeroName;
        SuperHero superHero = new SuperHero(superHeroName, null, isAvenger, superHeroDescription);
        when(repository.getByName(superHeroName)).thenReturn(new Result<SuperHero, SuperHeroDetailError>(superHero));
        return superHero;
    }

    private SuperHeroDetailActivity startActivity(SuperHero superHero) {
        return startActivity(superHero.getName());
    }

    private SuperHeroDetailActivity startActivity(String superHeroName) {
        Intent intent = new Intent();
        intent.putExtra("super_hero_name_key", superHeroName);
        return activityRule.launchActivity(intent);
    }
}