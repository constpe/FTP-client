import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class FileInfo {
    private final SimpleStringProperty fileName;
    private final SimpleStringProperty fileType;
    private final SimpleStringProperty fileDate;
    private final SimpleLongProperty fileSize;

    public FileInfo(String fileName, int fileType, String fileDate, long fileSize) {
        this.fileName = new SimpleStringProperty(fileName);
        this.fileType = (fileType == 1) ? new SimpleStringProperty("dir") : new SimpleStringProperty("file");
        this.fileDate = new SimpleStringProperty(fileDate);
        this.fileSize = new SimpleLongProperty(fileSize);
    }

    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public long getFileSize() {
        return fileSize.get();
    }

    public void setFileSize(String fileSize) {
        this.fileDate.set(fileSize);
    }

    public String getFileDate() {
        return fileDate.get();
    }

    public void setFileDate(String fileDate) {
        this.fileDate.set(fileDate);
    }

    public String getFileType() {
        return fileType.get();
    }

    public void setFileType(String fileType) {
        this.fileType.set(fileType);
    }
}
