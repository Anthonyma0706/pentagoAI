package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import java.util.ArrayList;
import java.util.Collections;

import boardgame.Move;

public class MyTools {
    public static double getSomething() {
        return Math.random();
    }

    // Win score should be greater than all possible board scores
    private static final int winScore = 100000000;
    public static int getWinScore() {
        return winScore;
    }



    // This function calculates the board score of the specified player.
    // (i.e. How good a player's general standing on the board by considering how many
    //  consecutive 2's, 3's, 4's it has, how many of them are blocked etc...)
    public static int getScore(PentagoBoardState boardState, boolean forBlack, boolean blacksTurn) {

        // Read the board
        PentagoBoardState.Piece[][] boardMatrix = boardState.getBoard();

        // Calculate score for each of the 3 directions
        return evaluateHorizontal(boardMatrix, forBlack, blacksTurn) +
                evaluateVertical(boardMatrix, forBlack, blacksTurn) +
                evaluateDiagonal(boardMatrix, forBlack, blacksTurn);
    }

    // This function calculates the relative score of the white player against the black.
    // (i.e. how likely is white player to win the game before the black player)
    // This value will be used as the score in the Minimax algorithm.

    /*
    if our Agent is black

    if our agent is white, then we should evaluate board for

     */
    public static int evaluateBoard(PentagoBoardState board, boolean blacksTurn, boolean agentIsBlack) {


        // Get board score of both players.
        double blackScore = getScore(board, true, blacksTurn);
        double whiteScore = getScore(board, false, blacksTurn);

        if(blackScore == 0) blackScore = 1.0;
        if(whiteScore == 0) whiteScore = 1.0;

        // Calculate relative score of white against black
        if(agentIsBlack){
            // if our agent is black
            int score = (int) (blackScore/whiteScore);
            System.out.println("Our agent is black, the relative score is: "+score);
            return score;
        }
        else{
            // if our agent is white
            int score = (int) (whiteScore / blackScore);
            System.out.println("Our agent is white, the relative score is: "+score);
            return score;
        }


    }









    // This function calculates the score by evaluating the stone positions in horizontal direction
    public static int evaluateHorizontal(PentagoBoardState.Piece[][] boardMatrix, boolean forBlack, boolean playersTurn ) {

        int consecutive = 0;
        /** blocks variable is used to check if a consecutive stone set is blocked by the opponent or the board border.
           If the both sides of a consecutive set is blocked, blocks variable will be 2
           If only a single side is blocked, blocks variable will be 1, and if both sides of the consecutive
           set is free, blocks count will be 0.

         By default, first cell in a row is blocked by the left border of the board.
         If the first cell is empty, block count will be decremented by 1.
         If there is another empty cell after a consecutive stones set, block count will again be
         decremented by 1.

         */
        int blocks = 2;
        int score = 0;

        PentagoBoardState.Piece playerPiece;
        if(forBlack){
            playerPiece = PentagoBoardState.Piece.BLACK;
        }
        else {
            playerPiece = PentagoBoardState.Piece.WHITE;
            //System.out.println("Now evaluate White!");
        }


        // Iterate over all rows
        for(int i=0; i<6.; i++) {
            // Iterate over all cells in a row
            for(int j=0; j<6; j++) {
                // Check if the selected player has a stone in the current cell
                if(boardMatrix[i][j] == playerPiece) {
                    // Increment consecutive stones count
                    consecutive++;
                }
                // Check if cell is empty
                else if(boardMatrix[i][j] == PentagoBoardState.Piece.EMPTY) {
                    // Check if there were any consecutive stones before this empty cell
                    if(consecutive > 0) {
                        // Consecutive set is not blocked by opponent, decrement block count
                        blocks--;
                        // Get consecutive set score
                        score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
                        // Reset consecutive stone count
                        consecutive = 0;
                        // Current cell is empty, next consecutive set will have at most 1 blocked side.
                        blocks = 1;
                    }
                    else {
                        // No consecutive stones.
                        // Current cell is empty, next consecutive set will have at most 1 blocked side.
                        blocks = 1;
                    }
                }
                // Cell is occupied by opponent
                // Check if there were any consecutive stones before this empty cell
                else if(consecutive > 0) {
                    // Get consecutive set score
                    score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
                    // Reset consecutive stone count
                    consecutive = 0;
                    // Current cell is occupied by opponent, next consecutive set may have 2 blocked sides
                    blocks = 2;
                }
                else {
                    // Current cell is occupied by opponent, next consecutive set may have 2 blocked sides
                    blocks = 2;
                }
            }
            // End of row, check if there were any consecutive stones before we reached right border
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
            }
            // Reset consecutive stone and blocks count
            consecutive = 0;
            blocks = 2;
        }

