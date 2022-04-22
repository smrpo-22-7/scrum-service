package si.smrpo.scrum.api.params;

import com.kumuluz.ee.rest.enums.OrderDirection;
import si.smrpo.scrum.lib.params.ProjectWallPostFilters;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class ProjectWallPostsParams {
    
    @QueryParam("sort")
    @DefaultValue("DESC")
    private OrderDirection order;
    
    @QueryParam("limit")
    @DefaultValue("10")
    private int limit;
    
    @QueryParam("offset")
    @DefaultValue("0")
    private int offset;
    
    public ProjectWallPostFilters toProjectWallPostFilters() {
        ProjectWallPostFilters params = new ProjectWallPostFilters();
        if (order != null) {
            params.setSortCreatedAtDesc(order.equals(OrderDirection.DESC));
        } else {
            params.setSortCreatedAtDesc(true);
        }
        params.setLimit(this.limit <= 100 ? (this.limit <= 0 ? 10 : this.limit) : 100);
        params.setOffset(this.offset);
        return params;
    }
}
