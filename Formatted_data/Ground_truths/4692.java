package TestAutomation_3;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_4692
{
    @Test
    void Ability_to_select_the_statistical_model_to_ensure_that_the_statistical_forecast_generated_is_as_accurate_as_possible()
    {

        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TEST_AUTOMATION_3, "4692", tc ->
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
            tc.addStepInfoWithScreenShot("The scenario is created", true, !scenario.isEmpty());

            //STEP 3
            String workBook1 = "SHS S&OP Forecast Item Management";
            String workSheet = "Forecast Item Configuration";
            String part = "Part:10018247";
            tc.menu.openMenu(EActivity.RESOURCES);
            tc.menu.resource.filterResources(workBook1, true);
            WaitFor.condition(()-> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.FILTER, "All Parts"),
                    Map.entry(EComboBox.SITE, "1030")
            ));
            tc.tab.select(ETab.HIERARCHY);
            tc.hierarchy.removeAll();
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.DATA_SETTINGS));
            BrowserControl.waitForLoadingIndicator();
            tc.table.selectLeftClickContextItem(ETable.byIndex(0),EColumn.byIndex(0), part, ELeftContext.FORECAST_REVIEW_CHART );
            WaitFor.specificTime(Duration.ofSeconds(10));
            tc.stepEvaluator
                    .add(() -> tc.workBook.exists(workBook1), "Workbook is not displayed")
                    .add(() -> tc.chart.exists(EChart.byIndex(0)), "Chart is not displayed");
            tc.addStepInfoWithScreenShot("The workbook SHS S&OP Forecast item Management will open and in the lower pane a chart of the forecast showing actual and forecast for the selected forecast item.","ok", tc.stepEvaluator.eval());

            //STEP 4
            BufferedImage img1 = tc.chart.getImage(EChart.byIndex(0));
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.table.performActionOnItem(ETable.byIndex(0),EColumn.SETTINGS_PARAMETER_SET, "BestFit", EAction.EDIT_RANGE);
            List<String> listValues =  tc.combo.getAvailableOptions(EComboBox.VALUE);
            tc.addStepInfoWithScreenShot("A list of forecast models will be displayed", true, !listValues.isEmpty());

            //STEP 5
            tc.combo.select(EComboBox.VALUE, "ExponentialSmoothing");
            tc.button.click(EButton.APPLY);
            tc.button.click(EButton.SAVE);
            WaitFor.condition(()-> tc.chart.exists(EChart.byIndex(0)), Duration.ofSeconds(10));
            BufferedImage img2 =  tc.chart.getImage(EChart.byIndex(0));
            boolean result = tc.chart.compareChartImages(img1, img2);
            tc.addStepInfoWithScreenShot("A the forecast review will change based on the new model selection", true, !result);
        });
    }
}


