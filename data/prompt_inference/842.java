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

public class TC05_Admin_User_Management_Functionality_Check
{
    @Test
    void Admin_User_Management_Functionality_Check()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "842", tc ->
        {
            // Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 2
            tc.button.click(EButton.ADMIN_ICON);
            WaitFor.condition(() -> tc.page.containsTiles(ETile.USER_MANAGEMENT, ETile.ESCALATION_CRITERIA, ETile.ADMIN_RULESET));
            tc.addStepInfo("Page with 3 options for admins is displayed", true, tc.page.containsTiles(ETile.USER_MANAGEMENT, ETile.ESCALATION_CRITERIA, ETile.ADMIN_RULESET),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 3
            boolean tilesExist = tc.page.containsTiles(ETile.USER_MANAGEMENT, ETile.ESCALATION_CRITERIA, ETile.ADMIN_RULESET);
            tc.addStepInfo("Page contains tiles: User Management, Escalation Criteria, Admin Ruleset", true, tilesExist,
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 4
            tc.tile.open(ETile.USER_MANAGEMENT);
            WaitFor.condition(() -> tc.page.getTitle().contains("User Management"));
            tc.addStepInfo("User management page is displayed with list of all users", true, tc.page.getTitle().contains("User Management"),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 5
            tc.edit.sendKeys(EEdit.SEARCH_USER, "GID or part of User name or e-mail");
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.USER_NAME).stream()
                    .anyMatch(name -> name.contains("GID or part of User name or e-mail")));
            tc.addStepInfo("Only users matching search conditions are displayed", true, true,
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 6
            tc.button.click(EButton.EDIT_USER);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with user details, assigned roles and teams is displayed", true, tc.modal.exists(),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 7
            tc.button.click(EButton.UPDATE_USER);
            WaitFor.condition(() -> tc.page.getTitle().contains("User Management"));
            tc.addStepInfo("User management page is displayed users matching search conditions", true, tc.page.getTitle().contains("User Management"),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```