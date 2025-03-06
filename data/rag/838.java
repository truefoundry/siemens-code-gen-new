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
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.ribbon.exists(ERibbon.SIEMENS_LOGO), "Siemens Healthineers Logo not found")
                    .add(() -> tc.ribbon.exists(ERibbon.MY_DIGITAL_LAB_ASSISTANT), "My Digital Lab Assistant not found")
                    .add(() -> tc.ribbon.exists(ERibbon.CONTACT_ICON), "Contact icon not found")
                    .add(() -> tc.ribbon.exists(ERibbon.LANGUAGE_ICON), "Language icon not found")
                    .add(() -> tc.ribbon.exists(ERibbon.SETTINGS_ICON), "Settings icon not found")
                    .add(() -> tc.ribbon.exists(ERibbon.NOTIFICATION_BELL_ICON), "Notification bell icon not found")
                    .add(() -> tc.ribbon.exists(ERibbon.NAME_SHORTCUT_ICON), "Icon with name shortcut not found")
                    .add(() -> tc.ribbon.exists(ERibbon.LOGGED_IN_USER_NAME), "Name of logged in user not found")
                    .add(() -> tc.ribbon.exists(ERibbon.ADMIN_ICON), "Admin icon not found");
            tc.addStepInfo("Landing page top ribbon content is according to screenshot", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 3: Check tile content
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE), "Tile 'Report an issue with an order or delivery' not found")
                    .add(() -> tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS), "Tile 'Show me my Requests' not found")
                    .add(() -> tc.tile.exists(ETile.QUESTION_ABOUT_ORDER), "Tile 'Question about an order or eSupport assistance' not found")
                    .add(() -> tc.tile.exists(ETile.QUESTION_ABOUT_ACCOUNT), "Tile 'Question about my Account' not found")
                    .add(() -> tc.tile.exists(ETile.REQUEST_ALLOCATION), "Tile 'Request Allocation or Saturday Delivery (SET Request)' not found");
            tc.addStepInfo("Landing page tile content is according to screenshot", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 4: Click on tile 'Report an issue with an order or delivery'
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            WaitFor.condition(() -> tc.page.exists(EPage.REPORT_ISSUE_DETAILS));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, tc.page.exists(EPage.REPORT_ISSUE_DETAILS),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 5: Click on tile 'Show me my Requests'
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.page.exists(EPage.REQUESTS_DASHBOARD));
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened", true, tc.page.exists(EPage.REQUESTS_DASHBOARD),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 6: Click on tile 'Question about an order or eSupport assistance'
            tc.tile.open(ETile.QUESTION_ABOUT_ORDER);
            WaitFor.condition(() -> tc.page.exists(EPage.REPORT_ISSUE_DETAILS));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, tc.page.exists(EPage.REPORT_ISSUE_DETAILS),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 7: Click on tile 'Question about my Account'
            tc.tile.open(ETile.QUESTION_ABOUT_ACCOUNT);
            WaitFor.condition(() -> tc.page.exists(EPage.REPORT_ISSUE_DETAILS));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, tc.page.exists(EPage.REPORT_ISSUE_DETAILS),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 8: Click on tile 'Request Allocation or Saturday Delivery (SET Request)'
            tc.tile.open(ETile.REQUEST_ALLOCATION);
            WaitFor.condition(() -> tc.browser.getCurrentUrl().contains("SalesEfficiencyTool"));
            tc.addStepInfo("User is redirected to external Sales Efficiency tool page", true, tc.browser.getCurrentUrl().contains("SalesEfficiencyTool"),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```