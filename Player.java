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
        readBestAttributes(filePath); // preberi best attributes iz datoteke

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

                
            //    System.out.println("M Hello");

                
                //System.out.println("E"); //koncaj potezo

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
                System.out.println("M Hello");
                System.out.println("E"); // End turn
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
//------
    
    //preberi datoteko 
    // izpisi pot do datoteke
    // exception ce napaka
    private static void readBestAttributes(String filePath) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath)); //odpri datoteko za branje
            String line = reader.readLine(); //preberi prvo vrstico
            if (line != null && line.startsWith("Best Attribute:")) {
                String[] parts = line.split(":"); //razdeli vrstico po :
                if (parts.length >= 2) {
                    bestAttribute = parts[1].trim(); //pridobi atribut
                    if (parts.length >= 3) {
                        bestThreshold = Double.parseDouble(parts[2].trim()); //pridobi prag
                    }
                }
            }
        } finally {
            if (reader != null) {
                reader.close(); //zapri reader
            }
        }
    }

    
    public static void getGameState() throws NumberFormatException, IOException {
        BufferedReader stdin = new BufferedReader(
            new java.io.InputStreamReader(System.in)
        );
        /*
            shranimo podatke, prejete iz igre
            uporabljamo seznamo ker nevemo st planetov/fleet
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

        
        //preberemo vnos igre in razclenimo podatke
        for (String line; (line = stdin.readLine()) != null; ) {
            //prazna vrstica == konec igre
            logToFile(line); //zapisi prejeto vrstico v datoteko za napake

            String[] tokens = line.split(" ");
            char firstLetter = line.charAt(0);

            /*
                U <int> <int> <string>
                - Universe: Size (x, y) of playing field, and your color
            */
            if (firstLetter == 'U') {
                universeWidth = Integer.parseInt(tokens[1]); //n<stavi sirino vesolja
                universeHeight = Integer.parseInt(tokens[2]);//nastavi visino
                myColor = tokens[3]; //nastavi barvo igralca
            }
            /*
                P <int> <int> <int> <float> <int> <string>
                - Planet: Name (number), position x, position y,
                planet size, army size, planet color (blue, cyan, green, yellow or null for neutral)
            */
            else if (firstLetter == 'P') {
                String planetName = tokens[1]; //ime planeta
                String planetColor = tokens[6]; //barva planeta

                switch (planetColor) {
                    case "blue":
                        bluePlanetsList.add(planetName); //dodaj planet v modri seznam
                        break;
                    case "cyan":
                        cyanPlanetsList.add(planetName);
                        break;
                    case "green":
                        greenPlanetsList.add(planetName);
                        break;
                    case "yellow":
                        yellowPlanetsList.add(planetName);
                        break;
                    case "null":
                        neutralPlanetsList.add(planetName);
                        break;
                }
            }
            /*
                F <int> <int> <int> <string> <int> <string>
                - Fleets: (Size of fleet, number of turns left, origianation of fleet (name of planet), destination (name of planet), is this my fleet or the enemies)
            */
            else if (firstLetter == 'F') {
                String fleetOwner = tokens[5]; //owner fleet
                String fleetDestination = tokens[4]; //cilj fleet

                switch (fleetOwner) {
                    case "blue":
                        blueFleetsList.add(fleetDestination); //dodaj cilj floate v moder seznam
                        break;
                    case "cyan":
                        cyanFleetsList.add(fleetDestination);
                        break;
                    case "green":
                        greenFleetsList.add(fleetDestination);
                        break;
                    case "yellow":
                        yellowFleetsList.add(fleetDestination);
                        break;
                }
            }
        }

        /*
           prepisi podatke iz prejsnje poteze, seznam v koncno velikost
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

    //Izvedi potek napada glede na st planetov
   /* private static void attackBasedOnNumPlanets(String[] myPlanets, String[] targetPlayerPlanets, Random rand) {
        // izvedi logiko 
        for (String myPlanet : myPlanets) {
            int randomEnemyIndex = rand.nextInt(targetPlayerPlanets.length); //random indeks ciljnega planeta
            String randomTargetPlanet = targetPlayerPlanets[randomEnemyIndex]; //pridobi planet
            System.out.println("A " + myPlanet + " " + randomTargetPlanet); //izvedi napad
        }
    }

    //Izvedi potek napada glede na st fleets
    private static void attackBasedOnNumFleets(String[] myPlanets, String[] targetPlayerPlanets, Random rand) {
        // Implement logic for attacking based on number of fleets
        for (String myPlanet : myPlanets) {
            int randomEnemyIndex = rand.nextInt(targetPlayerPlanets.length);
            String randomTargetPlanet = targetPlayerPlanets[randomEnemyIndex];
            System.out.println("A " + myPlanet + " " + randomTargetPlanet);
        }
    }

    //Izvedi potek napada glede na barvo planeta
    private static void attackBasedOnPlanetColor(String[] myPlanets, String[] targetPlayerPlanets, Random rand) {
        // igra gleets
        for (String myPlanet : myPlanets) {
            int randomEnemyIndex = rand.nextInt(targetPlayerPlanets.length); //random indeks
            String randomTargetPlanet = targetPlayerPlanets[randomEnemyIndex]; //cilj
            System.out.println("A " + myPlanet + " " + randomTargetPlanet); //izvedi napad
        }
    }
        */
        private static void attackBasedOnNumPlanets(String[] myPlanets, String[] targetPlayerPlanets, String[] allyPlanets, Random rand) {
            //izracunaj st attack na podlagi st mojih planetov
            int numAttacks = Math.min(myPlanets.length, targetPlayerPlanets.length);
        
            //Prednost napada šibkejsim planetom
            Arrays.sort(targetPlayerPlanets, Comparator.comparingInt(Player::getPlanetStrength));
        
            for (int i = 0; i < numAttacks; i++) {
                String myPlanet = myPlanets[i % myPlanets.length];
                String targetPlanet = targetPlayerPlanets[i % targetPlayerPlanets.length];
        
                // Coordinate attacks with ally (orange)
           /*      if (i % 2 == 0 && allyPlanets.length > 0) {
                    String allyPlanet = allyPlanets[i % allyPlanets.length];
                    System.out.println("A " + allyPlanet + " " + targetPlanet);
                }
                */ 
        
                // Concentrate attacks on selected target
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
        
                // Use ally fleets to reinforce attacks
             /*    if (i < allyFleets.length) {
                    String allyFleet = allyFleets[i];
                    System.out.println("A " + allyFleet + " " + targetPlanet);
                }
                    */
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

