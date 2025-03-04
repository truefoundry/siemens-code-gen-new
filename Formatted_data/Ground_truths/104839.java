package CAD_TMF_Dispatcher_Role;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreExcelControl;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.DirectoryControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC26_Dispatcher_Role_View_Resource_Territory_Export
{
    @Test
    void TC_26()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_ROLE, "104839", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_ROLE_USER);
            tc.addStepInfo("CAD application launched and logged in successfully",
                    true, tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.addStepInfo("Below options should be reflected on the left side of the screen Search Field Home " + "Schedule Calendar Management Views Analytics",
                    true,tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.sideBar.select(ESideBar.VIEWS);
            WaitFor.condition(()->tc.sideBar.getElements().contains("Task"));
            tc.addStepInfo("Below sub options should be reflected\n" +
                            "Capacity Limitations, Capacity Used Daily, Education Waitlist, Resource Territory, Task , Engineer",
                    true,tc.sideBar.getElements().containsAll(List.of("Capacity Limitations", "Capacity Used Daily",
                            "Education Waitlist","Resource Territory","Task","Engineer")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.sideBar.select(ESideBar.RESOURCE_TERRITORY);
            tc.spinner.waitForSpinner(ESpinner.TABLE_LOADER,Duration.ofSeconds(3));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.TABLE_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("EngineerTerritory_SO".toLowerCase()), "Engineer Territory Page is not visible")
                    .add(()-> tc.table.getRowsCount(ETable.CAD_TABLE)>0, "No Datas found in Resource Territory View");
            tc.addStepInfo("Resource Territory View page with the list of Resource details should be displayed",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.MANAGE_FILTER), "'%s' button not found!".formatted(EButton.MANAGE_FILTER))
                    .add(()-> tc.button.isDisplayed(EButton.SORTING), "'%s' button not found!".formatted(EButton.SORTING))
                    .add(()-> tc.button.isDisplayed(EButton.EXPORT), "'%s' button not found!".formatted(EButton.EXPORT))
                    .add(()-> tc.button.isDisplayed(EButton.REFRESH), "'%s' button not found!".formatted(EButton.REFRESH))
                    .add(()-> tc.button.isDisplayed(EButton.NEW), "'%s' button not found!".formatted(EButton.NEW))
                    .add(()-> tc.button.isDisplayed(EButton.DELETE), "'%s' button not found!".formatted(EButton.DELETE));
            tc.addStepInfo("Manage filter, sorting,  refresh, Delete,New , Export option should be  displayed",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            List<String> resource_Territory = tc.table.getAllColumnNames(ETable.CAD_TABLE);
            tc.addStepInfo("Date , Resource Name , Territories, Address, Max Distance,Max Travel Time fields with detail list should be displayed",
                    true,
                    resource_Territory.containsAll(List.of("Date","Resource Name","Territories",
                            "Address","Max Distance","Max Travel Time")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.button.click(EButton.EXPORT);
            WaitFor.specificTime(Duration.ofSeconds(5));
            boolean result = tc.browser.checkDownloadedSpreadsheetFile();
            tc.addStepInfo("Data present in the Resource Territories view page should be exported and file is " +
                    "downloaded in local device in the Excel format",
                    true, result,new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            Path lastFile = DirectoryControl.getLastDownloadedFile();
            Map<Integer,List<String>> csvFileData = CoreExcelControl.getCsvData(String.valueOf(lastFile));
            tc.addStepInfo("Exported Excel file should be opened in the local device",
                    true, !csvFileData.isEmpty(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 9
            List<String> firstRow = tc.table.getAllValuesFromRow(ETable.CAD_TABLE,0);
            tc.addStepInfo("Data should be validated.",
                    true, firstRow.get(2).equals(csvFileData.get(1).get(1).replace("\"", "").trim()),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


