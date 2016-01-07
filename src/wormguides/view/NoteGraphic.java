package wormguides.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import wormguides.model.Note;

/*
 * Graphical representation of a note
 * 
 * Note has a NoteGraphic
 */
public class NoteGraphic extends VBox{
	
	private Note parent;
	
	private Label title;
	private HBox contentsContainer;
	private Label contents;
	
	private boolean expanded;
	private BooleanProperty selected;
	
	
	// note is the parent note to which this graphic belongs to
	public NoteGraphic(Note note) {
		super();
		setPadding(new Insets(5, 5, 5, 5));
		
		parent = note;
		
		setBackground(Background.EMPTY);
		
		title = new Label();
		title.setWrapText(true);
		title.setFont(AppFont.getBoldFont());
		title.setStyle("-fx-text-fill: black");
		title.setText(note.getTagName());
		
		contentsContainer = new HBox(0);
		Region r = new Region();
		r.setPrefWidth(15);
		r.setMinWidth(15);
		r.setMaxWidth(15);
		contents = new Label();
		contents.setWrapText(true);
		contents.setFont(AppFont.getFont());
		contents.setStyle("-fx-text-fill: black");
		contentsContainer.getChildren().addAll(r, contents);
		contents.setText(note.getTagContents());
		
		getChildren().add(title);
		setPickOnBounds(false);
		
		expanded = false;
		
		selected = new SimpleBooleanProperty(false);
		selected.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue)
					highlightCell(true);
				else
					highlightCell(false);
			}
		});
	}
	
	
	private void highlightCell(boolean highlight) {
		if (highlight) {
			setStyle("-fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-selection-bar; "
					+ "-fx-background-insets: 0, 1, 2; "
					+ "-fx-background: -fx-accent;"
					+ "-fx-text-fill: -fx-selection-bar-text;");
		}
		else
			setStyle("-fx-background-color: white;");
	}
	
	
	/*
	 * Highlights note node in list view
	 */
	public void select() {
		selected.set(true);		
	}
	
	
	/*
	 * Un-highlights note node in list view
	 */
	public void deselect() {
		selected.set(false);
	}
	
	
	public BooleanProperty getSelectedBooleanProperty() {
		return selected;
	}
	
	
	public boolean isSelected() {
		return selected.get();
	}
	
	
	/*
	 * When the graphic is 'expanded' we can view the note contents
	 * otherwise, we just see the note title
	 */
	public boolean isExpanded() {
		return expanded;
	}
	
	
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
		
		if (expanded)
			getChildren().add(contentsContainer);
		else
			getChildren().remove(contentsContainer);
	}
	
	
	public void setTitle(String text) {
		title.setText("• "+text);
	}
	
	
	public void setContents(String text) {
		contents.setText(text);
	}
	
}
