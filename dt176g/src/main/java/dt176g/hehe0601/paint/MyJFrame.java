package dt176g.hehe0601.paint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import dt176g.hehe0601.paint.rxClient.Client;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 
 * This class handles the main UI functions of the paint program.
 * 
 * @author Henrik Henriksson (hehe0601)
 * @since 2020-12-05
 *
 */
public class MyJFrame extends JFrame {

	private JMenuBar menuBar;

	JMenuItem jmiConnect;
	JMenuItem jmiDisconnect;
	JMenuItem jmiExit;

	private JPanel toolBar;
	private JPanel colorToolbar;
	private JComboBox<String> shapeSelector;
	private JComboBox<Integer> sizeSelector;

	private DrawingPanel drawingPanel;

	private JPanel statusBar;
	private JPanel chosenColor;
	private JPanel colorBar;
	private JLabel Coordinates;

	private Observable<Point> mouseMovedPos;
	private Observable<Point> mouseDraggedPos;

	private Observable<String> selectedShapeObs;
	private Observable<Integer> selectedSizeObs;
	private Observable<Color> selectedColorObs;
	private ArrayList<Observable<Color>> buttonColors;

	private Client client;

	private JMenuItem jmiClear;
	private boolean isConnected = false;

	/**
	 * Required version ID for all serializable classes.
	 */
	private static final long serialVersionUID = 8506614957001646256L;

	/**
	 * Constructor creating the Frame, adding the menubars and panels, as well as
	 * setting the behaviour of the observables.
	 */
	public MyJFrame() {
		super("DT176G - Hehe0601");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setSize(800, 600);
		setLocationRelativeTo(null);

		// Set border Layout calling default constructor.
		setLayout(new BorderLayout());

		buttonColors = new ArrayList<>();

		client = new Client("localhost", 10000);

		makeMenuBar();
		makeToolBar();

		selectedShapeObs = activeShapeSelection(shapeSelector);
		selectedSizeObs = activeSizeSelection(sizeSelector);
		selectedColorObs = Observable.merge(buttonColors);

		makeDrawingPanel();

		makeStatusBar();

		mouseMovedPos = drawingPanel.activeMouseMove();
		mouseDraggedPos = drawingPanel.activeMouseDragged();
		// get the selected background
		selectedColorObs.subscribe(c -> chosenColor.setBackground(c));
		// a merge of two functions performing the same logic. Updates coordinates on
		// mouse moved and mouse dragged.
		Observable.merge(mouseMovedPos, mouseDraggedPos).subscribe(p -> updateCoordinates(p));

	}

	/**
	 * Create the menubar at the top of the Frame. Holds submenus.
	 */
	private void makeMenuBar() {
		// Allocate memory for menuBar object
		menuBar = new JMenuBar();

		// Call functions to create the menus with options.
		makeFileMenu();
		makeEditMenu();

		// Set the menuBar object as the JMenuBar.
		setJMenuBar(menuBar);
	}

	/**
	 * Create the "File" menu. Add shortcuts to each menu item. Add the submenu to
	 * the menubar.
	 */
	private void makeFileMenu() {
		JMenu jmFile = new JMenu("File");
		jmFile.setMnemonic(KeyEvent.VK_F);

		jmiConnect = new JMenuItem("Connect", KeyEvent.VK_N);
		jmiDisconnect = new JMenuItem("Disconnect", KeyEvent.VK_N);
		jmiExit = new JMenuItem("Exit", KeyEvent.VK_X);

		// Add the items to the menu.
		jmFile.add(jmiConnect);
		jmFile.add(jmiDisconnect);
		jmFile.addSeparator();
		jmFile.add(jmiExit);

		// Add Event Listeners
		jmiConnect.addActionListener(e -> connectOption());
		jmiDisconnect.addActionListener(e -> disconnectOption());
		jmiExit.addActionListener(e -> {
			if (isConnected) {
				disconnectOption();
			}
			System.exit(0);
		});
		jmiDisconnect.setEnabled(false);
		jmiConnect.setEnabled(true);

		menuBar.add(jmFile);

	}

	private void connectOption() {
		// call a new connection on a new thread. set outstream towards server and
		// instream from server.
		Observable.just(this.client).subscribeOn(Schedulers.io())
				.doOnNext(c -> c.setoutstreamObservable(drawingPanel.activeMouseReleased().mergeWith(activeClear())))
				.doOnNext(c -> c.attemptConnect()).doOnNext(c -> drawingPanel.setMyDrawing(c.getinstreamObservable()))
				.subscribe();
		jmiConnect.setEnabled(false);
		jmiDisconnect.setEnabled(true);
		isConnected = true;
		repaint();
	}

	private void disconnectOption() {

		client.attemptDisconnect();
		drawingPanel.setMyDrawing(drawingPanel.activeMouseReleased().mergeWith(activeClear()));
		jmiDisconnect.setEnabled(false);
		jmiConnect.setEnabled(true);

	}

