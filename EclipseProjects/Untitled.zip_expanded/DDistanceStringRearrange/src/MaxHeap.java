import java.util.ArrayList;

public class MaxHeap {

    private ArrayList<CharFreq> list;

    public MaxHeap() {
        this.list = new ArrayList<CharFreq>();
    }

    public MaxHeap(ArrayList<CharFreq> items) {
        this.list = items;
        buildHeap();
    }

    public CharFreq getMax() {
        return list.get(0);
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    private int right(int i) {
        return 2 * i + 2;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private void swap(int i, int parent) {
        CharFreq temp = list.get(parent);
        list.set(parent, list.get(i));
        list.set(i, temp);
    }

    public void insert(CharFreq item) {
        list.add(item);
        int i = list.size() - 1;
        int parent = parent(i);

        while (parent != i && list.get(i).compareTo(list.get(parent)) > 0) {
            swap(i, parent);
            i = parent;
            parent = parent(i);
        }
    }

    public void buildHeap() {
        for (int i = list.size() / 2; i >= 0; i--) {
            maxHeapify(i);
        }
    }

    public CharFreq extractMax() {
        if (list.size() == 0) {
            throw new IllegalStateException("MinHeap is EMPTY");
        } else if (list.size() == 1) {
            CharFreq max = list.remove(0);
            return max;
        }

        // remove the last item ,and set it as new root
        CharFreq max = list.get(0);
        CharFreq lastItem = list.remove(list.size() - 1);
        list.set(0, lastItem);

        // bubble-down until heap property is maintained
        maxHeapify(0);

        // return min key
        return max;
    }

    public void increaseKey(int i, CharFreq key) {
        if (list.get(i).compareTo(key) > 0) {
            throw new IllegalArgumentException("Key is larger than the original key");
        }

        list.set(i, key);
        int parent = parent(i);

        // bubble-up until heap property is maintained
        while (i > 0 && list.get(parent).compareTo(list.get(i)) < 0) {
            swap(i, parent);
            i = parent;
            parent = parent(parent);
        }
    }

    private void maxHeapify(int i) {
        int left = left(i);
        int right = right(i);
        int smallest = -1;

        // find the smallest key between current node and its children.
        if (left <= list.size() - 1 && list.get(left).compareTo(list.get(i)) > 0) {
            smallest = left;
        } else {
            smallest = i;
        }

        if (right <= list.size() - 1 && list.get(right).compareTo(list.get(smallest)) > 0) {
            smallest = right;
        }

        // if the smallest key is not the current key then bubble-down it.
        if (smallest != i) {
            swap(i, smallest);
            maxHeapify(smallest);
        }
    }

}