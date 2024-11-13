Overview: This program a flow log file and maps each row to a tag based on a lookup table. It will generate counts of matches for each tag and port/protocol combination.

Assuming
File order is <flowLogFile> <lookupFile> <outputFile>.
LookupFile contains 3 columns: dstport,protocol,tag.
Supports only default log formats.
Only logs starting with "2" are processed.
Case-insensitive matching for dstport.
Untagged logs are counted separately.
The dst is inferred from the 7th column based on https://docs.aws.amazon.com/vpc/latest/userguide/flow-log-records.html.
The protocol is inferred from the 8th column based on https://docs.aws.amazon.com/vpc/latest/userguide/flow-log-records.html.

To run the program: 
javac FlowLogTagCounter.java
java FlowLogTagCounter flowLogFile.txt lookup.csv output.txt

Output
Tag Counts: Count of matches for each tag.
Port/Protocol Combination Counts: Count of matches for each port/protocol combination.