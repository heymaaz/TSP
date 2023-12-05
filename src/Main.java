import java.util.Scanner;
import java.io.File;
public class Main {
    /**
     * Author: Maaz Chowdhry
     * Student Number: M00910300
     * Course:  Artificial Intelligence
     * To run the program, add the path to the file you want to run
     * The path should can be relative or absolute and should be in the format: C:/Users/Maaz/Desktop/trainFiles/input.txt or trainFiles/input.txt
     * The input file should be in the format:
     * city id, x coordinate, y coordinate
     * 1 6734 1453
     * 2 2233 10
     * 3 5530 1424
     * 4 401 841
     */
    //Initialize the variables to be used in the program (static so that they can be used in the methods) (final so that they cannot be changed)
    static double[][] distanceMatrix;//For the adjacency matrix to store the distance between each city (to not have to calculate the distance between each city every time it is needed)
    static final int POWER_OF_TWO = 2;//For the x^2 y^2 in the distance formula
    static final int ROUND_TO_FOUR_PLACES = 4;//For the number of decimal places to round to
    static final int BEST_FITNESS_INDEX = 0;//For the best fitness value (distance)
    static final int CITY_COLUMN_LENGHT = 3;//For the number of columns in the cities 2d array
    static final int CITY_ID = 0;//For the city id
    static final int X_COORDINATE = 1;//For the x coordinate of the city
    static final int Y_COORDINATE = 2;//For the y coordinate of the city
    static final int POPULATION_SIZE = 150;//For the size of the population (number of paths)
    static final int TOP_IF_NOT_BEST = 5;//For the number of paths to mutate if the path is the best path
    static final int TOP_IF_BEST = 10;//For the number of paths to mutate if the path is not the best path
    static final int REINITIALISE_POPULATION_AT = 1000;//For the generation limit of 1000 to reinitialize the population and fitness every 1000 generations
    static final int GENERATION_LIMIT_FOR_BIG_CITY = 150; //For the generation limit to stop the algorithm for big cities (city lenght greater than 30)
    static final int GENERATION_LIMIT = 1000000;//For the generation limit of 1,000,000 to stop the algorithm
    
    static final int NUMBER_OF_POPULATIONS = 2;//For the train and first 3 test files make this 5, for the fourth test file make this 2
    static final boolean STOP_EARLY = true;//MAKE THIS TRUE FOR THE FOURTH TEST FILE

    //Uncomment the input file you want to run or add the path to the file you want to run
    //static final String INPUT_FILE_NAME = "C:\\Users\\mc2098\\eclipse-workspace\\Coursework 1\\src\\trainFiles/train1.txt";
    //static final String INPUT_FILE_NAME = "C:\\Users\\mc2098\\eclipse-workspace\\Coursework 1\\src\\trainFiles/train2.txt";
    //static final String INPUT_FILE_NAME = "C:\\Users\\mc2098\\eclipse-workspace\\Coursework 1\\src\\trainFiles/train3.txt";
    //static final String INPUT_FILE_NAME = "C:\\Users\\mc2098\\eclipse-workspace\\Coursework 1\\src\\trainFiles/sample1-22.txt";
    //static final String INPUT_FILE_NAME = "C:\\Users\\mc2098\\eclipse-workspace\\Coursework 1\\src\\trainFiles/sample2-22.txt";
    //static final String INPUT_FILE_NAME = "C:\\Users\\mc2098\\eclipse-workspace\\Coursework 1\\src\\trainFiles/sample3-22.txt";
    static final String INPUT_FILE_NAME = "src/trainFiles/sample4-22.txt";
    public static void main(String[] args) {
        long startTime = System.nanoTime();//Start time
        int[][] cities = input();//Get the cities from the file
        //display(cities);//(uncomment to display the cities)
        initDistanceMatrix(cities);//Initialize the adjacency matrix
        double shortestPath = initGeneticAlgo(cities);//Run the genetic algorithm
        long endTime = System.nanoTime();//End time
        
        //Calculate the duration and display it
        String duration = String.format("%d min, %d sec, %d ms, %d ns", 
            ((endTime - startTime) / 1000) / 60/1000000, ((endTime - startTime) / 1000)/1000000 % 60, (endTime - startTime)/1000000 % 1000, (endTime - startTime) % 1000000);
        System.out.println("Time: " + duration);//Display the duration
        System.out.println("Time: " + (endTime - startTime)+" ns");//Display the duration in nanoseconds
        System.out.println("Time x Shortest Path^2 :" + (endTime - startTime)*shortestPath*shortestPath);//Display the duration in nanoseconds multiplied by the shortest path squared
        
    }
    
