package com.example.x.model.file;

import java.io.Serializable;
import java.util.UUID;

abstract public class File implements Serializable {
    protected String fileId;
    protected String filePath;
    protected long fileSize;

    public File(String filePath) {
        this.fileId = UUID.randomUUID().toString();
        this.filePath = filePath;
        this.fileSize = 0;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public abstract String getFileType();
}
