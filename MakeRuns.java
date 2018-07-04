//This was created by Uel Dalilis and Quynh Anh Pham for a university assignment
import java.io.*;

class MakeRuns
{
	//Initialised variables
    private static String[] heap;
    private static String file;
    private static  BufferedWriter writer;
    private static String last;
    private static int length;
    private static int count1=0;
    private static int count2=0;
    private static int runCounter = 0;
    
    public static void main(String [] args)
    {
        try{
             //Ensures the input is an integer and a file name
             if(args.length != 2 || Integer.parseInt(args[0]) <= 1)
             {
                System.err.println("Usage: java MakeRuns <int> <filename>");
                return;
             }
            File checkFile = new File(args[1]);
            if(!checkFile.exists())
            {
            	System.err.println("Invalid file name");
            	return;
            }
             
	    	//Use the input to get the file and the size of the heap(array)
            file = args[1];
            heap = new String[Integer.parseInt(args[0])];

			//Create reader that reads in a line of text
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s=br.readLine();
            //Get our writer to create and write on a file
            writer = new BufferedWriter(new FileWriter(file + ".runs"));
            
            length = heap.length;

			//While not end of the file
            while(s!=null)
            {
                add(s);
                s=br.readLine();

            }
            //Write out the rest of the heap
            writeOutHeap();
			//Closing the writer
            writer.close();
			System.err.println("Number of runs: " +runCounter);
        }catch(Exception e)
        {
            System.err.println("Usage: java MakeRuns <int> <filename>");
        }
    }

	//This method writes out the rest of the values in the heap in order
    private static void writeOutHeap()
    {
        try{
        //End the current run and increase the run counter by 1
        writer.write("#~#" + "\n");
		runCounter ++;
		
		//Get the full length of the heap (input k)
        length = heap.length;
        //Sort the heap
        reHeap();

		//While the heap has more than one string of text then write it to the run, downheap and repeat
        while(length > 1)
        {
            writer.write(heap[0] + "\n");
            heap[0]=heap[length-1];
            length--;
            downHeap(0);
        }

		//If the heaps length is 1 then write out the last value in the run
        if(length == 1)
        {
            writer.write(heap[0] + "\n");
            length --;
        }
        //End the run and increase the run counter
        writer.write("#~#" + "\n");
        runCounter++;
        }
        catch(Exception e)
        {
            System.err.println("Exception " + e);
        }
    }


    private static void add(String s)
    {
        try {
        	//If the heap is empty add in the string thats been read in the heap
            if (heap[0] == null) {
                heap[0] = s;
            }
            else 
            {
                if(length > 0) {
                	//Add the value in the last empty space in the array then upheap
                    for (int i = 0; i < length; i++) 
                    {
                        if (heap[i] == null) {
                            heap[i] = s;
                            upHeap(i);
                            return;
                        }
                    }
                    
                    //Keeping track of the last value put in the heap and comparing the current head to it
                    //If the head value is greater than the last value put in the heap then write out the head to the heap
                    if (last == null || heap[0].compareTo(last) >= 0) 
                    {
                        writer.write(heap[0] + "\n");
                        last = heap[0];
                        heap[0] = s;
                        downHeap(0);
                        count1++;
                    }
                    //If the last value in the heap is greater than the head then swap head with last value, cut the array size by 1 then downheap
                    else 
                    {
                        String temp = heap[0];
                        heap[0] = heap[length - 1];
                        heap[length - 1] = temp;
                        length--;
                        downHeap(0);
                        //If the heaps length is not 0
                        //Write the head out to the run, memorize the last value, put the next line in the heap and downheap
                        if(length != 0) 
                        {
                            writer.write(heap[0] + "\n");
                            last = heap[0];
                            heap[0] = s;
                            downHeap(0);
                            count2++;
                        }
                        else 
                        {
                        	//Add the next string
                            add(s);
                        }
                    }
                }
                //When the size is 0 just end the run, put the length of the heap back to 'k' (input) then reheap
                else
                {
                    writer.write("#~#" + "\n");
                    runCounter ++;
                    length = heap.length;
                    reHeap();
                    last = null;
                    add(s);
                }
            }
           
        }
        catch(Exception e)
        {
            System.err.println("Exception " + e);
        }

    }
    
    //Start at the highest position node that has a parent, downheap and repeat until on the root
    private static void reHeap(){
       
        for (int start = heap.length /2; start>=0; start--) {
            downHeap(start);
        }
      
    }
    
    //Compares the last value put in the heap and compare with the parent, if its less than the parent then swap, repeat this until the parent is smaller
    private static void upHeap(int i){
        while (i != 0 && heap[i].compareTo(heap[((i - 1) / 2)]) < 0) {
            String hold = heap[i];
            heap[i] = heap[((i - 1) / 2)];
            heap[((i - 1) / 2)] = hold;
            i = (i - 1) / 2;
        }
    }
    
    //This method sorts the heap from top to bottom
    private static void downHeap(int j){
    	//Getting the left and right position of the parent
        int left = (j + 1)*2 -1;
        int right = (j+1)*2;
        
        //While not at the end of heap
        while(left < length)
        {
            //If right exists
            if(right < length) {
                //If left < right
                int result = heap[left].compareTo(heap[right]);
                if (result < 0)
                {
                    //If left >= parent then do nothing
                    if(heap[left].compareTo(heap[j]) >= 0)
                    {
                        return;
                    }

                    //Switch parent and left , calculate the values to get to the next left and right
                    else
                    {
                        String hold = heap[j];
                        heap[j] = heap[left];
                        heap[left] = hold;

                        j = left;
                        left = (j + 1)*2 -1;
                        right = (j+1)*2;
                    }
                }
                else
                {
                    //If right >= parent do nothing
                    if(heap[right].compareTo(heap[j]) >= 0)
                    {
                        return;
                    }
                    //Switch parent and right , calculate the values to get to the next left and right
                    else
                    {
                        String hold = heap[j];
                        heap[j] = heap[right];
                        heap[right] = hold;

                        j = right;
                        left = (j + 1)*2 -1;
                        right = (j+1)*2;
                    }
                }
            }

            //Only left child exists. If left> parent do nothing
            else if(heap[left].compareTo(heap[j]) >= 0)
            {
                return;
            }
            //Switch parent and left, calculate the values to get to the next left and right
            else
            {
                String hold = heap[j];
                heap[j] = heap[left];
                heap[left] = hold;

                j = left;
                left = (j + 1)*2 -1;
                right = (j+1)*2;
            }
        }
    }
}

