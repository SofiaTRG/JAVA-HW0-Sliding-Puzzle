import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;

    public static void battleshipGame() {

        /** extract the dimension of the board: n for rows, m for the columns. */

        System.out.println("Enter the board size");
        String[] sizeStr = scanner.nextLine().split("X");
        int n = Integer.parseInt(sizeStr[0]);
        int m = Integer.parseInt(sizeStr[1]);

        /** making 2 boards and 2 guessing boards for both the user and the computer. */
        String[][] userGuessBoard = makeBoard(n, m);
        String[][] computerGuessBoard = makeBoard(n, m);
        String[][] userBoard = makeBoard(n, m);
        String[][] computerBoard = makeBoard(n, m);

        /** making array of battleships. */
        System.out.println("Enter the battleships sizes");
        String[] battleships = scanner.nextLine().split(" ");

        /** initializing user & computer boards and set the total number
         * of battleship into "totalBattleships" variable. */

        int totalBattleships = initializeUserBoard(userBoard, userGuessBoard, battleships);
        initializeComputerBoard(computerBoard, battleships, n, m);

        /**
         Creating new array of total battleships that still in the game
         battleshipState[0] for the user
         battleshipState[1] for the computer
         */

        int[] battleshipState = {totalBattleships, totalBattleships};

        /** Alert : War zone!!!
         The user attacks first, then the computer, and so on until every submarine of one of the players is sunk. */

        while(true) {
            /**User attack the computer now. */
            userAttackComputer(userGuessBoard, computerBoard, battleshipState, n, m);
            if(battleshipState[1] == 0){
                System.out.println("You won the game!");
                break;
            }

            /**Computer attack the user now. */
            computerAttackUser(userBoard, computerGuessBoard, battleshipState, n, m);
            if(battleshipState[0] == 0){
                System.out.println("You lost ):");
                break;
            }
            System.out.println("Your current guessing board:");
            printGameBoard(userGuessBoard);
        }
    }

    /**
     * Initializes the user's game board by prompting the user to enter the location and orientation of each battleship.
     * checks if the location, orientation, and size of each battleship are correct before placing it on the board.
     * @param userBoard   The game board to initialize
     * @param userGuessBoard
     * @param battleships An array of strings representing the number and size of each battleship to place on the board
     * @return The total number of battleships placed on the board
     */

    public static int initializeUserBoard(String[][] userBoard, String[][] userGuessBoard, String[] battleships) {
        int totalBattleships = 0;

        System.out.println("Your current game board:");
        printGameBoard(userBoard);

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
                boolean validPlacement = false;

                System.out.println("Enter location and orientation for battleship size " + currentSizeBattleship);
                /** the next do while will continue run till all the three parameters of the ship are correct */
                do {
                    String[] battleshipInfo = scanner.nextLine().split(", ");
                    int rowBattleship = Integer.parseInt(battleshipInfo[0].trim());
                    int colBattleship = Integer.parseInt(battleshipInfo[1].trim());
                    orientation = Integer.parseInt(battleshipInfo[2].trim());

                    if (checkCorrectPlacement(userBoard, currentSizeBattleship, rowBattleship, colBattleship, orientation)) {
                        validPlacement = true;
                        putBattleshipInBoard(userBoard, rowBattleship, colBattleship, orientation, currentSizeBattleship);
                        System.out.println("Your current game board:");
                        printGameBoard(userBoard);
                    }
                } while (!validPlacement);

            }
        }
        System.out.println("Your current guessing board:");
        printGameBoard(userGuessBoard);
        return totalBattleships;
    }


    /**
     * This function is responsible for the computer attack on the user,
     * while taking into account the rules and the printing order of the game instructions
     * @param userBoard - user's game board
     * @param computerGuessBoard - computer's guessing board
     * @param battleshipState - an array of the amount of the battleship available to each player.
     * @param n - number of rows in the board
     * @param m - number of columns in the board
     */
    public static void computerAttackUser(String[][] userBoard, String[][] computerGuessBoard,
                                          int[] battleshipState, int n, int m) {

        boolean isValid = false;
        do {
            int rowBattleship = rnd.nextInt(n);
            int colBattleship = rnd.nextInt(m);
            if (!isAlreadyBeenAttacked(computerGuessBoard, rowBattleship, colBattleship)) {
                isValid = true;
            }

            if (isValid) {
                System.out.println("The computer attacked (" + rowBattleship + ", " + colBattleship + ")");
                if (isAttackMissed(userBoard, rowBattleship, colBattleship)) {
                    System.out.println("That is a miss!");
                    updateBoard(computerGuessBoard, rowBattleship, colBattleship, "X");
                } else {
                    System.out.println("That is a hit!");
                    updateBoard(computerGuessBoard, rowBattleship, colBattleship, "V");
                    updateBoard(userBoard, rowBattleship, colBattleship, "X");
                    if (battleshipDrown(rowBattleship, colBattleship, userBoard)) {
                        System.out.println("Your battleship has been drowned, you have left "
                                + (--battleshipState[0]) + " more battleships!");
                    }
                }
            }
        } while (!isValid);
        System.out.println("Your current game board:");
        printGameBoard(userBoard);
    }

    /**
     *This function is responsible for the user's attack on the computer,
     *  while taking into account the rules and the printing order of the game instructions
     * @param userGuessBoard - user guessing board
     * @param computerBoard - computer game board
     * @param battleshipState - An array of the amount of the battleship available to each player.
     * @param n - number of rows in the board
     * @param m - number of columns in the board
     */
    public static void userAttackComputer(String[][] userGuessBoard, String[][] computerBoard,
                                          int[] battleshipState, int n, int m) {

        System.out.println("Enter a tile to attack");
        boolean validScan = false;

        while (!validScan) {
            String[] userAttackCord = scanner.nextLine().split(", ");
            int rowAttack = Integer.parseInt(userAttackCord[0]);
            int colAttack = Integer.parseInt(userAttackCord[1]);
            if (!checkStartingTile(n, m, rowAttack, colAttack)) {
                System.out.println("Illegal tile, try again!");
            } else if (isAlreadyBeenAttacked(userGuessBoard, rowAttack, colAttack)) {
                System.out.println("Tile already attacked, try again!");
            } else {
                if (isAttackMissed(computerBoard, rowAttack, colAttack)) {
                    System.out.println("That is a miss!");
                    updateBoard(userGuessBoard, rowAttack, colAttack, "X");
                } else {
                    System.out.println("That is a hit!");
                    updateBoard(userGuessBoard, rowAttack, colAttack, "V");
                    updateBoard(computerBoard, rowAttack, colAttack, "X");
                    if (battleshipDrown(rowAttack, colAttack, computerBoard)) {
                        System.out.println("The computer's battleship has been drowned, " + (--battleshipState[1])
                                + " more battleships to go!");
                    }
                }
                validScan = true;
            }
        }
    }

    /**
     * draw the computer's battleships tile and orientation until they're valid and place them on the board
     * @param computerBoard
     * @param battleships
     * @param n for rows
     * @param m for cols
     */

    public static void initializeComputerBoard(String[][] computerBoard, String[] battleships, int n, int m) {
        for (String s : battleships) {
            String[] currentBattleship = s.split("X");
            /** get the number and sizes of the current battleships */
            int numCurrentBattleship = Integer.parseInt(currentBattleship[0]);
            int currentSizeBattleship = Integer.parseInt(currentBattleship[1]);
            /** make another loop for the number of the current size */
            for (int i = 0; i < numCurrentBattleship; i++) {
                int orientation;
                boolean tile;
                boolean boundaries;
                boolean overlap;
                boolean adjacent;

                do {
                    int rowBattleship = rnd.nextInt(n);
                    int colBattleship = rnd.nextInt(m);
                    orientation = rnd.nextInt(2);
                    boundaries = checkBoardBoundaries(n, m, currentSizeBattleship, rowBattleship, colBattleship,
                            orientation);
                    overlap = checkOverlap(computerBoard, currentSizeBattleship, rowBattleship, colBattleship,
                            orientation);
                    adjacent = checkAdjacentBattleship(computerBoard, rowBattleship, colBattleship,
                            currentSizeBattleship, orientation);
                    tile = checkStartingTile(n, m, rowBattleship, colBattleship);
                    if (!tile)
                        continue;
                    if (!boundaries)
                        continue;
                    if (!overlap)
                        continue;
                    if (!adjacent)
                        continue;
                    /** If we here, it's means that all is valid */
                    putBattleshipInBoard(computerBoard, rowBattleship, colBattleship, orientation, currentSizeBattleship);
                } while (!boundaries || !overlap || !adjacent || !tile);
            }

        }
    }


    /**
     * put a new battleShip into a given game board.
     * @param board a game board
     * @param rowBattleship - row of the tile we want to put the battleship
     * @param columnBattleship - column tile we want to put the battleship
     * @param orientation - orientation of the battleship.
     *                    0- for horizontal, 1- for vertical
     * @param currentSizeBattleship
     */

    public static void putBattleshipInBoard(String[][] board,int rowBattleship,
                                            int columnBattleship,int orientation, int currentSizeBattleship){
        int HORIZONTAL = 0;
        if(orientation == HORIZONTAL){
            for(int i=0; i<currentSizeBattleship; i++) {
                board[rowBattleship][columnBattleship + i] = "#";
            }
        }
        else {
            /** Now we know the orientation is vertical */
            for(int i=0; i<currentSizeBattleship; i++) {
                board[rowBattleship + i][columnBattleship] = "#";
            }
        }
    }

    /**
     * Receives coordinates of a battleship we want to put on the game board
     * and checks if they are in the boundary of the board
     * @param row The number of rows in the game board
     * @param column The number of columns in the game board
     * @param rowBattleship The row of the first tile we want this battleship to be placed
     * @param columnBattleship The columns of the first tile we want this battleship to be placed
     * @return True if the tile is inside the boundaries of the game board,
     * false otherwise
     */
    public static boolean checkStartingTile(int row, int column, int rowBattleship, int columnBattleship) {
        if (rowBattleship < 0 || rowBattleship >= row || columnBattleship < 0 || columnBattleship >= column)
            return false;
        return true;
    }

    /**
     * Update a given board according to the coordinate and the sign
     * @param board The board we want to update
     * @param row The row of the tile we want to change
     * @param column The column of the tile we want to change
     * @param sign The new sign we want to put in the tile
     */
    public static void updateBoard(String[][] board, int row, int column, String sign){
        board[row][column] = sign;
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
        return !board[row][col].equals("–");
    }

    /**
     * Check if the attack missed a battleship
     * using the game board and the tile
     * @param board The private game board
     * @param row The row of an attack tile
     * @param column The column of an attack tile
     * @return True for miss, false for a hit
     */
    public static boolean isAttackMissed(String[][] board, int row, int column){
        return board[row][column].equals("–");
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
     * Makes matrix of the board that filled with "–" using the row and column input
     * @param n The number of rows
     * @param m The number of columns
     * @return The matrix of the board filled with "–"
     */
    public static String[][] makeBoard(int n, int m) {
        String[][] board = new String[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                board[i][j] = "–";
            }
        }
        return board;
    }

    /**
     * Prints the matrix of the board with numbering and spaces between
     * @param currentBoard The board we want to print
     */
    public static void printGameBoard(String[][] currentBoard) {
        /** calculate the max of spaces for the numbering of rows and columns */
        int maxNumSpacesRow = digitCount(currentBoard.length-1)-1;
        int maxNumSpacesColumn = digitCount(currentBoard[0].length-1);

        /** the min of spaces between the columns is 1 */
        if (maxNumSpacesColumn < 0) {
            maxNumSpacesColumn = 1;
        }

        int currentNumDigit;
        for (int i = 0; i < currentBoard.length + 1; i++) {
            for (int j = 0; j < currentBoard[0].length + 1; j++) {
                if(i==0&&j==0) {
                    if (currentBoard.length == 1)
                        System.out.print(" ");
                    for (int k = 0; k < maxNumSpacesRow+1; k++) {
                        System.out.print(" ");
                    }
                }

                /** numbering the columns */
                else if(i==0) {
                    currentNumDigit = digitCount(j-1);
                    if ((j-1) == 0) {
                        currentNumDigit = 1;
                    }
                    for (int k = 0; k < (maxNumSpacesColumn-currentNumDigit)+1; k++) {
                        System.out.print(" ");
                    }
                    System.out.print(j - 1);
                }

                /** numbering the rows */
                else if(j==0) {
                    currentNumDigit = digitCount(i-1);
                    if ((i-1) == 0) {
                        currentNumDigit = 1;
                    }
                    for (int k = 0; k < (maxNumSpacesRow-currentNumDigit)+1; k++) {
                        System.out.print(" ");
                    }
                    System.out.print(i - 1);
                }

                else
                    System.out.print(" "+currentBoard[i-1][j-1]);

            }
            System.out.println();
        }
        System.out.println();
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

    /**
     * Checks the overlap of battleships using the game board, the tile, and orientation
     * @param board The game board in the current state
     * @param sizeShip The size of a battleship we want to put on the board
     * @param row The row of the first tile we want this battleship to be placed
     * @param column The colum of the first tile we want this battleship to be placed
     * @param orientation The orientation we want this battleship to be placed
     *                    0- horizontal, 1- vertical
     * @return True if the ship can be placed otherwise, returns false
     */
    public static boolean checkOverlap(String[][] board, int sizeShip, int row, int column,  int orientation) {
        int HORIZONTAL = 0;
        String ALREADY_PLACED = "#";

        /** check for horizontal ship */
        if (orientation == HORIZONTAL) {
            /** use the size of the ship to calculate the space */
            for (int i = 0; i < sizeShip; i++) {
                if (((column + i) < board[0].length) && (board[row][column + i].equals(ALREADY_PLACED))) {
                    return false;
                }
            }

            /** check for vertical ships */
        } else {
            for (int j = 0; j < sizeShip; j++) {
                if (((row + j) < board.length) && (board[row + j][column].equals(ALREADY_PLACED))) {
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
     * @param columnBattleship The column of the first tile we want this battleship to be placed
     * @param orientation The orientation we want this battleship to be placed
     *                    0- horizontal, 1- vertical
     * @return True if the battleship is inside the boundaries, false otherwise
     */
    public static boolean checkBoardBoundaries(int n, int m, int sizeBattleship, int rowBattleship,
                                               int columnBattleship, int orientation) {
        int HORIZONTAL = 0;
        /** check for horizontal ship */

        if (orientation == HORIZONTAL) {
            for (int i = 0; i < sizeBattleship; i++) {
                if ((columnBattleship + i) >= m) {
                    return false;
                }
            }

            /** check for vertical ship */
        } else {
            for (int i = 0; i < sizeBattleship; i++) {
                if ((rowBattleship + i) >= n) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * In a given coordinates, this function checks if the coordinates are out of boundary.
     * @param currentRow - row coordinate of the current tile
     * @param currentColumn - col coordinate of the current tile
     * @param minRow - row min limit
     * @param minColumn - col min limit
     * @param maxRow - row max limit
     * @param maxColumn - col max limit
     * @return True if
     */

    public static boolean outOfBound(int currentRow, int currentColumn, int minRow, int minColumn, int maxRow,
                                     int maxColumn){
        if(currentRow < minRow || currentRow > maxRow || currentColumn < minColumn || currentColumn > maxColumn)
            return true;
        return false;
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
        int MAX_COL = (board[0].length - 1);
        int MAX_ROW = (board.length - 1);

        for(int i=-1; i<=1; i++){
            for(int j=-1; j<=1; j++){
                if(outOfBound(row+i,col+j,MIN,MIN,MAX_ROW,MAX_COL))
                    continue;
                if(board[row+i][col+j].equals("#"))
                    return false;
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
     * @param column The column of the first tile we want this battleship to be placed
     * @param size The size of the battleship
     * @param orientation The orientation we want this battleship to be placed
     *      *             0- horizontal, 1- vertical
     * @return True if there's no adjacent found, false otherwise
     */
    public static boolean checkAdjacentBattleship(String[][] board, int row, int column, int size, int orientation) {
        int HORIZONTAL = 0;
        /** for horizontal ship placement, check every tile of the ship */
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < size; i++) {
                if (!checkAdjacent(board, row, column + i)) {
                    return false;
                }
            }
            /** for vertical ship placement, check every tile of the ship */
        } else {
            for (int i = 0; i < size; i++) {
                if (!checkAdjacent(board, row + i, column)) {
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
     *
     * @param rowBattleship The row of the tile that was hit
     * @param columnBattleship The column of the tile that was hit
     * @param board The private game board
     * @return True if a ship was sunk, false otherwise
     */
    public static boolean battleshipDrown (int rowBattleship, int columnBattleship, String[][] board) {
        int MIN = 0;
        /** check horizontal to right */
        for (int i = columnBattleship; i < board[MIN].length; i++){
            if (board[rowBattleship][i].equals("#")) {
                return false;
            }
            if (board[rowBattleship][i].equals("–"))
                break;
        }
        /** check horizontal to left */
        for (int i = columnBattleship; i >= MIN; i--){
            if (board[rowBattleship][i].equals("#")) {
                return false;
            }
            /**avoid negative indices */
            if ((i == MIN) || (board[rowBattleship][i].equals("–")))
                break;
        }
        /** check vertical to top */
        for (int j = rowBattleship; j >= MIN; j--){
            if (board[j][columnBattleship].equals("#")) {
                return false;
            }
            if (board[j][columnBattleship].equals("–"))
                break;
        }
        /** check vertical to bot */
        for (int j = rowBattleship; j < board.length; j++){
            if (board[j][columnBattleship].equals("#"))
                return false;
            /**avoid negative indices */
            if ((board[j][columnBattleship].equals("–")))
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