/*
 * Bao Lab 2017
 */

package wormguides.view.graphicalrepresentations;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import wormguides.stories.Note;
import wormguides.stories.Story;

import static javafx.geometry.Insets.EMPTY;
import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.GREY;
import static javafx.scene.text.FontSmoothingType.LCD;

import static wormguides.layers.StoriesLayer.colorTexts;
import static wormguides.util.AppFont.getBolderFont;
import static wormguides.util.AppFont.getFont;

/**
 * Graphical representation of a {@link Story}. It makes an inactive story become active when the title/description
 * is clicked, and makes an active story become inactive when it is clicked. This graphical item is rendered in
 * the {@link ListCell} of the {@link ListView} in the 'Stories' tab.
 */
public class StoryGraphic extends VBox {

    private static final int PREF_WIDTH = 292;

    /** The heading containing the story title and description. The user clicks this to switch story contexts. */
    private final VBox storyHeadingVBox;

    /** Container (VBox) for the story's notes */
    private final VBox notesVBox;

    private final Text title;
    private final Text description;

    private boolean isClickedHandlerSet;

    public StoryGraphic(final Story story) {
        super();

        setPrefWidth(PREF_WIDTH);
        setMinWidth(USE_PREF_SIZE);

        setPadding(EMPTY);

        storyHeadingVBox = new VBox(5);
        storyHeadingVBox.setPickOnBounds(false);
        storyHeadingVBox.setPadding(new Insets(5));

        title = new Text(story.getTitle());
        title.setFont(getBolderFont());
        title.setFontSmoothingType(LCD);

        description = new Text(story.getDescription());
        description.setFont(getFont());
        description.setFontSmoothingType(LCD);

        storyHeadingVBox.getChildren().addAll(title, description);
        getChildren().addAll(storyHeadingVBox);

        story.getActiveProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // if story is active
                makeDisabled(false);
                // disable any active notes in the newly active story to get rid of old highlighting
                for (Note note : story.getNotes()) {
                    note.setActive(false);
                    addNoteGraphic(note.getGraphic());
                }
            } else {
                // is story is inactive
                makeDisabled(true);
                clearNotes();
            }
        });
        if (story.isActive()) {
            makeDisabled(false);
        } else {
            makeDisabled(true);
        }

        notesVBox = new VBox();

        final Separator s = new Separator(HORIZONTAL);
        s.setFocusTraversable(false);
        s.setStyle("-fx-focus-color: -fx-outer-border; -fx-faint-focus-color: transparent;");

        getChildren().addAll(notesVBox, s);

        isClickedHandlerSet = false;

        widthProperty().addListener(((observable, oldValue, newValue) -> {
            final int newWidth = newValue.intValue();
            title.setWrappingWidth(newWidth - 10);
            description.setWrappingWidth(newWidth - 10);
        }));
        title.setWrappingWidth(PREF_WIDTH - 10);
        description.setWrappingWidth(PREF_WIDTH - 10);

        notesVBox.setPrefWidth(PREF_WIDTH);
        notesVBox.setMaxWidth(USE_PREF_SIZE);
        notesVBox.setMinWidth(USE_PREF_SIZE);
    }

    /**
     * Removes the note graphics from this story graphic. Even though the notes are removed graphically, the story's
     * notes remain unchanged.
     */
    public void clearNotes() {
        notesVBox.getChildren().clear();
    }

    /**
     * Adds a note graphic to this story graphic. The width is changed in the note graphic to accomodate any changes
     * that may have occured to the listview's viewport size.
     *
     * @param noteGraphic
     *         note graphic to remove
     */
    private void addNoteGraphic(final NoteGraphic noteGraphic) {
        noteGraphic.setWidth(PREF_WIDTH);
        notesVBox.getChildren().add(noteGraphic);
    }

    /**
     * Removes a note graphic to this story graphic
     *
     * @param noteGraphic
     *         note graphic to remove
     */
    public void removeNoteGraphic(final NoteGraphic noteGraphic) {
        notesVBox.getChildren().remove(noteGraphic);
    }

    public void setTitle(final String titleText) {
        if (titleText != null) {
            title.setText(titleText);
        }
    }

    public void setDescription(final String descriptionText) {
        if (descriptionText != null) {
            description.setText(descriptionText);
        }
    }

    public boolean isClickedHandlerSet() {
        return isClickedHandlerSet;
    }

    public void setStoryClickedHandler(final EventHandler<MouseEvent> handler) {
        if (handler != null) {
            isClickedHandlerSet = true;
            storyHeadingVBox.setOnMouseClicked(handler);
        } else {
            isClickedHandlerSet = false;
        }
    }

    public void makeDisabled(boolean disabled) {
        if (!disabled) {
            colorTexts(BLACK, title, description);
        } else {
            colorTexts(GREY, title, description);
        }
    }
}
