package si.smrpo.scrum.lib.requests;

import java.util.List;

public class AddStoryRequest {

    private List<String> storyIds;

    public List<String> getStoryIds() {
        return storyIds;
    }

    public void setStoryIds(List<String> storyIds) {
        this.storyIds = storyIds;
    }
}