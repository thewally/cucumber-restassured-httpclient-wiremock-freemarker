Feature: HttpClient

  Background:
    Given users are available
      | id | lastName    | firstName | streetName | houseNumber | postalCode | city      |
      | 1  | van der Wal | Arjen     | laanstraat | 100         | 1111AA     | Amsterdam |
      | 2  | Jansen      | Joyce     | kerklaan   | 10          | 1234BB     | Almere    |
    Given books are available
      | id | title                                       | author        | year |
      | 1  | Beginning Programming with Java For Dummies | Barry A. Burd | 2014 |
      | 2  | Beginning Java Programming                  | Bart Baesens  | 2015 |
    Given user 1 has books with id
      | 1 |
      | 2 |
    Given user 2 has books with id
      | 2 |
    Given service getBooksForUsers returns response for all users and for each user
    Then stop test

  Scenario: send a request to getBooksForUsers for user 1
    When send request message to service getBooksForUsers for user 1
    Then getBookForUser returns for user 1 with their own books

  Scenario: send a request to getBooksForUsers for all users
    When send request message to service getBooksForUsers for all users
    Then getBookForUser returns for all users with their own books



