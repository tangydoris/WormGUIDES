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
import wormguides.model.CellCases;
import wormguides.model.Connectome;
import wormguides.model.ProductionInfo;
import wormguides.model.Rule;
import wormguides.model.TerminalCellCase;

public class ContextMenuController extends AnchorPane implements Initializable {
	
	@FXML private VBox mainVBox;
	@FXML private HBox expressesHBox;
	@FXML private HBox wiredToHBox;
	
	@FXML private Text nameText;
	@FXML private Button info;
	@FXML private Button color;
	@FXML private Button expresses;
	@FXML private Button wiredTo;
	@FXML private Button colorNeighbors;
	
	private int count; // to show loading in progress
	
	private String cellName;
	private ContextMenu expressesMenu;
	private MenuItem expressesTitle;
	private MenuItem loadingMenuItem;
	private Service<ArrayList<String>> expressesQueryService;
	private Service<Void> loadingService;
	
	private ContextMenu wiredToMenu;
	private MenuItem colorAll;
	private Menu preSyn, postSyn, electr, neuro;
	private Service<ArrayList<ArrayList<String>>> wiredToQueryService;
	
	private CellCases cellCases;
	private ProductionInfo productionInfo;
	private Connectome connectome;
	
	private Stage parentStage;
	
	private BooleanProperty bringUpInfoProperty;
	
