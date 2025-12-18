# BDDFramework (local modifications)

This project contains Cucumber + Selenium tests against the saucedemo sample site. I made the following enhancements:

- Centralized wait helpers in `utils.WaitUtils` to avoid blank-window and race conditions.
- `LoginPage.open()` now uses `WaitUtils.waitForDocumentComplete` and waits for the username element before proceeding.
- Driver lifecycle is managed by `utils.TestHooks` (@Before/@After) and `utils.DriverFactory`.
- A Maven profile `headless` was added. Run `mvn -P headless test` to execute tests in headless mode.

Run tests:

Windows (cmd.exe):

mvn test

Run headless:

mvn -P headless test

Override browser via system property:

mvn -Dbrowser=firefox test

Notes:
- You can also pass `-Dchromedriver.version=<version>` to pin the chromedriver used by WebDriverManager.
- If you see CDP warnings for Chrome, consider adding a matching `selenium-devtools-vXX` dependency for your Chrome major version.