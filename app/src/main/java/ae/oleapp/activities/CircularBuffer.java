package ae.oleapp.activities;

public class CircularBuffer {

    private final byte[] buffer;
    private final int capacity;
    private int head;
    private int tail;

    public CircularBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new byte[capacity];
        this.head = 0;
        this.tail = 0;
    }

    public synchronized void write(byte[] data, int length) {
        for (int i = 0; i < length; i++) {
            buffer[tail] = data[i];
            tail = (tail + 1) % capacity;
        }
    }

    public synchronized byte[] read(int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = buffer[head];
            head = (head + 1) % capacity;
        }
        return result;
    }
}
