# pentagoAI
##An AI agent to play Pentago-twist game
(Note that other key classes needed for the full functionality of this game project including GUI are kept private.)
In this project, we work on developing an artifcial intelligence agent to play Pentago-Twist,
a Moku family game. Pentago-Twist has significantly more complexity than Tic-tac-toe and
ve-in-a-row, in which the playboard is divided into four quadrants that can be rotated 90
degree clockwise or 
ipped horizontally after each time a stone is placed. Given the context
of a two-player game, we choose to implement minimax algorithm with alpha-beta pruning as
a benchmark. We further use randomness to extract a certain number of moves to consider in
each iteration instead of exploring all possible moves, which essentially lowers the branching
factor of the game.
The major technical challenges on this project can be divided in three parts including imple-
ment a baseline minimax algorithm, implementing and improving the evaluation function and
discerning immediate winning move for both the agent itself and the opponent.
