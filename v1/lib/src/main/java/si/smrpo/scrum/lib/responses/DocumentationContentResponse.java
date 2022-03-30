package si.smrpo.scrum.lib.responses;

public class DocumentationContentResponse {
    
    private byte[] bytes;
    
    private String filename;
    
    public byte[] getBytes() {
        return bytes;
    }
    
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
