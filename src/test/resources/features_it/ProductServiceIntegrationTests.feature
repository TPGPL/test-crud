Feature: Product service test

  Scenario Template: Create product successfully
    Given product data as below
      | name   | price   | stockQuantity   |
      | <name> | <price> | <stockQuantity> |
    When I create product
    Then I should see product created successfully
    Examples:
      | name  | price | stockQuantity |
      | Piwo  | 2.99  | 2136          |
      | Fajki | 15.99 | 420           |
      | Mleko | 4.49  | 16            |
