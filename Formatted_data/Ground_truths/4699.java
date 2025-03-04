package TestAutomation_3;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.CompositionRoot.AbstractCoreHandler;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.ControlImplementations.Generator;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_4699 {

    @Test
    void TC_RS_Validate_and_adjust_sales_history_for_outliers()
    {
        IocBuilder.execute(Duration.ofMinutes(16), EResultData.TEST_AUTOMATION_3, "4699", tc -> {
            // STEP 1
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            // STEP 2
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS S&OP Forecast Item Management";
            String workSheet = "Define Forecast Item Level";
            String scenarioParent = "S&OP Candidate";
            String part = "10012719";
            tc.menu.resource.filterResources(workBook, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.addStepInfoWithScreenShot("'%s' workbook opens.".formatted(workBook), workBook, tc.workBook.getSelected());

            // STEP 3
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.SCENARIO, scenarioParent),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites")
            ));
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.DATA_SETTINGS));
            tc.stepEvaluator.add(scenarioParent, tc.button.getName(EButton.SCENARIO_FILTER_ITEM), "Scenario not found or incorrect.")
                    .add(workSheet, tc.tab.getSelected(), "Worksheet not found or incorrect");
            tc.addStepInfoWithScreenShot("Able to use the data settings", "ok", tc.stepEvaluator.eval());

            // STEP 4
            tc.button.click(EButton.SCENARIO_FILTER_ITEM);
            tc.button.click(EButton.CREATE_SCENARIO);
            String scenario = Generator.getHashedName("Scenario_", true);
            WaitFor.condition(() -> tc.modal.exists(EModal.NEW_SCENARIO), Duration.ofSeconds(5));
            tc.edit.sendKeys(EEdit.NEW_SCENARIO_NAME, scenario);
            tc.button.click(EButton.CREATE);
            WaitFor.condition(() -> !tc.modal.exists(EModal.NEW_SCENARIO));
            tc.button.closeDropdown();
            CoreIocBuilder.getContainer().getComponent(AbstractCoreHandler.class).addToGarbageCollector(() -> tc.menu.scenarios.delete(scenario));
            tc.workBook.menu.openDataSettings();
            tc.picker.removeAll(EPicker.SCENARIOS, scenario);
            WaitFor.condition(() -> tc.button.getName(EButton.SCENARIO_FILTER_ITEM).equalsIgnoreCase(scenario));
            tc.addStepInfoWithScreenShot("Able to view created private scenario", scenario,
                    tc.button.getName(EButton.SCENARIO_FILTER_ITEM));

            // STEP 5
            tc.hierarchy.openHierarchyPanel();
            tc.hierarchy.removeAll();
            tc.addStepInfoWithScreenShot("Able to view hierarchy options", true, tc.button.exists(EButton.ADD_HIERARCHY));

            // STEP 6
            tc.hierarchy.add(EHierarchy.SHS_PRODUCT);
            tc.list.expand("Active");
            tc.hierarchy.handleToolargePopup();
            tc.addStepInfoWithScreenShot("Able to view SHS Product options", true, !tc.list.getAll().isEmpty());

            // STEP 7
            tc.hierarchy.remove(EHierarchy.SHS_PRODUCT);
            tc.tab.select(ETab.FORECAST_ITEM_CONFIGURATION);
            tc.addStepInfoWithScreenShot("Records are visible", true,
                    !tc.table.getColumnItems(ETable.byIndex(0), EColumn.FORECAST_ITEM).isEmpty());

            // STEP 8
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.OUTLIER);
            tc.addStepInfoWithScreenShot("Outlier venue is visible.", true,
                    tc.table.isColumnDisplayed(ETable.byIndex(0), EColumn.OUTLIER_TYPE));

            // STEP 9
            tc.stepEvaluator.reset();
            tc.table.clickIconInsideCell(ETable.byIndex(0), EColumn.byIndex(0), "#0", EColumn.OUTLIER, ETableCellIcon.VIEW_OUTLIERS);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.DATA_SETTINGS));
            tc.stepEvaluator.add(() -> tc.workBook.getSelected().equalsIgnoreCase("SHS S&OP Data Cleansing"), "Different workbook is opened")
                    .add(() -> tc.tab.getSelected().equalsIgnoreCase("Outliers"), "Outliers worksheet is not opened.");
            tc.addStepInfoWithScreenShot("Outliers worksheet opens.", "ok", tc.stepEvaluator.eval());

            // STEP 10
            tc.hierarchy.openHierarchyPanel();
            tc.hierarchy.add(EHierarchy.PRODUCT);
            tc.list.expand("<blank>");
            tc.hierarchy.handleToolargePopup();
            WaitFor.condition(() -> tc.modal.exists(EModal.LOAD_THESE_HIERARCHIES), Duration.ofSeconds(5));
            tc.list.fastScrollToItem(part);
            tc.list.select(part);
            tc.table.clickIconInsideCell(ETable.byIndex(0), EColumn.byIndex(0), "#0", EColumn.byIndex(6), ETableCellIcon.SHOW_CHART);
            WaitFor.condition(() -> tc.tab.exists(ETab.OUTLIERS_CHART));
            tc.addStepInfoWithScreenShot("The outliers chart is visible", true,
                    tc.tab.getSelected().equalsIgnoreCase("Outliers Chart"));

            // STEP 11
            tc.stepEvaluator.reset();
            BufferedImage img1 = tc.chart.getImage(EChart.byIndex(0));
            String val = tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(0), "#0", EColumn.byIndex(13), "10");
            tc.button.click(EButton.SAVE);
            WaitFor.specificTime(Duration.ofSeconds(1));
            BufferedImage img2 = tc.chart.getImage(EChart.byIndex(0));
            boolean result = tc.chart.compareChartImages(img1, img2);
            tc.table.setCellValue(ETable.byIndex(0), EColumn.byIndex(0), "#0", EColumn.byIndex(13), "0");
            tc.button.click(EButton.SAVE);
            tc.stepEvaluator.add(true, !val.isEmpty(), "Value update is not possible")
                    .add("false", result, "Changes are not visible in the 'Outlier Chart' worksheet");
            tc.addStepInfoWithScreenShot("Value update is possible and changes are visible in the 'Outlier Chart' worksheet.",
                    "ok", tc.stepEvaluator.eval());
        });
    }
}


