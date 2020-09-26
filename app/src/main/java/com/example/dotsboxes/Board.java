package com.example.dotsboxes;

import android.graphics.Color;
import android.media.midi.MidiOutputPort;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;


public class Board {


    enum FakeBoard {EMPTY_LINE, SELECTED_LINE, DOT, BLANK, HUMAN_SQUARE, COMPUTER_SQUARE};
    enum Player {HUMAN, AI}

    ArrayList<FakeBoard> board;
    ArrayList<Integer> moves;
    ArrayList<Integer> output;
    Player turn;
    int columnSize;
    int rowSize;
    int humanPoints;
    int computerPoints;
    int lastMove;
    int boardSize;

    public Board(ArrayList<FakeBoard> board, Player turn, int columnSize, int humanPoints, int computerPoints){
        this.board = (ArrayList<FakeBoard>) board.clone();
        this.moves = new ArrayList<Integer>();
        setMoves();
        this.output = new ArrayList<>();
        this.turn = turn;
        this.columnSize = columnSize;
        this.rowSize = board.size() / columnSize;
        this.humanPoints = humanPoints;
        this.computerPoints = computerPoints;
        boardSize = board.size();
    }


    private void setMoves(){

        moves.clear();
        for (int i = 0; i < board.size(); i++){
            if(board.get(i) == FakeBoard.EMPTY_LINE)
                moves.add(i);
        }
    }

    private boolean ifHorizontalLine(int line){

        if((line / columnSize) % 2 == 0)
            return true;
        return false;
    }

    private int CheckUpperSquare(int position, boolean isConquering) {
        int r = position / columnSize, c = position % columnSize;
        if (r != 0) {
            if ((board.get(turnToPosition(r - 1, c - 1))) == FakeBoard.SELECTED_LINE
                    && (board.get(turnToPosition(r - 2, c))) == FakeBoard.SELECTED_LINE
                    && (board.get(turnToPosition(r - 1, c + 1))) == FakeBoard.SELECTED_LINE) {
                if (isConquering) {
                    if (turn == Player.HUMAN) {
                        humanPoints++;
                        board.set(turnToPosition(r - 1, c), FakeBoard.HUMAN_SQUARE);
                        output.add(turnToPosition(r - 1, c));
                    } else if (turn == Player.AI) {
                        computerPoints++;
                        board.set(turnToPosition(r - 1, c), FakeBoard.COMPUTER_SQUARE);
                        output.add(turnToPosition(r - 1, c));
                    }
                }
                return 1;
            }
        }
        return 0;
    }

    private int CheackBottomSquare(int position, boolean isConquering) {
        int r = position / columnSize, c = position % columnSize;
        if (r !=  rowSize - 1) {
            if ((board.get(turnToPosition(r + 1, c - 1))) == FakeBoard.SELECTED_LINE
                    && (board.get(turnToPosition(r + 2, c))) == FakeBoard.SELECTED_LINE
                    && (board.get(turnToPosition(r + 1, c + 1))) == FakeBoard.SELECTED_LINE) {
                if (isConquering) {
                    if (turn == Player.HUMAN) {
                        humanPoints++;
                        board.set(turnToPosition(r + 1, c), FakeBoard.HUMAN_SQUARE);
                        output.add(turnToPosition(r + 1, c));
                    } else if (turn == Player.AI) {
                        computerPoints++;
                        board.set(turnToPosition(r + 1, c), FakeBoard.COMPUTER_SQUARE);
                        output.add(turnToPosition(r + 1, c));
                    }
                }
                return 1;
            }
        }
        return 0;
    }

    private int CheckRightSquare(int position, boolean isConquering) {
        int r = position / columnSize, c = position % columnSize;
        if (c != columnSize - 1) {
            if(turnToPosition(r - 1, c + 1) < 0) {
                ifHorizontalLine(position);
            }
            if ((board.get(turnToPosition(r - 1, c + 1))) == FakeBoard.SELECTED_LINE
                    && (board.get(turnToPosition(r, c + 2))) == FakeBoard.SELECTED_LINE
                    && (board.get(turnToPosition(r + 1, c + 1))) == FakeBoard.SELECTED_LINE) {
                if (isConquering) {
                    if (turn == Player.HUMAN) {
                        humanPoints++;
                        board.set(turnToPosition(r, c + 1), FakeBoard.HUMAN_SQUARE);
                        output.add(turnToPosition(r, c + 1));
                    } else if (turn == Player.AI) {
                        computerPoints++;
                        board.set(turnToPosition(r, c + 1), FakeBoard.COMPUTER_SQUARE );
                        output.add(turnToPosition(r, c + 1));

                    }
                }
                return 1;
            }
        }
        return 0;
    }

