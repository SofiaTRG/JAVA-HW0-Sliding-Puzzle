import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;

    public static void battleshipGame() {

        /* board issues */
        System.out.println("Enter the board size:");
        String[] sizeStr = scanner.nextLine().split("X");
        int n = Integer.parseInt(sizeStr[0]);
        int m = Integer.parseInt(sizeStr[1]);

        /* making 2 boards and 2 guessing boards */
        String[][] userGuessBoard = makeBoard(n, m);
        String[][] compGuessBoard = makeBoard(n, m);
        String[][] userBoard = makeBoard(n, m);
        String[][] compBoard = makeBoard(n, m);

        /* making array of battleships*/

        System.out.println("Enter the battleships sizes:");
        String[] battleships = scanner.nextLine().split(" ");

        //initializing user & computer boards and set the total number of battleship into "totalBattleships" variable.
        int totalBattleships = initializeUserBoard(userBoard, battleships, n, m);
        initializeComputerBoard(compBoard, battleships, n, m);

        /*
         Creating new array of total battleships that still in the game
         battleshipState[0] for the user
         battleshipState[1] for the computer
         */

        int[] battleshipState = {totalBattleships, totalBattleships};

        /* Attack Mode*/
        while(true) {
            //User attack the computer now.
            userAttackComputer(userGuessBoard, compBoard, compGuessBoard, battleshipState, n, m);
            if(battleshipState[1] == 0){
                System.out.println("You won the game!");
                break;
            }
            //Computer attack the user now.
            computerAttackUser(compGuessBoard, userBoard, userGuessBoard, battleshipState, n, m);
            if(battleshipState[0] == 0){
                System.out.println("You lost ):");
                break;
            }
        }
    }

    // initialize user's board and on the way returning the total amount of battleship in the current game.
    public static int initializeUserBoard (String[][] userBoard, String[] battleships, int n, int m){
        // check location, orientation and size of battleship
        // this for loop is easier than using a loop with index

        int totalBattleships = 0;

        for (String s : battleships) {
            String[] currentBattleship = s.split("X");
            // get the number and sizes of the current battleships
            int numCurrentBattleship = Integer.parseInt(currentBattleship[0]);
            int currentSizeBattleship = Integer.parseInt(currentBattleship[1]);

            //total battleships in the game
            totalBattleships += numCurrentBattleship;

            // make another loop for the number of the current size
            for (int i = 0; i < numCurrentBattleship; i++) {
                int orientation;
                boolean tile;
                boolean boundaries;
                boolean overlap;
                boolean adjacent;
                printGameBoard(userBoard, n, m);
                System.out.println("Enter location and orientation for battleship size " + currentSizeBattleship);

                // the next do while will continue run till all the three parameters of the ship are correct
                do {
                    String[] battleshipInfo = scanner.nextLine().split(", ");
                    int rowBattleship = Integer.parseInt(battleshipInfo[0].trim());
                    int colBattleship = Integer.parseInt(battleshipInfo[1].trim());
                    orientation = Integer.parseInt(battleshipInfo[2].trim());

                    orientation = checkOrientation(orientation);
                    tile = checkStartingTile(n, m, rowBattleship, colBattleship);
                    boundaries = checkBoardBoundaries(n, m, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    overlap = checkOverlap(userBoard, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    adjacent = checkAdjacentBattleship(userBoard, rowBattleship, colBattleship, currentSizeBattleship, orientation);

                    if (orientation == -1) {
                        System.out.println("Illegal orientation, try again!");
                        continue;
                    } else if (!tile) {
                        System.out.println("Illegal tile, try again!");
                        continue;
                    } else if (!boundaries) {
                        System.out.println("Battleship exceeds the boundaries of the board, try again!");
                        continue;
                    } else if (!overlap) {
                        System.out.println("Battleship overlaps another battleship, try again!");
                        continue;
                    } else if (!adjacent) {
                        System.out.println("Adjacent battleship detected, try again!");
                        continue;
                    }
                    putInBoard(userBoard, rowBattleship, colBattleship, orientation, currentSizeBattleship);
                    System.out.println("Your current game board:");
                    printGameBoard(userBoard, n, m);
                } while (orientation == -1 || !boundaries || !overlap || !adjacent || !tile);
            }
        }
        return totalBattleships;
    }

    public static void computerAttackUser(String[][] userBoard, String[][] userGuessBoard, String[][] compGuessBoard,
                                          int[] battleshipState, int n, int m) {

        boolean flag = true;
        do {
            int rowBattleship = rnd.nextInt(n);
            int colBattleship = rnd.nextInt(m);
            if (!isAlreadyBeenAttacked(compGuessBoard, rowBattleship, colBattleship)) {
                flag = false;
            }

            if (!flag) {
                System.out.println("The computer attacked (" + rowBattleship + ", " + colBattleship + ")");
                if (isAttackMissed(userBoard, rowBattleship, colBattleship)) {
                    System.out.println("That is a miss!");
                    updateBoard(compGuessBoard, rowBattleship, colBattleship, "X");
                } else {
                    System.out.println("That is a hit!");
                    updateBoard(compGuessBoard, rowBattleship, colBattleship, "V");
                    updateBoard(userBoard, rowBattleship, colBattleship, "X");
                }
                if (battleshipDrown(rowBattleship, colBattleship, userBoard, userGuessBoard)) {
                    System.out.println("Your battleship has been drowned, You have left " + (--battleshipState[0]) + " more battleships!");
                }
            }
        } while (flag);
    }

    public static void userAttackComputer(String[][] userGuessBoard, String[][] compBoard, String[][] compGuessBoard,
                                          int[] battleshipState, int n, int m) {
        System.out.println("Your current guessing board:");
        printGameBoard(userGuessBoard, n, m);
        System.out.println("Enter a tile to attack");
        int badScan = 1;
        while (badScan == 1) {
            String[] userAttackCord = scanner.nextLine().split(", ");
            int rowAttack = Integer.parseInt(userAttackCord[0]);
            int colAttack = Integer.parseInt(userAttackCord[1]);
            if (!checkStartingTile(n, m, rowAttack, colAttack)) {
                System.out.println("Illegal tile, try again!");
            } else if (isAlreadyBeenAttacked(userGuessBoard, rowAttack, colAttack)) {
                System.out.println("Tile already attacked, try again!");
            } else {
                if (isAttackMissed(compBoard, rowAttack, colAttack)) {
                    System.out.println("That is a miss!");
                    updateBoard(userGuessBoard, rowAttack, colAttack, "X");
                } else {
                    System.out.println("That is a hit!");
                    updateBoard(userGuessBoard, rowAttack, colAttack, "V");
                    updateBoard(compBoard, rowAttack, colAttack, "X");
                    if (battleshipDrown(rowAttack, colAttack, compBoard, compGuessBoard)) {
                        System.out.println("The computer's battleship has been drowned, " + (--battleshipState[1])
                                + " more battleships to go!");
                    }
                }
                badScan = 0;
            }
        }
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
                } while (!boundaries || !overlap || !adjacent || !tile);
            }
        }
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
                board[rowBattleship][colBattleship + i] = "#";
            }
        }
        else {
            // Now we know the orientation is vertical
            for(int i=0; i<currentSizeBattleship; i++) {
                board[rowBattleship + i][colBattleship] = "#";
            }
        }
    }

    //This function receives coordinates and checks if they are in the boundary of the board
    public static boolean checkStartingTile(int row, int col, int rowBattleship, int colBattleship) {
        if (rowBattleship < 0 || rowBattleship >= row || colBattleship < 0 || colBattleship >= col)
            return false;
        return true;
    }

    //this function update a given board according to the coordinate and the sign
    public static void updateBoard(String[][] board, int row, int col, String sign){
        board[row][col] = sign;
    }

    //checks if the coordinates already been attacked in the past
    public static boolean isAlreadyBeenAttacked(String[][] board, int row, int col){
        return !board[row][col].equals("-");
    }

    //check if the attack missed a battleship
    public static boolean isAttackMissed(String[][] board, int row, int col){
        return board[row][col].equals("-");
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
        for (int i = 1; i <= n; i++) {  // fixed the loop from i<n+1
            for (int j = 1; j <= m; j++) {  // fixed the loop from j < m
                board[i][j] = "-";
            }
        }
        int space_Num = digitCount(n);
        // first tile as length of digit of n
        board[0][0] = "";
        for (int i = 0; i < space_Num; i++) {
            board[0][0] += " ";
        }
        // number the first row
        for (int j = 1; j <= m; j++) {  // fixed starts fron 0
            board[0][j] = (j-1) + "";
        }
        // number the first col
        for (int i = 1; i <= n; i++) {
            // count the spaces before each number
            int sumSpaces = space_Num - digitCount(i-1);
            board[i][0] = "";
            // row number and spaces
            for (int j = 0; j < sumSpaces; j++) {
                board[i][0] += " ";
            }
            board[i][0] += (i-1) + "";
        }
        return board;
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

    // check adjacent of one tile of battleships
    public static boolean checkAdjacent(String[][] board, int row, int col) {
        // MIN, MAX : the range of the board
        int MIN = 0;
        int MAX = (board.length - 1);
        int TOP = -1;
        int LEFT = -1;
        int BOT = 1;
        int RIGHT = 1;
        // check in range of 1
        for (int i = row + TOP; i <= row + BOT; i++) {
            for (int j = col + LEFT; j <= row + RIGHT; j++ ) {
                if ((i >= MIN) && (i <= MAX) && (j >= MIN) && (j <= MAX)){
                    if (board[i][j].equals("#")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // check adjacent of whole battleships
    public static boolean checkAdjacentBattleship(String[][] board, int row, int col, int size, int orientation) {
        int HORIZONTAL = 0;
        // for horizontal ship placement
        // check every tile of the ship
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < size; i++) {
                if (!checkAdjacent(board, row, col + 1)) {
                    return false;
                }
            }
            // for vertical ship placement
            // check every tile of the ship
        } else {
            for (int i = 0; i < size; i++) {
                if (!checkAdjacent(board, row + 1, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    // check if hit a battleship
    public static boolean hitBattleship(int rowBattleship, int colBattleship,String[][] board) {
        String A_BATTLESHIP = "#";
        if (board[rowBattleship][colBattleship].equals(A_BATTLESHIP))
            return true;
        return false;
    }

    // check if a battleship sunk
    // it checks if after the attack around the tile the same tiles on the guessing board are the same the player's board
    public static boolean battleshipDrown(int rowBattleship, int colBattleship, String[][] board, String[][] guessBoard) {
        int MIN = 0;
        // check horizontal to right
        for (int i = colBattleship; i < board[MIN].length; i++){
            if (board[rowBattleship][i].equals("#") && !guessBoard[rowBattleship][i].equals("V"))
                return false;
        }
        // check horizontal to left
        for (int i = colBattleship; i >= MIN; i--){
            if (board[rowBattleship][i].equals("#") && !guessBoard[rowBattleship][i].equals("V"))
                return false;
            //avoid negative indices
            if (i == MIN)
                break;
        }
        // check vertical to top
        for (int j = rowBattleship; j >= MIN; j--){
            if (board[j][colBattleship].equals("#") && !guessBoard[j][colBattleship].equals("V"))
                return false;
        }
        // check vertical to bot
        for (int j = rowBattleship; j < board.length; j++){
            if (board[j][colBattleship].equals("#") && !guessBoard[j][colBattleship].equals("V"))
                return false;
            //avoid negative indices
            if (j == MIN)
                break;
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