package CAD_TMF_Dispatcher_Role;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC31_Dispatcher_Role_Views_Engineer
{
    @Test
    void TC_31()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_ROLE, "104844", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_ROLE_USER);
            tc.addStepInfo("CAD application launched and logged in successfully",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.addStepInfo("Below options should be reflected on the left side of the screen Search Field Home " + "Schedule Calendar Management Views Analytics",
                    true,tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.sideBar.select(ESideBar.VIEWS);
            WaitFor.condition(()->tc.sideBar.getElements().contains("Task"));
            tc.addStepInfo("Below sub options should be reflected\n" +
                            "Capacity Limitations, Capacity Used Daily, Education Waitlist, Resource Territory, Task , Engineer",
                    true,tc.sideBar.getElements().containsAll(List.of("Capacity Limitations", "Capacity Used Daily",
                            "Education Waitlist","Resource Territory","Task","Engineer")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.sideBar.select(ESideBar.ENGINEER);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("Engineer".toLowerCase()), "Engineer Page is not visible")
                    .add(()-> tc.edit.isDisplayed(EEdit.SEARCH), "Task Search bar not displayed")
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE_FILTER), "'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.SORTING), "'%s' button not found!".formatted(EButton.SORTING))
                    .add(()-> tc.button.isDisplayed(EButton.EXPORT), "'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.button.isDisplayed(EButton.NEW), "'%s' button not found!".formatted(EButton.NEW))
                    .add(()-> tc.table.getRowsCount(ETable.CAD_TABLE)>0, "No Datas are found in the table ");
            tc.addStepInfo("Task screen should be reflected with Search Bar, Manage Filter, Sorting, Refresh,New,Export buttons and list of notification",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


