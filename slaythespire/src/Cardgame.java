import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Cardgame {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the name of the card file: ");
        String fileName = input.nextLine();

        // Read the deck data from the specified file
        DeckData deckData = readDeckFile(fileName);

        // If deckData is null, the file was not found; exit the program
        if (deckData == null) return;

        // Check for deck validity based on card count and invalid cards
        if (deckData.invalidCards.size() > 10 || deckData.cardCount > 1000) {
            // Produce a void report if the conditions are met
            generateVoidReport(deckData.deckId);
            System.out.println("Deck ID: " + deckData.deckId + " (VOID)");
            System.out.println("VOID");
        } else {
            // Print a report if the deck is valid
            printReport(deckData);
        }
    }

    // Method to read the deck file and gather data
    private static DeckData readDeckFile(String fileName) {
        List<String> invalidCards = new ArrayList<>(); // List to store invalid card entries
        Map<Integer, Integer> costHistogram = new HashMap<>(); // Map to count occurrences of each cost
        int totalCost = 0; // Variable to accumulate total cost of cards
        int cardCount = 0; // Counter for total number of cards
        String deckId = generateDeckId(); // Generate a unique deck ID

        try {
            // Attempt to open the specified file
            File file = new File(fileName);
            Scanner fileScanner = new Scanner(file);

            // Process each line in the file
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                cardCount++; // Increment card count for each line
                processCard(line, invalidCards, costHistogram); // Process the current card
            }
            fileScanner.close(); // Close the file scanner
        } catch (FileNotFoundException e) {
            // Handle the case where the file cannot be found
            System.out.println("Error reading file: " + fileName);
            return null; // Return null for file not found
        }

        // Calculate total cost from the histogram
        totalCost = calculateTotalCost(costHistogram);

        // Return a new DeckData object containing the gathered data
        return new DeckData(deckId, totalCost, costHistogram, invalidCards, cardCount);
    }

    // Method to process a single card entry
    private static void processCard(String line, List<String> invalidCards, Map<Integer, Integer> costHistogram) {
        String[] parts = line.split(":"); // Split the line into card name and cost
        if (parts.length == 2) {
            String cardName = parts[0].trim(); // Get the card name
            String costStr = parts[1].trim(); // Get the cost string
            if (isValidCard(cardName, costStr)) { // Check if the card is valid
                int cost = Integer.parseInt(costStr); // Convert cost to an integer
                // Update the cost histogram with the card's cost
                costHistogram.put(cost, costHistogram.getOrDefault(cost, 0) + 1);
            } else {
                // If the card is invalid, add it to the invalid cards list
                invalidCards.add(line);
            }
        } else {
            // If the line doesn't have exactly two parts, it's invalid
            invalidCards.add(line);
        }
    }

    // Method to validate a card entry
    private static boolean isValidCard(String cardName, String costStr) {
        // A card is invalid if it has an empty name
        if (cardName.isEmpty()) return false;

        try {
            int cost = Integer.parseInt(costStr); // Attempt to parse the cost
            // Valid costs must be between 0 and 6 (inclusive)
            return cost >= 0 && cost <= 6;
        } catch (NumberFormatException e) {
            // If parsing fails, the cost is invalid
            return false;
        }
    }

    // Method to calculate the total cost of cards based on the cost histogram
    private static int calculateTotalCost(Map<Integer, Integer> costHistogram) {
        int totalCost = 0; // Initialize total cost
        // Iterate over each entry in the cost histogram
        for (Map.Entry<Integer, Integer> entry : costHistogram.entrySet()) {
            totalCost += entry.getKey() * entry.getValue(); // Accumulate the total cost
        }
        return totalCost; // Return the total cost
    }

    // Method to generate a random deck ID
    private static String generateDeckId() {
        Random rand = new Random(); // Create a Random object
        // Generate a random number and format it as a 9-digit string
        return String.format("%09d", rand.nextInt(1000000000));
    }

    // Method to print the report of the deck data
    private static void printReport(DeckData deckData) {
        System.out.println("Deck ID: " + deckData.deckId); // Print the deck ID
        System.out.println("Total Cost: " + deckData.totalCost); // Print the total cost
        System.out.println("Histogram of Costs:");

        // Print the histogram of costs from 0 to 6
        for (int i = 0; i <= 6; i++) {
            System.out.print(i + ": ");
            int count = deckData.costHistogram.getOrDefault(i, 0); // Get count for current cost
            for (int j = 0; j < count; j++) {
                System.out.print("*"); // Print an asterisk for each occurrence of the cost
            }
            System.out.println(); // New line after each cost row
        }

        // If there are invalid cards, print them out
        if (!deckData.invalidCards.isEmpty()) {
            System.out.println("Invalid Cards:");
            for (String invalidCard : deckData.invalidCards) {
                System.out.println(invalidCard); // Print each invalid card
            }
        }
    }

    // Method to generate a void report
    private static void generateVoidReport(String deckId) {
        String fileName = String.format("SpireDeck %s(VOID).pdf", deckId); // Format the void report filename
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("VOID"); // Write "VOID" message to the report
            System.out.println("Void report generated: " + fileName);
        } catch (IOException e) {
            System.out.println("Error creating void report: " + e.getMessage());
        }
    }

    // Class to hold deck data
    private static class DeckData {
        String deckId; // Unique ID for the deck
        int totalCost; // Total cost of all cards in the deck
        Map<Integer, Integer> costHistogram; // Histogram counting occurrences of each cost
        List<String> invalidCards; // List of invalid card entries
        int cardCount; // Total number of cards processed

        // Constructor to initialize DeckData fields
        DeckData(String deckId, int totalCost, Map<Integer, Integer> costHistogram, List<String> invalidCards, int cardCount) {
            this.deckId = deckId;
            this.totalCost = totalCost;
            this.costHistogram = costHistogram;
            this.invalidCards = invalidCards;
            this.cardCount = cardCount;
        }
    }
}
