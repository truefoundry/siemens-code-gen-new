package CAD_TMF_Dispatcher_Role;

import CAD_TMF_Dispatcher_Role.DataProvider.DispatcherTestCaseId;
import CAD_TMF_Dispatcher_Role.DataProvider.DispatcherTestEnvironmentProvider;
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

public class TC05_Dispatcher_Role_Schedule_Task_Notification_Actions_Show_on_Map
{
    @ParameterizedTest
    @ArgumentsSource(DispatcherTestEnvironmentProvider.class)
    @DispatcherTestCaseId("104814")
    void TC_05(Map<String,String> data)
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_ROLE, "104818", tc ->
        {
            final String notification = data.get("notification");
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            tc.browser.login(ETestData.DISPATCHER_ROLE_USER);

            // STEP 1
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.sideBar.select(ESideBar.SCHEDULE);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("schedule".toLowerCase()), 
                            "Schedule Page is not visible")
                    .add(()-> (tc.tab.getAllTabs()).containsAll(List.of("Tasks","Clipboard","Gantt","Map")) && (tc.tab.getAllTabs()).stream().anyMatch(s -> s.startsWith("Working on")),
                            "Some tabs('Tasks','Clipboard','Gantt','Map','working on domains') present on the Schedule page are not available");
            tc.addStepInfo("Schedule screen should be reflected with Tasks, Clipboard, Gantt, Map, Working on Domain options",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.tab.select(ETab.TASKS);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.button.isDisplayed(EButton.FILTER_TASKS),"'%s' button not found!".formatted(EButton.FILTER_TASKS))
                    .add(()->tc.button.isDisplayed(EButton.SEARCH_TASKS),"'%s' button not found!".formatted(EButton.SEARCH_TASKS))
                    .add(()->tc.button.isDisplayed(EButton.SORTED_BY),"'%s' button not found!".formatted(EButton.SORTED_BY));
            tc.addStepInfo("Filter, Sorted by : Call Id, Search Task, Split Task list domain, " +
                            "List of notifications option should be reflected",
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
                            "'Site Assigned Engineers','Change Status', 'Show on Gantt', 'Show on Map', 'Remove from Clipboard', " +
                            "'Pin Task', 'Comment', 'Check Rules', 'Get Candidates', 'Schedule'," +
                            " 'Unschedule','Relaxed Get Candidates'",
                    true,
                    tc.notification.getAllActions().containsAll(List.of("Edit", "Site Assigned Engineers","Change Status",
                            "Show on Gantt", "Show on Map", "Remove from Clipboard","Pin Task" , "Comment",
                            "Check Rules", "Get Candidates", "Schedule", "Unschedule","Relaxed Get Candidates")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.notification.performActions(EAction.SHOW_ON_MAP);
            tc.addStepInfo("Customer location details should be reflected on map",
                    true,tc.tab.isTabActive(ETab.MAP),new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


