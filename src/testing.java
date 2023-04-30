import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

class Main2 {
    public static Scanner scanner;
    public static Random rnd;

    public static void battleshipGame() {

        /** board issues */
        System.out.println("Enter the board size:");
        String[] sizeStr = scanner.nextLine().split("X");
        int n = Integer.parseInt(sizeStr[0]);
        int m = Integer.parseInt(sizeStr[1]);

        /** making 2 boards and 2 guessing boards */
        String[][] userGuessBoard = makeBoard(n, m);
        String[][] compGuessBoard = makeBoard(n, m);
        String[][] userBoard = makeBoard(n, m);
        String[][] compBoard = makeBoard(n, m);

        /** making array of battleships*/
        System.out.println("Enter the battleships sizes:");
        String[] battleships = scanner.nextLine().split(" ");

        /**initializing user & computer boards and set the total number
         * of battleship into "totalBattleships" variable */
        int totalBattleships = initializeUserBoard(userBoard, battleships, n, m);
        initializeComputerBoard(compBoard, battleships, n, m);

        /**
         Creating new array of total battleships that still in the game
         battleshipState[0] for the user
         battleshipState[1] for the computer
         */
        int[] battleshipState = {totalBattleships, totalBattleships};

        /** Attack Mode */
        while(true) {
            /**User attack the computer now. */
            userAttackComputer(userGuessBoard, compBoard, compGuessBoard, battleshipState, n, m);
            if(battleshipState[1] == 0){
                System.out.println("You won the game!");
                break;
            }
            /**Computer attack the user now. */
            computerAttackUser(compGuessBoard, userBoard, userGuessBoard, battleshipState, n, m);
            if(battleshipState[0] == 0){
                System.out.println("You lost ):");
                break;
            }
        }
    }

    // TEST TEST TEST
    public static int initializeUserBoardNew (String[][] userBoard, String[] battleships, int n, int m){
        int totalBattleships = 0;
        for (String s : battleships) {
            String[] currentBattleship = s.split("X");
            /** get the number and sizes of the current battleships */
            int numCurrentBattleship = Integer.parseInt(currentBattleship[0]);
            int currentSizeBattleship = Integer.parseInt(currentBattleship[1]);

            /** total battleships in the game */
            totalBattleships += numCurrentBattleship;

            /** make another loop for the number of the current size */
            for (int i = 0; i < numCurrentBattleship; i++) {
                int orientation = -1;
                printGameBoard(userBoard);
                System.out.println("Enter location and orientation for battleship size " + currentSizeBattleship);
                // the next do while will continue run till all the three parameters of the ship are correct
                do {
                    String[] battleshipInfo = scanner.nextLine().split(", ");
                    int rowBattleship = Integer.parseInt(battleshipInfo[0].trim());
                    int colBattleship = Integer.parseInt(battleshipInfo[1].trim());
                    orientation = Integer.parseInt(battleshipInfo[2].trim());

                    if (!checkCorrectPlacement(userBoard, currentSizeBattleship, rowBattleship, colBattleship, orientation)) {
                        continue;
                    }
                    putInBoard(userBoard, rowBattleship, colBattleship, orientation, currentSizeBattleship);
                    System.out.println("Your current game board:");
                    printGameBoard(userBoard);
                } while (orientation == -1);
            }
        }
        return totalBattleships;
    }



    /**
     * Initializes the user's game board by prompting the user to enter the location and orientation of each battleship.
     * checks if the location, orientation, and size of each battleship are correct before placing it on the board.
     *
     * @param userBoard   The game board to initialize
     * @param battleships An array of strings representing the number and size of each battleship to place on the board
     * @param n           The number of rows in the game board
     * @param m           The number of columns in the game board
     * @return The total number of battleships placed on the board
     */


