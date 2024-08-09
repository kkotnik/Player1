import java.io.*;
import java.util.*;

public class Player {
    static BufferedWriter fileOut = null; //spr za pisanje v datoteko

    /*
        GAME DATA
    */
    public static int universeWidth; //sirina vesolja
    public static int universeHeight; // visina vesolja
    public static String myColor; //barva igralca

    //seznam planetov izbranih barv
    public static String[] bluePlanets;
    public static String[] cyanPlanets;
    public static String[] greenPlanets;
    public static String[] yellowPlanets;
    public static String[] neutralPlanets;

    //seznam fleetov dolocenih barv
    public static String[] blueFleets;
    public static String[] cyanFleets;
    public static String[] greenFleets;
    public static String[] yellowFleets;

    public static String bestAttribute; //najboljsi atribut
    public static int bestThreshold;  //najboljsi prag

    
    public static void main(String[] args) throws Exception {
        // Load the OneR model
        loadOneRModel("C:/Users/petra/Desktop/Batalja/Gamepack2v2_2024-01-10/2024-01-10/Player1/games/best_attributes.txt"); // pot do datoteke z best attributes
    
        try {
            Random rand = new Random(); // random poteze

            
            //  vsak loop while  je nekdo na vrsti.
            //game over stop while
          
          while (true) {
          
              // podatki vesolja -igra
                getGameState();
    
                /*
                    *********************************
                    LOGIC: figure out what to do with
                    your turn
                    *********************************
                */
    
                String[] myPlanets = new String[0]; //moji planeti  - zacetna vr 0
                String[] myFleets = new String[0];  //fleets - zacetna vr 0
                String targetPlayer = ""; //ciljaj igralca - 0

                //pridobi moje planete izbrane barve in napadaj druge 
                
                if (myColor.equals("blue")) {
                    myPlanets = bluePlanets; //nastavi myplanets na modre planete
                    myFleets = blueFleets;//tudi fleets modri
                    String[] potentialTargets = {"cyan", "green", "yellow", "neutral"};  //napadi naslednje barve
                    targetPlayer = potentialTargets[rand.nextInt(4)];  //izberi random cilj
                } 
    
                if (myColor.equals("cyan")) {
                    myPlanets = cyanPlanets;
                    myFleets = cyanFleets;
                    String[] potentialTargets = {"blue", "green", "yellow", "neutral"};
                    targetPlayer = potentialTargets[rand.nextInt(4)];
                } 
    
                if (myColor.equals("green")) {
                    myPlanets = greenPlanets;
                    myFleets = greenFleets;
                    String[] potentialTargets = {"cyan", "blue", "neutral"};
                    targetPlayer = potentialTargets[rand.nextInt(3)];
                } 
                
                if (myColor.equals("yellow")) {
                    myPlanets = yellowPlanets;
                    myFleets = yellowFleets;
                    String[] potentialTargets = {"cyan", "green", "blue", "neutral"};
                    targetPlayer = potentialTargets[rand.nextInt(4)];
                }
    
                /*
                    glede na izbrano barvo kot cilj najdi planete igralca
                */
                String[] targetPlayerPlanets = new String[0];
                if (targetPlayer.equals("blue")) {
                    targetPlayerPlanets = bluePlanets;//cilj modri planeti
                }
    
                if (targetPlayer.equals("cyan")) {
                    targetPlayerPlanets = cyanPlanets;
                }
    
                if (targetPlayer.equals("green")) {
                    targetPlayerPlanets = greenPlanets;
                }
    
                if (targetPlayer.equals("yellow")) {
                    targetPlayerPlanets = yellowPlanets;
                }
    
                if (targetPlayer.equals("neutral")) {
                    targetPlayerPlanets = neutralPlanets;
                }
    
                /*
                    ce targetplayer ima planete
                    jaz imam planete..napadem random planet
                */
                if (targetPlayerPlanets.length > 0 && myPlanets.length > 0) {
                        // Preveri ali ima enmy ima še planete in ali imamo mi planete iz katerih lahko napadamo
                    for (int i = 0; i < myPlanets.length; i++) {
                        String myPlanet = myPlanets[i]; // Pridobi ime mojega planeta
                        int randomEnemyIndex = rand.nextInt(targetPlayerPlanets.length); // Izberi random indeks cilja 
                        String randomTargetPlanet = targetPlayerPlanets[randomEnemyIndex]; // planet nasprotnika
                        
                        // napademo z oneR?
                        boolean shouldAttack = shouldAttack(myPlanet, randomTargetPlanet, bestAttribute, bestThreshold);
    
                        if (shouldAttack) {
                            // Napadi na z OneR
                            System.out.println("A " + myPlanet + " " + randomTargetPlanet);
                        } else {
                            // else random
                            if (rand.nextBoolean()) { // 50% možnost za random napad
                                System.out.println("A " + myPlanet + " " + randomTargetPlanet);
                            }
                        }
                    }
                }
                
                /*
                    - send a hello message to your teammate bot :)
                    - it will receive it from the game next turn (if the bot parses it)
                */
                System.out.println("M Hello");
    
                /*
                    - E will end my turn. 
                    - you should end each turn (if you don't the game will think you timed-out)
                    - after E you should send no more commands to the game
                */
                System.out.println("E");
            }
        } catch (Exception e) {
            logToFile("ERROR: ");
            logToFile(e.getMessage()); //izpisi napako v datoteko
            e.printStackTrace(); //izpisi sled
        }
        fileOut.close(); //zapri datoteko ce je odprta
    }
    

