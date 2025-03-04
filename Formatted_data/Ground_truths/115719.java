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

public class TC04_Dispatcher_Education_Role_Schedule_Task_Notification_Actions_Edit
{
    @ParameterizedTest
    @ArgumentsSource(EducationTestEnvironmentProvider.class)
    @EducationTestCaseId("115719")
    void TC_04(Map<String,String> data)
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115719", tc ->
        {
            final String notification = data.get("notifications");
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
            tc.addStepInfo("Filter,Sorted by : Call Id, Search Task, Split Task list domain, List of notifications" +
                            " option should be reflected",
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

            //Step 4
            tc.notification.performActions(EAction.EDIT);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL));
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator
                    .add(()->tc.modal.getTitle().equals("Task"),"Task Modal is not visible")
                    .add(()->tc.tab.isTabPresent(ETab.ASSIGNMENT),"'%s' tab is not displayed!".formatted(ETab.ASSIGNMENT))
                    .add(()->tc.tab.isTabPresent(ETab.GENERAL),"'%s' tab is not displayed!".formatted(ETab.GENERAL))
                    .add(()->tc.tab.isTabPresent(ETab.TIME),"'%s' tab is not displayed!".formatted(ETab.TIME))
                    .add(()->tc.tab.isTabPresent(ETab.HISTORY),"'%s' tab is not displayed!".formatted(ETab.HISTORY));
            tc.addStepInfo("Task pop up window should open with below tabs\n" +
                            "Assignment, General , Time, History",
                    "ok", tc.stepEvaluator.eval() ,new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.tab.select(ETab.ASSIGNMENT);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Assignment Start:","Duration:","Assignment Finish:","Assigned Resource:")),
                            "Some labels that should be present under assignment tab are not present");
            tc.addStepInfo("Below details should be reflected with Ok, Apply, Cancel buttons Status ,Reschedule Call? ,Assigment Start ,Assigment Finish,Duration , Assigned Resource" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.tab.select(ETab.GENERAL);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Notification:","Notification Task:", "Sales Order:",
                            "DISP Task Short Text:","Delivery Type:","Primary Focus:")),
                            "Some labels that should be present under general tab are not present");
            tc.addStepInfo("General notification details are reflected and CSE skills can be added" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.tab.select(ETab.LOCATION);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Customer:","Contact Name:",
                                    "Contact Phone:","Contact E-Mail:","Street:","City:", "State:","Postcode:")),
                            "Some labels that should be present under location tab are not present");
            tc.addStepInfo("Location details should be reflected with customer name, Contact and Email" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());


            //STEP 8
            tc.tab.select(ETab.TIME);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("DISP Task Creation Date:", "Planned Start:",
                                    "Planned Finish:")),
                            "Some labels that should be present under time tab are not present");
            tc.addStepInfo("DISP Task Creation Date, Planned Start, Planned Finish options should be reflected" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 9
            tc.tab.select(ETab.HISTORY);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.table.getAllColumnNames(ETable.HISTORY).containsAll(List.of("Edit Date","Edited By",
                            "Triggered Action","Status", "Rule Violating","In Jeopardy")),
                            "Columns present under history tab are not present")
                    .add(()->tc.table.getRowsCount(ETable.HISTORY) > 0 ,"No Data found in History table");
            tc.addStepInfo("Notification history details should be reflected" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


