package TestAutomation_1;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import ControlImplementations.ButtonControl.ButtonControl;
import Enums.*;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TC_ID_2436
{

    @Test
    void TC_RS_1922_Ability_to_visualize_in_a_waterfall_the_Demand_plan()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION, "2436", tc -> {
            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //----- STEP 2 -----
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS Demand Waterfalls";
            tc.menu.resource.filterResources(workBook, true);
            tc.addStepInfoWithScreenShot("Workbook '%s' is opened.".formatted(workBook), workBook, tc.workBook.getSelected());

            //----- STEP 3 -----
            String scenario = "S&OP Candidate";
            String customer = "909", forecastStream = "BusinessLineAT", part = "= All =", asOfDate = "= All =";
            tc.workBook.menu.openDataSettings();
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "1080"),
                    Map.entry(EComboBox.CUSTOMER, customer),
                    Map.entry(EComboBox.FORECAST_STREAM, forecastStream),
                    Map.entry(EComboBox.PART, part),
                    Map.entry(EComboBox.BASELINE_AS_OF_DATE, asOfDate)
            ));

            //KUMAR WANT TO CHOOSE = ALL = FOR BASELINE_AS_OF_DATE for now
/*            List<String> asOfDateOptions = tc.combo.getAvailableOptions(EComboBox.BASELINE_AS_OF_DATE);
            //choose the oldes as of date because, only then all asofdates are shown in table for one part ( as is shown in picture)
            int asOfDateIndex = 3;
            String asOfDate = asOfDateOptions.get(asOfDateIndex);
            int asOfDateRowsCount = asOfDateOptions.size() - asOfDateIndex + 1;
            tc.combo.select(EComboBox.BASELINE_AS_OF_DATE, asOfDate);*/
            tc.button.click(EButton.DATA_SETTINGS);
            tc.stepEvaluator
                    .add(customer, tc.button.getName(EButton.CUSTOMER), "Incorrect value for customer.")
                    .add(forecastStream, tc.button.getName(EButton.FORECAST_STREAM), "Incorrect value for forecastStream.")
                    .add(part, tc.button.getName(EButton.SELECTED_PART), "Incorrect value for selected part.")
                    .add(asOfDate, tc.button.getName(EButton.SELECTED_AS_OF_DATE), "Incorrect value for selected part.");
            tc.addStepInfoWithScreenShot("Workbook '%s' shows data based on selection.".formatted(workBook),
                    "ok",
                    tc.stepEvaluator.eval());

            //----- STEP 4 -----
            tc.stepEvaluator.reset();
            int asOfDateRowsCount = 2;
            var subTable = readSubTable_AsOfDateRows_ForTheFuture12days(tc, asOfDateRowsCount);
            var incorrectValues = findIncorrectValues(subTable);
            StringBuilder builder = formatIncorrectValues(incorrectValues);
            Optional<Integer> firstFoundRow = incorrectValues.keySet().stream().findFirst();
            tc.stepEvaluator.add(() -> incorrectValues.size() == 0, ("In table are incorrect values, not present or not numeric values. %s For other" +
                    " values see full log.")
                    .formatted(formatIncorrectValues(firstFoundRow.isPresent() ?
                            Map.of(firstFoundRow.get(), incorrectValues.get(firstFoundRow.get())) :
                            Map.of())));
            tc.addStepInfoWithScreenShot("The quantities are visible for various dates as on the data " +
                            "selected (AS OF Date). ",
                    "ok",
                    tc.stepEvaluator.eval());
            if(incorrectValues.size() > 0)
            {
                TcLog.error("Incorrect values in table: \n %s.".formatted(formatIncorrectValues(incorrectValues)));
            }
        });
    }

    /**
     * check last 12 columns in table
     * filter outside because i want to see if the data are correctly loaded
     *
     * @param tc                tc handler
     * @param asOfDateRowsCount number of expected rows
     * @return column name and first asOfDateRowsCount values in this column
     */
    private List<Pair<String, String[]>> readSubTable_AsOfDateRows_ForTheFuture12days(InVivoHandler tc, int asOfDateRowsCount)
    {
        List<String> columnNames = tc.table.getColumnNames(ETable.byIndex(0));

        List<String> future12DaysColumns = columnNames
                .stream()
                .skip(11) // in this way we check first columns in last 12 days
                .toList();

        if(future12DaysColumns.isEmpty())
        {
            TcLog.error("Index not found." + Thread.currentThread().getStackTrace().toString());
        }

        //check column by column -> faster, you will scroll view for whole table only once

        List<Pair<String, String[]>> searchedSubtable = future12DaysColumns.stream()

                .map(
                        columnName ->
                        {
                            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byCustomValue(columnName));

                            String[] columnItems = tc.table.getColumnItems(ETable.byIndex(0), EColumn.byCustomValue(columnName))
                                    .subList(0, asOfDateRowsCount)
                                    .toArray(String[]::new);
                            return Pair.create(columnName.substring(23), columnItems);
                        })

                .filter(pair -> pair.getValue().length > 0 && !pair.getValue()[0].isEmpty())
                .toList();

        TcLog.info("Found values in table, [columnName,value] \n %s"
                .formatted(formatValues(searchedSubtable)));

        //reset view
        tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byIndex(5), true);

        return searchedSubtable;
    }

    Map<Integer, List<Pair<String, String>>> findIncorrectValues(List<Pair<String, String[]>> subTable)
    {
        return subTable.stream()
                .flatMap(
                        stringPair ->
                                IntStream.range(0, stringPair.getSecond().length)
                                        .mapToObj(index -> Triple.of(index, stringPair.getFirst(), stringPair.getSecond()[index]))
                                        .filter(triple -> toInt(triple.getRight()).isEmpty())
                )
                .collect(Collectors.groupingBy(Triple::getLeft,
                        Collectors.mapping(triple -> Pair.create(triple.getMiddle(), triple.getRight()), Collectors.toList())
                ));
    }

    StringBuilder formatIncorrectValues(Map<Integer, List<Pair<String, String>>> incorrectValues)
    {
        StringBuilder builder = new StringBuilder();
        incorrectValues.forEach(
                (integer, pairs) ->
                {
                    builder.append("row No. %d \n".formatted(integer + 1));
                    pairs.forEach(
                            stringStringPair -> builder.append("[%s,%s] "
                                    .formatted(stringStringPair.getFirst(), stringStringPair.getSecond())));
                    builder.append("\n");
                }
        );

        return builder;
    }

    StringBuilder formatValues(List<Pair<String, String[]>> values)
    {
        StringBuilder builder = new StringBuilder();
        values.forEach(
                stringStringPair -> builder.append("[%s,%s] "
                        .formatted(stringStringPair.getFirst(), Arrays.toString(stringStringPair.getSecond()))));
        return builder;
    }

    private OptionalInt toInt(String value)
    {
        try
        {
            return OptionalInt.of(Integer.parseInt(value));
        }
        catch(NumberFormatException numberFormatException)
        {
            return OptionalInt.empty();
        }
    }
}



