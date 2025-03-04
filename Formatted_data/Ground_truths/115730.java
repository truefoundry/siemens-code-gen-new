package CAD_TMF_Dispatcher_Education_Role;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class TC15_Dispatcher_Education_Role_Views_EducationWaitlist_EducationWaitlist_ListItem_Edit_Ok
{
    @Test
    void TC_15()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115730", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            String notification_Number = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()).substring(7);

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
            tc.sideBar.select(ESideBar.VIEWS);
            WaitFor.condition(()->tc.sideBar.getElements().contains("Task"));
            tc.addStepInfo("Below sub options should be reflected\n" +
                            "Education Waitlist, Task, Engineer, Education Skills, Assignment",
                    true,tc.sideBar.getElements().containsAll(List.of("Education Waitlist", "Task",
                            "Engineer","Education Skills","Assignment")), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.sideBar.select(ESideBar.EDUCATION_WAITLIST);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("EducationWaitlist".toLowerCase()), "Education Waitlist Page is not visible")
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE_FILTER), "'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.SORTING), "'%s' button not found!".formatted(EButton.SORTING))
                    .add(()-> tc.button.isDisplayed(EButton.EXPORT), "'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.table.exists(ETable.byIndex(0)), "No Datas are found in the table ");
            tc.addStepInfo("Education Waitlist screen should be reflected with Search Bar, Manage Filter, Sorting, Refresh, New, Export buttons and list of EducationWaitlist",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.table.itemClick(ETable.CAD_TABLE, EColumn.byIndex(4),"#0");
            WaitFor.specificTime(Duration.ofSeconds(3));
            WaitFor.condition(()->tc.button.isDisplayed(EButton.DUPLICATE),Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.DUPLICATE), "'%s' button not found!".formatted(EButton.DUPLICATE))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button not found!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button not found!".formatted(EButton.CANCEL));
            tc.edit.sendKeys(EEdit.NOTIFICATION_NUMBER,notification_Number,true);
            tc.button.click(EButton.OK);
            WaitFor.condition(()->tc.edit.isDisplayed(EEdit.SEARCH),Duration.ofSeconds(5));
            tc.edit.sendKeys(EEdit.SEARCH,notification_Number);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.add(()-> tc.table.getItemsFromColumn(ETable.CAD_TABLE,EColumn.NOTIFICATION_NUMBER).contains(notification_Number),
                    "The values has not been edited");
            tc.addStepInfo("Edited Education Waitlist details is displayed",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

        });
    }
}


