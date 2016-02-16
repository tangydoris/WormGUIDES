package wormguides.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import wormguides.Search;
import wormguides.SearchOption;
import wormguides.SearchType;
import wormguides.model.CellCases;
import wormguides.model.Connectome;
import wormguides.model.ProductionInfo;
import wormguides.model.Rule;
import wormguides.model.TerminalCellCase;

public class ContextMenuController extends AnchorPane implements Initializable {
	
	@FXML private Text nameText;
	@FXML private Button color;
	@FXML private Button expresses;
	@FXML private Button wiredTo;
	@FXML private Button colorNeighbors;
	
	private int count; // to show loading in progress
	
	private HashMap<String, ArrayList<String>> expressesCache;
	
	private String cellName;
	private ContextMenu expressesMenu;
	private MenuItem expressesTitle;
	private MenuItem expressesLoading;
	private Service<ArrayList<String>> expressesQueryService;
	private Service<Void> expressesLoadingService;
	
	private CellCases cellCases;
	private ProductionInfo productionInfo;
	private Connectome connectome;
	
	private Stage parentStage;
	
	private BooleanProperty bringUpInfoProperty;
	
	public ContextMenuController(Stage stage, BooleanProperty bringUpInfoProperty, 
			CellCases cases, ProductionInfo info, Connectome connectome) {
		super();
		
		expressesCache = new HashMap<String, ArrayList<String>>();
		
		this.bringUpInfoProperty = bringUpInfoProperty;
		parentStage = stage;
		cellCases = cases;
		productionInfo = info;
		this.connectome = connectome;
		
		expressesLoadingService = new Service<Void>() {
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
									
									expressesLoading.setText(loading);
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
						// TODO Auto-generated method stub
						if (cellName!=null && !cellName.isEmpty()) {
							if (cellCases==null)  {
								System.out.println("null cell cases");
								return null; //error check
							}
							
							if (expressesCache.containsKey(cellName))
								return expressesCache.get(cellName);
							else {
								TerminalCellCase terminalCase;
								if (!cellCases.containsTerminalCase(cellName)) {
									cellCases.makeTerminalCase(cellName,
											connectome.queryConnectivity(cellName, true, false, false, false, false),
											connectome.queryConnectivity(cellName, false, true, false, false, false),
											connectome.queryConnectivity(cellName, false, false, true, false, false),
											connectome.queryConnectivity(cellName, false, false, false, true, false),
											productionInfo.getNuclearInfo(), productionInfo.getCellShapeData(cellName));
								}
								terminalCase = cellCases.getTerminalCellCase(cellName);
								if (terminalCase!=null) {
									ArrayList<String> results = terminalCase.getExpressesWORMBASE();
									expressesCache.put(cellName, results);
									return results;
								}
							}
						}
						
						return null;
					}
				};
				
				return task;
			}
		};
	}
	
	/*
	public void setExpressesButtonListener(EventHandler<ActionEvent> handler) {
		expresses.setOnAction(handler);
	}
	*/
	
	public void setColorButtonListener(EventHandler<ActionEvent> handler) {
		color.setOnAction(handler);
	}
	
	public String getName() {
		return nameText.getText();
	}
	
	public void setDisableTerminalCaseFunctions(boolean disabled) {
		expresses.setDisable(disabled);
		wiredTo.setDisable(disabled);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();
		
		expresses.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (expressesMenu==null) {
					expressesMenu = new ContextMenu();
					expressesTitle = new MenuItem("Pick Gene To Color");
					expressesTitle.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							event.consume();
						}
					});
					
					expressesLoading = new MenuItem("Loading...");
					
					expressesMenu.getItems().add(expressesTitle);
					expressesMenu.setAutoHide(true);
					
					expresses.setContextMenu(expressesMenu);
					
					expressesQueryService.setOnScheduled(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							expressesMenu.getItems().add(expressesLoading);
							expressesLoadingService.restart();
						}
					});
					
					expressesQueryService.setOnCancelled(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							expressesLoadingService.cancel();
							expressesMenu.getItems().remove(expressesLoading);
						}
					});
					
					expressesQueryService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							expressesLoadingService.cancel();
							expressesMenu.getItems().remove(expressesLoading);
							ArrayList<String> results = expressesQueryService.getValue();
							if (results!=null) {
								for (String result : results) {
									MenuItem item = new MenuItem(result);
									
									item.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent event) {
											Rule rule = Search.addColorRule(SearchType.GENE, result, 
								                        Color.WHITE, SearchOption.CELL);
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
				expressesMenu.getItems().add(expressesTitle);
				expressesMenu.show(expresses, Side.RIGHT, 0, 0);
				expressesQueryService.restart();
			}
		});
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
	
	private void assertFXMLNodes() {
		assert (nameText!=null);
	}

	private static final long WAIT_TIME_MILLI = 750;
}