        //System.out.println("Horizontal score is: " + score);
        return score;
    }

    // This function calculates the score by evaluating the stone positions in vertical direction
    // The procedure is the exact same of the horizontal one.
    public static  int evaluateVertical(PentagoBoardState.Piece[][] boardMatrix, boolean forBlack, boolean playersTurn ) {

        int consecutive = 0;
        int blocks = 2;
        int score = 0;

        PentagoBoardState.Piece playerPiece;
        if(forBlack){
            playerPiece = PentagoBoardState.Piece.BLACK;
        }
        else {
            playerPiece = PentagoBoardState.Piece.WHITE;
            //System.out.println("Now evaluate White!");
        }

        for(int j=0; j<6; j++) {
            for(int i=0; i<6; i++) {
                if(boardMatrix[i][j] == playerPiece) {
                    consecutive++;
                }
                else if(boardMatrix[i][j] == PentagoBoardState.Piece.EMPTY) {
                    if(consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        blocks = 1;
                    }
                }
                else if(consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    blocks = 2;
                }
            }
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);

            }
            consecutive = 0;
            blocks = 2;

        }

        //System.out.println("Vertical score is: " + score);
        return score;
    }

    // This function calculates the score by evaluating the stone positions in diagonal directions
    // The procedure is the exact same of the horizontal calculation.
    public static  int evaluateDiagonal(PentagoBoardState.Piece[][] boardMatrix, boolean forBlack, boolean playersTurn ) {

        int consecutive = 0;
        int blocks = 2;
        int score = 0;

        PentagoBoardState.Piece playerPiece;
        if(forBlack){
            playerPiece = PentagoBoardState.Piece.BLACK;
        }
        else {
            playerPiece = PentagoBoardState.Piece.WHITE;
           // System.out.println("Now evaluate White!");
        }

        // From bottom-left to top-right diagonally
        int boardLength = 6;
        for (int k = 0; k <= 2 * (boardLength - 1); k++) {
            int iStart = Math.max(0, k - boardLength + 1);
            int iEnd = Math.min(boardLength - 1, k);
            for (int i = iStart; i <= iEnd; ++i) {
                int j = k - i;

                if(boardMatrix[i][j] == playerPiece) {
                    consecutive++;
                }
                else if(boardMatrix[i][j] == PentagoBoardState.Piece.EMPTY) {
                    if(consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        blocks = 1;
                    }
                }
                else if(consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    blocks = 2;
                }

            }
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);

            }
            consecutive = 0;
            blocks = 2;
        }
        // From top-left to bottom-right diagonally
        for (int k = 1-boardLength; k < boardLength; k++) {
            int iStart = Math.max(0, k);
            int iEnd = Math.min(boardLength + k - 1, boardLength-1);
            for (int i = iStart; i <= iEnd; ++i) {
                int j = i - k;

                if(boardMatrix[i][j] == playerPiece) {
                    consecutive++;
                }
                else if(boardMatrix[i][j] == PentagoBoardState.Piece.EMPTY) {
                    if(consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        blocks = 1;
                    }
                }
                else if(consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    blocks = 2;
                }

            }
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forBlack == playersTurn);

            }
            consecutive = 0;
            blocks = 2;
        }

       // System.out.println("Two diagonal score is: " + score);
        return score;
    }



    // This function returns the score of a given consecutive stone set.
    // count: Number of consecutive stones in the set
    // blocks: Number of blocked sides of the set (2: both sides blocked, 1: single side blocked, 0: both sides free)
    // currentTurn: boolean, true if we are placing the stone NEXT! For example , if we have 4 in a row and we 're at currentTurn, then we win!
    public static  int getConsecutiveSetScore(int count, int blocks, boolean currentTurn) {
        final int winGuarantee = 1000000;
        // If both sides of a set is blocked, this set is worthless return 0 points.
        if(blocks == 2 && count < 5) return 0;

        switch(count) {
            case 5: {
                // 5 consecutive wins the game
                return winScore;
            }
            case 4: {
                // 4 consecutive stones in the user's turn guarantees a win.
                // (User can win the game by placing the 5th stone after the set)
                if(currentTurn) return winGuarantee;
                else {
                    // Opponent's turn
                    // If neither side is blocked, 4 consecutive stones guarantees a win in the next turn.
                    // although it may be not the case in pentago
                    if(blocks == 0) return winGuarantee/4;
                        // If only a single side is blocked, 4 consecutive stones limits the opponents move
                        // (Opponent can only place a stone that will block the remaining side, otherwise the game is lost
                        // in the next turn). So a relatively high score is given for this set.
                    else return 200;
                }
            }
            case 3: {
                // 3 consecutive stones
                if(blocks == 0) {
                    // Neither side is blocked.
                    // If it's the current player's turn, a win is guaranteed in the next 2 turns.
                    // (User places another stone to make the set 4 consecutive, opponent can only block one side)
                    // However the opponent may win the game in the next turn therefore this score is lower than win
                    // guaranteed scores but still a very high score.
                    if(currentTurn) return 50000;
                        // If it's the opponent's turn, this set forces opponent to block one of the sides of the set.
                        // So a relatively high score is given for this set.
                    else return 200;
                }
                else {
                    // One of the sides is blocked.
                    // Playmaker scores
                    if(currentTurn) return 10;
                    else return 5;
                }
            }
            case 2: {
                // 2 consecutive stones
                // Playmaker scores
                if(blocks == 0) {
                    if(currentTurn) return 7;
                    else return 5;
                }
                else {
                    return 3;
                }
            }
            case 1: {
                return 1;
            }
        }

        // More than 5 consecutive stones?
        return winScore*2;
    }















    // This function looks for a move that can instantly win the game.
    public static PentagoMove searchWinningMove(PentagoBoardState boardState, boolean isBlackPlayer) {

        ArrayList<PentagoMove> allPossibleMoves = boardState.getAllLegalMoves();

        Move winningMove = boardState.getRandomMove();

        boolean forBlack = false;
        boolean blackTurn = false;
        if(isBlackPlayer){
            forBlack = true;
            blackTurn = true;
        }


        // Iterate for all possible moves
        for(PentagoMove move : allPossibleMoves) {
            PentagoBoardState newPbs = (PentagoBoardState) boardState.clone(); // clone the PBS
            newPbs.processMove(move);
            // first check if we can win immediately!
            int ourScore = getScore(newPbs,forBlack,blackTurn);
            //System.out.println("Our score is: " + opponentScore);
            if(ourScore >= winScore) {
                System.out.println("Winner Move!");
                //winningMove = move;
                return move;
            }


            // then check if our opponent is close to win
            int opponentScore = getScore(newPbs,!forBlack,!blackTurn);
           // int opponentScore2 = getScore(newPbs,false,true);
            //System.out.println("opponent score is: " + opponentScore);
            PentagoMove bestBreakMove = move;
            int lowestScoreSoFar = opponentScore;
            int count = 0;
            if(opponentScore >= 1000000) {
                System.out.println("Opponent is close to win!! We must break it! Its score is: " + opponentScore);
                Collections.shuffle(allPossibleMoves);

                // see what move will lower the opponent's score?
                for(PentagoMove breakMove : allPossibleMoves) {
                    System.out.println("evaluating opponent's ... try to break it");

                    PentagoBoardState updatedPbs = (PentagoBoardState) boardState.clone(); // clone the PBS
                    updatedPbs.processMove(breakMove);





                    int newOpposcore = getScore(updatedPbs,!forBlack,!blackTurn);
                    System.out.println("NEW opponent score is: " + newOpposcore);
                    if(newOpposcore < 1000000){
                        System.out.println("We successfully break opponent's win! The new score is: "+ newOpposcore);
                        if(newOpposcore < lowestScoreSoFar){
                            bestBreakMove = breakMove;
                            lowestScoreSoFar = newOpposcore;
                            System.out.println("The lowest score now is: "+ lowestScoreSoFar);
                        }
                        count++;
                        if(count >= 50){
                            System.out.println("We have found 50 possible moves that can stop the opponent to win! with Best score : "+ lowestScoreSoFar);
                            return bestBreakMove;
                        }

                        //return bestBreakMove;
                    }



                }
                if(count >0) {
                    System.out.println("The chosen move has score : "+ lowestScoreSoFar);
                    return bestBreakMove;
                }



                return null;
            }


        }
        return null;
    }
}