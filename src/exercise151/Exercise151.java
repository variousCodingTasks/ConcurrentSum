package exercise151;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * demonstrates how a NumbersPool object can be used to sum numbers using multi threading.
 */
public class Exercise151 {

    public static final int LOWER_BOUND = 1;
    public static final int MAX_ELEMNTS_ALLOWED = 10000000;
    public static final int MAX_THREADS_ALLOWED = 1000;
    
    /**
     * asks the user for input (n for number of random elements to sum, m for the number of
     * threads),creates an ExecutorService and NumbersPool object, then calls createThreads
     * to create and run the desired amount of threads to concurrently sum the elements stored
     * i the numbers pool.
     */
    public static void main(String[] args) {
        int n = getUserInput("Please enter the desired size of the numbers array:", MAX_ELEMNTS_ALLOWED);
        int m = getUserInput("Please enter the desired number of threads:", MAX_THREADS_ALLOWED);
        ExecutorService executorService = Executors.newCachedThreadPool();
        NumbersPool pool = new NumbersPool(n);
        pool.populatePool();
        createThreads(executorService, pool, m);
    }
    
    /**
     * creates m threads, adds them to the executorService. the threads sum the elements,
     * keeping only one in the pool, which is displayed at the end when all threads terminate
     * .
     * 
     * @param executorService an ExecutorService to handle the threads
     * @param pool the numbers pool object
     * @param m the number of desired threads
     * 
     */
    public static void createThreads(ExecutorService executorService, NumbersPool pool, int m){
        for(int i = 0; i < m; i++){
            executorService.execute(new SumThread(pool));
        }
        executorService.shutdown();
        try {            
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            System.err.printf("Thread %s was interrupted...\n", Thread.currentThread());
            Thread.currentThread().interrupt();
        }
        System.out.printf("Multi threaded sum is: %d\n", pool.numbersPoolPeeK());
        System.out.printf("Total times threads waited on empty array: %d\n", pool.getTotalWaited());        
    }
    
    /**
     * displays a message prompting the user to enter some integer, the while loop will keep
     * going until a correct integer is supplied by the user. the integer must be in the specified
     * range otherwise, the upperBound is returned, in case the input is too big, or a lower bound
     * in case the integer is too small.
     * 
     * @param message the message prompting the user
     * @param upperBound the highest value allowed for the input
     * @return the users input or any of the bounds.
     */
    public static int getUserInput(String message, int upperBound){
        Scanner scanner;
        int i = 0;
        System.out.println(message);
        System.out.printf("Range of permited values (%d - %d)\n", LOWER_BOUND, upperBound);
        while(true){
            try {
                scanner = new Scanner(System.in);
                i = scanner.nextInt();
                break;
            }
            catch (InputMismatchException | NumberFormatException ex){
                System.out.println("Please enter a valid integer...");  
            }           
        }
        if (i < 1) return 1;
        else if (i > upperBound) return upperBound;
        else return i;
    }
    
}