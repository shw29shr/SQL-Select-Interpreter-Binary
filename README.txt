DB Practicum Project 3 Checkpoint

Top level class for reading inputs : interpreter/SQLInterpreter.java
Output is also produced in SQLInterpreter.java by passing the BinaryTupleWriter object in the root.dump() function.
The output is actually written into binary files in fileformats/BinaryTupleWriter.java

A) Fileformats package – 

We introduced :

1) TupleReader – Top level interface for reading tuples
2) BinaryTupleReader – read Binary files
3) FileTupleReader – read human-readable  files
4) TupleWriter – Top level interface for writing tuples
5) BinaryTupleWriter – write Binary files
6) FileTupleWriter – write in human-readable format

B) Using a PropertyFile, we instantiate the FileTupleReader/Writer vs the BinaryTupleReader/Writer
We also introduced log4j for logging, in order to maintain a standard to log print statements and other logs.

C) Benchmarking – 
In the SQLInterpreter file, we calculate time taken by each query, in order to asses the performance. We will use this in continuing the project, to test our hypotheses on the right joins to use.

D) For query14, our output has the exact same tuples but in a different order than the expected output. Our tuples are still sorted on the attribute in the ORDER BY clause. This is because of the way we have implemented the Sort Operator

E) LogicalOperators vs PhysicalOperators – 
Code has been refactored with a separate package called logical operators and operators(physical operators). Visitor pattern has been used to translate a logical plan to physical plan. A visitor class called PhysicalPlanBuilder (in planbuilders package) has the concrete implementations of all the logical operators such as Join, Select, Project etc. Each Logical operator will internally call its underlying physical operator in this class. 
We have modified our generateTree() method in SelectExecutor.java to use logical operators instead of physical operator and to build a physical plan.




