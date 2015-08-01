The basic outline of the solution:

Since TreeMap offers O(log N) runtime for get and put operations I'm using a TreeMap to store the key-value pairs.
Additionally, I use another TreeMap to track the number of occurrences of each value.

To support transactions, I created a separate class called Transaction.java. Each transaction maintains two TreeMaps -
one to store changes made to the variables inside the transaction (referred as dataStore) and one to track the number of occurrences
of each value (countIndex). Since each transaction maintains its own data structures, my program is able to offer
O(log N) worst run time for all operations. When a transaction is rolled back, the active transaction object is simply discarded
and the most recent transaction that has not been committed or rolled back, if such a transaction exists is made active.
When a commit command is issued, data store from the current transaction object is copied over to the primary data store; similarly, the number of occurrences
of each value is also copied over.

For time complexity calculation of each operation, see DataStore.java

To run the program, simply compile the 3 java classes and run SimpleDataBase.main(). No additional libraries are used.
Alternatively, you can simply run the jar file inside the foler - out/artifacts/SimpleDatabase_jar/SimpleDatabase.jar
by running java -jar out/artifacts/SimpleDatabase_jar/SimpleDatabase.jar