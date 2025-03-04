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

public class TC20_Dispatcher_Education_Role_Views_Task_Manage_Filters_Advance_Filter
{
    @Test
    void TC_20()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115735", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_EDUCATION_ROLE_USER);
            tc.addStepInfo("CAD application launched and logged in successfully",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.addStepInfo("Below options should be reflected on the left side of the screen Search Field Home " + "Schedule Calendar Management Views Analytics",
                    true,tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.stepEvaluator.reset();
            tc.sideBar.select(ESideBar.VIEWS);
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Education Waitlist" , "Task" , "Engineer" , "Education Skills" , "Assignment")));
            tc.stepEvaluator.add(() -> tc.sideBar.getElements().containsAll(List.of("Education Waitlist" , "Task" , "Engineer"
                    , "Education Skills" , "Assignment")), "The Side bar does not contains certain elements inside view");
            tc.addStepInfo("Below options should be reflected Education Waitlist\" , \"Task\" , \"Engineer\" ," +
                    " \"Education Skills\" , \"Assignment", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.sideBar.select(ESideBar.TASK);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("Task".toLowerCase()), "Task Page is not visible")
                    .add(()-> tc.edit.isDisplayed(EEdit.SEARCH), "Task Search bar not displayed")
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE_FILTER), "'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.SORTING), "'%s' button not found!".formatted(EButton.SORTING))
                    .add(()-> tc.button.isDisplayed(EButton.EXPORT), "'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.button.isDisplayed(EButton.NEW), "'%s' button not found!".formatted(EButton.NEW))
                    .add(()-> tc.table.getRowsCount(ETable.CAD_TABLE)>0, "No Datas are found in the table ");
            tc.addStepInfo("Task screen should be reflected with Search Bar, Manage Filter, Sorting, Refresh,New,Export buttons and list of notification",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.button.click(EButton.MANAGE_FILTER);
            WaitFor.condition(()-> tc.modal.exists(EModal.MODAL));
            tc.addStepInfo("Filters Management - Task screen should be displayed",true,tc.modal.getTitle().equals("Filters Management - Task"),new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.button.click(EButton.ADD);
            WaitFor.condition(()->tc.button.isDisplayed(EButton.ADVANCED_FILTERS));
            tc.addStepInfo("Task filter 01 * - Conditions folder is created",true,tc.button.isDisplayed(EButton.ADVANCED_FILTERS),new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.button.click(EButton.ADVANCED_FILTERS);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("Text Fields is generated like \"Write down the expression to evaluate the condition:\"",
                    true,tc.edit.isDisplayed(EEdit.TASK_ADVANCED_FILTER),new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.edit.sendKeys(EEdit.TASK_ADVANCED_FILTER,"1");
            tc.edit.sendKeys(EEdit.SELECT_PROPERTY,"Actual Break");
            tc.button.click(EButton.APPLY);
            tc.button.click(EButton.SAVE);
            tc.addStepInfo("Expression should be Applied and saved",
                    true,!tc.modal.exists(EModal.MODAL),new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


