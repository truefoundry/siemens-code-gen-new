package TestAutomation_1;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.TcStates;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_2429
{
    @Test
    void tc_rs_1892()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TEST_AUTOMATION, "2429", tc -> {
            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //Create scenario
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);

            //----- STEP 2 -----
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS S&OP Demand Supply Balancing";
            String workSheet = "Demand Supply Balancing - Units Summary";
            tc.menu.resource.filterResources(workBook, false);
            boolean workbookExists = tc.menu.resource.resourceExists(workBook);
            if(!workbookExists)
                tc.abortTest("Workbook '%s' not found!".formatted(workBook), TcStates.ABORTED);
            tc.menu.resource.selectResource(workBook);
            tc.addStepInfoWithScreenShot("Workbook ‘SHS S&OP Demand Supply Balancing’ is opened in RapidResponse.", true, workbookExists);

            //----- STEP 3 -----
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.addStepInfoWithScreenShot("Data setting dialogue is shown in pop up.", true, tc.modal.exists(EModal.DATA_SETTINGS));

            //----- STEP 4 -----
            final String part = "10018564";
            final String site = "1040";
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.VIEW, "Units"),
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, site)
            ));
            String selectedScenario = tc.combo.getSelected(EComboBox.SCENARIO);
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.PRODUCT);
            tc.list.expand("<blank>");
            tc.hierarchy.handleToolargePopup();
            WaitFor.condition(() -> tc.modal.exists(EModal.LOAD_THESE_HIERARCHIES), Duration.ofSeconds(5));
            tc.list.fastScrollToItem(part);
            tc.list.select(part);
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.workBook.getSelected().equals(workBook));
            WaitFor.condition(() ->  tc.tab.getSelected().equals(workSheet));
            tc.addStepInfoWithScreenShot("Worksheet ‘Demand Supply Balancing – Units Summary’ is opened.", workSheet, tc.tab.getSelected());

            //----- STEP 5 -----
            tc.addStepInfoWithScreenShot("Private scenario using the 'S&OP Candidate scenario' was created successfully", scenario, selectedScenario);

            //----- STEP 6 -----
            final String overrideValue = "1,000";
            final String format = "MMM-yy";
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(0), "Consensus Override", EColumn.getColumnNameForCurrentMonth(format, 1),
                    overrideValue);
            String enteredValue = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(0), "Consensus Override",
                    EColumn.getColumnNameForCurrentMonth(format, 1));
            tc.addStepInfoWithScreenShot("Quantity for a month can be overwritten.", overrideValue, enteredValue);

            //----- STEP 7 -----
            tc.button.click(EButton.SAVE);
            WaitFor.specificTime(Duration.ofSeconds(3));
            String demandPlanValue = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(0), "Demand Plan",
                    EColumn.getColumnNameForCurrentMonth(format, 1));
            tc.addStepInfoWithScreenShot("After the record is saved, the ‘Demand Plan’ row is updated with the override quantity.", overrideValue, demandPlanValue);
        });
    }
}


