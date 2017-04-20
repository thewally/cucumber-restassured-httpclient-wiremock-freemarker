Feature: HttpClient

  Background:
    Given users are available
      | id | lastName    | firstName | streetName | houseNumber | postalCode | city      |
      | 1  | van der Wal | Arjen     | laanstraat | 100         | 1111AA     | Amsterdam |
      | 2  | Jansen      | Joyce     | kerklaan   | 10          | 1234BB     | Almere    |
      | 3  | Meloen      | Coen      | Bergweg    | 50          | 3214QQ     | Rotterdam |
    Given books are available
      | id | title                                       | author        | year |
      | 1  | Beginning Programming with Java For Dummies | Barry A. Burd | 2014 |
      | 2  | Beginning Java Programming                  | Bart Baesens  | 2015 |
    Given user 1 has books with id
      | 1 |
      | 2 |
    Given user 2 has books with id
      | 2 |
    Given service getBooksForUsers returns response for all users or by user id

  Scenario: send a request to getBooksForUsers for user 1
    When send request message to service getBooksForUsers for 1
    Then getBookForUser returns for 1 with their own books

  Scenario: send a request to getBooksForUsers for user 2
    When send request message to service getBooksForUsers for 2
    Then getBookForUser returns for 2 with their own books

  Scenario: send a request to getBooksForUsers for user 3
    When send request message to service getBooksForUsers for 3
    Then getBookForUser returns for 3 with their own books

  Scenario: send a request to getBooksForUsers for all users
    When send request message to service getBooksForUsers for ALL
    Then getBookForUser returns for ALL with their own books


