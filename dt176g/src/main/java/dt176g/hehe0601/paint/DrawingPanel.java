package dt176g.hehe0601.paint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JPanel;

import io.reactivex.rxjava3.core.Observable;

/**
 * 
 * This class represents the drawing area of the paint application. It handles
 * logic regarding what is actually drawn out onto the canvas.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2020-01-02
 * @version 3.0
 *
 */
public class DrawingPanel extends JPanel {

	private static final long serialVersionUID = -4824399496624903990L;

//	Drawing myDrawing;
	private Shape shape = null;

	private Observable<Point> mousePressed;
	private Observable<Point> mouseDragged;

	private Observable<String> selectedShapeObs;
	private Observable<Integer> selectedSizeObs;
	private Observable<Color> selectedColorObs;

	private Observable<Shape> myDrawingObs;
	// This container mirrors the one server side.
	private ConcurrentLinkedQueue<Shape> myDrawing = null;

	public DrawingPanel(Observable<String> shapeObs, Observable<Integer> sizeObs, Observable<Color> colorObs) {
		super();
		myDrawing = new ConcurrentLinkedQueue<Shape>();
		super.setBackground(Color.WHITE);
		this.selectedShapeObs = shapeObs;
		this.selectedSizeObs = sizeObs;
		this.selectedColorObs = colorObs;
		mousePressed = activeMousePressed();
		mouseDragged = activeMouseDragged();

		mousePressed.withLatestFrom(selectedShapeObs, selectedSizeObs, selectedColorObs, (pos, shape, size, color) -> {

			this.pressPanel(shape, pos, color, size);
			return Observable.empty();
		}).subscribe();

		mouseDragged.subscribe(p -> pressDragPanel(p));
		setMyDrawing(activeMouseReleased());

		repaint();
	}

	public void setMyDrawing(Observable<Shape> pDrawing) {
		this.myDrawingObs = pDrawing.filter(s -> s != null && s.isValidShape());
		this.myDrawingObs.subscribe(s -> {
			myDrawing.add(s);
			repaint();
		});
		this.shape = null;

	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		for (Shape recievedShape : myDrawing) {
			recievedShape.draw(g);
			if (recievedShape instanceof ClearRequest) {
				myDrawing.clear();
				((ClearRequest) recievedShape).clear(g, getWidth(), getHeight());
			}
		}
		if (this.shape != null) {
			this.shape.draw(g);
		}
	}

	public void pressPanel(String selectedShape, Point p, Color pColor, Integer selectedStroke) {
		if (shape != null) {
			shape = null;
		}

		String colorString = "#" + Integer.toHexString(pColor.getRGB()).substring(2);
		if (selectedShape == "Rectangle") {
			shape = new Rectangle(p, colorString, selectedStroke);
		}

		if (selectedShape == "Ellipse") {
			shape = new Ellipse(p, colorString, selectedStroke);
		}

		if (selectedShape == "Circle") {
			shape = new Circle(p, colorString, selectedStroke);
		}
		if (selectedShape == "Line") {
			shape = new Line(p, colorString, selectedStroke);
		}
		if (selectedShape == "Freestyle") {
			shape = new Freestyle(p, colorString, selectedStroke);
		}
	}

	public void pressDragPanel(Point p) {
		if (this.shape != null) {
			if (shape instanceof Freestyle) {
				shape.addPoint(p);
			} else {
				shape.setPoint(p);
			}
			repaint();
		}
	}

	// Observable tracking the location of an active mousepress.
	public Observable<Point> activeMousePressed() {
		return Observable.create(emitter -> {
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					emitter.onNext(new Point(e.getX(), e.getY()));
				}

			});
		});
	}

	// Observable tracking the active movement of the mouse cursor
	public Observable<Point> activeMouseMove() {
		return Observable.create(emitter -> {
			this.addMouseMotionListener(new MouseAdapter() {

				@Override
				public void mouseMoved(MouseEvent e) {
					emitter.onNext(new Point(e.getX(), e.getY()));
				}
			});
		});
	}

	// Observable tracking the active dragging of the cursor across the drawing
	// area.
	public Observable<Point> activeMouseDragged() {
		return Observable.create(emitter -> {
			this.addMouseMotionListener(new MouseAdapter() {

				@Override
				public void mouseDragged(MouseEvent e) {
					emitter.onNext(new Point(e.getX(), e.getY()));

				}

			});
		});
	}

	// Observable tracking the location of where the mouse was released, creating
	// the endpoint for the drawing.
	public Observable<Shape> activeMouseReleased() {
		return Observable.create(emitter -> {
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseReleased(MouseEvent e) {
					emitter.onNext(((DrawingPanel) e.getComponent()).shape);
				}
			});
		});
	}

}
