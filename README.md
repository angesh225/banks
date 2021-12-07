# Banking System
## About
This is a team based project for CS157A (Intro to DBMS) that utilizes MySQL as the DBMS, Java as the front-end, and JDBC as the connector in order to emulate a basic banking system based on the dataset provided below:

## Setup
This project uses Maven in order to inject our JDBC dependencies, so an IDE that allows for Maven projects (i.e: Eclipse) is preferred.

A sample config.json file is provided, and contains the credentials needed to connect MySQL locally. By default, the root account is used, but the username and password can be changed to an existing MySQL user if you would like to avoid such.


## Minimum Requirements
The system should support at least 15 distinct functions to the users. Here the users means public users and the administrator of the application, not including DBA.

The database involves at least 5 relations and total 15 attributes. There should be relations connect one relation to at least one other relation. The Loan relation in our case study is such an example; Loan connects User and Book.
Relations should be in BCNF or 3NF.

Your system should be able to handle at least 5 significantly different queries involving different relations and attributes. Make sure to have at least one co-related subquery, group by and having, aggregation, outer join, and set operation. At least 3 of them must involve several relations simultaneously.

All schema should come with a key constraint.

Reference integrity constraints are imposed on all possible cases to avoid dangling pointers. Please avoid circular constraints.
Define at least two triggers in the database.

In large database systems, it is very common that their data grows over time and an archive function, which copies older entries into an archive database, will be useful. You will follow a simple approach to implement this function. Decide a relation that will be archived. Let's say the table name is t. Supply one additional column called updatedAt to the relation t from which you want to archive. This column's value will be set to the current timestamp whenever a tuple is inserted and modified in the relation t. Create another relation called Archive, which will store archived data. And write a stored procedure that takes a cutoff date as a parameter and copies tuples from t that haven't been modified since the cutoff date into the table Archive and deletes those archived tuples from t.
