package si.smrpo.scrum.lib.meta;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Used only for OpenApi documentation
 */
public class FileUploadRequest {
    
    @Schema(format = "binary", type = SchemaType.STRING)
    private String file;
    
    public String getFile() {
        return file;
    }
    
    public void setFile(String file) {
        this.file = file;
    }
    
}
