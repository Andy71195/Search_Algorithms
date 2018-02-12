
package lizzard;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lizzard 
{
    static int boardSize;
    static int lizzardCount;
    static String algoName;
    static int finalBoard[][]=new int[boardSize][boardSize];
    static int initialBoard [][];
    static int middleBoard [][];
    static HashMap<Integer, Integer> treeCounterHashMap = new HashMap<Integer, Integer>();  
    static boolean treeExist=false;
    static int lizzard=0;
    static int j=0;
    static boolean position = false;
    static int returnArray[]= new int[2];
    static List<Nursery> queue= new ArrayList<>();
    static int top=-1;
    static int front=0;

    
    public static void main(String[] args) throws IOException
    {
        PrintWriter pw=new PrintWriter("output.txt","UTF-8");
        FileReader in = new FileReader("input.txt");
        BufferedReader br = new BufferedReader(in);
        algoName = br.readLine();
        boardSize = Integer.parseInt(br.readLine());
        lizzardCount = Integer.parseInt(br.readLine());
        initialBoard= new int[boardSize][boardSize];
        middleBoard= new int[boardSize][boardSize];
        for(int i =0;i<boardSize;i++)
        {   
            int tree=0;
            String temp[]=br.readLine().split("");
            for(int j=0; j<boardSize;j++)
            {
                initialBoard[i][j]=Integer.parseInt(temp[j]);
                middleBoard[i][j]=Integer.parseInt(temp[j]);
                if(initialBoard[i][j]==2)
                {
                    tree++;
                }

                
            }
            treeCounterHashMap.put(i,tree);
             
        }
        
        if(lizzardCount==0)
        {
            pw.println("OK");
                for(int i=0; i < boardSize;i++)
                {
                    for(int j =0;j<boardSize;j++)
                    {
                        pw.print(initialBoard[i][j]);
                    }
                    pw.println("");   
                }
                pw.close();
            System.exit(0);
        }
        int totalTreeCount=0;
        for(int i=0;i<boardSize;i++)
        {
            totalTreeCount+= treeCounterHashMap.get(i);
        }
        if(((boardSize*boardSize)-totalTreeCount)<lizzardCount)
        {
            pw.println("FAIL");
            pw.close();
            System.exit(0);
        }
        if(totalTreeCount==0)
        {
            if(boardSize<lizzardCount)
            {
                pw.println("FAIL");
                pw.close();
                System.exit(0); 
            }
        }

        if(algoName.equals("BFS"))
        {
            if(!BFS())
            {
                pw.println("FAIL");
            }
        }
        else if(algoName.equals("DFS"))
        {
           
            ExecutorService executorServiceDFS = Executors.newSingleThreadExecutor();
	    Future<Boolean> futureDFS = executorServiceDFS.submit(() -> DFS1(initialBoard,0,0));
	    try {
	        boolean output = (boolean) futureDFS.get(270, TimeUnit.SECONDS);
	        if(output == true) {

                    pw.println("OK");
                    for(int i=0; i < boardSize;i++)
                    {
                        for(int j =0;j<boardSize;j++)
                        {
                            pw.print(initialBoard[i][j]);
                        }
                        pw.println();
                    }
                    pw.close();
                    System.exit(0);
    
		}
        	else{
                
                    pw.println("FAIL");
                    pw.close();
                    System.exit(0);
                
                }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    pw.println("FAIL");
                    pw.close();
                    executorServiceDFS.shutdown();
                    System.exit(0);

                }

            if(DFS1(initialBoard,0,0))
            {
                pw.println("OK");
                for(int i=0; i < boardSize;i++)
                {
                    for(int j =0;j<boardSize;j++)
                    {
                        pw.print(initialBoard[i][j]);
                    }
                    pw.println();
                }
                  
            }
            else
            {
                pw.println("FAIL");
            }
        }
        else if(algoName.equals("SA"))
        {

            SA();
         
        }
        pw.close();
    }       



    public static int []nextPosition(int row, int col)
    {
        int nextPossible[]=new int[2];
        if(row==boardSize-1 && col==boardSize-1)
        {
            nextPossible[0]=row;
            nextPossible[1]=col;
        }

        else if(col==boardSize-1)
        {
            col=0;
            row++;
        }
        else if(col<boardSize)
        {
            col++;
        }

        nextPossible[0]=row;
        nextPossible[1]=col;
        return nextPossible;
    }

    public static boolean DFS1(int initialBoard[][], int row,int col)
    {
        
        int prevI=row,prevJ=col;
        if(Lizzard.lizzard==lizzardCount)
            return true;
        int value[]=new int[2];
        
        int treesLeft=0;
        for(int z=prevI;z<boardSize;z++)
        {
            treesLeft+=treeCounterHashMap.get(z);
        }
        int rowsLeft = boardSize-prevI;
        if((lizzardCount-Lizzard.lizzard)>(treesLeft+rowsLeft))
        {
            return false;
        }
        
        
        for(int i =row; i <boardSize;i++)
        {
            for(int j=col;j<boardSize;j++)
            {

                value = nextPosition(i, j);

                if(!isConflicting(initialBoard, i, j))
                {

                    initialBoard[i][j]=1;
                    Lizzard.lizzard = Lizzard.lizzard+1;

                    prevI = i;
                    prevJ = j;
                    i=value[0];
                    j=value[1];
                    if(i>=boardSize||j>=boardSize)
                        return false;

                    if(DFS1(initialBoard,i,j) == true)
                    {

                        return true;
                    }
                    else
                    {

                        initialBoard[prevI][prevJ]=0;
                        Lizzard.lizzard = Lizzard.lizzard -1;

                        i = prevI;
                        j = prevJ;

                    }
                }

            }
            col=0;
         
        }
        return false;
    }


    public static boolean isConflicting(int initialBoard[][],int row, int col)
    {
        if (initialBoard[row][col] == 2)
        {

            return true;
        }

        if(initialBoard[row][col]==1)
        {
            return true;
        }
        
        for(int i =col;i>=0;i--)
        {
       
            if(initialBoard[row][i]==2)
                break;
            if(initialBoard[row][i]==1)
                return true;
        }

        for(int i =row;i>=0;i--)
        {
            if(initialBoard[i][col]==2)
                break;
            if(initialBoard[i][col]==1)
                return true;
        }
        //left top jaane wala
        for(int i=row,j=col ; i>=0&&j>=0 ; i--,j--)
        {
            if(initialBoard[i][j]==2)
                break;
            if(initialBoard[i][j]==1)
                return true;               
        }
        //left bottom jaane wala
        for(int i=row,j=col ; i<boardSize && j>=0 ; i++,j--)
        {
            if(initialBoard[i][j]==2)
                break;
            if(initialBoard[i][j]==1)
                return true;               
        }
        //right top jaane wala
        for(int i=row,j=col ; i>=0 && j<boardSize ; i--,j++) 
        { 
            if(initialBoard[i][j]==2)
                break;
            if(initialBoard[i][j]==1)
                return true;
        }

        return false;
     }
    

    public static boolean isConflictingBFS(Nursery nursery,int row, int col)
    {
        if (nursery.nursery[row][col] == 2)
        {

            return true;
        }

        if(nursery.nursery[row][col]==1)
        {

            return true;
        }
        
        for(int i =col;i>=0;i--)
        {

            if(nursery.nursery[row][i]==2)
                break;
            if(nursery.nursery[row][i]==1)
                return true;
        }

        for(int i =row;i>=0;i--)
        {

            if(nursery.nursery[i][col]==2)
                break;
            if(nursery.nursery[i][col]==1)
                return true;
        }
        //left top jaane wala
        for(int i=row,j=col ; i>=0&&j>=0 ; i--,j--)
        {

            if(nursery.nursery[i][j]==2)
                break;
            if(nursery.nursery[i][j]==1)
                return true;               
        }
        //left bottom jaane wala
        for(int i=row,j=col ; i<boardSize && j>=0 ; i++,j--)
        {
            if(nursery.nursery[i][j]==2)
                break;
            if(nursery.nursery[i][j]==1)
                return true;               
        }
        //right top jaane wala
        for(int i=row,j=col ; i>=0 && j<boardSize ; i--,j++) 
        { 
            if(nursery.nursery[i][j]==2)
                break;
            if(nursery.nursery[i][j]==1)
                return true;
        }

        return false;
     }
    

    public static boolean BFS() throws IOException
    {   

        Nursery nursery = new Nursery();
        
        if(nursery.numberOfLizards==lizzardCount)
        {
            
            print(nursery);
            
            return true;
        }
       
        double systemCall= System.currentTimeMillis();
        createChild(nursery);
        

        while(queue.size()!=0)
        {
            if((System.currentTimeMillis()-systemCall)>= 270000)
            {
                return false;
            }
            Nursery finalNursery=removeChild();
            if(createChild(finalNursery))
                return true;

        }
        
        
        return false; 
    }

    
    public static boolean createChild(Nursery n) throws IOException
    {

        int treesLeft=0;

        for(int z=n.row;z<boardSize;z++)
        {
            treesLeft+=treeCounterHashMap.get(z);
        }
        int rowsLeft = boardSize-n.row;
        if((lizzardCount-n.numberOfLizards)>(treesLeft+rowsLeft))
        {
            return false;
        }
        
        int i,j;
        int position[]=nextPosition(n.row,n.col);
        if(n.row==0&&n.col==0)
        {
            i=0;
            j=0;
        }
            
        else
        {
        i=position[0];
        j=position[1];            
        }
        

        for(i=i;i<boardSize;i++)
        {
            for(j=j;j<boardSize;j++)
            {
                
                if(!isConflictingBFS(n, i, j))
                {
                    Nursery childNursery=new Nursery(n);
                    childNursery.nursery[i][j]=1;
                    childNursery.row=i;
                    childNursery.col=j;
                    childNursery.numberOfLizards++;
                    if(Lizzard.lizzardCount==childNursery.numberOfLizards)
                    {
                        
                        print(childNursery);
                        
                        return true;
                    }

                    push(childNursery);
                    
                }    
                    
            }
            j=0;
        }

        
        return false;
    }
    
    
    
    public static Nursery removeChild()
    {
        Nursery poppedNursery=pop();
        return poppedNursery;        
    }
    
    public static Nursery removeChildDFS()
    {
        Nursery poppedNursery=popDFS();
        return poppedNursery;    
    }
    
    
    
    
    public static void push(Nursery n)
    {
        
        top++;
        queue.add(n);
    }
    
    public static Nursery pop()
    {

        Nursery n = queue.remove(front);        

        return n;
    }
    
    public static Nursery popDFS()
    {

        Nursery n = queue.remove(top--);
            
        return n;
    }

    
    
    static void SA() throws IOException
    {
        PrintWriter pw = new PrintWriter("output.txt","UTF-8");
        Nursery currentStateInitial = createinitialSolution();
        
        Random random=new Random();
        double probability= random.nextDouble();
        double Temperature = 100;
        double alpha=0.95;
        double deltaEnergy;
        int nextStateConflicts;
        Nursery currentState;
        double currentTime= System.currentTimeMillis();
        int currentStateConflicts;
        double value;
        boolean flag=true;
        while(Temperature>=0 && (System.currentTimeMillis()-currentTime)<240000)
        {               
            if(flag&& (System.currentTimeMillis()-currentTime)>= 120000)
            {   
                flag=false;
                if(DFS1(middleBoard,0,0))
                {
                    pw.println("OK");
                    for(int i=0; i < boardSize;i++)
                    {
                        for(int j =0;j<boardSize;j++)
                        {
                            pw.print(middleBoard[i][j]);
                        }
                        pw.println();
                        
                    }
                        pw.close();
                        System.exit(0);
                }
            }
            currentStateConflicts = checkNumberofConflicts(currentStateInitial);        
            Nursery nextState = createNewState(currentStateInitial);
            nextStateConflicts = checkNumberofConflicts(nextState);
            if(nextStateConflicts==0)
            {   
                print(nextState);
                System.exit(0);
            }

            deltaEnergy = nextStateConflicts - currentStateConflicts;

            probability=Math.random();
            if(deltaEnergy < 0)
            {
                currentStateInitial= new Nursery(nextState,1);
            }
            else
            {
                value = Math.exp(-deltaEnergy/Temperature);
                BigDecimal value1 = new BigDecimal(value);
                BigDecimal probability1 = new BigDecimal(probability);
                int greater = value1.compareTo(probability1);
                if(greater >=0)
                {
                    currentStateInitial= new Nursery(nextState,1);
                }
                else
                {
                    currentStateInitial= new Nursery(currentStateInitial,1);

                }
            }
            Temperature=Math.log10(1+1/Temperature);
        }
        pw.println("FAIL");
        pw.close();
        System.exit(0); 
    }
    public static void print(Nursery n) throws IOException
    {
        PrintWriter pw = new PrintWriter("output.txt","UTF-8");

        pw.println("OK");
        for(int i=0;i<boardSize;i++)
        {
            for(int j=0;j<boardSize;j++)
            {

                pw.print(n.nursery[i][j]);
            }
            pw.println("");
        }
        pw.close();
    }
    
   
    static Nursery createNewState(Nursery nursery) throws IOException
    {
        
        Random random=new Random();
        int toBePickedQueen=random.nextInt(nursery.placedLizzards.size());

       
        //random coodinatesd dhundh having place p lizzards already
        int toBePlacedPosition[]=randomCoordinatesGenerator(nursery);
        
        //ek random queen ko nikal
        int position[]=nursery.placedLizzards.remove(toBePickedQueen);
        nursery.nursery[position[0]][position[1]]=0;
       
        //jo position mili thi usmai add kar
        nursery.nursery[toBePlacedPosition[0]][toBePlacedPosition[1]]=1;
       
        nursery.placedLizzards.add(toBePlacedPosition);
       
        return nursery;
    }
 
   
    static Nursery createinitialSolution() throws IOException
    {
        Nursery n= new Nursery(1);               

        here:
        while(n.numberOfLizards<lizzardCount)
        {       

               
            int placement[]=randomCoordinatesGenerator(n);               
            n.nursery[placement[0]][placement[1]]=1;
            n.numberOfLizards++;

            n.placedLizzards.add(placement);
        }
          
       
        return n;
    }
   
    static int[] randomCoordinatesGenerator(Nursery nursery) throws IOException
    {
        PrintWriter pw = new PrintWriter("output.txt","UTF-8");
        int[] coordinates = new int [2];
        Random random = new Random();
        boolean flag =true;
       
        int i=0;
        coordinates[0]=random.nextInt(boardSize);
        coordinates[1]=random.nextInt(boardSize);

        while(flag && i<(boardSize*boardSize))
        {
            if(nursery.nursery[coordinates[0]][coordinates[1]]!=1 && nursery.nursery[coordinates[0]][coordinates[1]]!=2)
            {
                flag=false;
                break;
            }
            else
            {
                coordinates[0]=random.nextInt(boardSize);
                coordinates[1]=random.nextInt(boardSize);
            }
            i++;
            
        }
        superhere:
        if(flag==true)
        {   
            for(int j=0;j<boardSize;j++)
            {
                for(int k=0;k<boardSize;k++)
                {
            
                    if(nursery.nursery[j][k]!=1 && nursery.nursery[j][k]!=2)
                    {
                        coordinates[0]=j;
                        coordinates[1]=k;
                        flag  = false;
                        break superhere;
                    }
                }
            }
        }   

        if(flag==true)
        {
            
            pw.println("FAIL");
            pw.close();
            System.exit(0);
        }

        return coordinates;
    }
   
    static int checkNumberofConflicts(Nursery nursery)
    {
        int count = 0;
        for(int row=0;row<boardSize;row++)
        {
            for(int col=0;col<boardSize;col++)
            {
                if(nursery.nursery[row][col]==1)
                {
                    int i=row;
                    int j=col;

                    //left dekhne wala
                    i=col-1;
                    for( i =i;i>0;i--)
                    {
                        if(nursery.nursery[row][i]==2)
                            break;
                        if(nursery.nursery[row][i]==1)
                        {
                            count++;

                            break;
                        }
                    }
                    //right dekhne wala
                    i=col+1;
                    for(i =i;i<boardSize;i++)
                    {
                       
                        if(nursery.nursery[row][i]==2)
                            break;
                        if(nursery.nursery[row][i]==1)
                        {
                            count++;

                            break;
                        }
                    }
                   
                    //upar side mai dekhne wala
                    i=row-1;
                    for(i =i;i>=0;i--)
                    {
                        if(nursery.nursery[i][col]==2)
                            break;
                        if(nursery.nursery[i][col]==1)
                        {
                            count++;

                            break;
                        }
                    }
                    //niche side mai dekhne wala
                    i=row+1;
                    for(i =i;i<boardSize;i++)
                    {
                        if(nursery.nursery[i][col]==2)
                            break;
                        if(nursery.nursery[i][col]==1)
                        {
                            count++;

                            break;
                        }
                    }
                   
                    //left top jaane wala
                    i=row-1;
                    j=col-1;
                    for(i=i,j=j ; i>=0&&j>=0 ; i--,j--)
                    {
                        if(nursery.nursery[i][j]==2)
                            break;
                        if(nursery.nursery[i][j]==1)
                        {
                            count++;

                            break;
                        }
                    }
                    //left bottom jaane wala
                    i=row+1;
                    j=col-1;
                    for(i=i,j=j ; i<boardSize && j>=0 ; i++,j--)
                    {
                        if(nursery.nursery[i][j]==2)
                            break;
                        if(nursery.nursery[i][j]==1)
                        {
                            count++;

                            break;
                        }
                    }
                    //right top jaane wala
                    i=row-1;
                    j=col+1;
                    for(i=i,j=j ; i>=0 && j<boardSize ; i--,j++)
                    {
                        if(nursery.nursery[i][j]==2)
                            break;
                        if(nursery.nursery[i][j]==1)
                        {
                            count++;

                            break;
                        }
                    }
                    //rightbottom wala
                    i=row+1;
                    j=col+1;
                    for(i=i,j=j ; i<boardSize && j<boardSize ; i++,j++)
                    {  
                        if(nursery.nursery[i][j]==2)
                            break;
                        if(nursery.nursery[i][j]==1)
                        {
                            count++;

                            break;
                        }
                    }
   
                }
            }
        }
        return count;
    }  
}

class Nursery
{
    int row;
    int col;
    int nursery[][]=new int[Lizzard.boardSize][Lizzard.boardSize];
    int numberOfLizards=0;
    ArrayList<int []> placedLizzards;
    Nursery()
    {
        nursery=Lizzard.initialBoard;
        numberOfLizards=0;
    }
    Nursery(int x)
    {
        nursery=Lizzard.initialBoard;
        numberOfLizards=0;
        placedLizzards =new ArrayList<int []>();
    }
    Nursery(Nursery nursery)
    {

        for(int i=0;i<Lizzard.boardSize;i++)
        {
            System.arraycopy(nursery.nursery[i], 0, this.nursery[i], 0, Lizzard.boardSize);
            
        } 
        this.numberOfLizards=nursery.numberOfLizards;        
    } 
    Nursery(Nursery nursery,int x)
    {

        for(int i=0;i<Lizzard.boardSize;i++)
        {
            System.arraycopy(nursery.nursery[i], 0, this.nursery[i], 0, Lizzard.boardSize);
        } 
        this.numberOfLizards=nursery.numberOfLizards;
        this.placedLizzards=nursery.placedLizzards;
    }
}