    /**
     * Loads the OneR model from a file.
     * @param fileName the file containing the OneR model data.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public static void loadOneRModel(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName)); // Odpri datoteko fileName - branje
        String line;
        while ((line = reader.readLine()) != null) { // Preberi do konca
            if (line.startsWith("Best Attribute: ")) { // ce se vrstica zacne z best attributes = best lastnost
                bestAttribute = line.substring(16).trim(); // Shrani najboljso lastnost v spr bestAttribute
            } else if (line.startsWith("Best Threshold: ")) { // Če vrstica Best Threshold =najboljši prag
                bestThreshold = Integer.parseInt(line.substring(16).trim()); // Shrani 
            }
        }
        reader.close();// Zapri reader
    }

    
    public static boolean shouldAttack(String myPlanet, String targetPlanet, String attribute, int threshold) {
       //pridobi vr za napad
        int attributeValue = getAttributeValue(myPlanet, targetPlanet, attribute);
        //true if 
        return attributeValue > threshold;
    }

    
    public static int getAttributeValue(String myPlanet, String targetPlanet, String attribute) {
        // implementacija glede na ime
        switch (attribute) {
            case "NumPlanets":
                return calculateNumPlanets(myPlanet, targetPlanet); //razlka v st planetov
            case "NumFleets":
                return calculateNumFleets(myPlanet, targetPlanet); //razlika v st flot
            default:
                return 0; //else
        }
    }

    
    public static int calculateNumPlanets(String myPlanet, String targetPlanet) {
        int myPlanets = 0;
        int targetPlanets = 0;

        // moja barva
        String myColor = getColorFromPlanet(myPlanet);

        // st mojih planetov
        switch (myColor) {
            case "blue":
                myPlanets = bluePlanets.length;
                break;
            case "cyan":
                myPlanets = cyanPlanets.length;
                break;
            case "green":
                myPlanets = greenPlanets.length;
                break;
            case "yellow":
                myPlanets = yellowPlanets.length;
                break;
        }

        // st enmy planeti
        String targetColor = getColorFromPlanet(targetPlanet);
        switch (targetColor) {
            case "blue":
                targetPlanets = bluePlanets.length;
                break;
            case "cyan":
                targetPlanets = cyanPlanets.length;
                break;
            case "green":
                targetPlanets = greenPlanets.length;
                break;
            case "yellow":
                targetPlanets = yellowPlanets.length;
                break;
            case "neutral":
                targetPlanets = neutralPlanets.length;
                break;
        }

        //razlika moji planeti -enmy
        return myPlanets - targetPlanets;
    }

    
    public static int calculateNumFleets(String myPlanet, String targetPlanet) {
        int myFleets = 0;
        int targetFleets = 0;

        // barva
        String myColor = getColorFromPlanet(myPlanet);

        // moj flot glede na barvo
        switch (myColor) {
            case "blue":
                myFleets = blueFleets.length;
                break;
            case "cyan":
                myFleets = cyanFleets.length;
                break;
            case "green":
                myFleets = greenFleets.length;
                break;
            case "yellow":
                myFleets = yellowFleets.length;
                break;
        }

        // enmy float
        String targetColor = getColorFromPlanet(targetPlanet);
        switch (targetColor) {
            case "blue":
                targetFleets = blueFleets.length;
                break;
            case "cyan":
                targetFleets = cyanFleets.length;
                break;
            case "green":
                targetFleets = greenFleets.length;
                break;
            case "yellow":
                targetFleets = yellowFleets.length;
                break;
        }

        //razlika
        return myFleets - targetFleets;
    }

    
    public static String getColorFromPlanet(String planet) {
        //check ce je planet v seznamu modrih
        if (Arrays.asList(bluePlanets).contains(planet)) {
            return "blue"; //vrni
        }
        if (Arrays.asList(cyanPlanets).contains(planet)) {
            return "cyan";
        }
        if (Arrays.asList(greenPlanets).contains(planet)) {
            return "green";
        }
        if (Arrays.asList(yellowPlanets).contains(planet)) {
            return "yellow";
        }
        return "neutral";
    }

    /**
     * This function should be used instead of System.out.print for 
     * debugging, since the System.out.println is used to send 
     * commands to the game
     * @param line String you want to log into the log file.
     * @throws IOException
     */
    //odpravljanje napak
    public static void logToFile(String line) throws IOException {
        if (fileOut == null) {
            FileWriter fstream = new FileWriter("Igralec.log"); //ustvari fileWriter v igralec
            fileOut = new BufferedWriter(fstream); //ustvari bfwriter za zapisovanje
        }
        if (line.charAt(line.length() - 1) != '\n') {
            line += "\n"; ///nova vrstica
        }
        fileOut.write(line); //zapisi vrstico v dat
        fileOut.flush(); //izpisi da se zagotovi, da se podatki zapisejo v dat
    }

