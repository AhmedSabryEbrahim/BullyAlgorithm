package bullyAlgo1;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import Cluster.bullyNetwork;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;

public class main {

	protected Shell shell;
	private Text text_1;
	private Text text;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text text_2;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	private static final String EOL = "\n";
	private Text text_3;
	private Text text_4;
	bullyNetwork network;
    int Coordinator=-1;
	public static void main(String[] args) {
		try {
			main window = new main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(703, 380);
		shell.setText("SWT Application");

		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(139, 31, 76, 21);

		text = new Text(shell, SWT.BORDER);
		text.setBounds(139, 58, 76, 21);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 34, 109, 15);
		lblNewLabel.setText("No. Processes");

		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setBounds(10, 58, 76, 15);
		lblNewLabel_1.setText("No. Iterations");

		Button btnNewButton = formToolkit.createButton(shell, "Enter", SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int numProc = Integer.parseInt(text_1.getText().toString());
				int numIterations = Integer.parseInt(text.getText().toString());
				network = new bullyNetwork(numProc);
				for (int k = 0; k <= numIterations; k++) {
					for (int i = 0; i < numProc; i++) {

						text_2.append("----------- Iteration " + k + "  | Cycle  " + i + "--------" + EOL);
						String[] Respond = network.bullySimulation(i, k, null);
						if (Respond[0] != "")
							text_2.append(Respond[0]);
						if (Respond[1] != "" && Respond[1] != null)
							text_4.append(Respond[1]);
						if (Respond[2] != "-1" || Respond[2] != "")
							text_3.setText(Respond[2]);
						else
							text_3.setText("");
					}
				}
			}
		});
		btnNewButton.setBounds(271, 29, 75, 25);

		text_2 = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		text_2.setLayoutData(new GridData(GridData.FILL_BOTH));
		text_2.setBounds(10, 103, 400, 228);
		formToolkit.adapt(text_2, true, true);

		text_3 = new Text(shell, SWT.BORDER);
		text_3.setBounds(601, 31, 76, 21);
		formToolkit.adapt(text_3, true, true);

		Button btnNewButton_1 = formToolkit.createButton(shell, "Kill Coordinator", SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				network.KillCoordinator(Integer.parseInt(text_3.getText()));
			}
		});
		btnNewButton_1.setBounds(549, 72, 128, 25);

		text_4 = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text_4.setBounds(454, 103, 223, 228);
		formToolkit.adapt(text_4, true, true);

		Label lblNewLabel_2 = formToolkit.createLabel(shell, "Coordinator ID", SWT.NONE);
		lblNewLabel_2.setBounds(480, 34, 114, 15);

		Button btnContinue = formToolkit.createButton(shell, "Continue", SWT.NONE);
		btnContinue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int numProc = Integer.parseInt(text_1.getText().toString());
				int numIterations = Integer.parseInt(text.getText().toString());

				for (int k = 0; k <= numIterations; k++) {
					for (int i = 0; i < numProc; i++) {
						String[] Respond = network.bullySimulation(i, k, null);
						if (Respond[0] != "")
							text_2.append(Respond[0]);
						if (Respond[1] != "" && Respond[1] != null)
							text_4.append(Respond[1]);
						if (Respond[2] != "-1" || Respond[2] != "")
							text_3.setText(Respond[2]);
							
						else
							text_3.setText("");
					}
				}
			}
		});
		btnContinue.setBounds(352, 29, 75, 25);
		
		Button btnMinimum = new Button(shell, SWT.NONE);
		btnMinimum.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int numProc = Integer.parseInt(text_1.getText().toString());
				int numIterations = Integer.parseInt(text.getText().toString());
				int[] arr = { 4, 3, 56, 75, 72, 24, 23, 66, 88, 33, 34, 5, 67, 3, 6, 4, 33, 2, 34, 56, 78, 50, 1 };

				for (int k = 0; k <= numIterations; k++) {
					for (int i = 0; i < numProc; i++) {
						String[] Respond = network.bullySimulation(i, k, arr);
						if (Respond[0] != "")
							text_2.append(Respond[0]);
						if (Respond[1] != "" && Respond[1] != null)
							text_4.append(Respond[1]);
						if (Respond[2] != "-1" || Respond[2] != "")
							text_3.setText(Respond[2]);
							
						else
							text_3.setText("");
					}
				}
			}
		});
		btnMinimum.setBounds(271, 58, 75, 25);
		formToolkit.adapt(btnMinimum, true, true);
		btnMinimum.setText("Minimum");

	}
}
