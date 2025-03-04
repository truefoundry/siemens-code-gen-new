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

public class TC12_Dispatcher_Education_Role_Schedule_Gantt_GanttSorting
{
    @Test
    void TC_12()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115727", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            tc.browser.login(ETestData.DISPATCHER_EDUCATION_ROLE_USER);

            // STEP 1
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.sideBar.select(ESideBar.SCHEDULE);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.stepEvaluator.reset();
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.MAP));
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("schedule".toLowerCase()), "Schedule Page is not visible")
                    .add(()-> (tc.tab.getAllTabs()).containsAll(List.of("Tasks","Clipboard","Gantt","Map")) && (tc.tab.getAllTabs()).stream().anyMatch(s -> s.startsWith("Working on")),
                            "Some tabs('Tasks','Clipboard','Gantt','Map','working on domains') present on the Schedule page are not available");
            tc.addStepInfo("Schedule screen should be reflected with Tasks, Clipboard, Gantt, Map, Working on Domain options",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

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
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.button.click(EButton.GANTT_SORT);
            tc.stepEvaluator.reset();
            tc.stepEvaluator.add(() -> tc.modal.getTitle().equals("Gantt Sorting"), "Gantt Sorting window popup is not be displayed.")
                    .add(() -> tc.button.isDisplayed(EButton.CANCEL), "Cancel button is not present.");
            tc.addStepInfo("Gantt Sorting window popup should be displayed with select columns and Sort by fields along with sort and cancel button",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            List<String> columnNames = List.of("Active", "Approver Login ID");
            tc.list.dragAndDrop(EList.SELECT_COLUMNS, EList.SORT_BY, columnNames);
            tc.addStepInfo("Validate the Selected columns are listed in the sort by fields and which are drag and drop from select columns",
                    true, tc.list.getListItems(EList.SORT_BY).containsAll(columnNames), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


