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

public class TC17_Zone_Operation_Lead_Role_View_Capacity_Limitations_Sorting
{
    @Test
    void TC_17()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104897", tc ->
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
            WaitFor.condition(()->tc.sideBar.getElements().contains("Task"));
            tc.addStepInfo("Below sub options should be reflected\n" +
                            "Capacity Limitations, Capacity Used Daily, Education Waitlist, Resource Territory, Task , Engineer",
                    true,tc.sideBar.getElements().containsAll(List.of("Capacity Limitations", "Capacity Used Daily",
                            "Education Waitlist","Resource Territory","Task","Engineer")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.sideBar.select(ESideBar.CAPACITY_LIMITATION);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("CapacityLimits".toLowerCase()), "Capacity Limits Page is not visible");
            tc.addStepInfo("Generic Capacity page should be displayed",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE_FILTER), "'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.SORTING), "'%s' button not found!".formatted(EButton.SORTING))
                    .add(()-> tc.button.isDisplayed(EButton.EXPORT), "'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.button.isDisplayed(EButton.NEW), "'%s' button not found!".formatted(EButton.NEW))
                    .add(()-> tc.table.getRowsCount(ETable.CAD_TABLE)>0, "No Datas are found in the table ")
                    .add(()-> tc.table.getAllColumnNames(ETable.CAD_TABLE).containsAll(List.of("Name","District","Task Type Category","Capacity Threshold")), "Column not found");
            tc.addStepInfo("In Generic Capacity , fields like Name, District, Task Type Category, Capacity Threshold " +
                            "should be displayed with the list and along with the options like manage filters, sorting, refresh, " +
                            "New, Export should be displayed",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.button.click(EButton.SORTING);
            tc.addStepInfo("Generic Capacity Sorting page should be displayed",
                    true,tc.modal.getTitle().equals("Generic Capacity Sorting"),new ComparerOptions().takeScreenShotPlatform());


            //STEP 7
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.addStepInfo("Select Columns list are like Name, District, Task Type Category, " +
                            "Capacity Threshold should be displayed under the select columns list",
                    true,tc.list.getListItems(EList.SELECT_COLUMNS).containsAll(List.of("District",
                            "Task Type Category","Capacity Threshold")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.list.dragAndDrop(EList.SELECT_COLUMNS,EList.SORT_BY,List.of("District",
                    "Task Type Category","Capacity Threshold"));
            tc.addStepInfo("Dragged select columns should be dropped in the sort by table",
                    true,tc.list.getListItems(EList.SORT_BY).containsAll(List.of("Name","District","Task Type Category","Capacity Threshold")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 9
            List<String> sortBy = tc.list.getListItems(EList.SORT_BY);
            tc.addStepInfo("All fields in the sort by should be validate",
                    true,tc.list.getListItems(EList.SORT_BY).size()==4,
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 10
            tc.button.click(EButton.SORT);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.table.isColumnSorted(ETable.CAD_TABLE,EColumn.NAME),"Column '%s' is not sorted".formatted(EColumn.NAME.getValue()))
                    .add(()->tc.table.isColumnSorted(ETable.CAD_TABLE,EColumn.DISTRICT),"Column '%s' is not sorted".formatted(EColumn.DISTRICT.getValue()))
                    .add(()->tc.table.isColumnSorted(ETable.CAD_TABLE,EColumn.CAPACITY_THRESHOLD),"Column '%s' is not sorted".formatted(EColumn.CAPACITY_THRESHOLD.getValue()))
                    .add(()->tc.table.isColumnSorted(ETable.CAD_TABLE,EColumn.TASK_TYPE_CATEGORY),"Column '%s' is not sorted".formatted(EColumn.TASK_TYPE_CATEGORY.getValue()));
            tc.addStepInfo("All the fields should be sorted and displayed.",
                    "ok",tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //POST STEPS
            tc.button.click(EButton.SORTING);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL));
            tc.list.dragAndDrop(EList.SORT_BY,EList.SELECT_COLUMNS,List.of("District","Task Type Category","Capacity Threshold"));
            tc.button.click(EButton.SORT);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
        });
    }
}


