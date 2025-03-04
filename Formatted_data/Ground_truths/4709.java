package TestAutomation_3;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.ControlImplementations.Generator;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_4709
{

    @Test
    void Adjust_the_parameters_of_the_statistical_forecast_item()
    {

        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION_3, "4709", tc ->
        {
            //STEP 1
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfoWithScreenShot("Rapid Response window will open.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //STEP 2
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS S&OP Forecast Item Management";
            String workSheet = "Define Forecast Item Level";
            tc.menu.resource.filterResources(workBook, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "All Sites")
            ));
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.hierarchy.add(EHierarchy.PRODUCT);
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.DATA_SETTINGS));
            tc.addStepInfoWithScreenShot("workbook ‘SHS S&OP Forecast Item Management is visible" , workBook,
                    tc.workBook.getSelected());

            //STEP 3
            tc.button.click(EButton.WORKBOOK_COMMANDS);
            tc.workBook.menu.select(EWbMItem.WORKBOOK_COMMANDS_STATISTICAL_FORECAST_SETUP);
            WaitFor.condition(() -> tc.button.exists(EButton.RUN));
            tc.button.click(EButton.RUN);
            WaitFor.condition(() -> tc.modal.exists(EModal.STATISTICAL_FORECAST_SETUP_CONFIRM));
            tc.button.click(tc.button.exists(EButton.CONFIRM) ? EButton.CONFIRM : EButton.DONE);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfoWithScreenShot("statistical forecast run the command ‘Statistical Forecast Setup’ is " +
                    "proceeded", true, tc.table.getColumnNames(ETable.byIndex(0)).contains("Part Name"));

            //STEP 4
            tc.tab.select(ETab.FORECAST_ITEM_CONFIGURATION);
            tc.table.exists(ETable.byIndex(0));
            List<String> listItems = tc.table.getColumnItems(ETable.byIndex(0), EColumn.FORECAST_CATEGORY);
            tc.stepEvaluator
                    .add(() -> !listItems.isEmpty(), "Column items are empty")
                    .add(() -> listItems.stream().allMatch(str -> str.equals("Statistical")), "Column Items" +
                            " does not belong to Statistical Forecast");
            tc.addStepInfoWithScreenShot("newly created forecast item is visible and correct","ok",
                    tc.stepEvaluator.eval());

            //STEP 5
            tc.stepEvaluator.reset();
            String startDate = "02-02-24";
            String stopDate = "02-02-27";
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.OVERRIDE_FORECAST_STOP);
            tc.table.performActionOnColumn(ETable.byIndex(0), EColumn.OVERRIDE_FORECAST_START, EAction.EDIT_RANGE);
            tc.combo.select(EComboBox.OPERATION, "Change to");
            tc.edit.sendKeys(EEdit.VALUE, startDate);
            tc.checkbox.check(ECheckBox.APPLY_TO_ENTIRE_COLUMN);
            tc.button.click(EButton.APPLY);
            tc.table.performActionOnColumn(ETable.byIndex(0), EColumn.OVERRIDE_FORECAST_STOP, EAction.EDIT_RANGE);
            tc.combo.select(EComboBox.OPERATION, "Change to");
            tc.edit.sendKeys(EEdit.VALUE, stopDate);
            tc.checkbox.check(ECheckBox.APPLY_TO_ENTIRE_COLUMN);
            tc.button.click(EButton.APPLY);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.SAVE));
            tc.button.click(EButton.SAVE);
            BrowserControl.waitForLoadingIndicator();
            List<String> startDates = tc.table.getColumnItems(ETable.byIndex(0), EColumn.OVERRIDE_FORECAST_START);
            List<String> stopDates = tc.table.getColumnItems(ETable.byIndex(0), EColumn.OVERRIDE_FORECAST_STOP);
            tc.stepEvaluator
                    .add(() -> !startDates.isEmpty(), "No data present in Forecast Start")
                    .add(() -> startDates.stream().allMatch(str -> str.equals(startDate)), "Data was" +
                            " not modified in Forecast Start")
                    .add(() -> !stopDates.isEmpty(), "No data present in Forecast Stop")
                    .add(() -> stopDates.stream().allMatch(str -> str.equals(stopDate)), "Data was" +
                            " not modified in Forecast Stop");
            tc.addStepInfoWithScreenShot("modified forecast start and stop dates are visible", "ok",tc.stepEvaluator.eval());


            //STEP 6
            String countValue = "50";
            tc.stepEvaluator.reset();
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.INTERVALS_HISTORICAL, true);
            tc.table.performActionOnColumn(ETable.byIndex(0), EColumn.INTERVALS_HISTORICAL, EAction.EDIT_RANGE);
            tc.combo.select(EComboBox.OPERATION, "Change to");
            tc.edit.sendKeys(EEdit.VALUE, countValue);
            tc.checkbox.check(ECheckBox.APPLY_TO_ENTIRE_COLUMN);
            tc.button.click(EButton.APPLY);
            tc.table.performActionOnColumn(ETable.byIndex(0), EColumn.INTERVALS_FORECAST, EAction.EDIT_RANGE);
            tc.combo.select(EComboBox.OPERATION, "Change to");
            tc.edit.sendKeys(EEdit.VALUE, countValue);
            tc.checkbox.check(ECheckBox.APPLY_TO_ENTIRE_COLUMN);
            tc.button.click(EButton.APPLY);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.SAVE));
            tc.button.click(EButton.SAVE);
            BrowserControl.waitForLoadingIndicator();
            List<String> historicalItems = tc.table.getColumnItems(ETable.byIndex(0), EColumn.INTERVALS_HISTORICAL);
            List<String> forecastItems = tc.table.getColumnItems(ETable.byIndex(0), EColumn.INTERVALS_FORECAST);
            tc.stepEvaluator
                    .add(() -> !historicalItems.isEmpty(), "No data present in Historical Intervals")
                    .add(() -> historicalItems.stream().allMatch(str -> str.equals(countValue)), "Data was" +
                            " not modified in Historical Intervals")
                    .add(() -> !forecastItems.isEmpty(), "No data present in Forecast Intervals")
                    .add(() -> forecastItems.stream().allMatch(str -> str.equals(countValue)), "Data was" +
                            " not modified in Forecast Intervals");
            tc.addStepInfoWithScreenShot("modified historical and forecast interval count are visible", "ok",tc.stepEvaluator.eval());

            //STEP 7
            tc.stepEvaluator.reset();
            String modelSetName = Generator.getHashedName("Model_Set_", true);
            tc.tab.select(ETab.MODELS_SETS_AND_METHOD);
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)));
            tc.button.click(EButton.INSERT_RECORD);
            WaitFor.condition(tc.modal::exists);
            tc.frame.switchToFrame(1);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.OK));
            tc.edit.sendKeys(EEdit.MODEL_SET, modelSetName, true);
            tc.edit.sendKeys(EEdit.METHOD, "DoubleExponentialSmoothing", true);
            tc.combo.select(EComboBox.FORECAST_MODEL, "DoubleExponentialSmoothing");
            tc.button.click(EButton.OK);
            tc.frame.switchToDefault();
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.DATA_SETTINGS));
            tc.addStepInfoWithScreenShot("New records are visible", true, tc.table.getColumnItems(ETable.byIndex(0), EColumn.MODEL_SET).contains(modelSetName));

            // STEP 8
            tc.tab.select(ETab.FORECAST_ITEM_CONFIGURATION);
            tc.table.scrollToColumn(ETable.byIndex(0), EColumn.BEST_FIT_MODEL_SET);
            tc.table.performActionOnColumn(ETable.byIndex(0), EColumn.BEST_FIT_MODEL_SET, EAction.EDIT_RANGE);
            tc.combo.select(EComboBox.OPERATION, "Change to");
            tc.combo.select(EComboBox.VALUE, modelSetName);
            tc.checkbox.check(ECheckBox.APPLY_TO_ENTIRE_COLUMN);
            tc.button.click(EButton.APPLY);
            tc.button.click(EButton.SAVE);
            List<String> modelSetNames = tc.table.getColumnItems(ETable.byIndex(0), EColumn.BEST_FIT_MODEL_SET);
            tc.addStepInfoWithScreenShot("Newly created model set is visible", true, modelSetNames.stream().allMatch(str -> str.equals(modelSetName)));

        });

    }
}


