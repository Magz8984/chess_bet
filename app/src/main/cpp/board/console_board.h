#define GREEN "\x1B[32m"
#define RED "\x1B[31m"
#define NO_COLOR "\x1B[30m"
#define ORANGE "\x1B[33m"
#define BLUE "\x1B[34m"
#define PURPLE "\x1B[35m"
#define L_BLUE "\x1B[36m"
#define WHITE "\x1B[37m"

void print_c_board(Component **c_board);
void game_play();

void print_c_board(Component **c_board){
  for(int i=0;i<BOARD_SIZE;i++){
    printf("\t%s%d",ORANGE,i);
  }
  printf("%s\n\n",ORANGE);
  for(int i=0;i<BOARD_SIZE;i++){
    printf("%s%d%s\t",ORANGE,i,GREEN);
    for(int j=0;j<BOARD_SIZE;j++){
      printf("%s\t",c_board[i][j].type);
    }
    printf("%s\n\n",WHITE);
  }
}
