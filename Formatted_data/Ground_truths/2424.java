package TestAutomation_1;

import CompositionRoot.InVivoHandler;
import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_2424
{
    @Test
    void TC_RS_1886()
    {
        IocBuilder.execute(Duration.ofMinutes(12), EResultData.TEST_AUTOMATION, "2424", tc -> {

                    //----- STEP 1 -----
                    tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
                    tc.addStepInfoWithScreenShot("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                            && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                            && tc.button.exists(EButton.USER_PROFILE));

                    //----- STEP 2 -----
                    final String dashboard = "SHS Demand Planner";
                    final String scenarioParent = "S&OP Candidate";
                    tc.menu.openMenu(EActivity.SCENARIOS);
                    String scenario = tc.menu.scenarios.create(scenarioParent);
                    tc.menu.openMenu(EActivity.RESOURCES);
                    tc.menu.resource.filterResources(dashboard, true);
                    BrowserControl.waitForLoadingIndicator();
                    this.openDataSettings(tc);
                    tc.tab.select(ETab.DEMAND_PLANNER_METRICS);
                    tc.workBook.menu.setDataSettings(List.of(
                            Map.entry(EComboBox.SCENARIO, scenario),
                            Map.entry(EComboBox.FILTER, "All Parts"),
                            Map.entry(EComboBox.SITE, "All Sites")
                    ));
                    // closeDataSettings(tc);
                    tc.addStepInfoWithScreenShot("Dashboard '%s' is opened.".formatted(dashboard), dashboard, tc.workBook.getSelected());

                    //----- STEP 3 -----
                    tc.addStepInfoWithScreenShot("‘Consensus Demand Plan’ is selected in a private scenario",
                            true,
                            tc.workBook.menu.isListedInWidgetToolTipForScenario("Consensus Demand Plan"));

                    //----- STEP 4 -----
                    this.demandsStreamsAreDisplayed_AndActualSalesAreSeen(tc);

                    tc.addStepInfoWithScreenShot("In ‘Consensus Demand Plan ‘Widget all demand streams displayed + actual sales are seen.", "ok",
                            tc.stepEvaluator.eval());

                    //----- STEP 5 -----
                    this.selectCurrency(tc, "EUR (Euro)");
                    this.demandsStreamsAreDisplayed_AndActualSalesAreSeen(tc);
                    String consensusWorkbook = "SHS S&OP Consensus Demand Planning";
                    tc.button.click(EButton.DATA_SETTINGS);
                    tc.chart.clickOnCanvasWithCorrection(EChart.CONSENSUS_DEMAND_PLAN,
                            new Dimension(637, 265),
                            new Point(372, 186),
                            40,
                            tc.workBook.getSelected().equalsIgnoreCase(consensusWorkbook)
                    );

                    WaitFor.condition(() -> tc.workBook.getSelected().equalsIgnoreCase(consensusWorkbook));

                    // test case dont care about values in 'Consensus Demand Planning', but if you do, then you need to change preferencess for
                    // user ( currency EURO )in order to see values
                    tc.stepEvaluator.add(() -> tc.workBook.getSelected().equalsIgnoreCase(consensusWorkbook), "Click on  " +
                            "‘Consensus Demand " +
                            "Plan ‘Widget is not redirecting you into S&OP consensus " +
                            "demand planning workbook"
                    );
                    tc.addStepInfoWithScreenShot(("In ‘Consensus Demand Plan ‘Widget all demand streams displayed +" +
                                    " actual sales are seen. click on it and it will take you to the S&OP consensus demand planning workbook."),
                            "ok",
                            tc.stepEvaluator.eval());
                }
        );
    }

    private void selectCurrency(InVivoHandler tc, String currency)
    {
        if(!tc.combo.exists(EComboBox.SHOW_UNITS_OR_REVENUE))
        {
            tc.button.click(EButton.CONSENSUS_DEMAND_PLAN); //expand to see combo
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SHOW_UNITS_OR_REVENUE));
        }
        tc.combo.select(EComboBox.SHOW_UNITS_OR_REVENUE, "Revenue");
        WaitFor.condition(() -> tc.combo.exists(EComboBox.CURRENCY));
        tc.combo.select(EComboBox.CURRENCY, currency);
        //wait for finish of loading chart
        WaitFor.condition(() -> !tc.chart.getGroups(EChart.CONSENSUS_DEMAND_PLAN).isEmpty());
    }

    private void demandsStreamsAreDisplayed_AndActualSalesAreSeen(InVivoHandler tc)
    {
        tc.stepEvaluator.reset();
        tc.stepEvaluator.add(true, tc.chart.exists(EChart.CONSENSUS_DEMAND_PLAN),
                "Chart '%s' is not present.".formatted(EChart.CONSENSUS_DEMAND_PLAN.getValue()));
        List<String> currentStreams = tc.chart.getGroups(EChart.CONSENSUS_DEMAND_PLAN).stream().map(x -> x.split(":")[0]).toList();
        WaitFor.condition(() -> tc.chart.getGroups(EChart.CONSENSUS_DEMAND_PLAN).contains("Actual"), Duration.ofSeconds(30));
        List<String> demandStreams = List.of("Actual", "BusinessLinesAT", "BusinessLinesCT", "SparePartsCS",
                "DependentDmdFromSAP", "Statistical", "Demand Plan");
        List<String> missingDemandStreams = demandStreams.stream().filter(stream -> !currentStreams.contains(stream)).toList();
        tc.stepEvaluator
                .add(true, demandStreams.equals(currentStreams),
                        "Not all demand stream are displayed. These streams are missing: %s. Expected: %s. Current: %s"
                                .formatted(missingDemandStreams, demandStreams, currentStreams))
                .add(true, tc.chart.getGroups(EChart.CONSENSUS_DEMAND_PLAN).contains("Actual"),
                        "Missing actual sales label.")
                .add(true, !tc.chart.getDataFromDom(EChart.CONSENSUS_DEMAND_PLAN).get("Actual.").isEmpty(),
                        "Actual sales data are missing.");
    }

    private void closeDataSettings(InVivoHandler tc)
    {
        WaitFor.condition(() -> tc.button.exists(EButton.DATA_SETTINGS));
        if(!tc.button.isPressed(EButton.DATA_SETTINGS))
        {
            return;
        }
        tc.button.click(EButton.DATA_SETTINGS);
        WaitFor.condition(() -> !tc.button.isPressed(EButton.DATA_SETTINGS));
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
}


