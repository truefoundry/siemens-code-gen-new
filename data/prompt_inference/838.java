```java
package Admin;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class TC_Landing_Page_TopRibbon_And_Tiles_Functionality_Check
{
    @Test
    void Landing_Page_TopRibbon_And_Tiles_Functionality_Check()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "LPTT001", tc ->
        {
            // Step 1: Log in to Digital Customer Portal
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            // Wait until one of the tiles (e.g., "Report an issue") is visible to ensure landing page is loaded
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is displayed", true, tc.tile.exists(ETile.REPORT_AN_ISSUE),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 2: Verify top ribbon icons and elements
            boolean contactIconExists = tc.button.exists(EButton.CONTACT);
            boolean settingsIconExists = tc.button.exists(EButton.SETTINGS);
            boolean notificationIconExists = tc.button.exists(EButton.NOTIFICATION_BELL);
            boolean shortcutIconExists = tc.button.exists(EButton.SHORTCUT);
            boolean loggedUserNameExists = tc.label.exists(ELabel.LOGGED_USER);  // Assuming ELabel.LOGGED_USER represents the logged in user's name display
            boolean adminIconExists = tc.button.exists(EButton.ADMIN);

            boolean topRibbonCorrect = contactIconExists && settingsIconExists && notificationIconExists
                    && shortcutIconExists && loggedUserNameExists && adminIconExists;

            tc.addStepInfo(
                    "Landing page top ribbon content is according to screenshot and consists of:" +
                    " Contact icon, Settings icon, Notification bell icon, Icon with name shortcut, " +
                    "Name of logged in user, Admin icon",
                    true,
                    topRibbonCorrect,
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 3: Verify that page contains required tiles
            boolean reportIssueTileExists = tc.tile.exists(ETile.REPORT_AN_ISSUE);
            boolean myRequestsTileExists = tc.tile.exists(ETile.SHOW_ME_All_REQUESTS);
            boolean orderOrESupportTileExists = tc.tile.exists(ETile.QUESTION_ABOUT_ORDER_OR_ESUPPORT);
            boolean accountQuestionTileExists = tc.tile.exists(ETile.QUESTION_ABOUT_ACCOUNT);
            boolean setRequestTileExists = tc.tile.exists(ETile.SET_REQUEST);

            boolean tilesCorrect = reportIssueTileExists && myRequestsTileExists &&
                    orderOrESupportTileExists && accountQuestionTileExists && setRequestTileExists;

            tc.addStepInfo(
                    "Landing page tile content is according to screenshot and consists of following tiles:" +
                    " 'Report an issue with an order or delivery', 'Show me my Requests', " +
                    "'Question about an order or eSuport assistance', 'Question about my Account', " +
                    "'Request Allocation or Saturday Delivery (SET Request)'",
                    true,
                    tilesCorrect,
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 4: Click on tile "Report an issue with an order or delivery"
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            // Assuming that the reporting issue page has a unique element, e.g., a header or a form field
            WaitFor.condition(() -> tc.edit.exists(EEdit.REPORT_ISSUE_HEADER));  
            tc.addStepInfo("Page with details for reporting an issue is opened",
                    true,
                    tc.edit.exists(EEdit.REPORT_ISSUE_HEADER),
                    new ComparerOptions().takeScreenShotPlatform());

            // Navigate back to the landing page before next tile click
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_All_REQUESTS));

            // Step 5: Click on tile "Show me my Requests"
            tc.tile.open(ETile.SHOW_ME_All_REQUESTS);
            // Assuming dashboard page is identified by existence of 'My Dashboard' tab
            WaitFor.condition(() -> tc.tab.exists(ETab.MY_DASHBOARD));
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened",
                    true,
                    tc.tab.exists(ETab.MY_DASHBOARD),
                    new ComparerOptions().takeScreenShotPlatform());

            // Navigate back to the landing page
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTION_ABOUT_ORDER_OR_ESUPPORT));

            // Step 6: Click on tile "Question about an order or eSuport assistance"
            tc.tile.open(ETile.QUESTION_ABOUT_ORDER_OR_ESUPPORT);
            // Reusing the same check as reporting issue page for demonstration
            WaitFor.condition(() -> tc.edit.exists(EEdit.REPORT_ISSUE_HEADER));
            tc.addStepInfo("Page with details for reporting an issue is opened",
                    true,
                    tc.edit.exists(EEdit.REPORT_ISSUE_HEADER),
                    new ComparerOptions().takeScreenShotPlatform());

            // Navigate back to the landing page
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTION_ABOUT_ACCOUNT));

            // Step 7: Click on tile "Question about my Account"
            tc.tile.open(ETile.QUESTION_ABOUT_ACCOUNT);
            // Reusing the same check as reporting issue page for demonstration
            WaitFor.condition(() -> tc.edit.exists(EEdit.REPORT_ISSUE_HEADER));
            tc.addStepInfo("Page with details for reporting an issue is opened",
                    true,
                    tc.edit.exists(EEdit.REPORT_ISSUE_HEADER),
                    new ComparerOptions().takeScreenShotPlatform());

            // Navigate back to the landing page
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.SET_REQUEST));

            // Step 8: Click on tile "Request Allocation or Saturday Delivery (SET Request)"
            tc.tile.open(ETile.SET_REQUEST);
            // Verify that the user is redirected to an external Sales Efficiency tool page.
            // This can be verified by checking that the current URL does not contain the QA portal domain.
            WaitFor.condition(() -> !tc.browser.getCurrentUrl().contains(ETestData.QA_ENV_URL));
            boolean redirectedToExternal = !tc.browser.getCurrentUrl().contains(ETestData.QA_ENV_URL);
            tc.addStepInfo("User is redirected to external Sales Efficiency tool page",
                    true,
                    redirectedToExternal,
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```