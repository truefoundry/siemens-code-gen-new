package CAD_TMF_Zone_Operation_Lead_Role;

import CAD_TMF_Zone_Operation_Lead_Role.DataProvider.ZoneOperationTestCaseId;
import CAD_TMF_Zone_Operation_Lead_Role.DataProvider.ZoneOperationTestEnvironmentProvider;
import CompositionRoot.IocBuilder;
import ControlImplementation.ModalControl;
import Enums.*;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.DomUtils;
import fate.core.ControlImplementations.Generator;
import fate.core.ControlImplementations.WaitFor;
import fate.core.DTO.DateManipulator;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC12_Zone_Operation_Lead_Role_Schedule_ClipBoard_Notification_Actions_BookAppointment
{
    @ParameterizedTest
    @ArgumentsSource(ZoneOperationTestEnvironmentProvider.class)
    @ZoneOperationTestCaseId("104957")
    void TC12(Map<String,String> data)
    {
        IocBuilder.execute(Duration.ofMinutes(8), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104957", tc ->
        {
            final String notification = data.get("notification");
            tc.browser.start(WebDrv.CHROME, ETestData.URL, ETestData.ZONE_OPERATION_USER);

            // STEP 1
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.sideBar.select(ESideBar.SCHEDULE);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("schedule".toLowerCase()), "Schedule Page is not visible")
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
                    .add(()-> tc.button.isDisplayed(EButton.FILTER_TASKS), "Filter Task button not found!")
                    .add(()-> tc.button.isDisplayed(EButton.SORTED_BY), "Sorted By button not found!")
                    .add(()-> tc.button.isDisplayed(EButton.SEARCH_TASKS), "'%s' button not found!".formatted(EButton.SEARCH_TASKS))
                    .add(()-> tc.button.isDisplayed(EButton.SPLIT_TASK_LIST_TIME_DOMAIN), "'%s' button not found!".formatted(EButton.SPLIT_TASK_LIST_TIME_DOMAIN));
            tc.addStepInfo("Filter,Sorted by : Call Id, Search Task, Split Task list domain, List of notifications option should be reflected",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.search.search(notification, true);
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.notification.expandActions("#0");
            List<String> actions = tc.notification.getAllActions();
            List<String> expectedActions = List.of("Edit", "Site Assigned Engineers", "Change Status", "Show on Map", "Remove from Clipboard",
                    "Pin Task", "Comment", "Get Candidates", "Schedule", "Book Appointment");
            tc.addStepInfo("Below options should be reflected for each notification 'notification', 'Edit', " +
                            "'Site Assigned Engineers', 'Show on Gantt', 'Show on Map', 'Add to Clipboard', " +
                            "'Show related tasks', 'Pin Task', 'Comment', 'Check Rules', 'Get Candidates', 'Schedule'," +
                            " 'Unschedule' ", true, actions.equals(expectedActions),new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.notification.performActions(EAction.BOOK_APPOINTMENT);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.GET_APPOINTMENTS));
            tc.addStepInfo("Appointment Booking pop up should be reflected with Get Appointments / close buttons and Appointment " +
                    "Start date field", true, tc.button.isDisplayed(EButton.CLOSE) && tc.edit.isDisplayed(EEdit.DATE_TIME_PICKER),new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            final String t2_date = Generator.getSpecificDate(new DateManipulator().setDay(1), new SimpleDateFormat("dd/MM/yyyy hh:mm"));
            tc.edit.setValue(EEdit.byIndex(0), t2_date);
            tc.combo.select(EComboBox.OPEN_THE_TIME_VIEW, "11:00 AM");
            tc.button.click(EButton.GET_APPOINTMENTS);
            tc.spinner.waitForSpinner(ESpinner.FA_FA_SPINNER, Duration.ofSeconds(10), true);
            tc.spinner.waitForSpinnerToDisappear(ESpinner.FA_FA_SPINNER, Duration.ofSeconds(30), true);
            tc.addStepInfo("Appointments available for the week should be reflected.", true, this.isAppointmentVisible(),new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            this.selectAppointment();
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.modal.getTitle().contains("Book Slot"), "Modal not found!")
                    // TODO: 5/27/2024 can't verify `because byIndex not implemented 
//                    .add(()-> tc.button.isDisplayed(EButton.byIndex(1)), "Button 'CANCEL' not found!") 
                    .add(()-> tc.button.isDisplayed(EButton.BOOK_APPOINTMENT), "'Book Appointment' button not found!");
            tc.addStepInfo("CSE Slot should be selected and Book Slot window popup should be displayed with Book Appointment and " +
                    "Cancel button", "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.button.click(EButton.BOOK_APPOINTMENT);
            tc.spinner.waitForSpinner(ESpinner.FA_FA_SPINNER, Duration.ofSeconds(10), true);
            tc.spinner.waitForSpinnerToDisappear(ESpinner.FA_FA_SPINNER, Duration.ofSeconds(30), true);
            tc.addStepInfo("Appointment Details should be reflected.", true, this.getBookedDetails().contains("Appointment Details"),new ComparerOptions().takeScreenShotPlatform());
        });
    }

    private boolean isAppointmentVisible()
    {
        try
        {
            CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
            WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
            WebElement data = css.findControlWithRoot(By.cssSelector("form[name='ABSlotsSelect'] table div[class='ng-scope SHS_card']"), root);// TODO: 5/14/2024 need to add on table implementation
            if(data != null)
            {
                return data.isDisplayed();
            }
        }
        catch (Exception e)
        {
            TcLog.error("Failed to get Dates.");
        }
        return false;
    }

    private void selectAppointment()
    {
        try
        {
            CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
            WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
            WebElement data = css.findControlWithRoot(By.cssSelector("form[name='ABSlotsSelect'] table div[class='ng-scope SHS_card']"), root);// TODO: 5/27/2024 need to add on table implementation
            if(data != null)
            {
                data.click();
            }
        }
        catch (Exception e)
        {
            TcLog.error("Failed to get Dates.");
        }
    }

    private String getBookedDetails()
    {
        try
        {
            CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
            WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
            WebElement data = css.findControlWithRoot(By.cssSelector("div[name='ShowBookingDetails']"), root);// TODO: 5/27/2024 need to add on table implementation
            if(data != null)
            {
                return data.getText();
            }
        }
        catch (Exception e)
        {
            TcLog.error("Failed to get Dates.");
        }
        return "";
    }
}


