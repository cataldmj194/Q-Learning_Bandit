/** 
   Name : Michael Cataldo
   Class: CIS 421
   Assignment: Q-Learning
   Due: Phase 1 - Monday, November 21
        Phase 2 - Wednesday, December 7
*/
import java.util.*;
import java.io.*;
import java.awt.Point;

public class Q_learning{

  static int N;              //Our board size
  static int num_trolls;     //Number of trolls
  static int num_ponies;     //Number of ponies
  static Point esc_loc;      //Location of escape
  static Point burglar;      //Location of burglar
  static int epochs = 10000; //Maximum epochs
  static ArrayList<Point> pony_loc = new ArrayList<Point>();  //Pony locations
  static ArrayList<Point> obs_loc = new ArrayList<Point>();   //obstacles
  static ArrayList<Point> troll_loc = new ArrayList<Point>(); //troll locations
  static int [][] board;     //Environment

  public static void main(String[] args) throws InterruptedException{

    parseFile(args);
    buildBoard();
    printBoard();
    run();
  }

  //Postcondition: Our run loop. While our burglar has not been eaten
  //               by a troll or they have not found the escape location,
  //               continually explores the board, gathering rewards.
  //               Observes state, generates an action, prints board and total
  public static void run() throws InterruptedException{
    int total = 0;


    //TODO: Restructure. Q-learning algorithm is as follows:
    /** 1: Start 
        2: For the current state(s), select an action(a)
        3: Receive reward (r)
        5: For state(s) and action(a), update the Q value given the reward(r)
        6: Transition to new state S', repeat from step 2

        Observe state should not return a reward, it should return Q values
        of transition functions
 
        Selecting an action should return a reward.
    */
    while(true){
      total += observeState();
      if(troll_loc.contains(burglar) || burglar.equals(esc_loc))
        break;
      action();
      printBoard();
      System.out.println("Running Total: " + total);
      Thread.sleep(1000);
    }
    System.out.println("End condition met, total = : " + total);
  }

  /** Currently random, implement alternate action selection in later phase */
  //Postcondition: Randomly generates a move in one of the 8 valid directions.
  //               If move generated is illegal, continue to generate moves
  //               until we get one that is valid. Mark path taken with a 5
  //               which will be printed as a 'o'.
  public static void action(){
    Point tmp;
    int max = 1;
    int min = -1;

    do{
      tmp = new Point(burglar);
      Random rand = new Random();

      int res_x = rand.nextInt((max + 1) - min) + min;
      int res_y = rand.nextInt((max + 1)- min) + min;

      while(res_x == 0 && res_y == 0){
        res_x = rand.nextInt((max + 1) - min) + min;
        res_y = rand.nextInt((max + 1)- min) + min;
      }

      tmp.translate(res_x,res_y);
      
    }while(boundsCheck(tmp));

    board[burglar.x][burglar.y] = 5;
    burglar = tmp;
    board[burglar.x][burglar.y] = 4;
  }

  //Parameters: tmp - a Point that needs to have it's bounds verified.
  //Postcondition: Used to ensure we don't try and make a move off the board
  //               or onto an obstacle location.
  //Returns: True if the move generated is out of bounds or onto an obstacle
  //         False otherwise.
  public static boolean boundsCheck(Point tmp){
    return (tmp.x < 0) || (tmp.x > N-1) || 
           (tmp.y < 0) || (tmp.y > N-1) || 
           (obs_loc.contains(tmp));
  }

  //Postcondition: Observes state of the burglar. Returns reward of the
  //               state. -15 if we are on a troll, 10 if we are on a pony,
  //               15 if we are on the esc location and 2 otherwise.
  //Returns: Reward value for current state.
  public static int observeState(){
    if(troll_loc.contains(burglar))
      return -15;
    
    if(pony_loc.contains(burglar)){
      pony_loc.remove(burglar);     
      return 10;
    }
    if(burglar.equals(esc_loc))
      return 15;

    return 2;
  }

