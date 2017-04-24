@state
Feature: Stateful Example

  Background:
    Given possible states are available
      | ACTIVE   |
      | INACTIVE |
      | EXPIRED  |
      | BLOCKED  |
    Then stop test

  Scenario: Set States
    When change state to ACTIVE
    Then state is changed to ACTIVE
