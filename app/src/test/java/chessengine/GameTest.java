package chessengine;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import  static  org.junit.Assert.assertEquals;

public class GameTest {
    private Game game;
    @Before
    public void setUp(){
        game=new Game();
    }

    @Test
    public  void testAssignGame(){
        String expected="pw";
        Context context=null;
        Cell cell1 =new Cell(1,0,context);
        Cell cell2 =new Cell(2,0,context);
        Component component1=new Component();
        Component component2=new Component();
        component1.setResourceId(1);
        component1.setType("pw");
        cell1.setComponent(component1);

        component2.setResourceId(1);
        component2.setType("0");
        cell2.setComponent(component2);

        game.assignGame(cell1);
        game.assignGame(cell2);
        assertEquals(cell2.getComponent().getType(),expected);
    }



}
