package renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 * <p>
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {
    public List<HashMap<Integer, Float>> min = new ArrayList<>();
    public List<HashMap<Integer, Float>> max = new ArrayList<>();
    private int startY, endY;

    public EdgeList(int startY, int endY) {
        this.startY = startY;
        this.endY = endY;
    }

    public void addRow(HashMap<Integer, Integer> row) {

    }

    public int getStartY() {
        return startY;
    }

    public int getEndY() {
        return endY;
    }

    public float getLeftX(int y) {
        if (min.get(0).containsKey(y)) {
            return min.get(0).get(y);
        }
        return 0;
    }

    public float getRightX(int y) {
        if (max.get(0).containsKey(y)) {
            return max.get(0).get(y);
        }
        return 0;
    }

    public float getLeftZ(int y) {
        if (min.get(1).containsKey(y)) {
            return min.get(1).get(y);
        }
        return 0;
    }

    public float getRightZ(int y) {
        if (max.get(1).containsKey(y)) {
            return max.get(1).get(y);
        }
        return 0;
    }
}

// code for comp261 assignments
