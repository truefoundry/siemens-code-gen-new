package TestAutomation_1;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.Generator;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.EType;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TC_ID_2426
{

    @Test
    void TC_RS_1888_As_a_Demand_Planner_All()
    {

        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION, "2426", tc -> {
            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            //tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //----- STEP 2 -----
            tc.menu.openMenu(EActivity.SCENARIOS);
            String privateScenario = tc.menu.scenarios.create("S&OP Candidate");
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS S&OP Forecast Disaggregation";
            tc.menu.resource.filterResources(workBook, true);
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, privateScenario),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "1080")
            ));

            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.button.getName(EButton.SCENARIO_FILTER_ITEM).equalsIgnoreCase(privateScenario));
            tc.stepEvaluator.add(workBook, tc.workBook.getSelected(), "Workbook not found or incorrect.");
            tc.stepEvaluator.add(privateScenario, tc.button.getName(EButton.SCENARIO_FILTER_ITEM), "Scenario not found or incorrect.");
            tc.addStepInfoWithScreenShot("Workbook '%s' is opened with private scenario '%s'".formatted(workBook, privateScenario), "ok", tc.stepEvaluator.eval());

            //----- STEP 3 -----
            List<String> forecastCategories = List.of("1. Spare Parts CS", "2. Forecast Adjustments", "3. Dependent Dmd From SAP", "4. Statistical");
            String forecastAdjustments = forecastCategories.get(1);
            if(!tc.button.isPressed(EButton.DATA_SETTINGS)) tc.button.click(EButton.DATA_SETTINGS);
            tc.combo.select(EComboBox.FORECAST_CATEGORY, forecastAdjustments);
            String item = tc.combo.getSelected(EComboBox.FORECAST_CATEGORY);
            tc.addStepInfoWithScreenShot("Able to select one of the option ('%s')from the list. Current value is: '%s'".formatted(forecastAdjustments, item),
                    true, item.contains(forecastAdjustments));

            //----- STEP 4 -----
            tc.hierarchy.openHierarchyPanel();
            //some users (QATest07) have different default hierarchy as user (QATest01),
            // and we need product hierarchy (screenshot in tc)
            tc.hierarchy.removeAll();
            //originally EHierarchy.PRODUCT but amount of values exceeded 10000 -> cannot be loaded
            EHierarchy hierarchy = EHierarchy.SHS_SITE;
            tc.hierarchy.add(hierarchy);
            String hierarchyItem = "All";
            //workaround, for taking screenshot
            String hashSuffix = Generator.getHashedName("", true);
            String filterName = "tc2426_%s".formatted(hashSuffix);
            this.createFilter(tc, hierarchy, hierarchyItem, "SHS Site Site", filterName);
            WaitFor.condition(() -> tc.button.getName(EButton.FILTER).equalsIgnoreCase(filterName), Duration.ofSeconds(10));
            tc.addStepInfo("Able to create a private part filter", filterName, tc.button.getName(EButton.FILTER),
                    new ComparerOptions().takeScreenShotPlatform());

            //----- STEP 5 -----
            tc.stepEvaluator.reset();
            String nextMonth = EColumn.getColumnNameForCurrentMonth("MMM-yy", 1).getValue();
            String nextNextMonth = EColumn.getColumnNameForCurrentMonth("MMM-yy", 2).getValue();
            int thirdRow = 2, sixthRow = 5;
            tc.button.click(EButton.DATA_SETTINGS);
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byCustomValue(nextMonth));
            tc.stepEvaluator.add(true, tc.table.isCellValueEditable(ETable.byIndex(0),
                            thirdRow,
                            EColumn.byCustomValue(nextMonth),
                            "61.0%"),
                    "Value in row '%s' and column '%s' is not editable.".formatted(thirdRow, nextMonth));

            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.byCustomValue(nextNextMonth));
            tc.stepEvaluator.add(true, tc.table.isCellValueEditable(ETable.byIndex(0),
                            thirdRow,
                            EColumn.byCustomValue(nextNextMonth),
                            "66.0%"),
                    "Value in row '%s' and column '%s' is not editable.".formatted(sixthRow, nextNextMonth));

            tc.addStepInfoWithScreenShot("Able to edit using the override line", "ok", tc.stepEvaluator.eval());
        });
    }

    private boolean createFilter(InVivoHandler tc, EHierarchy hierarchy, String hierarchyItem, String checkedColumnName, String filterName)
    {
        BrowserControl.waitForLoadingIndicator();
        AtomicReference<Boolean> hierarchyExists = new AtomicReference<>(false);
        WaitFor.condition(() ->
                {
                    hierarchyExists.set(tc.list.exists(hierarchyItem));
                    return hierarchyExists.get();
                }
        );

        if(!hierarchyExists.get())
        {
            TcLog.error("Product hierarchy '%s' is not found.".formatted(hierarchyItem));
            return false;
        }

        TcLog.info("START CREATING FILTER");
        tc.list.expand(hierarchyItem);
        tc.hierarchy.selectItem(hierarchy, hierarchyItem);
        WaitFor.specificTime(Duration.ofSeconds(5));//Added because cancel loader takes time to appear
        BrowserControl.waitForLoadingIndicator();
        tc.table.performActionOnItemsInCommonView(ETable.byIndex(0),
                Pair.create(EColumn.byCustomValue(checkedColumnName), "#2"),
                Pair.create(EColumn.byCustomValue(checkedColumnName), "#6"),
                EAction.COPY_TO_FILTER);
        tc.edit.sendKeys(EEdit.byCustomValue("name", EType.NAME), filterName);
        tc.button.click(EButton.CREATE);
        TcLog.info("END CREATING FILTER");

        return true;
    }
}


