package si.smrpo.scrum.api.params;

import com.kumuluz.ee.rest.enums.OrderDirection;
import si.smrpo.scrum.lib.params.ProjectStoriesFilters;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class ProjectStoriesParams {
    
    @QueryParam("numberIdSort")
    @DefaultValue("ASC")
    private OrderDirection numberIdSort;
    
    @QueryParam("filterRealized")
    @DefaultValue("false")
    private Boolean filterRealized;
    
    @QueryParam("filterAssigned")
    @DefaultValue("false")
    private Boolean filterAssigned;
    
    @QueryParam("limit")
    @DefaultValue("10")
    private int limit;
    
    @QueryParam("offset")
    @DefaultValue("0")
    private int offset;
    
    public ProjectStoriesFilters toProjectStoriesFilters() {
        ProjectStoriesFilters params = new ProjectStoriesFilters();
        if (numberIdSort != null) {
            params.setNumberIdSortAsc(numberIdSort.equals(OrderDirection.ASC));
        }
        params.setFilterAssigned(this.filterAssigned);
        params.setFilterRealized(this.filterRealized);
        params.setLimit(this.limit <= 100 ? (this.limit <= 0 ? 10 : this.limit) : 100);
        params.setOffset(this.offset);
        return params;
    }
    
    public OrderDirection getNumberIdSort() {
        return numberIdSort;
    }
    
    public void setNumberIdSort(OrderDirection numberIdSort) {
        this.numberIdSort = numberIdSort;
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