    static void initDistanceMatrix(int[][] cities){
        /*
            Initialize the adjacency matrix
            The adjacency matrix is a 2d array of doubles
            The adjacency matrix is a square matrix with the number of rows and columns equal to the number of cities
            The adjacency matrix is symmetric
            It stores the distance between each city
            It helps to not have to calculate the distance between each city every time it is needed (it is needed a lot)
        */
        distanceMatrix = new double[cities.length][cities.length];
        for (int currentCity = 0; currentCity < cities.length; currentCity++) {
            for (int nextCity = currentCity; nextCity < cities.length; nextCity++) {
                if(currentCity==nextCity)
                    distanceMatrix[currentCity][nextCity] = 0;
                else{
                    distanceMatrix[currentCity][nextCity] = distanceMatrix[nextCity][currentCity] = calculateDistance(cities[currentCity], cities[nextCity]);//Calculate the distance between the cities
                }
            }
        }
    }
    
    static int[][] initPopulation(int[][] population)
    {
        /*
         * Initialize the population
         * The population is a 2d array of ints
         * The population stores the different paths (each row is a path)
         * Each path starts with the same city (the first city)
         * Each path contains each city exactly once (except the first city)
         * Each path ends with the same city (the first city)
         * The population is sorted by fitness (distance)
         * The population is initialized by putting the city id in each column of each row
         * The cities are shuffled to make unique paths
         */
        for (int pathIndex = 0; pathIndex < population.length; pathIndex++) {
            for (int cityIndex = 0; cityIndex < population[pathIndex].length; cityIndex++) {
                population[pathIndex][cityIndex] = cityIndex;
            }
            shuffle(population[pathIndex]);//Shuffle the cities
        }
        return population;
    }
    
    static double[] initFitness(double[] fitness, int[][] population)
    {
        /*
         * Initialize the fitness
         * The fitness is a 1d array of doubles
         * The fitness stores the distance of each path in the population
         * The fitness is sorted in ascending order
         * The fitness is used to sort the population
         * The fitness is calculated by adding the distance of each path in the population
         * The fitness is calculated using the distance formula
         */
        for (int pathIndex = 0; pathIndex < population.length; pathIndex++) {
            fitness[pathIndex] = distance(population[pathIndex]);
        }
        return fitness;
    }
    
