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

public class TC05_Admin_Menu_User_Management
{
    @Test
    void Admin_Menu_User_Management()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.ADMIN, "842", tc ->
        {
            //Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            WaitFor.condition(() -> tc.button.exists(EButton.ADMIN));
            tc.button.click(EButton.ADMIN);
            WaitFor.condition(() -> tc.tile.exists(ETile.USER_MANAGEMENT));
            List<String> actualTiles = tc.tile.getAllTilesName();
            tc.addStepInfo("Page with 3 options for admins is displayed", 3, actualTiles.size(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            List<String> expectedTiles = Arrays.asList("User Management", "Escalation Criteria" ,"Admin Ruleset");
            tc.addStepInfo("""
                    Page contains tiles:             
                    - User Management
                    - Escalation Criteria
                    - Admin Ruleset""", true, actualTiles.equals(expectedTiles), new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.tile.open(ETile.USER_MANAGEMENT);
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.byIndex(0)).isEmpty());
            boolean isUserManagementOpened = !tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.byIndex(0)).isEmpty();
            tc.addStepInfo("User management page is displayed with list of all users", true, isUserManagementOpened
            , new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            String gid = "Z004XU7E";
            tc.edit.sendKeys(EEdit.SEARCH, gid);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.byIndex(0)).contains(gid));
            boolean isUserDisplayed = tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.byIndex(0)).contains(gid);
            tc.addStepInfo("Only users matching search conditions are displayed", true, isUserDisplayed
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.button.click(EButton.EDIT);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("""
                    Popup with user details, assigned roles and teams is displayed              
                    For reference check attached screenshot""", true, tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.button.click(EButton.UPDATE_USER);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.byIndex(0)).contains(gid));
            boolean isUserDisplayedAfterUpdate = tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.byIndex(0)).contains(gid);
            tc.addStepInfo("User management page is dipslayed users matching search conditions", true,
                    isUserDisplayedAfterUpdate, new ComparerOptions().takeScreenShotPlatform());
        });
    }

}


