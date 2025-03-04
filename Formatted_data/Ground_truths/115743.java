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

public class TC28_Dispatcher_Education_Role_Views_Assignment_Manage_Filters_Rename
{
    @Test
    void TC_28()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115743", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_EDUCATION_ROLE_USER);
            tc.addStepInfo("Home screen should be reflected",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());

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
            tc.stepEvaluator.reset();
            tc.sideBar.select(ESideBar.ASSIGNMENT);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.MANAGE_FILTER));
            tc.stepEvaluator
                    .add(() -> tc.combo.exists(EComboBox.ALL), "Filter is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.MANAGE_FILTER), "Manage Filter is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.SORTING), "Sorting is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.REFRESH), "Refresh is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.NEW), "New is not displayed")
                    .add(() -> tc.button.isDisplayed(EButton.EXPORT), "export is not visible");
            tc.addStepInfo("Assignment List screen should be displayed with Search bar, Manage Filters, Sorting , Refresh, New and Export Options.",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());
            //STEP 5
            tc.button.click(EButton.MANAGE_FILTER);
            String modalTitle = tc.modal.getTitle();
            tc.addStepInfo("Filters Management - Non-availability popup screen should be displayed",
                    "Filters Management - Non-availability", modalTitle, new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.button.click(EButton.NON_AVAILABILITY_FILTER);
            boolean isRenameEnabled = tc.button.isClickable(EButton.RENAME);
            tc.addStepInfo("Selected Filter folder should be able to Rename.", true, isRenameEnabled
            , new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            String filterName = Generator.getHashedName("TC07_");
            tc.button.click(EButton.RENAME);
            tc.edit.setValue(EEdit.NON_AVAILABILTY_INPUT, filterName);
            tc.combo.select(EComboBox.byIndex(0), "Key");
            tc.combo.select(EComboBox.byIndex(1), "[272621568]");
            tc.button.click(EButton.SAVE);
            String renamedFilterName = tc.edit.getValue(EEdit.NON_AVAILABILTY_INPUT);
            tc.addStepInfo("Rename folder should be saved.", filterName, renamedFilterName,
                    new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


