package chessengine;

import chessbet.app.com.R;

class Component{ // Holds Component Data;
    private  String  type;
    private int row;
    private  int col;
    private  int resourceId;

    void setRow(int row) {
        this.row = row;
    }

    void setCol(int col) {
        this.col = col;
    }

    void setResourceId(int reSourceId) {
        this.resourceId = reSourceId;
    }

    void setType(String  type) {
        this.type = type;
    }

    int getCol() {
        return col;
    }

    int getResourceId() {
        return resourceId;
    }

    String  getType() {
        return type;
    }

    int getRow() {
        return row;
    }

    static int resID(String c){
        switch (c){
            case "rb":
                return R.drawable.rb;
            case "nb":
                return R.drawable.nb;
            case "bb":
                return R.drawable.bb;
            case "kb":
                return R.drawable.kb;
            case "qb":
                return R.drawable.qb;
            case "pb":
                return R.drawable.pb;
            case "rw":
                return R.drawable.rw;
            case "nw":
                return R.drawable.nw;
            case "bw":
                return R.drawable.bw;
            case "kw":
                return R.drawable.kw;
            case "qw":
                return R.drawable.qw;
            case "pw":
                return R.drawable.pw;
            default:
                return 0;
        }
    }
}
