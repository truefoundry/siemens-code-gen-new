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

public class TC05_Landing_Page_Content_Check {

    @Test
    void Landing_Page_Content_Check() {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "842", tc -> {
            // Step 1: Log in to Digital Customer Portal
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 2: Check top ribbon content
            boolean topRibbonContent = tc.ribbon.exists(ERibbon.SIEMENS_LOGO) &&
                    tc.ribbon.exists(ERibbon.MY_DIGITAL_LAB_ASSISTANT) &&
                    tc.ribbon.exists(ERibbon.CONTACT_ICON) &&
                    tc.ribbon.exists(ERibbon.LANGUAGE_ICON) &&
                    tc.ribbon.exists(ERibbon.SETTINGS_ICON) &&
                    tc.ribbon.exists(ERibbon.NOTIFICATION_BELL_ICON) &&
                    tc.ribbon.exists(ERibbon.NAME_SHORTCUT_ICON) &&
                    tc.ribbon.exists(ERibbon.LOGGED_IN_USER_NAME) &&
                    tc.ribbon.exists(ERibbon.ADMIN_ICON);
            tc.addStepInfo("Landing page top ribbon content is according to screenshot", true, topRibbonContent,
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 3: Check tile content
            boolean tileContent = tc.tile.exists(ETile.REPORT_AN_ISSUE) &&
                    tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS) &&
                    tc.tile.exists(ETile.QUESTION_ABOUT_ORDER) &&
                    tc.tile.exists(ETile.QUESTION_ABOUT_ACCOUNT) &&
                    tc.tile.exists(ETile.REQUEST_ALLOCATION);
            tc.addStepInfo("Landing page tile content is according to screenshot", true, tileContent,
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 4: Click on tile "Report an issue with an order or delivery"
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            WaitFor.condition(() -> tc.page.exists(EPage.REPORT_ISSUE_DETAILS));
            tc.addStepInfo("Page with details for reporting an issue is opened", true,
                    tc.page.exists(EPage.REPORT_ISSUE_DETAILS), new ComparerOptions().takeScreenShotPlatform());

            // Step 5: Click on tile "Show me my Requests"
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.page.exists(EPage.REQUESTS_DASHBOARD));
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened", true,
                    tc.page.exists(EPage.REQUESTS_DASHBOARD), new ComparerOptions().takeScreenShotPlatform());

            // Step 6: Click on tile "Question about an order or eSupport assistance"
            tc.tile.open(ETile.QUESTION_ABOUT_ORDER);
            WaitFor.condition(() -> tc.page.exists(EPage.REPORT_ISSUE_DETAILS));
            tc.addStepInfo("Page with details for reporting an issue is opened", true,
                    tc.page.exists(EPage.REPORT_ISSUE_DETAILS), new ComparerOptions().takeScreenShotPlatform());

            // Step 7: Click on tile "Question about my Account"
            tc.tile.open(ETile.QUESTION_ABOUT_ACCOUNT);
            WaitFor.condition(() -> tc.page.exists(EPage.REPORT_ISSUE_DETAILS));
            tc.addStepInfo("Page with details for reporting an issue is opened", true,
                    tc.page.exists(EPage.REPORT_ISSUE_DETAILS), new ComparerOptions().takeScreenShotPlatform());

            // Step 8: Click on tile "Request Allocation or Saturday Delivery (SET Request)"
            tc.tile.open(ETile.REQUEST_ALLOCATION);
            WaitFor.condition(() -> tc.browser.getCurrentUrl().contains("SalesEfficiencyTool"));
            tc.addStepInfo("User is redirected to external Sales Efficiency tool page", true,
                    tc.browser.getCurrentUrl().contains("SalesEfficiencyTool"), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```