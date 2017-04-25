@state
Feature: Stateful Example

  Background:
    Given possible states are available
      | ACTIVE   |
      | INACTIVE |
      | EXPIRED  |
      | BLOCKED  |

  Scenario: Set States
    When change state to ACTIVE
    Then state is changed to ACTIVE
    When change state to INACTIVE
    Then state is changed to INACTIVE
    When change state to EXPIRED
    Then state is changed to EXPIRED
    When change state to BLOCKED
    Then state is changed to BLOCKED
    When change state to ACTIVE
    Then state is changed to ACTIVE
