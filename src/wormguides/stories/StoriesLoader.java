/*
 * Bao Lab 2016
 */

package wormguides.stories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javafx.collections.ObservableList;

/**
 * Loader for wormguides.stories specified in the internal wormguides.stories config file
 */
public class StoriesLoader {

    public static final int NUMBER_OF_CSV_FIELDS = 15;

    public static final int STORY_NAME_INDEX = 0,
            STORY_DESCRIPTION_INDEX = 1,
            STORY_AUTHOR_INDEX = 12,
            STORY_DATE_INDEX = 13,
            STORY_COLOR_URL_INDEX = 14;

    public static final int NAME_INDEX = 0,
            CONTENTS_INDEX = 1,
            DISPLAY_INDEX = 2,
            TYPE_INDEX = 3,
            LOCATION_INDEX = 4,
            CELLNAME_INDEX = 5,
            MARKER_INDEX = 6,
            IMG_SOURCE_INDEX = 7,
            RESOURCE_LOCATION_INDEX = 8,
            START_TIME_INDEX = 9,
            END_TIME_INDEX = 10,
            COMMENTS_INDEX = 11;

    private static final String STORY_LIST_CONFIG = "/wormguides/stories/StoryListConfig.csv";

    public static void loadFromFile(File file, ObservableList<Story> stories, int offset) {
        if (file != null) {
            try (InputStream stream = new FileInputStream(file)) {
                processStream(stream, stories, offset);

            } catch (IOException ioe) {
                System.out.println("Could not read file '" + file.getName() + "' in the system.");
            }
        }
    }

    public static void loadConfigFile(ObservableList<Story> stories, int offset) {
        final URL url = StoriesLoader.class.getResource(STORY_LIST_CONFIG);

        if (url != null) {
            try (InputStream stream = url.openStream()){
                processStream(stream, stories, offset);

            } catch (IOException e) {
                System.out.println("Could not read file '" + STORY_LIST_CONFIG + "' in the system.");
            }
        }
    }

    private static void processStream(InputStream stream, ObservableList<Story> stories, int offset) {
        // used for accessing the current story for adding scene elements
        int storyCounter = stories.size() - 1;

        try {
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line;

            // Skip heading line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (split.length != NUMBER_OF_CSV_FIELDS) {
                    System.out.println("Missing fields in CSV file.");
                    continue;
                }

                // get rid of quotes in story description/note contents since field might have contained commas
                String contents = split[CONTENTS_INDEX];
                if (contents.startsWith("\"") && contents.endsWith("\"")) {
                    split[CONTENTS_INDEX] = contents.substring(1, contents.length() - 1);
                }

                if (isStory(split)) {
                    Story story = new Story(split[STORY_NAME_INDEX], split[STORY_DESCRIPTION_INDEX],
                            split[STORY_AUTHOR_INDEX], split[STORY_DATE_INDEX], split[STORY_COLOR_URL_INDEX]);
                    stories.add(story);
                    storyCounter++;

                } else {
                    Story story = stories.get(storyCounter);
                    final Note note = new Note(story, split[NAME_INDEX], split[CONTENTS_INDEX]);
                    story.addNote(note);

                    try {
                        note.setTagDisplay(split[DISPLAY_INDEX]);
                        note.setAttachmentType(split[TYPE_INDEX]);
                        note.setLocation(split[LOCATION_INDEX]);
                        note.setCellName(split[CELLNAME_INDEX]);

                        note.setImagingSource(split[IMG_SOURCE_INDEX]);
                        note.setResourceLocation(split[RESOURCE_LOCATION_INDEX]);

                        String startTime = split[START_TIME_INDEX];
                        String endTime = split[END_TIME_INDEX];
                        if (!startTime.isEmpty() && !endTime.isEmpty()) {
                            note.setStartTime(Integer.parseInt(startTime) - offset);
                            note.setEndTime(Integer.parseInt(endTime) - offset);
                        }

                        note.setComments(split[COMMENTS_INDEX]);

                    } catch (Exception e) {
                        System.out.println(e.toString());
                        System.out.println(line);
                        e.printStackTrace();
                    }
                }
            }

            reader.close();

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Unable to process file '" + STORY_LIST_CONFIG + "'.");

        } catch (NumberFormatException e) {
            System.out.println("Number Format Error in file '" + STORY_LIST_CONFIG + "'.");

        } catch (IOException e) {
            System.out.println("The config file '" + STORY_LIST_CONFIG + "' wasn't found on the system.");
        }
    }

    private static boolean isStory(String[] csvLine) {
        try {
            if (csvLine[DISPLAY_INDEX].isEmpty() && csvLine[TYPE_INDEX].isEmpty()) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }
}
