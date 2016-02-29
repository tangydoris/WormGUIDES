package wormguides.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import wormguides.Search;
import wormguides.SearchOption;
import wormguides.SearchType;
import wormguides.model.CellCasesLists;
import wormguides.model.Connectome;
import wormguides.model.PartsList;
import wormguides.model.ProductionInfo;
import wormguides.model.Rule;
import wormguides.model.TerminalCellCase;

/**
 * ContextMenuController is the controller class for the context menu that shows
 * up on right click on a 3D entity. The menu can be accessed via the 3D
 * subscene or the sulston tree.
 */

public class ContextMenuController extends AnchorPane implements Initializable {

	private Stage ownStage;

	@FXML
	private VBox mainVBox;
	@FXML
	private HBox expressesHBox;
	@FXML
	private HBox wiredToHBox;

	@FXML
	private Text nameText;
	@FXML
	private Button info;
	@FXML
	private Button color;
	@FXML
	private Button expresses;
	@FXML
	private Button wiredTo;
	@FXML
	private Button colorNeighbors;

	private int count; // to show loading in progress

	private String cellName; // lineage name of cell
	private ContextMenu expressesMenu;
	private MenuItem expressesTitle;
	private MenuItem loadingMenuItem;
	private Service<ArrayList<String>> expressesQueryService;
	private Service<Void> loadingService;

	private ContextMenu wiredToMenu;
	private MenuItem colorAll;
	private Menu preSyn, postSyn, electr, neuro;
	private Service<ArrayList<ArrayList<String>>> wiredToQueryService;

	private CellCasesLists cellCases;
	private ProductionInfo productionInfo;
	private Connectome connectome;

	private Stage parentStage;

	private BooleanProperty bringUpInfoProperty;

