package TestAutomation_2;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.Generator;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class TC_ID_2422
{
    @Test
    void tc_rs_1897()
    {
        IocBuilder.execute(Duration.ofMinutes(30), EResultData.TEST_AUTOMATION, "2422", tc -> {

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
            tc.addStepInfoWithScreenShot("Private scenario using the 'S&OP Candidate scenario' was created successfully", true, !scenario.isEmpty());


            //----- STEP 3 -----
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS S&OP Consensus Demand Planning";
            String workSheet = "Consensus Demand Plan - Units Summary";
            tc.menu.resource.filterResources(workBook, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.addStepInfoWithScreenShot("Able to open the workbook.", true, tc.modal.exists(EModal.DATA_SETTINGS));

            //----- STEP 4 -----
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.VIEW, "Units"),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.UNIT_OF_MEASURE, "ANZ")
            ));

            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(workSheet, tc.combo.getSelected(EComboBox.WORKSHEET), "Worksheet wasn't selected!")
                    .add(scenario, tc.combo.getSelected(EComboBox.SCENARIO), "Scenario wasn't selected!")
                    .add("All Parts", tc.combo.getSelected(EComboBox.FILTER), "Filter wasn't selected!")
                    .add("All Sites", tc.combo.getSelected(EComboBox.SITE), "Site wasn't selected!")
                    .add("ANZ", tc.combo.getSelected(EComboBox.UNIT_OF_MEASURE), "Unit of measure wasn't selected!");
            tc.addStepInfoWithScreenShot("Able to make selections using the data settings.", "ok", tc.stepEvaluator.eval());

            //----- STEP 5 -----
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.SHS_CUSTOMER);
            String region = "EMEA";
            String region2 = "Central Western Europe";
//            String country = "Germany";
            tc.list.expand(region);
            tc.list.expand(region2);
            tc.hierarchy.selectItem(EHierarchy.SHS_CUSTOMER, region2);
            tc.addStepInfoWithScreenShot("Able to make selection using the hierarchy.", true,
                    tc.hierarchy.getAddedHierarchies().contains(EHierarchy.SHS_CUSTOMER.getValue()));

            //----- STEP 6 -----
            tc.button.click(EButton.OPEN);
            tc.stepEvaluator.add(workBook, tc.workBook.getSelected(), "Workbook not found or incorrect.");
            tc.addStepInfo("Workbook '%s' is opened with used scenario '%s'".formatted(workBook, scenario), "ok", tc.stepEvaluator.eval());

            //----- STEP 7 -----
            String format = "MMM-yy";
            String currentMonth = EColumn.getColumnNameForCurrentMonth(format).getValue();
            String currentMonthPlus1 = EColumn.getColumnNameForCurrentMonth(format, 1).getValue();
            String currentMonthPlus2 = EColumn.getColumnNameForCurrentMonth(format, 2).getValue();
            String currentMonthPlus3 = EColumn.getColumnNameForCurrentMonth(format, 3).getValue();
            String currentMonthPlus4 = EColumn.getColumnNameForCurrentMonth(format, 4).getValue();

            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)));
            int rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);
            int calculatedUdp0 = TestUtils.calculateUdpForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonth), rowLabelsColIndex);
            int calculatedUdp1 = TestUtils.calculateUdpForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            int calculatedUdp2 = TestUtils.calculateUdpForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus2), rowLabelsColIndex);
            int calculatedUdp3 = TestUtils.calculateUdpForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus3), rowLabelsColIndex);
            int calculatedUdp4 = TestUtils.calculateUdpForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus4), rowLabelsColIndex);
            int actualUdp0 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonth), rowLabelsColIndex);
            int actualUdp1 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            int actualUdp2 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus2), rowLabelsColIndex);
            int actualUdp3 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus3), rowLabelsColIndex);
            int actualUdp4 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus4), rowLabelsColIndex);
            int actualAdjustment0 = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonth), rowLabelsColIndex);
            int actualAdjustment1 = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            int actualAdjustment2 = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus2), rowLabelsColIndex);
            int actualAdjustment3 = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus3), rowLabelsColIndex);
            int actualAdjustment4 = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus4), rowLabelsColIndex);

            this.addToStepEvaluator(tc, currentMonth, calculatedUdp0 + actualAdjustment0, actualUdp0);
            this.addToStepEvaluator(tc, currentMonthPlus1, calculatedUdp1 + actualAdjustment1, actualUdp1);
            this.addToStepEvaluator(tc, currentMonthPlus2, calculatedUdp2 + actualAdjustment2, actualUdp2);
            this.addToStepEvaluator(tc, currentMonthPlus3, calculatedUdp3 + actualAdjustment3, actualUdp3);
            this.addToStepEvaluator(tc, currentMonthPlus4, calculatedUdp4 + actualAdjustment4, actualUdp4);
            tc.addStepInfo("The 'Unconstrained Demand Plan' shows up correctly.", "ok", tc.stepEvaluator.eval());

            //----- STEP 8 -----
            int adjustment = 10;
            //            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonth),
            //                    Integer.toString(adjustment));
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonthPlus1),
                    Integer.toString(adjustment));
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonthPlus2),
                    Integer.toString(adjustment));
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonthPlus3),
                    Integer.toString(adjustment));
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonthPlus4),
                    Integer.toString(adjustment));

            tc.button.click(EButton.SAVE);
