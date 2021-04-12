package student_player;

import boardgame.Board;
import boardgame.Move;

import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import java.util.Collections;

import java.util.ArrayList;
import java.util.LinkedList;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260829600");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */


    // Also implement a utility func!!

    public static int utility(PentagoBoardState boardState, boolean isBlackPlayer)
    {

        //System.out.println("Now calculate the utility for this state!");
        boolean blacksTurn = isBlackPlayer ?true : false;
        int totalScore = MyTools.getScore(boardState,isBlackPlayer,blacksTurn);
        //System.out.println("The score computed by getScore is: " + totalScore);


        PentagoBoardState.Piece playerPiece; // our agent! not the first player!
        // although our agent can be the first player as well!

        if(isBlackPlayer){
            playerPiece = PentagoBoardState.Piece.BLACK;
        }
        else {
            playerPiece = PentagoBoardState.Piece.WHITE;

        }

        int utility = 0;
        int streak = 0;
        //streak is used to add additional points for more than 2 in a row
        //2 in a row = 1 pt, 3 in a row = 2 pt, etc.

        PentagoBoardState.Piece[][] board = boardState.getBoard();

        //count horizontal doubles
        for(int i = 0; i < 6; i++)	//go down row
        {
            for(int j = 0; j < 5; j++)	//count doubles
            {

                if(board[i][j] == playerPiece && board[i][j+1] == playerPiece)
                {
                    utility += streak + 1;
                    streak++;
                }
                else
                    streak = 0;
            }
        }

        //count vertical doubles
        for(int i = 0; i < 6; i++)	//go down columns
        {
            for(int j = 0; j < 5; j++)	//count doubles
            {
                if(board[j][i] == playerPiece && board[j+1][i] == playerPiece)
                {
                    utility += streak + 1;
                    streak++;
                }
                else
                    streak = 0;
            }
        }

        //count main diagonal up-left to down-right
        for(int i = 0; i < 5; i++)
        {
            if(board[i][i] == playerPiece && board[i+1][i+1] == playerPiece)
            {
                utility += streak + 1;
                streak++;
            }
            else
                streak = 0;
        }

        //count main diagonal up-right to down-left
        for(int i = 0; i < 5; i++)
        {
            if(board[i][5-i] == playerPiece && board[i+1][4-i] == playerPiece)
            {
                utility += streak + 1;
                streak++;
            }
            else
                streak = 0;
        }

        //System.out.println("The utility is:" + utility);

        return utility;
    }

    /**
    This function implements miniMax algorithm for the AI agent to consider "all" possible moves and choose the best one

     This is version 1.0 where this is a raw miniMax without further modification with alpha-beta pruning.

     Raw MiniMax Algorithm Idea:
     1. Must loop through all the possible children(next possible Moves) for a given/current state
      --> this can be done using the helper function: getAllLegalMoves() ,provided in PentagoBoardSate

     2. Then recursively do the miniMax function to reach the Terminal State (where there is a tie/win).

     3. report the value according to the utility function

     4. back recursion to the top... we get the score and we can determine what's the best next move!


     To-do List:
     1. implement a raw minimax algorithm (deadline April 6.)

     2. implement a utility function!! hard one (deadline April 6.)

     3. test the minimax function with a small depth (deadline April 6.)

     4. implement alpha-beta

     */
    public static int miniMax(PentagoBoardState boardState, boolean isMaxPlayer, int depthLimit, int alpha, int beta, boolean isTerminalState, int numMovesToConsider,
                              long startTime, boolean isBlackPlayer){


        int bestScore = 0;



        long currTime = System.nanoTime();
        if((currTime - startTime)/ 1000000 > 1500){
            System.out.println("may exceed TIME!!");
            return -1000;
        }

        boolean terminalState = isTerminalState;
        // 1. update terminal state, see if the game or search is terminated
        if(depthLimit == 0) {
            terminalState = true;
            //System.out.println("evaluation ends by depth limit!");
        }
        if(boardState.getWinner() != Board.NOBODY){
            terminalState = true;
        }

        // the possible nextMoves given the current state
        // we process it, choose 30 random moves ONLY
        ArrayList<PentagoMove> childrenMoves = boardState.getAllLegalMoves();
        if(childrenMoves.size() <= 30){ // we can choose 30 or 20 here
            //System.out.println("Warning! Fewer than 30 next moves! used for debugging");
            terminalState = true;
            numMovesToConsider = Math.max(childrenMoves.size() -2, 0); // account for an edge case where it becomes negative!
        }

        LinkedList<PentagoMove> possibleLinkedMoves = new LinkedList<>();

        Collections.shuffle(childrenMoves);
        for(int i = 0;i<numMovesToConsider;i++){
            PentagoMove randMove = childrenMoves.get(i);
            possibleLinkedMoves.add(randMove);
        }


        if(terminalState){ // reach the terminal

            return MyTools.evaluateBoard(boardState, true, isBlackPlayer);
           // return utility(boardState, isBlackPlayer); // evaluate the boardState!
        }

        if(isMaxPlayer){
            // at max node, update alpha only
            // use lowScore to represent the updated alpha!
            //int maxScore = alpha; // -inf -> 4 ->
            bestScore = alpha;
            for (PentagoMove move: possibleLinkedMoves) {
                // clone it
                PentagoBoardState newPbs = (PentagoBoardState) boardState.clone();
                //PentagoBoardState newPbs = new PentagoBoardState(boardState); // clone the PBS for safety!
                newPbs.processMove(move); // update the cloned PBS!
                int score = miniMax(newPbs,false, depthLimit - 1, bestScore, beta, isTerminalState, numMovesToConsider, startTime,isBlackPlayer);
                if(score > 1000) {
                    System.out.println("bigScore at Max! we keep going! "+ score); // maybe we should dive in here more! give it more depth limit
                    /**
                     *
                     * we can do something useful here!
                     * For example, we will not return the current score! instead we dive in this move!!!
                     * while changing the depthLimit or number of random nextMoves to consider

                     Assume we are at the last Node! We will give it another shot with more moves!
                     An alternative plan, give it more depthLimit but with fewer Moves to consider

                     maybe count how long will this extra computation cost?

                     This will cause error since the score can only be greater and greater....
                     */
                    try{
                        score = miniMax(newPbs,false, 1, bestScore, beta, false, 20, startTime, isBlackPlayer);
                    }
                    catch (IndexOutOfBoundsException e){
                        System.out.println("Index out of bound!" + e);
                        return bestScore;
                    }

                    //score = miniMax(newPbs,false, 2, maxScore, beta, false, 50);
                }
                else if(score > 40000){
                    System.out.println("This is a high score move!");
                    return bestScore;
                }

                if(score > bestScore) bestScore = score; // score better than the current is ACCEPTED!

                if(alpha >= beta){ // in this case 3 > 2
                    //break;
                    //System.out.println("pruned!");
                    System.out.println("alpha is returned");
                    return alpha;

                }
            }
            System.out.println("Max score at Max Node is returned Now: "+ bestScore);
            return bestScore;
            // 3 is returned in the END!!
        }
        else{// the Min player's turn
            // use lowScore to represent the updated beta!
            //int lowScore = beta; // +inf (MAX_VALUE;)
            bestScore = beta;
            for (PentagoMove move: possibleLinkedMoves) {
                // clone it
                PentagoBoardState newPbs = (PentagoBoardState) boardState.clone(); // clone the PBS for safety!
                newPbs.processMove(move); // update the cloned PBS!
                int score = miniMax(newPbs,true, depthLimit - 1, alpha, bestScore, isTerminalState , numMovesToConsider, startTime, isBlackPlayer); // 3


                if(score > 40000){ // which is really high... enough
                    System.out.println("This is a high score move!");
                    return bestScore;
                }
                /*
                if(score == 0 || score == 1){
                    // this means that we are at the beginning, no need to explore A LOT!
                    return score;
                    //break;
                }
                */

                // now it reaches the ROOT [2] -> return 2
                if(score < bestScore ) bestScore = score;


                if(alpha >= bestScore){ // in this case 3 > 2
                    //break;
                    System.out.println("pruned!");
                    return alpha;

                }
            }
            System.out.println("low score at Min Node is returned Now: "+ bestScore);
            return bestScore; // 3 is returned by the FIRST MinNode


        }


        //return 1;
    }

    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        //MyTools.getSomething();

        try {


            int bestScore = Integer.MIN_VALUE;
            Move bestMove = boardState.getRandomMove();
            int firstPlayer = boardState.firstPlayer();
            int ourPlayer = bestMove.getPlayerID();

            System.out.println("The first player id is: " + firstPlayer);
            System.out.println("Our player id is: " + ourPlayer);

            boolean weAreBlack = false;

            if (firstPlayer == ourPlayer) {
                System.out.println("We play first! we are white!");

            } else {
                weAreBlack = true;
                System.out.println("We play the second, we are black! Change the utility func accordingly!");
            }

            System.out.println("start searching killer move first!!");
            PentagoMove killerMove = MyTools.searchWinningMove(boardState, weAreBlack);
            if (killerMove == null) {
                System.out.println("Running minimax to find a move");

            } else {
                System.out.println("There is a killer move or Our opponent is winning! We need to choose a move to break its win!!");
                return killerMove;
            }


            ArrayList<PentagoMove> childrenMoves = boardState.getAllLegalMoves();
            // System.out.println("there are " + childrenMoves.size() + "possible next moves for this state");
            // randomly

            ArrayList<PentagoMove> movesToConsider = new ArrayList<>();

            Collections.shuffle(childrenMoves);
            for (int i = 0; i < 30; i++) {
                PentagoMove randMove = childrenMoves.get(i);
                movesToConsider.add(randMove);
            }


            int count = 0;
            for (PentagoMove move : movesToConsider) {
                // clone it
                //System.out.println("Now process move "+count);
                PentagoBoardState newPbs = (PentagoBoardState) boardState.clone();
                newPbs.processMove(move);// when we take this move, what would happen? we will calculate this miniMax score!

                int alpha = Integer.MIN_VALUE;
                int beta = Integer.MAX_VALUE;
                int depthAllocated = 3; // 3 is fine for play time
                int numMovesConsider = 20;
                //System.out.println("Now compute the minimax Score....");

                long startTime = System.nanoTime();
                int score = miniMax(newPbs, true, depthAllocated, alpha, beta, false, 20, startTime, weAreBlack);
                System.out.println("The chosen move in iteration: " + count + " has score: " + score);
                if (score == -1000) {
                    System.out.println("use random move!!");
                    return boardState.getRandomMove(); // reach time limit!
                }
                //System.out.println("The score is: " + score);
                if (score > bestScore) {
                    System.out.println("Bestmove is updated!");
                    bestScore = score;
                    bestMove = move;
                }
                System.out.println("Best score so far: " + bestScore);
                count++;

            }


            // Is random the best you can do?
            // Move myMove = boardState.getRandomMove();


            // Return your move to be processed by the server.
            return bestMove;

        }
        catch (Exception e){
            System.out.println("Exception caught! Return a random move");
            return boardState.getRandomMove();

        }
    }
}