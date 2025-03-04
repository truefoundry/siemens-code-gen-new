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

public class TC35_Zone_Operation_Lead_Role_Views_Engineer_Sorting
{
    @Test
    void TC_35()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104915", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.ZONE_OPERATION_USER);
            tc.addStepInfo("CAD application launched and logged in successfully",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.addStepInfo("Below options should be reflected on the left side of the screen Search Field Home " + "Schedule Calendar Management Views Analytics",
                    true,tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.sideBar.select(ESideBar.VIEWS);
            WaitFor.condition(()->tc.sideBar.getElements().contains("Engineer"));
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
                    .add(()-> tc.table.exists(ETable.byIndex(0)), "No Datas are found in the table ");
            tc.addStepInfo("Engineer screen should be reflected with Search Bar, Manage Filter, Sorting, Refresh,New,Export buttons and list of notification",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.button.click(EButton.SORTING);
            tc.addStepInfo("Engineer Sorting screen should be displayed",
                    true,tc.modal.exists(EModal.MODAL) && tc.modal.getTitle().equals("Engineer Sorting"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            WaitFor.condition(()-> tc.modal.exists(EModal.MODAL));
            tc.list.dragAndDrop(EList.SELECT_COLUMNS,EList.SORT_BY,List.of("Name","District","ID"));
            tc.addStepInfo("Dragged select columns should be dropped in the sort by table",
                    true,tc.list.getListItems(EList.SORT_BY).containsAll(List.of("Name","District","ID")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.button.click(EButton.SORT);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.stepEvaluator
                    .add(()->tc.table.isColumnSorted(ETable.CAD_TABLE,EColumn.NAME),"Column '%s' is not sorted".formatted(EColumn.NAME.getValue()))
                    .add(()->tc.table.isColumnSorted(ETable.CAD_TABLE,EColumn.DISTRICT),"Column '%s' is not sorted".formatted(EColumn.DISTRICT.getValue()))
                    .add(()->tc.table.isColumnSorted(ETable.CAD_TABLE,EColumn.ID),"Column '%s' is not sorted".formatted(EColumn.ID.getValue()));
            tc.addStepInfo("Columns should be sorted","ok",tc.stepEvaluator.eval());

            //POST STEPS
            tc.button.click(EButton.SORTING);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL));
            tc.list.dragAndDrop(EList.SORT_BY,EList.SELECT_COLUMNS,List.of("District","ID"));
            tc.button.click(EButton.SORT);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
        });
    }

}