    private int CheackLeftSquare(int position, boolean isConquering) {
        int r = position / columnSize, c = position % columnSize;
        if (c != 0) {
            if ((board.get(turnToPosition(r - 1, c - 1))) == FakeBoard.SELECTED_LINE
                    && (board.get(turnToPosition(r, c - 2))) == FakeBoard.SELECTED_LINE
                    && (board.get(turnToPosition(r + 1, c - 1))) == FakeBoard.SELECTED_LINE) {
                if (isConquering) {
                    if (turn == Player.HUMAN) {
                        humanPoints++;
                        board.set(turnToPosition(r, c - 1), FakeBoard.HUMAN_SQUARE);
                        output.add(turnToPosition(r, c - 1));
                    } else if (turn == Player.AI) {
                        computerPoints++;
                        board.set(turnToPosition(r, c - 1), FakeBoard.COMPUTER_SQUARE);
                        output.add(turnToPosition(r, c - 1));
                    }
                }
                return 1;
            }
        }
        return 0;
    }

    public int conquerBoxes(int position) {

        int numOfConqueredSquares = 0;
        if (ifHorizontalLine(position)) {
            numOfConqueredSquares += CheckUpperSquare(position, true);
            numOfConqueredSquares += CheackBottomSquare(position, true);
        } else {
            numOfConqueredSquares += CheckRightSquare(position, true);
            numOfConqueredSquares += CheackLeftSquare(position, true);
        }

        return numOfConqueredSquares;
    }

    public int checkBoxes(int position) {

        int numOfConqueredSquares = 0;
        if (ifHorizontalLine(position)) {
            numOfConqueredSquares += CheckUpperSquare(position, false);
            numOfConqueredSquares += CheackBottomSquare(position, false);
        } else {
            numOfConqueredSquares += CheckRightSquare(position, false);
            numOfConqueredSquares += CheackLeftSquare(position, false);
        }

        return numOfConqueredSquares;
    }

    private int turnToPosition(int r, int c) {
        return r * columnSize + c;
    }

    public boolean checkWinner() {

        int numOfSquares = ((rowSize - 1) / 2) * ((columnSize - 1) / 2);
        if (humanPoints + computerPoints == numOfSquares) {
            return true;
        }
        return false;
    }

    private void setNextTurn() {

        if (turn == Player.HUMAN) {
            turn = Player.AI;
        } else if (turn == Player.AI) {
            turn = Player.HUMAN;
        }
    }

