package ae.oleapp.models;

public class LineupRealDragData {

    private final LineupGlobalPlayers item;
    private int pos = 0;

    public LineupRealDragData(LineupGlobalPlayers item, int pos) {
        this.item = item;
        this.pos = pos;
    }

    public LineupGlobalPlayers getItem() {
        return item;
    }

    public int getPos() {
        return pos;
    }
}
