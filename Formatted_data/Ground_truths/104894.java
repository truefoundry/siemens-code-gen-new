package CAD_TMF_Zone_Operation_Lead_Role;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC14_Zone_Operation_Lead_Role_Schedule_Gantt_Resources_Option
{
    @Test
    void TC_14()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104894", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            tc.browser.login(ETestData.ZONE_OPERATION_USER);

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
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.addStepInfo("Below option should be reflected",true,
                    tc.gantt.getAllGanttUser().containsAll(List.of("Edit","Show Home Base","Show Daily Route",
                            "Add Tasks To Clipboard","Relocate Resource","Cancel All Relocations")));

            //STEP 4
            tc.gantt.performGanttUserActions(EAction.EDIT);
            WaitFor.specificTime(Duration.ofSeconds(5));
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL),Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.modal.exists(EModal.MODAL),"Modal does not exist")
                    .add(()->tc.modal.getTitle().equals("Resource"),"Modal title should be Non Availability but not found")
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button not found in the modal!".formatted(EButton.APPLY))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button not found in the modal!".formatted(EButton.CANCEL))
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button not found in the modal!".formatted(EButton.OK))
                    .add(()->tc.tab.isTabPresent(ETab.HISTORY),"'%s' tab not found in the modal!".formatted(ETab.LOCATION))
                    .add(()->tc.tab.getAllTabs().containsAll(List.of("Professional","Location","Personal")),"some tabs not found in the modal!");
            tc.addStepInfo("Resource pop up should be reflected with Personal, Location, Professional and History sections with OK, Apply, Cancel button for each",
                    "ok", tc.stepEvaluator.eval());


        });
    }
}


