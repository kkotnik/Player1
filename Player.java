import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

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

    private static String bestAttribute; //najboljsi atribut
    private static double bestThreshold; //najboljsi prag

	public static void main(String[] args) throws Exception {
        String filePath = "best_attributes.txt"; // pot do datoteke z best attributes

		try {
            Random rand = new Random(); // random poteze

            
              //  vsak loop while  je nekdo na vrsti.
              //game over stop while
            
            while (true) {
            
                // podatki vesolja -igra
                
                getGameState();

                

                String[] myPlanets = new String[0]; //moji planeti  - zacetna vr 0
                String[] myFleets = new String[0];  //fleets - zacetna vr 0
                String targetPlayer = ""; //ciljaj igralca - 0

                //pridobi moje planete izbrane barve in napadaj druge

                if (myColor.equals("blue")) {
                    myPlanets = bluePlanets; //nastavi myplanets na modre planete
                    myFleets = blueFleets;//tudi fleets modri
                    String[] potentialTargets = {"cyan", "green", "yellow", "neutral"}; //napadi naslednje barve
                    targetPlayer = potentialTargets[rand.nextInt(4)]; //izberi random cilj
                } else if (myColor.equals("cyan")) {
                    myPlanets = cyanPlanets;
                    myFleets = cyanFleets;
                    String[] potentialTargets = {"blue", "green", "yellow", "neutral"};
                    targetPlayer = potentialTargets[rand.nextInt(4)];
                } else if (myColor.equals("green")) {
                    myPlanets = greenPlanets;
                    myFleets = greenFleets;
                    String[] potentialTargets = {"cyan", "blue", "yellow", "neutral"};
                    targetPlayer = potentialTargets[rand.nextInt(4)];
                } else if (myColor.equals("yellow")) {
                    myPlanets = yellowPlanets;
                    myFleets = yellowFleets;
                    String[] potentialTargets = {"cyan", "green", "blue", "neutral"};
                    targetPlayer = potentialTargets[rand.nextInt(4)];
                }

                /*
                    glede na izbrano barvo kot cilj najdi planete igralca
                */
                String[] targetPlayerPlanets = new String[0];
                switch (targetPlayer) {
                    case "blue":
                        targetPlayerPlanets = bluePlanets; //cilj modri planeti
                        break;
                    case "cyan":
                        targetPlayerPlanets = cyanPlanets;
                        break;
                    case "green":
                        targetPlayerPlanets = greenPlanets;
                        break;
                    case "yellow":
                        targetPlayerPlanets = yellowPlanets;
                        break;
                    case "neutral":
                        targetPlayerPlanets = neutralPlanets;
                        break;
                }

                /*
                    ce targetplayer ima planete
                    jaz imam planete..napadem random planet
                */
                if (targetPlayerPlanets.length > 0 && myPlanets.length > 0) {
                    for (String myPlanet : myPlanets) {
                        int randomEnemyIndex = rand.nextInt(targetPlayerPlanets.length); //random indeks ciljnega planeta
                        String randomTargetPlanet = targetPlayerPlanets[randomEnemyIndex]; ///pridobi ta planet
                        
                        System.out.println("A " + myPlanet + " " + randomTargetPlanet); //izvedi napad
                    }
                }

                
                System.out.println("M Hello");

                
                System.out.println("E"); //koncaj potezo

				// Implementacija logike glede na bestAttribute in bestThreshold
                if (bestAttribute != null) {
                    switch (bestAttribute) {
                        case "NumPlanets":
                            // logika glede na št planetov
                            if (myPlanets.length >= bestThreshold) {
                                // izvedi igro glede na prag
                                attackBasedOnNumPlanets(myPlanets, targetPlayerPlanets, rand);
                            }
                            break;
                        case "NumFleets":
                            // Logika st float
                            if (myFleets.length >= bestThreshold) {
                                // igra glede na prag
                                attackBasedOnNumFleets(myPlanets, targetPlayerPlanets, rand);
                            }
                            break;
                        case "PlanetColor":
                            // Logika glede barve planetov
                            if (myColor.equals("blue") && bestThreshold > 0.5) {
                                
                                attackBasedOnPlanetColor(myPlanets, targetPlayerPlanets, rand);
                            }
                            break;
                        default:
                            // ostalo
                            break;
                    }
                }
			}
		} catch (Exception e) {
            logToFile("ERROR: " + e.getMessage()); //izpisi napako v datoteko
            e.printStackTrace(); //izpisi sled
        } finally {
            if (fileOut != null) {
                fileOut.close(); //zapri datoteko ce je odprta
            }
        }
    }


	/**
	 * This function should be used instead of System.out.print for 
	 * debugging, since the System.out.println is used to send 
	 * commands to the game
	 * @param line String you want to log into the log file.
	 * @throws IOException
	 */
	public static void logToFile(String line) throws IOException {
		if (fileOut == null) {
			FileWriter fstream = new FileWriter("Igralec.log");
			fileOut = new BufferedWriter(fstream);
		}
		if (line.charAt(line.length() - 1) != '\n') {
			line += "\n";
		}
		fileOut.write(line);
		fileOut.flush();
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
			- this is where we will store the data recieved from the game,
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
			- game is telling us about the state of the game (who ows planets
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
				- save the data we recieve to the log file, so you can see what 
				data is recieved form the game (for debugging)
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
				String plantetName = tokens[1];
				if (tokens[6].equals("blue")) {
					bluePlanetsList.add(plantetName);
				} 
				if (tokens[6].equals("cyan")) {
					cyanPlanetsList.add(plantetName);
				} 
				if (tokens[6].equals("green")) {
					greenPlanetsList.add(plantetName);
				} 
				if (tokens[6].equals("yellow")) {
					yellowPlanetsList.add(plantetName);
				} 
				if (tokens[6].equals("null")) {
					neutralPlanetsList.add(plantetName);
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

	//Izvedi potek napada glede na st planetov
    private static void attackBasedOnNumPlanets(String[] myPlanets, String[] targetPlayerPlanets, Random rand) {
	 // izvedi logiko 
	 for (String myPlanet : myPlanets) {
		int randomEnemyIndex = rand.nextInt(targetPlayerPlanets.length); //random indeks ciljnega planeta
		String randomTargetPlanet = targetPlayerPlanets[randomEnemyIndex]; //pridobi planet
		System.out.println("A " + myPlanet + " " + randomTargetPlanet); //izvedi napad
	 	}
	}

	 //Izvedi potek napada glede na st fleets
	 private static void attackBasedOnNumFleets(String[] myPlanets, String[] targetPlayerPlanets, Random rand) {
		//gleets
		for (String myPlanet : myPlanets) {
            int randomEnemyIndex = rand.nextInt(targetPlayerPlanets.length);
            String randomTargetPlanet = targetPlayerPlanets[randomEnemyIndex];
            System.out.println("A " + myPlanet + " " + randomTargetPlanet);
        } 
	}

	//Izvedi potek napada glede na barvo planeta
    private static void attackBasedOnPlanetColor(String[] myPlanets, String[] targetPlayerPlanets, Random rand) {
	 // rand barva planeta
	 for (String myPlanet : myPlanets) {
		int randomEnemyIndex = rand.nextInt(targetPlayerPlanets.length); //random indeks
		String randomTargetPlanet = targetPlayerPlanets[randomEnemyIndex]; //cilj
		System.out.println("A " + myPlanet + " " + randomTargetPlanet); //izvedi napad
	}
	}
}
