package CAD_TMF_Zone_Operation_Lead_Role;

import CompositionRoot.IocBuilder;
import Enums.EButton;
import Enums.EEdit;
import Enums.EResultData;
import Enums.ETestData;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class TC01_Zone_Operation_Lead_Role_HomeScreen {

    @Test
    void TC_01() {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104917", tc ->
        {

            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.ZONE_OPERATION_USER);
            tc.addStepInfo("Home screen should be reflected",
                    true,
                    tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            WaitFor.condition(() -> tc.button.isDisplayed(EButton.NOTIFICATION_BELL));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.button.isDisplayed(EButton.NOTIFICATION_BELL), "'%s' button is not Displayed!".formatted(EButton.NOTIFICATION_BELL))
                    .add(() -> tc.button.isDisplayed(EButton.SIDE_MENU), "'%s' button is not Displayed!".formatted(EButton.SIDE_MENU))
                    .add(() -> tc.edit.isDisplayed(EEdit.SERVICE_EDGE_SEARCH), "'%s' edit box is not Displayed!".formatted(EEdit.SERVICE_EDGE_SEARCH));
            tc.addStepInfo("Menu, Search bar, Notification button should be reflected",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.addStepInfo("HomeScreen message is present",
                    true, tc.browser.getHomeScreenMessage().equals("You can start browsing the different views " +
                            "by clicking the menu button at the top left. This button is always available for quick " +
                            "navigation between the views. You can also perform a global search for records, objects and" +
                            " views using the Search field in the blue header.\n" + "\n" +
                            "We hope you enjoy this new product! Feedback is always welcome :-)\n" +
                            "- The ClickSoftware team"), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


