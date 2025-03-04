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

public class TC_ID_4703 {
    @Test
    void Ability_to_adjust_the_disaggregation_rates_for_particular_part_customer_combination()
    {

        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION_3, "4703", tc -> {

            // STEP 1
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //STEP 2
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);
            tc.addStepInfoWithScreenShot("Private scenario is created.", true, !scenario.isEmpty());

            //STEP 3
            final String workBook1 = "SHS S&OP Forecast Disaggregation";
            final String workBook2 = "SHS S&OP Consensus Demand Planning";
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook1, true);
            tc.addStepInfoWithScreenShot(" '%s' workbook opens.".formatted(workBook1), workBook1, tc.workBook.getSelected());

            //STEP 4
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Items"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.FORECAST_CATEGORY, "1. Spare Parts CS")
            ));
            tc.addStepInfoWithScreenShot("Able to use the data settings", true,
                    tc.modal.exists(EModal.DATA_SETTINGS));

            // STEP 5
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.SHS_SITE);
            tc.list.expand("All");
            tc.hierarchy.selectItem(EHierarchy.SHS_SITE, "1030");
            tc.hierarchy.add(EHierarchy.SHS_CUSTOMER);
            tc.list.expand("EMEA");
            tc.list.expand("Central Western Europe");
            tc.hierarchy.selectItem(EHierarchy.SHS_CUSTOMER, "Germany");
            tc.list.select("Germany");
            tc.hierarchy.add(EHierarchy.SHS_PRODUCT);
            tc.list.expand("Active");
            tc.hierarchy.selectItem(EHierarchy.SHS_PRODUCT,"10018247 Tales-3T-C");
            List<String> hierarchies =  tc.hierarchy.getAddedHierarchies();
            tc.button.click(EButton.OPEN);
            tc.addStepInfoWithScreenShot("Hierarchy is selected.", true, !hierarchies.isEmpty());

            // STEP 6
            String currentMonth = EColumn.getColumnNameForCurrentMonth("MMM-yy").getValue();
            int thirdRow = 2;
            WaitFor.condition(() -> tc.button.exists(EButton.DATA_SETTINGS), Duration.ofSeconds(5));
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byCustomValue(currentMonth));
            tc.table.isCellValueEditable(ETable.byIndex(0),
                    thirdRow,
                    EColumn.byCustomValue(currentMonth),
                    "30.0");
            tc.button.click(EButton.SAVE);
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(1)));
            tc.stepEvaluator
                    .add(true, tc.tab.exists(ETab.RATES_OVERRIDE_DETAIL_UNITS), "Worksheet " +
                            "'%s' is not available" )
                    .add(true, tc.table.getAllColumnNames(ETable.byIndex(1)).contains("Override Units"),
                            "Values are not overridden");
            tc.addStepInfoWithScreenShot("Able to make changes. 'Rates Override Detail - Units' worksheet is" +
                    " available and values are overridden." , "ok", tc.stepEvaluator.eval());
            //STEP 7
            int adjustmentValue = 200;
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook2, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.combo.select(EComboBox.UNIT_OF_MEASURE, "ANZ");
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)));
            int rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);
            List<Integer> adjustmentsIndexes = TestUtils.getAdjustmentLabelRowIndexes(tc, 8);
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "#9", EColumn.byCustomValue(currentMonth),
                    Integer.toString(adjustmentValue));
            tc.button.click(EButton.SAVE);
            WaitFor.specificTime(Duration.ofSeconds(6));
            int calculateSumOfUnitAdjustments = TestUtils.calculateSumOfUnitAdjustments(tc, EColumn.getColumnNameForCurrentMonth(),
                    adjustmentsIndexes);
            boolean result = TestUtils.getToleranceRange(adjustmentValue).containsInteger(calculateSumOfUnitAdjustments);
            tc.addStepInfoWithScreenShot("The forecast is disaggregated using the effective disaggregation rates",
                    true, result);

        });
    }

}


