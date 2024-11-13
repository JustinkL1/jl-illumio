import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FlowLogTagCounter {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java FlowLogTagger <flowLogFile> <lookupFile> <outputFile>");
            return;
        }

        String flowLogFile = args[0];
        String lookupFile = args[1];
        String outputFile = args[2];

        // Data structures to store mappings and counts
        Map<String, String> lookupTable = new HashMap<>();
        Map<String, Integer> tagCounts = new HashMap<>();
        Map<String, Integer> portProtocolCounts = new HashMap<>();

        try {
            // Read lookup table
            BufferedReader lookupReader = new BufferedReader(new FileReader(lookupFile));
            System.out.println("Reading " + lookupFile);
            String lookupLine;
            while ((lookupLine = lookupReader.readLine()) != null) {
                String[] parts = lookupLine.split(",");
                System.out.println(Arrays.toString(parts));
                if (parts.length == 3) {
                    String key = parts[0].trim() + "," + parts[1].trim().toLowerCase(); // Make key comparison case-insensitive
                    String tag = parts[2].trim();
                    lookupTable.put(key, tag);
                }
            }
            lookupReader.close();

            // Read flow log file
            BufferedReader flowLogReader = new BufferedReader(new FileReader(flowLogFile));
            System.out.println("Reading " + flowLogFile);
            String flowLogLine;
            while ((flowLogLine = flowLogReader.readLine()) != null) {
                String[] parts = flowLogLine.split("\\s+");
                System.out.println(Arrays.toString(parts));
                if (parts.length >= 8 && parts[0].equals("2")) {
                    String dstPort = parts[6];
                    String protocol = mapProtocol(parts[7]);
                    String key = dstPort + "," + protocol;
                    // Determine tag
                    String tag = lookupTable.getOrDefault(key.toLowerCase(), "untagged"); // Compare in lowercase
                    tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);

                    // Count port/protocol combinations
                    portProtocolCounts.put(key, portProtocolCounts.getOrDefault(key, 0) + 1);
                }
            }
            flowLogReader.close();
            
            // Write output
            PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
            writer.println("Tag Counts:");
            writer.println("Tag,Count");
            for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }

            writer.println();
            writer.println("Port/Protocol Combination Counts:");
            writer.println("Port,Protocol,Count");
            for (Map.Entry<String, Integer> entry : portProtocolCounts.entrySet()) {
                String[] keyParts = entry.getKey().split(",");
                writer.println(keyParts[0] + "," + keyParts[1] + "," + entry.getValue());
            }
            System.out.println("Finished writing output to " + outputFile);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to map numeric protocol to its name
    private static String mapProtocol(String protocolNumber) {
        switch (protocolNumber) {
            case "6":
                return "tcp";
            case "17":
                return "udp";
            case "1":
                return "icmp";
            default:
                return "unknown";
        }

    }
}
