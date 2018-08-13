package exercise151;

import java.util.concurrent.ArrayBlockingQueue;
import java.security.SecureRandom;

/**
 * creates an object with an underlying synchronized ArrayBlockingQueue and implements
 * additional methods to query the data structure.
 */
public class NumbersPool {
    
    public static final int LOWER_BOUND = 1;
    public static final int UPPER_BOUND = 100;
    
    private final ArrayBlockingQueue<Integer> queue;
    private int capacity;
    private int remainingItems;
    private int totalWaited = 0;
    
    /**
     * creates and initializes the classes fields.
     */
    public NumbersPool(int capacity){
        this.queue = new ArrayBlockingQueue<Integer>(capacity);
        this.capacity = capacity;
        this.remainingItems = capacity;
    }
    
    /**
     * this synchronized method tries to draw two elements from the blocking array and to
     * store them in dest. if the array is empty but other threads are expected to put numbers
     * back in the array, the method causes the calling thread to wait, otherwise, it either
     * stores two numbers inn dest or returns 0 for an empty array. if the method manages to
     * draw 2 elements, the counter remainingItems is decremented since a call to putOne
     * is guaranteed to follow, this counter helps threads determine if the pool still have
     * numbers to be summed. since the method is synchronized, only a single thread can draw numbers
     * at a time, so no fear that a thread will only manage to draw one number only.
     * 
     * @param dest a destination array to store the drawn numbers, must be of size
     * 2 at least.
     * @return the status: 0, no more elements in the blocking array. 1, two numbers
     * were stored in dest. 2, there were no numbers to draw in the blocking array,
     * however, other threads are still processing numbers.
     */
    public synchronized int drawTwo(Integer[] dest){
        if (this.remainingItems < 2) return 0;
        else if (this.queue.size() >= 2){
            try {
                dest[0] = this.queue.take();
                dest[1] = this.queue.take();
                this.remainingItems--;
                return 1;                
            }
            catch (InterruptedException e){
                return 0;
            }
        }
        else {
            try {
                this.totalWaited++;
                this.wait();
                return 2;
            }
            catch (InterruptedException ex) {
                System.err.printf("Thread %s was interrupted...\n", Thread.currentThread());
                Thread.currentThread().interrupt();
                return 0;
            }
        }
    }
    
    /**
     * puts an item in the pool's array and notifies all the threads waiting
     * for the array to have enough elements to draw (at least 2).
     * 
     * @param item the item to be inserted
     */
    public synchronized void putOne(Integer item){
        try {
            this.queue.put(item);
            this.notifyAll();
        } 
        catch (InterruptedException ex) {
            System.err.printf("Thread %s was interrupted...\n", Thread.currentThread());
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * wraps ArrayBlockingQueue peek method.
     * 
     * @return the first element in the pool's array
     */
    public Integer numbersPoolPeeK(){
        return this.queue.peek();
    }    
    
    /**
     * populates the blocking array with random integers from the default range.
     */
    public void populatePool(){
        Integer sum = 0;
        SecureRandom generator = new SecureRandom();
        for(int i = 0; i < this.capacity; i++){
            Integer temp = generator.nextInt(UPPER_BOUND) + LOWER_BOUND;
            try {
                this.queue.put(temp);
            }
            catch (InterruptedException ex) {
                System.err.printf("Thread %s was interrupted...\n", Thread.currentThread());
                Thread.currentThread().interrupt();
            }
            sum += temp;
        }
        System.out.printf("The sum of the elements in the array (using a single thread): %d\n", sum);        
    }

    /**
     * getter for this.totalWaited.
     * 
     * @return the number of times threads tried to draw numbers from the pool
     * and wait was called.
     */
    public int getTotalWaited() {
        return this.totalWaited;
    }
    
}