  //Postcondition: Evaluates if our burglar has reached an end condition
  //Returns: True if our burglar is on the esc location or on a troll
  public static boolean endCondition(){
    return (burglar.equals(esc_loc)) || (troll_loc.contains(burglar));
  }

  //Postcondition: Prints the current board configuration to standard out.
  //               (0,0) in the upper left.
  public static void printBoard(){

    printPadding();
    for(int i = 0; i < N; i++){
      System.out.print("##");
      for(int j = 0; j < N; j++){
        if(board[i][j] == 0)
          System.out.print(" - ");
        if(board[i][j] == 1)
          System.out.print(" P ");
        if(board[i][j] == -1)
          System.out.print(" X ");
        if(board[i][j] == 3)
          System.out.print(" T ");
        if(board[i][j] == 4)
          System.out.print(" B ");
        if(board[i][j] == 5)
          System.out.print(" o ");
        if(board[i][j] == 9)
          System.out.print(" E ");
      }
      System.out.print(" ##");
      System.out.println();
    }
    printPadding();

    /** for testing purposes */
    /*
    for(int i = 0; i < N; i++){
      for(int j = 0; j < N; j++){
        System.out.print(board[i][j] + " ");
      }
      System.out.println();
    } */
  }

  //Postcondition: Prints wall padding for board.
  public static void printPadding(){
    System.out.print("##");
    for(int i = 0; i < N; i++)
      System.out.print(" ##");
    System.out.println(" ##");
  }

  //Postcondition: initializes and builds our board configuration according to
  //               config details provided by input document.
  //               Randomly places burglar on board anywhere that doesn't 
  //               contain an obstacle.
  public static void buildBoard(){
    board = new int[N][N];
    for(Point p : pony_loc)
      board[p.x][p.y] = 1;
    for(Point o : obs_loc)
      board[o.x][o.y] = -1;
    for(Point t : troll_loc)
      board[t.x][t.y] = 3;
    board[esc_loc.x][esc_loc.y] = 9;
   
    Random rand = new Random();
    do{
      int x = rand.nextInt(N);
      int y = rand.nextInt(N);
      burglar = new Point(x,y);
    }while(obs_loc.contains(burglar));

    board[burglar.x][burglar.y] = 4;
  }

  //Parameters: args - a String [] provided from command line. Should contain
  //                   a file name to process at args[0] if used appropriately.
  //Postcondition: File is read from user input and we read from it the board
  //               configuration details for use in building the board. 
  public static void parseFile(String[] args){
    
    try{
      File f = new File(args[0]);
      Scanner sc = new Scanner(f);
      
      for(int i = 0; i < 5; i++){
        switch(i){
          case 0:
            N = sc.nextInt();
            num_trolls = sc.nextInt();
            num_ponies = sc.nextInt();
            break;
          case 1:
            esc_loc = new Point(sc.nextInt(),sc.nextInt());
            break;
          case 2:
            sc.nextLine();
            String ponyup = sc.nextLine();
            Scanner ps = new Scanner(ponyup);
            while(ps.hasNextInt()){
              int x = ps.nextInt();
              int y = ps.nextInt();
              pony_loc.add(new Point(x,y));
            }
            break;
          case 3:
            String obs_line = sc.nextLine();
            Scanner os = new Scanner(obs_line);
            while(os.hasNextInt()){
              int x = os.nextInt();
              int y = os.nextInt();
              obs_loc.add(new Point(x,y));
            }
            break;
          case 4:
            String troll_line = sc.nextLine();
            Scanner ts = new Scanner(troll_line);
            while(ts.hasNextInt()){
              int x = ts.nextInt();
              int y = ts.nextInt();
              troll_loc.add(new Point(x,y));
            }
            break;
        }
      }
           
    }catch(FileNotFoundException e){
      System.out.println(e);
      System.exit(0);
    }catch(IndexOutOfBoundsException e){
      System.out.println(e);
      System.exit(0);
    }
    
  }
}

