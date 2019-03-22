package chessengine;

import android.util.Log;


class Game {
    private  static Component component_to=null;
    private  static Component component_from=null;
    static void Move(Cell[][] cells){ //Moves Cell From To;
        freeGame();
    }
     static int assignGame(Component component){
        if(component_from==null){
            component_from=component;
        }
        else if(component_to==null){
            component_to=component;
            return 0;
        }
        else{
            Log.d(Game.class.getSimpleName(),"GAME MOVE INVALID");
        }
        return  1;
    }

    private static void freeGame(){
        component_from=null;
        component_to=null;
    }


    private static Component createEmptyComponent(int row,int col){ //Create Empty Component
        Component component=new Component();
        component.setType("0");
        component.setCol(col);
        component.setRow(row);
        component.setResourceId(0);
        return  component;
    }
}
