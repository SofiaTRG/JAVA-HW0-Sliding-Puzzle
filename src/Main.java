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
        int[] sizeBoardNum = extractNumbersFromStr(sizeBoard);
        // n = rows, m = cols
        int n = sizeBoardNum[0];
        int m = sizeBoardNum[1];
        // making the board
        int [][] board = new int[n][m];

        // ?should we check if the user actually put two integers?

        // battleships size
        System.out.println("Enter the battleships sizes:");
        String battleships = scanner.nextLine();
        // make the string into number and sizes.
        String[] battleshipsSplit = splitStr(battleships);
        // the sizes (no needed right now. we will get the ints when we start placing the ships)
        int numOfBattleship = battleshipsSplit.length;

        // should we check is the sizes are good? (not bigger than max(n,m))?

        // !!PLACE THE BATTLESHIPS!! (use other function and for
        System.out.println("Enter location and orientation for battleship size s");

        // check if the placing is correct
    }

    // from string "nXm" to array of 2 integers (cause java cannot return 2 integers)
    public static int[] extractNumbersFromStr(String input) {
        String[] numbers = input.split("X");
        int num1 = Integer.parseInt(numbers[0]);
        int num2 = Integer.parseInt(numbers[1]);
        return new int[] {num1, num2};
    }

    // split from str to str[] by " "
    public static String[] splitStr(String input) {
        return input.split(" ");
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



