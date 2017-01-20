package io.sbelkin.monitorgrowth.impl;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import io.sbelkin.monitorgrowth.ActionInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by sabelkin on 1/19/2017.
 */
public class DropboxUpload implements ActionInterface {

    private static final Logger LOGGER = Logger.getLogger( DropboxUpload.class.getName() );

    private final String accessToken;
    private final String failFolder;
    private final String uploadFolder;

    public DropboxUpload(String accessToken, String failFolder, String uploadFolder){
        this.accessToken = accessToken;
        this.failFolder = failFolder;
        this.uploadFolder = uploadFolder;
    }

    @Override
    public void action() {
        DbxRequestConfig config = new DbxRequestConfig("monitor-growth");
        DbxClientV2 client = new DbxClientV2(config, accessToken);

        List<File> filesInFolder = null;
        try {
            filesInFolder = Files.walk(Paths.get(uploadFolder))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception throw accessing list of files.",e);
        }

        for (File file : filesInFolder ) {
            try (InputStream in = new FileInputStream(file.getPath())) {
                FileMetadata metadata = client.files().uploadBuilder("/"+file.getName())
                        .uploadAndFinish(in);
            } catch (Exception e) {
                Path filePath = file.toPath();
                Path failPath = Paths.get(failFolder+file.getName()).toAbsolutePath();
                try {
                    LOGGER.log(Level.INFO, String.format("Moving %1$s to %2$s", file.getName(), failFolder));
                    Files.move(filePath,failPath, REPLACE_EXISTING);
                } catch (IOException e1) {
                    LOGGER.log(Level.SEVERE, "Moving file failed.",e1);
                }
            } finally {
                file.delete();
            }
        }
        LOGGER.log(Level.INFO, String.format("Uploaded %1$d files.", filesInFolder.size()));

    }
}