    public static int initializeUserBoard (String[][] userBoard, String[] battleships, int n, int m){
        int totalBattleships = 0;
        for (String s : battleships) {
            String[] currentBattleship = s.split("X");
            /** get the number and sizes of the current battleships */
            int numCurrentBattleship = Integer.parseInt(currentBattleship[0]);
            int currentSizeBattleship = Integer.parseInt(currentBattleship[1]);

            /** total battleships in the game */
            totalBattleships += numCurrentBattleship;

            /** make another loop for the number of the current size */
            for (int i = 0; i < numCurrentBattleship; i++) {
                boolean correctPlacement;
                printGameBoard(userBoard);
                System.out.println("Enter location and orientation for battleship size " + currentSizeBattleship);
                // the next do while will continue run till all the three parameters of the ship are correct
                do {
                    String[] battleshipInfo = scanner.nextLine().split(", ");
                    int rowBattleship = Integer.parseInt(battleshipInfo[0].trim());
                    int colBattleship = Integer.parseInt(battleshipInfo[1].trim());
                    int orientation = Integer.parseInt(battleshipInfo[2].trim());
                    correctPlacement = checkCorrectPlacement(userBoard, currentSizeBattleship, rowBattleship, colBattleship, orientation);

                    if (!correctPlacement) {
                        continue;
                    }
                    putInBoard(userBoard, rowBattleship, colBattleship, orientation, currentSizeBattleship);
                    System.out.println("Your current game board:");
                    printGameBoard(userBoard);
                } while (!correctPlacement);
            }
        }
        return totalBattleships;
    }


