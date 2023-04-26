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
        String sizeBoard = scanner.nextLine();
        // cal the func extractNumbersFromStr to split to 2 integers
        int[] sizeBoardNum = strToIntArray(sizeBoard.split("X"));
        // n = rows, m = cols
        int n = sizeBoardNum[0];
        int m = sizeBoardNum[1];
        // making the board
        String [][] board = makeBoard(n, m);

        // ?should we check if the user actually put two integers?

        // battleships size
        System.out.println("Enter the battleships sizes:");
        String[] battleships = scanner.nextLine().split(" ");

        // now we'll use the data of the ships and put them on the board
        // location, orientation and size of battleship
        // this for loop is easier than using a loop with index
        // !THIS IS NOT FINALL!
        for (String s : battleships) {
            System.out.println("Enter location and orientation for battleship size" + s);
            String[] battleshipInfo = scanner.nextLine().split(", ");
            // make to array of int for easier access
            int[] battleshipInfoInt = strToIntArray(battleshipInfo);

            // !MAKE THE NEXT LINES INTO DO WHILE!

            // check for correct orientation
            boolean correctInfo;
            if (battleshipInfoInt[2] == 0){
                correctInfo = true;
                int orientation = 0;
            }
            if (battleshipInfoInt[2] == 1)) {
                correctInfo = true;
                int orientation = 1;
            }
            while (!correctInfo) {
                System.out.println("Illegal orientation, try again!");
                battleshipInfo = scanner.nextLine().split(", ");
            }


        }

        // check if the placing is correct (using Yaron's idea)
    }
    public static int[] strToIntArray(String[] input) {
        int[] intArray = new int[input.length];
        for(int i = 0;i < input.length;i++) {
            intArray[i] = Integer.parseInt(input[i]);
        }
        return intArray;
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
                board[i][j] = "-";
            }
        }

        int space_Num = digitCount(n);
        // first tille as lenght of digit of n
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
    // check for correct info for ships
    public static boolean correctOrientation(String input, int rows, int coll, int[][] board) {
        int HORIZONTAL_ORIENTATION = 0;
        int VERTICAL_ORIENTATION = 1;
        // check the orientation
        if ((Integer.parseInt(input.charAt(2)) != HORIZONTAL_ORIENTATION) && (Integer.parseInt(input.charAt(2)) != VERTICAL_ORIENTATION)) {
            System.out.println("Illegal orientation, try again!");
            return false;
        }
        // check if the tile is inside the board
        if ((input.charAt(0) >= rows) || (input.charAt(0) < '0')) {
            System.out.println("Illegal tile, try again!");
            return false;
        }
        if ((input.charAt(1) >= coll) || (input.charAt(1) < '0')) {
            System.out.println("Illegal tile, try again!");
            return false;
        }
        // CHECK IF THE WHOLE SHIP IS INSIDE THE BOARD!

        // CHECK IF THE SHIP IS NOT PLACE ON OTHER SHIP!

        return true;
    }

    // display the board
    public static void printBoard(int[][] board, int rows, int coll) {

        // !PRINT THE NUMBERS ON THE TOP OF THE BOARD AND ON THE SIDE! HAVE NO IDEA HOW

        // 0 - no ship, 1 - ship, -1 - a hit
        // go by indexes (i for rows, j for coll). and check for the state in this place
        int i = 0;
        while (i < rows) {
            int j = 0;
            while (j < coll) {
                // check if there was a hit
                if (board[i][j] == -1) {
                    System.out.println("X");
                }
                // check if there's a ship
                if (board[i][j] == 1) {
                    System.out.println("#");
                }
                // if there's neither a hit or a ship there's blank space
                else {
                    System.out.println("–");
                }
                j++;
            }
            i++;
        }
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