//            int adjustedUdp0 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonth));
            int adjustedUdp1 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            int adjustedUdp2 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus2), rowLabelsColIndex);
            int adjustedUdp3 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus3), rowLabelsColIndex);
            int adjustedUdp4 = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus4), rowLabelsColIndex);

            this.addToStepEvaluator(tc, currentMonthPlus1, calculatedUdp1 + adjustment, adjustedUdp1);
            this.addToStepEvaluator(tc, currentMonthPlus2, calculatedUdp2 + adjustment, adjustedUdp2);
            this.addToStepEvaluator(tc, currentMonthPlus3, calculatedUdp3 + adjustment, adjustedUdp3);
            this.addToStepEvaluator(tc, currentMonthPlus4, calculatedUdp4 + adjustment, adjustedUdp4);
            tc.addStepInfo("Able to add adjustment, and after adding adjustment the 'Unconstrained Demand Plan' should show correct numbers",
                    "ok", tc.stepEvaluator.eval());

            //----- STEP 9 -----
            tc.workBook.menu.select(EWbMItem.CREATE_ASSUMPTION);
            WaitFor.condition(() -> tc.modal.exists(EModal.CREATE_ASSUMPTION));
            tc.addStepInfoWithScreenShot("Able to open the assumption dialogue box.", true, tc.modal.exists(EModal.CREATE_ASSUMPTION));

            //----- STEP 10 -----
            tc.stepEvaluator.reset();
            String assumptionName = Generator.getHashedName("Assumption");
            String endDate = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            tc.edit.sendKeys(EEdit.ASSUMPTION, assumptionName);
            tc.combo.select(EComboBox.STATUS, "Open");
            tc.edit.sendKeys(EEdit.END_DATE, endDate, true);
            tc.stepEvaluator
                    .add(() -> tc.combo.isDisplayed(EComboBox.ASSIGNED_TO), "Assignee combo not visible!")
                    .add(assumptionName, tc.edit.getText(EEdit.ASSUMPTION), "Assumption field not editable!")
                    .add("Open", tc.combo.getSelected(EComboBox.STATUS), "Status combo not editable!")
                    .add(endDate, tc.edit.getText(EEdit.END_DATE), "End date field not editable!");
            tc.addStepInfoWithScreenShot("Able to add entries in the dialogue window.", "ok", tc.stepEvaluator.eval());

            //----- STEP 11 -----
            tc.button.click(EButton.SAVE);
            WaitFor.specificTime(Duration.ofSeconds(2));
            String assumptionsCurrentMonthNumber = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Assumptions",
                    EColumn.byCustomValue(currentMonth));
            String assumptionsCurrentMonthPlus1Number = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Assumptions",
                    EColumn.byCustomValue(currentMonthPlus1));

            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> !assumptionsCurrentMonthNumber.isEmpty(), "No assumptions for %s in the table!".formatted(currentMonth))
                    .add(() -> !assumptionsCurrentMonthPlus1Number.isEmpty(), "No assumptions for %s in the table".formatted(currentMonthPlus1));
            if(!assumptionsCurrentMonthNumber.contains("#not found!"))
            {
                tc.table.clickLinkInsideCell(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Assumptions", EColumn.byCustomValue(currentMonth));
                WaitFor.specificTime(Duration.ofSeconds(3));
                WaitFor.condition(()-> !tc.table.getColumnItems(ETable.byIndex(1), EColumn.ASSUMPTION).isEmpty(), Duration.ofSeconds(1));
                List<String> assumptionsCurrentMonth = tc.table.getColumnItems(ETable.byIndex(1), EColumn.ASSUMPTION);
                tc.stepEvaluator
                        .add(() -> !assumptionsCurrentMonth.isEmpty(), "Assumption details not visible(%s)!".formatted(currentMonth));
            }
            if(!assumptionsCurrentMonthPlus1Number.contains("#not found!"))
            {
                tc.table.clickLinkInsideCell(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Assumptions", EColumn.byCustomValue(currentMonthPlus1));
                WaitFor.specificTime(Duration.ofSeconds(3));
                List<String> assumptionsCurrentMonthPlus1 = tc.table.getColumnItems(ETable.byIndex(1), EColumn.ASSUMPTION);
                tc.stepEvaluator
                        .add(() -> !assumptionsCurrentMonthPlus1.isEmpty(), "Assumption details not visible(%s)!".formatted(currentMonthPlus1));
            }
            tc.addStepInfoWithScreenShot("Able to view the assumption.", "ok", tc.stepEvaluator.eval());

            //----- STEP 12 -----
            String country = "Germany";
            tc.hierarchy.openHierarchyPanel();
            tc.list.expand(region);
//            tc.list.expand(region2);
            tc.hierarchy.selectItem(EHierarchy.SHS_CUSTOMER, region2);
            //For step 13 before changing to specific unit.
            tc.tab.select(ETab.CONSENSUS_DEMAND_PLAN_UNITS);
            List<Integer> adjustmentsIndexes = TestUtils.getAdjustmentLabelRowIndexes(tc, rowLabelsColIndex + 1);
            int adjustmentsUnitsSum = TestUtils.calculateSumOfUnitAdjustments(tc, EColumn.getColumnNameForCurrentMonth(1), adjustmentsIndexes);
            tc.hierarchy.openHierarchyPanel();
            tc.list.expand(region2);
            tc.hierarchy.selectItem(EHierarchy.SHS_CUSTOMER, country);
            rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);
            tc.addStepInfoWithScreenShot("Able to add assumptions at different levels.", true, tc.button.exists(EWbMItem.CREATE_ASSUMPTION));

            //----- STEP 13 -----
            tc.stepEvaluator.reset();
            tc.stepEvaluator.add(adjustment, adjustmentsUnitsSum,
                    "Sum of adjustments on unit level != adjustment on country level!");
            int actualSelectedUnitAdjustment = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Adjustments", EColumn.byCustomValue(currentMonthPlus1),
                    Integer.toString(actualSelectedUnitAdjustment + adjustment));
            tc.button.click(EButton.SAVE);
            tc.workBook.menu.select(EWbMItem.CREATE_ASSUMPTION);
            WaitFor.condition(() -> tc.modal.exists(EModal.CREATE_ASSUMPTION));
            String assumptionName2 = Generator.getHashedName("Assumption");
            tc.edit.sendKeys(EEdit.ASSUMPTION, assumptionName2);
            tc.combo.select(EComboBox.STATUS, "Open");
            tc.edit.sendKeys(EEdit.END_DATE, endDate, true);
            tc.button.click(EButton.SAVE);
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)));
            tc.stepEvaluator
                    .add(Integer.toString(actualSelectedUnitAdjustment + adjustment), tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex),
                            "Adjustments", EColumn.byCustomValue(currentMonthPlus1)), "Adjustments were not edited!");
            tc.table.clickLinkInsideCell(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Assumptions", EColumn.byCustomValue(currentMonthPlus1));
            WaitFor.condition(() -> tc.table.getAllColumnItems(ETable.byIndex(1), EColumn.ASSUMPTION).contains(assumptionName2),
                    Duration.ofSeconds(30));
            tc.stepEvaluator.add(() ->  tc.table.getAllColumnItems(ETable.byIndex(1), EColumn.ASSUMPTION).contains(assumptionName2), "Assumption " +
                    "was not added!");
            tc.addStepInfoWithScreenShot("Modify the adjustment at this level and add an assumption is possible.", "ok", tc.stepEvaluator.eval());

            //----- STEP 14 -----
            tc.hierarchy.openHierarchyPanel();
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.SHS_CUSTOMER);
            tc.list.expand(region);
//            tc.list.expand(region2);
            tc.hierarchy.selectItem(EHierarchy.SHS_CUSTOMER, region2);
            rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);

            tc.stepEvaluator.reset();
            int adjustmentZone = TestUtils.getAdjustmentForColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            tc.stepEvaluator
                    .add(adjustment + actualSelectedUnitAdjustment, adjustmentZone,
                            "Adjustments are not applied at the country level! Country adjustment:%s, original adjustment:%s.".formatted(adjustmentZone, actualSelectedUnitAdjustment + adjustment));
            tc.addStepInfoWithScreenShot("Adjustments are applied.", "ok", tc.stepEvaluator.eval());
        });
    }

    private void addToStepEvaluator(InVivoHandler tc, String month, int expected, int actual)
    {
        tc.stepEvaluator
                .add(() -> TestUtils.getToleranceRange(expected, 10).containsInteger(actual), ("""
                            Incorrect UDP for column '%s'!
                            Expected:%s.
                            Actual:%s.
                            """).formatted(month, expected, actual));
    }
}


