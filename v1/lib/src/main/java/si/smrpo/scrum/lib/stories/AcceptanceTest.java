package si.smrpo.scrum.lib.stories;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.stories.*;

public class AcceptanceTest extends BaseType {

    private String result;

    private Story story;

    private String storyId;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }
}
