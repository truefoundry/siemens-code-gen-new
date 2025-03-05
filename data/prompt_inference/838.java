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

public class TC05_Landing_Page_Content_Check
{
    @Test
    void Landing_Page_Content_Check()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "842", tc ->
        {
            // Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 2
            boolean isTopRibbonCorrect = tc.button.exists(EButton.CONTACT) &&
                                         tc.button.exists(EButton.SETTINGS) &&
                                         tc.button.exists(EButton.NOTIFICATION_BELL) &&
                                         tc.button.exists(EButton.NAME_SHORTCUT) &&
                                         tc.button.exists(EButton.ADMIN_ICON);
            tc.addStepInfo("""
                    Landing page top ribbon content is according screenshot and consists of:
                    - Contact icon
                    - Settings icon
                    - Notification bell icon
                    - Icon with name shortcut
                    - Name of logged in user
                    - Admin icon
                    """, true, isTopRibbonCorrect, new ComparerOptions().takeScreenShotPlatform());

            // Step 3
            boolean areTilesCorrect = tc.tile.exists(ETile.REPORT_AN_ISSUE) &&
                                      tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS) &&
                                      tc.tile.exists(ETile.QUESTION_ABOUT_ORDER) &&
                                      tc.tile.exists(ETile.QUESTION_ABOUT_ACCOUNT) &&
                                      tc.tile.exists(ETile.REQUEST_ALLOCATION);
            tc.addStepInfo("""
                    Landing page tile content is according screenshot and consists of following tiles:
                    - Report an issue with an order or delivery
                    - Show me my Requests
                    - Question about an order or eSupport assistance
                    - Question about my Account
                    - Request Allocation or Saturday Delivery (SET Request)
                    """, true, areTilesCorrect, new ComparerOptions().takeScreenShotPlatform());

            // Step 4
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Report an Issue"));
            tc.addStepInfo("Page with details for reporting an issue is opened", true,
                    tc.browser.getPageTitle().contains("Report an Issue"), new ComparerOptions().takeScreenShotPlatform());

            // Step 5
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS));
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("My Requests"));
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened", true,
                    tc.browser.getPageTitle().contains("My Requests"), new ComparerOptions().takeScreenShotPlatform());

            // Step 6
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTION_ABOUT_ORDER));
            tc.tile.open(ETile.QUESTION_ABOUT_ORDER);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Question about Order"));
            tc.addStepInfo("Page with details for reporting an issue is opened", true,
                    tc.browser.getPageTitle().contains("Question about Order"), new ComparerOptions().takeScreenShotPlatform());

            // Step 7
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTION_ABOUT_ACCOUNT));
            tc.tile.open(ETile.QUESTION_ABOUT_ACCOUNT);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Question about Account"));
            tc.addStepInfo("Page with details for reporting an issue is opened", true,
                    tc.browser.getPageTitle().contains("Question about Account"), new ComparerOptions().takeScreenShotPlatform());

            // Step 8
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.REQUEST_ALLOCATION));
            tc.tile.open(ETile.REQUEST_ALLOCATION);
            WaitFor.condition(() -> tc.browser.getPageTitle().contains("Sales Efficiency Tool"));
            tc.addStepInfo("User is redirected to external Sales Efficiency tool page", true,
                    tc.browser.getPageTitle().contains("Sales Efficiency Tool"), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```