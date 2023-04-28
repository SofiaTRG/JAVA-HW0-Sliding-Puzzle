import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;
    public static void battleshipGame() {
        // TODO: Add your code here (and add more methods).

        // user input:
        // size of board
        System.out.println("Enter the board size:");
        String[] sizeStr = scanner.nextLine().split("x"); // Is java case-sensitive language?
        int n = Integer.parseInt(sizeStr[0]);
        int m = Integer.parseInt(sizeStr[1]);
        String [][] userBoard = makeBoard(n, m);
        String [][] compBoard = makeBoard(n, m);

        // ?should we check if the user actually put two integers? // Answer: no.

        // battleships size
        System.out.println("Enter the battleships sizes:");
        String[] battleships = scanner.nextLine().split(" ");

        // check location, orientation and size of battleship
        // this for loop is easier than using a loop with index
        for (String s : battleships) {
            String[] currentBattleship = s.split("x");
            // get the number and sizes of the current battleships
            int numCurrentBattleship = Integer.parseInt(currentBattleship[0]);
            int currentSizeBattleship = Integer.parseInt(currentBattleship[1]);
            // make another loop for the number of the current size
            for (int i = 0; i < numCurrentBattleship; i++) {
                int orientation;
                boolean tile;
                boolean boundaries;
                boolean overlap;
                boolean adjacent;
                printGameBoard(userBoard, n, m);
                System.out.println("Enter location and orientation for battleship size" + currentSizeBattleship);
                // the next do while will continue run till all the three parameters of the ship are correct
                do {
                    //We need to print the current board here I think.
                    String[] battleshipInfo = scanner.nextLine().split(", ");
                    int rowBattleship = Integer.parseInt(battleshipInfo[0].trim());
                    int colBattleship = Integer.parseInt(battleshipInfo[1].trim());
                    orientation = Integer.parseInt(battleshipInfo[2].trim());
                    // check for correct orientation
                    orientation = checkOrientation(orientation);
                    tile = checkStartingTile(n, m, rowBattleship, colBattleship);
                    boundaries = checkBoardBoundaries(n, m, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    overlap = checkOverlap(userBoard, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    adjacent = checkAdjacent(userBoard, rowBattleship, colBattleship);
                    // we got the correct orientation
                    if (orientation == -1) {
                        System.out.println("Illegal orientation, try again!");
                        // check if the chosen tile is inside the board
                        // MAYBE MAKE FUNCTIONS THAT CHECKS IF THE TILES ARE CORRECT
                    } else if(!tile){
                         System.out.println("Illegal tile, try again!");
                    } else if (!boundaries) {
                        System.out.println("Battleship exceeds the boundaries of the board, try again!");
                        // check overlap
                    } else if (!overlap) {
                        System.out.println("Battleship overlaps another battleship, try again!");
                    } else if (!adjacent) {
                        System.out.println("Adjacent battleship detected, try again!");
                    }
                    putInBoard(userBoard, rowBattleship, colBattleship, orientation, currentSizeBattleship);
                    System.out.println("Your current game board:");
                    printGameBoard(userBoard, n, m);
                } while (orientation == -1 || !boundaries || !overlap || !adjacent);
            }
        }
        initializeComputerBoard(compBoard, battleships, n, m);
        // check if the placing is correct (using Yaron's idea)
    }

    // initialize the computer battleships and place them on the board
    public static void initializeComputerBoard(String[][] compBoard,String[] battleships,int n, int m) {
        for (String s : battleships) {
            String[] currentBattleship = s.split("X");
            // get the number and sizes of the current battleships
            int numCurrentBattleship = Integer.parseInt(currentBattleship[0]);
            int currentSizeBattleship = Integer.parseInt(currentBattleship[1]);
            // make another loop for the number of the current size
            for (int i = 0; i < numCurrentBattleship; i++) {
                int orientation;
                boolean tile;
                boolean boundaries;
                boolean overlap;
                boolean adjacent;
                // the next do while will continue run till all the three parameters of the ship are correct
                do {
                    Random rnd = new Random();
                    int rowBattleship = rnd.nextInt(n);
                    int colBattleship = rnd.nextInt(m);
                    orientation = rnd.nextInt(2);
                    boundaries = checkBoardBoundaries(n, m, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    overlap = checkOverlap(compBoard, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    adjacent = checkAdjacent(compBoard, rowBattleship, colBattleship);
                    tile = checkStartingTile(n, m, rowBattleship, colBattleship);
                    if (!tile)
                        continue;
                    if (!boundaries)
                        continue;
                    if (!overlap)
                        continue;
                    if (!adjacent)
                        continue;
                    putInBoard(compBoard, rowBattleship, colBattleship, orientation, currentSizeBattleship);
                } while (!boundaries || !overlap || !adjacent);
            }
        }
    }

    // count how much digit in n to know how spaces to put on board
    public static int digitCount(int num) {
        int count = 0;
        while (num != 0) {
            num /= 10;
            count++;
        }
        return count;
    }

    // make the board
    public static String[][] makeBoard(int n, int m) {
        // m+1 and n+1 because the first row and column of the board are used for labels
        // fill the board with "-"
        String[][] board = new String[n+1][m+1];
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < m+1; j++) {
                board[i][j] = "-";  // Why not : '-' ?
            }
        }
        int space_Num = digitCount(n);
        // first tile as length of digit of n
        board[0][0] = "";
        for (int i = 0; i < space_Num; i++) {
            board[0][0] += " ";
        }
        // number the first row
        for (int j = 0; (j + 1) < (m + 1); j++) {
            board[0][j + 1] = j + "";
        }
        // number the first col
        for (int i = 0; (i + 1) < (n + 1); i++) {
            // count the spaces before each number
            int sumSpaces = space_Num - digitCount(i);
            board[i + 1][0] = "";
            // row number and spaces
            for (int j = 0; j < sumSpaces; j++) {
                board[i + 1][0] += " ";
            }
            board[i + 1][0] += i + "";
        }
        return board;
    }

    // print the board
    public static void printGameBoard(String[][]board, int row, int col){
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    // Putting BattleShip into the user's GameBoard
    public static void putInBoard(String[][] board,int rowBattleship,int colBattleship,int orientation, int currentSizeBattleship){
        int HORIZONTAL = 0;
        if(orientation == HORIZONTAL){
            for(int i=0; i<currentSizeBattleship; i++) {
                board[rowBattleship][colBattleship + i] = "# ";
            }
        }
        else {
            // Now we know the orientation is vertical
            for(int i=0; i<currentSizeBattleship; i++) {
                board[rowBattleship + i][colBattleship] = "# ";
            }
        }
    }

    // check for correct orientation
    public static int checkOrientation(int input) {
        int HORIZONTAL = 0;
        int VERTICAL = 1;
        // if the orientation is not 1/0 it will return -1 which is an error
        int ERROR = -1;
        if (input == HORIZONTAL) {
            return HORIZONTAL;
        } else if (input == VERTICAL) {
            return VERTICAL;
        } else {
            return ERROR;
        }
    }
    
    //check for starting tile
    public static boolean checkStartingTile(int row, int col, int rowBattleship, int colBattleship) {
        if (rowBattleship < 0 || rowBattleship >= row || colBattleship < 0 || colBattleship >= col)
            return false;
        return true;
    }
  
    // check overlap of battleships.
    // use the board, the size of ship, the tiles and the orientation
    public static boolean checkOverlap(String[][] board, int sizeShip, int row, int col,  int orientation) {
        int HORIZONTAL = 0;
        String ALREADY_PLACED = "#";
        // check for horizontal ship
        if (orientation == HORIZONTAL) {
            // use the size of the ship to calculate the space
            for (int i = 0; i < sizeShip; i++) {
                if (board[row][col + 1].equals(ALREADY_PLACED)) {
                    return false;
                }
            }
            // check for vertical ships
        } else {
            for (int j = 0; j < sizeShip; j++) {
                if (board[row + j][col].equals(ALREADY_PLACED)) {
                    return false;
                }
            }
        }
        return true;
    }

    // check if the battleship is inside the board
    // n,m are the sizes of the board
    // rowBattleship and colBattleship belongs for the battleship
    public static boolean checkBoardBoundaries(int n, int m, int sizeBattleship, int rowBattleship, int colBattleship, int orientation) {
        int HORIZONTAL = 0;
        // check for horizontal ship
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < sizeBattleship; i++) {
                if ((colBattleship + i) > m) {
                    return false;
                }
            }
            // check for vertical ship
        } else {
            for (int i = 0; i < sizeBattleship; i++) {
                if ((rowBattleship + i) > n) {
                    return false;
                }
            }
        }
        return true;
    }

    // check adjacent of battleships
    public static boolean checkAdjacent(String[][] board, int row, int col) {
        // MIN, MAX : the range of the board
        int MIN = 0;
        int MAX = (board.length - 1);
        int TOP = -1;
        int LEFT = -1;
        int BOT = 1;
        int RIGHT = 1;
        // check in range of 2
        for (int i = row + TOP; i <= row + BOT; i++) {
            for (int j = col + LEFT; j <= row + RIGHT; j++ ) {
                if ((i >= MIN) && (i <= MAX) && (j >= MIN) && (j <= MAX)){
                    if (board[i][j].equals("v")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        String path = args[0];
        scanner = new Scanner(new File(path));
        int numberOfGames = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Total of " + numberOfGames + " games.");

        for (int i = 1; i <= numberOfGames; i++) {
            scanner.nextLine();
            int seed = scanner.nextInt();
            rnd = new Random(seed);
            scanner.nextLine();
            System.out.println("Game number " + i + " starts.");
            battleshipGame();
            System.out.println("Game number " + i + " is over.");
            System.out.println("------------------------------------------------------------");
        }
        System.out.println("All games are over.");
    }
}



