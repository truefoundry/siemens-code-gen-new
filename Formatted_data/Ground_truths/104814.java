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

public class TC01_Dispatcher_Role_Schedule_Task_Notification_Actions
{
    @ParameterizedTest
    @ArgumentsSource(DispatcherTestEnvironmentProvider.class)
    @DispatcherTestCaseId("104814")
    void TC_01(Map<String,String> data)
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_ROLE, "104814", tc ->
        {
            final String notification = data.get("notification");
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_ROLE_USER);
            tc.addStepInfo("CAD application launched and logged in successfully",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.addStepInfo("Below options should be reflected on the left side of the screen " +
                            "Search Field Home Schedule Calendar Management Views Analytics",
                    true,
                    tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.sideBar.select(ESideBar.SCHEDULE);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.tab.select(ETab.TASKS);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("schedule".toLowerCase()), "Schedule Schedule Page is not visible")
                    .add(()-> (tc.tab.getAllTabs()).containsAll(List.of("Tasks","Clipboard","Gantt","Map")) && (tc.tab.getAllTabs()).stream().anyMatch(s -> s.startsWith("Working on")),
                            "Some tabs('Tasks','Clipboard','Gantt','Map','working on domains') present on the Schedule page are not available");
            tc.addStepInfo("Schedule screen should be reflected with Tasks, Clipboard, Gantt, Map, Working on Domain options",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.search.search(notification, true);
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.addStepInfo("Notification should be appear under the Clipboard tab and assign the notification to the CSE.",
                    true,
                    tc.notification.getAllNotifications().size() == 1 && tc.notification.getAllNotifications().get(0).startsWith(notification)
                    ,new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.notification.expandActions("#0");
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.addStepInfo("Below options should be reflected for each notification 'notification', 'Edit', " +
                            "'Site Assigned Engineers','Change Status', 'Show on Gantt', 'Show on Map', 'Remove from Clipboard', " +
                            "'Pin Task', 'Comment', 'Check Rules', 'Get Candidates', 'Schedule'," +
                            " 'Unschedule','Relaxed Get Candidates'",
                    true,
                    tc.notification.getAllActions().containsAll(List.of("Edit", "Site Assigned Engineers","Change Status",
                            "Show on Gantt", "Show on Map", "Remove from Clipboard", "Pin Task" , "Comment",
                            "Check Rules", "Get Candidates", "Schedule", "Unschedule","Relaxed Get Candidates")),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}

