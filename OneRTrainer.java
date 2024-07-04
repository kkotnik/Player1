import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.io.*;

public class OneRTrainer{
    private Random rand;

    public static void main(String[] args) throws IOException {
        //podatki iger
        String folderPath = "C:\\Users\\krist\\Desktop\\2024-01-10\\Player1\\games";

        //prazen list kamer se bo shranila data
        //GameData bom ze pol naredu
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
        
}