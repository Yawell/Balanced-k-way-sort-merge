//This was created by Uel Dalilis and Quynh Anh Pham for a university assignment
import java.io.*;

public class MergeRuns {

	//initialise variables
    private static BufferedWriter writer;
    private static String file;
    private static Node[] heap;
    private static int runCount;
    private static int length;
    private static int k;
    private static String inputFile;
    private static int numbrun;
    private static int currentRun;
    private static String[] oldF;
    private static String newFileName;
    
    public static void main(String[] args) {
        try {
        	//check if input is valid
            if(args.length != 2 || Integer.parseInt(args[0]) <= 1 )
            {
                System.err.println("Usage: java MergeRuns <int (above 1)> <filename>");
                return;
            }
            File checkFile = new File(args[1]);
            if(!checkFile.exists())
            {
            	System.err.println("Invalid file name");
            	return;
            }
            //initialise k and file name
            k = Integer.parseInt(args[0]);
            file = args[1];        	
            
            //split original file name to able to create the resulting file with same name except for the .run
            oldF = file.split("\\.");
            newFileName = "";
            
            for(int i = 0; i < oldF.length-1; i++)
            {
            newFileName = newFileName + oldF[i] + ".";
            }			    

			
            inputFile = file;
            currentRun = 0;
            
            //run distribution and sort the first time
            distribute(file);
            sort();
            
			int totalTimes = 0;
            int times = 0;
            int cur = runCount;
            
            //calculate number of pass
            while (cur >1){
                int temp = cur /k;
                if(cur >k) {
                    if (cur % k != 0) {
                        temp++;
                    }
                }
                cur = temp;

                times++;
                totalTimes++;
            }
            times--;
            
            file = newFileName + "sorted";
            
            //execute the rest of the passes
            while (times !=0) {
                distribute(file);
                sort();
                times--;
            }
			
			System.err.println("Number of Passes: " + totalTimes);
        }
        catch(Exception e)
        {
            System.err.println("Exception " + e);
        }


    }
    //distribut runs over k files
    private static void distribute(String file){
        try {
        	//initialise variables
            BufferedReader br = new BufferedReader(new FileReader(file));
            Integer current = 1;
            runCount = 0;
            
            //read the first line
            String s = br.readLine();
            
            //while not at the end of the file
            while (s != null) {
            	//set up writer
                writer = new BufferedWriter(new FileWriter(current + "temp.tmp", true));
                //while not at the end of a run write line to tmp file and read next line
                while (s.compareTo("#~#") != 0) {
                    writer.write(s + "\n");
                    s = br.readLine();
                }
                writer.write("#~#" + "\n");
                runCount++;
                writer.close();

                s = br.readLine();
                //loop back to first tmp file when finish writing to k file and still have new runs to write
                if (current == k) {
                    current = 1;
                }
                else {
                    current++;
                }
            }
            //remove input file if it not the original file
            if(file.compareTo(inputFile) != 0) {
                File f = new File(file);
                f.delete();
            }
        }
        catch(Exception e)
        {
            System.err.println("Exception " + e);
        }
    }

