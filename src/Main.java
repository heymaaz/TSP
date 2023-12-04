import java.util.Scanner;
import java.io.File;
public class Main {
    /**
     * Author: Maaz Chowdhry
     * Student Number: M00910300
     * Course:  Artificial Intelligence
     * To run the program, Uncomment the input file you want to run or add the path to the file you want to run
     * The path should can be relative or absolute and should be in the format: C:/Users/Maaz/Desktop/trainFiles/input.txt or trainFiles/input.txt
     * The input file should be in the format:
     * city id, x coordinate, y coordinate
     * 1 6734 1453
     * 2 2233 10
     * 3 5530 1424
     * 4 401 841
     */
    //Initialize the variables to be used in the program (static so that they can be used in the methods)
    static double[][] distanceMatrix;//For the adjacency matrix
    static final int POWER_OF_TWO = 2;//For the x^2 y^2 in the distance formula
    static final int POPULATION_SIZE = 100;//For the POPULATION_SIZE of the population
    static final int CHECK_FOR_BIG_CITY=30;//For the city lenght to check if the algorithm should stop early
    static final int TOP_IF_NOT_BEST = 5;//For the top 5 paths
    static final int TOP_IF_BEST = 5;//For the top 20 paths
    static final int REINITIALISE_POPULATION_AT = 1000;//For the generation limit of 1000 to reinitialize the population and fitness every 1000 generations
    static final int GENERATION_LIMIT_FOR_BIG_CITY = 100; //For the generation limit of 1,000 to stop the algorithm for city lenghts greater than 30
    static final int GENERATION_LIMIT = 1000000;//For the generation limit of 1,000,000 to stop the algorithm
    static final int NUMBER_OF_POPULATIONS = 5;//For the number of populations

