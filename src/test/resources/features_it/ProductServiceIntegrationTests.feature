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

  Scenario Template: Create product with invalid data
    Given product data as below
      | name   | price   | stockQuantity   |
      | <name> | <price> | <stockQuantity> |
    When I try to create a product
    Then product creation throws Validation Exception
    Examples:
      | name | price | stockQuantity |
      |      | 2.99  | 420           |
      | P    | 2.99  | 420           |
      | Piwo | -2.99 | 420           |
      | Piwo |       | 420           |
      | Piwo | 0     | 420           |
      | Piwo | 2.99  | -1            |

  Scenario: Create product with duplicated name
    Given product data as below
      | name | price | stockQuantity |
      | Piwo | 2.99  | 420           |
    And product is created if not exists
    When I create product with duplicated name
    Then product creation throws Unique Constraint Exception

  Scenario: Read non existing product
    Given product id is max+1
    When I read product by id
    Then product is null

