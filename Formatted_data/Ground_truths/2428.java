package TestAutomation_1;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class TC_ID_2428
{
    @Test
    void TC_RS_1891_As_a_Global_Regional_Demand_Planner()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION, "2428", tc -> {
            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //----- STEP 2 -----
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS S&OP Consensus Demand Planning";
            String scenario = "S&OP Candidate";
            tc.menu.resource.filterResources(workBook, true);
            WaitFor.condition(tc.modal::exists);
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.VIEW, "Units"),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites")
            ));
            tc.button.click(EButton.OPEN);

            WaitFor.condition(() -> tc.button.exists(EButton.SCENARIO_FILTER_ITEM));
            tc.stepEvaluator.add(workBook, tc.workBook.getSelected(), "Workbook not found or incorrect.");
            tc.stepEvaluator.add(scenario, tc.button.getName(EButton.SCENARIO_FILTER_ITEM), "Scenario not found or incorrect.");
            tc.addStepInfoWithScreenShot("Workbook '%s' is opened with used scenario '%s'".formatted(workBook, scenario), "ok", tc.stepEvaluator.eval());

            //----- STEP 3 -----
            //CLEAR HIERARCHIES OTHERWISE 0. COLUMN CANNOT BE FOUND (THERE IS NO IMPL FOR IT)
            //WITH HIERARCHY IS ADDED NEW COLUMN WHICH AFFECTS THE WAY OF INDEXING
            tc.stepEvaluator.reset();
            tc.hierarchy.openHierarchyPanel();
            tc.hierarchy.removeAll();
            tc.button.click(EButton.DATA_SETTINGS);
            String businessLineAT = "Business Line (AT)";
            String spareParts = "Spare Parts (CS)";
            List<String> expectedForecastCategories =
                    List.of(businessLineAT, "Dmd From SAP", spareParts, "Business Line (CT)",
                            "Business Line (XP)", "Business Line (MR)");

            List<Pair<String, String>> futureForecastsSpareParts = getFutureForecasts(tc, "Statistical")
                    .stream()
                    .filter(stringStringPair -> stringStringPair.getSecond().isEmpty())
                    .toList();
            tc.stepEvaluator.add(futureForecastsSpareParts::isEmpty
                    , "Future forecasts for '%s' are not present. Missing forecasts for columns: '%s'".formatted(spareParts, futureForecastsSpareParts));

            tc.addStepInfo("Forecast exists.", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //----- STEP 4 -----
            tc.stepEvaluator.reset();
            BiFunction<List<String>, List<String>, List<String>> findAllCategoriesNotPresent = (expected, current) ->
                    expected.stream()
                            .filter(expectedItem -> !current.contains(expectedItem))
                            .toList();

            List<String> firstColumn = tc.table.getAllColumnItems(ETable.byIndex(0), EColumn.byIndex(0));
            tc.stepEvaluator.add(() -> findAllCategoriesNotPresent.apply(expectedForecastCategories, firstColumn).isEmpty(),
                    "These categories are not present: '%s'".formatted(findAllCategoriesNotPresent.apply(expectedForecastCategories, firstColumn)));

            String wantedRow = "Unconstrained Demand Plan";
            //           tc.stepEvaluator.add(()->tc.table.getItemValueFromColumn(ETable.byIndex(0),EColumn.byIndex(0),wantedRow,)
            List<Triple<String, Boolean, Pair<Integer, Integer>>> checkDemandPlanRow = checkNumbersForUnconstrainedDemandPlan(tc, wantedRow, expectedForecastCategories);
            tc.stepEvaluator.add(() -> checkDemandPlanRow
                            .stream()
                            .filter(stringBooleanIntegerTriple -> !stringBooleanIntegerTriple.getMiddle())
                            .toList()
                            .isEmpty()
                    ,
                    "The '%s' does not show up the correct number/s. Incorrect values in columns: '%s' "
                            .formatted(wantedRow, checkDemandPlanRow
                                    .stream()
                                    .filter(stringBooleanIntegerTriple -> !stringBooleanIntegerTriple.getMiddle())
                                    .toList()
                            ));

            tc.addStepInfo("Forecast categories '%s' are shown and & The '%s' shows up the correct number ".formatted(expectedForecastCategories,
                            wantedRow),
                    "ok",
                    tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }

    private List<Pair<String, String>> getFutureForecasts(InVivoHandler tc, final String forecastCatogerieLineName)
    {
        List<String> columnNames = tc.table.getColumnNames(ETable.byIndex(0));
        List<String> futureForecastsColumns = columnNames.subList(columnNames.indexOf(getNewestPastForecastColumnName()), columnNames.size() - 1);
        List<Pair<String, String>> futureForecastsValues = futureForecastsColumns.stream()
                .map(
                        futureForecast ->
                        {
                            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byCustomValue(futureForecast));
                            return Pair.create(futureForecast,
                                    tc.table.getItemValueFromColumn(
                                            ETable.byIndex(0),
                                            EColumn.byIndex(0),
                                            forecastCatogerieLineName,
                                            EColumn.byCustomValue(futureForecast)
                                    )
                            );
                        }
                )
                .toList();

        //set view to default
        tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byIndex(1), true);
        return futureForecastsValues;
    }

    public String getNewestPastForecastColumnName()
    {
        String shortcutMonth = LocalDate.now().getMonth().name().substring(0, 3);
        return new StringBuilder()
                .append(shortcutMonth.substring(0, 1))
                .append(shortcutMonth.substring(1, 3).toLowerCase())
                .append("-")
                .append(LocalDate.now().getYear() - 2000)
                .toString();
    }

    private Optional<Integer> getSumInColumn(InVivoHandler tc, List<String> forecastCategories, String columnDateName)
    {
        return forecastCategories.stream().map(forecast ->
                toInt(tc.table.getItemValueFromColumn(
                        ETable.byIndex(0),
                        EColumn.byIndex(0),
                        forecast,
                        EColumn.byCustomValue(columnDateName))

                )).reduce(Integer::sum);
    }

    List<Triple<String, Boolean, Pair<Integer, Integer>>> checkNumbersForUnconstrainedDemandPlan(InVivoHandler tc, String demandPlanner,
                                                                                                 List<String> forecastCategoriesForSummation)
    {
        List<String> columnNames = tc.table.getColumnNames(ETable.byIndex(0));
        OptionalInt futureMonthIndex = IntStream.range(0, columnNames.size())
                .filter(index -> columnNames.get(index).equalsIgnoreCase(EColumn.getColumnNameForCurrentMonth("MMM-yy", 1).getValue()))
                .findFirst();

        if(futureMonthIndex.isEmpty())
        {
            TcLog.error("Index not found." + Arrays.toString(Thread.currentThread().getStackTrace()));
            return Collections.emptyList();
        }

        List<String> futureMonths = columnNames
                .stream()
                .skip(futureMonthIndex.getAsInt())
                .toList();

        //IS NOT NECESSARY NOW, BECAUSE we are checking now only future not past forecasts
        /*//first time move to the left, the second time to the right, columns should be sorted
        tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byIndex(1), true);
*/
        return futureMonths.stream()
                .map(
                        columnName ->
                        {
                            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byCustomValue(columnName));
                            Optional<Integer> sum = getSumInColumn(tc, forecastCategoriesForSummation, columnName);
                            if(sum.isEmpty())
                            {
                                return Triple.of(columnName, false, Pair.create(0, 0));
                            }

                            int valueInDemandPlanner = toInt(tc.table.getItemValueFromColumn(
                                    ETable.byIndex(0),
                                    EColumn.byIndex(0),
                                    demandPlanner,
                                    EColumn.byCustomValue(columnName)));
                            int calculatedDemandPlan = Math.round(sum.get());
                            int adjustments = toInt(tc.table.getItemValueFromColumn(
                                    ETable.byIndex(0),
                                    EColumn.byIndex(0),
                                    "Adjustments",
                                    EColumn.byCustomValue(columnName)));
                            //adjustments has weight 1 always
                            calculatedDemandPlan += adjustments;
                            return Triple.of(columnName, equalWithToleranceOne(valueInDemandPlanner, calculatedDemandPlan),
                                    Pair.create(calculatedDemandPlan,
                                            valueInDemandPlanner));
                        })
                .toList();
    }

    private int toInt(String value)
    {
        return NumberUtils.toInt(value.replaceAll(",", ""));
    }

    boolean equalWithToleranceOne(int first, int second)
    {
        return Math.abs(first - second) <= 1;
    }
}


