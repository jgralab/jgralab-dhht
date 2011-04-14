/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.utilities.greqlinterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIO.TGFilenameFilter;
import de.uni_koblenz.jgralab.ProgressFunction;
import de.uni_koblenz.jgralab.WorkInProgress;
import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueHTMLOutputVisitor;

@WorkInProgress(description = "insufficcient result presentation, simplistic hacked GUI, no load/save functionality, ...", responsibleDevelopers = "horn")
public class GreqlGui extends JFrame {
	private static final long serialVersionUID = 1L;

	private Graph graph;
	final private JFileChooser fileChooser;
	final private JPanel queryPanel;
	final private JTextArea queryArea;
	final private JEditorPane resultPane;
	final private JTabbedPane tabPane;
	final private JTextArea consoleOutputArea;
	final private JButton fileSelectionButton;
	final private JButton evalQueryButton;
	final private JButton stopButton;
	final private JProgressBar progressBar;
	final private BoundedRangeModel brm;
	final private JLabel statusLabel;
	private Evaluator evaluator;
	final private JCheckBox optimizeCheckBox;
	final private JCheckBox debugOptimizationCheckBox;
	final private JScrollPane resultScrollPane;

	class Worker extends Thread implements ProgressFunction {
		BoundedRangeModel brm;
		private long totalElements;
		Exception ex;

		Worker(BoundedRangeModel brm) {
			this.brm = brm;
		}

