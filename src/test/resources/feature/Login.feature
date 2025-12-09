Feature: Feature to test login functionality

Background: 
Given User is on login page

  @Smoke
  Scenario: Check for valid login credentials
    
    When User enters a "<standard_user>" and "<secret_sauce>"
    And Click on login Button
    Then User navigate to home page
    And Close the browser

  @Regression @Smoke
  Scenario Outline: check for other valid login credentials
    When User enters a "<username>" and "<password>"
    And Click on login Button
    Then User navigate to home page
    And Close the browser

    Examples: 
      | username                | password     |
      | locked_out_user         | secret_sauce |
      | problem_user            | secret_sauce |
      | performance_glitch_user | secret_sauce |
      | error_user              | secret_sauce |
      | visual_user             | secret_sauce |
