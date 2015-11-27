package wormguides.view;

import wormguides.model.RuleInfoPacket;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class RuleEditPane extends AnchorPane{
	
	private RuleInfoPacket infoPacket;

	public RuleEditPane() {
		this(new RuleInfoPacket(), null);
	}
		
	public RuleEditPane(RuleInfoPacket packet, EventHandler<ActionEvent> handler) {
		super();
		
		infoPacket = packet;
		
		setPrefHeight(370.0);
		setPrefWidth(240.0);
		
		VBox vbox = new VBox();
		vbox.setSpacing(3);
		AnchorPane.setTopAnchor(vbox, 10d);
		AnchorPane.setLeftAnchor(vbox, 10d);
		AnchorPane.setRightAnchor(vbox, 10d);
		AnchorPane.setBottomAnchor(vbox, 10d);
		
		Label optionsLabel = new Label("Search Options for "+packet.getName());
		
		Region r1 = new Region();
		r1.setPrefHeight(10);
		
		Label ancestryLabel = new Label("Cell Ancestry");
		
		Label cellLabel = new Label("Cell");
		CheckBox cellTick = new CheckBox();
		cellTick.setSelected(infoPacket.isCellSelected());
		cellTick.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				infoPacket.setCellSelected(newValue);
			}
		});
		HBox cellRow = makeEditRow(cellLabel, cellTick);
		
		Label ancLabel = new Label("Ancestor");
		CheckBox ancTick = new CheckBox();
		ancTick.setSelected(infoPacket.isAncestorSelected());
		ancTick.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				infoPacket.setAncestorSelected(newValue);
			}
		});
		HBox ancRow = makeEditRow(ancLabel, ancTick);
		
		Label desLabel = new Label("Descendant");
		CheckBox desTick = new CheckBox();
		desTick.setSelected(infoPacket.isDescendantSelected());
		desTick.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				infoPacket.setDescendantSelected(newValue);
			}
		});
		HBox desRow = makeEditRow(desLabel, desTick);
		if (packet.getName().contains("functional") 
				|| packet.getName().contains("description")
				|| packet.isDescendantDisabled()) {
			desLabel.disableProperty().set(true);
			desTick.disableProperty().set(true);
		}
		
		Region r2 = new Region();
		r2.setPrefHeight(10);
		
		Label colorLabel = new Label("Color");
		
		AnchorPane pickerPane = new AnchorPane();
		VBox.setVgrow(pickerPane, Priority.ALWAYS);
		ColorPicker picker = new ColorPicker(infoPacket.getColor());
		AnchorPane.setTopAnchor(picker, 0d);
		AnchorPane.setLeftAnchor(picker, 0d);
		AnchorPane.setRightAnchor(picker, 0d);
		AnchorPane.setBottomAnchor(picker, 0d);
		picker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				infoPacket.setColor(picker.getValue());
			}
		});
		pickerPane.getChildren().add(picker);
		
		HBox buttonBox = new HBox();
		buttonBox.setPrefHeight(30);
		Button submit = new Button("Submit");
		submit.setDefaultButton(true);
		submit.setFont(AppFont.getFont());
		submit.setPrefWidth(100);
		submit.setAlignment(Pos.CENTER);
		submit.setOnAction(handler);
		buttonBox.getChildren().add(submit);
		buttonBox.setAlignment(Pos.CENTER);
		
		Region r4 = new Region();
		r4.setPrefHeight(10);
		
		Region r5 = new Region();
		r5.setPrefHeight(5);
		
		if (packet.isAlphaEnabled()) {
			Label alphaLabel = new Label("Transparency");
			Slider alphaSlider = new Slider();
			alphaSlider.setMax(100);
			alphaSlider.setMin(0);
			alphaSlider.setValue(100);
			alphaSlider.valueProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, 
									Number oldValue, Number newValue) {
					packet.setAlpha(Math.round(newValue.doubleValue())/100d);
				}
			});
			HBox.setHgrow(alphaSlider, Priority.ALWAYS);
			
			Region r3 = new Region();
			r3.setPrefHeight(10);
			
			vbox.getChildren().addAll(optionsLabel, r1, ancestryLabel,
					cellRow, ancRow, desRow, r2, alphaLabel, alphaSlider, r3, 
					colorLabel, pickerPane, r4, buttonBox, r5);
		}
		else {
			vbox.getChildren().addAll(optionsLabel, r1, ancestryLabel,
					cellRow, ancRow, desRow, r2, 
					colorLabel, pickerPane, r4, buttonBox, r5);
		}
		
		
		
		for (Node node : vbox.getChildren()) {
			node.setStyle("-fx-focus-color: -fx-outer-border; "+
					"-fx-faint-focus-color: transparent;");
			if (node instanceof Label)
				((Label) node).setFont(AppFont.getFont());
		}
		
		getChildren().add(vbox);
	}
	
	private HBox makeEditRow(Label label, CheckBox box) {
		HBox row = new HBox();
		row.setPrefHeight(22);
		
		HBox inner = new HBox();
		inner.setPrefWidth(160);
		inner.setFillHeight(true);
		
		Region indent = new Region();
		indent.setPrefWidth(40);
		
		label.setFont(AppFont.getFont());
		
		Region separator = new Region();
		HBox.setHgrow(separator, Priority.ALWAYS);
		
		box.setPrefHeight(22);
		
		inner.getChildren().addAll(indent, label, separator, box);
		row.getChildren().add(inner);
		
		return row;
	}
}
