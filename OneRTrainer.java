import java.util.ArrayList;
import java.util.List;
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
        
        //kam se shrani in kaj napise tocno
        OneRModel model = trainOneR(allGameDataList);
        String outputPath = folderPath + "\\best_attributes.txt";
        saveModel(model, outputPath);
        System.out.println("Best Attribute: " + model.bestAttribute + ", Best Threshold: " + model.bestThreshold);
    }
    class GameData { //vsi podatki igre, kasneje bom ekstra extends za ostale atribute
        boolean isWinner;
    }
      public void loadPastGameData(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            int numPlanets = Integer.parseInt(tokens[0]);
            int numFleets = Integer.parseInt(tokens[1]);
            int totalArmySize = Integer.parseInt(tokens[2]);
            String action = tokens[3];
            pastGameStates.add(new GameState(numPlanets, numFleets, totalArmySize, action));
        }
        reader.close();
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