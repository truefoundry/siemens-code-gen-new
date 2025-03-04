package CAD_TMF_Zone_Operation_Lead_Role;

import CompositionRoot.IocBuilder;
import CompositionRoot.ProjectHandler;
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

public class TC22_Zone_Operation_Lead_Role_View_Education_WaitList_Duplicate
{
    @Test
    void TC_22()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104902", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            String notification_Number = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()).substring(7);
            String preferred_ces = "Tester22";
            //STEP 1
            tc.browser.login(ETestData.ZONE_OPERATION_USER);
            tc.addStepInfo("CAD application launched and logged in successfully",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.addStepInfo("Below options should be reflected on the left side of the screen " +
                            "Search Field Home Schedule Calendar Management Views Analytics",
                    true,tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.sideBar.select(ESideBar.VIEWS);
            WaitFor.condition(()->tc.sideBar.getElements().contains("Task"));
            tc.addStepInfo("Below sub options should be reflected\n" +
                            "Capacity Limitations, Capacity Used Daily, Education Waitlist, Resource Territory, Task , Engineer",
                    true,tc.sideBar.getElements().containsAll(List.of("Capacity Limitations", "Capacity Used Daily",
                            "Education Waitlist","Resource Territory","Task","Engineer")),new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.sideBar.select(ESideBar.EDUCATION_WAITLIST);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.addStepInfo("Education WaitList screen should be displayed",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("EducationWaitlist".toLowerCase()),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE_FILTER), "'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.SORTING), "'%s' button not found!".formatted(EButton.SORTING))
                    .add(()-> tc.button.isDisplayed(EButton.DELETE), "'%s' button not found!".formatted(EButton.DELETE))
                    .add(()-> tc.button.isDisplayed(EButton.NEW), "'%s' button not found!".formatted(EButton.NEW))
                    .add(()-> tc.button.isDisplayed(EButton.EXPORT), "'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.table.exists(ETable.byIndex(0)), "No Datas are found in the table ");
            tc.addStepInfo("Manage filter, sorting,  refresh,Delete, New , Export option should be  displayed",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            WaitFor.specificTime(Duration.ofSeconds(3));
            List<String> educational_Waitlist = tc.table.getAllColumnNames(ETable.CAD_TABLE);
            tc.addStepInfo("Education Waitlist should contains fields like Priority Ranking," +
                            " Notification Number, Sales Order, FL#, Requested Training Start Date, Customer Contact Date, " +
                            "Preferred CSE, Customer Name, City, State",
                    true,
                    educational_Waitlist.containsAll(List.of("Priority Ranking","Notification Number","Sales Order #",
                            "FL #","Requested Training Start Date","Customer Contact Date","Preferred CES","Customer Name","City","State")) ,new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            //Pre - steps //
            create_notification(tc,notification_Number,preferred_ces);
            //
            tc.edit.sendKeys(EEdit.SEARCH,notification_Number,true);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.table.itemClick(ETable.CAD_TABLE,EColumn.NOTIFICATION_NUMBER,"#0");
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.DUPLICATE), "'%s' button not found!".formatted(EButton.DUPLICATE))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.button.isDisplayed(EButton.OK), "'%s' button not found!".formatted(EButton.OK))
                    .add(()-> tc.button.isDisplayed(EButton.DELETE), "'%s' button not found!".formatted(EButton.DELETE))
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button not found!".formatted(EButton.CANCEL));
            tc.addStepInfo("Notification details should be displayed",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            String waitList = tc.navigation.getLastBreadcrumb();
            tc.button.click(EButton.DUPLICATE);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("Another copy of same notification should be created",
                    true,("Duplicate of "+waitList).equals(tc.navigation.getLastBreadcrumb()),new ComparerOptions().takeScreenShotPlatform());

            //STEP 9
            tc.button.click(EButton.APPLY);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.button.click(EButton.OK);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("Duplicate copy is created and saved",
                    true,tc.navigation.getAllBreadCrumbs().size()==2,new ComparerOptions().takeScreenShotPlatform());

            //STEP 10
            tc.navigation.navigateTo("View");
            tc.edit.sendKeys(EEdit.SEARCH,notification_Number,true);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.addStepInfo("Duplicate copy of the same notification should be displayed",
                    true,tc.table.getRowsCount(ETable.CAD_TABLE) == 2 &&
                            tc.table.getItemsFromColumn(ETable.CAD_TABLE,EColumn.NOTIFICATION_NUMBER).stream().distinct().count() == 1
                    ,new ComparerOptions().takeScreenShotPlatform());
        });
    }
    private void create_notification(ProjectHandler tc , String notification_Number , String preferred_ces)
    {
        tc.button.click(EButton.NEW);
        WaitFor.condition(()->tc.button.isDisplayed(EButton.OK));
        tc.edit.sendKeys(EEdit.NOTIFICATION_NUMBER,notification_Number,true);
        tc.edit.sendKeys(EEdit.PREFERRED_CES,preferred_ces,true);
        tc.button.click(EButton.OK);
        WaitFor.condition(()->tc.table.exists(ETable.CAD_TABLE));
    }
}


