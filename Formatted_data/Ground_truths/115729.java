package CAD_TMF_Dispatcher_Education_Role;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC14_Dispatcher_Education_Role_Calendar_Management_Calendar_Manage_Filter_Add
{
    @Test
    void TC_14()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115729", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_EDUCATION_ROLE_USER);
            tc.addStepInfo("CAD application launched and logged in successfully",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.edit.isDisplayed(EEdit.SERVICE_EDGE_SEARCH), "'%s' Search field is not Displayed!".formatted(EEdit.SERVICE_EDGE_SEARCH))
                    .add(()-> tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics","Views")),"The Side bar does not contains certain elements");
            tc.addStepInfo("Below options should be reflected on the left side of the screen Search Field Home " + "Schedule Calendar Management Views Analytics",
                    "ok",tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());


            //STEP 3
            tc.sideBar.select(ESideBar.CALENDER_MANAGEMENT);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.tab.isTabPresent(ETab.CALENDARS),"Calendar tab is not present")
                    .add(()->tc.tab.isTabPresent(ETab.RESOURCE),"Resource tab is not present")
                    .add(()->tc.button.isDisplayed(EButton.SORTING),"'%s' button not found!".formatted(EButton.SORTING))
                    .add(()->tc.button.isDisplayed(EButton.REFRESH),"'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()->tc.button.isDisplayed(EButton.NEW),"'%s' button not found!".formatted(EButton.NEW))
                    .add(()->tc.button.isDisplayed(EButton.MANAGE_FILTER),"'%s' button not found!".formatted(EButton.MANAGE_FILTER));
            tc.addStepInfo("Calendars and Resources fields along with that Manage Filters, Sorting, Refresh and New options should be display",
                    "ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.tab.select(ETab.CALENDARS);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.addStepInfo("Name and Base fields should be displayed",
                    true,tc.table.getAllColumnNames(ETable.CAD_TABLE).containsAll(List.of("Name","Base")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.sideBar.select(ESideBar.CALENDER_MANAGEMENT);
            tc.spinner.waitForSpinner(ESpinner.ANIMATION_LOADER,Duration.ofSeconds(15));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.ANIMATION_LOADER,Duration.ofSeconds(15));
            tc.button.click(EButton.MANAGE_FILTER);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL));
            tc.stepEvaluator.reset();
            tc.addStepInfo("Filters Management - Calendar screen page should be displayed",
                    true,tc.modal.getTitle().equals("Filters Management - Calendar"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.button.click(EButton.ADD);
            WaitFor.specificTime(Duration.ofSeconds(5));
            WaitFor.condition(()->tc.button.isDisplayed(EButton.ADVANCED_FILTERS));
            tc.addStepInfo("Calendar filter 01 * - Condition should be displayed",
                    true,tc.filter.getLastFilterName().contains("Calendar filter"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.combo.select(EComboBox.SELECT_PROPERTY_1,"Name");
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.combo.select(EComboBox.SELECT_PROPERTY_2,"Contains");
            tc.edit.sendKeys(EEdit.ALL_VALUES,"400");
            tc.addStepInfo("Condition should be entered",true,tc.edit.isDisplayed(EEdit.ALL_VALUES),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.button.click(EButton.APPLY);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            List<String> getAllNames = tc.table.getItemsFromColumn(ETable.CAD_TABLE,EColumn.NAME);
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> !tc.modal.exists(EModal.MODAL),"Modal is present")
                    .add(()->tc.button.isDisplayed(EButton.MANAGE_FILTER),"'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()->tc.button.isDisplayed(EButton.SORTING),"'%s' button not found!".formatted(EButton.SORTING))
                    .add(()->tc.button.isDisplayed(EButton.REFRESH),"'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()->tc.button.isDisplayed(EButton.NEW),"'%s' button not found!".formatted(EButton.NEW))
                    .add(()->tc.button.isDisplayed(EButton.EXPORT),"'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()->getAllNames.stream().allMatch(s -> s.contains("400")) ,"Filter is not evaluated");
            tc.addStepInfo("Condition should be applied and saved , Based on Condition the Name and Base list should be displayed",
                    "ok",tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


