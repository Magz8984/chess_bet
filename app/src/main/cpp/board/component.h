#define FROM_Y 0
#define FROM_X 1
#define TO_Y 2
#define TO_X 3
#define BOARD_SIZE 8

int points[]={5,3,3,9,10,3,3,5}; //Points To Be Won
const char components[]="rnbQKbnr";

typedef struct Component{
  char *type;
  int points;
  int *coordinates;
  int isMoved;
}Component;

Component **make_c_board(void); // Makes The Board
void fill_component(Component *component,char *type,int points,int *coordinates);
int isOccupied(Component **c_board,int y,int x);

char *concat_str(char a,char b){
  char *comp;
  comp=(char *) malloc(2 * sizeof(char));
  comp[0]=a;
  comp[1]=b;
  return comp;
}

Component **make_c_board(){
   Component **c_board;
   int *coordinates;
   c_board=(Component **)  calloc(8,sizeof(Component*));
   for(int i=0;i<BOARD_SIZE;i++){
      c_board[i]=(Component *)  calloc(8,sizeof(Component));
      for(int j=0;j<BOARD_SIZE;j++){
        coordinates=(int *) calloc(4,sizeof(int));
        coordinates[0]=i; //y
        coordinates[1]=j; //x
        if(i<2){
           if(i==0){
             fill_component(&c_board[i][j],concat_str(components[j],'b'),points[j],coordinates);
           }
           else{
             fill_component(&c_board[i][j],concat_str('p','b'),1,coordinates);
           }
        }
        else if(i>5){
          if(i==7){
             fill_component(&c_board[i][j],concat_str(components[j],'w'),points[j],coordinates);
          }
          else{
             fill_component(&c_board[i][j],concat_str('p','w'),1,coordinates);
          }
        }
        else{
            fill_component(&c_board[i][j],"0",0,coordinates);
        }
      }
   }
   return  c_board;
}
void fill_component(Component *component,char *type,
  int points,int *coordinates){
    component->type=(char *) malloc(2 *sizeof(char));
    strcpy(component->type,type);
    component->points=points;
    component->coordinates=coordinates;
    component->isMoved=0;
    // free(coordinates);
}

int isOccupied(Component **c_board,int y,int x){
  return (c_board[y][x].type[0]!='0') ? 0 : 1;
}
