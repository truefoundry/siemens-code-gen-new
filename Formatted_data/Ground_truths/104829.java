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

public class TC16_Dispatcher_Role_CalendarManagement_Resources_AnyResourcesItem
{
    @Test
    void TC_16()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_ROLE, "104829", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_ROLE_USER);
            tc.addStepInfo("Home screen should be reflected",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.addStepInfo("Below options should be reflected on the left side of the screen " +
                            "Search Field Home Schedule Calendar Management Views Analytics",
                    true,tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")),
                    new ComparerOptions().takeScreenShotPlatform());

           //STEP 3
            tc.sideBar.select(ESideBar.CALENDER_MANAGEMENT);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.addStepInfo("Calendar and Resources section are reflected",
                    true,tc.tab.isTabPresent(ETab.CALENDARS)&&tc.tab.isTabPresent(ETab.RESOURCE),
                    new ComparerOptions().takeScreenShotPlatform());


            //STEP 4
           tc.tab.select(ETab.RESOURCE);
           tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
           tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.edit.isDisplayed(EEdit.SEARCH),"'%s' search bar is not displayed!".formatted(EEdit.SEARCH))
                    .add(()->tc.button.isDisplayed(EButton.MANAGE_FILTER),"'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()->tc.button.isDisplayed(EButton.SORTING),"'%s' button not found!".formatted(EButton.SORTING))
                    .add(()->tc.button.isDisplayed(EButton.REFRESH),"'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()->tc.button.isDisplayed(EButton.NEW),"'%s' button not found!".formatted(EButton.NEW))
                    .add(()->tc.button.isDisplayed(EButton.EXPORT),"'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()->tc.table.getRowsCount(ETable.CAD_TABLE) > 0 ,"Table is Empty");
            tc.addStepInfo("Search Bar, Manage Filter, Sorting, Refresh, New, Export button should be reflected" +
                            " with list of resources calendar schedule",
                    "ok",tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

           //STEP 5
            tc.table.itemClick(ETable.CAD_TABLE,EColumn.NAME,"#1");
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.addStepInfo("Resource schedule screen should be reflected",
                    true,tc.navigation.getAllBreadCrumbs().get(0).equals("Resources")
                            && tc.browser.getCurrentURL().toLowerCase().contains("calendarForm".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


