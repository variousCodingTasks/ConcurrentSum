package exercise151;

/**
 * a thread used to sum integers stored in NumbersPool object.
 */
public class SumThread implements Runnable{
    
    private final NumbersPool numbersPool;
    
    /**
     * sets the user provided NumbersPool to draw numbers from, in order to sum them.
     */
    public SumThread(NumbersPool numbersPool) {
        this.numbersPool = numbersPool;
    }
    
    /**
     * the thread's run method implementing the interface Runnable single method:
     * the while loop keeps trying to draw two numbers from the NumbersPool: if the
     * method numbersPool.drawTwo returns 0, the pool is empty and the loop terminates
     * causing the thread to terminate, if 1 is returned, this means that two integers
     * were properly drawn and stored in the tempStorage two elements, which are summed
     * and stored back in the pool, else (status returned is 2), the pool had numbers
     * but its storage was empty because all of the integers were being processed by
     * other threads, causing this thread to wait until the status is changed (notifyAll
     * is called on all threads waiting to draw numbers), starting a new iteration.
     */
    @Override
    public void run() {
        Integer[] tempStorage = new Integer[2];        
        while(true){
            int currentPoolState = numbersPool.drawTwo(tempStorage);
            if (currentPoolState == 0)
                break;
            else if (currentPoolState == 1){
                this.numbersPool.putOne(tempStorage[0] + tempStorage[1]);
            }
            else
                continue;                
        }
    }    
}
