package jhin3.tui.window;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.GridLayout.Alignment;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.ProgressBar;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.input.KeyStroke;

import jhin3.time.stopwatch.Stopwatch;
import jhin3.time.timer.Timer;
import jhin3.tui.misc.TimerFormatter;

public class TimeWindow extends AbstractJhinWindow {

	private Panel mainPanel;

	private Panel timerPanel;

	private Panel dateTimePanel;

	private Panel stopwatchPanel;

	private Timer timer;

	private ProgressBar timerProgressBar;

	private Label timerTotal;

	private Label timerElapsed;

	private Label timerRemaining;

	private Label currentDate;

	private Label currentTime;

	private Label stopwatchLabel1;

	private Label stopwatchLabel2;

	private Label stopwatchLabel3;

	private Label stopwatchLabel4;

	private Stopwatch stopwatch1;

	private Stopwatch stopwatch2;

	private Stopwatch stopwatch3;

	private Stopwatch stopwatch4;

	private DateTimeFormatter dateFormatter;

	private DateTimeFormatter timeFormatter;

	public TimeWindow(TerminalSize terminalSize) {
		super("Time", terminalSize);

		// Timer initialization (default 20 minutes)
		timer = new Timer(1200000);

		// Create stopwatches
		stopwatch1 = new Stopwatch();
		stopwatch2 = new Stopwatch();
		stopwatch3 = new Stopwatch();
		stopwatch4 = new Stopwatch();

		// Formatters for date and time
		dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		timeFormatter = DateTimeFormatter.ofPattern(" HH:mm:ss ");

		mainPanel = new Panel();
		mainPanel.setLayoutManager(new GridLayout(3));
		mainPanel.setLayoutData(GridLayout.createLayoutData(Alignment.CENTER,
				Alignment.CENTER, true, false));

		addTimerControl();
		mainPanel.addComponent(timerPanel);
		addCurrentDateTime();
		mainPanel.addComponent(dateTimePanel);
		addStopwatches();
		mainPanel.addComponent(stopwatchPanel);

		setComponent(mainPanel);

		new Thread(() -> mainUpdateLoop()).start();

	}

	@Override
	public void updatePosition(TerminalSize newSize) {
		setPosition(new TerminalPosition(0,
				newSize.getRows() - WindowLayoutHelper.getTimeHeight() - 2));
	}

	@Override
	public void updateSize(TerminalSize newSize) {
		setSize(new TerminalSize(newSize.getColumns() - 2,
				WindowLayoutHelper.getTimeHeight()));
	}

	private void addCurrentDateTime() {
		dateTimePanel = new Panel();
		dateTimePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

		currentDate = new Label("The stage ");
		currentTime = new Label("is set.");

		currentDate.setForegroundColor(TextColor.ANSI.WHITE);
		currentTime.setForegroundColor(TextColor.ANSI.WHITE);

		dateTimePanel.addComponent(currentDate);
		dateTimePanel.addComponent(currentTime);
	}

	private void addTimerControl() {
		timerPanel = new Panel();
		timerPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

		Panel textPanel = new Panel();
		textPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

		timerProgressBar = new ProgressBar();
		timerProgressBar.setLabelFormat("");
		timerProgressBar.setMin(0);
		timerProgressBar.setMax(Timer.progressMax);
		timerProgressBar.setPreferredSize(new TerminalSize(49, 1));

		timerTotal = new Label("[T] 00:00:00 ");
		timerElapsed = new Label("[E] 00:00:00 ");
		timerRemaining = new Label("[R] 00:00:00");

		textPanel.addComponent(timerElapsed);
		textPanel.addComponent(timerRemaining);
		textPanel.addComponent(timerTotal);

		timerPanel.addComponent(timerProgressBar);
		timerPanel.addComponent(textPanel);
	}

