package renderer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Renderer extends GUI {
    public Scene scene;

    @Override
    protected void onLoad(File file) {
        List<Scene.Polygon> polygons = new ArrayList<>();
        int numberOfPolygons = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String currentLine = reader.readLine();
            numberOfPolygons = Integer.parseInt(currentLine);
            Vector3D lightPos = null;
            while (reader.ready()) {
                currentLine = reader.readLine();
                String[] splittedCurrentLine = currentLine.split(",");
                Color polygonColor = null;
                if (splittedCurrentLine.length == 3) {
                    lightPos = new Vector3D(Float.parseFloat(splittedCurrentLine[0]), Float.parseFloat(splittedCurrentLine[1]), Float.parseFloat(splittedCurrentLine[2]));
                } else {
                    List<Vector3D> tempVectors = new ArrayList<>();
                    // Gets Color Reflectivity Of Polygon
                    polygonColor = new Color(Integer.parseInt(splittedCurrentLine[0]), Integer.parseInt(splittedCurrentLine[1]), Integer.parseInt(splittedCurrentLine[2]));
                    // Gets the vectors of the polygon
                    for (int i = 3; i < splittedCurrentLine.length; i = i+3) {
                        tempVectors.add(new Vector3D(Float.parseFloat(splittedCurrentLine[i]), Float.parseFloat(splittedCurrentLine[i + 1]), Float.parseFloat(splittedCurrentLine[i + 2])));
                    }
                    // Creates a new polygon and adds it to the fiel
                    polygons.add(new Scene.Polygon(tempVectors.get(0), tempVectors.get(1), tempVectors.get(2), polygonColor));
                }
            }
            scene = new Scene(polygons, lightPos);
        } catch (IOException e) {
            System.out.println(e);
        }
        /*
         * This method should parse the given file into a Scene object, which
         * you store and use to render an image.
         */
    }

    @Override
    protected void onKeyPress(KeyEvent ev) {
        if(scene==null){
            return;
        }
        if(ev.getKeyCode() == KeyEvent.VK_UP || ev.getKeyCode() == KeyEvent.VK_W){
            scene = Pipeline.rotateScene(scene,(float)Math.toRadians(-2),0);
        } else if(ev.getKeyCode() == KeyEvent.VK_DOWN || ev.getKeyCode() == KeyEvent.VK_S){
            scene = Pipeline.rotateScene(scene,(float)Math.toRadians(2),0);
        } else if(ev.getKeyCode() == KeyEvent.VK_LEFT || ev.getKeyCode() == KeyEvent.VK_A){
            scene = Pipeline.rotateScene(scene,0,(float)Math.toRadians(2));
        } else if(ev.getKeyCode() == KeyEvent.VK_RIGHT || ev.getKeyCode() == KeyEvent.VK_D){
            scene = Pipeline.rotateScene(scene,0,(float)Math.toRadians(-2));
        }
    }
    /**
     * This method should put together the pieces of your renderer, as
     * described in the lecture. This will involve calling each of the
     * static method stubs in the Pipeline class, which you also need to
     * fill in.
     **/
    @Override
    protected BufferedImage render() {
        if (scene == null) {
            return null;
        }
        scene = Pipeline.translateScene(scene);
        scene = Pipeline.scaleScene(scene);
        scene = Pipeline.translateScene(scene);
        Color[][] renderedImg = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
        float[][] zDepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];
        for (int y = 0; y < CANVAS_HEIGHT; y++) {
            for (int x = 0; x < CANVAS_WIDTH; x++) {
                zDepth[y][x] = Float.MAX_VALUE;
                renderedImg[y][x] = Color.gray;
            }
        }
        for (Scene.Polygon p : scene.getPolygons()) {
            if (!Pipeline.isHidden(p)) {
                Color shadedColor = Pipeline.getShading(p, scene.getLight(), Color.WHITE, new Color(getAmbientLight()[0], getAmbientLight()[1], getAmbientLight()[2]));
                EdgeList polgonEdgeList = Pipeline.computeEdgeList(p);
                Pipeline.computeZBuffer(renderedImg, zDepth, polgonEdgeList, shadedColor);
            }
        }

        return convertBitmapToImage(renderedImg);
    }

    /**
     * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
     * indexed by column then row and has imageHeight rows and imageWidth
     * columns. Note that image.setRGB requires x (col) and y (row) are given in
     * that order.
     */
    private BufferedImage convertBitmapToImage(Color[][] bitmap) {
        BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < CANVAS_WIDTH; x++) {
            for (int y = 0; y < CANVAS_HEIGHT; y++) {
                image.setRGB(x, y, bitmap[x][y].getRGB());
            }
        }
        return image;
    }

    public static void main(String[] args) {
        new Renderer();
    }
}

// code for comp261 assignments
