import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Manager {
    private FTPClient ftpClient = new FTPClient();

    public Manager() throws IOException, LoggingException {
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.connect("192.168.0.1", 21);
        ftpClient.login("Const", "q1w2e3r4");
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new LoggingException();
        }
    }

    public FileInfo[] listFiles(String directory) throws IOException {
        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new IOException();
        }

        FTPFile[] files = ftpClient.listFiles(directory);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+12:00"));
        FileInfo[] result = new FileInfo[files.length];

        int i = 0;
        for (FTPFile file : files) {
            result[i] = new FileInfo(file.getName(), file.getType(), dateFormat.format(file.getTimestamp().getTime()), file.getSize());
            i++;
        }

        return result;
    }

    public void setCurrDir(String dir) throws IOException{
        ftpClient.changeWorkingDirectory(dir);
    }

    public boolean uploadFile(String dest, String src) throws IOException {
        boolean result = false;

        try (FileInputStream fis = new FileInputStream(src)) {
            result = ftpClient.storeFile(dest, fis);
        }
        catch (IOException e) {
            throw new IOException();
        }

        return result;
    }

    public boolean createDir(String dirName) throws IOException {
        return ftpClient.makeDirectory(dirName);
    }

    public boolean remove(String name) throws IOException {
        if (ftpClient.removeDirectory(name))
            return true;
        else if (ftpClient.deleteFile(name))
            return true;
        else
            return false;
    }

    public boolean rename(String prevName, String newName) throws IOException {
        if (newName.contains("."))
            return ftpClient.rename(prevName, newName);
        else
            return false;
    }

    public boolean download(String remote, String local) throws IOException {
        boolean result = false;

        try (FileOutputStream fos = new FileOutputStream(local)) {
            result = ftpClient.retrieveFile(remote, fos);
        }
        catch (IOException e) {
            throw new IOException();
        }

        return result;
    }
}
