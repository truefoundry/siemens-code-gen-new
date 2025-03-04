package External_User;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TC01_External_User_Landing_Page_Layout_Check
{
    @Test
    void Landing_Page_Layout_Check()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.EXTERNAL_USER, "854", tc ->
        {
            //Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_WEBS_SHOP_URL, new CoreStartOptions().startIncognito(), new CoreStartOptions());
            tc.browser.externalUserLogin(ETestData.EXTERNAL_USER);
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.tile.exists(ETile.REPORT_AN_ISSUE),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.stepEvaluator.reset();
            WaitFor.condition(() -> !tc.browser.getActiveUser(EActions.USERNAME).isEmpty());
            tc.stepEvaluator
                    .add(() -> tc.menu.exists(EMenu.LANGUAGE), "Language icon is not present")
                    .add(() -> tc.menu.exists(EMenu.SETTINGS), "Settings icon is not present")
                    .add(() -> tc.button.exists(EButton.CONTACT), "Contact button is not present")
                    .add(() -> tc.button.exists(EButton.NOTIFICATIONS), "Notifications button is not present")
                    .add(() -> !tc.browser.getActiveUser(EActions.ICON).isEmpty(), "user icon is not present")
                    .add(() -> !tc.browser.getActiveUser(EActions.USERNAME).isEmpty(), "user name is not present")
                    .add(() ->  tc.button.exists(EButton.APP_NAME), "App icon is not present");

            tc.addStepInfo("""
                                        Check that page contains top ribbon with:
                                        - Siemens healthineers Logo
                                        - My Digital Lab Assistant (vX.X.X)
                                        - Contact icon
                                        - Settings icon
                                        - Notification bell icon
                                        - Icon with name shortcut
                                        - Name of logged in user""",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            tc.stepEvaluator.reset();
            List<String> wantedTiles = Arrays.asList("Report an issue with an order or delivery", "Show me my Requests",
                    "Question about an order or eSupport assistance", "Questions about my Account");
            List<String> actualTiles = tc.tile.getAllTilesName();
            boolean areEqual = wantedTiles.size() == actualTiles.size() &&
                    wantedTiles.stream().allMatch(item ->
                            Collections.frequency(wantedTiles, item) == Collections.frequency(actualTiles, item));
            tc.addStepInfo("""
                    Landing page tile content is according screenshot and consists of following tiles:
                    - Report an issue with an order or delivery
                    - Show me my Requests
                    - Question about an order or eSuport assistance
                    - Question about my Account""",
                    true ,areEqual, new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Which request would you like to submit?"));
            tc.addStepInfo("Page with details for reporting an issue is opened", true,
                    tc.browser.getPageTitle().contains("Which request would you like to submit?")
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.browser.navigateToURL(ETestData.QA_ENV_URL);
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS));
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("take a look at your Requests"));
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened", true,
                    tc.browser.getPageTitle().contains("take a look at your Requests")
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.browser.navigateToURL(ETestData.QA_ENV_URL);
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTIONS_ABOUT_AN_ORDER));
            tc.tile.open(ETile.QUESTIONS_ABOUT_AN_ORDER);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Which request would you like to submit?"));
            tc.addStepInfo("Page with details for reporting an issue is opened", true,
                    tc.browser.getPageTitle().contains("Which request would you like to submit?")
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.browser.navigateToURL(ETestData.QA_ENV_URL);
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTIONS_ABOUT_MY_ACCOUNT));
            tc.tile.open(ETile.QUESTIONS_ABOUT_MY_ACCOUNT);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Which request would you like to submit?"));
            tc.addStepInfo("Page with details for reporting an issue is opened", true,
                    tc.browser.getPageTitle().contains("Which request would you like to submit?")
                    , new ComparerOptions().takeScreenShotPlatform());

        });
    }
}



