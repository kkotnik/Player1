import java.io.*;
import java.util.*;

public class OneRTrainer {

    public static void main(String[] args) throws IOException {
        //podatki iger
        String folderPath = "C:\\Users\\krist\\Desktop\\2024-01-10\\Player1\\games";
        //prazen list kamer se bo shranila data
        List<GameData> allGameDataList = new ArrayList<>();
        
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        
        if (files != null) { //failsafe da ne gledamo prazn file
            for (File file : files) { //vsak file objekt v files array
                if (file.isFile() && file.getName().endsWith(".txt")) { //iscemo txt
                    //branje in dodajanje
                    List<GameData> gameDataList = readGameData(file.getAbsolutePath()); 
                    allGameDataList.addAll(gameDataList);
                }
            }
        }
        
        //kam se shrani in kaj napise tocno
        OneRModel model = trainOneR(allGameDataList);
        String outputPath = folderPath + "\\best_attributes.txt";
        saveModel(model, outputPath);
        System.out.println("Best Attribute: " + model.bestAttribute + ", Best Threshold: " + model.bestThreshold);
    }

    public static List<GameData> readGameData(String fileName) throws IOException {
        //inicializacija game data in buffered reader
        List<GameData> gameDataList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        //shramba za win in lose barv v hash
        Map<String, Boolean> colorResults = new HashMap<>();
        while ((line = br.readLine()) != null) { //do zadnje vrstice beri 
            String[] tokens = line.split("\\s+"); //tokenizacija

            //gledamo ce je C ali P prva stvar v text filu, ko je C imamo 5 "tokenov"
            if (tokens[0].equals("C")) {
                //status ce je zmaga
                if (tokens.length >= 5) {
                    String color = tokens[3].trim();
                    boolean isWinner = tokens[4].trim().equals("1");
                    colorResults.put(color, isWinner);
                }
            } else if (tokens[0].equals("P")) {
                if (tokens.length >= 7) {
                    //tokenizacija podatkov planetov, dodano v game data list
                    int id = Integer.parseInt(tokens[1].trim());
                    int x = Integer.parseInt(tokens[2].trim());
                    int y = Integer.parseInt(tokens[3].trim());
                    double size = Double.parseDouble(tokens[4].trim());
                    int value = Integer.parseInt(tokens[5].trim());
                    String color = tokens[6].equals("null") ? null : tokens[6].trim();
                    boolean isWinner = color != null && colorResults.getOrDefault(color, false);
                    gameDataList.add(new PlanetData(id, x, y, size, value, color, isWinner));
                }
            }
        }
        //brez ne dela, zakaj? Nism sure
        //"Pravilno zapiranje datoteke"
        br.close();
        return gameDataList;
    }

    public static OneRModel trainOneR(List<GameData> gameDataList) {
        //inicializacija 
        String[] attributes = {"NumPlanets", "NumFleets", "PlanetColor"};
        double bestAccuracy = 0;
        String bestAttribute = null;
        int bestThreshold = 0;

        //vsak unikaten threshold se shrani
        for (String attribute : attributes) {
            TreeSet<Integer> thresholds = new TreeSet<>();
            for (GameData data : gameDataList) {
                //thresholdu doda vrednost
                thresholds.add(getAttributeValue(data, attribute));
            }

            //classic min max scena, zamenjaj ce je boljsi accuracy/threshold/attribute
            for (int threshold : thresholds) {
                double accuracy = calculateAccuracy(gameDataList, attribute, threshold);
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
        //gledamo za pravilen tip
        if (data instanceof PlanetData) {
            PlanetData planetData = (PlanetData) data;
            switch (attribute) {
                case "NumPlanets":
                    return planetData.id;
                case "NumFleets":
                    return planetData.value;
                case "PlanetColor": //ce je barva vrne 1 cene 0
                    return planetData.color != null && planetData.color.equals("someColor") ? 1 : 0; 
                default: //failsafe, ce ne ujema ni validen ampak nebi smel bit problem? Upam?
                    throw new IllegalArgumentException("Invalid attribute: " + attribute);
            }
        }
        return 0;
    }

    private static double calculateAccuracy(List<GameData> gameDataList, String attribute, int threshold) {
        int correct = 0; //counter
        for (GameData data : gameDataList) {
            int value = getAttributeValue(data, attribute);
            //kalkulacija natancnosti
            boolean predictedWin = value >= threshold;
            if (data instanceof PlanetData) {
                PlanetData planetData = (PlanetData) data;
                if (predictedWin == planetData.isWinner) {
                    correct++;
                }
            }
        }
        return (double) correct / gameDataList.size();
    }

    public static void saveModel(OneRModel model, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false)); //zapis file
        //false da se prepise
        writer.write("Best Attribute: " + model.bestAttribute);
        writer.newLine();
        writer.write("Best Threshold: " + model.bestThreshold);
        writer.close();
    }
}

class GameData { //vsi podatki igre, kasneje bom ekstra extends za ostale atribute
    boolean isWinner;
}

class PlanetData extends GameData {
//drugi atributi
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