    //Uncomment the input file you want to run or add the path to the file you want to run
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
        for (int city1 = 0; city1 < cities.length; city1++) {
            for (int city2 = city1; city2 < cities.length; city2++) {
                if(city1==city2)
                    distanceMatrix[city1][city2] = 0;
                else{
                    distanceMatrix[city1][city2] = distanceMatrix[city2][city1] = calculateDistance(cities[city1], cities[city2]);//Calculate the distance between the cities
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
        for (int rowNum = 0; rowNum < population.length; rowNum++) {
            for (int colNum = 0; colNum < population[rowNum].length; colNum++) {
                population[rowNum][colNum] = colNum;
            }
            shuffle(population[rowNum]);//Shuffle the cities
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
        for (int rowNum = 0; rowNum < population.length; rowNum++) {
            fitness[rowNum] = distance(population[rowNum]);
        }
        sort(population, fitness);//Sort the population by fitness
        return fitness;
    }
    
    static double calculateDistance(int[] city1, int[] city2) {
        /*
         * Calculate the distance between two cities
         * The distance is calculated using the distance formula
         * The distance formula is sqrt((x2-x1)^2+(y2-y1)^2)
         */
        return Math.sqrt(Math.pow(city2[1] - city1[1], POWER_OF_TWO) + Math.pow(city2[2] - city1[2], POWER_OF_TWO));//Distance formula using adjacency matrix
    }
    static int min(double[][] fitness){
        /*
         * This function is used to get the index of the minimum fitness value (distance)
         */
        double min = Double.MAX_VALUE;
        int minIndex = -1;
        for (int fitnessIndex = 0; fitnessIndex < fitness.length; fitnessIndex++) {
            if(fitness[fitnessIndex][0]<min)
            {
                minIndex = fitnessIndex;
                min = fitness[fitnessIndex][0];
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
        for (int i = 0; i < bestPath.length; i++) {
            System.out.print((1+bestPath[i]) + " ");
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
        boolean lenghtFlag = cities.length>CHECK_FOR_BIG_CITY;//Flag to stop the algorithm early if the city lenght is greater than 30
        int[] bestPath = new int[cities.length];//The best path
        double bestDistance = Double.MAX_VALUE;//The best distance
        for(int i = 0; i<NUMBER_OF_POPULATIONS; i++)//For each population
            sort(population[i], fitness[i]);//Sort the population by fitness (distance
        int generation = 0;//to keep track of the generation
        boolean dontStop = true;//Flag to stop the algorithm
        String ifStop = "";//To display why the algorithm stopped
        return geneticAlgo(cities, population, fitness, lenghtFlag, bestPath, bestDistance, generation, dontStop, ifStop);//Run the genetic algorithm
     }
     static double geneticAlgo(int[][] cities, int[][][] population, double[][] fitness, boolean lenghtFlag, int[] bestPath, double bestDistance, int generation, boolean dontStop, String ifStop){
        int countOfAgreeingPopulations=0;//To keep track of how many contenders agree
        while(dontStop && countOfAgreeingPopulations!=population.length){
            countOfAgreeingPopulations=0;//Reset the count of agreeble contenders
            bestDistance =fitness[min(fitness)][0];//Get the best distance from all the populations
            for(int populationIndex = 0; populationIndex<population.length; populationIndex++){//For each population
                if(bestDistance==fitness[populationIndex][0]){//If the best distance is the best distance of the population
                    countOfAgreeingPopulations++;//increment the count
                    mutate(population[populationIndex],fitness[populationIndex],TOP_IF_BEST);//Mutate the population by swapping 3 cities from the top 20 paths
                }
                else{
                    if(generation%REINITIALISE_POPULATION_AT==(REINITIALISE_POPULATION_AT-1))//Every 1000 generations, reinitialize the population and fitness 
                    {
                        initPopulation(population[populationIndex]);//Reinitialize the population
                        initFitness(fitness[populationIndex],population[populationIndex]);//Reinitialize the fitness
                        sort(population[populationIndex], fitness[populationIndex]);//Sort the population by fitness
                    }
                    mutate(population[populationIndex],fitness[populationIndex],TOP_IF_NOT_BEST);//Mutate the population by swapping 3 cities from the top 5 paths
                }
            }
            displayGeneration(generation++,bestDistance,countOfAgreeingPopulations);//Display the generation, best distance, and number of agreeble contenders
            if(lenghtFlag&&generation>GENERATION_LIMIT_FOR_BIG_CITY){//If the city length is greater than 30 and the generation is greater than 1000, stop the algorithm
                dontStop=false;
                ifStop="Stopped because of generation limit of 1000 reached for city lenght:"+cities.length+".\n";
            }
            if(generation>GENERATION_LIMIT){//If the generation is greater than 1,000,000, stop the algorithm
                dontStop=false;
                ifStop="Stopped because of generation limit of 1,000,000 reached.\n";
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
        for(int i = top; i<fitness.length; i++){//For the rest of the population (after the top paths)
            int[] child = new int[population[i%top].length];//Create a child path from the top paths
            for (int j = 0; j < child.length; j++) {
                child[j]= population[i%top][j];
            }
            int random = 1+(int)(Math.random() * (child.length-1));//1 to child.length-1 so that start city is not swapped
            int random2 = 1+(int)(Math.random() * (child.length-1));
            int random3 = 1+(int)(Math.random() * (child.length-1));
            int temp = child[random];//Swap 3 cities in the child path to mutate it
            child[random] = child[random2];
            child[random2] = child[random3];
            child[random3] = temp;
            fitness[i] = distance(child);//Calculate the fitness of the child path
            for (int j = 0; j < child.length; j++) {//Replace the population path with the mutated child path
                population[i][j] = child[j];
            }
            
        }
        sort(population, fitness);//Sort the population by fitness value (distance)
    }
    static void sort(int[][] population, double[] fitness){
        //selection sort by fitness value (distance)
        for (int i = 0; i < fitness.length-1; i++) {//For each fitness value (distance) in the population
            int min_index = i;//Set the minimum index to the current index
            for (int j = i+1; j < fitness.length; j++) {//For each fitness value (distance) after the current index
                if(fitness[j] < fitness[min_index]){//If the fitness value (distance) is less than the current minimum
                    min_index = j;//Set the minimum index to the current index
                }
            }
            double temp = fitness[min_index];//Swap the fitness value (distance)
            fitness[min_index] = fitness[i];
            fitness[i] = temp;
            int[] temp2 = population[min_index];//Swap the population path
            population[min_index] = population[i];
            population[i] = temp2;
        }
        
    }
    
    static void shuffle(int[] array){
        /*
         * Shuffle the cities
         * The cities are shuffled to make unique paths
         * The cities are shuffled by swapping each city with a random city
         * The cities are shuffled except the start city
         * The start city is not shuffled because it is the same for each path
         */
        for (int i = 1; i < array.length; i++) {//For each city in the path (except the start city)
            int random = 1+(int)(Math.random() * (array.length-1));//1 to array.length-1 so that start is not swapped
            //swap the city with a random city
            int temp = array[i];
            array[i] = array[random];
            array[random] = temp;
        }
    }

    static double distance(int[] path){//Calculate the distance of a path
        double distance = 0;//Initialize the distance
        for (int i = 0; i < path.length; i++) {//For each city in the path
            int fromCity = path[i];//Get the current city
            int toCity = path[(i + 1) % path.length];//Get the next city (if the current city is the last city, get the first city)
            distance += distanceMatrix[fromCity][toCity];//Add the distance between the current city and the next city
        }
        return round(distance, 4); // round to 4 decimal places
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
        for (int j = 0; j < cities.length; j++) {
            System.out.println(cities[j][0] + " " + cities[j][1] + " " + cities[j][2]);//Display the city id, x coordinate, and y coordinate
        }
    }
    
    static int[][] input(){//Get the cities from the file and return them in a 2d array
        Scanner input = null;//Initialize the scanner
        int count = 0;//Initialize the count of cities to 0
        File file = new File(INPUT_FILE_NAME);//Initialize the file to read from
        int[][] cities;//Initialize the cities 2d array
        String lines="";//Initialize the lines string to store the file contents in a string
        try {
            input = new Scanner(file);//Initialize the scanner to read from the file
            while (input.hasNextLine()) {//While there is another line in the file
                if(count!=0)//If the count is not 0, add a new line to (lines) string
                    lines=lines+"\n";
                count++;//Increment the count
                lines=lines+input.nextLine();//Add the line to (lines) string
            }
            input.close();//Close the scanner
            cities = new int[count][3];//Initialize the cities 2d array
            input = new Scanner(lines);//Initialize the scanner to read from the string (lines)
            for(int i = 0; i<count; i++){//For each city in the file
                cities[i][0] = input.nextInt();//Get the city id
                cities[i][1] = input.nextInt();//Get the x coordinate
                cities[i][2] = input.nextInt();//Get the y coordinate
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