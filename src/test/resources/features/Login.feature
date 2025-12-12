Feature: Feature to test login functionality

Background:
  Given User is on login page

@Smoke
Scenario: Check for valid login credentials
  When User enters a "standard_user" and "secret_sauce"
  And Click on login Button
  Then User should see "success"
  And Close the browser

@Regression @Smoke
Scenario Outline: check login outcome for multiple users
  When User enters a "<username>" and "<password>"
  And Click on login Button
  Then User should see "<expected>"
  And Close the browser

  Examples:
    | username                | password     | expected |
    | locked_out_user         | secret_sauce | locked   |
    | problem_user            | secret_sauce | success  |
    | performance_glitch_user | secret_sauce | success  |
    | error_user              | secret_sauce | success  |
    | visual_user             | secret_sauce | success  |