    static double calculateDistance(int[] city1, int[] city2) {
        /*
         * Calculate the distance between two cities
         * The distance is calculated using the distance formula
         * The distance formula is sqrt((x2-x1)^2+(y2-y1)^2)
         */
        return Math.sqrt(Math.pow(city2[X_COORDINATE] - city1[X_COORDINATE], POWER_OF_TWO) + Math.pow(city2[Y_COORDINATE] - city1[Y_COORDINATE], POWER_OF_TWO));//Distance formula using adjacency matrix
    }
    static int min(double[][] fitness){
        /*
         * This function is used to get the index of the minimum fitness value (distance)
         */
        double min = Double.MAX_VALUE;
        int minIndex = -1;
        for (int fitnessIndex = 0; fitnessIndex < fitness.length; fitnessIndex++) {
            if(fitness[fitnessIndex][BEST_FITNESS_INDEX]<min)
            {
                minIndex = fitnessIndex;
                min = fitness[fitnessIndex][BEST_FITNESS_INDEX];
            }
        }
        return minIndex;
    }
    static int[][][] initializePopulation(int[][][] population){
        //Initialize the population using the initPopulation function
        for (int populationIndex = 0; populationIndex < population.length; populationIndex++) {
            initPopulation(population[populationIndex]);
        }
        return population;
    }
    static double[][] initializeFitness(double[][] fitness, int[][][] population){
        //Initialize the fitness using the initFitness function
        for (int fitnessIndex = 0; fitnessIndex < fitness.length; fitnessIndex++) {
            initFitness(fitness[fitnessIndex], population[fitnessIndex]);
        }
        return fitness;
    }
    static void displayResult(String ifStop, int[] bestPath, double bestDistance, int generation){
        /*
         * This function is used to display the results of the algorithm
         * It displays the best path, best distance, and generation at which the algorithm stopped
         */
        System.out.println(ifStop+"Best Path: ");//Display the best path
        for (int cityIdIndex = 0; cityIdIndex < bestPath.length; cityIdIndex++) {
            System.out.print((1+bestPath[cityIdIndex]) + " ");
        }
        System.out.println(1+bestPath[0]);
        System.out.println("Best Distance: " + bestDistance);//Display the best distance
        System.out.println("Generations: " + generation);//Display the generation at which the algorithm stopped
    }
    static void displayGeneration(int generation, double bestDistance, int count){
        /*
         * This function is used to display the generation, best distance, and number of agreeble contenders
         */
        System.out.println("Generation: "+generation);//Display the generation
        System.out.println("Best Distance: "+bestDistance);//Display the best distance
        System.out.println("Agreeble contenders: "+count);//Display the number of agreeble contenders
    }
     static double initGeneticAlgo(int[][] cities){
        int[][][] population = initializePopulation(new int[NUMBER_OF_POPULATIONS][POPULATION_SIZE][cities.length]);
        double[][] fitness = initializeFitness(new double[NUMBER_OF_POPULATIONS][POPULATION_SIZE],population);
        int[] bestPath = new int[cities.length];//The best path
        double bestDistance = Double.MAX_VALUE;//The best distance
        for(int populationIndex = 0; populationIndex<NUMBER_OF_POPULATIONS; populationIndex++)//For each population
            sort(population[populationIndex], fitness[populationIndex]);//Sort the population by fitness (distance
        int generation = 0;//to keep track of the generation
        boolean dontStop = true;//Flag to stop the algorithm
        String ifStop = "";//To display why the algorithm stopped
        return geneticAlgo(cities, population, fitness, bestPath, bestDistance, generation, dontStop, ifStop);//Run the genetic algorithm
     }
     static double geneticAlgo(int[][] cities, int[][][] population, double[][] fitness, int[] bestPath, double bestDistance, int generation, boolean dontStop, String ifStop){
        int countOfAgreeingPopulations=0;//To keep track of how many contenders agree
        while(dontStop && countOfAgreeingPopulations!=population.length){
            countOfAgreeingPopulations=0;//Reset the count of agreeble contenders
            bestDistance =fitness[min(fitness)][BEST_FITNESS_INDEX];//Get the best distance from all the populations
            for(int populationIndex = 0; populationIndex<population.length; populationIndex++){//For each population
                if(bestDistance==fitness[populationIndex][BEST_FITNESS_INDEX]){//If the best distance is the best distance of the population
                    countOfAgreeingPopulations++;//increment the count
                    mutate(population[populationIndex],fitness[populationIndex],TOP_IF_BEST);//Mutate the population by swapping 3 cities from the top {TOP_IF_BEST} paths
                }
                else{
                    if(generation%REINITIALISE_POPULATION_AT==(REINITIALISE_POPULATION_AT-1))//Every 1000 generations, reinitialize the population and fitness 
                    {
                        initPopulation(population[populationIndex]);//Reinitialize the population
                        initFitness(fitness[populationIndex],population[populationIndex]);//Reinitialize the fitness
                        sort(population[populationIndex], fitness[populationIndex]);//Sort the population by fitness
                    }
                    mutate(population[populationIndex],fitness[populationIndex],TOP_IF_NOT_BEST);//Mutate the population by swapping 3 cities from the top {TOP_IF_NOT_BEST} paths
                }
            }
            displayGeneration(generation++,bestDistance,countOfAgreeingPopulations);//Display the generation, best distance, and number of agreeble contenders
            if(STOP_EARLY&&generation>GENERATION_LIMIT_FOR_BIG_CITY){//If the number of cities is big then stop the algorithm early
                dontStop=false;
                ifStop="Stopped because of generation limit of "+GENERATION_LIMIT_FOR_BIG_CITY+" reached for city lenght:"+cities.length+".\n";
            }
            if(generation>GENERATION_LIMIT){//If the generation is greater than GENERATION_LIMIT, stop the algorithm
                dontStop=false;
                ifStop="Stopped because of generation limit of "+GENERATION_LIMIT+" reached.\n";
            }
        }
        bestPath = population[min(fitness)][0];//Get the best path from the populations
        displayResult(ifStop,bestPath,bestDistance,generation);//Display the results of the algorithm
        return bestDistance;//Return the best distance
    }
    static void mutate(int[][] population, double[] fitness, int top){
        /*
         * Mutate the population
         * The top paths are not mutated
         * The rest of the population is mutated by swapping 3 cities
         * The 3 cities are randomly selected (except the start city)
         * The 3 cities are swapped to mutate the path
         * The fitness is recalculated for the mutated path
         * The population is sorted by fitness
         */
        for(int populationIndex = top; populationIndex<population.length; populationIndex++){//For the rest of the population (after the top paths)
            int[] child = new int[population[populationIndex%top].length];//Create a child path from the top paths
            for (int childIndex = 0; childIndex < child.length; childIndex++) {
                child[childIndex]= population[populationIndex%top][childIndex];
            }
            int random = 1+(int)(Math.random() * (child.length-1));//1 to child.length-1 so that start city is not swapped
            int random2 = 1+(int)(Math.random() * (child.length-1));
            int random3 = 1+(int)(Math.random() * (child.length-1));
            int temp = child[random];//Swap 3 cities in the child path to mutate it
            child[random] = child[random2];
            child[random2] = child[random3];
            child[random3] = temp;
            fitness[populationIndex] = distance(child);//Calculate the fitness of the child path
            for (int childIndex = 0; childIndex < child.length; childIndex++) {//Replace the population path with the mutated child path
                population[populationIndex][childIndex] = child[childIndex];
            }
            
        }
        sort(population, fitness);//Sort the population by fitness value (distance)
    }
    static void sort(int[][] population, double[] fitness){
        //selection sort by fitness value (distance)
        for (int currentIndex = 0; currentIndex < fitness.length-1; currentIndex++) {//For each fitness value (distance) in the population
            int min_index = currentIndex;//Set the minimum index to the current index
            for (int compareIndex = currentIndex+1; compareIndex < fitness.length; compareIndex++) {//For each fitness value (distance) after the current index
                if(fitness[compareIndex] < fitness[min_index]){//If the fitness value (distance) is less than the current minimum
                    min_index = compareIndex;//Set the minimum index to the current index
                }
            }
            double temp = fitness[min_index];//Swap the fitness value (distance)
            fitness[min_index] = fitness[currentIndex];
            fitness[currentIndex] = temp;
            int[] temp2 = population[min_index];//Swap the population path
            population[min_index] = population[currentIndex];
            population[currentIndex] = temp2;
        }
        
    }
    
