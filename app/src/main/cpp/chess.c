/* Chess 19th Feb 2019

Chess Main Implementer

Collins Magondu
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
// #include <string.h>
#include "game/game.h"
#include "board/console_board.h"
#include "tests/asserts.h"

void printrules(void);
void tests(void);
void _clear_(void);
int *get_movements(int);

Game *game;
Component **c_board;
void _init_(){
// Get game state
game =(Game *)malloc(sizeof(Game));
_clear_();
printf("\x1B[33mENTER GAME STATE\x1B[37m\n");
printf("User vs User => 1\n");
printf("User vs Computer => 0\n");
printf("Computer vs Computer => 2\n");
printf("Exit => Any other letter\n");

game->player1=(Player *) malloc(sizeof(Player));
game->player2=(Player *) malloc(sizeof(Player));
game->player1->name=(char *)malloc(255* sizeof(char));
game->player2->name=(char *)malloc(255* sizeof(char));

int  state;
scanf("%d",&state); // State of the game
fflush(stdin);
if(state==1) {
      game->state=UvsU;
      printf("Enter Player 1 Name\n");
      scanf("%s",game->player1->name);
      fflush(stdin);
      printf("Enter Player 2 Name\n");
      scanf("%s",game->player2->name);
      fflush(stdin);
}
else if(state==0){
      game->state=UvsC;
      printf("Enter Player 1 Name\n");
      scanf("%s",game->player1->name);
      game->player2->name="Computer";
      fflush(stdin);
}
else if(state==2){
      game->state=CvsC;
      game->player1->name="Computer";
      game->player2->name="Computer";
      fflush(stdin);
}
else{
      free(game);
      free(c_board);
      exit(0);

}
printf("%s vs %s\n",game->player1->name,game->player2->name);
printrules();
}
void _clear_(){
  puts("\033c");
}
//Helper Method For exit
void exit_on_ten(int num){
  if(num==10){
    free(game);
    free(c_board);
    exit(0);
  }
}

int *get_movements(int check_state){
  char name[2];
  if(check_state==1){
    (game->player==1) ? strcpy(name,game->player2->name) : strcpy(name,game->player1->name);
     game->player=(game->player==1)? 2:1;
  }
  else{
    printf("%s\n","Reached Check");
    (game->player1->isCheck==1) ? strcpy(name,game->player1->name) : strcpy(name,game->player2->name);
    game->player=(game->player1->isCheck==1)? 1:2;
  }
  printf("%s's turn\n",name);
  int *movements;
  movements=(int *)calloc(4,sizeof(int*));
  printf("Enter from coordinates (x,y) or 10 to exit \n");
  printf("Enter from y :\n");
  scanf("%d",&movements[0]);
  exit_on_ten(movements[0]);
  printf("Enter from x :\n");
  scanf("%d",&movements[1]);
  exit_on_ten(movements[1]);

  printf("Enter to coordinates (x,y) or 10 to exit \n");
  printf("Enter to y :\n");
  scanf("%d",&movements[2]);
  exit_on_ten(movements[2]);
  printf("Enter to x :\n");
  scanf("%d",&movements[3]);
  exit_on_ten(movements[3]);
  return movements;
}


int main(){
  _init_();
  printrules();
  c_board=make_c_board();
  print_c_board(c_board);
  if(game->state==UvsU){
      game_play();
  }

  // tests();
  return 0;
}
void printrules(){
  char rule_line[255];
  int state=0;
  FILE *file;
  file=fopen("Rules.spec","rb");
  while(fgets(rule_line,sizeof(rule_line),file)){
    printf("%s\n",rule_line);
  }
}

void tests(){
  assertTrue(isOccupied(c_board,2,0),0);
}


void game_play(){
 int *coordinates;
 coordinates=get_movements(check_state(c_board,game));
 char type=c_board[coordinates[FROM_Y]][coordinates[FROM_X]].type[0];
  printf("%c\n",type);
  // _clear_();
 if(type=='p'){
    printf("%s\n","Reached");
    int a=pawn_movements(coordinates,c_board,game->player,game);
    _clear_();
    if(a==0){
      printf("%s\n","Done");
    }
    else{
      printf("%s\n","Failed");
    }
 }
 printf("%s : %d \n",game->player1->name,game->player1->points);
 printf("%s : %d \n",game->player2->name,game->player2->points);
 print_c_board(c_board);
 game_play();
}
