package CAD_TMF_Dispatcher_Education_Role;

import CompositionRoot.IocBuilder;
import ControlImplementation.EditControl;
import ControlImplementation.ModalControl;
import Enums.*;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.*;
import fate.core.DTO.DateManipulator;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;

public class TC13_Dispatcher_Role_Schedule_Gantt_Resources_Option
{
    @Test
    void TC_13()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115728", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            tc.browser.login(ETestData.DISPATCHER_EDUCATION_ROLE_USER);

            // STEP 1
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.sideBar.select(ESideBar.SCHEDULE);
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.MAP));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("schedule".toLowerCase()), "Schedule Page is not visible")
                    .add(()-> (tc.tab.getAllTabs()).containsAll(List.of("Tasks","Clipboard","Gantt","Map")) && (tc.tab.getAllTabs()).stream().anyMatch(s -> s.startsWith("Working on")),
                            "Some tabs('Tasks','Clipboard','Gantt','Map','working on domains') present on the Schedule page are not available");
            tc.addStepInfo("Schedule screen should be reflected with Tasks, Clipboard, Gantt, Map, Working on Domain options",
                    "ok", tc.stepEvaluator.eval());

            //STEP 2
            tc.tab.select(ETab.GANTT);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.GANTT_FILTER), "'%s' button is not clickable!".formatted(EButton.GANTT_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.GANTT_RESOLUTION), "'%s' button is not clickable!".formatted(EButton.GANTT_RESOLUTION))
                    .add(()-> tc.button.isDisplayed(EButton.GANTT_SORT), "'%s' button is not clickable!".formatted(EButton.GANTT_SORT))
                    .add(()-> tc.button.isDisplayed(EButton.GANTT_CALENDAR), "'%s' button is not clickable!".formatted(EButton.GANTT_CALENDAR))
                    .add(()-> tc.edit.isDisplayed(EEdit.RESOURCE_SEARCH), "'%s' is not displayed!".formatted(EEdit.RESOURCE_SEARCH));
            tc.addStepInfo("Gantt Filters, Sort, Change Gantt resolution, Change the start date of the Gantt Settings, " +
                            "Search Resources, List of Resources should be reflected",
                    "ok", tc.stepEvaluator.eval());

            //STEP 3
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.gantt.expandGanttActions("#3");
            tc.addStepInfo("Below option should be reflected",true,
                    tc.gantt.getAllGanttUser().containsAll(List.of("Edit","Show Home Base","Show Daily Route",
                            "Add Tasks To Clipboard","Relocate Resource","Cancel All Relocations","Create NA")));

            //STEP 4
            tc.gantt.performGanttUserActions(EAction.CREATE_NA);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL),Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.modal.exists(EModal.MODAL),"Modal does not exist")
                    .add(()->tc.modal.getTitle().equals("Non Availability"),"Modal title should be Non Availability but not found")
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button not found in the modal!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button not found in the modal!".formatted(EButton.CANCEL))
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button not found in the modal!".formatted(EButton.OK))
                    .add(()->tc.tab.isTabPresent(ETab.GENERAL),"'%s' tab not found in the modal!".formatted(ETab.GENERAL))
                    .add(()->tc.tab.isTabPresent(ETab.LOCATION),"'%s' tab not found in the modal!".formatted(ETab.LOCATION))
                    .add(()->tc.tab.isTabPresent(ETab.NOTES),"'%s' tab not found in the modal!".formatted(ETab.NOTES));
            tc.addStepInfo("Non Availability pop up should be reflected with General, Location, Notes and " +
                            "each with OK,Apply, Cancel button",
                    "ok", tc.stepEvaluator.eval());

            //STEP 5
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL));
            tc.tab.select(ETab.GENERAL);
            this.selectDates();
            tc.button.click(EButton.OK);
            WaitFor.condition(()-> !tc.modal.exists(EModal.MODAL),Duration.ofSeconds(5));
            tc.button.click(EButton.GANTT_CALENDAR);
            WaitFor.condition(()->tc.button.isDisplayed(EButton.TODAY));
            tc.button.click(EButton.TODAY);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.addStepInfo("Non Availability should be created for selected date & time and Type",
                    true,this.isNA_Visible());
        });
    }

    public void selectDates()
    {
        CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        EditControl edit = CoreIocBuilder.getContainer().getComponent(EditControl.class);
        WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
        try
        {
            List<WebElement> list = css.findControlsWithRoot(By.cssSelector("input[k-on-open*='onDatePickerOpen']"),root);
            final String date = Generator.getSpecificDate(new DateManipulator().setDay(0), new SimpleDateFormat("MM/dd/yyyy"));
            WebElement startDate = list.get(0);
            WebElement finishDate = list.get(1);
            startDate.clear();
            startDate.sendKeys(date);
            finishDate.clear();
            finishDate.sendKeys(date);
        }
        catch (NullPointerException | NotFoundException e)
        {
            TcLog.error("The element not found "+e);
        }
        catch (Exception e)
        {
            TcLog.error("Error while entering the Date "+e);
        }
    }

    public boolean isNA_Visible()
    {
        CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
        try
        {
            List<WebElement> list = css.findControlsWithRoot(By.cssSelector("div[class *='schedule_realTaskOnGantt']"),root);
            return !list.isEmpty();

        }
        catch (Exception e)
        {
            TcLog.error("Error while getting element "+e);
        }
        return false;
    }
}


