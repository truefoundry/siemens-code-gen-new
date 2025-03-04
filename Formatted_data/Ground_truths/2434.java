package TestAutomation_2;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TC_ID_2434
{

    @Test
    void tc_rs_1912()
    {
        IocBuilder.execute(Duration.ofMinutes(12), EResultData.TEST_AUTOMATION, "2434", tc -> {

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
            String workBook2 = "SHS Planning Sheet";
            String workSheet2 = "Horizontal MPS (Due Date)";
            String part = "1170609";
            String site = "All Sites";
            String uom = "PC";
            tc.menu.resource.filterResources(workBook, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.VIEW, "Units"),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.SITE, site),
                    Map.entry(EComboBox.UNIT_OF_MEASURE, uom)
            ));
            tc.button.click(EButton.OPEN);
            String cdpDateFormat = "MMM-yy";
            WaitFor.condition(() -> tc.workBook.getSelected().equals(workBook));
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)), Duration.ofSeconds(10));
            tc.workBook.menu.openDataSettings();
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.PRODUCT);
            tc.list.expand("<blank>");
            tc.hierarchy.handleToolargePopup();
            WaitFor.condition(() -> tc.modal.exists(EModal.LOAD_THESE_HIERARCHIES), Duration.ofSeconds(5));
            tc.list.fastScrollToItem(part);
            tc.list.select(part);
            int rowLabelsColIndex = this.getRowLabelsColIndex(tc);
            String currentMonthPlus1 = EColumn.getColumnNameForCurrentMonth(cdpDateFormat, 1).getValue();
            int actualUdp = TestUtils.getUdpFromColumn(tc, ETable.byIndex(0), EColumn.byCustomValue(currentMonthPlus1), rowLabelsColIndex);
            tc.stepEvaluator
                    .add(workBook, tc.workBook.getSelected(), "Wrong workbook opened!")
                    .add(() -> actualUdp > 0,
                            "Unconstrained demand plan = 0 for selected combination! Hierarchy/part:%s, Month:%s."
                                    .formatted(part, currentMonthPlus1));

            tc.addStepInfoWithScreenShot("forecast exists for a specific part/site/month that has ‘Unconstrained Demand Plan’.",
                    "ok", tc.stepEvaluator.eval());

            //----- STEP 3 -----
            tc.stepEvaluator.reset();
            tc.workBook.close("SHS S&OP Consensus Demand Planning");
            tc.menu.resource.filterResources(workBook2, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.WORKSHEET, workSheet2),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.PART, "1170583")
            ));
            tc.stepEvaluator.add("1170583", tc.combo.getSelected(EComboBox.PART), "Wrong part selected!");
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.workBook.getSelected().equals(workBook2));
            tc.workBook.menu.openBucketSettings();
            WaitFor.condition(() -> tc.modal.exists(EModal.ADVANCED_BUCKET_SETTINGS), Duration.ofSeconds(5));
            final String size = "Calendar_Week";
            if(!tc.combo.getText(EComboBox.BUCKETS_AFTER_PLANNING_DATE_SIZE).equals(size))
            {
                tc.combo.select(EComboBox.BUCKETS_AFTER_PLANNING_DATE_SIZE, size);
                if(tc.button.isEnabled(EButton.APPLY))
                {
                    tc.stepEvaluator.add(size, tc.combo.getText(EComboBox.BUCKETS_AFTER_PLANNING_DATE_SIZE), "Wrong bucket size selected!");
                    tc.button.click(EButton.APPLY);
                }
                else
                {
                    TcLog.error("Failed to select 'Calendar week' in 'Advanced bucket settings'!");
                }
            }
            else
            {
                tc.stepEvaluator.add(size, tc.combo.getText(EComboBox.BUCKETS_AFTER_PLANNING_DATE_SIZE), "Wrong bucket size selected!");
                tc.button.click(EButton.CANCEL);
            }
            tc.addStepInfoWithScreenShot("‘Calendar_Week’ under the bucket settings is selected for the ‘Horizontal MPS (Due Date) worksheet under the ‘SHS " +
                    "Planning Sheet’ workbook with selected part/site combination ",
                    "ok", tc.stepEvaluator.eval());

            //----- STEP 4 -----
            tc.stepEvaluator.reset();
            int sum = this.calculateForecastSumForMonth(tc);
            tc.stepEvaluator
                    .add(() -> TestUtils.getToleranceRange(actualUdp,12).containsInteger(actualUdp),
                            "Expected:%s. Actual:%s".formatted(sum, actualUdp));
            tc.addStepInfoWithScreenShot("The forecast line show the spread numbers on the selected ‘Calendar_Week’ bucket setting.", "ok", tc.stepEvaluator.eval());
         });
    }

    /**
     * Returns initial column index(column with labels, usually after hierarchy item columns) for worksheet table, based on selected hirarchy item's
     * level(if item
     * of specific
     * level is
     * selected, there are
     * invisible columns added to table).
     * @param tc Handler.
     * @return Index. -1 in case of error.
     */
    int getRowLabelsColIndex(InVivoHandler tc)
    {
        int level = tc.hierarchy.getSelectedItemsLevel();
        return level < -1 ? 0 : level + 1;
    }

    /**
     * Calculates sum of forecast for current month + 1 month. It is a sum of each week's forecast.
     * @param tc Handler.
     * @return Sum or -1 in case of error.
     */
    int calculateForecastSumForMonth(InVivoHandler tc)
    {
        try
        {
            int forecastRowIndex = tc.table.getItemsIndex(ETable.byIndex(0), EColumn.byIndex(0), "Forecast");
            String monthPlus1 = Integer.toString(LocalDate.now().plusMonths(1).getMonthValue());
            String currentMonthPlus1 = String.format("%02d", Integer.parseInt(monthPlus1));
            List<String> wantedColumns = tc.table.getAllColumnNames(ETable.byIndex(0)).stream().filter(c -> c.startsWith(currentMonthPlus1)).toList();
            TcLog.action("Calculating forecast sum for month:%s.".formatted(currentMonthPlus1));
            return wantedColumns.stream().map(c -> {
                String value = tc.table.getColumnItems(ETable.byIndex(0), EColumn.byCustomValue(c)).get(forecastRowIndex);
                return value.isEmpty() ? 0 : Integer.parseInt(value);
            }).mapToInt(Integer::intValue).sum();
        }
        catch(Exception e)
        {
            TcLog.error("Unexpected error!", e);
        }
        return -1;
    }
}


