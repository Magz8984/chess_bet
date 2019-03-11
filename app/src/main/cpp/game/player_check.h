int pawn_check(Component **c_board,Game *game,char type,int i,int j);

int check_state(Component **c_board,Game *game){
 for(int i=0;i<BOARD_SIZE;i++){
    for(int j=0;j<BOARD_SIZE;j++){
       if(c_board[i][j].type[0]=='p'){
         if(pawn_check(c_board,game,c_board[i][j].type[1],i,j)==0){
            return 0;
         }
       }
    }
 }
 return 1;
}

int pawn_check(Component **c_board,Game *game,char type,int i,int j){
  Player *next_player;
  if(type=='b'){
      next_player=game->player1;
        if(i!=7 && j!=0 && j!=7){
          if(strcmp(c_board[i+1][j-1].type,"Kw")==0 ||strcmp(c_board[i+1][j+1].type,"Kw")==0){
            printf("%s\n","Player 1 Check");
            next_player->isCheck=1;
            return 0;
          }
        }
  }
  else if(type=='w'){
    next_player=game->player2;
    if(i!=0 && j!=0 && j!=7){
       if(strcmp(c_board[i-1][j-1].type,"Kb")==0 || strcmp(c_board[i-1][j+1].type,"Kb")==0){
         printf("%s\n","Player 2 Check");
        next_player->isCheck=1;
        return 0;
      }
    }
  }
  return 1;
}
