package TestAutomation_3;


import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;
import utils.TestUtils;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TC_ID_4669 {
    @Test
   void Ability_to_enter_rolling_forecast_and_view_or_modify_the_forecast_in_monthly_or_quarterly_buckets()
    {
        IocBuilder.execute(Duration.ofMinutes(15), EResultData.TEST_AUTOMATION_3, "4669", tc -> {

            // STEP 1
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //Create scenario
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);

            // STEP 2
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook1 = "SHS S&OP Consensus Demand Planning";
            String workSheet1 = "Consensus Demand Plan - Units Summary";
            tc.menu.resource.filterResources(workBook1, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.addStepInfoWithScreenShot("The workbook 'SHS S&OP Consensus Demand Planning' will open.", workBook1, tc.workBook.getSelected());

            // STEP 3
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.VIEW, "Units"),
                    Map.entry(EComboBox.WORKSHEET, workSheet1),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Items"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.UNIT_OF_MEASURE, "= No conversion =")
            ));
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.button.exists(EButton.DATA_SETTINGS));
            if(tc.button.isPressed(EButton.DATA_SETTINGS))tc.button.click(EButton.DATA_SETTINGS);
            WaitFor.condition(() -> tc.table.getColumnItems(ETable.byIndex(0), EColumn.byIndex(0)).
                    contains("Annual Plan"), Duration.ofSeconds(10));
            tc.table.getColumnItems(ETable.byIndex(0), EColumn.byIndex(0));
            int rowLabelsColIndex = TestUtils.getRowLabelsColIndex(tc);
            String businessLineAT = "Business Line (AT)";
            String spareParts = "Spare Parts (CS)";
            String businessLineXP = "Business Line (XP)";
            String statistical = "Statistical";
            tc.stepEvaluator
                    .add(() -> tc.tab.getSelected().equalsIgnoreCase(workSheet1), "The '%s' is not opened.".formatted(workSheet1))
                    .add(() -> !tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex),
                                    businessLineAT, EColumn.getColumnNameForCurrentMonth("MMM-yy")).isEmpty(),
                            "Data not present for forecast category %s".formatted(businessLineAT))
                    .add(() -> !tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex),
                                    spareParts, EColumn.getColumnNameForCurrentMonth("MMM-yy")).isEmpty(),
                            "Data not present for forecast category %s".formatted(spareParts))
                    .add(() -> !tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex),
                                    businessLineXP, EColumn.getColumnNameForCurrentMonth("MMM-yy")).isEmpty(),
                            "Data not present for forecast category %s".formatted(businessLineXP))
                    .add(() -> !tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex),
                                    statistical, EColumn.getColumnNameForCurrentMonth("MMM-yy")).isEmpty(),
                            "Data not present for forecast category %s".formatted(statistical));
            tc.addStepInfoWithScreenShot("The 'Consensus Demand Plan –Units Summary' worksheet will open and" +
                    "the data for each forecast category can be viewed from this workbook.", "ok", tc.stepEvaluator.eval());

            // STEP 4
            String workBook2 = "SHS SpareParts CS";
            tc.table.clickLinkInsideCell(ETable.byIndex(0), EColumn.byIndex(rowLabelsColIndex), "Spare Parts (CS)", EColumn.byIndex(rowLabelsColIndex));
            WaitFor.condition(() -> tc.workBook.getSelected().equalsIgnoreCase(workBook2), Duration.ofSeconds(15));
            tc.addStepInfoWithScreenShot("The workbook for the forecast category 'Spare Parts (CS) will open.",
                    workBook2, tc.workBook.getSelected());

            // STEP 5
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.PROPOSED_PLAN_DETAILS_UNITS);
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)), Duration.ofSeconds(15));
            tc.stepEvaluator
                    .add(() -> tc.tab.getSelected().equalsIgnoreCase(ETab.PROPOSED_PLAN_DETAILS_UNITS.getValue()),
                            "'%s' worksheet is not opened".formatted(ETab.PROPOSED_PLAN_DETAILS_UNITS.getValue()))
                    .add(() ->tc.table.isCellValueEditable(ETable.byIndex(0), 0, EColumn.byIndex(6), "10"),
                            "Forcast cannot be edited in monthly buckets");
            tc.addStepInfoWithScreenShot("The Proposed Plan Detail worksheet will open and the forecast can be input/edited in monthly buckets",
                    "ok", tc.stepEvaluator.eval());

            //STEP 6
            tc.stepEvaluator.reset();
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook3 = "SHS S&OP Forecast Accuracy";
            String workSheet2 = "Forecast Accuracy";
            tc.menu.resource.filterResources(workBook3, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.WORKSHEET, workSheet2),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Items"),
                    Map.entry(EComboBox.SITE, "All Sites"),
                    Map.entry(EComboBox.FORECAST_CATEGORY, "BusinessLineAT"),
                    Map.entry(EComboBox.ACTUAL_CATEGORY, "Shipment")
            ));
            tc.tab.select(ETab.HIERARCHY);
            if (tc.button.exists(EButton.REMOVE_HIERARCHY))tc.button.click(EButton.REMOVE_HIERARCHY);
            tc.hierarchy.add(EHierarchy.PRODUCT);
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.DATA_SETTINGS));
            tc.tab.select(ETab.GENERAL);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.ERROR_MEASURE), Duration.ofSeconds(5));
            tc.combo.select(EComboBox.ERROR_MEASURE, "MAPE");
            WaitFor.condition(() -> !tc.table.getColumnItems(ETable.byIndex(0), EColumn.byIndex(3)).isEmpty());
            List<String> columnItems = tc.table.getColumnItems(ETable.byIndex(0), EColumn.byIndex(3));
            tc.tab.select(ETab.FORECAST_VALUE_ADD);
            WaitFor.condition(() -> !tc.chart.exists(EChart.byIndex(0)), Duration.ofSeconds(20));
            boolean forecastChartData =  tc.chart.exists(EChart.byIndex(0));
            tc.stepEvaluator
                            .add(() -> !columnItems.isEmpty(), "Column items are empty")
                            .add(() -> forecastChartData, "Forecast value add Chart data is empty");
            tc.addStepInfoWithScreenShot("The SHS S&OP Forecast Accuracy workbook will open.In the Forecast" +
                    " Accuracy worksheet we can see the accuracy of the forecast streams on the basis of the Error Measure " +
                    "selected Similarly in the Forecast Value Add worksheet, we can evaluate if each forecast stream is more or " +
                    "less accurate than the naïve forecast","ok", tc.stepEvaluator.eval());
        });
    }

}


