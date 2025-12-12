package TestRunner;

import org.testng.annotations.DataProvider;

/*import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(tags="",features= {"src/test/resources/features"},
glue= {"StepDefinition"},
plugin= {"pretty","html:target/htmlreport.html"})*/

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
	tags = "@Smoke or @Regression",	
    features = {"src/test/resources/features"}, // Path to feature files
    glue = {"StepDefinitions","utils"},                // Package containing step definitions
    plugin = {"pretty", "html:target/cucumber-reports.html"}, // Reports
    monochrome = true,                       // For better console readability
    dryRun = false                            // Checks missing step definitions without execution
)

public class CucumberTestRunner extends AbstractTestNGCucumberTests {
	// enable parallel scenarios
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }

}
