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

public class TC19_Dispatcher_Education_Role_Views_Task_NotificationListItem
{
    @Test
    void TC_19()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115734", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_EDUCATION_ROLE_USER);
            tc.addStepInfo("Home screen should be reflected",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),new ComparerOptions().takeScreenShotPlatform());

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
            tc.sideBar.select(ESideBar.VIEWS);
            WaitFor.condition(()->tc.sideBar.getElements().contains("Task"));
            tc.addStepInfo("Below sub options should be reflected\n" +
                            "Education Waitlist, Task, Engineer, Education Skills, Assignment",
                    true,tc.sideBar.getElements().containsAll(List.of("Education Waitlist", "Task",
                            "Engineer","Education Skills","Assignment")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.sideBar.select(ESideBar.TASK);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            WaitFor.condition(()->tc.edit.isDisplayed(EEdit.SEARCH),Duration.ofSeconds(5));
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("Task".toLowerCase()), "Task Page is not visible")
                    .add(()-> tc.edit.isDisplayed(EEdit.SEARCH), "Task Search bar not displayed")
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE_FILTER), "'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.SORTING), "'%s' button not found!".formatted(EButton.SORTING))
                    .add(()-> tc.button.isDisplayed(EButton.EXPORT), "'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.button.isDisplayed(EButton.NEW), "'%s' button not found!".formatted(EButton.NEW))
                    .add(()-> tc.table.getRowsCount(ETable.CAD_TABLE)>0, "No Datas are found in the table ");
            tc.addStepInfo("Task screen should be reflected with Search Bar, Manage Filter, Sorting, Refresh,New,Export buttons and list of notification",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.table.itemClick(ETable.CAD_TABLE,EColumn.NOTIFICATION_NUMBER,"#1");
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.tab.isTabPresent(ETab.ASSIGNMENT),"'%s' tab not found!".formatted(ETab.ASSIGNMENT))
                    .add(()->tc.tab.isTabPresent(ETab.GENERAL),"'%s' tab not found!".formatted(ETab.GENERAL))
                    .add(()->tc.tab.isTabPresent(ETab.LOCATION),"'%s' tab not found!".formatted(ETab.LOCATION))
                    .add(()->tc.tab.isTabPresent(ETab.TIME),"'%s' tab not found!".formatted(ETab.TIME))
                    .add(()->tc.tab.isTabPresent(ETab.HISTORY),"'%s' tab not found!".formatted(ETab.HISTORY))
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button not found!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button not found!".formatted(EButton.CANCEL));
            tc.addStepInfo("Task screen should be reflected with below options with OK, Apply, Cancel buttons",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


