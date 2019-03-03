#include <stdio.h>
#include <stdlib.h>
#include "string.h"
#include "component.h"

void print_c_board(Component **c_board);

void print_c_board(Component **c_board){
  for(int i=0;i<BOARD_SIZE;i++){
    printf("\t%d",i);
  }
  printf("\n\n");
  for(int i=0;i<BOARD_SIZE;i++){
    printf("%d\t",i);
    for(int j=0;j<BOARD_SIZE;j++){
      printf("%s\t",c_board[i][j].type);
    }
    printf("\n\n");
  }
}

int main(int argc, char const *argv[]) {
  Component **c_board;
  c_board=make_c_board();
  print_c_board(c_board);
  free(c_board);
  return 0;
}
