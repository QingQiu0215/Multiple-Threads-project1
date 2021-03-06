// Source code for stack manager:
import CharStackExceptions.*;

public class StackManager
{
          // The Stack
           private static CharStack stack = new CharStack();
           private static final int NUM_ACQREL = 4; // Number of Producer/Consumer threads
           private static final int NUM_PROBERS = 1; // Number of threads dumping stack
           private static int iThreadSteps = 3; // Number of steps they take
          // Semaphore declarations. Insert your code in the following:
           private static int count=4;
           private static Semaphore mutex=new Semaphore(1);
          // The main()
          public static void main(String[] argv)
          {
                    // Some initial stats...
                    try
                    {
                              System.out.println("Main thread starts executing.");
                              System.out.println("Initial value of top = " + stack.getTop() + ".");
                              System.out.println("Initial value of stack top = " + stack.pick() + ".");
                              System.out.println("Main thread will now fork several threads.");
                    }
                    catch(CharStackEmptyException e)
                    {
                              System.out.println("Caught exception: StackCharEmptyException");
                              System.out.println("Message : " + e.getMessage());
                              System.out.println("Stack Trace : ");
                              e.printStackTrace();
                     }
                    /*
                   * The birth of threads
                    */
                   Consumer ab1 = new Consumer();
                   Consumer ab2 = new Consumer();
                   System.out.println ("Two Consumer threads have been created.");
                  Producer rb1 = new Producer();
                  Producer rb2 = new Producer();
                  System.out.println ("Two Producer threads have been created.");
                  CharStackProber csp = new CharStackProber();
                  System.out.println ("One CharStackProber thread has been created.");
                  /*
                 * start executing
                  */
                 ab1.start();
                 rb1.start();
                 ab2.start();
                 rb2.start();
                 csp.start();
                 /*
                  * Wait by here for all forked threads to die
                 */
                try
                {
                           ab1.join();
                           ab2.join();
                           rb1.join();
                           rb2.join();
                          csp.join();
                          // Some final stats after all the child threads terminated...
                          System.out.println("System terminates normally.");
                          System.out.println("Final value of top = " + stack.getTop() + ".");
                          System.out.println("Final value of stack top = " + stack.pick() + ".");
                          System.out.println("Final value of stack top-1 = " + stack.getAt(stack.getTop() - 1) + ".");
                          System.out.println("Stack access count = " + stack.getAccessCounter());
                }
               catch(InterruptedException e)
               {
                      System.out.println("Caught InterruptedException: " + e.getMessage());
                           System.exit(1);
               }
              catch(Exception e)
              {
                           System.out.println("Caught exception: " + e.getClass().getName());
                           System.out.println("Message : " + e.getMessage());
                          System.out.println("Stack Trace : ");
                          e.printStackTrace();
               }
        } // main()
        /*
        * Inner Consumer thread class
        */
        static class Consumer extends BaseThread
        {
                 private char copy; // A copy of a block returned by pop()
                 public void run()
                 {

                              System.out.println ("Consumer thread [TID=" + this.iTID + "] starts executing.");
                              for (int i = 0; i < StackManager.iThreadSteps; i++)  
                              {
                            	  while(count<=0);
                            	  mutex.P();//synchronization by using semaphore.
                            	    try 
                            	    {
										this.copy=StackManager.stack.pop();
										count--;
									} 
                            	    catch (CharStackEmptyException e) 
                            	    {
										e.printStackTrace();
									}
                            	    System.out.println("Consumer thread [TID=" + this.iTID + "] pops character =" + this.copy);
                            	    mutex.V();  
                              }
                              System.out.println ("Consumer thread [TID=" + this.iTID + "] terminates.");
                     }
          } // class Consumer
           /*
          * Inner class Producer
           */
          static class Producer extends BaseThread
          {
                     private char block; // block to be returned
                     public void run()
                     {
                                System.out.println ("Producer thread [TID=" + this.iTID + "] starts executing.");
                                for (int i = 0; i < StackManager.iThreadSteps; i++)  
                                {
                                	while(count>10);
                              	    mutex.P();//synchronization by using semaphore.
                              	    try 
                              	    {
                              	    	if(stack.getTop()==-1)
                              	    	{
                              	    		this.block = 'a';
                              	    		StackManager.stack.push(this.block);
                              	    		count++;
                              	    	}
                              	    	else
                              	    	{
                              	    	    this.block = (char)(stack.pick() + 1);                             	    
                              	    	    StackManager.stack.push(this.block);
                              	    	    count++;
                              	    	}
  									} 
                              	    catch (CharStackFullException e) 
                              	    {
  										e.printStackTrace();
  									} 
                              	    catch (CharStackEmptyException e) 
                              	    {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
                              	  System.out.println("Producer thread [TID=" + this.iTID + "] pushes character =" + this.block);
                              	    mutex.V();  
                                }
                               System.out.println("Producer thread [TID=" + this.iTID + "] terminates.");
                     }
          } // class Producer
            /*
           * Inner class CharStackProber to dump stack contents
            */
           static class CharStackProber extends BaseThread
           {
                     public void run()
                     {
                    	 mutex.P();
                               System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");
                               for (int i = 0; i < 2 * StackManager.iThreadSteps; i++)
                               {
                            	   System.out.print("Stack S=(");
        
                            	   for(int j=0;j<stack.getSize();j++)
                            	   {
                            	       try 
                            	       {
                            	    	   if(j<stack.getSize()-1)
									          System.out.print("[" +StackManager.stack.getAt(j) + "],");
                            	    	   else
                            	    		  System.out.print("[" +StackManager.stack.getAt(j) + "]");
								       } 
                            	       catch (CharStackInvalidAceessException e) 
                            	       {
									    // TODO Auto-generated catch block
								          e.printStackTrace();
								       }
                            	       
                            	   }
                            	   System.out.println(")");
                               }
                         mutex.V();
                     }
           } // class CharStackProber
} // class StackManager