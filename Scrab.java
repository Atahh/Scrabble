/**
 * Atah
 * Scrabble Game using console
 *
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Scrab{
    //CONSTANTS
    private int BOARDSIZE = 15;
    private int RACKSIZE = 7;
    private int PLAYERS = 2;

    //GameBoard
    private char[][] board;

    //Racks for each player
    private ArrayList<Character> [] racks;

    //Dictionary of valid words using a Trie
    private Trie dictionary;

    //Bag of tiles to choose from
    private List<Character> tiles = new ArrayList<>();

    //Scores of each player
    private int[] scores;

    //Current player
    private int player;

    public Scrab(){
        board = new char[BOARDSIZE][BOARDSIZE];//new board
        emptyBoard(board);//empty board
        scores = new int[PLAYERS];//array of scores[0] = player 1
        racks = new ArrayList[PLAYERS];//racks of the players
        dictionary = new Trie();//dictionary of words
        initDictionary(dictionary);//initialize dictionary of words
        initTiles();//initialize the tiles of letters
        initRacks();//initialize the racks of players

        player = 0;
    }
    /** Fill the empty board **/
    public void emptyBoard(char[][]board){
        for(int i = 0;i<BOARDSIZE;i++){
            for(int j = 0;j<BOARDSIZE;j++){
                board[i][j] = '.';
            }
        }
    }
    /** Fill the tiles with letters **/
    public void initTiles(){
        for (int i = 0; i < 12; i++) {
            tiles.add('E');
        }
        for (int i = 0; i < 9; i++) {
            tiles.add('A');
            tiles.add('I');
        }
        for (int i = 0; i < 8; i++) {
            tiles.add('O');
        }
        for (int i = 0; i < 6; i++) {
            tiles.add('N');
            tiles.add('R');
            tiles.add('T');
        }
        for (int i = 0; i < 4; i++) {
            tiles.add('L');
            tiles.add('S');
            tiles.add('U');
            tiles.add('D');
        }
        for (int i = 0; i < 3; i++) {
            tiles.add('G');
        }
        for (int i = 0; i < 2; i++) {
            tiles.add('B');
            tiles.add('C');
            tiles.add('M');
            tiles.add('P');
            tiles.add('F');
            tiles.add('H');
            tiles.add('V');
            tiles.add('W');
            tiles.add('Y');
        }
        tiles.add('K');
        tiles.add('J');
        tiles.add('X');
        tiles.add('Q');
        tiles.add('Z');

// Add the blank tiles
        tiles.add(' ');
        tiles.add(' ');
    }

    /** Fill the racks of players **/
    public void initRacks(){
        for(int i = 0;i<PLAYERS;i++){
            racks[i] = new ArrayList<>();

            while(racks[i].size() < RACKSIZE){
                racks[i].add(drawTile());
            }
        }
    }

    /** Shuffle the racks of a player **/
    public void shuffleRacks(int player){
        racks[player].clear();
        while (racks[player].size() < RACKSIZE) {
            racks[player].add(drawTile());
        }

    }

    /** Fill the dictionary of words **/
    public void initDictionary(Trie dictionary){
        try(BufferedReader br = new BufferedReader(new FileReader("/usr/share/dict/web2"))){
            String line;
            while((line = br.readLine()) != null){
                dictionary.insert(line.trim().toUpperCase());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /** draw tiles from the tile list **/
    public char drawTile(){
        Random random = new Random();
        if(tiles.isEmpty()){
            return ' ';
        }
        return tiles.remove(random.nextInt(tiles.size()));

    }
    /** print the board **/
    public void printBoard(){
        for(int i = 0;i<BOARDSIZE;i++){
            for(int j = 0;j<BOARDSIZE;j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    /** print the rack **/
    public void printRack(int player){
        System.out.print("Rack: ");
        for(char c:racks[player]){
            System.out.print(c + " ");
        }
        System.out.println();
    }

    /** place the word on the board **/
    public void placeWord(String word,char orientation,int row,int col) {
        // Prompt the user for the position and orientation of the word

        // Place the word on the board
        if (orientation == 'H') {
            for (int i = 0; i < word.length(); i++) {
                board[row][col + i] = word.charAt(i);
            }
        } else if (orientation == 'V') {
            for (int i = 0; i < word.length(); i++) {
                board[row + i][col] = word.charAt(i);
            }
        }
    }

    /** The actual gameplay of the game **/
    public void play() {
        Scanner scanner = new Scanner(System.in);
        int currentPlayer = 0;
        while (true) {
            // Display the board and the racks to the players
            System.out.println("Current board:");
            printBoard();
            System.out.println("Player " + (currentPlayer + 1) + "'s rack:");
            printRack(currentPlayer);


            System.out.print("Enter a word to place on the board, or 'S' to shuffle your rack: ");
            String word = scanner.nextLine();

            if (word.equalsIgnoreCase("S")) {
                shuffleRacks(currentPlayer);
                System.out.println("Player " + (currentPlayer + 1) + "'s new rack:");
                printRack(currentPlayer);
                currentPlayer = (currentPlayer + 1) % 2; // Switch to the other player
                continue;
            }

            // Check if the word is valid and can be placed on the board
            if (!isValidWord(dictionary,word)) {
                System.out.println("Invalid word!");
                continue;
            }

            System.out.print("Enter the orientation (H or V): ");
            char orientation = scanner.next().charAt(0);

            System.out.print("Enter the row and column of the first letter: ");
            int row = scanner.nextInt();
            int col = scanner.nextInt();

            if (!canPlaceWord(word,orientation,row,col)) {
                System.out.println("Cannot place word on the board!");
                continue;
            }

            // Place the word on the board
            placeWord(word,orientation,row,col);

            // Update the racks and scores
            updateRacks(currentPlayer, word);
            scores[currentPlayer] += calculateScore(word);

            // Check if the game is over
            if (isGameOver()) {
                break;
            }

            // Switch to the next player
            currentPlayer = (currentPlayer + 1) % PLAYERS;
        }

        // Determine the winner
        int winner = 0;
        for (int i = 1; i < PLAYERS; i++) {
            if (scores[i] > scores[winner]) {
                winner = i;
            }
        }
        System.out.println("Player " + (winner + 1) + " wins with a score of " + scores[winner] + "!");
    }

    /** Check the Trie for the word **/
    public boolean isValidWord(Trie trie,String word) {
        Trie.TrieNode current = trie.root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            Trie.TrieNode node = current.children.get(c);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEnd;
    }

    /** Check to see if the game is over **/
    public boolean isGameOver() {
        // Check if any player has no tiles left in their rack
        for (List<Character> rack : racks) {
            if (rack.isEmpty()) {
                return true;
            }
        }
        // Check if the board is full
        for (char[] row : board) {
            for (char c : row) {
                if (c == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    /** update the rack after a player used tiles **/
    public void updateRacks(int player, String word) {
        // Remove the tiles for the word from the player's rack
        for (char c : word.toCharArray()) {
            racks[player].remove(Character.valueOf(c));
        }
        // Draw new tiles for the player until their rack is full
        while (racks[player].size() < RACKSIZE) {
            racks[player].add(drawTile());
        }
    }


    /** Check to see if you can place a word at a spot **/
    public boolean canPlaceWord(String word,char orientation,int row,int col) {
        // Prompt the user for the position and orientation of the word

        // Check if the word fits on the board
        if (orientation == 'H') {
            if (col + word.length() > BOARDSIZE) {
                return false;
            }
        } else if (orientation == 'V') {
            if (row + word.length() > BOARDSIZE) {
                return false;
            }
        }

        // Check if the word overlaps with any existing tiles on the board
        if (orientation == 'H') {
            for (int i = 0; i < word.length(); i++) {
                if (board[row][col + i] != '.' && board[row][col + i] != word.charAt(i)) {
                    return false;
                }
            }
        } else if (orientation == 'V') {
            for (int i = 0; i < word.length(); i++) {
                if (board[row + i][col] != '.' && board[row + i][col] != word.charAt(i)) {
                    return false;
                }
            }
        }

        return true;
    }

    /** Calculate the score of a certain word **/
    public int calculateScore(String word) {
        int score = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            switch (c) {
                case 'A':
                case 'E':
                case 'I':
                case 'O':
                case 'U':
                case 'L':
                case 'N':
                case 'S':
                case 'T':
                case 'R':
                    score += 1;
                    break;
                case 'D':
                case 'G':
                    score += 2;
                    break;
                case 'B':
                case 'C':
                case 'M':
                case 'P':
                    score += 3;
                    break;
                case 'F':
                case 'H':
                case 'V':
                case 'W':
                case 'Y':
                    score += 4;
                    break;
                case 'K':
                    score += 5;
                    break;
                case 'J':
                case 'X':
                    score += 8;
                    break;
                case 'Q':
                case 'Z':
                    score += 10;
                    break;
            }
        }
        return score;
    }


    public static void main(String[] args) {
        Scrab game = new Scrab();
        game.play();

    }


}