    static void shuffle(int[] path){
        /*
         * Shuffle the cities
         * The cities are shuffled to make unique paths
         * The cities are shuffled by swapping each city with a random city
         * The cities are shuffled except the start city
         * The start city is not shuffled because it is the same for each path
         */
        for (int currentCityIndex = 1; currentCityIndex < path.length; currentCityIndex++) {//For each city in the path (except the start city)
            int randomCityIndex = 1+(int)(Math.random() * (path.length-1));//1 to array.length-1 so that start is not swapped
            //swap the city with a random city
            int tempCity = path[currentCityIndex];
            path[currentCityIndex] = path[randomCityIndex];
            path[randomCityIndex] = tempCity;
        }
    }

    static double distance(int[] path){//Calculate the distance of a path
        double distance = 0;//Initialize the distance
        for (int cityIndex = 0; cityIndex < path.length; cityIndex++) {//For each city in the path
            int currentCity = path[cityIndex];//Get the current city
            int nextCity = path[(cityIndex + 1) % path.length];//Get the next city (if the current city is the last city, get the first city)
            distance += distanceMatrix[currentCity][nextCity];//Add the distance between the current city and the next city
        }
        return round(distance, ROUND_TO_FOUR_PLACES); // round to {ROUND_TO_FOUR_PLACES} decimal places
    }
    
