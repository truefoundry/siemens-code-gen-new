package CAD_TMF_Dispatcher_Education_Role;

import CompositionRoot.IocBuilder;
import ControlImplementation.ModalControl;
import Enums.*;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.DomUtils;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Objects;


public class TC02_Dispatcher_Education_Role_HomeScreen_Notification
{
    @Test
    void TC_02()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_DISPATCHER_EDUCATION_ROLE, "115717", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());

            //STEP 1
            tc.browser.login(ETestData.DISPATCHER_EDUCATION_ROLE_USER);
            tc.addStepInfo("Home screen should be reflected",
                    true,
                    tc.browser.getCurrentURL().toLowerCase().contains("welcome".toLowerCase()),
                    new ComparerOptions().takeScreenShotPlatform());

            // STEP 2
            WaitFor.condition(()->tc.button.isDisplayed(EButton.NOTIFICATION_BELL));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.NOTIFICATION_BELL), "'%s' button is not displayed!".formatted(EButton.NOTIFICATION_BELL))
                    .add(()-> tc.button.isDisplayed(EButton.SIDE_MENU), "'%s' button is not displayed!".formatted(EButton.SIDE_MENU))
                    .add(()-> tc.edit.isDisplayed(EEdit.SERVICE_EDGE_SEARCH), "'%s' edit box is not displayed!".formatted(EEdit.SERVICE_EDGE_SEARCH));
            tc.addStepInfo("Menu, Search bar, Notification button should be reflected",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.button.click(EButton.NOTIFICATION_BELL);
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.modal.exists(EModal.ALERT_MODAL),"Alert Modal under notification bell button is not present")
                    .add(()-> tc.button.isDisplayed(EButton.NOTIFICATION_SETTING), "'%s' button not found!".formatted(EButton.NOTIFICATION_SETTING))
                    .add(()-> tc.button.isDisplayed(EButton.DISMISS_ALL_ALERTS), "'%s' button not found!".formatted(EButton.DISMISS_ALL_ALERTS));
            tc.addStepInfo("Alert Center pop up should be reflected with list of pending notifications" +
                    " Setting and dismiss all alerts button","ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.button.click(EButton.NOTIFICATION_SETTING);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(this::checkHeader,"Alert Center setting modal header is not correct")
                    .add(()->tc.modal.exists(EModal.MODAL),"Alert Center setting modal is not present")
                    .add(()-> tc.button.isDisplayed(EButton.APPLY), "'%s' button not found!".formatted(EButton.APPLY));
            tc.addStepInfo("Alert Center Settings should be reflected with Schedule view domains only toggle button, " +
                            "Search Domain, Apply /Cancel button","ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.checkbox.check(ECheckBox.SELECT_ALL);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.checkbox.unCheck(ECheckBox.SELECT_ALL);
            tc.button.click(EButton.APPLY);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.NOTIFICATION_SETTING), "'%s' button not found!".formatted(EButton.NOTIFICATION_SETTING))
                    .add(()-> tc.button.isDisplayed(EButton.DISMISS_ALL_ALERTS), "'%s' button not found!".formatted(EButton.DISMISS_ALL))
                    .add(this::isNotificationPresent,"  There is no notification present which is related to the domain");
            tc.addStepInfo("Notifications related to the selected domain should be reflected",
                    "ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.button.click(EButton.DISMISS_ALL_ALERTS);
            WaitFor.condition(()->tc.button.isDisplayed(EButton.DISMISS_ALL),Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.button.isDisplayed(EButton.CANCEL), "'%s' button not found!".formatted(EButton.CANCEL))
                    .add(()-> tc.button.isDisplayed(EButton.DISMISS_ALL), "'%s' button not found!".formatted(EButton.DISMISS_ALL));
            tc.addStepInfo("Are you sure? Message should be reflected with Dismiss All and Cancel button",
                    "ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.button.click(EButton.DISMISS_ALL);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.addStepInfo("All the notifications should be removed",
                    true,!isNotificationPresent(),
                    new ComparerOptions().takeScreenShotPlatform());

        });
    }

    private boolean checkHeader()
    {
        CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
        String header = Objects.requireNonNull(css.findControlWithRoot(By.cssSelector("div[class='header ng-binding']"), root)).getText();
        return header.equals("Alert Center Settings");
    }

    private boolean isNotificationPresent()
    {
        CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
        try
        {
           WebElement element = css.findControlWithRoot(By.cssSelector("div[class='no_alerts']"),root);
            return element == null;
        }
        catch (Exception e)
        {
            TcLog.error("Error while getting notification");
        }
        return false;
    }
}