    public boolean canConquer() {

        for (int i = 0; i < moves.size(); i++) {
            if (checkBoxes(moves.get(i)) > 0) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<FakeBoard> getBoard() {
        return board;
    }

    public ArrayList<Integer> getMoves() {
        return moves;
    }

    public ArrayList<Integer> getOutput(){
        return output;
    }

    public int evaluate() { return computerPoints - humanPoints; }

    public void makeMove(int move){ // O(N^3)

        if(checkBoxes(move) > 0){
            if(ifMinPath()){
                aiLineMove(move);
                minPathMove();
            }
            else{
                aiLineMove(move);
                notMinPathMove();
            }
        }
        else
            aiLineMove(move);

    }

    public void pathMove(){


        if (ifMinPath()) {
            minPathMove();
        }
        else {
            notMinPathMove();
        }
    }

    public void notMinPathMove(){
        fillAlmostFilledBoxes(generateCurrentPathLines());
        minDamageMove();
    }

    public void minPathMove(){
        ArrayList<Integer> preConquredLines = generateCurrentPathLines();
        int size = preConquredLines.size();
        if(size >= 3) {
            atLeastThreeLinesInMinPath(preConquredLines.remove(size - 3), preConquredLines.remove(size - 3), preConquredLines.remove(size - 3), preConquredLines);
        }
        else if (size == 2) {
            onlyTwoLinesInMinPath(preConquredLines.remove(size - 2), preConquredLines.remove(size - 2), preConquredLines);
        }
        else {
            fillAlmostFilledBoxes(generateCurrentPathLines());
            minDamageMove();
        }
    }

    public void atLeastThreeLinesInMinPath(int line1, int line2, int line3, ArrayList<Integer> preConquredLines) {

        fillAlmostFilledBoxes(preConquredLines);
        if(!(firstYesSecondNo(line1, line2) || firstYesSecondNo(line1, line3) || firstYesSecondNo(line2, line1)
                || firstYesSecondNo(line2, line3) || firstYesSecondNo(line3, line1) || firstYesSecondNo(line3, line2))){

            if(firstNo(line1))
                return;
            else if(firstNo(line2))
                return;
            else
                firstNo(line3);

        }
    }

    public void onlyTwoLinesInMinPath(int line1, int line2, ArrayList<Integer> preConquredLines){

        fillAlmostFilledBoxes(preConquredLines);
        if(!(firstNo(line1) || firstNo(line2))){
            aiLineMove(line1);
            aiLineMove(line2);
        }
    }

    public boolean firstYesSecondNo(int line1, int line2){

        if(checkBoxes(line1) > 0) {
            board.set(line1, FakeBoard.SELECTED_LINE);
            if(checkBoxes(line2) == 0) {
                board.set(line1, FakeBoard.EMPTY_LINE);
                aiLineMove(line1);
                aiLineMove(line2);
                return true;
            }
        }
        board.set(line1, FakeBoard.EMPTY_LINE);
        return false;

    }

    public boolean firstNo(int line){

        if(checkBoxes(line) == 0){
            aiLineMove(line);
            return true;
        }
        return false;
    }


    public boolean ifMinPath(){

        int currentPathBoxes = sumAlmostFilledBoxes() + checkBoxes(lastMove) , min = 1000, index, current;
        FakeBoard fakeBoard;
        ArrayList<Integer> currentPathLines = generateCurrentPathLines();
        currentPathLines.add(lastMove);

        if(((rowSize - 1) / 2) * ((columnSize - 1) / 2) - (computerPoints + humanPoints) == currentPathBoxes) // if there is only one path
            return false;


        board.set(lastMove, FakeBoard.EMPTY_LINE);

        moves.add(lastMove);
        for (int i = 0; i < moves.size(); i++){
            index = moves.get(i);
            if (!currentPathLines.contains(Integer.valueOf(index))) {
                fakeBoard = board.get(index);
                board.set(index, FakeBoard.SELECTED_LINE);
                current = sumAlmostFilledBoxes() + checkBoxes(index);
                if(current < min)
                    min = current;
                if (current < currentPathBoxes) {
                    board.set(lastMove, FakeBoard.SELECTED_LINE);
                    board.set(index, fakeBoard);
                    moves.remove(moves.size() - 1);
                    return false;
                }
                board.set(index, fakeBoard);
            }
        }
        board.set(lastMove, FakeBoard.SELECTED_LINE);
        moves.remove(moves.size() - 1);

        if(min == currentPathBoxes)
            return false;

        return true;
    }

    public void minDamageMove(){

        if (moves.size() != 0) {
            aiLineMove(minDamageIndex());
        }
        else
            setNextTurn();

    }

    public void aiLineMove(final int line){

        output.add(line);
        moves.remove(Integer.valueOf(line));
        board.set(line, FakeBoard.SELECTED_LINE);
        if(conquerBoxes(line) == 0){
            setNextTurn();
        }
        lastMove = line;
    }

    private int minDamageIndex() {

        Random r = new Random();
        int rand = r.nextInt(moves.size() - 1);
        int bestPos = moves.get(rand);
        board.set(bestPos, FakeBoard.SELECTED_LINE);
        int min = sumAlmostFilledBoxes(), current;
        board.set(bestPos, FakeBoard.EMPTY_LINE);


        for (int i = 0; i < moves.size() && min > 0; i++) {
            board.set(moves.get(i), FakeBoard.SELECTED_LINE);
            current = sumAlmostFilledBoxes();
            board.set(moves.get(i), FakeBoard.EMPTY_LINE);
            if (current < min) {
                min = current;
                bestPos = moves.get(i);
            }
        }
        return bestPos;
    }

    private ArrayList<Integer> generateCurrentPathLines() {

        Board temp = new Board(board, turn, columnSize, humanPoints, computerPoints);
        ArrayList<Integer> result = new ArrayList<>();
        int current, sum = 0;
        while (true) {
            sum = 0;
            for (int i = 0; i < temp.moves.size(); i++) {
                FakeBoard line = temp.board.get(temp.moves.get(i));
                if (line == FakeBoard.EMPTY_LINE) {
                    current = temp.checkBoxes(temp.moves.get(i));
                    if (current > 0) {
                        temp.board.set(temp.moves.get(i), FakeBoard.SELECTED_LINE);
                        result.add(temp.moves.get(i));
                        sum += current;
                    }
                }
            }
            if(sum == 0)
                break;
        }
        return result;
    }

    public int sumAlmostFilledBoxes() {

        Board temp = new Board(board, turn, columnSize, humanPoints, computerPoints);
        int sumOfSums = 0, currentSum, currentConquestes;
        while (true) {
            currentSum = 0;
            for (int i = 0; i < temp.moves.size(); i++) {
                if(temp.board.get(temp.moves.get(i)) == FakeBoard.EMPTY_LINE) {
                    currentConquestes = temp.checkBoxes(temp.moves.get(i));
                    if (currentConquestes > 0) {
                        temp.board.set(temp.moves.get(i), FakeBoard.SELECTED_LINE);
                        currentSum += currentConquestes;
                    }
                }
            }
            if (currentSum == 0)
                break;
            sumOfSums += currentSum;
        }

        return sumOfSums;
    }

    public void fillAlmostFilledBoxes(ArrayList<Integer> lines) {

            if(lines == null)
                lines = generateCurrentPathLines();
            for (int i = 0; i < lines.size(); i++)
                        aiLineMove(lines.get(i));

    }

}
