package TestAutomation_3;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_4704 {

    @Test
    void TC_RS_Ability_to_generate_and_publish_the_statistical_forecast()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION_3, "4704", tc -> {
            // STEP 1
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            // STEP 2
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);
            tc.addStepInfoWithScreenShot("Private scenario is created.", true, !scenario.isEmpty());

            // STEP 3
            String workBook1 = "SHS S&OP Forecast Item Management";
            String workSheet1 = "Define Forecast Item Level";
            String workBook2 = "SHS S&OP Consensus Demand Planning";
            String workSheet2 = "Consensus Demand Plan - Units Summary";
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook1, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.stepEvaluator.add(workBook1, tc.workBook.getSelected(), "Unable to open '%s' workbook".formatted(workBook1))
                    .add(() -> tc.modal.exists(EModal.DATA_SETTINGS), "Unable to open Data Settings window");
            tc.addStepInfoWithScreenShot("Able to open the workbook and a data setting window pops up", "ok", tc.stepEvaluator.eval());

            // STEP 4
            tc.stepEvaluator.reset();
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.WORKSHEET, workSheet1),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites")
            ));
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.SHS_PRODUCT);
            tc.button.click(EButton.OPEN);
            WaitFor.specificTime(Duration.ofSeconds(7));
            tc.stepEvaluator.add(scenario, tc.button.getName(EButton.SCENARIO_FILTER_ITEM), "Scenario not found or incorrect.")
                    .add(workSheet1, tc.tab.getSelected(), "Worksheet not found or incorrect")
                    .add("All Parts", tc.button.getName(EButton.FILTER), "Selected filter is incorrect")
                    .add("All Sites", tc.button.getName(EButton.SITE_FILTER), "Selected Site is incorrect");
            tc.addStepInfoWithScreenShot("The '%s' workbook opens with the selected Data settings".formatted(workBook1),
                    "ok", tc.stepEvaluator.eval());

            // STEP 5
            tc.button.click(EButton.WORKBOOK_COMMANDS);
            tc.workBook.menu.select(EWbMItem.WORKBOOK_COMMANDS_GENERATE_AND_SAVE_FORECAST);
            boolean res = tc.modal.exists(EModal.GENERATE_AND_SAVE_FORECAST);
            WaitFor.condition(tc.modal::exists);
            tc.button.click(EButton.RUN);
            WaitFor.condition(() -> tc.button.exists(EButton.CANCEL));
            tc.button.click(tc.button.exists(EButton.CONFIRM) ? EButton.CONFIRM : EButton.DONE);
            tc.addStepInfoWithScreenShot("Command run is completed. Forecast is generated", true, res);

            // STEP 6
            tc.stepEvaluator.reset();
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook2, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.stepEvaluator.add(workBook2, tc.workBook.getSelected(), "Unable to open '%s' workbook".formatted(workBook2))
                    .add(() -> tc.modal.exists(EModal.DATA_SETTINGS), "Unable to open Data Settings window");
            tc.addStepInfoWithScreenShot("Able to open the workbook and a data setting window pops up", "ok", tc.stepEvaluator.eval());

            // STEP 7
            String afterPlaningDate = "36";
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.WORKSHEET, workSheet2),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.UNIT_OF_MEASURE, "ANZ")
            ));
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.add(EHierarchy.SHS_PRODUCT);
            tc.tab.select(ETab.BUCKETS);
            tc.button.click(EButton.EDIT_BUCKETS);
            tc.edit.sendKeys(EEdit.BUCKETS_BEFORE_PLANNING_DATE_BUCKETS, "0", true);
            tc.edit.sendKeys(EEdit.BUCKETS_AFTER_PLANNING_DATE_BUCKETS, afterPlaningDate, true);
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.button.exists(EButton.DATA_SETTINGS));
            tc.workBook.menu.openBucketSettings();
            String afterDate = tc.edit.getText(EEdit.BUCKETS_AFTER_PLANNING_DATE_BUCKETS);
            tc.button.click(EButton.CANCEL);
            tc.button.click(EButton.DATA_SETTINGS);
            tc.addStepInfoWithScreenShot("Able to open the workbook and view the full plan cycle of 36 months",
                    true, afterDate.equals(afterPlaningDate));

            // STEP 8
            tc.stepEvaluator.reset();
            String format = "MMM-yy";
            int adjustment = 10;
            String currentMonthPlus1 = EColumn.getColumnNameForCurrentMonth(format, 1).getValue();
            String currentMonthPlus2 = EColumn.getColumnNameForCurrentMonth(format, 2).getValue();
            int rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments",
                    EColumn.byCustomValue(currentMonthPlus1), Integer.toString(adjustment));
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments",
                    EColumn.byCustomValue(currentMonthPlus2), Integer.toString(adjustment));
            tc.button.click(EButton.SAVE);
            int calculatedUdp1 = TestUtils.calculateUdpForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1),
                    rowLabelsColIndex) + adjustment;
            int calculatedUdp2 = TestUtils.calculateUdpForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus2),
                    rowLabelsColIndex) +adjustment;
            int actualUdp1 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            int actualUdp2 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus2), rowLabelsColIndex);
            tc.stepEvaluator
                    .add(() -> TestUtils.getToleranceRange(calculatedUdp1).containsInteger(actualUdp1),
                            "Incorrect UDP for column '%s'!\nExpected:%s\nActual:%s".formatted(currentMonthPlus1, calculatedUdp1, actualUdp1))
                    .add(() -> TestUtils.getToleranceRange(calculatedUdp2).containsInteger(actualUdp2),
                            "Incorrect UDP for column '%s'!\nExpected:%s\nActual:%s".formatted(currentMonthPlus2, calculatedUdp2, actualUdp2));
            tc.addStepInfoWithScreenShot("Verify the statistical forecast quantities with other categories and add adjustments to the"
                            + " demand quantities  in the Consensus Demand Plan - Unit Summary", "ok", tc.stepEvaluator.eval());
        });
    }
}


