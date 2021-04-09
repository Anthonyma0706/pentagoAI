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

    public static int utility(PentagoBoardState boardState)
    {

        //System.out.println("Now calculate the utility for this state!");

        PentagoBoardState.Piece playerPiece; // our agent! not the first player!
        // although our agent can be the first player as well!
        if(boardState.firstPlayer() == 0){

            playerPiece = PentagoBoardState.Piece.BLACK;
            //System.out.println("First player is white, the utility function should report w.r.t white piece");
            /**
             * we assume our agent is black now
             */
        }
        else {
            playerPiece = PentagoBoardState.Piece.WHITE;
            System.out.println("evaluating white, wrong!!");

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
    public static int miniMax(PentagoBoardState boardState, boolean isMaxPlayer, int depthLimit, int alpha, int beta, boolean isTerminalState, int numMovesToConsider){

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
        //ArrayList<PentagoMove> possibleMoves = boardState.getAllLegalMoves();
        ArrayList<PentagoMove> childrenMoves = boardState.getAllLegalMoves();
        if(childrenMoves.size() <= 30){ // we can choose 30 or 20 here
            //System.out.println("Warning! Fewer than 30 next moves! used for debugging");
            terminalState = true;
            numMovesToConsider = Math.max(childrenMoves.size() -2, 0); // account for an edge case where it becomes negative!
        }
        //System.out.println("there are " + childrenMoves.size() + "possible next moves for this state");
        // randomly

        //ArrayList<PentagoMove> possibleMoves = new ArrayList<>();
        LinkedList<PentagoMove> possibleLinkedMoves = new LinkedList<>();


        Collections.shuffle(childrenMoves);
        for(int i = 0;i<numMovesToConsider;i++){
            PentagoMove randMove = childrenMoves.get(i);
            possibleLinkedMoves.add(randMove);
            //possibleMoves.add(randMove);
        }


        if(terminalState){ // reach the terminal
            return utility(boardState); // evaluate the boardState!
        }

        if(isMaxPlayer){
            // at max node, update alpha only
            // use lowScore to represent the updated alpha!
            int maxScore = alpha; // -inf -> 4 ->
            for (PentagoMove move: possibleLinkedMoves) {
                // clone it
                PentagoBoardState newPbs = (PentagoBoardState) boardState.clone();
                //PentagoBoardState newPbs = new PentagoBoardState(boardState); // clone the PBS for safety!
                newPbs.processMove(move); // update the cloned PBS!
                int score = miniMax(newPbs,false, depthLimit - 1, maxScore, beta, isTerminalState, numMovesToConsider);
                if(score >=25 && score <= 30) {
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
                        score = miniMax(newPbs,false, 1, maxScore, beta, false, 20);
                    }
                    catch (IndexOutOfBoundsException e){
                        System.out.println("Index out of bound!" + e);
                        return maxScore;
                    }

                    //score = miniMax(newPbs,false, 2, maxScore, beta, false, 50);
                }
                else if(score > 30){
                    System.out.println("This is a high score move!");
                    return maxScore;
                }
                if(score == 0 || score == 1){
                    // this means that we are at the beginning, no need to explore A LOT!
                    return score;
                    //break;
                }
                // 3 initially, returned by the FIRST Min Node
                // Now explore the SECOND Min Node, with alpha = 3, beta = -inf


                if(score > maxScore ) maxScore = score; // score better than the current is ACCEPTED!
                // 3 > -inf, so maxScore (alpha) is now 3.
                // 3 is returned after the Second Min Node is pruned..., maxScore is not updated
                // we keep searching the THIRD MIN NODE...

                if(alpha >= beta){ // in this case 3 > 2
                    //break;
                    //System.out.println("pruned!");
                    return alpha;

                }
            }
            return maxScore;
            // 3 is returned in the END!!
        }
        else{// the Min player's turn
            // use lowScore to represent the updated beta!
            int lowScore = beta; // +inf (MAX_VALUE;)
            for (PentagoMove move: possibleLinkedMoves) {
                // clone it
                PentagoBoardState newPbs = (PentagoBoardState) boardState.clone(); // clone the PBS for safety!
                newPbs.processMove(move); // update the cloned PBS!
                int score = miniMax(newPbs,true, depthLimit - 1, alpha, lowScore, isTerminalState , numMovesToConsider); // 3

                /*if(score >=15 && score <= 20) {
                    System.out.println("bigScore at min! we keep going! "+ score); // maybe we should dive in here more! give it more depth limit

                    try{
                        score = miniMax(newPbs,true, 1, alpha, lowScore, false, 10);
                    }
                    catch (IndexOutOfBoundsException e){
                        System.out.println("Index out of bound!" + e);
                        return lowScore;
                    }

                    //score = miniMax(newPbs,false, 2, maxScore, beta, false, 50);
                }
                else if(score > 20){ // which is really high... enough
                    System.out.println("This is a high score move!");
                    return lowScore;
                }
                */
                if(score > 25){ // which is really high... enough
                    System.out.println("This is a high score move!");
                    return lowScore;
                }
                if(score == 0 || score == 1){
                    // this means that we are at the beginning, no need to explore A LOT!
                    return score;
                    //break;
                }
                // now it reaches the ROOT [2] -> return 2
                if(score < lowScore ) lowScore = score;
                // FIRST ROUND:
                // 3 < inf yes -> lowScore(beta) is now 3
                // 12 is not satisfied -> lowScore not updated
                // 8 is not satisfied -> lowScore not updated
                // lowScore keeps to be 3

                // SECOND ROUND:
                // 2 is indeed lower than 3, so lowScore(beta) is 2 now.
                // we notice that alpha < beta now, which is not good -> prune it, stop searching this node
                // pruned, return alpha (3)


                // THIRD ROUND:
                // 1. 14 is returned, lowScore is updated to be 14
                // 2. 5 ....
                // 3. 2 is returned, ---lowScore is updated to be 2 --> again, pruned ...
                // pruned, return alpha (3)

                if(alpha >= lowScore){ // in this case 3 > 2
                    //break;
                    //System.out.println("pruned!");
                    return alpha;

                }
            }
            return lowScore; // 3 is returned by the FIRST MinNode


        }


        //return 1;
    }

    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        //MyTools.getSomething();
        boardState.firstPlayer();


        int bestScore = Integer.MIN_VALUE;
        Move bestMove = boardState.getRandomMove();

        ArrayList<PentagoMove> childrenMoves = boardState.getAllLegalMoves();
       // System.out.println("there are " + childrenMoves.size() + "possible next moves for this state");
        // randomly

        ArrayList<PentagoMove> movesToConsider = new ArrayList<>();

        Collections.shuffle(childrenMoves);
        for(int i = 0;i<20;i++){
            PentagoMove randMove = childrenMoves.get(i);
            movesToConsider.add(randMove);
        }


        int count = 0;
        for (PentagoMove move: movesToConsider) {
            // clone it
            System.out.println("Now process the: "+count);
            PentagoBoardState newPbs = (PentagoBoardState) boardState.clone();
            newPbs.processMove(move);// when we take this move, what would happen? we will calculate this miniMax score!

            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;
            int depthAllocated = 3; // 3 is fine for play time
            int numMovesConsider = 20;
            //System.out.println("Now compute the minimax Score....");
            int score = miniMax(newPbs, true, depthAllocated, alpha, beta, false,20 );

            //System.out.println("The score is: " + score);
            if(score > bestScore){
                bestScore = score;
                bestMove = move;
            }
            count++;

        }


        // Is random the best you can do?
       // Move myMove = boardState.getRandomMove();




        // Return your move to be processed by the server.
        return bestMove;
    }
}