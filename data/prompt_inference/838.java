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
            boolean isTopRibbonCorrect = tc.ribbon.containsIcons(
                    EIcon.SIEMENS_LOGO,
                    EIcon.MY_DIGITAL_LAB_ASSISTANT,
                    EIcon.CONTACT,
                    EIcon.LANGUAGE,
                    EIcon.SETTINGS,
                    EIcon.NOTIFICATION_BELL,
                    EIcon.NAME_SHORTCUT,
                    EIcon.LOGGED_IN_USER_NAME,
                    EIcon.ADMIN
            );
            tc.addStepInfo("Landing page top ribbon content is according to screenshot", true, isTopRibbonCorrect,
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 3
            boolean areTilesCorrect = tc.tile.containsTiles(
                    ETile.REPORT_AN_ISSUE,
                    ETile.SHOW_ME_MY_REQUESTS,
                    ETile.QUESTION_ABOUT_ORDER,
                    ETile.QUESTION_ABOUT_ACCOUNT,
                    ETile.REQUEST_ALLOCATION
            );
            tc.addStepInfo("Landing page tile content is according to screenshot", true, areTilesCorrect,
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 4
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            WaitFor.condition(() -> tc.page.exists(EPage.REPORT_ISSUE_DETAILS));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, tc.page.exists(EPage.REPORT_ISSUE_DETAILS),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 5
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.page.exists(EPage.REQUESTS_DASHBOARD));
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened", true, tc.page.exists(EPage.REQUESTS_DASHBOARD),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 6
            tc.tile.open(ETile.QUESTION_ABOUT_ORDER);
            WaitFor.condition(() -> tc.page.exists(EPage.REPORT_ISSUE_DETAILS));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, tc.page.exists(EPage.REPORT_ISSUE_DETAILS),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 7
            tc.tile.open(ETile.QUESTION_ABOUT_ACCOUNT);
            WaitFor.condition(() -> tc.page.exists(EPage.REPORT_ISSUE_DETAILS));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, tc.page.exists(EPage.REPORT_ISSUE_DETAILS),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 8
            tc.tile.open(ETile.REQUEST_ALLOCATION);
            WaitFor.condition(() -> tc.browser.isRedirectedToExternalTool());
            tc.addStepInfo("User is redirected to external Sales Efficiency tool page", true, tc.browser.isRedirectedToExternalTool(),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```