	public ContextMenuController(Stage stage, BooleanProperty bringUpInfoProperty, 
			CellCases cases, ProductionInfo info, Connectome connectome) {
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
									int num = count%modulus;
									switch (num) {
										case 1:		loading+=".";
													break;
										case 2:		loading+="..";
													break;
										case 3:		loading+="...";
													break;
										case 4:		loading+="....";
													break;
										default:	break;
									}
									
									loadingMenuItem.setText(loading);
								}
							});
							try {
								Thread.sleep(WAIT_TIME_MILLI);
								count++;
								if (count<0)
									count=0;
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
						if (cellName!=null && !cellName.isEmpty()) {
							if (cellCases==null)  {
								System.out.println("null cell cases");
								return null; //error check
							}
							
							if (!cellCases.containsTerminalCase(cellName)) {
								cellCases.makeTerminalCase(cellName,
										connectome.queryConnectivity(cellName, true, false, false, false, false),
										connectome.queryConnectivity(cellName, false, true, false, false, false),
										connectome.queryConnectivity(cellName, false, false, true, false, false),
										connectome.queryConnectivity(cellName, false, false, false, true, false),
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
						if (cellName!=null && !cellName.isEmpty()) {
							ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
							if (cellCases.containsTerminalCase(cellName)) {
								TerminalCellCase terminalCase = cellCases.getTerminalCellCase(cellName);
								results.add(PRE_SYN_INDEX, terminalCase.getPresynapticPartners());
								results.add(POST_SYN_INDEX, terminalCase.getPostsynapticPartners());
								results.add(ELECTR_INDEX, terminalCase.getElectricalPartners());
								results.add(NEURO_INDEX, terminalCase.getNeuromuscularPartners());
							}
							else {
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
	
	public void setInfoButtonListener(EventHandler<MouseEvent> handler) {
		info.setOnMouseClicked(handler);
	}
	
	public void setColorButtonListener(EventHandler<MouseEvent> handler) {
		color.setOnMouseClicked(handler);
	}
	
	public void setColorNeighborsButtonListener(EventHandler<MouseEvent> handler) {
		colorNeighbors.setOnMouseClicked(handler);
	}
	
	public void setWiredToButtonListener(EventHandler<MouseEvent> handler) {
		wiredTo.setOnMouseClicked(handler);
	}
	
	public void setExpressesButtonListener(EventHandler<MouseEvent> handler) {
		expresses.setOnMouseClicked(handler);
	}
	
	public String getName() {
		return nameText.getText();
	}
	
	public void removeTerminalCaseFunctions(boolean remove) {
		if (remove && mainVBox.getChildren().contains(expressesHBox) 
				&& mainVBox.getChildren().contains(wiredToHBox))
			mainVBox.getChildren().removeAll(expressesHBox, wiredToHBox);
		else if (!remove && !mainVBox.getChildren().contains(expressesHBox)
				&& !mainVBox.getChildren().contains(wiredToHBox))
			mainVBox.getChildren().addAll(expressesHBox, wiredToHBox);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadingMenuItem = new MenuItem("Loading");
		
		expresses.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (expressesMenu==null) {
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
							expressesMenu.getItems().remove(loadingMenuItem);
							ArrayList<String> results = expressesQueryService.getValue();
							if (results!=null) {
								for (String result : results) {
									MenuItem item = new MenuItem(result);
									
									item.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent event) {
											Rule rule = Search.addColorRule(SearchType.GENE, result, 
													DEFAULT_COLOR, SearchOption.CELL);
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
				expressesMenu.getItems().addAll(expressesTitle, loadingMenuItem);
				expressesMenu.show(expresses, Side.RIGHT, 0, 0);
				
				expressesQueryService.restart();
			}
		});
		
		wiredTo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (wiredToMenu==null) {
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
					
					wiredToQueryService.setOnScheduled(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							loadingService.restart();
						}
					});
					
					wiredToQueryService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							loadingService.cancel();
							resetLoadingMenuItem();
						}
					});
					
					wiredToQueryService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							loadingService.cancel();
							wiredToMenu.getItems().remove(loadingMenuItem);
							ArrayList<ArrayList<String>> results = wiredToQueryService.getValue();
							if (results!=null) {
								wiredToMenu.getItems().addAll(colorAll, preSyn, postSyn, electr, neuro);
								
								colorAll.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										Rule rule = Search.addGiantConnectomeColorRule(cellName, 
												DEFAULT_COLOR, true, true, true, true);
										rule.showEditStage(parentStage);
									}
								});
								
								populateWiredToMenu(results.get(PRE_SYN_INDEX), preSyn, true, false, false, false);
								populateWiredToMenu(results.get(POST_SYN_INDEX), postSyn, false, true, false, false);
								populateWiredToMenu(results.get(ELECTR_INDEX), electr, false, false, true, false);
								populateWiredToMenu(results.get(NEURO_INDEX), neuro, false, false, false, true);
							}
						}
					});
				}
				
				wiredToMenu.getItems().clear();
				
				preSyn.getItems().clear();
				postSyn.getItems().clear();
				electr.getItems().clear();
				neuro.getItems().clear();
				
				wiredToMenu.show(wiredTo, Side.RIGHT, 0, 0);
				
				wiredToQueryService.restart();
			}
		});
	}
	
	private void resetLoadingMenuItem() {
		if (loadingMenuItem!=null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (wiredToMenu!=null && wiredToMenu.getItems().contains(loadingMenuItem))
						wiredToMenu.getItems().remove(loadingMenuItem);
					
					else if (expressesMenu!=null && expressesMenu.getItems().contains(loadingMenuItem))
						expressesMenu.getItems().remove(loadingMenuItem);
					
					loadingMenuItem.setText("Loading");
				}
			});
		}
	}
	
	private void populateWiredToMenu(ArrayList<String> results, Menu menu, boolean isPresynaptic, 
			boolean isPostsynaptic, boolean isElectrical, boolean isNeuromuscular) {
		if (results.isEmpty()) {
			MenuItem none = new MenuItem("None");
			menu.getItems().add(none);
			return;
		}
		
		MenuItem all = new MenuItem("Color All");
		menu.getItems().add(all);
		
		all.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Rule rule = Search.addGiantConnectomeColorRule(cellName, DEFAULT_COLOR, 
						isPresynaptic, isPostsynaptic, isElectrical, isNeuromuscular);
				rule.showEditStage(parentStage);
			}
		});
		
		for (String result : results) {
			MenuItem item = new MenuItem(result);
			menu.getItems().add(item);
			
			item.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Rule rule = Search.addConnectomeColorRule(result, DEFAULT_COLOR, 
							isPresynaptic, isPostsynaptic, isElectrical, isNeuromuscular);
					rule.showEditStage(parentStage);
				}
			});
		}
	}
	
	@FXML public void showInfoAction() {
		if (bringUpInfoProperty!=null)
			bringUpInfoProperty.set(true);
	}
	
	public void setName(String name) {
		name = name.trim();
		
		if (name.startsWith("Ab"))
			name = "AB"+name.substring(2);
		
		nameText.setText(name);
		cellName = name;
	}

	private final long WAIT_TIME_MILLI = 750;
	private final double MAX_MENU_HEIGHT = 200;
	private final int PRE_SYN_INDEX = 0,
					POST_SYN_INDEX = 1,
					ELECTR_INDEX = 2,
					NEURO_INDEX = 3;
	private final Color DEFAULT_COLOR = Color.WHITE;
}