    /**
     *
     * @param userBoard
     * @param userGuessBoard
     * @param compGuessBoard
     * @param battleshipState
     * @param n
     * @param m
     */
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
                if (!isAttackMissed(userBoard, rowBattleship, colBattleship)) {
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

    /**
     *
     * @param userGuessBoard
     * @param compBoard
     * @param compGuessBoard
     * @param battleshipState
     * @param n
     * @param m
     */
    public static void userAttackComputer(String[][] userGuessBoard, String[][] compBoard, String[][] compGuessBoard,
                                          int[] battleshipState, int n, int m) {
        System.out.println("Your current guessing board:");
        printGameBoard(userGuessBoard);
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
                if (!isAttackMissed(compBoard, rowAttack, colAttack)) {
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

    /**
     *
     * @param compBoard
     * @param battleships
     * @param n
     * @param m
     */
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

    /**
     *
     * @param board
     * @param rowBattleship
     * @param colBattleship
     * @param orientation
     * @param currentSizeBattleship
     */
    // Putting BattleShip into the user's GameBoard
    public static void putInBoard(String[][] board,int rowBattleship,int colBattleship,int orientation, int currentSizeBattleship){
        int HORIZONTAL = 0;
        if(orientation == HORIZONTAL){
            for(int i=0; i<currentSizeBattleship; i++) {
                board[rowBattleship][colBattleship + i] = "#";
            }
        }
        else {
            /** Now we know the orientation is vertical */
            for(int i=0; i<currentSizeBattleship; i++) {
                board[rowBattleship + i][colBattleship] = "#";
            }
        }
    }

    /**
     * Receives coordinates of a battleship we want to put on the game board
     * and checks if they are in the boundary of the board
     * @param row The number of rows in the game board
     * @param col The number of columns in the game board
     * @param rowBattleship The row of the first tile we want this battleship to be placed
     * @param colBattleship The columns of the first tile we want this battleship to be placed
     * @return True if the tile is inside the boundaries of the game board,
     * false otherwise
     */
    public static boolean checkStartingTile(int row, int col, int rowBattleship, int colBattleship) {
        if (rowBattleship < 0 || rowBattleship >= row || colBattleship < 0 || colBattleship >= col)
            return false;
        return true;
    }

    /**
     * Update a given board according to the coordinate and the sign
     * @param board
     * @param row
     * @param col
     * @param sign
     */
    public static void updateBoard(String[][] board, int row, int col, String sign){
        board[row][col] = sign;
    }

    /**
     * Checks if the coordinates already been attacked
     * using the guessing board, and the tile
     * @param board The guessing board
     * @param row The row of an attack tile
     * @param col The column of an attack tile
     * @return True if the tile has been attacked before,
     * false, otherwise
     */
    public static boolean isAlreadyBeenAttacked(String[][] board, int row, int col){
        return !board[row][col].equals("-");
    }

    /**
     * Check if the attack missed a battleship
     * using the game board and the tile
     * @param board The private game board
     * @param row The row of an attack tile
     * @param col The column of an attack tile
     * @return True for miss, false for a hit
     */
    public static boolean isAttackMissed(String[][] board, int row, int col){
        return board[row][col].equals("-");
    }

    /**
     * counts the number of digit in number
     * @param num The number we want to check its digits
     * @return The number of digit
     */
    public static int digitCount(int num) {
        int count = 0;
        while (num != 0) {
            num /= 10;
            count++;
        }
        return count;
    }

    /**
     * Makes matrix of the board that filled with "-" using the row and column input
     * @param n The number of rows
     * @param m The number of columns
     * @return The matrix of the board filled with "-"
     */
    public static String[][] makeBoard(int n, int m) {
        String[][] board = new String[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                board[i][j] = "-";
            }
        }
        return board;
    }

    /**
     * Prints the matrix of the board with numbering and spaces between
     * @param currentboard The board we want to print
     */
    public static void printGameBoard(String[][] currentboard) {
        int numSpaces = digitCount(currentboard.length);
        String spaces = "";
        for (int s = 0; s < numSpaces; s++) {
            spaces += " ";
        }
        for (int i = 0; i < currentboard.length + 1; i++) {
            for (int j = 0; j < currentboard[0].length + 1; j++) {
                if(i==0&&j==0)
                    System.out.print("  ");
                else if(i==0)
                    System.out.print(j-1+" ");
                else if(j==0)
                    System.out.print(i-1+" ");
                else
                    System.out.print(currentboard[i-1][j-1]+" ");

            }
            System.out.println();
        }
    }

    /**
     * Checks the given orientation
     * @param input The given orientation
     * @return Return true for 1/0 otherwise, returns false
     */
    public static int checkOrientation(int input) {
        int HORIZONTAL = 0;
        int VERTICAL = 1;
        /** if the orientation is not 1/0 it will return -1 which is an error */
        int ERROR = -1;
        if (input == HORIZONTAL) {
            return HORIZONTAL;
        } else if (input == VERTICAL) {
            return VERTICAL;
        } else {
            return ERROR;
        }
    }

    // SOMETHING IS STILL WRONG IN THIS FUNCTION
    /**
     * Checks the overlap of battleships using the game board, the tile, and orientation
     * @param board The game board in the current state
     * @param sizeShip The size of a battleship we want to put on the board
     * @param row The row of the first tile we want this battleship to be placed
     * @param col The colum of the first tile we want this battleship to be placed
     * @param orientation The orientation we want this battleship to be placed
     *                    0- horizontal, 1- vertical
     * @return True if the ship can be placed otherwise, returns false
     */
    public static boolean checkOverlap(String[][] board, int sizeShip, int row, int col,  int orientation) {
        int HORIZONTAL = 0;
        String ALREADY_PLACED = "#";
        /** check for horizontal ship */
        if (orientation == HORIZONTAL) {
            // use the size of the ship to calculate the space
            for (int i = 0; i < sizeShip; i++) {
                if (((col + i) < board[0].length) && (board[row][col + 1].equals(ALREADY_PLACED))) {
                    return false;
                }
            }
            /** check for vertical ships */
        } else {
            for (int j = 0; j < sizeShip; j++) {
                if (((row + j) < board.length) && (board[row + j][col].equals(ALREADY_PLACED))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the battleship we want to place is on the game board boundaries,
     * using the size of the game board, the size of the battleship, the place we want to put the battleship
     * and battleship orientation
     * @param n Number of rows in the game board
     * @param m Number of columns in the game board
     * @param sizeBattleship The size of the battleship
     * @param rowBattleship The row of the first tile we want this battleship to be placed
     * @param colBattleship The column of the first tile we want this battleship to be placed
     * @param orientation The orientation we want this battleship to be placed
     *                    0- horizontal, 1- vertical
     * @return True if the battleship is inside the boundaries, false otherwise
     */
    public static boolean checkBoardBoundaries(int n, int m, int sizeBattleship, int rowBattleship, int colBattleship, int orientation) {
        int HORIZONTAL = 0;
        /** check for horizontal ship */
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < sizeBattleship; i++) {
                if ((colBattleship + i) > m) {
                    return false;
                }
            }
            /** check for vertical ship */
        } else {
            for (int i = 0; i < sizeBattleship; i++) {
                if ((rowBattleship + i) > n) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the tile of a battleship we want to place is located too close for other battleship
     * using the current game board, and the tile
     * @param board The current game board
     * @param row The row of the tile we want this battleship to be placed
     * @param col The column of the tile we want this battleship to be placed
     * @return True if no adjacent is found, false otherwise
     */
    public static boolean checkAdjacent(String[][] board, int row, int col) {
        /** MIN, MAX : the range of the board */
        int MIN = 0;
        int MAX = (board.length - 1);
        int TOP = -1;
        int LEFT = -1;
        int BOT = 1;
        int RIGHT = 1;
        /** check in range of 1 */
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

    /**
     * Checks the whole ship adjacent using the previous function checkAdjacent
     * using the current game board, the tile of the battleship, the size of the battleship
     * and orientation
     * @param board The current game board
     * @param row The row of the first tile we want this battleship to be placed
     * @param col The column of the first tile we want this battleship to be placed
     * @param size The size of the battleship
     * @param orientation The orientation we want this battleship to be placed
     *      *             0- horizontal, 1- vertical
     * @return True if there's no adjacent found, false otherwise
     */
    public static boolean checkAdjacentBattleship(String[][] board, int row, int col, int size, int orientation) {
        int HORIZONTAL = 0;
        /** for horizontal ship placement, check every tile of the ship */
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < size; i++) {
                if (!checkAdjacent(board, row, col + 1)) {
                    return false;
                }
            }
            /** for vertical ship placement, check every tile of the ship */
        } else {
            for (int i = 0; i < size; i++) {
                if (!checkAdjacent(board, row + 1, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the wanted placement of the battleship is correct using the previous check functions
     * and print and error message according to what it did not pass
     * @param board The game board we want the battleship to put on
     * @param size The size of the battleship
     * @param row The row of the first tile we want this battleship to be placed
     * @param column The column of the first tile we want this battleship to be placed
     * @param orientation The orientation of the battleship
     * @return True if all the tests are correct, false if at least one is incorrect
     */
    public static boolean checkCorrectPlacement(String[][] board, int size, int row, int column, int orientation) {
        int WRONG_ORIENTATION = -1;
        if (checkOrientation(orientation) == WRONG_ORIENTATION) {
            System.out.println("Illegal orientation, try again!");
            return false;
        } else if (!checkStartingTile(board.length, board[0].length, row, column)) {
            System.out.println("Illegal tile, try again!");
            return false;
        } else if (!checkBoardBoundaries(board.length, board[0].length, size, row, column, orientation)) {
            System.out.println("Battleship exceeds the boundaries of the board, try again!");
            return false;
        } else if (!checkOverlap(board, size, row, column, orientation)) {
            System.out.println("Battleship overlaps another battleship, try again!");
            return false;
        } else if (!checkAdjacentBattleship(board, row, column, size, orientation)) {
            System.out.println("Adjacent battleship detected, try again!");
            return false;
        }
        return true;
    }
    /**
     // check if hit a battleship
     public static boolean hitBattleship(int rowBattleship, int colBattleship, String[][] board) {
     String A_BATTLESHIP = "#";
     if (board[rowBattleship][colBattleship].equals(A_BATTLESHIP))
     return true;
     return false;
     } */

    /**
     * Check if a battleship is sunk by comparing the area of a hit between the guessing board
     * and the private game board, using the both boards and the tile of the last attack
     * @param rowBattleship The row of the tile that was hit
     * @param colBattleship The column of the tile that was hit
     * @param board The private game board
     * @param guessBoard The guessing board
     * @return True if a ship was sunk, false otherwise
     */

    public static boolean battleshipDrown (int rowBattleship, int colBattleship, String[][] board,
                                           String[][] guessBoard) {
        int MIN = 0;
        /** check horizontal to right */
        for (int i = colBattleship; i < board[MIN].length; i++){
            if (board[rowBattleship][i].equals("#") && !guessBoard[rowBattleship][i].equals("V"))
                return false;
        }
        /** check horizontal to left */
        for (int i = colBattleship; i >= MIN; i--){
            if (board[rowBattleship][i].equals("#") && !guessBoard[rowBattleship][i].equals("V"))
                return false;
            /**avoid negative indices */
            if (i == MIN)
                break;
        }
        /** check vertical to top */
        for (int j = rowBattleship; j >= MIN; j--){
            if (board[j][colBattleship].equals("#") && !guessBoard[j][colBattleship].equals("V"))
                return false;
        }
        /** check vertical to bot */
        for (int j = rowBattleship; j < board.length; j++){
            if (board[j][colBattleship].equals("#") && !guessBoard[j][colBattleship].equals("V"))
                return false;
            /**avoid negative indices */
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
