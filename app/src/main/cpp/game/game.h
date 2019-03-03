#include "../board/component.h"
#include "../player.h"


// Game States
#define UvsU 1
#define UvsC 0
#define CvsC 2

typedef struct Game{
  int player;
  uint state;
  Player *player1;
  Player *player2;
}Game;

#include "player_check.h"

int pawn_movements(int *coordinates,Component **c_board,int player,Game* game);
void configure_component(Component *component,char * type,int point,int *coordinates,int isMoved);

void configure_component(Component *component,char * type,int point,int *coordinates,int isMoved){
  strcpy(component->type,type);
  component->points=point;
  component->coordinates=coordinates;
  component->isMoved=1;
}
 //Pawn Movement
int pawn_movements(int *coordinates,Component **c_board,int player,Game *game){
  Player *current_player;
  int possible=1;
  current_player = (player==1) ? game->player1 : game->player2;
  Component *component;
  component=&c_board[coordinates[FROM_Y]][coordinates[FROM_X]];
  Component *to_component;
  to_component=&c_board[coordinates[TO_Y]][coordinates[TO_X]];
  int y_diff=coordinates[FROM_Y]-coordinates[TO_Y];
  int x_diff=coordinates[FROM_X]-coordinates[TO_X];
  printf("y diff %d , x diff %d  , player %d , isMoved %d , type %c\n",y_diff,
  x_diff,player,component->isMoved,component->type[1]);
  if(!current_player->isCheck==1){ // Check if current_player is
  if(isOccupied(c_board,coordinates[TO_Y],coordinates[TO_X])!=0){
    if(y_diff==2 && (player==1) && x_diff==0 &&  component->isMoved==0 && component->type[1]=='w'){
      printf("%s\n","Err");
      possible=0;
    }
    else if(y_diff==1 && (player==1) && x_diff==0 && component->type[1]=='w'){
      printf("Player 1 played\n");
      possible=0;
    }
    else if(y_diff==-2 && (player==2) && x_diff==0 && component->isMoved==0 && component->type[1]=='b'){
      printf("Player 2 played\n");
      possible=0;
    }
    else if(y_diff==-1 && (player==2) && x_diff==0 && component->type[1]=='b'){
      printf("%s\n","Err");
      possible=0;
    }
  }
  else{
     if((x_diff==1 || x_diff==-1) && y_diff==1 &&  to_component->type[1]=='b' && player==1){
      printf("Player 1 scores\n");
      current_player->points+=to_component->points;
      possible=0;
    }
    else if((x_diff==1 || x_diff==-1) && y_diff==-1 && to_component->type[1]=='w' && player==2){
      printf("Player 2 scores\n");
      current_player->points+=to_component->points;
      possible=0;
    }
  }
}
  if(possible==0){
    configure_component(to_component,component->type,component->points,coordinates,1);
    int *other_coordinates;
    other_coordinates=(int *) calloc(4,sizeof(int));
    other_coordinates[0]=coordinates[0];
    other_coordinates[1]=coordinates[1];
    configure_component(component,"0",0,other_coordinates,0);
    return possible;
  }
  return possible;
}