	/**
	 * Constructur for ContextMenuController
	 * 
	 * @param stage
	 *            the parent stage (or popup window) that the context menu lives
	 *            in, used for rule editing window popups
	 * @param bringUpInfoProperty
	 *            when set to true, RootLayoutController brings up the cell info
	 *            window
	 * @param cases
	 *            list of cell cases (terminal and non-terminal) that are
	 *            currently in the program
	 * @param info
	 *            production information about WormGUIDES
	 * @param connectome
	 *            connectome object that has information about cell connectome
	 */
	public ContextMenuController(Stage stage, BooleanProperty bringUpInfoProperty, CellCasesLists cases,
			ProductionInfo info, Connectome connectome) {
		super();

		this.bringUpInfoProperty = bringUpInfoProperty;
		parentStage = stage;
		cellCases = cases;
		productionInfo = info;
		this.connectome = connectome;

		loadingService = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						final int modulus = 5;
						while (true) {
							if (isCancelled()) {
								break;
							}
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									String loading = "Loading";
									int num = count % modulus;
									switch (num) {
									case 1:
										loading += ".";
										break;
									case 2:
										loading += "..";
										break;
									case 3:
										loading += "...";
										break;
									case 4:
										loading += "....";
										break;
									default:
										break;
									}

									loadingMenuItem.setText(loading);
								}
							});
							try {
								Thread.sleep(WAIT_TIME_MILLI);
								count++;
								if (count < 0)
									count = 0;
							} catch (InterruptedException ie) {
								break;
							}
						}
						return null;
					}
				};
			}
		};

		expressesQueryService = new Service<ArrayList<String>>() {
			@Override
			protected Task<ArrayList<String>> createTask() {
				final Task<ArrayList<String>> task = new Task<ArrayList<String>>() {
					@Override
					protected ArrayList<String> call() throws Exception {
						if (cellName != null && !cellName.isEmpty()) {
							if (cellCases == null) {
								System.out.println("null cell cases");
								return null; // error check
							}

							String funcName = PartsList.getFunctionalNameByLineageName(cellName);
							String searchName = cellName;
							if (funcName != null)
								searchName = funcName;
							if (!cellCases.containsTerminalCase(searchName)) {
								cellCases.makeTerminalCase(cellName, searchName,
										connectome.queryConnectivity(searchName, true, false, false, false, false),
										connectome.queryConnectivity(searchName, false, true, false, false, false),
										connectome.queryConnectivity(searchName, false, false, true, false, false),
										connectome.queryConnectivity(searchName, false, false, false, true, false),
										productionInfo.getNuclearInfo(), productionInfo.getCellShapeData(cellName));
							}
							return cellCases.getTerminalCellCase(cellName).getExpressesWORMBASE();
						}

						return null;
					};
				};
				return task;
			}
		};

		wiredToQueryService = new Service<ArrayList<ArrayList<String>>>() {
			@Override
			protected Task<ArrayList<ArrayList<String>>> createTask() {
				final Task<ArrayList<ArrayList<String>>> task = new Task<ArrayList<ArrayList<String>>>() {
					@Override
					protected ArrayList<ArrayList<String>> call() throws Exception {
						if (cellName != null && !cellName.isEmpty()) {
							ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
							if (cellCases.containsTerminalCase(cellName)) {
								TerminalCellCase terminalCase = cellCases.getTerminalCellCase(cellName);
								results.add(PRE_SYN_INDEX, terminalCase.getPresynapticPartners());
								results.add(POST_SYN_INDEX, terminalCase.getPostsynapticPartners());
								results.add(ELECTR_INDEX, terminalCase.getElectricalPartners());
								results.add(NEURO_INDEX, terminalCase.getNeuromuscularPartners());
							} else {
								results.add(PRE_SYN_INDEX,
										connectome.queryConnectivity(cellName, true, false, false, false, false));
								results.add(POST_SYN_INDEX,
										connectome.queryConnectivity(cellName, false, true, false, false, false));
								results.add(ELECTR_INDEX,
										connectome.queryConnectivity(cellName, false, false, true, false, false));
								results.add(NEURO_INDEX,
										connectome.queryConnectivity(cellName, false, false, false, true, false));
							}

							return results;
						}

						return null;
					};
				};
				return task;
			}
		};
	}

	/**
	 * Sets the stage to which this popup menu belongs.
	 * 
	 * @param stage
	 *            the stage to which this popup menu belongs. This is different
	 *            from the parent's stage that owns this stage
	 * @return void
	 */
	public void setOwnStage(Stage stage) {
		ownStage = stage;
	}

	/**
	 * Returns the stage that the popup context menu lives in
	 * 
	 * @return Stage the stage that the menu lives in (its own stage)
	 */
	public Stage getOwnStage() {
		return ownStage;
	}

	/**
	 * Sets the listener for the 'more info' button click in the menu. Called by
	 * Window3DController
	 * 
	 * @param handler
	 *            the handler (provided by Window3DController) that handles the
	 *            'more info' button click action
	 */
	public void setInfoButtonListener(EventHandler<MouseEvent> handler) {
		info.setOnMouseClicked(handler);
	}

	/**
	 * Sets te listener for the 'color this cell' button click in the menu.
	 * Called by Window3DController and SulstonTreePane since they handle the
	 * click differently. A different mouse click listener is set depending on
	 * where the menu pops up (whether in the 3D subscene or the sulston tree)
	 * 
	 * @param handler
	 *            the handler (provided by Window3DController or
	 *            SulstonTreePane) that handles the 'color this cell' button
	 *            click action
	 */
	public void setColorButtonListener(EventHandler<MouseEvent> handler) {
		color.setOnMouseClicked(handler);
	}

	/**
	 * Sets te listener for the 'color neighbors' button click in the menu.
	 * Called by Window3DController and SulstonTreePane since they handle the
	 * click differently. A different mouse click listener is set depending on
	 * where the menu pops up (whether in the 3D subscene or the sulston tree)
	 * 
	 * @param handler
	 *            the handler (provided by Window3DController or
	 *            SulstonTreePane) that handles the 'color neighbors' button
	 *            click action
	 */
	public void setColorNeighborsButtonListener(EventHandler<MouseEvent> handler) {
		colorNeighbors.setOnMouseClicked(handler);
	}

	/**
	 * Sets the listener for the 'wired to' button click in the menu. Called by
	 * Window3DController
	 * 
	 * @param handler
	 *            the handler (provided by Window3DController) that handles the
	 *            'wired to' button click action
	 */
	public void setWiredToButtonListener(EventHandler<MouseEvent> handler) {
		wiredTo.setOnMouseClicked(handler);
	}

	/**
	 * Sets the listener for the 'gene expressions' button click in the menu.
	 * Called by Window3DController
	 * 
	 * @param handler
	 *            the handler (provided by Window3DController) that handles the
	 *            'gene expressions' button click action
	 */
	public void setExpressesButtonListener(EventHandler<MouseEvent> handler) {
		expresses.setOnMouseClicked(handler);
	}

	/**
	 * Returns the cell name of the context menu (also its title). This name is
	 * either the lineage name or the functional name (if the cell is a terminal
	 * cell)
	 * 
	 * @return cell name (title of the context menu)
	 */
	public String getName() {
		return cellName;
	}

	/**
	 * Disables/enables the 'wired to' button depending on whether the cell is
	 * terminal or non-terminal.
	 * 
	 * @param disable
	 *            if true, 'wired to' button is disabled, otherwise, the button
	 *            is enabled
	 */
	public void disableTerminalCaseFunctions(boolean disable) {
		wiredTo.setDisable(disable);
	}

	/**
	 * Initializer for the loading of ContextMenuLayout.fxml. Sets 'wired to'
	 * and 'gene expression' button actions.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadingMenuItem = new MenuItem("Loading");

		expresses.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (expressesMenu == null) {
					expressesMenu = new ContextMenu();
					expressesMenu.setMaxHeight(MAX_MENU_HEIGHT);

					expresses.setContextMenu(expressesMenu);

					expressesMenu.setOnHidden(new EventHandler<WindowEvent>() {
						@Override
						public void handle(WindowEvent event) {
							if (expressesMenu.getItems().contains(loadingMenuItem))
								expressesMenu.getItems().remove(loadingMenuItem);
						}
					});

					expressesTitle = new MenuItem("Pick Gene To Color");
					expressesTitle.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							event.consume();
						}
					});

					expressesMenu.setAutoHide(true);

					expressesQueryService.setOnScheduled(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									expressesMenu.getItems().addAll(loadingMenuItem);
								}
							});
							loadingService.restart();
						}
					});

					expressesQueryService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							resetLoadingMenuItem();
							loadingService.cancel();
						}
					});

					expressesQueryService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							loadingService.cancel();
							resetLoadingMenuItem();
							ArrayList<String> results = expressesQueryService.getValue();
							if (results != null) {
								for (String result : results) {
									MenuItem item = new MenuItem(result);

									item.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent event) {
											Rule rule = Search.addColorRule(SearchType.GENE, result, DEFAULT_COLOR,
													SearchOption.CELL);
											rule.showEditStage(parentStage);
										}
									});

									expressesMenu.getItems().add(item);
								}
							}
						}
					});
				}

				expressesMenu.getItems().clear();
				expressesMenu.getItems().addAll(expressesTitle);
				expressesMenu.show(expresses, Side.RIGHT, 0, 0);

				expressesQueryService.restart();
			}
		});

		wiredTo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (wiredToMenu == null) {
					wiredToMenu = new ContextMenu();
					wiredToMenu.setMaxHeight(MAX_MENU_HEIGHT);

					wiredTo.setContextMenu(wiredToMenu);

					wiredToMenu.setOnHidden(new EventHandler<WindowEvent>() {
						@Override
						public void handle(WindowEvent event) {
							wiredToQueryService.cancel();
						}
					});

					wiredToMenu.setAutoHide(true);

					colorAll = new MenuItem("Color All");
					preSyn = new Menu("Pre-Synaptic");
					postSyn = new Menu("Post-Synaptic");
					electr = new Menu("Electrical");
					neuro = new Menu("Neuromuscular");

					wiredToQueryService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							ArrayList<ArrayList<String>> results = wiredToQueryService.getValue();

							if (results != null) {
								colorAll.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										Rule rule = Search.addGiantConnectomeColorRule(cellName, DEFAULT_COLOR, true,
												true, true, true);
										rule.showEditStage(parentStage);
									}
								});

								populateWiredToMenu(results.get(PRE_SYN_INDEX), preSyn, true, false, false, false);
								populateWiredToMenu(results.get(POST_SYN_INDEX), postSyn, false, true, false, false);
								populateWiredToMenu(results.get(ELECTR_INDEX), electr, false, false, true, false);
								populateWiredToMenu(results.get(NEURO_INDEX), neuro, false, false, false, true);
							}

							else {
								wiredToMenu.getItems().clear();
								wiredToMenu.getItems().add(new MenuItem("None"));
							}

							wiredToMenu.show(wiredTo, Side.RIGHT, 0, 0);
						}
					});
				}

				wiredToMenu.getItems().clear();
				wiredToMenu.getItems().addAll(colorAll, preSyn, postSyn, electr, neuro);

				wiredToQueryService.restart();
			}
		});
	}

	/**
	 * Removes the MenuItem that shows that gene expression web querying is in
	 * progress.
	 */
	private void resetLoadingMenuItem() {
		if (loadingMenuItem != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (wiredToMenu != null && wiredToMenu.getItems().contains(loadingMenuItem))
						wiredToMenu.getItems().remove(loadingMenuItem);

					else if (expressesMenu != null && expressesMenu.getItems().contains(loadingMenuItem))
						expressesMenu.getItems().remove(loadingMenuItem);

					loadingMenuItem.setText("Loading");
				}
			});
		}
	}

	/**
	 * Populates the input menu with MenuItems for each of the wired-to results.
	 * 
	 * @param results
	 *            Results from either a pre-synaptic, post-synaptic, electrical,
	 *            or neuromuscular query to the connectome
	 * @param menu
	 *            The pre-synaptic, post-synaptic, eletrical, or neuromuscular
	 *            menu that should be populated with these results
	 * @param isPresynaptic
	 *            if true, a pre-synaptic query to the connectome was issued
	 * @param isPostsynaptic
	 *            if true, a pose-synaptic query to the connectome was issued
	 * @param isElectrical
	 *            if true, an electrical query to the connectome was issued
	 * @param isNeuromuscular
	 *            if true, a neuromuscular query to the connectome was issued
	 * @return void
	 */
	private void populateWiredToMenu(ArrayList<String> results, Menu menu, boolean isPresynaptic,
			boolean isPostsynaptic, boolean isElectrical, boolean isNeuromuscular) {
		menu.getItems().clear();

		if (results.isEmpty()) {
			menu.getItems().add(new MenuItem("None"));
			return;
		}

		MenuItem all = new MenuItem("Color All");
		menu.getItems().add(all);

		all.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Rule rule = Search.addGiantConnectomeColorRule(cellName, DEFAULT_COLOR, isPresynaptic, isPostsynaptic,
						isElectrical, isNeuromuscular);
				rule.showEditStage(parentStage);
			}
		});

		for (String result : results) {
			MenuItem item = new MenuItem(result);
			menu.getItems().add(item);

			item.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Rule rule = Search.addConnectomeColorRule(result, DEFAULT_COLOR, isPresynaptic, isPostsynaptic,
							isElectrical, isNeuromuscular);
					rule.showEditStage(parentStage);
				}
			});
		}
	}

	/**
	 * Toggles the BooleanProperty bringUpInfoProperty so that the cell info
	 * window is displayed. ContextMenuController listens for changes in this
	 * toggle.
	 * 
	 * @return void
	 */
	@FXML
	public void showInfoAction() {
		if (bringUpInfoProperty != null) {
			bringUpInfoProperty.set(true);
			bringUpInfoProperty.set(false);
		}
	}

	/**
	 * Sets the linage name (cell/cellbody scope) of the context menu
	 * 
	 * @param name
	 *            lineage name of cell/cell body that the context menu is for
	 */
	public void setName(String name) {
		name = name.trim();

		if (name.startsWith("Ab"))
			name = "AB" + name.substring(2);

		cellName = name;

		String funcName = PartsList.getFunctionalNameByLineageName(name);
		if (funcName != null)
			name = name + " (" + funcName + ")";

		nameText.setText(name);
	}

	/**
	 * Wait time in miliseconds between showing a different number of periods
	 * after 'loading'
	 */
	private final long WAIT_TIME_MILLI = 750;
	private final double MAX_MENU_HEIGHT = 200;
	private final int PRE_SYN_INDEX = 0, POST_SYN_INDEX = 1, ELECTR_INDEX = 2, NEURO_INDEX = 3;
	/** Default color of the rules that are created by the context menu */
	private final Color DEFAULT_COLOR = Color.WHITE;
}
