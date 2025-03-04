package Admin;

import CompositionRoot.IocBuilder;
import ControlImplementation.BrowserControl;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC04_Show_Me_All_Requests_Layout_Check
{
    @Test
    void Show_Me_All_Requests_Layout_Check()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "841", tc ->
        {
            //Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_All_REQUESTS));
            tc.tile.open(ETile.SHOW_ME_All_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.byIndex(0)));
            tc.addStepInfo("""
                    Page with dashboard with all requests created by the user is opened on tab Created Requests            
                    For reference check attached screenshot
                    """, true, tc.tab.exists(ETab.byIndex(0)), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            BrowserControl.waitForLoadingIndicator();
            List<String> actualTabs = tc.tab.getAllTabsNames();
            tc.addStepInfo("""
                    Page contains tabs:                 
                    - My Dashboard
                    - Created Requests
                    - Submitted
                    - Under Review
                    - Pending Requestor Response
                    - Referred Out
                    - Referred Out to Me
                    - Resolved
                    """, true, !actualTabs.isEmpty(), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}


