package CAD_TMF_Zone_Operation_Lead_Role;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC36_Zone_Operation_Lead_Role_Views_Engineer_ManageFilters_Rename
{
    @Test
    void TC_36()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104916", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.ZONE_OPERATION_USER);
            tc.addStepInfo("Home screen should be reflected",
                    true,
                    tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.addStepInfo("Below options should be reflected on the left side of the screen Search Field Home "
                            + "Schedule Calendar Management Views Analytics",
                    true,
                    tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")),
                    new ComparerOptions().takeScreenShotPlatform());

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
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("Engineer".toLowerCase()),
                            "Engineer Page is not visible")
                    .add(()-> tc.edit.isDisplayed(EEdit.SEARCH), "Task Search bar not displayed")
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE_FILTER), "'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.SORTING), "'%s' button not found!".formatted(EButton.SORTING))
                    .add(()-> tc.button.isDisplayed(EButton.EXPORT), "'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.button.isDisplayed(EButton.NEW), "'%s' button not found!".formatted(EButton.NEW))
                    .add(()-> tc.table.getRowsCount(ETable.CAD_TABLE)>0, "No datas are found in the table");
            tc.addStepInfo("Engineer screen should be reflected with Search Bar, Manage Filter, Sorting, " +
                            "Refresh, New, Export buttons and list of engineers",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.button.click(EButton.MANAGE_FILTER);
            WaitFor.condition(()-> tc.modal.exists(EModal.MODAL));
            tc.addStepInfo("Filters Management - Resource screen should be displayed",
                    true,tc.modal.getTitle().equals("Filters Management - Resource"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.filter.removeAllFilter();
            tc.button.click(EButton.ADD);
            WaitFor.condition(()->tc.button.isDisplayed(EButton.ADVANCED_FILTERS));
            tc.addStepInfo("Resource filter 01 * - Conditions folder should be created",
                    true,tc.filter.getLastFilterName().equals("Resource filter 01"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.button.click(EButton.RENAME);
            tc.edit.sendKeys(EEdit.FILTER,"Resource FAT");
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("Text Fields is generated like \"Write down the expression to evaluate the condition:\"",
                    true,tc.filter.getLastFilterName().equals("Resource FAT"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.combo.select(EComboBox.SELECT_PROPERTY_1,"Active");
            tc.button.click(EButton.SAVE);
            tc.button.click(EButton.APPLY);
            tc.addStepInfo("New name should be saved.",
                    true,!tc.modal.exists(EModal.MODAL),new ComparerOptions().takeScreenShotPlatform());

        });
    }
}