    /**
     * This function should be called at the start of each turn to obtain information about the current state of the game.
     * The data received includes details about planets and fleets, categorized by color and type.
     *
     * This version of the function uses dynamic lists to store data about planets and fleets for each color,
     * accommodating for an unknown quantity of items. At the end of data collection, these lists are converted into fixed-size
     * arrays for consistent integration with other parts of the program.
     *
     * Feel free to modify and extend this function to enhance the parsing of game data to your needs.
     *
     * @throws NumberFormatException if parsing numeric values from the input fails.
     * @throws IOException if an I/O error occurs while reading input.
     */
    public static void getGameState() throws NumberFormatException, IOException {
        BufferedReader stdin = new BufferedReader(
            new java.io.InputStreamReader(System.in)
        ); 
        /*
            - this is where we will store the data received from the game,
            - Since we don't know how many planets/fleets each player will 
            have, we are using lists.
        */ 
        LinkedList<String> bluePlanetsList = new LinkedList<>();
        LinkedList<String> cyanPlanetsList = new LinkedList<>();
        LinkedList<String> greenPlanetsList = new LinkedList<>();
        LinkedList<String> yellowPlanetsList = new LinkedList<>();
        LinkedList<String> neutralPlanetsList = new LinkedList<>();

        LinkedList<String> blueFleetsList = new LinkedList<>();
        LinkedList<String> cyanFleetsList = new LinkedList<>();
        LinkedList<String> greenFleetsList = new LinkedList<>();
        LinkedList<String> yellowFleetsList = new LinkedList<>();

        
        /*
            ********************************
            read the input from the game and
            parse it (get data from the game)
            ********************************
            - game is telling us about the state of the game (who owns planets
            and what fleets/attacks are on their way). 
            - The game will give us data line by line. 
            - When the game only gives us "S", this is a sign
            that it is our turn and we can start calculating out turn.
            - NOTE: some things like parsing of fleets(attacks) is not implemented 
            and you should do it yourself
        */
        String line = "";
        /*
            Loop until the game signals to start playing the turn with "S"
        */ 
        while (!(line = stdin.readLine()).equals("S")) {
            /* 
                - save the data we receive to the log file, so you can see what 
                data is received form the game (for debugging)
            */ 
            logToFile(line); 
            
            String[] tokens = line.split(" ");
            char firstLetter = line.charAt(0);
            /*
                U <int> <int> <string> 						
                - Universe: Size (x, y) of playing field, and your color
            */
            if (firstLetter == 'U') {
                universeWidth = Integer.parseInt(tokens[1]);
                universeHeight = Integer.parseInt(tokens[2]);
                myColor = tokens[3];
            } 
            /*
                P <int> <int> <int> <float> <int> <string> 	
                - Planet: Name (number), position x, position y, 
                planet size, army size, planet color (blue, cyan, green, yellow or null for neutral)
            */
            if (firstLetter == 'P') {
                String planetName = tokens[1];
                if (tokens[6].equals("blue")) {
                    bluePlanetsList.add(planetName);
                } 
                if (tokens[6].equals("cyan")) {
                    cyanPlanetsList.add(planetName);
                } 
                if (tokens[6].equals("green")) {
                    greenPlanetsList.add(planetName);
                } 
                if (tokens[6].equals("yellow")) {
                    yellowPlanetsList.add(planetName);
                } 
                if (tokens[6].equals("null")) {
                    neutralPlanetsList.add(planetName);
                } 
            } 
        }
        /*
            - override data from previous turn
            - convert the lists into fixed size arrays
        */ 
        bluePlanets = bluePlanetsList.toArray(new String[0]);
        cyanPlanets = cyanPlanetsList.toArray(new String[0]);
        greenPlanets = greenPlanetsList.toArray(new String[0]);
        yellowPlanets = yellowPlanetsList.toArray(new String[0]);
        neutralPlanets = neutralPlanetsList.toArray(new String[0]);
        blueFleets = blueFleetsList.toArray(new String[0]);
        cyanFleets = cyanFleetsList.toArray(new String[0]);
        greenFleets = greenFleetsList.toArray(new String[0]);
        yellowFleets = yellowFleetsList.toArray(new String[0]);
    }
	private static void attackBasedOnNumPlanets(String[] myPlanets, String[] targetPlayerPlanets, String[] allyPlanets, Random rand) {
		//izracunaj st attack na podlagi st mojih planetov
		int numAttacks = Math.min(myPlanets.length, targetPlayerPlanets.length);

		

		for (int i = 0; i < numAttacks; i++) {
			String myPlanet = myPlanets[i % myPlanets.length];
			String targetPlanet = targetPlayerPlanets[i % targetPlayerPlanets.length];


			// napadi
			System.out.println("A " + myPlanet + " " + targetPlanet);
		}
	}
	private static void attackBasedOnNumFleets(String[] myPlanets, String[] targetPlayerPlanets, String[] allyFleets, Random rand) {
		// Določite število flot za namestitev
		int numFleets = Math.min(myPlanets.length + allyFleets.length, targetPlayerPlanets.length);

		/// Okrepi napade ali strateško razširite flote
		for (int i = 0; i < numFleets; i++) {
			String myPlanet = myPlanets[i % myPlanets.length];
			String targetPlanet = targetPlayerPlanets[i % targetPlayerPlanets.length];

			// napadi iz različnih smeri, da povečate učinek
			System.out.println("A " + myPlanet + " " + targetPlanet);

			}
	}

	private static void attackBasedOnPlanetColor(String[] myPlanets, String[] targetPlayerPlanets, Random rand) {
        // Določite strategijo glede na barvo
        for (String myPlanet : myPlanets) {
            for (String targetPlanet : targetPlayerPlanets) {
                // preveri prednost barve cilja
                if (myColorAdvantage(targetPlanet)) {
                    // Uporabite barvno prednost za napad
                    System.out.println("A " + myPlanet + " " + targetPlanet);
                }
            }
        }
}

// metoda prednosti barv
    private static boolean myColorAdvantage(String targetPlanet) {
    // Zelena ima prednost pred blue in cyan
        if (myColor.equals("green") && (targetPlanet.equals("blue") || targetPlanet.equals("cyan"))) {
            return true;
        }

    /// oranzna ima prednost pred blue in cyan
        if (myColor.equals("orange") && (targetPlanet.equals("cyan") || targetPlanet.equals("green"))) {
            return true;
        }

        return false;
    }


}