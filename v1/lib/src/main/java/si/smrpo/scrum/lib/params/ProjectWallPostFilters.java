package si.smrpo.scrum.lib.params;

public class ProjectWallPostFilters {
    
    private int limit;
    
    private int offset;
    
    private boolean sortCreatedAtDesc;
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public boolean isSortCreatedAtDesc() {
        return sortCreatedAtDesc;
    }
    
    public void setSortCreatedAtDesc(boolean sortCreatedAtDesc) {
        this.sortCreatedAtDesc = sortCreatedAtDesc;
    }
}