	//do one pass of sorting 
    private static void sort(){
        try {

            String s = "";
            //calculate the number of files have to read from
            if(runCount < k)
            {
                numbrun = runCount % k;
            }
            else{
                numbrun = k;
            }

			//calculate number of full k runs merging
            int whole = runCount/k;
			//calculate number of leftover runs 
            int left = runCount%k;
            //if number of runs is less than k and run sort once
            if(whole ==0) {
                whole++;
                left = 0;
            }
            //create array of reader
            BufferedReader[] rd = new BufferedReader[numbrun];

			//initialise writer
            writer = new BufferedWriter(new FileWriter(newFileName + "sorted", true));
            
            //initial all readers
            for (int i = 0; i < numbrun; i++){
                String f =(i+1) + "temp.tmp";
                File file = new File(f);
                rd[i] = new BufferedReader(new FileReader(file));
            }
		
			//While all the data hasn't been read in apart from the last merge of the file
            while(whole !=0 && s !=null) {
				//Initialise the heap array
                heap = new Node[numbrun];
                //Storing the length of the hap array
                length = heap.length;
                //Goes through the last remaining runs
                for (int i = 1; i <= numbrun; i++) {  
                	//Reads the line               
                    s = rd[(i-1)].readLine();
                    //If the heap is empty then put the node in the top of the heap
                    if (heap[0] == null) {
                        Node n = new Node(i, s);
                        heap[0] = n;
                    } else {
                        for (int j = 0; j < length; j++) {
                        	//If the heap at the last position is empty then add a node in then upheap to sort
                            if (heap[j] == null) {
                                Node n = new Node(i, s);
                                heap[j] = n;
                                upHeap(j);
                                break;
                            }
                        }
                    }
                }

				//While the heap isn't empty
                while (length != 0) {
                	//Write the string from the root to the file
                    writer.write(heap[0].getLine() + "\n");
                    //Storing the current time file in a variable
                    String f = heap[0].getFile() + "temp.tmp";
                    //Read the line and store it in the variable
                    s = rd[(heap[0].getFile()-1)].readLine();

					//If its not at the end of the run store the node created into the heap then downheap to sort
                    if (s.compareTo("#~#") != 0) {
                        Node n = new Node(heap[0].getFile(), s);
                        heap[0] = n;
                        downHeap(0);
                    } else {
                    	//If we reach the end of a run we move the last value of the heap to the top, decrease the heap size and downheap to sort
                        heap[0] = heap[length - 1];
                        length--;
                        downHeap(0);
                    }
                }
                //Write out the end of run and decrease the 'whole' length by 1
                writer.write("#~#" + "\n");
                whole--;
                //If theres leftover runs, run it again
                if(whole == 0 && left !=0){
                    whole = 1;
                    numbrun = left;
                    left = 0;
                }
            }
            //Closing the readers
            for (int i = 0; i < numbrun; i++){
                rd[i].close();
            }
            writer.close();
            //Closing the writer
            
            //Deleting the temp files
            for (int i = 1; i <= k; i++) {
                File f = new File(i + "temp.tmp");
                f.delete();
            }
        }
        catch(Exception e)
        {
            System.err.println("Exception " + e);
        }
    }

	//upheap sort the heap
    private static void upHeap(int i){
    	//while not the first node and parent is larger, switch place with parent
        while (i != 0 && heap[i].getLine().compareTo(heap[((i - 1) / 2)].getLine()) < 0) {
            Node hold = heap[i];
            heap[i] = heap[((i - 1) / 2)];
            heap[((i - 1) / 2)] = hold;
            i = (i - 1) / 2;
        }
    }

    //This method sorts the heap from top to bottom
    private static void downHeap(int j){
        int left = (j + 1)*2 -1;
        int right = (j+1)*2;
        //While not at the end of heap
        while(left < length)
        {
            //If right exists
            if(right < length) {
                //If left < right
                int result = heap[left].getLine().compareTo(heap[right].getLine());
                if (result < 0)
                {
                    //If left >= parent then do nothing
                    if(heap[left].getLine().compareTo(heap[j].getLine()) >= 0)
                    {
                        return;
                    }

                    //Switch parent and left , calculate the values to get to the next left and right
                    else
                    {
                        Node hold = heap[j];
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
                    if(heap[right].getLine().compareTo(heap[j].getLine()) >= 0)
                    {
                        return;
                    }
                    //Switch parent and right , calculate the values to get to the next left and right
                    else
                    {
                        Node hold = heap[j];
                        heap[j] = heap[right];
                        heap[right] = hold;

                        j = right;
                        left = (j + 1)*2 -1;
                        right = (j+1)*2;
                    }
                }
            }

            //Only left child exists. If left> parent do nothing
            else if(heap[left].getLine().compareTo(heap[j].getLine()) >= 0)
            {
                return;
            }
            //Switch parent and left, calculate the values to get to the next left and right
            else
            {
                Node hold = heap[j];
                heap[j] = heap[left];
                heap[left] = hold;

                j = left;
                left = (j + 1)*2 -1;
                right = (j+1)*2;
            }
        }
    }
	
	//Object to store line of text and the file that line come from
    private static class Node
    {
    	//create variable
        private int file;
        private String line;
        
        //initialise and create the object
        public Node(int f, String l)
        {
            file = f;
            line = l;
        }
		//return file name
        public int getFile() {
            return file;
        }
		//return line of text
        public String getLine() {
            return line;
        }
    }

}
