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

public class TC25_Dispatcher_Education_Role_Views_Education_Skills_Manage_Filters_Add
{
    @Test
    void TC_25()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115740", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_EDUCATION_ROLE_USER);
            tc.addStepInfo("Home screen should be reflected",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.edit.isDisplayed(EEdit.SERVICE_EDGE_SEARCH), "'%s' Search field is not Displayed!".formatted(EEdit.SERVICE_EDGE_SEARCH))
                    .add(()-> tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics","Views")),"The Side bar does not contains certain elements");
            tc.addStepInfo("Below options should be reflected on the left side of the screen Search Field Home " + "Schedule Calendar Management Views Analytics",
                    "ok",tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.stepEvaluator.reset();
            tc.sideBar.select(ESideBar.VIEWS);
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Education Waitlist" , "Task" , "Engineer" , "Education Skills" , "Assignment")));
            tc.stepEvaluator.add(() -> tc.sideBar.getElements().containsAll(List.of("Education Waitlist" , "Task" , "Engineer"
                    , "Education Skills" , "Assignment")), "The Side bar does not contains certain elements inside view");
            tc.addStepInfo("Below options should be reflected Education Waitlist\" , \"Task\" , \"Engineer\" ," +
                    " \"Education Skills\" , \"Assignment", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.sideBar.select(ESideBar.EDUCATION_SKILLS);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.MANAGE_FILTER));
            tc.stepEvaluator
                    .add(() -> tc.combo.exists(EComboBox.ALL), "Filter is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.MANAGE_FILTER), "Manage Filter is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.SORTING), "Sorting is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.REFRESH), "Refresh is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.NEW), "New is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.EXPORT), "export is not visible");
            tc.addStepInfo("Education skills screen should be reflected with Manage Filters, Sorting, Refresh," +
                            " New Options and List of Education Skills", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.stepEvaluator.reset();
            tc.button.click(EButton.MANAGE_FILTER);
            WaitFor.condition(tc.modal::exists);
            tc.filter.removeAllFilter();
            tc.button.click(EButton.ADD);
            String modal = "Filters Management - EducationSkills";
            tc.stepEvaluator
                    .add(() -> tc.modal.getTitle().equalsIgnoreCase(modal), "Modal is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.ADD), "Add button is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.RENAME), "Rename is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.DELETE), "Delete is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.ADD_CONDITION), "Add condition is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.ADVANCED_FILTERS), "Advance filter is not displayed")
                    .add(() -> tc.checkbox.isPresent(ECheckBox.SYSTEM_FILTERS), "System filter is not displayed")
                    .add(() -> tc.checkbox.isPresent(ECheckBox.MY_FILTERS), "My filter is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.SAVE), "Save button is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.SAVE_AS), "Save_as button is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.APPLY), "Apply button is not displayed");
            tc.addStepInfo("Filters Management - EducationSkills popup screen should be displayed with Add, Rename," +
                    " Delete, Add Condition, Advanced Filter, System Filters, My Filters Options along with Apply, Save " +
                    "As and Save button", "ok", tc.stepEvaluator.eval());
            //STEP 6
            tc.stepEvaluator.reset();
            tc.combo.select(EComboBox.byIndex(0), "Key");
            tc.combo.select(EComboBox.byIndex(1), "ARTIS ONE");
            tc.button.click(EButton.SAVE);
            tc.addStepInfo("EducationSkills filter 01 * folder should be created and condition should be entered and saved",
                   true , tc.filter.getLastFilterName().contains("filter 01"), new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


