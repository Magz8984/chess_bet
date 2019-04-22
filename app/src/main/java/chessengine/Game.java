package chessengine;

import java.util.ArrayList;
import java.util.List;

class Game {
    private char turn='w';  //Start At White
    private List<Cell> cells=new ArrayList<>();
    boolean assignGame(Cell cell){ // By ref
        if(cells.size()<2) {
            if (cells.size() == 1) {
                if (!cell.getComponent().getType().equals(cells.get(0).getComponent().getType())) {
                    cells.add(cell);
                }
            } else if (!cell.getComponent().getType().equals("0") && cell.getComponent().getType().charAt(1) ==  turn){
                freeGame();
                cells.add(cell);
            }
        }
           if(cells.size() == 2) {
                Component t_component=cells.get(0).getComponent();
                t_component.setRow(cells.get(1).getComponent().getRow());
                t_component.setCol(cells.get(1).getComponent().getCol());
                cells.get(1).setComponent(t_component);
                cells.get(0).setComponent(createEmptyComponent(cells.get(0).getComponent().getRow(),cells.get(0).getComponent().getCol()));
                if (turn == 'w') {
                        turn='b';
                    }
                    else {
                        turn='w';
                    }
                freeGame();
                return true;
            }
            return  false;
    }

    private void freeGame(){
        cells.clear();
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
