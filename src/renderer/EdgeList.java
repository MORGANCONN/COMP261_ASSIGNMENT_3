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
        min.add(new HashMap<Integer, Float>());
        min.add(new HashMap<Integer, Float>());
        max.add(new HashMap<Integer, Float>());
        max.add(new HashMap<Integer, Float>());
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

    public void setLeftX(int y, float x) {
        if (min.get(0).containsKey(y)) {
            max.get(0).replace(y, x);
        } else {
            min.get(0).put(y, x);
        }
    }

    public float getRightX(int y) {
        if (max.get(0).containsKey(y)) {
            return max.get(0).get(y);
        }
        return 0;
    }

    public void setRightX(int y, float x) {
        if (max.get(0).containsKey(y)) {
            max.get(0).replace(y, x);
        } else {
            max.get(0).put(y, x);
        }
    }

    public float getLeftZ(int y) {
        if (min.get(1).containsKey(y)) {
            return min.get(1).get(y);
        }
        return 0;
    }

    public void setLeftZ(int y, float z) {
        if (min.get(1).containsKey(y)) {
            min.get(1).replace(y, z);
        } else {
            min.get(1).put(y, z);
        }
    }

    public float getRightZ(int y) {
        if (max.get(1).containsKey(y)) {
            return max.get(1).get(y);
        }
        return 0;
    }

    public void setRightZ(int y, float z) {
        if (max.get(1).containsKey(y)) {
            max.get(1).replace(y, z);
        } else {
            max.get(1).put(y, z);
        }
    }
}

// code for comp261 assignments
