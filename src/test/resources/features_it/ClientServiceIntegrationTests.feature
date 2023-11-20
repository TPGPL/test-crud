Feature: Client service test

  Scenario Template: Create client successfully
    Given client data as below:
      | name   | surname   | email   |
      | <name> | <surname> | <email> |
    When I create client
    Then I should see client created successfully
    Examples:
      | name       | surname    | email                      |
      | Andrzej    | Bulwa      | andrzejbulwa@wp.pl         |
      | Anna Maria | Wesołowska | wesolowska.ssr@sady.gov.pl |

  Scenario Template: Create client with invalid data
    Given client data as below:
      | name   | surname   | email   |
      | <name> | <surname> | <email> |
    When I try to create a client
    Then Client creation throws Validation Exception
    Examples:
      | name    | surname | email              |
      | Andrzej | Bulwa   | andrzejbulwa       |
      | Andrzej | Bulwa   | andrzejbulwa@      |
      | Andrzej | Bulwa   | andrzejbulwa@wp.   |
      | Andrzej | Bulwa   |                    |
      | Andrzej |         | andrzejbulwa@wp.pl |
      |         | Bulwa   | andrzejbulwa@wp.pl |
      | A       | Bulwa   | andrzejbulwa@wp.pl |
      | Andrzej | Bulwa   | a@b.pl             |
      |         |         |                    |

  Scenario: Create client with duplicated email
    Given client data as below:
      | name    | surname | email              |
      | Andrzej | Bulwa   | andrzejbulwa@wp.pl |
    And client is created if not exists
    When I create client with duplicate email
    Then client creation throws Unique Constraint Exception

  Scenario: Read non existing client
    Given client id is max+1
    When I read client by id
    Then client is null

  Scenario Template: Read existing client
    Given client data as below:
      | name   | surname   | email   |
      | <name> | <surname> | <email> |
    And client is created if not exists
    When I read client
    Then I should see client data as below:
      | name   | surname   | email   |
      | <name> | <surname> | <email> |
    Examples:
      | name       | surname    | email                      |
      | Andrzej    | Bulwa      | andrzejbulwa@wp.pl         |
      | Anna Maria | Wesołowska | wesolowska.ssr@sady.gov.pl |

  Scenario: Update non existing client
    Given client id is max+1
    When I update client by id
    Then client not found exception is thrown


  Scenario Template: Update existing client
    Given client data as below:
      | name    | surname | email              |
      | Andrzej | Bulwa   | andrzejbulwa@wp.pl |
    And client is created if not exists
    When I update client with below data:
      | name     | surname     | email     |
      | <upname> | <upsurname> | <upemail> |
    Then I should see client data as below:
      | name      | surname      | email      |
      | <aftname> | <aftsurname> | <aftemail> |
    Examples:
      | upname  | upsurname | upemail            | aftname | aftsurname | aftemail           |
      | Andrzej | Bulwa     | abba@wp.pl         | Andrzej | Bulwa      | abba@wp.pl         |
      | Andrzej | Babba     | andrzejbulwa@wp.pl | Andrzej | Babba      | andrzejbulwa@wp.pl |
      | Maniek  | Bulwa     | andrzejbulwa@wp.pl | Maniek  | Bulwa      | andrzejbulwa@wp.pl |
      | Maniek  | Babba     | andrzejbulwa@wp.pl | Maniek  | Babba      | andrzejbulwa@wp.pl |
      | Maniek  | Babba     | abba2@wp.pl        | Maniek  | Babba      | abba2@wp.pl        |
      | Andrzej | Bulwa     | andrzejbulwa@wp.pl | Andrzej | Bulwa      | andrzejbulwa@wp.pl |

  Scenario: Delete existing client
    Given client data as below:
      | name    | surname | email              |
      | Andrzej | Bulwa   | andrzejbulwa@wp.pl |
    And client is created if not exists
    When I delete client
    Then client is not in database anymore