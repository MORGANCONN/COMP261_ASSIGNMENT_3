package renderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import renderer.Scene.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * <p>
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

    /**
     * Returns true if the given polygon is facing away from the camera (and so
     * should be hidden), and false otherwise.
     */
    public static boolean isHidden(Polygon poly) {
        // V2-V1
        Vector3D tempVector1 = poly.getVertices()[1].minus(poly.getVertices()[0]);
        // V3-V2
        Vector3D tempVector2 = poly.getVertices()[2].minus(poly.getVertices()[1]);
        // (V2-V1)X(V3-V2)
        Vector3D normal = tempVector1.crossProduct(tempVector2);
        if (normal.z > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Computes the colour of a polygon on the screen, once the lights, their
     * angles relative to the polygon's face, and the reflectance of the polygon
     * have been accounted for.
     *
     * @param lightDirection The Vector3D pointing to the directional light read in from
     *                       the file.
     * @param lightColor     The color of that directional light.
     * @param ambientLight   The ambient light in the scene, i.e. light that doesn't depend
     *                       on the direction.
     */
    public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {
        int[] ambientLightValues = {ambientLight.getRed(), ambientLight.getGreen(), ambientLight.getBlue()};
        int[] reflectanceValues = {poly.reflectance.getRed(), poly.reflectance.getGreen(), poly.reflectance.getBlue()};
        int[] incidentValues = {lightColor.getRed(), lightColor.getGreen(), lightColor.getBlue()};
        // V2-V1
        Vector3D tempVector1 = poly.getVertices()[1].minus(poly.getVertices()[0]);
        // V3-V2
        Vector3D tempVector2 = poly.getVertices()[2].minus(poly.getVertices()[1]);
        // (V2-V1)X(V3-V2)
        Vector3D normal = tempVector1.crossProduct(tempVector2);
        float cosOfVector = lightDirection.cosTheta(normal);
        List<Integer> shade = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            float ambientColor = ((float) ambientLightValues[i] / 255) * reflectanceValues[i];
            ambientColor = Math.max(0, ambientColor);
            ambientColor = Math.min(255, ambientColor);
            float incidentColor = ((float) incidentValues[i] / 255) * reflectanceValues[i] * cosOfVector;
            incidentColor = Math.max(0, incidentColor);
            incidentColor = Math.min(255, incidentColor);
            int light = (int) (ambientColor + incidentColor);
            light = Math.max(0, light);
            light = Math.min(255, light);
            shade.add(light);
        }
        return new Color(shade.get(0), shade.get(1), shade.get(2));
    }

    /**
     * This method should rotate the polygons and light such that the viewer is
     * looking down the Z-axis. The idea is that it returns an entirely new
     * Scene object, filled with new Polygons, that have been rotated.
     *
     * @param scene The original Scene.
     * @param xRot  An angle describing the viewer's rotation in the YZ-plane (i.e
     *              around the X-axis).
     * @param yRot  An angle describing the viewer's rotation in the XZ-plane (i.e
     *              around the Y-axis).
     * @return A new Scene where all the polygons and the light source have been
     * rotated accordingly.
     */
    public static Scene rotateScene(Scene scene, float xRot, float yRot) {
        // TODO fill this in.
        return null;
    }

    /**
     * This should translate the scene by the appropriate amount.
     *
     * @param scene
     * @return
     */
    public static Scene translateScene(Scene scene) {
        // TODO fill this in.
        return null;
    }

    /**
     * This should scale the scene.
     *
     * @param scene
     * @return
     */
    public static Scene scaleScene(Scene scene) {
        // TODO fill this in.
        return null;
    }

    /**
     * Computes the edgelist of a single provided polygon, as per the lecture
     * slides.
     */
    public static EdgeList computeEdgeList(Polygon poly) {
        Vector3D[] polygonVerticies = {poly.getVertices()[0], poly.getVertices()[1], poly.getVertices()[1], poly.getVertices()[2], poly.getVertices()[2], poly.getVertices()[0]};
        int maxY = Math.max((int) polygonVerticies[0].y, (int) polygonVerticies[1].y);
        maxY = Math.max(maxY, (int) polygonVerticies[2].y);
        int minY = Math.max((int) polygonVerticies[0].y, (int) polygonVerticies[1].y);
        minY = Math.min(minY, (int) polygonVerticies[2].y);
        HashMap<Integer, Float> minX = new HashMap<>();
        HashMap<Integer, Float> maxX = new HashMap<>();
        HashMap<Integer, Float> minZ = new HashMap<>();
        HashMap<Integer, Float> maxZ = new HashMap<>();
        for (int i = 0; i < polygonVerticies.length - 1; i += 2) {
            float xYslope = (polygonVerticies[i + 1].x - polygonVerticies[i].x) / (polygonVerticies[i + 1].y - polygonVerticies[i].y);
            float zSlope = (polygonVerticies[i+1].z-polygonVerticies[i].z)/(polygonVerticies[i+1].y-polygonVerticies[i].y);

            float x = polygonVerticies[i].x;
            int y = Math.round(polygonVerticies[i].y);
            float z = polygonVerticies[i].z;
            if (polygonVerticies[i].y < polygonVerticies[i + 1].y) {
                while (y <= Math.round(polygonVerticies[i + 1].y)) {
                    minX.put(y, x);
                    minZ.put(y, z);
                    z += zSlope;
                    x += xYslope;
                    y++;
                }
            } else {
                while (y >= Math.round(polygonVerticies[i + 1].y)) {
                    maxX.put(y, x);
                    maxZ.put(y, z);
                    z -= zSlope;
                    x -= xYslope;
                    y--;
                }
            }
        }
        EdgeList newEdgeList = new EdgeList(minY, maxY);
        newEdgeList.min.add(minX);
        newEdgeList.min.add(minZ);
        newEdgeList.max.add(maxX);
        newEdgeList.max.add(maxZ);
        return newEdgeList;
    }

    /**
     * Fills a zbuffer with the contents of a single edge list according to the
     * lecture slides.
     * <p>
     * The idea here is to make zbuffer and zdepth arrays in your main loop, and
     * pass them into the method to be modified.
     *
     * @param zbuffer      A double array of colours representing the Color at each pixel
     *                     so far.
     * @param zdepth       A double array of floats storing the z-value of each pixel
     *                     that has been coloured in so far.
     * @param polyEdgeList The edgelist of the polygon to add into the zbuffer.
     * @param polyColor    The colour of the polygon to add into the zbuffer.
     */
    public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {
        // TODO fill this in.
    }
}

// code for comp261 assignments