	/**
	 * Create the "Edit" menu. Add shortcuts to each menu item. Add the submenu to
	 * the menubar.
	 */
	private void makeEditMenu() {

		JMenu jmEdit = new JMenu("Edit");
		jmEdit.setMnemonic(KeyEvent.VK_E);

		// Create "Undo" option:
		jmiClear = new JMenuItem("Clear Everything", KeyEvent.VK_U);

		// Set shortcut to ctrl+U
		jmiClear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK));

		// Add the items to the menu.
		jmEdit.add(jmiClear);

		menuBar.add(jmEdit);

		jmiClear.addActionListener(e -> {

			drawingPanel.setMyDrawing(drawingPanel.activeMouseReleased());
			repaint();

		});

	}

	/**
	 * Create the topmost tool bar that holds the color chooser and shape selector
	 */
	private void makeToolBar() {
		// Create the toolbar
		toolBar = new JPanel(new BorderLayout());

		// Call functions to add tools to the toolbar
		makeColorToolbar();
		makeShapeSelector();
		makeSizeSelector();

		// Add the tool bar to the top of the page.
		add(toolBar, BorderLayout.PAGE_START);
		toolBar.setVisible(true);
	}

	/**
	 * create the color tool bar used for selecting color.
	 */
	private void makeColorToolbar() {

		// Allocate memory for the color tool bar panel
		colorToolbar = new JPanel();

		// Set layout to grid 0 by 5 for a single line.
		colorToolbar.setLayout(new GridLayout(0, 5));

		Color colors[] = { Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.WHITE };

		// Iterate and create buttons with corresponding colors from the color array.
		for (int i = 0; i < colors.length; i++) {
			JButton btn = new JButton();
			btn.setBackground(colors[i]);
			buttonColors.add(activeColorSelection(btn));
			colorToolbar.add(btn);
		}

		toolBar.add(colorToolbar, BorderLayout.CENTER);

	}

	/**
	 * Create the shape selector combobox used for selecting shape.
	 */
	private void makeShapeSelector() {

		String[] shapeOptions = new String[] { "Rectangle", "Ellipse", "Circle", "Line", "Freestyle" };
		shapeSelector = new JComboBox<String>(shapeOptions);

		toolBar.add(shapeSelector, BorderLayout.EAST);
	}

	// Get the color background of the currently selected color option.
	private Observable<Color> activeColorSelection(JButton button) {
		return Observable.create(emitter -> {
			emitter.onNext(Color.BLACK);
			button.addActionListener(ae -> {
				emitter.onNext(button.getBackground());
			});
		});
	}

	// return the currently selected shape from the combobox as a string.
	private Observable<String> activeShapeSelection(JComboBox<String> pBox) {
		return Observable.create(emitter -> {
			emitter.onNext("Rectangle");
			pBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						emitter.onNext((String) e.getItem());
					}
				}
			});
		});
	}

	// Return the currently selected size selection from the combobox.
	private Observable<Integer> activeSizeSelection(JComboBox<Integer> pBox) {
		return Observable.create(emitter -> {
			emitter.onNext(2);
			pBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						emitter.onNext((Integer) e.getItem());
					}
				}
			});
		});
	}

	// Observable sending a new ClearRequest to the server.
	private Observable<Shape> activeClear() {
		return Observable.<Shape>create(emitter -> {
			jmiClear.addActionListener(ae -> {
				emitter.onNext(new ClearRequest());
			});
		});
	}

	private void makeSizeSelector() {
		Integer sizeOptions[] = { 2, 4, 6, 8, 10 };
		sizeSelector = new JComboBox<Integer>(sizeOptions);

		toolBar.add(sizeSelector, BorderLayout.WEST);

	}

	/**
	 * Create the drawing panel that takes up the bulk of the frame. The drawing
	 * panel includes a mouse motion tracker.
	 */
	private void makeDrawingPanel() {

		drawingPanel = new DrawingPanel(selectedShapeObs, selectedSizeObs, selectedColorObs);
		add(drawingPanel, BorderLayout.CENTER);
		// initialize the drawingpanel with both newly sent shapes and a clear shape.
		drawingPanel.setMyDrawing(drawingPanel.activeMouseReleased().mergeWith(activeClear()));

	}

	/**
	 * Create the Status bar, the status bar presents the user with relevant
	 * information. Current implementation shows currently selected color and
	 * current mouse coordinates on the drawing panel.
	 */
	private void makeStatusBar() {

		statusBar = new JPanel(new BorderLayout());

		makeChosenColor();
		makeCoordinateLabel();

		statusBar.add(colorBar, BorderLayout.EAST);
		statusBar.add(Coordinates, BorderLayout.WEST);

		add(statusBar, BorderLayout.PAGE_END);
		// statusBar.setVisible(true);

	}

	// ---------------------------------------------------------------------------
	/**
	 * ChosenColor is a subpanel of the status bar, holding the currently selected
	 * color. Default is black.
	 */
	private void makeChosenColor() {
		colorBar = new JPanel(new FlowLayout());
		JLabel text = new JLabel("Chosen Color: ");
		chosenColor = new JPanel();
		chosenColor.setPreferredSize(new Dimension(12, 12));
		chosenColor.setBackground(Color.BLACK);

		colorBar.add(text);
		colorBar.add(chosenColor);

	}

	// ---------------------------------------------------------------------------
	/**
	 * Create a JLabel holding presenting the current coordinates. This instance
	 * variable is manipulated in the overridden mouselistener methods.
	 */
	private void makeCoordinateLabel() {
		Coordinates = new JLabel("x/y: ");

	}

	public void updateCoordinates(Point e) {
		Coordinates.setText("Coordinates: " + e.getX() + "," + e.getY());

	}
}
//---------------------------------------------------------------------------
