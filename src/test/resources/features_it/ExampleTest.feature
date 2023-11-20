Feature: Example feature that's being tested
  
  Scenario: Addition test
    Given a is 1
    And b is 2
    When I add a and b
    Then the result is 3
    
  Scenario Template: Subtraction test
    Given a is <aval>
    And b is <bval>
    When I subtract a from b
    Then the result is <result>
    Examples:
      | aval | bval | result |
      | 10   | 5    | -5     |
      | 4    | 6    | 2      |