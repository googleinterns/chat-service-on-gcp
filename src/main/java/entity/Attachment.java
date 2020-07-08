package entity;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;
import org.springframework.cloud.gcp.data.spanner.core.mapping.NotMapped;

@Table(name = "Attachment")
public final class Attachment {
    
    @PrimaryKey
    @Column(name = "AttachmentID")
    private long attachmentId;

    /**
     * Attribute containing file name and extension
     */
    @Column(name = "FileName")
    private String fileName;

    /**
     * Attribute containing file type such as text/plain 
     * Different from file extension
     */
    @Column(name = "FileType")
    private String fileType;

    /**
     * Attribute containing file size in bytes
     */
    @Column(name = "FileSize")
    private long fileSizeInBytes;

    @NotMapped
    private MultipartFile file;

    public Attachment() {}

    public Attachment(long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Attachment(long attachmentId, MultipartFile file) {
        this.attachmentId = attachmentId;
        this.fileName = file.getOriginalFilename();
        this.fileType = fileName.split("\\.")[1];
        this.fileSizeInBytes = file.getSize();
        this.file = file;
    }

    public void setAttachmentId(long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileSizeInBytes(long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public long getAttachmentId() {
        return attachmentId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public MultipartFile getFile() {
        return file;
    }
}
