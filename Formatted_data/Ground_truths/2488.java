package TestAutomation_2;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_2488
{
    @Test
    void tc_rs_1885()
    {
        IocBuilder.execute(Duration.ofMinutes(40), EResultData.TEST_AUTOMATION, "2488", tc -> {

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
            String workBook = "SHS S&OP Consensus Demand Planning";
            String workSheet = "Consensus Demand Plan - Units Summary";
            tc.menu.resource.filterResources(workBook, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.VIEW, "Units"),
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.UNIT_OF_MEASURE, "ANZ")
            ));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.modal.exists(EModal.DATA_SETTINGS), "Data settings modal not opened!")
                    .add(workSheet, tc.combo.getSelected(EComboBox.WORKSHEET), "Worksheet wasn't selected!")
                    .add(scenario, tc.combo.getSelected(EComboBox.SCENARIO), "Scenario wasn't selected!")
                    .add("All Parts", tc.combo.getSelected(EComboBox.FILTER), "Filter wasn't selected!")
                    .add("All Sites", tc.combo.getSelected(EComboBox.SITE), "Site wasn't selected!")
                    .add("ANZ", tc.combo.getSelected(EComboBox.UNIT_OF_MEASURE), "Unit of measure wasn't selected!");
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.workBook.getSelected().equals(workBook));
            tc.stepEvaluator.add(workBook, tc.workBook.getSelected(), "Wrong workbook opened!");
            tc.addStepInfoWithScreenShot("The workbook 'SHS S&OP Consensus Demand Planning' is opened.", "ok", tc.stepEvaluator.eval());

            //----- STEP 3 -----
            tc.addStepInfoWithScreenShot("A private scenario under 'S&OP Candidate' is created and selected.", true, !scenario.isEmpty());

            //----- STEP 4 -----
            final String format = "MMM-yy";
            final String currentMonthPlus1 = EColumn.getColumnNameForCurrentMonth(format, 1).getValue();
            final String currentMonthPlus2 = EColumn.getColumnNameForCurrentMonth(format, 2).getValue();
            final String region = "EMEA";
            final String region2 = "West Europe countries";
            final String country = "Germany";
            final int summaryAdjustment = 100;

            //select hierarchy
            tc.hierarchy.openHierarchyPanel();
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.SHS_CUSTOMER);
            tc.list.expand(region);
            tc.list.expand(region2);
            tc.hierarchy.selectItem(EHierarchy.SHS_CUSTOMER, country);
            int rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);

            //edit adjustments on summary level
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonthPlus1), Integer.toString(summaryAdjustment));
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonthPlus2), Integer.toString(summaryAdjustment));
            tc.button.click(EButton.SAVE);

            //Check
            int adjustmentPlus1 = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            int adjustmentPlus2 = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus2), rowLabelsColIndex);
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(summaryAdjustment, adjustmentPlus1, "Adjustment for month '%s' was not set!".formatted(currentMonthPlus1))
                    .add(summaryAdjustment, adjustmentPlus2, "Adjustment for month '%s' was not set!".formatted(currentMonthPlus2));
            tc.addStepInfoWithScreenShot("The adjustments column is updated in the Units summary worksheet.", "ok", tc.stepEvaluator.eval());

            //----- STEP 5 -----
            int sumTolerance = 2; //when calculating sum of unit adjustments, it can differ from summary adjustment by 1, as decimal nums are
            // ignored.
            //get unit level adjustments sum
            tc.stepEvaluator.reset();
            List<Integer> adjustmentsIndexes = TestUtils.getAdjustmentLabelRowIndexes(tc, rowLabelsColIndex + 1);

            // TODO: 10/27/2023  fix... table.scrolltotop doesn't work
            int sumBeforeCurrentMonthPlus1 = TestUtils.calculateSumOfUnitAdjustments(tc, EColumn.getColumnNameForCurrentMonth(1), adjustmentsIndexes);
            int sumBeforeCurrentMonthPlus2 = TestUtils.calculateSumOfUnitAdjustments(tc, EColumn.getColumnNameForCurrentMonth(2), adjustmentsIndexes);
            tc.stepEvaluator
                    .add(() -> this.isDifferenceInRange(summaryAdjustment, sumBeforeCurrentMonthPlus1, sumTolerance),
                            "Month:%s -> Units adjustment(%s) != summary adjustment!(%s)"
                                    .formatted(currentMonthPlus1, sumBeforeCurrentMonthPlus1, summaryAdjustment))
                    .add(() -> this.isDifferenceInRange(summaryAdjustment, sumBeforeCurrentMonthPlus2, sumTolerance),
                            "Month:%s -> Units adjustment(%s) != summary adjustment!(%s)"
                                    .formatted(currentMonthPlus2, sumBeforeCurrentMonthPlus2, summaryAdjustment));
            //edit summary level adjustment
            int summaryAdjustmentMonthPlus1After = 400;
            int summaryAdjustmentMonthPlus2After = 250;
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonthPlus1),
                    Integer.toString(summaryAdjustmentMonthPlus1After));
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonthPlus2),
                    Integer.toString(summaryAdjustmentMonthPlus2After));
            tc.button.click(EButton.SAVE);

            //check unit level
            int sumAfterCurrentMonthPlus1 = TestUtils.calculateSumOfUnitAdjustments(tc, EColumn.getColumnNameForCurrentMonth(1), adjustmentsIndexes);
            int sumAfterCurrentMonthPlus2 = TestUtils.calculateSumOfUnitAdjustments(tc, EColumn.getColumnNameForCurrentMonth(2), adjustmentsIndexes);
            tc.stepEvaluator
                    .add(() -> this.isDifferenceInRange(summaryAdjustmentMonthPlus1After, sumAfterCurrentMonthPlus1, sumTolerance),
                            "Month:%s -> Units adjustment(%s) != summary adjustment!(%s)"
                                    .formatted(currentMonthPlus1, sumAfterCurrentMonthPlus1, summaryAdjustmentMonthPlus1After))
                    .add(() -> this.isDifferenceInRange(summaryAdjustmentMonthPlus2After, sumAfterCurrentMonthPlus2, sumTolerance),
                            "Month:%s -> Units adjustment(%s) != summary adjustment!(%s)"
                                    .formatted(currentMonthPlus2, sumAfterCurrentMonthPlus2, summaryAdjustmentMonthPlus2After));

            //edit adjustments on unit level
            tc.table.scrollToTop(ETable.byIndex(1));
            final int unitAdjustment = 5;
            int unitadjustmentPlus1 = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(1), EColumn.getColumnNameForCurrentMonth(1),
                    rowLabelsColIndex + 1) + unitAdjustment;
            tc.table.setCellValue(ETable.byIndex(1), EColumn.byIndex(rowLabelsColIndex + 1), "Adjustments", EColumn.getColumnNameForCurrentMonth(1),
                    Integer.toString(unitadjustmentPlus1));
            tc.button.click(EButton.SAVE);

            //check summary lebel
            //NOTE:  after save it takes some time until the value is changed (old one were perserved until finishing save)
            WaitFor.condition(() -> TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1),
                    rowLabelsColIndex) != summaryAdjustmentMonthPlus1After);
            int aggregatedAdjustmentCurrMonthPlus1 = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1),
                    rowLabelsColIndex);
            tc.stepEvaluator
                    .add(summaryAdjustmentMonthPlus1After + unitAdjustment, aggregatedAdjustmentCurrMonthPlus1,
                            "Units summary adjustment was not automatically rolled up!");
            tc.addStepInfoWithScreenShot("The data updated in Summary view is disaggregated automatically and visible in the Consensus Demand Plan - Units worksheet.", "ok", tc.stepEvaluator.eval());
        });
    }

    /**
     * Check if diff between 2 numbers is in range.
     *
     * @param num1  Number1.
     * @param num2  Number2.
     * @param range Range.
     * @return True, iff difference between specified numbers is in range.
     */
    @SuppressWarnings("SameParameterValue")
    boolean isDifferenceInRange(int num1, int num2, int range)
    {
        int diff = num1 - num2;
        return diff >= -range && diff <= range;
    }
}


