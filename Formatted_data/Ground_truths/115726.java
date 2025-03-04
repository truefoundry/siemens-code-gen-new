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

public class TC11_Dispatcher_Education_Role_Schedule_Gantt_GanttFilter_Add
{
    @Test
    void TC_11()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115726", tc ->
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
            tc.button.click(EButton.GANTT_FILTER);
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.modal.exists(EModal.GANTT_FILTER_BOX), "'Filter Box is not present")
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE), "'%s' button is not clickable!".formatted(EButton.MANAGE))
                    .add(()-> tc.button.isDisplayed(EButton.CLEAR), "'%s' button is not clickable!".formatted(EButton.CLEAR));
            tc.addStepInfo("Search bar with Manage and Clear button should be reflected",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.button.click(EButton.MANAGE);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.modal.getTitle().equals("Filters Management - Resource"), "The Modal is not present")
                    .add(()-> tc.button.isDisplayed(EButton.ADD), "'%s' button is not clickable!".formatted(EButton.ADD))
                    .add(()-> tc.checkbox.isPresent(ECheckBox.SYSTEM_FILTERS), "'%s' is not present".formatted(ECheckBox.SYSTEM_FILTERS))
                    .add(()-> tc.checkbox.isPresent(ECheckBox.MY_FILTERS), "'%s' is not present".formatted(ECheckBox.MY_FILTERS)) ;
            tc.addStepInfo("Filters Management Resources should be reflected with Add button, System Filters and My filters option",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());


            //STEP 5
            tc.button.click(EButton.ADD);
            WaitFor.condition(()->tc.button.isDisplayed(EButton.ADVANCED_FILTERS));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.RENAME), "'%s' button is not clickable!".formatted(EButton.RENAME))
                    .add(()-> tc.button.isDisplayed(EButton.DELETE), "'%s' button is not clickable!".formatted(EButton.DELETE))
                    .add(()-> tc.button.isDisplayed(EButton.ADVANCED_FILTERS), "'%s' button is not clickable!".formatted(EButton.ADVANCED_FILTERS))
                    .add(()-> tc.button.isDisplayed(EButton.ADD_CONDITION), "'%s' button is not clickable!".formatted(EButton.ADD_CONDITION))
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button is not clickable!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.RESET), "'%s' button not found!".formatted(EButton.RESET))
                    .add(()-> tc.button.isDisplayed(EButton.SAVE), "'%s' button not found!".formatted(EButton.SAVE))
                    .add(()-> tc.button.isDisplayed(EButton.SAVE_AS), "'%s' button not found!".formatted(EButton.SAVE_AS));
            tc.addStepInfo("Rename , Delete button should be enabled with Advance filter check box , " +
                            "Add condition option and Apply, Save As, Save buttons",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.button.click(EButton.ADD_CONDITION);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("User should be able to add the required condition from the drop down",
                    true,tc.edit.isDisplayed(EEdit.SELECT_PROPERTY),new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.button.click(EButton.ADVANCED_FILTERS);
            tc.addStepInfo("\"Write down the expression to evaluate the condition:\" field should be reflected",
                    true,tc.edit.isDisplayed(EEdit.TASK_ADVANCED_FILTER),new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


