package ae.oleapp.models;

public class DragData {

    private final PlayerInfo item;
    private int pos = 0;

    public DragData(PlayerInfo item, int pos) {
        this.item = item;
        this.pos = pos;
    }

    public PlayerInfo getItem() {
        return item;
    }

    public int getPos() {
        return pos;
    }
}
