package renderer;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.scene.shape.Line;
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
        Vector3D normal = calculateNormal(poly);
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
        Vector3D normal = calculateNormal(poly);
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
            List<Polygon> rotatedPolygons = new ArrayList<>(scene.getPolygons());
            Vector3D lightToRotate = scene.getLight();
            // Calculates rotation matrices
            Transform xRotation = Transform.newXRotation(xRot);
            Transform yRotation = Transform.newYRotation(yRot);
            // Rotates all vertices of all polygons
            for(Polygon p : rotatedPolygons){
                for(int i = 0;i<3;i++){
                    if(xRot!=0){
                        p.getVertices()[i] = xRotation.multiply(p.getVertices()[i]);
                    }
                    if(yRot!=0){
                        p.getVertices()[i] = yRotation.multiply(p.getVertices()[i]);
                    }
                }
            }
            if(xRot!=0){
                lightToRotate = xRotation.multiply(lightToRotate);
            }
            if(yRot!=0){
                lightToRotate = yRotation.multiply(lightToRotate);
            }
            return new Scene(rotatedPolygons, lightToRotate);

    }

    /**
     * This should translate the scene by the appropriate amount.
     *
     * @param scene
     * @return
     */
    public static Scene translateScene(Scene scene) {
        if(scene == null){
            return  null;
        }
        float xTranslationAmount = Float.MAX_VALUE, yTranslationAmount = Float.MAX_VALUE;
        for (Polygon p : scene.getPolygons()) {
            for (Vector3D v : p.getVertices()) {
                xTranslationAmount = Math.min(xTranslationAmount, v.x);
                yTranslationAmount = Math.min(yTranslationAmount, v.y);
            }
        }
        List<Polygon> translatedPolygons = new ArrayList<>(scene.getPolygons());
        Transform translate = Transform.newTranslation(new Vector3D(-(xTranslationAmount-(GUI.CANVAS_WIDTH/8)), -(yTranslationAmount-(GUI.CANVAS_WIDTH/8)), 0));
        for (Polygon p : translatedPolygons) {
            for (int i = 0; i < 3; i++) {
                p.getVertices()[i] = translate.multiply(p.getVertices()[i]);
            }
        }
        return new Scene(translatedPolygons, scene.getLight());
    }

    /**
     * This scales the scene so it all fits within the bounds of the canvas
     * @param scene the scene to scale
     * @return
     */
    public static Scene scaleScene(Scene scene) {
        Line sceneBounds = getSceneBounds(scene);
        float sceneHeight = (float) (sceneBounds.getEndY()-sceneBounds.getStartY());
        float sceneWidth = (float) (sceneBounds.getEndX()-sceneBounds.getStartX());

        float scale = 1;
        boolean useWidth = (sceneWidth > sceneHeight);
        if (useWidth) {
            scale = ((GUI.CANVAS_WIDTH - (GUI.CANVAS_WIDTH / 4)) / sceneWidth);
        } else {
            scale = ((GUI.CANVAS_HEIGHT - (GUI.CANVAS_WIDTH / 4)) / sceneHeight);
        }
        Transform matrixScalar = Transform.newScale(scale, scale, scale);
        ArrayList<Polygon> scaledPolygons = new ArrayList<>(scene.getPolygons());
        for (Polygon p : scaledPolygons) {
            for (int i = 0; i < 3; i++) {
                p.getVertices()[i] = matrixScalar.multiply(p.getVertices()[i]);
            }
        }
        return new Scene(scaledPolygons, matrixScalar.multiply(scene.getLight()));
    }

    /**
     * Computes the edgelist of a single provided polygon, as per the lecture
     * slides.
     */
    public static EdgeList computeEdgeList(Polygon poly) {
        Vector3D[] polygonVerticies = {poly.getVertices()[0], poly.getVertices()[1], poly.getVertices()[2]};
        float minY = Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        for(int i = 0;i<3;i++){
            if(polygonVerticies[i].y>maxY){
                maxY = poly.getVertices()[i].y;
            }
            if(polygonVerticies[i].y<minY){
                minY = poly.getVertices()[i].y;
            }
        }
        EdgeList newEdgeList = new EdgeList(minY, maxY);
        for (int i = 0; i < polygonVerticies.length ; i ++) {
            Vector3D a = polygonVerticies[i];
            Vector3D b = polygonVerticies[(i+1)%3];
            float xSlope = (b.x - a.x) / ((int)b.y - (int)a.y);
            float zSlope = (b.z - a.z) / ((int)b.y - (int)a.y);
            float x = a.x;
            int y = (int)a.y;
            float z = a.z;
            if (a.y < b.y) {
                while (y <= (int)b.y) {
                    newEdgeList.setLeftX(y, x);
                    newEdgeList.setLeftZ(y, z);
                    z += zSlope;
                    x += xSlope;
                    y++;
                }
            } else {
                while (y >= (int)b.y) {
                    newEdgeList.setRightX(y, x);
                    newEdgeList.setRightZ(y, z);
                    z -= zSlope;
                    x -= xSlope;
                    y--;
                }
            }
        }
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
        for (int y = polyEdgeList.getStartY(); y < polyEdgeList.getEndY(); y++) {
            float slope = (polyEdgeList.getRightZ(y) - polyEdgeList.getLeftZ(y)) / (polyEdgeList.getRightX(y) - polyEdgeList.getLeftX(y));
            int x = (int)polyEdgeList.getLeftX(y);
            float z = polyEdgeList.getLeftZ(y);
            while (x <= (int)polyEdgeList.getRightX(y) - 1) {
                if ((y>=0&&x>=0&&y<GUI.CANVAS_HEIGHT&&x<GUI.CANVAS_WIDTH)&&z < zdepth[x][y]) {
                    zbuffer[x][y] = polyColor;
                    zdepth[x][y] = z;
                }
                z = z + slope;
                x++;
            }
        }
    }

    /**
     * Returns a rectangle that represents the bounds of the scene
     * @param scene The scene to get the bounds of
     * @return
     */
    public static Line getSceneBounds(Scene scene){
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (Polygon p : scene.getPolygons()) {
            for (Vector3D v : p.getVertices()) {
                minX = Math.min(minX,v.x);
                minY = Math.min(minY, v.y);
                maxX = Math.max(maxX, v.x);
                maxY = Math.max(maxY, v.y);
            }
        }
        return new Line(minX,minY,maxX,maxY);
    }

    /**
     * Calculates the normal of the supplied polygon
     * @param poly the polygon to get the normal vector3d of
     * @return
     */
    public static Vector3D calculateNormal(Polygon poly){
        // V2-V1
        Vector3D tempVector1 = poly.getVertices()[1].minus(poly.getVertices()[0]);
        // V3-V2
        Vector3D tempVector2 = poly.getVertices()[2].minus(poly.getVertices()[1]);
        // (V2-V1)X(V3-V2)
        return tempVector1.crossProduct(tempVector2).unitVector();
    }


}

// code for comp261 assignments
