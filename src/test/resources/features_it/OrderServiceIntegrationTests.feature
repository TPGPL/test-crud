Feature: Client service test

  Background:
    Given client, product and order line entities are removed
    And client is created as below:
      | name    | surname | email              |
      | Andrzej | Bulba   | andrzejbulba@wp.pl |
    And product is created as below:
      | productName | productPrice | productQuantity |
      | Apple       | 1.10         | 420             |
      | Samsung     | 5599.99      | 40              |
    And order lines are created as below:
      | quantity |
      | 10       |
      | 20       |
    And order lines too big are created as below:
      | quantity |
      | 10       |
      | 430      |


  Scenario Template: Create order successfully
    Given order data as below:
      | status   |
      | <status> |
    When I create order
    Then I should see order created successfully
    Examples:
      | status     |
      | New        |
      | InProgress |
      | Completed  |
      | Cancelled  |

  Scenario: Create order with invalid data
    When I try to create an order with null status
    Then Order creation throws Validation Exception

  Scenario: Create order with invalid data
    When I try to create an order with null client
    Then Order creation throws Validation Exception

  Scenario: Create order with empty order lines
    When I create order with empty order lines
    Then Order creation throws Validation Exception

  Scenario: Create order when not enough stock
    When I create order with order line quantity more than stock
    Then Order creation throws Validation Exception

  Scenario: Read non existing order
    Given order id is max+1
    When I read order by id
    Then order is null

  Scenario Template: Read existing order
    Given order data as below:
      | status   |
      | <status> |
    And order is created if not exists
    When I read order by id
    Then I should see order data as below:
      | status   |
      | <status> |
    Examples:
      | status     |
      | New        |
      | InProgress |
      | Completed  |
      | Cancelled  |

  Scenario: Update non existing order
    Given order id is max+1
    When I update order by id
    Then order not found exception is thrown


  Scenario Template: Update existing order
    Given order data as below:
      | status     |
      | New        |
      | InProgress |
      | Completed  |
      | Cancelled  |
    And order is created if not exists
    When I update order with below data:
      | status     |
      | <upStatus> |
    Then I should see order data as below:
      | status      |
      | <aftStatus> |
    Examples:
      | upStatus   | aftStatus  |
      | New        | New        |
      | InProgress | InProgress |
      | Cancelled  | Cancelled  |
      | Completed  | Completed  |

  Scenario: Delete existing order
    Given order data as below:
      | status     |
      | New        |
      | InProgress |
      | Completed  |
      | Cancelled  |
    And order is created if not exists
    When I delete order
    Then order is not in database anymore