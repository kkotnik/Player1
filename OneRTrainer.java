import java.io.File;
import java.io.IOException;
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