Feature: Product service tests

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

  Scenario Template: Read existing product
    Given product data as below
      | name   | price   | stockQuantity   |
      | <name> | <price> | <stockQuantity> |
    And product is created if not exists
    When I read product
    Then I should see product data as below
      | name   | price   | stockQuantity   |
      | <name> | <price> | <stockQuantity> |
    Examples:
      | name  | price | stockQuantity |
      | Piwo  | 2.99  | 2136          |
      | Fajki | 15.99 | 420           |
      | Mleko | 4.49  | 16            |

  Scenario: Update non existing product
    Given product id is max+1
    When I update product by id
    Then product not found exception is thrown

  Scenario Template: Update existing product
    Given product data as below
      | name | price | stockQuantity |
      | Piwo | 2.99  | 2136          |
    And product is created if not exists
    When I update product with data below
      | name     | price     | stockQuantity     |
      | <upName> | <upPrice> | <upStockQuantity> |
    Then I should see product data as below
      | name      | price      | stockQuantity      |
      | <aftName> | <aftPrice> | <aftStockQuantity> |
    Examples:
      | upName | upPrice | upStockQuantity | aftName | aftPrice | aftStockQuantity |
      | Woda   | 2.99    | 2136            | Woda    | 2.99     | 2136             |
      | Piwo   | 5.99    | 2136            | Piwo    | 5.99     | 2136             |
      | Piwo   | 2.99    | 420             | Piwo    | 2.99     | 420              |
      | Cola   | 6.99    | 102             | Cola    | 6.99     | 102              |
      | Piwo   | 2.99    | 2136            | Piwo    | 2.99     | 2136             |

  Scenario: Delete existing product
    Given product data as below
      | name | price | stockQuantity |
      | Piwo | 2.99  | 2136          |
    And product is created if not exists
    When I delete product
    Then product is not in database anymore
