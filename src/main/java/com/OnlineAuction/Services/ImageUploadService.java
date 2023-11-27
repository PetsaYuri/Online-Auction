package com.OnlineAuction.Services;

import com.OnlineAuction.Exceptions.UnableToSaveFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final String PATH_TO_UPLOAD = System.getProperty("user.dir") + "/uploads/";

    public boolean saveImage(MultipartFile file, String filename) {
        try {
            file.transferTo(new File(PATH_TO_UPLOAD + filename));
            return true;
        }   catch (IOException ex) {
            throw new UnableToSaveFileException(ex.getMessage());
        }
    }

    public String getUniqueFilename(String originalFilename) {
        try {
            return UUID.randomUUID().toString() + "." + originalFilename.split("\\.", 2)[1];
        }   catch (IndexOutOfBoundsException ex) {
            return UUID.randomUUID().toString() + ".jpg";
        }
    }
}
