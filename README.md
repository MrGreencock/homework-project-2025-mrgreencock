
# Kétszemélyes játék (2.12)

## Feladat leírása
A 38. ábrán látható módon rendezzünk el 5 kék és 5 piros korongot egy 5×5
mezőből álló táblán. Az egyik játékos színe a kék, a másiké a piros. Felváltva
következnek lépni, amelynek során két saját színű korongot mozdítanak el
ugyanabba az irányba egy-egy nyolcszomszédos üres mezőre, azonban a fekete
mezőkre nem lehet lépni. A játékosok célja az összes figura átjuttatása az
ellenkező oldalra. Az nyer, akinek ez hamarabb sikerül.

## Egy példa játszma

```markdown
##Game

2 2 2 2 2
0 3 0 3 0
0 0 0 0 0
0 3 0 3 0
1 1 1 1 1

PLAYER_1' move [from]: 4 0
PLAYER_1' move [to]: 3 0
2 2 2 2 2
0 3 0 3 0
0 0 0 0 0
1 3 0 3 0
0 1 1 1 1

PLAYER_1' move [from]: 4 2
PLAYER_1' move [to]: 3 2
2 2 2 2 2
0 3 0 3 0
0 0 0 0 0
1 3 1 3 0
0 1 0 1 1

PLAYER_2' move [from]: 0 0
PLAYER_2' move [to]: 1 0
0 2 2 2 2
2 3 0 3 0
0 0 0 0 0
1 3 1 3 0
0 1 0 1 1

PLAYER_2' move [from]: 0 2
PLAYER_2' move [to]: 1 2
0 2 0 2 2
2 3 2 3 0
0 0 0 0 0
1 3 1 3 0
0 1 0 1 1

...

1 0 0 2 1 
0 3 2 3 1 
0 0 1 0 0 
2 3 1 3 0 
0 2 0 0 2 

PLAYER_2' move [from]: 1 2
PLAYER_2' move [to]: 2 1
1 0 0 2 1
0 3 0 3 1
0 2 1 0 0
2 3 1 3 0
0 2 0 0 2

PLAYER_2' move [from]: 0 3
PLAYER_2' move [to]: 1 2
1 0 0 0 1
0 3 2 3 1
0 2 1 0 0
2 3 1 3 0
0 2 0 0 2

PLAYER_1' move [from]: 1 4
PLAYER_1' move [to]: 0 3
Game Over! You don't have any second move to continue the game
1 0 0 1 1
0 3 2 3 0
0 2 1 0 0
2 3 1 3 0
0 2 0 0 2

PLAYER_2 won
``