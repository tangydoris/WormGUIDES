package wormguides.view;


import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.*;
import java.lang.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.text.FontBuilder;
import wormguides.RootLayoutController;

import java.util.ResourceBundle;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import javafx.geometry.InsetsBuilder;
//import org.eclipse.fx.core.fxml.FXMLDocument;

@SuppressWarnings("all")
public class RootLayout extends BorderPane {
	private Map<String,Object> namespaceMap = new HashMap<>();
	
	public Object getController() {
		return new RootLayoutController();
	}
	
	public BorderPane load() {
		BorderPane root = new BorderPane();
		root.setPrefHeight(800.0);
		root.setPrefWidth(850.0);
		{
			MenuBar e_1 = new MenuBar();
			e_1.setId("menu");
			e_1.setTranslateZ(100.0);
			{
				Menu e_2 = new Menu();
				e_2.setMnemonicParsing(false);
				e_2.setText("File");
				{
					MenuItem e_3 = new MenuItem();
					e_3.setId("menuClose");
					e_3.setMnemonicParsing(false);
					e_3.setText("Close");
					e_2.getItems().add(e_3);
				}
				e_1.getMenus().add(e_2);
			}
			{
				Menu e_4 = new Menu();
				e_4.setMnemonicParsing(false);
				e_4.setText("Help");
				{
					MenuItem e_5 = new MenuItem();
					e_5.setId("menuAbout");
					e_5.setMnemonicParsing(false);
					e_5.setText("About");
					e_4.getItems().add(e_5);
				}
				e_1.getMenus().add(e_4);
			}
			root.setTop(e_1);
			// an enum type
			BorderPane.setAlignment(e_1,javafx.geometry.Pos.CENTER);
		}
		{
			GridPane e_6 = new GridPane();
			e_6.setId("mainGridPane");
			e_6.setPickOnBounds(false);
			{
				ColumnConstraints e_7 = new ColumnConstraints();
				e_7.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
				e_7.setMaxWidth(256.0);
				e_7.setMinWidth(10.0);
				e_7.setPercentWidth(34.0);
				e_7.setPrefWidth(100.0);
				e_6.getColumnConstraints().add(e_7);
			}
			{
				ColumnConstraints e_8 = new ColumnConstraints();
				e_8.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
				e_8.setMinWidth(10.0);
				e_8.setPercentWidth(66.0);
				e_8.setPrefWidth(100.0);
				e_6.getColumnConstraints().add(e_8);
			}
			{
				RowConstraints e_9 = new RowConstraints();
				e_9.setMinHeight(10.0);
				e_9.setPrefHeight(30.0);
				e_9.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
				e_6.getRowConstraints().add(e_9);
			}
			{
				TabPane e_10 = new TabPane();
				e_10.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
				e_10.setId("dataPanel");
				e_10.setMaxWidth(289.0);
				e_10.setPrefWidth(289.0);
				e_10.setTabMinHeight(23.0);
				e_10.setTabMinWidth(45.0);
				{
					Tab e_11 = new Tab();
					e_11.setText("Data");
					{
						AnchorPane e_12 = new AnchorPane();
						e_12.setPrefHeight(180.0);
						e_12.setPrefWidth(200.0);
						e_11.setContent(e_12);
					}
					e_10.getTabs().add(e_11);
				}
				{
					Tab e_13 = new Tab();
					e_13.setText("Search");
					{
						AnchorPane e_14 = new AnchorPane();
						e_14.setId("searchTabAnchorPane");
						{
							ScrollPane e_15 = new ScrollPane();
							e_15.setLayoutX(1.0);
							e_15.setLayoutY(-18.0);
							e_15.setPrefViewportHeight(719.0);
							e_15.setPrefViewportWidth(269.0);
							{
								VBox e_16 = new VBox();
								e_16.setMaxWidth(270.0);
								e_16.setMinWidth(270.0);
								e_16.setPrefWidth(270.0);
								e_16.setSpacing(3.0);
								{
									Pane e_17 = new Pane();
									e_17.setPrefHeight(80.0);
									e_17.setPrefWidth(269.0);
									e_17.setStyle("-fx-border-color: grey;");
									e_16.getChildren().add(e_17);
								}
								{
									Region e_18 = new Region();
									e_18.setPrefHeight(5.0);
									e_16.getChildren().add(e_18);
								}
								{
									HBox e_19 = new HBox();
									e_19.setMaxHeight(22.0);
									e_19.setMinHeight(22.0);
									e_19.setPrefHeight(22.0);
									{
										Label e_20 = new Label();
										e_20.setPrefHeight(22.0);
										e_20.setText("Search");
										{
											Font e_21;
											FontBuilder e_21Builder = FontBuilder.create();
											e_21Builder.size(14.0);
											e_21 = e_21Builder.build();
											e_20.setFont(e_21);
										}
										e_19.getChildren().add(e_20);
									}
									{
										Region e_22 = new Region();
										e_22.setPrefHeight(22.0);
										e_19.getChildren().add(e_22);
										// an enum type
										HBox.setHgrow(e_22,javafx.scene.layout.Priority.ALWAYS);
									}
									{
										TextField e_23 = new TextField();
										e_23.setId("searchField");
										e_23.setMaxHeight(22.0);
										e_23.setMinHeight(22.0);
										e_23.setPrefHeight(22.0);
										e_23.setPrefWidth(175.0);
										e_19.getChildren().add(e_23);
									}
									e_16.getChildren().add(e_19);
								}
								{
									Region e_24 = new Region();
									e_24.setPrefHeight(5.0);
									e_24.setPrefWidth(250.0);
									e_16.getChildren().add(e_24);
								}
								{
									Label e_25 = new Label();
									e_25.setText("Search Type:");
									e_16.getChildren().add(e_25);
								}
								{
									HBox e_26 = new HBox();
									e_26.setPrefHeight(22.0);
									e_26.setPrefWidth(200.0);
									{
										Region e_27 = new Region();
										e_27.setPrefHeight(22.0);
										e_27.setPrefWidth(40.0);
										e_26.getChildren().add(e_27);
									}
									{
										Label e_28 = new Label();
										e_28.setPrefHeight(22.0);
										e_28.setText("Systematic");
										e_26.getChildren().add(e_28);
									}
									{
										Region e_29 = new Region();
										e_29.setPrefHeight(22.0);
										e_29.setPrefWidth(35.0);
										e_26.getChildren().add(e_29);
									}
									{
										RadioButton e_30 = new RadioButton();
										e_30.setId("");
										e_30.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
										e_30.setMnemonicParsing(false);
										e_30.setPrefHeight(22.0);
										e_30.setPrefWidth(16.0);
										e_26.getChildren().add(e_30);
									}
									e_16.getChildren().add(e_26);
								}
								{
									HBox e_31 = new HBox();
									e_31.setPrefHeight(22.0);
									e_31.setPrefWidth(200.0);
									{
										Region e_32 = new Region();
										e_32.setPrefHeight(22.0);
										e_32.setPrefWidth(40.0);
										e_31.getChildren().add(e_32);
									}
									{
										Label e_33 = new Label();
										e_33.setPrefHeight(22.0);
										e_33.setText("Functional");
										e_31.getChildren().add(e_33);
									}
									{
										Region e_34 = new Region();
										e_34.setPrefHeight(22.0);
										e_34.setPrefWidth(40.0);
										e_31.getChildren().add(e_34);
									}
									{
										RadioButton e_35 = new RadioButton();
										e_35.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
										e_35.setMnemonicParsing(false);
										e_35.setPrefHeight(22.0);
										e_35.setPrefWidth(16.0);
										e_31.getChildren().add(e_35);
									}
									e_16.getChildren().add(e_31);
								}
								{
									HBox e_36 = new HBox();
									e_36.setPrefHeight(22.0);
									e_36.setPrefWidth(200.0);
									{
										Region e_37 = new Region();
										e_37.setPrefHeight(22.0);
										e_37.setPrefWidth(40.0);
										e_36.getChildren().add(e_37);
									}
									{
										Label e_38 = new Label();
										e_38.setPrefHeight(22.0);
										e_38.setText("Description");
										e_36.getChildren().add(e_38);
									}
									{
										Region e_39 = new Region();
										e_39.setPrefHeight(22.0);
										e_39.setPrefWidth(34.0);
										e_36.getChildren().add(e_39);
									}
									{
										RadioButton e_40 = new RadioButton();
										e_40.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
										e_40.setMnemonicParsing(false);
										e_40.setPrefHeight(22.0);
										e_40.setPrefWidth(16.0);
										e_36.getChildren().add(e_40);
									}
									e_16.getChildren().add(e_36);
								}
								{
									HBox e_41 = new HBox();
									e_41.setPrefHeight(22.0);
									e_41.setPrefWidth(200.0);
									{
										Region e_42 = new Region();
										e_42.setPrefHeight(22.0);
										e_42.setPrefWidth(40.0);
										e_41.getChildren().add(e_42);
									}
									{
										Label e_43 = new Label();
										e_43.setPrefHeight(22.0);
										e_43.setText("Gene");
										e_41.getChildren().add(e_43);
									}
									{
										Region e_44 = new Region();
										e_44.setPrefHeight(22.0);
										e_44.setPrefWidth(70.0);
										e_41.getChildren().add(e_44);
									}
									{
										RadioButton e_45 = new RadioButton();
										e_45.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
										e_45.setMnemonicParsing(false);
										e_45.setPrefHeight(22.0);
										e_45.setPrefWidth(16.0);
										e_41.getChildren().add(e_45);
									}
									e_16.getChildren().add(e_41);
								}
								{
									Region e_46 = new Region();
									e_46.setPrefHeight(15.0);
									e_16.getChildren().add(e_46);
								}
								{
									Label e_47 = new Label();
									e_47.setText("Search Options:");
									e_16.getChildren().add(e_47);
								}
								{
									Label e_48 = new Label();
									e_48.setText("     Cell Ancestry");
									e_16.getChildren().add(e_48);
								}
								{
									HBox e_49 = new HBox();
									e_49.setPrefHeight(22.0);
									e_49.setPrefWidth(200.0);
									{
										Label e_50 = new Label();
										e_50.setPrefHeight(22.0);
										e_50.setText("          Cell");
										e_49.getChildren().add(e_50);
									}
									{
										Region e_51 = new Region();
										e_51.setPrefHeight(22.0);
										e_51.setPrefWidth(82.0);
										e_49.getChildren().add(e_51);
									}
									{
										CheckBox e_52 = new CheckBox();
										e_52.setMnemonicParsing(false);
										e_52.setPrefHeight(22.0);
										e_49.getChildren().add(e_52);
									}
									e_16.getChildren().add(e_49);
								}
								{
									HBox e_53 = new HBox();
									e_53.setPrefHeight(22.0);
									e_53.setPrefWidth(200.0);
									{
										Label e_54 = new Label();
										e_54.setPrefHeight(22.0);
										e_54.setText("          Ancestor");
										e_53.getChildren().add(e_54);
									}
									{
										Region e_55 = new Region();
										e_55.setPrefHeight(22.0);
										e_55.setPrefWidth(52.0);
										e_53.getChildren().add(e_55);
									}
									{
										CheckBox e_56 = new CheckBox();
										e_56.setMnemonicParsing(false);
										e_56.setPrefHeight(22.0);
										e_53.getChildren().add(e_56);
									}
									e_16.getChildren().add(e_53);
								}
								{
									HBox e_57 = new HBox();
									e_57.setPrefHeight(22.0);
									e_57.setPrefWidth(200.0);
									{
										Label e_58 = new Label();
										e_58.setPrefHeight(22.0);
										e_58.setText("          Descendant");
										e_57.getChildren().add(e_58);
									}
									{
										Region e_59 = new Region();
										e_59.setPrefHeight(22.0);
										e_59.setPrefWidth(34.0);
										e_57.getChildren().add(e_59);
									}
									{
										CheckBox e_60 = new CheckBox();
										e_60.setMnemonicParsing(false);
										e_60.setPrefHeight(22.0);
										e_57.getChildren().add(e_60);
									}
									e_16.getChildren().add(e_57);
								}
								{
									Label e_61 = new Label();
									e_61.setText("     Interactors");
									e_16.getChildren().add(e_61);
								}
								{
									HBox e_62 = new HBox();
									e_62.setPrefHeight(22.0);
									e_62.setPrefWidth(200.0);
									{
										Label e_63 = new Label();
										e_63.setPrefHeight(22.0);
										e_63.setText("          Wired to");
										e_62.getChildren().add(e_63);
									}
									{
										Region e_64 = new Region();
										e_64.setPrefHeight(22.0);
										e_64.setPrefWidth(77.0);
										e_62.getChildren().add(e_64);
									}
									{
										CheckBox e_65 = new CheckBox();
										e_65.setMnemonicParsing(false);
										e_65.setPrefHeight(22.0);
										e_62.getChildren().add(e_65);
									}
									e_16.getChildren().add(e_62);
								}
								{
									HBox e_66 = new HBox();
									e_66.setPrefHeight(22.0);
									e_66.setPrefWidth(200.0);
									{
										Label e_67 = new Label();
										e_67.setPrefHeight(22.0);
										e_67.setText("          L/R homologues");
										e_66.getChildren().add(e_67);
									}
									{
										Region e_68 = new Region();
										e_68.setPrefHeight(22.0);
										e_68.setPrefWidth(29.0);
										e_66.getChildren().add(e_68);
									}
									{
										CheckBox e_69 = new CheckBox();
										e_69.setMnemonicParsing(false);
										e_69.setPrefHeight(22.0);
										e_66.getChildren().add(e_69);
									}
									e_16.getChildren().add(e_66);
								}
								{
									Label e_70 = new Label();
									e_70.setText("     Geometry");
									e_16.getChildren().add(e_70);
								}
								{
									HBox e_71 = new HBox();
									e_71.setPrefHeight(22.0);
									e_71.setPrefWidth(200.0);
									{
										Label e_72 = new Label();
										e_72.setPrefHeight(22.0);
										e_72.setText("          Nucleus");
										e_71.getChildren().add(e_72);
									}
									{
										Region e_73 = new Region();
										e_73.setPrefHeight(22.0);
										e_73.setPrefWidth(57.0);
										e_71.getChildren().add(e_73);
									}
									{
										CheckBox e_74 = new CheckBox();
										e_74.setMnemonicParsing(false);
										e_74.setPrefHeight(22.0);
										e_71.getChildren().add(e_74);
									}
									e_16.getChildren().add(e_71);
								}
								{
									HBox e_75 = new HBox();
									e_75.setPrefHeight(22.0);
									e_75.setPrefWidth(200.0);
									{
										Label e_76 = new Label();
										e_76.setPrefHeight(22.0);
										e_76.setText("          Cell shape");
										e_75.getChildren().add(e_76);
									}
									{
										Region e_77 = new Region();
										e_77.setPrefHeight(22.0);
										e_77.setPrefWidth(43.0);
										e_75.getChildren().add(e_77);
									}
									{
										CheckBox e_78 = new CheckBox();
										e_78.setMnemonicParsing(false);
										e_78.setPrefHeight(22.0);
										e_75.getChildren().add(e_78);
									}
									e_16.getChildren().add(e_75);
								}
								{
									Region e_79 = new Region();
									e_79.setPrefHeight(15.0);
									e_16.getChildren().add(e_79);
								}
								{
									HBox e_80 = new HBox();
									e_80.setMaxHeight(22.0);
									e_80.setMinHeight(22.0);
									e_80.setPrefHeight(22.0);
									{
										Label e_81 = new Label();
										e_81.setPrefHeight(22.0);
										e_81.setText("Search Results:");
										{
											Font e_82;
											FontBuilder e_82Builder = FontBuilder.create();
											e_82Builder.size(14.0);
											e_82 = e_82Builder.build();
											e_81.setFont(e_82);
										}
										e_80.getChildren().add(e_81);
									}
									{
										Region e_83 = new Region();
										e_83.setPrefHeight(22.0);
										e_80.getChildren().add(e_83);
										// an enum type
										HBox.setHgrow(e_83,javafx.scene.layout.Priority.ALWAYS);
									}
									{
										Button e_84 = new Button();
										e_84.setMnemonicParsing(false);
										e_84.setPrefHeight(26.0);
										e_84.setPrefWidth(100.0);
										e_84.setText("Add");
										e_80.getChildren().add(e_84);
									}
									e_16.getChildren().add(e_80);
								}
								{
									Region e_85 = new Region();
									e_85.setPrefHeight(3.0);
									e_85.setPrefWidth(200.0);
									e_16.getChildren().add(e_85);
								}
								{
									AnchorPane e_86 = new AnchorPane();
									e_86.setId("searchResultsPane");
									e_86.setPrefHeight(140.0);
									e_86.setPrefWidth(250.0);
									e_86.setStyle("-fx-border-color: grey;");
									{
										ListView e_87 = new ListView();
										e_87.setId("searchResultsList");
										e_87.setLayoutX(-12.0);
										e_87.setLayoutY(-22.0);
										e_86.getChildren().add(e_87);
										AnchorPane.setBottomAnchor(e_87,0.0);
										AnchorPane.setLeftAnchor(e_87,0.0);
										AnchorPane.setRightAnchor(e_87,0.0);
										AnchorPane.setTopAnchor(e_87,0.0);
									}
									e_16.getChildren().add(e_86);
								}
								{
									Insets e_88;
									InsetsBuilder e_88Builder = InsetsBuilder.create();
									e_88 = e_88Builder.build();
									e_16.setOpaqueInsets(e_88);
								}
								{
									Insets e_89;
									InsetsBuilder e_89Builder = InsetsBuilder.create();
									e_89Builder.bottom(10.0);
									e_89Builder.left(15.0);
									e_89Builder.right(10.0);
									e_89Builder.top(10.0);
									e_89 = e_89Builder.build();
									e_16.setPadding(e_89);
								}
								e_15.setContent(e_16);
								AnchorPane.setBottomAnchor(e_16,10.0);
								AnchorPane.setLeftAnchor(e_16,10.0);
								AnchorPane.setRightAnchor(e_16,10.0);
								AnchorPane.setTopAnchor(e_16,10.0);
							}
							e_14.getChildren().add(e_15);
							AnchorPane.setBottomAnchor(e_15,0.0);
							AnchorPane.setLeftAnchor(e_15,0.0);
							AnchorPane.setRightAnchor(e_15,0.0);
							AnchorPane.setTopAnchor(e_15,0.0);
						}
						e_13.setContent(e_14);
					}
					e_10.getTabs().add(e_13);
				}
				{
					Tab e_90 = new Tab();
					e_90.setText("Layers");
					{
						AnchorPane e_91 = new AnchorPane();
						e_91.setMinHeight(0.0);
						e_91.setMinWidth(0.0);
						e_91.setPrefHeight(180.0);
						e_91.setPrefWidth(200.0);
						{
							VBox e_92 = new VBox();
							e_92.setSpacing(5.0);
							{
								HBox e_93 = new HBox();
								e_93.setPrefHeight(10.0);
								e_93.setPrefWidth(220.0);
								{
									Label e_94 = new Label();
									e_94.setPrefHeight(22.0);
									e_94.setText("Search Layers:");
									{
										Font e_95;
										FontBuilder e_95Builder = FontBuilder.create();
										e_95Builder.size(14.0);
										e_95 = e_95Builder.build();
										e_94.setFont(e_95);
									}
									e_93.getChildren().add(e_94);
								}
								e_92.getChildren().add(e_93);
							}
							{
								HBox e_96 = new HBox();
								e_96.setSpacing(2.0);
								{
									Label e_97 = new Label();
									e_97.setLayoutX(10.0);
									e_97.setLayoutY(10.0);
									e_97.setPrefHeight(22.0);
									e_97.setText("ABa cell, ancestors");
									{
										Font e_98;
										FontBuilder e_98Builder = FontBuilder.create();
										e_98Builder.size(14.0);
										e_98 = e_98Builder.build();
										e_97.setFont(e_98);
									}
									e_96.getChildren().add(e_97);
								}
								{
									Region e_99 = new Region();
									e_99.setPrefHeight(22.0);
									e_96.getChildren().add(e_99);
									// an enum type
									HBox.setHgrow(e_99,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_100 = new Button();
									e_100.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_100.setMaxHeight(22.0);
									e_100.setMaxWidth(22.0);
									e_100.setMinHeight(22.0);
									e_100.setMinWidth(22.0);
									e_100.setMnemonicParsing(false);
									e_100.setPrefHeight(22.0);
									e_100.setPrefWidth(22.0);
									e_100.setStyle("-fx-background-color: red; -fx-border-radius: 3;");
									e_96.getChildren().add(e_100);
								}
								{
									Button editAbaButton = new Button();
									editAbaButton.setId("editAbaButton");
									editAbaButton.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									editAbaButton.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									editAbaButton.setMaxHeight(22.0);
									editAbaButton.setMaxWidth(22.0);
									editAbaButton.setMinHeight(22.0);
									editAbaButton.setMinWidth(22.0);
									editAbaButton.setMnemonicParsing(false);
									editAbaButton.setPrefHeight(22.0);
									editAbaButton.setPrefWidth(22.0);
									editAbaButton.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
									editAbaButton.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
									{
										Insets e_102;
										InsetsBuilder e_102Builder = InsetsBuilder.create();
										e_102Builder.bottom(1.0);
										e_102Builder.left(1.0);
										e_102Builder.right(1.0);
										e_102Builder.top(1.0);
										e_102 = e_102Builder.build();
										editAbaButton.setPadding(e_102);
									}
									e_96.getChildren().add(editAbaButton);
								}
								{
									Button abaEyeButton = new Button();
									abaEyeButton.setId("abaEyeButton");
									abaEyeButton.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									abaEyeButton.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									abaEyeButton.setMaxHeight(22.0);
									abaEyeButton.setMaxWidth(22.0);
									abaEyeButton.setMinHeight(22.0);
									abaEyeButton.setMinWidth(22.0);
									abaEyeButton.setMnemonicParsing(false);
									abaEyeButton.setPrefHeight(22.0);
									abaEyeButton.setPrefWidth(22.0);
									abaEyeButton.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
									{
										Insets e_104;
										InsetsBuilder e_104Builder = InsetsBuilder.create();
										e_104Builder.bottom(1.0);
										e_104Builder.left(1.0);
										e_104Builder.right(1.0);
										e_104Builder.top(1.0);
										e_104 = e_104Builder.build();
										abaEyeButton.setPadding(e_104);
									}
									e_96.getChildren().add(abaEyeButton);
								}
								{
									Button abaCloseButton = new Button();
									abaCloseButton.setId("abaCloseButton");
									abaCloseButton.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									abaCloseButton.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
									abaCloseButton.setMaxHeight(22.0);
									abaCloseButton.setMaxWidth(22.0);
									abaCloseButton.setMinHeight(22.0);
									abaCloseButton.setMinWidth(22.0);
									abaCloseButton.setMnemonicParsing(false);
									abaCloseButton.setPrefHeight(22.0);
									abaCloseButton.setPrefWidth(22.0);
									abaCloseButton.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
									{
										Insets e_106;
										InsetsBuilder e_106Builder = InsetsBuilder.create();
										e_106Builder.bottom(1.0);
										e_106Builder.left(1.0);
										e_106Builder.right(1.0);
										e_106Builder.top(1.0);
										e_106 = e_106Builder.build();
										abaCloseButton.setPadding(e_106);
									}
									e_96.getChildren().add(abaCloseButton);
								}
								e_92.getChildren().add(e_96);
							}
							{
								HBox e_107 = new HBox();
								e_107.setSpacing(2.0);
								{
									Label e_108 = new Label();
									e_108.setPrefHeight(22.0);
									e_108.setText("ABp");
									{
										Font e_109;
										FontBuilder e_109Builder = FontBuilder.create();
										e_109Builder.size(14.0);
										e_109 = e_109Builder.build();
										e_108.setFont(e_109);
									}
									e_107.getChildren().add(e_108);
								}
								{
									Region e_110 = new Region();
									e_110.setPrefHeight(22.0);
									e_107.getChildren().add(e_110);
									// an enum type
									HBox.setHgrow(e_110,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_111 = new Button();
									e_111.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_111.setMaxHeight(22.0);
									e_111.setMaxWidth(22.0);
									e_111.setMinHeight(22.0);
									e_111.setMinWidth(22.0);
									e_111.setMnemonicParsing(false);
									e_111.setPrefHeight(22.0);
									e_111.setPrefWidth(22.0);
									e_111.setStyle("-fx-background-color: red; -fx-border-radius: 3;");
									e_107.getChildren().add(e_111);
								}
								{
									Button editAbpButton = new Button();
									editAbpButton.setId("editAbpButton");
									editAbpButton.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									editAbpButton.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									editAbpButton.setMaxHeight(22.0);
									editAbpButton.setMaxWidth(22.0);
									editAbpButton.setMinHeight(22.0);
									editAbpButton.setMinWidth(22.0);
									editAbpButton.setMnemonicParsing(false);
									editAbpButton.setPrefHeight(22.0);
									editAbpButton.setPrefWidth(22.0);
									editAbpButton.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
									editAbpButton.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
									{
										Insets e_113;
										InsetsBuilder e_113Builder = InsetsBuilder.create();
										e_113Builder.bottom(1.0);
										e_113Builder.left(1.0);
										e_113Builder.right(1.0);
										e_113Builder.top(1.0);
										e_113 = e_113Builder.build();
										editAbpButton.setPadding(e_113);
									}
									e_107.getChildren().add(editAbpButton);
								}
								{
									Button e_114 = new Button();
									e_114.setId("abpEyeButton");
									e_114.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_114.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_114.setMaxHeight(22.0);
									e_114.setMaxWidth(22.0);
									e_114.setMinHeight(22.0);
									e_114.setMinWidth(22.0);
									e_114.setMnemonicParsing(false);
									e_114.setPrefHeight(22.0);
									e_114.setPrefWidth(22.0);
									{
										Insets e_115;
										InsetsBuilder e_115Builder = InsetsBuilder.create();
										e_115Builder.bottom(1.0);
										e_115Builder.left(1.0);
										e_115Builder.right(1.0);
										e_115Builder.top(1.0);
										e_115 = e_115Builder.build();
										e_114.setPadding(e_115);
									}
									e_107.getChildren().add(e_114);
								}
								{
									Button e_116 = new Button();
									e_116.setId("abpCloseButton");
									e_116.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_116.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
									e_116.setMaxHeight(22.0);
									e_116.setMaxWidth(22.0);
									e_116.setMinHeight(22.0);
									e_116.setMinWidth(22.0);
									e_116.setMnemonicParsing(false);
									e_116.setPrefHeight(22.0);
									e_116.setPrefWidth(22.0);
									{
										Insets e_117;
										InsetsBuilder e_117Builder = InsetsBuilder.create();
										e_117Builder.bottom(1.0);
										e_117Builder.left(1.0);
										e_117Builder.right(1.0);
										e_117Builder.top(1.0);
										e_117 = e_117Builder.build();
										e_116.setPadding(e_117);
									}
									e_107.getChildren().add(e_116);
								}
								e_92.getChildren().add(e_107);
							}
							{
								HBox e_118 = new HBox();
								e_118.setSpacing(2.0);
								{
									Label e_119 = new Label();
									e_119.setPrefHeight(22.0);
									e_119.setText("EMS");
									{
										Font e_120;
										FontBuilder e_120Builder = FontBuilder.create();
										e_120Builder.size(14.0);
										e_120 = e_120Builder.build();
										e_119.setFont(e_120);
									}
									e_118.getChildren().add(e_119);
								}
								{
									Region e_121 = new Region();
									e_121.setPrefHeight(22.0);
									e_118.getChildren().add(e_121);
									// an enum type
									HBox.setHgrow(e_121,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_122 = new Button();
									e_122.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_122.setMaxHeight(22.0);
									e_122.setMaxWidth(22.0);
									e_122.setMinHeight(22.0);
									e_122.setMinWidth(22.0);
									e_122.setMnemonicParsing(false);
									e_122.setPrefHeight(22.0);
									e_122.setPrefWidth(22.0);
									e_122.setStyle("-fx-background-color: red; -fx-border-radius: 3;");
									e_118.getChildren().add(e_122);
								}
								{
									Button e_123 = new Button();
									e_123.setId("editEmsButton");
									e_123.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_123.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_123.setMaxHeight(22.0);
									e_123.setMaxWidth(22.0);
									e_123.setMinHeight(22.0);
									e_123.setMinWidth(22.0);
									e_123.setMnemonicParsing(false);
									e_123.setPrefHeight(22.0);
									e_123.setPrefWidth(22.0);
									e_123.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
									e_123.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
									{
										Insets e_124;
										InsetsBuilder e_124Builder = InsetsBuilder.create();
										e_124Builder.bottom(1.0);
										e_124Builder.left(1.0);
										e_124Builder.right(1.0);
										e_124Builder.top(1.0);
										e_124 = e_124Builder.build();
										e_123.setPadding(e_124);
									}
									e_118.getChildren().add(e_123);
								}
								{
									Button e_125 = new Button();
									e_125.setId("emsEyeButton");
									e_125.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_125.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_125.setMaxHeight(22.0);
									e_125.setMaxWidth(22.0);
									e_125.setMinHeight(22.0);
									e_125.setMinWidth(22.0);
									e_125.setMnemonicParsing(false);
									e_125.setPrefHeight(22.0);
									e_125.setPrefWidth(22.0);
									{
										Insets e_126;
										InsetsBuilder e_126Builder = InsetsBuilder.create();
										e_126Builder.bottom(1.0);
										e_126Builder.left(1.0);
										e_126Builder.right(1.0);
										e_126Builder.top(1.0);
										e_126 = e_126Builder.build();
										e_125.setPadding(e_126);
									}
									e_118.getChildren().add(e_125);
								}
								{
									Button e_127 = new Button();
									e_127.setId("emsCloseButton");
									e_127.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_127.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
									e_127.setMaxHeight(22.0);
									e_127.setMaxWidth(22.0);
									e_127.setMinHeight(22.0);
									e_127.setMinWidth(22.0);
									e_127.setMnemonicParsing(false);
									e_127.setPrefHeight(22.0);
									e_127.setPrefWidth(22.0);
									{
										Insets e_128;
										InsetsBuilder e_128Builder = InsetsBuilder.create();
										e_128Builder.bottom(1.0);
										e_128Builder.left(1.0);
										e_128Builder.right(1.0);
										e_128Builder.top(1.0);
										e_128 = e_128Builder.build();
										e_127.setPadding(e_128);
									}
									e_118.getChildren().add(e_127);
								}
								e_92.getChildren().add(e_118);
							}
							{
								Region e_129 = new Region();
								e_129.setPrefHeight(22.0);
								e_129.setPrefWidth(242.0);
								e_92.getChildren().add(e_129);
							}
							{
								Label e_130 = new Label();
								e_130.setPrefHeight(22.0);
								e_130.setText("Cell Shapes Available:");
								{
									Font e_131;
									FontBuilder e_131Builder = FontBuilder.create();
									e_131Builder.size(14.0);
									e_131 = e_131Builder.build();
									e_130.setFont(e_131);
								}
								e_92.getChildren().add(e_130);
							}
							{
								HBox e_132 = new HBox();
								e_132.setSpacing(2.0);
								{
									Label e_133 = new Label();
									e_133.setLayoutX(10.0);
									e_133.setLayoutY(10.0);
									e_133.setPrefHeight(22.0);
									e_133.setText("-VNC");
									{
										Font e_134;
										FontBuilder e_134Builder = FontBuilder.create();
										e_134Builder.size(14.0);
										e_134 = e_134Builder.build();
										e_133.setFont(e_134);
									}
									e_132.getChildren().add(e_133);
								}
								{
									Region e_135 = new Region();
									e_135.setPrefHeight(22.0);
									e_132.getChildren().add(e_135);
									// an enum type
									HBox.setHgrow(e_135,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_136 = new Button();
									e_136.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_136.setDisable(true);
									e_136.setMaxHeight(22.0);
									e_136.setMaxWidth(22.0);
									e_136.setMinHeight(22.0);
									e_136.setMinWidth(22.0);
									e_136.setMnemonicParsing(false);
									e_136.setPrefHeight(22.0);
									e_136.setPrefWidth(22.0);
									e_136.setStyle("-fx-background-color: transparent;");
									e_136.setVisible(false);
									e_132.getChildren().add(e_136);
								}
								{
									Button e_137 = new Button();
									e_137.setId("editAbaIcon1");
									e_137.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_137.setDisable(true);
									e_137.setMaxHeight(22.0);
									e_137.setMaxWidth(22.0);
									e_137.setMinHeight(22.0);
									e_137.setMinWidth(22.0);
									e_137.setMnemonicParsing(false);
									e_137.setPrefHeight(22.0);
									e_137.setPrefWidth(22.0);
									e_137.setStyle("-fx-background-color: transparent;");
									e_137.setVisible(false);
									e_132.getChildren().add(e_137);
								}
								{
									Button e_138 = new Button();
									e_138.setId("vncEyeButton");
									e_138.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_138.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_138.setMaxHeight(22.0);
									e_138.setMaxWidth(22.0);
									e_138.setMinHeight(22.0);
									e_138.setMinWidth(22.0);
									e_138.setMnemonicParsing(false);
									e_138.setPrefHeight(22.0);
									e_138.setPrefWidth(22.0);
									{
										Insets e_139;
										InsetsBuilder e_139Builder = InsetsBuilder.create();
										e_139Builder.bottom(1.0);
										e_139Builder.left(1.0);
										e_139Builder.right(1.0);
										e_139Builder.top(1.0);
										e_139 = e_139Builder.build();
										e_138.setPadding(e_139);
									}
									e_132.getChildren().add(e_138);
								}
								{
									Button e_140 = new Button();
									e_140.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_140.setDisable(true);
									e_140.setMaxHeight(22.0);
									e_140.setMaxWidth(22.0);
									e_140.setMinHeight(22.0);
									e_140.setMinWidth(22.0);
									e_140.setMnemonicParsing(false);
									e_140.setPrefHeight(22.0);
									e_140.setPrefWidth(22.0);
									e_140.setVisible(false);
									e_132.getChildren().add(e_140);
								}
								e_92.getChildren().add(e_132);
							}
							{
								HBox e_141 = new HBox();
								e_141.setSpacing(2.0);
								{
									Label e_142 = new Label();
									e_142.setLayoutX(10.0);
									e_142.setLayoutY(10.0);
									e_142.setPrefHeight(22.0);
									e_142.setText("     DD1...");
									{
										Font e_143;
										FontBuilder e_143Builder = FontBuilder.create();
										e_143Builder.size(14.0);
										e_143 = e_143Builder.build();
										e_142.setFont(e_143);
									}
									e_141.getChildren().add(e_142);
								}
								{
									Region e_144 = new Region();
									e_144.setPrefHeight(22.0);
									e_141.getChildren().add(e_144);
									// an enum type
									HBox.setHgrow(e_144,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_145 = new Button();
									e_145.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_145.setDisable(true);
									e_145.setMaxHeight(22.0);
									e_145.setMaxWidth(22.0);
									e_145.setMinHeight(22.0);
									e_145.setMinWidth(22.0);
									e_145.setMnemonicParsing(false);
									e_145.setPrefHeight(22.0);
									e_145.setPrefWidth(22.0);
									e_145.setStyle("-fx-background-color: transparent;");
									e_145.setVisible(false);
									e_141.getChildren().add(e_145);
								}
								{
									Button e_146 = new Button();
									e_146.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_146.setDisable(true);
									e_146.setMaxHeight(22.0);
									e_146.setMaxWidth(22.0);
									e_146.setMinHeight(22.0);
									e_146.setMinWidth(22.0);
									e_146.setMnemonicParsing(false);
									e_146.setPrefHeight(22.0);
									e_146.setPrefWidth(22.0);
									e_146.setVisible(false);
									e_141.getChildren().add(e_146);
								}
								{
									Button e_147 = new Button();
									e_147.setId("dd1EyeButton");
									e_147.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_147.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_147.setMaxHeight(22.0);
									e_147.setMaxWidth(22.0);
									e_147.setMinHeight(22.0);
									e_147.setMinWidth(22.0);
									e_147.setMnemonicParsing(false);
									e_147.setPrefHeight(22.0);
									e_147.setPrefWidth(22.0);
									{
										Insets e_148;
										InsetsBuilder e_148Builder = InsetsBuilder.create();
										e_148Builder.bottom(1.0);
										e_148Builder.left(1.0);
										e_148Builder.right(1.0);
										e_148Builder.top(1.0);
										e_148 = e_148Builder.build();
										e_147.setPadding(e_148);
									}
									e_141.getChildren().add(e_147);
								}
								{
									Button e_149 = new Button();
									e_149.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_149.setDisable(true);
									e_149.setMaxHeight(22.0);
									e_149.setMaxWidth(22.0);
									e_149.setMinHeight(22.0);
									e_149.setMinWidth(22.0);
									e_149.setMnemonicParsing(false);
									e_149.setPrefHeight(22.0);
									e_149.setPrefWidth(22.0);
									e_149.setVisible(false);
									e_141.getChildren().add(e_149);
								}
								e_92.getChildren().add(e_141);
							}
							{
								HBox e_150 = new HBox();
								e_150.setSpacing(2.0);
								{
									Label e_151 = new Label();
									e_151.setLayoutX(10.0);
									e_151.setLayoutY(10.0);
									e_151.setPrefHeight(22.0);
									e_151.setText("+Nerve Ring");
									{
										Font e_152;
										FontBuilder e_152Builder = FontBuilder.create();
										e_152Builder.size(14.0);
										e_152 = e_152Builder.build();
										e_151.setFont(e_152);
									}
									e_150.getChildren().add(e_151);
								}
								{
									Region e_153 = new Region();
									e_153.setPrefHeight(22.0);
									e_150.getChildren().add(e_153);
									// an enum type
									HBox.setHgrow(e_153,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_154 = new Button();
									e_154.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_154.setDisable(true);
									e_154.setMaxHeight(22.0);
									e_154.setMaxWidth(22.0);
									e_154.setMinHeight(22.0);
									e_154.setMinWidth(22.0);
									e_154.setMnemonicParsing(false);
									e_154.setPrefHeight(22.0);
									e_154.setPrefWidth(22.0);
									e_154.setStyle("-fx-background-color: transparent;");
									e_154.setVisible(false);
									e_150.getChildren().add(e_154);
								}
								{
									Button e_155 = new Button();
									e_155.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_155.setDisable(true);
									e_155.setMaxHeight(22.0);
									e_155.setMaxWidth(22.0);
									e_155.setMinHeight(22.0);
									e_155.setMinWidth(22.0);
									e_155.setMnemonicParsing(false);
									e_155.setPrefHeight(22.0);
									e_155.setPrefWidth(22.0);
									e_155.setVisible(false);
									e_150.getChildren().add(e_155);
								}
								{
									Button e_156 = new Button();
									e_156.setId("nerveRingEyeButton");
									e_156.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_156.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_156.setMaxHeight(22.0);
									e_156.setMaxWidth(22.0);
									e_156.setMinHeight(22.0);
									e_156.setMinWidth(22.0);
									e_156.setMnemonicParsing(false);
									e_156.setPrefHeight(22.0);
									e_156.setPrefWidth(22.0);
									{
										Insets e_157;
										InsetsBuilder e_157Builder = InsetsBuilder.create();
										e_157Builder.bottom(1.0);
										e_157Builder.left(1.0);
										e_157Builder.right(1.0);
										e_157Builder.top(1.0);
										e_157 = e_157Builder.build();
										e_156.setPadding(e_157);
									}
									e_150.getChildren().add(e_156);
								}
								{
									Button e_158 = new Button();
									e_158.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_158.setDisable(true);
									e_158.setMaxHeight(22.0);
									e_158.setMaxWidth(22.0);
									e_158.setMinHeight(22.0);
									e_158.setMinWidth(22.0);
									e_158.setMnemonicParsing(false);
									e_158.setPrefHeight(22.0);
									e_158.setPrefWidth(22.0);
									e_158.setVisible(false);
									e_150.getChildren().add(e_158);
								}
								e_92.getChildren().add(e_150);
							}
							{
								Region e_159 = new Region();
								e_159.setPrefHeight(22.0);
								e_159.setPrefWidth(242.0);
								e_92.getChildren().add(e_159);
							}
							{
								Label e_160 = new Label();
								e_160.setPrefHeight(22.0);
								e_160.setText("Tissue Type Layers:");
								{
									Font e_161;
									FontBuilder e_161Builder = FontBuilder.create();
									e_161Builder.size(14.0);
									e_161 = e_161Builder.build();
									e_160.setFont(e_161);
								}
								e_92.getChildren().add(e_160);
							}
							{
								HBox e_162 = new HBox();
								e_162.setSpacing(2.0);
								{
									Label e_163 = new Label();
									e_163.setLayoutX(10.0);
									e_163.setLayoutY(10.0);
									e_163.setPrefHeight(22.0);
									e_163.setText("-Muscle");
									{
										Font e_164;
										FontBuilder e_164Builder = FontBuilder.create();
										e_164Builder.size(14.0);
										e_164 = e_164Builder.build();
										e_163.setFont(e_164);
									}
									e_162.getChildren().add(e_163);
								}
								{
									Region e_165 = new Region();
									e_165.setPrefHeight(22.0);
									e_162.getChildren().add(e_165);
									// an enum type
									HBox.setHgrow(e_165,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_166 = new Button();
									e_166.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_166.setDisable(true);
									e_166.setMaxHeight(22.0);
									e_166.setMaxWidth(22.0);
									e_166.setMinHeight(22.0);
									e_166.setMinWidth(22.0);
									e_166.setMnemonicParsing(false);
									e_166.setPrefHeight(22.0);
									e_166.setPrefWidth(22.0);
									e_166.setStyle("-fx-background-color: transparent;");
									e_166.setVisible(false);
									e_162.getChildren().add(e_166);
								}
								{
									Button e_167 = new Button();
									e_167.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_167.setDisable(true);
									e_167.setMaxHeight(22.0);
									e_167.setMaxWidth(22.0);
									e_167.setMinHeight(22.0);
									e_167.setMinWidth(22.0);
									e_167.setMnemonicParsing(false);
									e_167.setPrefHeight(22.0);
									e_167.setPrefWidth(22.0);
									e_167.setVisible(false);
									e_162.getChildren().add(e_167);
								}
								{
									Button e_168 = new Button();
									e_168.setId("musEyeButton");
									e_168.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_168.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_168.setMaxHeight(22.0);
									e_168.setMaxWidth(22.0);
									e_168.setMinHeight(22.0);
									e_168.setMinWidth(22.0);
									e_168.setMnemonicParsing(false);
									e_168.setPrefHeight(22.0);
									e_168.setPrefWidth(22.0);
									{
										Insets e_169;
										InsetsBuilder e_169Builder = InsetsBuilder.create();
										e_169Builder.bottom(1.0);
										e_169Builder.left(1.0);
										e_169Builder.right(1.0);
										e_169Builder.top(1.0);
										e_169 = e_169Builder.build();
										e_168.setPadding(e_169);
									}
									e_162.getChildren().add(e_168);
								}
								{
									Button e_170 = new Button();
									e_170.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_170.setDisable(true);
									e_170.setMaxHeight(22.0);
									e_170.setMaxWidth(22.0);
									e_170.setMinHeight(22.0);
									e_170.setMinWidth(22.0);
									e_170.setMnemonicParsing(false);
									e_170.setPrefHeight(22.0);
									e_170.setPrefWidth(22.0);
									e_170.setVisible(false);
									e_162.getChildren().add(e_170);
								}
								e_92.getChildren().add(e_162);
							}
							{
								HBox e_171 = new HBox();
								e_171.setSpacing(2.0);
								{
									Label e_172 = new Label();
									e_172.setLayoutX(10.0);
									e_172.setLayoutY(10.0);
									e_172.setPrefHeight(22.0);
									e_172.setText("     Body Wall");
									{
										Font e_173;
										FontBuilder e_173Builder = FontBuilder.create();
										e_173Builder.size(14.0);
										e_173 = e_173Builder.build();
										e_172.setFont(e_173);
									}
									e_171.getChildren().add(e_172);
								}
								{
									Region e_174 = new Region();
									e_174.setPrefHeight(22.0);
									e_171.getChildren().add(e_174);
									// an enum type
									HBox.setHgrow(e_174,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_175 = new Button();
									e_175.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_175.setDisable(true);
									e_175.setMaxHeight(22.0);
									e_175.setMaxWidth(22.0);
									e_175.setMinHeight(22.0);
									e_175.setMinWidth(22.0);
									e_175.setMnemonicParsing(false);
									e_175.setPrefHeight(22.0);
									e_175.setPrefWidth(22.0);
									e_175.setStyle("-fx-background-color: transparent;");
									e_175.setVisible(false);
									e_171.getChildren().add(e_175);
								}
								{
									Button e_176 = new Button();
									e_176.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_176.setDisable(true);
									e_176.setMaxHeight(22.0);
									e_176.setMaxWidth(22.0);
									e_176.setMinHeight(22.0);
									e_176.setMinWidth(22.0);
									e_176.setMnemonicParsing(false);
									e_176.setPrefHeight(22.0);
									e_176.setPrefWidth(22.0);
									e_176.setVisible(false);
									e_171.getChildren().add(e_176);
								}
								{
									Button e_177 = new Button();
									e_177.setId("bodEyeButton");
									e_177.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_177.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_177.setMaxHeight(22.0);
									e_177.setMaxWidth(22.0);
									e_177.setMinHeight(22.0);
									e_177.setMinWidth(22.0);
									e_177.setMnemonicParsing(false);
									e_177.setPrefHeight(22.0);
									e_177.setPrefWidth(22.0);
									{
										Insets e_178;
										InsetsBuilder e_178Builder = InsetsBuilder.create();
										e_178Builder.bottom(1.0);
										e_178Builder.left(1.0);
										e_178Builder.right(1.0);
										e_178Builder.top(1.0);
										e_178 = e_178Builder.build();
										e_177.setPadding(e_178);
									}
									e_171.getChildren().add(e_177);
								}
								{
									Button e_179 = new Button();
									e_179.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_179.setDisable(true);
									e_179.setMaxHeight(22.0);
									e_179.setMaxWidth(22.0);
									e_179.setMinHeight(22.0);
									e_179.setMinWidth(22.0);
									e_179.setMnemonicParsing(false);
									e_179.setPrefHeight(22.0);
									e_179.setPrefWidth(22.0);
									e_179.setVisible(false);
									e_171.getChildren().add(e_179);
								}
								e_92.getChildren().add(e_171);
							}
							{
								HBox e_180 = new HBox();
								e_180.setSpacing(2.0);
								{
									Label e_181 = new Label();
									e_181.setLayoutX(10.0);
									e_181.setLayoutY(10.0);
									e_181.setPrefHeight(22.0);
									e_181.setText("     Pharyngeal");
									{
										Font e_182;
										FontBuilder e_182Builder = FontBuilder.create();
										e_182Builder.size(14.0);
										e_182 = e_182Builder.build();
										e_181.setFont(e_182);
									}
									e_180.getChildren().add(e_181);
								}
								{
									Region e_183 = new Region();
									e_183.setPrefHeight(22.0);
									e_180.getChildren().add(e_183);
									// an enum type
									HBox.setHgrow(e_183,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_184 = new Button();
									e_184.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_184.setDisable(true);
									e_184.setMaxHeight(22.0);
									e_184.setMaxWidth(22.0);
									e_184.setMinHeight(22.0);
									e_184.setMinWidth(22.0);
									e_184.setMnemonicParsing(false);
									e_184.setPrefHeight(22.0);
									e_184.setPrefWidth(22.0);
									e_184.setStyle("-fx-background-color: transparent;");
									e_184.setVisible(false);
									e_180.getChildren().add(e_184);
								}
								{
									Button e_185 = new Button();
									e_185.setId("editAbaIcon11");
									e_185.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_185.setDisable(true);
									e_185.setMaxHeight(22.0);
									e_185.setMaxWidth(22.0);
									e_185.setMinHeight(22.0);
									e_185.setMinWidth(22.0);
									e_185.setMnemonicParsing(false);
									e_185.setPrefHeight(22.0);
									e_185.setPrefWidth(22.0);
									e_185.setStyle("-fx-background-color: transparent;");
									e_185.setVisible(false);
									e_180.getChildren().add(e_185);
								}
								{
									Button e_186 = new Button();
									e_186.setId("phaEyeButton");
									e_186.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_186.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_186.setMaxHeight(22.0);
									e_186.setMaxWidth(22.0);
									e_186.setMinHeight(22.0);
									e_186.setMinWidth(22.0);
									e_186.setMnemonicParsing(false);
									e_186.setPrefHeight(22.0);
									e_186.setPrefWidth(22.0);
									{
										Insets e_187;
										InsetsBuilder e_187Builder = InsetsBuilder.create();
										e_187Builder.bottom(1.0);
										e_187Builder.left(1.0);
										e_187Builder.right(1.0);
										e_187Builder.top(1.0);
										e_187 = e_187Builder.build();
										e_186.setPadding(e_187);
									}
									e_180.getChildren().add(e_186);
								}
								{
									Button e_188 = new Button();
									e_188.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_188.setDisable(true);
									e_188.setMaxHeight(22.0);
									e_188.setMaxWidth(22.0);
									e_188.setMinHeight(22.0);
									e_188.setMinWidth(22.0);
									e_188.setMnemonicParsing(false);
									e_188.setPrefHeight(22.0);
									e_188.setPrefWidth(22.0);
									e_188.setVisible(false);
									e_180.getChildren().add(e_188);
								}
								e_92.getChildren().add(e_180);
							}
							{
								HBox e_189 = new HBox();
								e_189.setSpacing(2.0);
								{
									Label e_190 = new Label();
									e_190.setLayoutX(10.0);
									e_190.setLayoutY(10.0);
									e_190.setPrefHeight(22.0);
									e_190.setText("+Neuronal");
									{
										Font e_191;
										FontBuilder e_191Builder = FontBuilder.create();
										e_191Builder.size(14.0);
										e_191 = e_191Builder.build();
										e_190.setFont(e_191);
									}
									e_189.getChildren().add(e_190);
								}
								{
									Region e_192 = new Region();
									e_192.setPrefHeight(22.0);
									e_189.getChildren().add(e_192);
									// an enum type
									HBox.setHgrow(e_192,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_193 = new Button();
									e_193.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_193.setDisable(true);
									e_193.setMaxHeight(22.0);
									e_193.setMaxWidth(22.0);
									e_193.setMinHeight(22.0);
									e_193.setMinWidth(22.0);
									e_193.setMnemonicParsing(false);
									e_193.setPrefHeight(22.0);
									e_193.setPrefWidth(22.0);
									e_193.setStyle("-fx-background-color: transparent;");
									e_193.setVisible(false);
									e_189.getChildren().add(e_193);
								}
								{
									Button e_194 = new Button();
									e_194.setId("editAbaIcon111");
									e_194.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_194.setDisable(true);
									e_194.setMaxHeight(22.0);
									e_194.setMaxWidth(22.0);
									e_194.setMinHeight(22.0);
									e_194.setMinWidth(22.0);
									e_194.setMnemonicParsing(false);
									e_194.setPrefHeight(22.0);
									e_194.setPrefWidth(22.0);
									e_194.setStyle("-fx-background-color: transparent;");
									e_194.setVisible(false);
									e_189.getChildren().add(e_194);
								}
								{
									Button e_195 = new Button();
									e_195.setId("neuEyeButton");
									e_195.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_195.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_195.setMaxHeight(22.0);
									e_195.setMaxWidth(22.0);
									e_195.setMinHeight(22.0);
									e_195.setMinWidth(22.0);
									e_195.setMnemonicParsing(false);
									e_195.setPrefHeight(22.0);
									e_195.setPrefWidth(22.0);
									{
										Insets e_196;
										InsetsBuilder e_196Builder = InsetsBuilder.create();
										e_196Builder.bottom(1.0);
										e_196Builder.left(1.0);
										e_196Builder.right(1.0);
										e_196Builder.top(1.0);
										e_196 = e_196Builder.build();
										e_195.setPadding(e_196);
									}
									e_189.getChildren().add(e_195);
								}
								{
									Button e_197 = new Button();
									e_197.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_197.setDisable(true);
									e_197.setMaxHeight(22.0);
									e_197.setMaxWidth(22.0);
									e_197.setMinHeight(22.0);
									e_197.setMinWidth(22.0);
									e_197.setMnemonicParsing(false);
									e_197.setPrefHeight(22.0);
									e_197.setPrefWidth(22.0);
									e_197.setVisible(false);
									e_189.getChildren().add(e_197);
								}
								e_92.getChildren().add(e_189);
							}
							{
								HBox e_198 = new HBox();
								e_198.setSpacing(2.0);
								{
									Label e_199 = new Label();
									e_199.setLayoutX(10.0);
									e_199.setLayoutY(10.0);
									e_199.setPrefHeight(22.0);
									e_199.setText("+Alimentary");
									{
										Font e_200;
										FontBuilder e_200Builder = FontBuilder.create();
										e_200Builder.size(14.0);
										e_200 = e_200Builder.build();
										e_199.setFont(e_200);
									}
									e_198.getChildren().add(e_199);
								}
								{
									Region e_201 = new Region();
									e_201.setPrefHeight(22.0);
									e_198.getChildren().add(e_201);
									// an enum type
									HBox.setHgrow(e_201,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_202 = new Button();
									e_202.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_202.setDisable(true);
									e_202.setMaxHeight(22.0);
									e_202.setMaxWidth(22.0);
									e_202.setMinHeight(22.0);
									e_202.setMinWidth(22.0);
									e_202.setMnemonicParsing(false);
									e_202.setPrefHeight(22.0);
									e_202.setPrefWidth(22.0);
									e_202.setStyle("-fx-background-color: transparent;");
									e_202.setVisible(false);
									e_198.getChildren().add(e_202);
								}
								{
									Button e_203 = new Button();
									e_203.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_203.setDisable(true);
									e_203.setMaxHeight(22.0);
									e_203.setMaxWidth(22.0);
									e_203.setMinHeight(22.0);
									e_203.setMinWidth(22.0);
									e_203.setMnemonicParsing(false);
									e_203.setPrefHeight(22.0);
									e_203.setPrefWidth(22.0);
									e_203.setVisible(false);
									e_198.getChildren().add(e_203);
								}
								{
									Button e_204 = new Button();
									e_204.setId("aliEyeButton");
									e_204.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_204.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_204.setMaxHeight(22.0);
									e_204.setMaxWidth(22.0);
									e_204.setMinHeight(22.0);
									e_204.setMinWidth(22.0);
									e_204.setMnemonicParsing(false);
									e_204.setPrefHeight(22.0);
									e_204.setPrefWidth(22.0);
									{
										Insets e_205;
										InsetsBuilder e_205Builder = InsetsBuilder.create();
										e_205Builder.bottom(1.0);
										e_205Builder.left(1.0);
										e_205Builder.right(1.0);
										e_205Builder.top(1.0);
										e_205 = e_205Builder.build();
										e_204.setPadding(e_205);
									}
									e_198.getChildren().add(e_204);
								}
								{
									Button e_206 = new Button();
									e_206.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_206.setDisable(true);
									e_206.setMaxHeight(22.0);
									e_206.setMaxWidth(22.0);
									e_206.setMinHeight(22.0);
									e_206.setMinWidth(22.0);
									e_206.setMnemonicParsing(false);
									e_206.setPrefHeight(22.0);
									e_206.setPrefWidth(22.0);
									e_206.setVisible(false);
									e_198.getChildren().add(e_206);
								}
								e_92.getChildren().add(e_198);
							}
							{
								Region e_207 = new Region();
								e_207.setPrefHeight(22.0);
								e_207.setPrefWidth(242.0);
								e_92.getChildren().add(e_207);
							}
							{
								Label e_208 = new Label();
								e_208.setPrefHeight(22.0);
								e_208.setText("Tags:");
								{
									Font e_209;
									FontBuilder e_209Builder = FontBuilder.create();
									e_209Builder.size(14.0);
									e_209 = e_209Builder.build();
									e_208.setFont(e_209);
								}
								e_92.getChildren().add(e_208);
							}
							{
								HBox e_210 = new HBox();
								e_210.setSpacing(2.0);
								{
									Label e_211 = new Label();
									e_211.setLayoutX(10.0);
									e_211.setLayoutY(10.0);
									e_211.setPrefHeight(22.0);
									e_211.setText("VNC Formation");
									{
										Font e_212;
										FontBuilder e_212Builder = FontBuilder.create();
										e_212Builder.size(14.0);
										e_212 = e_212Builder.build();
										e_211.setFont(e_212);
									}
									e_210.getChildren().add(e_211);
								}
								{
									Region e_213 = new Region();
									e_213.setPrefHeight(22.0);
									e_210.getChildren().add(e_213);
									// an enum type
									HBox.setHgrow(e_213,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_214 = new Button();
									e_214.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_214.setDisable(true);
									e_214.setMaxHeight(22.0);
									e_214.setMaxWidth(22.0);
									e_214.setMinHeight(22.0);
									e_214.setMinWidth(22.0);
									e_214.setMnemonicParsing(false);
									e_214.setPrefHeight(22.0);
									e_214.setPrefWidth(22.0);
									e_214.setStyle("-fx-background-color: transparent;");
									e_214.setVisible(false);
									e_210.getChildren().add(e_214);
								}
								{
									Button e_215 = new Button();
									e_215.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_215.setDisable(true);
									e_215.setMaxHeight(22.0);
									e_215.setMaxWidth(22.0);
									e_215.setMinHeight(22.0);
									e_215.setMinWidth(22.0);
									e_215.setMnemonicParsing(false);
									e_215.setPrefHeight(22.0);
									e_215.setPrefWidth(22.0);
									e_215.setVisible(false);
									e_210.getChildren().add(e_215);
								}
								{
									Button e_216 = new Button();
									e_216.setId("tagVncEyeButton");
									e_216.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_216.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_216.setMaxHeight(22.0);
									e_216.setMaxWidth(22.0);
									e_216.setMinHeight(22.0);
									e_216.setMinWidth(22.0);
									e_216.setMnemonicParsing(false);
									e_216.setPrefHeight(22.0);
									e_216.setPrefWidth(22.0);
									{
										Insets e_217;
										InsetsBuilder e_217Builder = InsetsBuilder.create();
										e_217Builder.bottom(1.0);
										e_217Builder.left(1.0);
										e_217Builder.right(1.0);
										e_217Builder.top(1.0);
										e_217 = e_217Builder.build();
										e_216.setPadding(e_217);
									}
									e_210.getChildren().add(e_216);
								}
								{
									Button e_218 = new Button();
									e_218.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_218.setDisable(true);
									e_218.setMaxHeight(22.0);
									e_218.setMaxWidth(22.0);
									e_218.setMinHeight(22.0);
									e_218.setMinWidth(22.0);
									e_218.setMnemonicParsing(false);
									e_218.setPrefHeight(22.0);
									e_218.setPrefWidth(22.0);
									e_218.setVisible(false);
									e_210.getChildren().add(e_218);
								}
								e_92.getChildren().add(e_210);
							}
							{
								HBox e_219 = new HBox();
								e_219.setSpacing(2.0);
								{
									Label e_220 = new Label();
									e_220.setLayoutX(10.0);
									e_220.setLayoutY(10.0);
									e_220.setPrefHeight(22.0);
									e_220.setText("Nerve Ring Timeline");
									{
										Font e_221;
										FontBuilder e_221Builder = FontBuilder.create();
										e_221Builder.size(14.0);
										e_221 = e_221Builder.build();
										e_220.setFont(e_221);
									}
									e_219.getChildren().add(e_220);
								}
								{
									Region e_222 = new Region();
									e_222.setPrefHeight(22.0);
									e_219.getChildren().add(e_222);
									// an enum type
									HBox.setHgrow(e_222,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_223 = new Button();
									e_223.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_223.setDisable(true);
									e_223.setMaxHeight(22.0);
									e_223.setMaxWidth(22.0);
									e_223.setMinHeight(22.0);
									e_223.setMinWidth(22.0);
									e_223.setMnemonicParsing(false);
									e_223.setPrefHeight(22.0);
									e_223.setPrefWidth(22.0);
									e_223.setStyle("-fx-background-color: transparent;");
									e_223.setVisible(false);
									e_219.getChildren().add(e_223);
								}
								{
									Button e_224 = new Button();
									e_224.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_224.setDisable(true);
									e_224.setMaxHeight(22.0);
									e_224.setMaxWidth(22.0);
									e_224.setMinHeight(22.0);
									e_224.setMinWidth(22.0);
									e_224.setMnemonicParsing(false);
									e_224.setPrefHeight(22.0);
									e_224.setPrefWidth(22.0);
									e_224.setVisible(false);
									e_219.getChildren().add(e_224);
								}
								{
									Button e_225 = new Button();
									e_225.setId("tagNerEyeButton");
									e_225.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_225.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_225.setMaxHeight(22.0);
									e_225.setMaxWidth(22.0);
									e_225.setMinHeight(22.0);
									e_225.setMinWidth(22.0);
									e_225.setMnemonicParsing(false);
									e_225.setPrefHeight(22.0);
									e_225.setPrefWidth(22.0);
									{
										Insets e_226;
										InsetsBuilder e_226Builder = InsetsBuilder.create();
										e_226Builder.bottom(1.0);
										e_226Builder.left(1.0);
										e_226Builder.right(1.0);
										e_226Builder.top(1.0);
										e_226 = e_226Builder.build();
										e_225.setPadding(e_226);
									}
									e_219.getChildren().add(e_225);
								}
								{
									Button e_227 = new Button();
									e_227.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_227.setDisable(true);
									e_227.setMaxHeight(22.0);
									e_227.setMaxWidth(22.0);
									e_227.setMinHeight(22.0);
									e_227.setMinWidth(22.0);
									e_227.setMnemonicParsing(false);
									e_227.setPrefHeight(22.0);
									e_227.setPrefWidth(22.0);
									e_227.setVisible(false);
									e_219.getChildren().add(e_227);
								}
								e_92.getChildren().add(e_219);
							}
							{
								HBox e_228 = new HBox();
								e_228.setSpacing(2.0);
								{
									Label e_229 = new Label();
									e_229.setLayoutX(10.0);
									e_229.setLayoutY(10.0);
									e_229.setPrefHeight(22.0);
									e_229.setText("Gastrulation Events");
									{
										Font e_230;
										FontBuilder e_230Builder = FontBuilder.create();
										e_230Builder.size(14.0);
										e_230 = e_230Builder.build();
										e_229.setFont(e_230);
									}
									e_228.getChildren().add(e_229);
								}
								{
									Region e_231 = new Region();
									e_231.setPrefHeight(22.0);
									e_228.getChildren().add(e_231);
									// an enum type
									HBox.setHgrow(e_231,javafx.scene.layout.Priority.ALWAYS);
								}
								{
									Button e_232 = new Button();
									e_232.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_232.setDisable(true);
									e_232.setMaxHeight(22.0);
									e_232.setMaxWidth(22.0);
									e_232.setMinHeight(22.0);
									e_232.setMinWidth(22.0);
									e_232.setMnemonicParsing(false);
									e_232.setPrefHeight(22.0);
									e_232.setPrefWidth(22.0);
									e_232.setStyle("-fx-background-color: transparent;");
									e_232.setVisible(false);
									e_228.getChildren().add(e_232);
								}
								{
									Button e_233 = new Button();
									e_233.setId("editAbaIcon12");
									e_233.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_233.setDisable(true);
									e_233.setMaxHeight(22.0);
									e_233.setMaxWidth(22.0);
									e_233.setMinHeight(22.0);
									e_233.setMinWidth(22.0);
									e_233.setMnemonicParsing(false);
									e_233.setPrefHeight(22.0);
									e_233.setPrefWidth(22.0);
									e_233.setStyle("-fx-background-color: transparent;");
									e_233.setVisible(false);
									e_228.getChildren().add(e_233);
								}
								{
									Button e_234 = new Button();
									e_234.setId("tagGasEyeButton");
									e_234.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_234.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
									e_234.setMaxHeight(22.0);
									e_234.setMaxWidth(22.0);
									e_234.setMinHeight(22.0);
									e_234.setMinWidth(22.0);
									e_234.setMnemonicParsing(false);
									e_234.setPrefHeight(22.0);
									e_234.setPrefWidth(22.0);
									{
										Insets e_235;
										InsetsBuilder e_235Builder = InsetsBuilder.create();
										e_235Builder.bottom(1.0);
										e_235Builder.left(1.0);
										e_235Builder.right(1.0);
										e_235Builder.top(1.0);
										e_235 = e_235Builder.build();
										e_234.setPadding(e_235);
									}
									e_228.getChildren().add(e_234);
								}
								{
									Button e_236 = new Button();
									e_236.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
									e_236.setDisable(true);
									e_236.setMaxHeight(22.0);
									e_236.setMaxWidth(22.0);
									e_236.setMinHeight(22.0);
									e_236.setMinWidth(22.0);
									e_236.setMnemonicParsing(false);
									e_236.setPrefHeight(22.0);
									e_236.setPrefWidth(22.0);
									e_236.setVisible(false);
									e_228.getChildren().add(e_236);
								}
								e_92.getChildren().add(e_228);
							}
							e_91.getChildren().add(e_92);
							AnchorPane.setBottomAnchor(e_92,10.0);
							AnchorPane.setLeftAnchor(e_92,10.0);
							AnchorPane.setRightAnchor(e_92,10.0);
							AnchorPane.setTopAnchor(e_92,10.0);
							// an enum type
							HBox.setHgrow(e_92,javafx.scene.layout.Priority.ALWAYS);
						}
						e_90.setContent(e_91);
					}
					e_10.getTabs().add(e_90);
				}
				e_6.getChildren().add(e_10);
				Insets e_237;
				InsetsBuilder e_237Builder = InsetsBuilder.create();
				e_237 = e_237Builder.build();
				GridPane.setMargin(e_10,e_237);
			}
			{
				BorderPane e_238 = new BorderPane();
				e_238.setId("displayPanel");
				{
					Insets e_239;
					InsetsBuilder e_239Builder = InsetsBuilder.create();
					e_239 = e_239Builder.build();
					e_238.setOpaqueInsets(e_239);
				}
				{
					Insets e_240;
					InsetsBuilder e_240Builder = InsetsBuilder.create();
					e_240Builder.bottom(3.0);
					e_240Builder.left(3.0);
					e_240Builder.right(3.0);
					e_240Builder.top(3.0);
					e_240 = e_240Builder.build();
					e_238.setPadding(e_240);
				}
				{
					AnchorPane e_241 = new AnchorPane();
					e_241.setId("modelAnchorPane");
					e_241.setPrefHeight(608.0);
					e_241.setPrefWidth(619.0);
					{
						Slider e_242 = new Slider();
						e_242.setId("timeSlider");
						e_242.setLayoutX(111.0);
						e_242.setLayoutY(574.0);
						e_242.setPrefHeight(30.0);
						e_242.setPrefWidth(420.0);
						e_241.getChildren().add(e_242);
						AnchorPane.setBottomAnchor(e_242,0.0);
						AnchorPane.setLeftAnchor(e_242,97.0);
						AnchorPane.setRightAnchor(e_242,0.0);
					}
					{
						Button e_243 = new Button();
						e_243.setId("backwardButton");
						e_243.setId("backwardButton");
						e_243.setLayoutX(14.0);
						e_243.setLayoutY(577.0);
						e_243.setMaxHeight(25.0);
						e_243.setMaxWidth(25.0);
						e_243.setMinHeight(25.0);
						e_243.setMinWidth(25.0);
						e_243.setMnemonicParsing(false);
						e_243.setPrefHeight(25.0);
						e_243.setPrefWidth(25.0);
						e_241.getChildren().add(e_243);
						AnchorPane.setBottomAnchor(e_243,0.0);
						AnchorPane.setLeftAnchor(e_243,0.0);
					}
					{
						Button e_244 = new Button();
						e_244.setId("playButton");
						e_244.setId("playButton");
						e_244.setLayoutX(38.0);
						e_244.setLayoutY(577.0);
						e_244.setMaxHeight(25.0);
						e_244.setMaxWidth(25.0);
						e_244.setMinHeight(25.0);
						e_244.setMinWidth(25.0);
						e_244.setMnemonicParsing(false);
						e_244.setPrefHeight(25.0);
						e_244.setPrefWidth(25.0);
						e_241.getChildren().add(e_244);
						AnchorPane.setBottomAnchor(e_244,0.0);
						AnchorPane.setLeftAnchor(e_244,32.0);
					}
					{
						Button e_245 = new Button();
						e_245.setId("forwardButton");
						e_245.setId("forwardButton");
						e_245.setLayoutX(82.0);
						e_245.setLayoutY(573.0);
						e_245.setMaxHeight(25.0);
						e_245.setMaxWidth(25.0);
						e_245.setMinHeight(25.0);
						e_245.setMinWidth(25.0);
						e_245.setMnemonicParsing(false);
						e_245.setPrefHeight(25.0);
						e_245.setPrefWidth(25.0);
						e_241.getChildren().add(e_245);
						AnchorPane.setBottomAnchor(e_245,0.0);
						AnchorPane.setLeftAnchor(e_245,66.0);
					}
					{
						Label e_246 = new Label();
						e_246.setId("timeLabel");
						e_246.setLayoutX(500.0);
						e_246.setLayoutY(581.0);
						e_246.setText("Time 001");
						e_246.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
						e_246.setTextFill(javafx.scene.paint.Paint.valueOf("WHITE"));
						e_246.setTranslateZ(10.0);
						{
							Font e_247;
							FontBuilder e_247Builder = FontBuilder.create();
							e_247Builder.size(14.0);
							e_247 = e_247Builder.build();
							e_246.setFont(e_247);
						}
						e_241.getChildren().add(e_246);
						AnchorPane.setBottomAnchor(e_246,35.0);
						AnchorPane.setRightAnchor(e_246,5.0);
					}
					{
						Label e_248 = new Label();
						e_248.setId("totalNucleiLabel");
						e_248.setLayoutX(1.0);
						e_248.setLayoutY(558.0);
						e_248.setText("0 nuclei");
						e_248.setTextFill(javafx.scene.paint.Paint.valueOf("WHITE"));
						e_248.setTranslateZ(10.0);
						{
							Font e_249;
							FontBuilder e_249Builder = FontBuilder.create();
							e_249Builder.size(14.0);
							e_249 = e_249Builder.build();
							e_248.setFont(e_249);
						}
						e_241.getChildren().add(e_248);
						AnchorPane.setBottomAnchor(e_248,33.0);
						AnchorPane.setLeftAnchor(e_248,5.0);
					}
					e_238.setCenter(e_241);
					// an enum type
					BorderPane.setAlignment(e_241,javafx.geometry.Pos.CENTER);
				}
				{
					AnchorPane e_250 = new AnchorPane();
					{
						ScrollPane e_251 = new ScrollPane();
						e_251.setId("infoPane");
						e_251.setPrefHeight(159.0);
						{
							VBox e_252 = new VBox();
							e_252.setPrefWidth(550.0);
							e_252.setSpacing(5.0);
							{
								Insets e_253;
								InsetsBuilder e_253Builder = InsetsBuilder.create();
								e_253Builder.bottom(10.0);
								e_253Builder.left(10.0);
								e_253Builder.right(10.0);
								e_253Builder.top(10.0);
								e_253 = e_253Builder.build();
								e_252.setPadding(e_253);
							}
							{
								Label e_254 = new Label();
								e_254.setId("cellName");
								{
									Font e_255;
									FontBuilder e_255Builder = FontBuilder.create();
									e_255Builder.name("System Bold");
									e_255Builder.size(14.0);
									e_255 = e_255Builder.build();
									e_254.setFont(e_255);
								}
								e_252.getChildren().add(e_254);
							}
							{
								Label e_256 = new Label();
								e_256.setId("cellDescription");
								{
									Font e_257;
									FontBuilder e_257Builder = FontBuilder.create();
									e_257Builder.size(14.0);
									e_257 = e_257Builder.build();
									e_256.setFont(e_257);
								}
								e_252.getChildren().add(e_256);
							}
							e_251.setContent(e_252);
						}
						e_250.getChildren().add(e_251);
						AnchorPane.setBottomAnchor(e_251,0.0);
						AnchorPane.setLeftAnchor(e_251,0.0);
						AnchorPane.setRightAnchor(e_251,0.0);
						AnchorPane.setTopAnchor(e_251,0.0);
						// an enum type
						BorderPane.setAlignment(e_251,javafx.geometry.Pos.BOTTOM_CENTER);
						Insets e_258;
						InsetsBuilder e_258Builder = InsetsBuilder.create();
						e_258Builder.top(3.0);
						e_258 = e_258Builder.build();
						BorderPane.setMargin(e_251,e_258);
					}
					e_238.setBottom(e_250);
					Insets e_259;
					InsetsBuilder e_259Builder = InsetsBuilder.create();
					e_259Builder.top(5.0);
					e_259 = e_259Builder.build();
					BorderPane.setMargin(e_250,e_259);
				}
				e_6.getChildren().add(e_238);
				GridPane.setColumnIndex(e_238,1);
				Insets e_260;
				InsetsBuilder e_260Builder = InsetsBuilder.create();
				e_260 = e_260Builder.build();
				GridPane.setMargin(e_238,e_260);
			}
			root.setCenter(e_6);
			// an enum type
			BorderPane.setAlignment(e_6,javafx.geometry.Pos.CENTER);
		}
		return root;
	}


}
