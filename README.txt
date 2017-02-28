Attached is the working directory for the prototype.  Data and code packages are included. -->
1.) Layout
2.) Layout/data            - The database files for the oracles.
3.) Layout/input           - Synthetic input files originally used for proof of concept testing.
4.) Layout/bean            - A code package.  Contains the code supporting the webservice endpoint to be consumed.  (Does not necessarily work anymore since re-sampling has been implemented.)
5.) Layout/main            - A code package.  Contains the entry point to the program.
6.) Layout/oracles         - A code package.  Contains all of the defined oracles.
7.) Layout/supportMethods  - A code package.  Contains methods used by the main.Main class.
8.) Layout/supportObjects  - A code package.  Contains object definitions used by the rest of the program.
9.) Layout/dataStructures  - A code package.  Contains basic data structure definitions used by the rest of the program.


How to run -->
The JVM requires approximately 60+ MB of memory to hold all of the databases (otherwise it thrashes and the timing is ridiculous), so I set the memory size to 128 MB just to be safe.  JDK/JRE1.5.0_12 was used as the JVM.  "main.Main" is the entry point for the program.  "configFile" is the configuration file, while not necessary it is encouraged that this file be used.

$ java -Xms128m -Xmx128m main.Main configFile inputFilePath/inputFileName

Three simple shell scripts are provided...
jc.sh --> Used to compile the code.  The last two lines can be removed as they copy the built .JAR file to the deploy directory of tomcat.
jr.sh --> Used to run the compiled code.  Accepts two parameters:  [1] the number of records to sample 25 - 200 in 25 record increments, and [2] the input file.
ra.sh --> Runs the compiled code against all of the test suite files.


Text file output ordering (XML output is essentially the same) -->
1.)  Source file name
2.)  Runtime in milliseconds
3.)  Character encoding
4.)  File type
5.)  Header found
6.)  Record delimiter {possible, found}
7.)  Field delimiter {possible, found}
8.)  Text delimiter {possible, found}
9.)  Records examined
10.) Record length
11.) The record structure
12.) The guesses used to determine the record structure
13.) The oracles used by the prototype


Configuration file description -->
1.) Written in very simple XML there are two primary sections:  the global parameters and the oracle parameters.  The global parameters control everything from the type of output to the size of the sample.
2.) A working, example file is included.
3.) <global_parameters>
  PrintTextOutput - TRUE if user wants text output, FALSE if XML is desired.
  RecordsToTest - A hueristic.  For each start position during combinatoric analysis, length pair only RTT records are considered.  If an acceptable percentage are valid, as determined by the oracle, then the remaining records will be examined; otherwise the method will move to the next start position, length pair.
  AcceptableRecordCount - A heristic.  After considering RecordsToTest records the number of valid hits must be greater than or equal to AcceptableRecordCount, acceptable record count.
  OptimalRecordCount - When re-sampling a file, attempt to acquire at least this number of records.
  SamplePartitionCount - This parameter specifies the number of partitions to read from the original file from which to build the sample.  In an attempt to make the prototype more robust, when the source file is re-sampled samples are acquired from mutiple positions in the file.
  HeaderRecordsToSkip - The number of records to skip in order to avoid any header information.
  SampleSize - The number of bytes to read from the file to use as the original sampling.
  EbcdicPercentage - The percentage of characters from the sampling that must have the most significant bit set in order to assume a file is EBCDIC.
  LineUpPercentage - Used in determining the length of records for fixed files without a record delimiter.  If the percentage a field occurs at a certain position is greater than or equal to this value it will be considered the correct record length.
  HeaderLabelSourceFile - Still in development.  The path to the knowledge base of header record labels.
  recordDelimiter - Specifiy the record delimiters to search for.  A record delimter may consist of multiple characters.  In this case separate their representative integer values by a comma.
  fieldDelimiter - Specifiy the field delimiters to search for.  A field delimter may consist of multiple characters.  In this case separate their representative integer values by a comma.
  textDelimiter - Specifiy the text delimiters to search for.  A text delimter may consist of multiple characters.  In this case separate their representative integer values by a comma.
4.) <oracles>
  qualified_name - The qualified name of the oracle to be run.  Oracles are loaded dynamically at run time using this parameter.
  rank - The empirically determined confidence ranking of the current oracle.
  TypeName - The label associated with the oracle of this content type.
  MinimumSourceFile - This is the maximum knowledge base associated with this oracle.  Only applicable for oracles that implement the AlterDatabase.java interface.
  MaximumSourceFile - This is the maximum knowledge base associated with this oracle.
  MaximumLength - A hueristic used to improve the combinatoric analysis.  Should be set to the minimum value that will include the lengths of all fields of this type.  (e.g., Most first name fields should be less than 50 characters in length.)
  MinimumThreshold - A threshold.  Percentage determining whether or not to return a field position associated with this oracle as a potential.  Eliminates poor guesses at the transition from combinatoric anaylsis to record structure decision.
  Grouping - For oracles implementing the AlterDatabase.java interface there are two counts recorded, the primary which is associated with the largest knowledge base and the secondary count which is associated with the smallest knowledge base.  Oracles in the same grouping always compare primary to primary.
  MaximumNumberPercentage - A threshold used in the name suffix oracle for vertical analysis.
