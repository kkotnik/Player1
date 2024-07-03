public static OneRModel trainOneR(List<GameData> gameDataList) {
    String[] attributes = {"NumPlanets", "NumFleets", "PlanetColor"}; //atributi ki jih bomo uporabili
    double bestAccuracy = 0; //kaj iscemo
    String bestAttribute = null;
    int bestThreshold = 0;


    return new OneRModel(bestAttribute, bestThreshold);
}
