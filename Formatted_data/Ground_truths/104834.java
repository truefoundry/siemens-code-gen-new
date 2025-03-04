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

public class TC21_Dispatcher_Role_Views_EducationWaitlist_New
{
    @Test
    void TC_21()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_ROLE, "104834", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_ROLE_USER);
            tc.addStepInfo("Home screen should be reflected",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.addStepInfo("Below options should be reflected on the left side of the screen " +
                            "Search Field Home Schedule Calendar Management Views Analytics",
                    true,tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.sideBar.select(ESideBar.VIEWS);
            WaitFor.condition(()->tc.sideBar.getElements().contains("Task"));
            tc.addStepInfo("Below sub options should be reflected\n" +
                            "Capacity Limitations, Capacity Used Daily, Education Waitlist, Resource Territory, Task , Engineer",
                    true,tc.sideBar.getElements().containsAll(List.of("Capacity Limitations", "Capacity Used Daily",
                            "Education Waitlist","Resource Territory","Task","Engineer")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.sideBar.select(ESideBar.EDUCATION_WAITLIST);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("EducationWaitlist".toLowerCase()), "Education Waitlist Page is not visible")
                    .add(()-> tc.edit.isDisplayed(EEdit.SEARCH), "Task Search bar not displayed")
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE_FILTER), "'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.SORTING), "'%s' button not found!".formatted(EButton.SORTING))
                    .add(()-> tc.button.isDisplayed(EButton.DELETE), "'%s' button not found!".formatted(EButton.DELETE))
                    .add(()-> tc.button.isDisplayed(EButton.NEW), "'%s' button not found!".formatted(EButton.NEW))
                    .add(()-> tc.button.isDisplayed(EButton.EXPORT), "'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.table.getRowsCount(ETable.CAD_TABLE) > 0, "List of educationa waitlist in the table is empty");
            tc.addStepInfo("Education Waitlist screen should be reflected with Search Bar, Manage Filter, " +
                            "Sorting, Refresh,New,Export buttons and list of education waitlist",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.button.click(EButton.NEW);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.browser.getCurrentURL().toLowerCase().contains("EducationWaitlist".toLowerCase()),"Page not found")
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button not found!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button not found!".formatted(EButton.CANCEL))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button not found!".formatted(EButton.APPLY));
            tc.addStepInfo("Education Waitlist screen should be reflected with OK, Apply, Cancel button",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


