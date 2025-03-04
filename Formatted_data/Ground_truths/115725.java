package CAD_TMF_Dispatcher_Education_Role;

import CAD_TMF_Dispatcher_Education_Role.DataProvider.EducationTestCaseId;
import CAD_TMF_Dispatcher_Education_Role.DataProvider.EducationTestEnvironmentProvider;
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

public class TC10_Dispatcher_Education_Role_Schedule_Task_Notification_Actions_CheckRules
{
    @ParameterizedTest
    @ArgumentsSource(EducationTestEnvironmentProvider.class)
    @EducationTestCaseId("115725")
    void TC_10(Map<String,String> data)
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115725", tc ->
        {
            final String notification = data.get("notification");
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            tc.browser.login(ETestData.DISPATCHER_EDUCATION_ROLE_USER);

            // STEP 1
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.sideBar.select(ESideBar.SCHEDULE);
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.MAP));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("schedule".toLowerCase()), "Schedule Schedule Page is not visible")
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
                    .add(()->!tc.notification.getAllNotifications().isEmpty(),"Notifications present under task section is not available")
                    .add(()->tc.button.isDisplayed(EButton.FILTER_TASKS),"'%s' button not found!".formatted(EButton.FILTER_TASKS))
                    .add(()->tc.button.isDisplayed(EButton.SEARCH_TASKS),"'%s' button not found!".formatted(EButton.SEARCH_TASKS))
                    .add(()->tc.button.isDisplayed(EButton.SORTED_BY),"'%s' button not found!".formatted(EButton.SORTED_BY));
            tc.search.search(notification, true);
            tc.stepEvaluator
                    .add(()->tc.notification.getAllNotifications().size() == 1 && tc.notification.getAllNotifications().get(0).startsWith(notification),"The Searched notification is not present");
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.addStepInfo("Filter,Sorted by : Call Id, Search Task, Split Task list domain, List of notifications option should be reflected",
                    "ok",
                    tc.stepEvaluator.eval()
                    ,new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.notification.expandActions("#0");
            tc.addStepInfo("Below options should be reflected for each notification 'Edit', " +
                            "'Site Assigned Engineers', 'Show on Gantt', 'Show on Map', 'Add to Clipboard', " +
                            "'Show related tasks', 'Pin Task', 'Comment', 'Check Rules', 'Get Candidates', 'Schedule'," +
                            " 'Unschedule', 'Relaxed Get Candidates'",
                    true,
                    tc.notification.getAllActions().containsAll(List.of("Edit", "Site Assigned Engineers",
                            "Show on Gantt", "Show on Map", "Remove from Clipboard", "Show Related Tasks", "Pin Task" ,
                            "Comment", "Check Rules", "Get Candidates", "Schedule", "Unschedule", "Relaxed Get Candidates")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.notification.performActions(EAction.CHECK_RULES);
            WaitFor.condition(()-> tc.modal.exists(EModal.MODAL));
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("Rule Violations Check popup window should be displayed",
                    true,tc.modal.getTitle().equals("Rule Violations Check"),
                    new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


