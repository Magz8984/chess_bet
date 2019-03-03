package chessengine;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;


public class BoardView extends View {
    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public  BoardView(Context context){
        super(context);

    }

    @Override
    protected  void onDraw(Canvas canvas){

    }
}
