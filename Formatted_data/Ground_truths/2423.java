package TestAutomation_1;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Statics.MsgCode;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TC_ID_2423
{
    @Test
    void TC_RS_2107()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION, "2423", tc -> {
            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //----- STEP 2 -----
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBookForecastAccuracy = "SHS S&OP Forecast Accuracy";
            String scenario = "S&OP Candidate";
            tc.menu.resource.filterResources(workBookForecastAccuracy, true);
            tc.addStepInfoWithScreenShot("Workbook '%s' is opened.".formatted(workBookForecastAccuracy), workBookForecastAccuracy, tc.workBook.getSelected());

            //----- STEP 3 -----
            WaitFor.condition(()-> tc.button.exists(EButton.OPEN));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "1040"),
                    Map.entry(EComboBox.ACTUAL_CATEGORY, "Shipment"),
                    Map.entry(EComboBox.FIRST_LAG, "1 Month"),
                    Map.entry(EComboBox.SECOND_LAG, "2 Months"),
                    Map.entry(EComboBox.THIRD_LAG, "3 Months")
            ));
            tc.addStepInfoWithScreenShot("The scenario '%s' is selected.".formatted(scenario), scenario, tc.combo.getSelected(EComboBox.SCENARIO));

            //----- STEP 4 -----
            tc.edit.sendKeys(EEdit.HISTORICAL_INTERVAL_COUNT, "12", true);
            String count = tc.edit.getText(EEdit.HISTORICAL_INTERVAL_COUNT);
            tc.addStepInfoWithScreenShot("12 months are set in the Data settings.", "12", count);

            // ----- STEP 5 -----
            tc.button.click(EButton.OPEN);
            openDataSettings(tc);
            WaitFor.condition(() -> tc.tab.exists(ETab.HIERARCHY));
            tc.tab.select(ETab.HIERARCHY);
            WaitFor.condition(() -> tc.button.exists(EButton.ADD_HIERARCHY));
            tc.hierarchy.removeAll();
            tc.button.click(EButton.ADD_HIERARCHY);
            EHierarchy hierarchy = EHierarchy.SHS_CUSTOMER;
            tc.hierarchy.add(hierarchy);
            WaitFor.condition(() -> tc.hierarchy.getAddedHierarchies().contains(hierarchy.getValue()));
            tc.list.select("EMEA");
            tc.addStepInfoWithScreenShot("Hierarchy '%s' is selected.".formatted(hierarchy),
                    true,
                    tc.hierarchy.getAddedHierarchies().contains(hierarchy.getValue()));

            // ----- STEP 6 -----
            WaitFor.condition(() -> tc.table.isColumnDisplayed(ETable.byIndex(0), EColumn.FORECAST_CATEGORY), Duration.ofSeconds(30));
            tc.table.clickLinkInsideCell(ETable.byIndex(0), EColumn.FORECAST_CATEGORY, "SparePartsCS", EColumn.FORECAST_CATEGORY);
            WaitFor.specificTime(Duration.ofSeconds(10));
            WaitFor.condition(() -> tc.chart.isDisplayed(EChart.byIndex(0)), Duration.ofSeconds(30));
            tc.frame.switchToDefault();
            tc.addStepInfoWithScreenShot("The  Forecast Accuracy Detail MAPE chart is available.", true,
                    tc.tab.getSelected().equalsIgnoreCase("Forecast Accuracy Detail MAPE"));

            // ----- STEP 7 -----
            List<String> apeMeasures = tc.chart.getGroups(EChart.byIndex(0));
            boolean isDataPopulated = apeMeasures.isEmpty();
            tc.addStepInfoWithScreenShot("APE measures are available from the tool and can be used for calculation testing",
                    true, !isDataPopulated);

            // ----- STEP 8 -----
            HashMap<String,  Map<String, String>> data = tc.chart.getDataFromDom(EChart.byIndex(0));
            String targetDescription = "Absolute Percent Error Lag 1 Month.";
            Map<String, String> targetMap = data.getOrDefault(targetDescription, new HashMap<>());
            List<String> extractedValues = new ArrayList<>(targetMap.values());
            double calculatedValue = this.calculateMAPE(extractedValues);
            tc.addStepInfoWithScreenShot("Values needed for the calculation are available from the tool.", true,
                                !extractedValues.isEmpty());

            // ----- STEP 9 -----
            tc.frame.switchToDefault();
           double actualValue =  Double.parseDouble(tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.FORECAST_CATEGORY, "SparePartsCS",
                    EColumn.byCustomValue("Error Measure: MAPE Lag 1 Month")).replace("%", ""));
           tc.addStepInfoWithScreenShot("Final calculation is the same as the one generated by the tool.",
                   true, calculatedValue == actualValue);
        });
    }
    private void openDataSettings(InVivoHandler tc)
    {
        WaitFor.condition(() -> tc.button.exists(EButton.DATA_SETTINGS));
        if(tc.button.isPressed(EButton.DATA_SETTINGS))
        {
            return;
        }
        tc.button.click(EButton.DATA_SETTINGS);
        WaitFor.condition(() -> tc.button.isPressed(EButton.DATA_SETTINGS));
    }

    private double calculateMAPE(List<String> apeValues)
    {
        // Step 1: Convert the list of strings to doubles and apply the multiplication rule if necessary
        List<Double> convertedValues = apeValues.stream()
                .map(value -> {
                    double doubleValue = Double.parseDouble(value);
                    if (value.startsWith("0"))
                    {
                        return doubleValue * 100;
                    }
                    else
                    {
                        return doubleValue;
                    }
                }).toList();

        // Step 2: Sum all APE values
        double sumAPE = convertedValues.stream().mapToDouble(Double::doubleValue).sum();

        // Step 3: Count the number of intervals
        int intervalCount = convertedValues.size();

        // Step 4: Calculate the average
        double result = sumAPE / intervalCount;

        // Step 5: Round the result to one decimal place
        return Math.round(result * 10.0) / 10.0;
    }
}


