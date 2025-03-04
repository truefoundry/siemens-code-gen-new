package TestAutomation_1;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NotFoundException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TC_ID_2435
{
    @Test
    void TC_RS_1918_As_Demand_Planner_I_want_the_ability_to_visualize_the_forecast_consumption()
    {
        IocBuilder.execute(Duration.ofMinutes(15), EResultData.TEST_AUTOMATION, "2435", tc -> {
            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //----- STEP 2 -----
            tc.menu.openMenu(EActivity.SCENARIOS);
            String privateScenario = tc.menu.scenarios.create("S&OP Candidate");
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS Forecast Consumption Analysis";
            tc.menu.resource.filterResources(workBook, true);
            //this scenario was replaced by scenario for which a data are displaeyd
            //String scenario = "Master Production Scheduling";
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, privateScenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All sites"),
                    Map.entry(EComboBox.PART, "= All =")
            ));
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.SHS_SITE);
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.button.exists(EButton.SCENARIO_FILTER_ITEM));
            tc.stepEvaluator.add(workBook, tc.workBook.getSelected(), "Workbook not found or incorrect.");
            tc.stepEvaluator.add(privateScenario, tc.button.getName(EButton.SCENARIO_FILTER_ITEM), "Scenario not found or incorrect.");
            tc.addStepInfoWithScreenShot("The '%s' workbook is opened, using the '%s' scenario.".formatted(workBook, privateScenario),
                    "ok", tc.stepEvaluator.eval());

            //----- STEP 3 -----
            tc.tab.select(ETab.ACTUAL_FORECAST_CONSUMPTION);
            //categories are repeating, we consider only first product
            checkForecastConsumption(tc);

            tc.addStepInfoWithScreenShot("The worksheet ‘Actual/Forecast Consumption %’ in the ‘SHS Forecast Consumption Analysis’" +
                            " workbook shows the consensus forecast, spread of the forecast ," +
                            " consumed and unconsumed forecast for a given period.\n" +
                            "The details and percentages of how the spread forecast is consumed is also displayed.",
                    "ok",
                    tc.stepEvaluator.eval());

            //----- STEP 4 -----
            tc.tab.select(ETab.FORECAST_CONSUMPTION_CHART);
            WaitFor.specificTime(Duration.ofSeconds(4));
            WaitFor.condition(() -> tc.chart.exists(EChart.byIndex(0)), Duration.ofSeconds(20));
            var chart = tc.chart.getDataFromDom(EChart.byIndex(0));
            List<String> relatedCategories = List.of("Overconsumed Forecast.", "Sales Forecast Quantity Consumed.", "Sales Forecast Quantity Unconsumed.",
                    "Forecast Quantity.", "Sales Order Quantity.");
            relatedCategories.forEach(
                    category -> tc.stepEvaluator.add(true,
                            chart.containsKey(category) && chart.get(category).size() > 0,
                            "Chart doesn't contain category '%s' or is empty.".formatted(category)));
            tc.addStepInfoWithScreenShot("Forecast Consumption Chart’ is showing the consumption related numbers.",
                    "ok",
                    tc.stepEvaluator.eval());

            //----- STEP 5 -----
            tc.stepEvaluator.eval();
            tc.tab.select(ETab.FORECAST_CONSUMPTION);
            List<String> columns = List.of("Forecast Quantity", "Forecast Unconsumed", "Actual Quantity", "Actual Consumed");
            int rowsCount = 6;
            var subTable = readSubTable(tc, 6, columns);
            tc.stepEvaluator.add(() -> !isSubTableEmpty(subTable), ("Missing content for columns '%s', or cannot be read. Current content for " +
                    "these " +
                    "columns is '%s'")
                    .formatted(columns, formatValues(subTable)));

            findNonNumbers(tc, subTable);
            tc.stepEvaluator.add(() -> findNonPositiveNonNumber_ColumnValues(subTable.get("Forecast Quantity")).isEmpty(),
                    "In 'Forecast Quantity' %s".formatted(findNonPositiveNonNumber_ColumnValues(subTable.get("Forecast Quantity"))));
            tc.stepEvaluator.add(() -> findNonPositiveNonNumber_ColumnValues(subTable.get("Actual Quantity")).isEmpty(),
                    "In 'Actual Quantity' %s".formatted(findNonPositiveNonNumber_ColumnValues(subTable.get("Actual Quantity"))));
            tc.addStepInfoWithScreenShot("Forecast for a customer or part along with the actual orders that are consuming" +
                            " the forecast are shown in  the worksheet 'Forecast Consumption’.",
                    "ok",
                    tc.stepEvaluator.eval());

            //----- STEP 6 -----
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.ORDERS_CONSUMING_FORECAST);
            List<String> columnsInOrderWorkSheet = List.of("Actual Order Number:Line", "Actual Site", "Actual Type", "Forecast Order Number:Line",
                    "Actual Consumed", "Forecast Unconsumed");

            var subTableOrders = readSubTable(tc, 2, columnsInOrderWorkSheet);
            tc.stepEvaluator.add(() -> !isSubTableEmpty(subTableOrders), ("Missing content for columns '%s', or cannot be read. Current content for" +
                    " " +
                    "these " +
                    "columns is '%s'")
                    .formatted(columnsInOrderWorkSheet, formatValues(subTableOrders)));
            findNonNumbers(tc, subTable);

            tc.addStepInfoWithScreenShot("The worksheet displays the actual orders that are consuming the forecast" +
                            " in a given period along with the forecast orders. Also the quantity of the forecast that's consumed or unconsumed are" +
                            " shown.",
                    "ok",
                    tc.stepEvaluator.eval());
        });
    }

    private void checkForecastConsumption(InVivoHandler tc)
    {
        AtomicReference<List<String>> visibleProductCategories = new AtomicReference<>();
        WaitFor.condition(() -> {
            visibleProductCategories.set(tc.table.getColumnItems(ETable.byIndex(0), EColumn.byCustomValue("Header name")));
            return visibleProductCategories.get().size() >= 9;
        });

        List<String> firstProductCategories = visibleProductCategories.get().subList(0, 9);

        int spreadForecast_RowIndex = firstProductCategories.indexOf("Spread Forecast");
        int consumedForecast_RowIndex = firstProductCategories.indexOf("Consumed Forecast");
        int unconsumedForecast_RowIndex = firstProductCategories.indexOf("Unconsumed Forecast");
        int forecastConsumptionPercentage_RowIndex = firstProductCategories.indexOf("Forecast Consumption %");
        if(spreadForecast_RowIndex < 0 || consumedForecast_RowIndex < 0 || unconsumedForecast_RowIndex < 0 || forecastConsumptionPercentage_RowIndex < 0)
        {
            throw new NotFoundException("Column with forecast consumption is not found or does not contain correct values.");
        }
        WaitFor.condition(() -> !tc.table.getColumnItems(ETable.byIndex(0), EColumn.getColumnName_DateCalendarWeek(4)).isEmpty());
        List<String> weekColumn = tc.table.getColumnItems(ETable.byIndex(0), EColumn.getColumnName_DateCalendarWeek(4));
        int spreadForecast = NumberUtils.toInt(weekColumn.get(spreadForecast_RowIndex));
        int consumedForecast = NumberUtils.toInt(weekColumn.get(consumedForecast_RowIndex));
        int unconsumedForecast = NumberUtils.toInt(weekColumn.get(unconsumedForecast_RowIndex));
        int forecastConsumptionPercentage = NumberUtils.toInt(weekColumn.get(forecastConsumptionPercentage_RowIndex));
        tc.stepEvaluator.add(spreadForecast, consumedForecast + unconsumedForecast, "Sum of consumed and unconsumed forecast should be equal to " +
                "spread forecast.");
        tc.stepEvaluator.add(forecastConsumptionPercentage,
                Math.abs(spreadForecast) < 0.001 ? 0 : Math.round(100 * consumedForecast / (spreadForecast)),
                "Forecast " +
                        "percentage " +
                        "consumption is incorrect.");
    }

    private OptionalInt toInt(String value)
    {
        try
        {
            return OptionalInt.of(Integer.parseInt(value.replaceAll(",", "")));
        }
        catch(NumberFormatException numberFormatException)
        {
            return OptionalInt.empty();
        }
    }

    List<String> readColumn(InVivoHandler tc, String columnName, int firstNRows)
    {
        tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byCustomValue(columnName));
        List<String> columnItems = tc.table.getColumnItems(ETable.byIndex(0), EColumn.byCustomValue(columnName));
        if(columnItems.size() < firstNRows)
        {
            return columnItems;
        }

        return columnItems.subList(0, firstNRows);
    }

    Map<String, List<String>> readSubTable(InVivoHandler tc, int firstNRows, List<String> columnsNames)
    {
        //check column by column -> faster, you will scroll view for whole table only once

        if(!WaitFor.condition(() -> !readColumn(tc, columnsNames.get(0), firstNRows).isEmpty()))
        {
            TcLog.error("First column '%s' cannot be read (missing data or impl problem)".formatted(columnsNames.get(0)));

            return columnsNames.stream()
                    .collect(Collectors.toMap(
                            columnName -> columnName,
                            columnName -> new ArrayList<>()
                    ));
        }

        Map<String, List<String>> subTable = columnsNames.stream()
                .collect(Collectors.toMap(
                        columnName -> columnName,
                        columnName -> readColumn(tc, columnName, firstNRows)
                ));

        TcLog.info("Found values in table, [columnName,value] \n %s"
                .formatted(formatValues(subTable)));

        //reset view
        tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byIndex(5), true);

        return subTable;
    }

    boolean isSubTableEmpty(Map<String, List<String>> subTable)
    {
        AtomicBoolean isEmpty = new AtomicBoolean(true);
        Optional<Map.Entry<String, List<String>>> nonEmptyColumn = subTable.entrySet()
                .stream()
                .filter(stringListEntry -> !stringListEntry.getValue().isEmpty())
                .findFirst();

        return nonEmptyColumn.isEmpty();
    }

    private StringBuilder formatValues(Map<String, List<String>> subTable)
    {
        StringBuilder builder = new StringBuilder();
        subTable.forEach((key, value) -> builder.append("[%s,%s] "
                .formatted(key, value.toString())));
        return builder;
    }

    List<String> findNonPositiveNonNumber_ColumnValues(List<String> columnValues)
    {
        List<String> nonNumberNonPositive = columnValues.stream()
                .filter(value -> toInt(value).isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

        nonNumberNonPositive.addAll(
                columnValues.stream()
                        .filter(value -> toInt(value).isPresent() && toInt(value).getAsInt() < 0)
                        .toList()
        );

        return nonNumberNonPositive;
    }

    private void findNonNumbers(InVivoHandler tc, Map<String, List<String>> subTable)
    {

        subTable.forEach(
                (key, values) ->
                {
                    List<String> nonNumbers = values.stream()
                            .filter(value -> toInt(value).isEmpty())
                            .toList();
                    tc.stepEvaluator.add(nonNumbers::isEmpty,
                            "Values in '%s' are not numbers.".formatted(nonNumbers));
                }
        );
    }
}