		@Override
		public void finished() {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						brm.setValue(brm.getMaximum());
					}
				});
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		}

		@Override
		public long getUpdateInterval() {
			return brm.getMaximum() > totalElements ? 1 : totalElements
					/ brm.getMaximum();
		}

		@Override
		public void init(long totalElements) {
			this.totalElements = totalElements;
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						brm.setValue(brm.getMinimum());
					}
				});
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		}

		@Override
		public void progress(long processedElements) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						if (brm.getValue() < brm.getMaximum()) {
							brm.setValue(brm.getValue() + 1);
						}
					}
				});
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}

	class GraphLoader extends Worker {
		private File file;

		GraphLoader(BoundedRangeModel brm, File file) {
			super(brm);
			this.file = file;
		}

		@Override
		public void run() {
			try {
				graph = GraphIO.loadSchemaAndGraphFromFile(
						file.getCanonicalPath(),
						CodeGeneratorConfiguration.MINIMAL, this);
			} catch (Exception e1) {
				graph = null;
				ex = e1;
			}
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						if (graph == null) {
							JOptionPane.showMessageDialog(null,
									ex.getMessage(), ex.getClass()
											.getSimpleName(),
									JOptionPane.ERROR_MESSAGE);
							statusLabel.setText("Couldn't load graph :-(");
						} else {
							try {
								statusLabel.setText("Graph '" + graph.getUid()
										+ "' loaded.");
							} catch (RemoteException e) {
								throw new RuntimeException(e);
							}
						}
						fileSelectionButton.setEnabled(true);
						evalQueryButton.setEnabled(graph != null);
					}
				});
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		}

		@Override
		public void init(long totalElements) {
			super.init(totalElements);
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						statusLabel.setText("Loading graph...");
					}
				});
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}

	class Evaluator extends Worker {
		private String query;

		Evaluator(BoundedRangeModel brm, String query) {
			super(brm);
			this.query = query;
		}

		@Override
		public void run() {
			final GreqlEvaluator eval = new GreqlEvaluator(query, graph, null,
					this);
			eval.setOptimize(optimizeCheckBox.isSelected());
			GreqlEvaluator.DEBUG_OPTIMIZATION = debugOptimizationCheckBox
					.isSelected();
			try {
				eval.startEvaluation();
			} catch (Exception e1) {
				ex = e1;
			}
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						stopButton.setEnabled(false);
						evalQueryButton.setEnabled(true);
						fileSelectionButton.setEnabled(true);
						if (ex != null) {
							brm.setValue(brm.getMinimum());
							statusLabel.setText("Couldn't evaluate query :-(");
							String msg = ex.getMessage();
							if (msg == null) {
								msg = "An exception occured!";
							}
							JOptionPane.showMessageDialog(GreqlGui.this, msg,
									ex.getClass().getSimpleName(),
									JOptionPane.ERROR_MESSAGE);
						} else {
							statusLabel
									.setText("Evaluation finished, loading HTML result - this may take a while...");
						}
					}
				});
				if (ex == null) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JValue result = eval.getEvaluationResult();
							try {
								File resultFile = File.createTempFile(
										"greqlQueryResult", ".html");
								new JValueHTMLOutputVisitor(result, resultFile
										.getCanonicalPath(), graph, false,
										false);
								resultPane.setPage(new URL("file", "localhost",
										resultFile.getCanonicalPath()));
								tabPane.setSelectedComponent(resultScrollPane);
							} catch (IOException e) {
							}
						}
					});
				}
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		}

		@Override
		public void init(long totalElements) {
			super.init(totalElements);
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						statusLabel.setText("Evaluating query...");
					}
				});
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}

	public GreqlGui() {
		super("GReQL GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		queryArea = new JTextArea(15, 50);
		queryArea.setEditable(true);
		queryArea.setText("// Please enter your query here!\n\n");
		JScrollPane queryScrollPane = new JScrollPane(queryArea);

		queryPanel = new JPanel();
		queryPanel.setLayout(new BorderLayout(4, 4));
		queryPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		queryPanel.add(queryScrollPane, BorderLayout.CENTER);

		resultPane = new JEditorPane(
				"text/html; charset=UTF-8",
				"<html>Here the <b>query results</b> will be shown. "
						+ "Simply select a graph, type a query and press the evaluation button."
						+ "</html>");
		resultPane.setEditable(false);
		resultScrollPane = new JScrollPane(resultPane);
		resultScrollPane.setPreferredSize(new Dimension(200, 200));

		brm = new DefaultBoundedRangeModel();
		progressBar = new JProgressBar();
		progressBar.setModel(brm);

		consoleOutputArea = new JTextArea();
		consoleOutputArea.setEditable(false);
		JScrollPane consoleScrollPane = new JScrollPane(consoleOutputArea);
		consoleScrollPane.setPreferredSize(new Dimension(200, 200));

		System.setOut(new ConsoleOutputStream());
		System.setErr(new ConsoleOutputStream());

		tabPane = new JTabbedPane();
		tabPane.addTab("Console", consoleScrollPane);
		tabPane.addTab("Result", resultScrollPane);

		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(TGFilenameFilter.instance());

		fileSelectionButton = new JButton(new AbstractAction("Select Graph") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(fileSelectionButton);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					fileSelectionButton.setEnabled(false);
					evalQueryButton.setEnabled(false);
					statusLabel.setText("Compling schema...");
					new GraphLoader(brm, fileChooser.getSelectedFile()).start();
				}
			}
		});

		evalQueryButton = new JButton(new AbstractAction("Evaluate Query") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				fileSelectionButton.setEnabled(false);
				evalQueryButton.setEnabled(false);
				brm.setValue(brm.getMinimum());
				evaluator = new Evaluator(brm, queryArea.getText());
				evaluator.start();
				stopButton.setEnabled(true);
			}

		});
		evalQueryButton.setEnabled(false);

		optimizeCheckBox = new JCheckBox("Enable optimizer");
		optimizeCheckBox.setSelected(true);

		debugOptimizationCheckBox = new JCheckBox("Debug optimization");
		debugOptimizationCheckBox.setSelected(false);

		stopButton = new JButton(new AbstractAction("Stop evaluation") {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				evaluator.stop(); // this brutal brake is intended!
				stopButton.setEnabled(false);
				fileSelectionButton.setEnabled(true);
				evalQueryButton.setEnabled(true);
				evaluator = null;
				brm.setValue(brm.getMinimum());
				statusLabel.setText("Query aborted.");
			}

		});
		stopButton.setEnabled(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(fileSelectionButton);
		buttonPanel.add(evalQueryButton);
		buttonPanel.add(optimizeCheckBox);
		buttonPanel.add(debugOptimizationCheckBox);
		buttonPanel.add(stopButton);
		queryPanel.add(buttonPanel, BorderLayout.SOUTH);

		statusLabel = new JLabel("Welcome", SwingConstants.LEFT);
		statusLabel.setBorder(new EmptyBorder(0, 4, 4, 4));

		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.add(progressBar, BorderLayout.NORTH);
		statusPanel.add(statusLabel, BorderLayout.SOUTH);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(queryPanel, BorderLayout.NORTH);
		getContentPane().add(tabPane, BorderLayout.CENTER);
		getContentPane().add(statusPanel, BorderLayout.SOUTH);

		// Don't allow shrinking so that buttons get invisible
		this.setMinimumSize(new Dimension(
				buttonPanel.getPreferredSize().width + 10, 450));
		pack();
		setVisible(true);
	}

	private class ConsoleOutputStream extends PrintStream {
		public ConsoleOutputStream() {
			super(new ByteArrayOutputStream());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.PrintStream#write(byte[], int, int)
		 */
		@Override
		public void write(byte[] buf, int off, int len) {
			String aString = new String(buf, off, len);
			consoleOutputArea.append(aString);
		}

	}

	public static void main(String[] args) {
		new GreqlGui();
	}
}
