import com.microsoft.playwright.*;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.nio.file.Paths;


public class BaseTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    protected Page page;

    @BeforeClass
    protected void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
        );
    }

    @BeforeMethod
    protected void createContextAndPage() {
        context = browser.newContext(
                new Browser.NewContextOptions()
                        .setBaseURL("https://magento.softwaretestingboard.com/")
        );

        context.tracing().start(
                new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true)
        );

        page = context.newPage();
    }

    @AfterMethod
    protected void closeContext(Method method, ITestResult testResult) {
        Tracing.StopOptions tracingStopOptions = null;
        String classMethodName = this.getClass().getName() + method.getName();
        if (!testResult.isSuccess()) {
            tracingStopOptions = new Tracing.StopOptions()
                    .setPath(Paths.get("testTracing/" + classMethodName + ".zip"));
        }
        context.tracing().stop(
                tracingStopOptions
        );

        context.close();
    }

    @AfterClass
    protected void closeBrowser() {
        browser.close();
        playwright.close();
    }
}
