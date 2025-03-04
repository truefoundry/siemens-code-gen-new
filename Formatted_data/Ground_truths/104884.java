package CAD_TMF_Zone_Operation_Lead_Role;

import CAD_TMF_Zone_Operation_Lead_Role.DataProvider.ZoneOperationTestCaseId;
import CAD_TMF_Zone_Operation_Lead_Role.DataProvider.ZoneOperationTestEnvironmentProvider;
import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC04_Zone_Operation_Lead_Role_Schedule_Task_Notification_Actions_Show_on_Gantt
{
    @ParameterizedTest
    @ArgumentsSource(ZoneOperationTestEnvironmentProvider.class)
    @ZoneOperationTestCaseId("104884")
    void TC_04(Map<String,String> data)
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104884", tc ->
        {
            final String notification = data.get("notification");
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            tc.browser.login(ETestData.ZONE_OPERATION_USER);

            // STEP 1
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.sideBar.select(ESideBar.SCHEDULE);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("schedule".toLowerCase()), 
                            "Schedule Page is not visible")
                    .add(()-> (tc.tab.getAllTabs()).containsAll(List.of("Tasks","Clipboard","Gantt","Map")), "Some tabs('Tasks','Clipboard','Gantt','Map') present on the Schedule page are not available")
                    .add(()-> tc.tab.isTabPresent(ETab.TERRITORY_PLANNING),"The tab terrirtory planning is not present");
             tc.addStepInfo("Schedule screen should be reflected with Tasks, Clipboard, Gantt, Map, Territory Planning,Working on Domain options",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());


            //STEP 2
            tc.tab.select(ETab.TASKS);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->!tc.notification.getAllNotifications().isEmpty(),"Notifications present under task section is not available")
                    .add(()->tc.button.isDisplayed(EButton.FILTER_TASKS),"'%s' button not found!".formatted(EButton.FILTER_TASKS))
                    .add(()->tc.button.isDisplayed(EButton.SEARCH_TASKS),"'%s' button not found!".formatted(EButton.SEARCH_TASKS))
                    .add(()->tc.button.isDisplayed(EButton.SORTED_BY),"'%s' button not found!".formatted(EButton.SORTED_BY));
            tc.addStepInfo("Filter, Sorted by : Call Id, Search Task, Split Task list domain," +
                            " List of notifications option should be reflected",
                    "ok",tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.search.search(notification, true);
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.addStepInfo("Notification should be appear under the Clipboard tab and assign the notification to the CSE.",
                    true,
                    tc.notification.getAllNotifications().size() == 1 && tc.notification.getAllNotifications().get(0).startsWith(notification)
                    ,new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.notification.expandActions("#0");
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.addStepInfo("Below options should be reflected for each notification 'notification', 'Edit', " +
                            "'Site Assigned Engineers', 'Show on Gantt', 'Show on Map', 'Remove from Clipboard', " +
                            "'Show related tasks', 'Pin Task', 'Comment', 'Check Rules', 'Get Candidates', 'Schedule'," +
                            " 'Unschedule'",
                    true,
                    tc.notification.getAllActions().containsAll(List.of("Edit", "Site Assigned Engineers",
                            "Show on Gantt", "Show on Map", "Remove from Clipboard", "Show Related Tasks", "Pin Task" , "Comment",
                            "Check Rules", "Get Candidates", "Schedule", "Unschedule")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.notification.performActions(EAction.SHOW_ON_GANTT);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.GANTT_FILTER), "'%s' button is not displayed!".formatted(EButton.GANTT_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.GANTT_RESOLUTION), "'%s' button is not displayed!".formatted(EButton.GANTT_RESOLUTION))
                    .add(()-> tc.button.isDisplayed(EButton.GANTT_SORT), "'%s' button is not displayed!".formatted(EButton.GANTT_SORT))
                    .add(()-> tc.button.isDisplayed(EButton.GANTT_CALENDAR), "'%s' button is not displayed!".formatted(EButton.GANTT_CALENDAR))
                    .add(()-> tc.edit.isDisplayed(EEdit.RESOURCE_SEARCH), "'%s' is not displayed!".formatted(EEdit.RESOURCE_SEARCH));
            tc.addStepInfo("List of all CSE's is reflected with their schedule for the selected day and" +
                            " user can click and drap the notifications and assign them based on their availability",
                    "ok",tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


