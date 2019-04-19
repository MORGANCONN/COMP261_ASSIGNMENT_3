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
			while(reader.ready()){
				currentLine = reader.readLine();
				String[] splittedCurrentLine = currentLine.split(",");
				if(splittedCurrentLine.length==3){
					lightPos = new Vector3D(Float.parseFloat(splittedCurrentLine[0]),Float.parseFloat(splittedCurrentLine[1]),Float.parseFloat(splittedCurrentLine[2]));
				} else {
					// Gets Color Reflectivity Of Polygon
					Color polygonColor = new Color(Integer.parseInt(splittedCurrentLine[0]), Integer.parseInt(splittedCurrentLine[1]), Integer.parseInt(splittedCurrentLine[2]));
					// Gets the vectors of the polygon
					List<Vector3D> tempVectors = new ArrayList<>();
					for (int i = 3; i + 2 < splittedCurrentLine.length; i++) {
						tempVectors.add(new Vector3D(Float.parseFloat(splittedCurrentLine[i]), Float.parseFloat(splittedCurrentLine[i + 1]), Float.parseFloat(splittedCurrentLine[i + 2])));
					}
					// Creates a new polygon and adds it to the fiel
					polygons.add(new Scene.Polygon(tempVectors.get(0), tempVectors.get(1), tempVectors.get(2), polygonColor));
				}
			}
			scene = new Scene(polygons,lightPos);
		} catch (IOException e){
			System.out.println(e);
		}
		/*
		 * This method should parse the given file into a Scene object, which
		 * you store and use to render an image.
		 */
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		// TODO fill this in.

		/*
		 * This method should be used to rotate the user's viewpoint.
		 */
	}

	@Override
	protected BufferedImage render() {
		// TODO fill this in.

		/*
		 * This method should put together the pieces of your renderer, as
		 * described in the lecture. This will involve calling each of the
		 * static method stubs in the Pipeline class, which you also need to
		 * fill in.
		 */
		return null;
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
