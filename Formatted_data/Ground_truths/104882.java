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

public class TC02_Zone_Operation_Lead_Role_Schedule_Task_Notification_Actions_Edit
{
    @ParameterizedTest
    @ArgumentsSource(ZoneOperationTestEnvironmentProvider.class)
    @ZoneOperationTestCaseId("104882")
    void TC_02(Map<String,String> data)
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104882", tc ->
        {
            final String notification = data.get("notification");
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            tc.browser.login(ETestData.ZONE_OPERATION_USER);

            // STEP 1
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.sideBar.select(ESideBar.SCHEDULE);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("schedule".toLowerCase()), "schedule Schedule Page is not visible")
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
            tc.addStepInfo("Filter, Sorted by : Call Id, Search Task, Split Task list domain, List of notifications option should be reflected",
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
            tc.addStepInfo("Below options should be reflected for each",
                    true,
                    tc.notification.getAllActions().containsAll(List.of("Edit", "Site Assigned Engineers",
                            "Show on Gantt", "Show on Map", "Remove from Clipboard", "Show Related Tasks", "Pin Task" ,
                            "Comment", "Check Rules", "Get Candidates", "Schedule", "Unschedule")),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.notification.performActions(EAction.EDIT);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL));
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.addStepInfo("Task pop up window should open with below tabs\n" +
                            "Assignment, General , Bundling, Comments, Time, Customer , Location , Resource, Dependencies, Notes , Functional Location , History",
                    true,tc.modal.getTitle().equals("Task"),new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.tab.select(ETab.ASSIGNMENT);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Assignment Start:","Duration:","Assignment Finish:","Assigned Resource:")),
                            "Some labels that should be present under assignment tab are not present");
            tc.addStepInfo("Below details should be reflected with Ok, Apply, Cancel buttons : Assigment Start ,Duration ,Assigment Finish ,Assigned Resource" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.tab.select(ETab.GENERAL);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Notification:","Notification Task:",
                            "DISP Task Short Text:","TRP:","Task Type:","SAP Priority:","Priority:","Calendar:","Status:",
                            "Duration:","Short Text:","In Jeopardy:","Effect Code:")),
                            "Some labels that should be present under general tab are not present");
            tc.addStepInfo("General notification details are reflected and CSE skills can be added" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.tab.select(ETab.COMMENTS);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("DISP Task Short Text:","Next Action Date:",
                            "Task Creation:","Additional Task Reason:","ETA Canceled By:")),
                            "Some labels that should be present under comments tab are not present");
            tc.addStepInfo("Any comments added present in SAP is reflected here with ETA cancellation " +
                            "details and Site Assigned Engineers details" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 9
            tc.tab.select(ETab.TIME);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Early Start:","Due Date:",
                            "DISP Task Creation Date:","Appointment Start:","Appointment Finish:","Dispatch Date:",
                                    "Notification Creation Date:","Additional Travel:","Schedule Date:","Time Zone:")),
                            "Some labels that should be present under time tab are not present");
            tc.addStepInfo("Early start and due date , Appointment date and time All date and time, " +
                            "Schedule details should be reflected" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 10
            tc.tab.select(ETab.CUSTOMER);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Customer:", "Contact Name:", "Contact Phone:", "Contact Phone Extension:",
                "Contact E-Mail:", "Contract ID:", "Contract Line:", "Contract Description:", "Customer Addresses:")),
                            "Some labels that should be present under customer tab are not present");
            tc.addStepInfo("Customer Details and Contract details should be reflected" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 11
            tc.tab.select(ETab.LOCATION);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Latitude:","Longitude:",
                                    "Region:","District:","Street:","City:", "State:","Postal code:")),
                            "Some labels that should be present under location tab are not present");
            tc.addStepInfo("Location latitude and longitude details should be reflected" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 12
            tc.tab.select(ETab.RESOURCES);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Additional Resources:","Secondary Resources:",
                                    "Required Resources:","Preferred Resources:")),
                            "Some labels that should be present under resources tab are not present");
            tc.addStepInfo("Primary, Secondary,Required , Additional resources details should be reflected" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 13
            tc.tab.select(ETab.DEPENDENCIES);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().contains("Critical:"),
                            "Some labels that should be present under dependencies tab are not present");
            tc.addStepInfo("Time dependencies and Resource dependencies option should be reflected and user should be able to add the same" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 14
            tc.tab.select(ETab.NOTES);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("Site Message(s):","Notification Long Text:", "DISP Task Long Text:")),
                            "Some labels that should be present under notes tab are not present");
            tc.addStepInfo("Any messages like Site messages, long text should be reflected" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 15
            tc.tab.select(ETab.FUNCTIONAL_LOCATION);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button is not displayed!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not displayed!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button is not displayed!".formatted(EButton.CANCEL))
                    .add(()->tc.edit.getAllLabelNames().containsAll(List.of("FL Description:","Functional Location:",
                                    "Equipment Description:","Equipment:","Sales Area:","Distribution Channel:", "Division:","Family Description:")),
                            "Some labels that should be present under functional location tab are not present");
            tc.addStepInfo("FL details should be reflected" ,
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 16
            tc.button.click(EButton.ARROW_RIGHT);
            WaitFor.specificTime(Duration.ofSeconds(3));
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


