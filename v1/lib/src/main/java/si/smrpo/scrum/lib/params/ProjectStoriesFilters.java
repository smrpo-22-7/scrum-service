package si.smrpo.scrum.lib.params;

public class ProjectStoriesFilters {
    
    private boolean numberIdSortAsc;
    
    private Boolean filterRealized;
    
    private Boolean filterAssigned;
    
    private int limit;
    
    private int offset;
    
    public boolean getNumberIdSortAsc() {
        return numberIdSortAsc;
    }
    
    public void setNumberIdSortAsc(boolean numberIdSortAsc) {
        this.numberIdSortAsc = numberIdSortAsc;
    }
    
    public Boolean getFilterRealized() {
        return filterRealized;
    }
    
    public void setFilterRealized(Boolean filterRealized) {
        this.filterRealized = filterRealized;
    }
    
    public Boolean getFilterAssigned() {
        return filterAssigned;
    }
    
    public void setFilterAssigned(Boolean filterAssigned) {
        this.filterAssigned = filterAssigned;
    }
    
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
}
