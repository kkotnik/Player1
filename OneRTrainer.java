import java.io.*;
import java.util.*;

public class OneRTrainer {

    public static void main(String[] args) throws IOException {
        // podatki iger
        String folderPath = "C:\\Users\\krist\\Desktop\\2024-01-10\\Player1\\games";
        // prazen list kamer se bo shranila data
        List<GameData> allGameDataList = new ArrayList<>();
        
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        
        if (files != null) { // failsafe da ne gledamo prazn file
            for (File file : files) { // vsak file objekt v files array
                if (file.isFile() && file.getName().endsWith(".txt")) { // iscemo txt
                    // branje in dodajanje
                    List<GameData> gameDataList = readGameData(file.getAbsolutePath()); 
                    allGameDataList.addAll(gameDataList);
                }
            }
        }
        
        // kam se shrani in kaj napise tocno
        OneRModel model = trainOneR(allGameDataList);
        String outputPath = folderPath + "\\best_attributes.txt";
        saveModel(model, outputPath);
        System.out.println("Best Attribute: " + model.bestAttribute + ", Best Threshold: " + model.bestThreshold);
    }

    public static List<GameData> readGameData(String fileName) throws IOException {
        List<GameData> gameDataList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        Map<String, Integer> colorScores = new HashMap<>();
        String winningColor = null;
        int highestScore = -1; // Keep track of the highest score to determine the winner

        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\\s+");

            // pridobi R da dobis rezultat vsake barve
            if (tokens[0].equals("R")) {
                if (tokens.length >= 3) {
                    int score = Integer.parseInt(tokens[1].trim());
                    String color = tokens[2].trim();
                    colorScores.put(color, score);

                    // doloci highestScore = zmaga
                    if (score > highestScore) {
                        highestScore = score;
                        winningColor = color;
                    }
                }
            } 

            // Handle 'P' lines for planet information
            else if (tokens[0].equals("P")) {
                if (tokens.length >= 7) {
                    int id = Integer.parseInt(tokens[1].trim());
                    int x = Integer.parseInt(tokens[2].trim());
                    int y = Integer.parseInt(tokens[3].trim());
                    double size = Double.parseDouble(tokens[4].trim());
                    int value = Integer.parseInt(tokens[5].trim());
                    String color = tokens[6].equals("null") ? null : tokens[6].trim();

                    // Determine if this planet's color is the winner
                    boolean isWinner = color != null && color.equals(winningColor);
                    gameDataList.add(new PlanetData(id, x, y, size, value, color, isWinner));
                }
            }
        }

        br.close();
        return gameDataList;
    }
    public static OneRModel trainOneR(List<GameData> gameDataList) {
        // Initialize variables
        String[] attributes = {"NumPlanets", "NumFleets"};
        double bestAccuracy = 0;
        String bestAttribute = null;
        int bestThreshold = 0;
    
        // Iterate over each attribute
        for (String attribute : attributes) {
            // Collect all unique values for the current attribute
            TreeSet<Integer> thresholds = new TreeSet<>();
            for (GameData data : gameDataList) {
                thresholds.add(getAttributeValue(data, attribute));
            }
            // Include minimum and maximum values to the thresholds
            if (!thresholds.isEmpty()) {
                int minValue = thresholds.first() - 1; // add a lower bound threshold
                int maxValue = thresholds.last() + 1;  // add an upper bound threshold
                thresholds.add(minValue);
                thresholds.add(maxValue);
            }
    
            // Evaluate each threshold for the current attribute
            for (int threshold : thresholds) {
                double accuracy = calculateAccuracy(gameDataList, attribute, threshold);
                System.out.println("Attribute: " + attribute + ", Threshold: " + threshold + ", Accuracy: " + accuracy);
                if (accuracy > bestAccuracy) {
                    bestAccuracy = accuracy;
                    bestAttribute = attribute;
                    bestThreshold = threshold;
                }
            }
        }
    
        return new OneRModel(bestAttribute, bestThreshold);
    }
    

    private static int getAttributeValue(GameData data, String attribute) {
        // gledamo za pravilen tip
        if (data instanceof PlanetData) {
            PlanetData planetData = (PlanetData) data;
            switch (attribute) {
                case "NumPlanets":
                    return planetData.id;
                case "NumFleets":
                    return planetData.value;
                case "PlanetColor": // ce je barva vrne hashcode barve
                    return planetData.color != null ? planetData.color.hashCode() : 0; 
                default: // failsafe, ce ne ujema ni validen ampak nebi smel bit problem? Upam?
                    throw new IllegalArgumentException("Invalid attribute: " + attribute);
            }
        }
        return 0;
    }

    private static double calculateAccuracy(List<GameData> gameDataList, String attribute, int threshold) {
        int correct = 0; // counter
        int total = 0; // total number of instances to check

        for (GameData data : gameDataList) {
            int value = getAttributeValue(data, attribute);
            boolean predictedWin = value >= threshold;
            if (data instanceof PlanetData) {
                PlanetData planetData = (PlanetData) data;
                total++;
                // Debug output
                //System.out.println("Attribute: " + attribute + ", Threshold: " + threshold + ", Value: " + value + ", Predicted Win: " + predictedWin + ", Actual Win: " + planetData.isWinner);
                if (predictedWin == planetData.isWinner) {
                    correct++;
                }
            }
        }
        return total == 0 ? 0 : (double) correct / total; // Avoid division by zero
    }

    private static void printValueDistribution(List<GameData> gameDataList, String attribute) {
        Map<Integer, Integer> valueCounts = new HashMap<>();
        for (GameData data : gameDataList) {
            int value = getAttributeValue(data, attribute);
            valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);
        }
        System.out.println("Value distribution for " + attribute + ": " + valueCounts);
    }

    public static void saveModel(OneRModel model, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false)); // zapis file
        // false da se prepise
        writer.write("Best Attribute: " + model.bestAttribute);
        writer.newLine();
        writer.write("Best Threshold: " + model.bestThreshold);
        writer.close();
    }
}

class GameData { // vsi podatki igre, kasneje bom ekstra extends za ostale atribute
    boolean isWinner;
}

class PlanetData extends GameData {
    // drugi atributi
    int id;
    int x;
    int y;
    double size;
    int value;
    String color;

    PlanetData(int id, int x, int y, double size, int value, String color, boolean isWinner) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
        this.value = value;
        this.color = color;
        this.isWinner = isWinner;
    }
}

class OneRModel {
    String bestAttribute;
    int bestThreshold;

    OneRModel(String bestAttribute, int bestThreshold) {
        this.bestAttribute = bestAttribute;
        this.bestThreshold = bestThreshold;
    }
}