    public static double round(double value, int places) {
        /*
         * Round a double to a certain number of decimal places
         * The double is rounded to the nearest integer value
         * The double is divided by the factor to get the desired decimal places
         */
        if (places < 0) throw new IllegalArgumentException();//If the places is less than 0, throw an exception
    
        long factor = (long) Math.pow(10, places);//Get the factor to multiply by to get the desired decimal places
        value = value * factor;//Multiply the value by the factor
        long tmp = Math.round(value);//Round to the nearest integer value
        return (double) tmp / factor;//Divide by the factor to get the desired decimal places
    }
    
    static void display(int[][] cities){//Display the cities
        System.out.println("Cities:");
        for (int city_Index = 0; city_Index < cities.length; city_Index++) {
            System.out.println(cities[city_Index][CITY_ID] + " " + cities[city_Index][X_COORDINATE] + " " + cities[city_Index][Y_COORDINATE]);//Display the city id, x coordinate, and y coordinate
        }
    }
    
    static int[][] input(){//Get the cities from the file and return them in a 2d array
        Scanner input = null;//Initialize the scanner
        int count_of_cities = 0;//Initialize the count of cities to 0
        File file = new File(INPUT_FILE_NAME);//Initialize the file to read from
        int[][] cities;//Initialize the cities 2d array
        String lines="";//Initialize the lines string to store the file contents in a string
        try {
            input = new Scanner(file);//Initialize the scanner to read from the file
            while (input.hasNextLine()) {//While there is another line in the file
                if(count_of_cities!=0)//If the count is not 0, add a new line to (lines) string
                    lines=lines+"\n";
                count_of_cities++;//Increment the count
                lines=lines+input.nextLine();//Add the line to (lines) string
            }
            input.close();//Close the scanner
            cities = new int[count_of_cities][CITY_COLUMN_LENGHT];//Initialize the cities 2d array
            input = new Scanner(lines);//Initialize the scanner to read from the string (lines)
            for(int cityIndex = 0; cityIndex<count_of_cities; cityIndex++){//For each city in the file
                cities[cityIndex][CITY_ID] = input.nextInt();//Get the city id
                cities[cityIndex][X_COORDINATE] = input.nextInt();//Get the x coordinate
                cities[cityIndex][Y_COORDINATE] = input.nextInt();//Get the y coordinate
            }
            input.close();//Close the scanner
            return cities;//Return the cities 2d array
        } catch (Exception FileNotFoundException) {//If there is an exception, display the exception and exit the program
            System.out.println(FileNotFoundException);
            System.exit(0);
        }

        return null;//Just to get rid of the error that the method needs to return a value
    }
}