	private void addStopwatches() {
		stopwatchPanel = new Panel();
		stopwatchPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

		Panel panel1 = new Panel();
		Panel panel2 = new Panel();
		panel1.setLayoutManager(new LinearLayout(Direction.VERTICAL));
		panel2.setLayoutManager(new LinearLayout(Direction.VERTICAL));

		stopwatchLabel1 = new Label("[1] 00:00:00 ");
		stopwatchLabel2 = new Label("[2] 00:00:00 ");
		stopwatchLabel3 = new Label("[3] 00:00:00");
		stopwatchLabel4 = new Label("[4] 00:00:00");

		panel1.addComponent(stopwatchLabel1);
		panel1.addComponent(stopwatchLabel2);
		panel2.addComponent(stopwatchLabel3);
		panel2.addComponent(stopwatchLabel4);

		stopwatchPanel.addComponent(panel1);
		stopwatchPanel.addComponent(panel2);
	}

	private void mainUpdateLoop() {
		do {
			// Timer
			updateTimer();

			// Current time
			updateDateTime();

			// Update stopwatches
			updateStopwatches();

			// Wait a second until the next update
			try {
				Thread.sleep(999);
			} catch (InterruptedException e) {
				e.printStackTrace();
				// Just try to repeat
			}
		} while (true);
	}

	private void updateTimer() {
		WindowBasedTextGUI gui = getTextGUI();
		if (gui != null) {
			try {
				gui.getGUIThread().invokeLater(() -> updateTimerUI());
			} catch (IllegalStateException e) {
				// This is fine, just do nothing in case the gui is not running
			}
		}
	}

	private void updateDateTime() {
		// Write to console
		WindowBasedTextGUI gui = getTextGUI();
		if (gui != null) {
			try {
				gui.getGUIThread().invokeLater(() -> {
					LocalDateTime dateTime = LocalDateTime.now();
					currentDate.setText(dateTime.format(dateFormatter));
					currentTime.setText(dateTime.format(timeFormatter));
				});
			} catch (IllegalStateException e) {
				// This is fine, just do nothing in case the gui is not running
			}
		}
	}

	private void updateStopwatches() {
		WindowBasedTextGUI gui = getTextGUI();
		if (gui != null) {
			try {
				gui.getGUIThread().invokeLater(() -> {
					stopwatchLabel1.setText("[1] "
							+ TimerFormatter.format(stopwatch1.getElapsed())
							+ " ");
					stopwatchLabel2.setText("[2] "
							+ TimerFormatter.format(stopwatch2.getElapsed())
							+ " ");
					stopwatchLabel3.setText("[3] "
							+ TimerFormatter.format(stopwatch3.getElapsed()));
					stopwatchLabel4.setText("[4] "
							+ TimerFormatter.format(stopwatch4.getElapsed()));
				});
			} catch (IllegalStateException e) {
				// This is fine, just do nothing in case the gui is not running
			}
		}
	}

	private void updateTimerUI() {
		timerProgressBar.setValue(timer.getProgress());
		timerTotal.setText(
				"[T] " + TimerFormatter.format(timer.getTotal()) + " ");
		timerElapsed.setText(
				"[E] " + TimerFormatter.format(timer.getElapsed()) + " ");
		timerRemaining
				.setText("[R] " + TimerFormatter.format(timer.getRemaining()));
	}

	@Override
	public boolean handleInput(KeyStroke key) {
		// Base condition: no modification key is pressed
		if (!key.isAltDown() && !key.isCtrlDown() && !key.isShiftDown()
				&& key.getCharacter() != null) {
			switch (key.getCharacter()) {
				// Timer
				case 's' :
					timer.toggle();
					return true;
				case 'c' :
					timer.reset();
					return true;

				// Stopwatches
				case '1' :
					stopwatch1.toggle();
					return true;
				case '2' :
					stopwatch2.toggle();
					return true;
				case '3' :
					stopwatch3.toggle();
					return true;
				case '4' :
					stopwatch4.toggle();
					return true;
				case 'q' :
					stopwatch1.reset();
					return true;
				case 'w' :
					stopwatch2.reset();
					return true;
				case 'e' :
					stopwatch3.reset();
					return true;
				case 'r' :
					stopwatch4.reset();
					return true;
			}
		}

		// Other cases
		return super.handleInput(key);
	}
}
