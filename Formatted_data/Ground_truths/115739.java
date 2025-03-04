package CAD_TMF_Dispatcher_Education_Role;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.Generator;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC24_Dispatcher_Education_Role_Views_Education_Skills_New
{
    @Test
    void TC_24()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115739", tc ->
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
            tc.stepEvaluator.eval();
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
            tc.button.click(EButton.NEW);
            tc.stepEvaluator.eval();
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.OK));
            tc.stepEvaluator
                    .add(() -> tc.edit.isDisplayed(EEdit.byIndex(1)), "Skill input is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.OK), "OK is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.APPLY), "Apply is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.CANCEL), "Cancel is not displayed");
            tc.addStepInfo("New Education Skills screen should be displayed with Skill fields along with ok, " +
                    "Apply and Cancel button", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            String skillName = Generator.getHashedName("Skill_");
            tc.edit.setValue(EEdit.byIndex(1), skillName);
            tc.button.click(EButton.OK);
            WaitFor.condition(() -> tc.edit.isDisplayed(EEdit.SEARCH));
            tc.edit.sendKeys(EEdit.SEARCH, skillName);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("Education Skills is Added to the List with a Key Number", true,
                    tc.table.getItemsFromColumn(ETable.CAD_TABLE, EColumn.NAME).contains(skillName),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


