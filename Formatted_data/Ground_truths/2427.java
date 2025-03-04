package TestAutomation_1;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;

import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_2427
{
    @Test
    void tc_rs_1890()
    {
        IocBuilder.execute(Duration.ofMinutes(15), EResultData.TEST_AUTOMATION, "2427", tc -> {

            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //----- STEP 2 -----
            //Create scenario
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);

            //Open workbook1
            String workBook1 = "SHS S&OP Consensus Demand Planning";
            String workBook2 = "SHS S&OP Demand Planning Ratios";
            String workSheet1 = "Consensus Demand Plan - Units Summary";
            String workSheet2 = "Edit Default Ratio";
            String part1 = "10013272";   // part that has some unconstrained demand plan for future months.
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook1, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.VIEW, "Units"),
                    Map.entry(EComboBox.WORKSHEET, workSheet1),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.UNIT_OF_MEASURE, "PC")
            ));
            tc.tab.select(ETab.HIERARCHY);  //Cannot select part in filter combo, so selecting it as a hierarchy(based on clarification comment)
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.REMOVE_HIERARCHY));
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.PRODUCT);
            tc.button.click(EButton.OPEN);

            //Open workbook 2
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook2, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.WORKSHEET, workSheet2),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.SITE, "All Sites")
            ));
            tc.button.click(EButton.OPEN);
            List<String> openedWorkbooks = tc.workBook.getAllWorkBooks();
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> openedWorkbooks.contains(workBook1), "Workbook '%s' not opened!".formatted(workBook1))
                    .add(() -> openedWorkbooks.contains(workBook2), "Workbook '%s' not opened!".formatted(workBook2));
            tc.addStepInfoWithScreenShot("Able to create a private scenario, and able to open both the workbooks using the newly created scenario.",
                    "ok", tc.stepEvaluator.eval());

            //----- STEP 3 -----
            tc.stepEvaluator.reset();
            tc.workBook.select(workBook1);
            String format = "MMM-yy";
            String currentMonthPlus1 = EColumn.getColumnNameForCurrentMonth(format, 1).getValue();
            tc.hierarchy.openHierarchyPanel();
            tc.list.expand("<blank>");
            tc.hierarchy.handleToolargePopup();
            tc.list.fastScrollToItem(part1);
            tc.list.select(part1);
            int rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);
            int calculatedUdp1 = TestUtils.calculateUdpForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            int actualUdp1 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            tc.stepEvaluator
                    .add(() -> TestUtils.getToleranceRange(calculatedUdp1).containsInteger(actualUdp1),
                            "Incorrect UDP for column '%s'!\nExpected:%s\nActual:%s".formatted(currentMonthPlus1, calculatedUdp1, actualUdp1));
            tc.addStepInfoWithScreenShot("""
                            Able to see that for a selected part we can see that each of the forecast streams
                            are contributing 25% each while calculating the 'Unconstrained Demand Plan'.""",
                    "ok", tc.stepEvaluator.eval());

            //----- STEP 4 -----
            tc.stepEvaluator.reset();
            final int override = 50;
            tc.workBook.select(workBook2);
            tc.table.filter(ETable.byIndex(0), EColumn.HEADER_PART, part1);
            tc.stepEvaluator.reset();
            tc.table.performActionOnColumn(ETable.byIndex(0), EColumn.RATIO_OVERRIDE, EAction.EDIT_RANGE);
            tc.edit.sendKeys(EEdit.VALUE, "50");
            tc.checkbox.check(ECheckBox.APPLY_TO_ENTIRE_COLUMN);
            tc.button.click(EButton.APPLY);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.SAVE);
            boolean res = tc.table.getColumnItems(ETable.byIndex(0), EColumn.RATIO_OVERRIDE).stream().allMatch(o -> o.equals(override + ".00%"));
            tc.addStepInfoWithScreenShot("Able to change the ratios.", true, res);

            //----- STEP 5 -----
            tc.stepEvaluator.reset();
            tc.workBook.select(workBook1);
            int calculatedUdpOverride = TestUtils.calculateUdpForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1),
                    rowLabelsColIndex, (float)override/100);
            int actualUdpOverride = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            tc.stepEvaluator
                    .add(() -> TestUtils.getToleranceRange(calculatedUdpOverride, 5).containsInteger(actualUdpOverride),
                            "Incorrect UDP for column '%s'!\nExpected:%s\nActual:%s"
                                    .formatted(currentMonthPlus1, calculatedUdpOverride, actualUdpOverride));
            tc.addStepInfoWithScreenShot("The 'Unconstrained Demand Plan' should use the newly created ratios.", "ok", tc.stepEvaluator.eval());
        });
    }
}


