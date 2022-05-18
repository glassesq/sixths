package com.example.sixths.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;

@Service
public class ResourceService {
    /* https://spring.io/guides/gs/uploading-files/ */

    private final String root = System.getProperty("user.dir") + "/statics";

    public String savePart(Part part, String type) {
        try {
            String path = root + "/" + type + "s/";

            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();

            path = path.concat(part.getSubmittedFileName());
            part.write(path);
            return type + "s/" + part.getSubmittedFileName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Resource loadAsResource(String src) {
        Resource res;
        res = new PathResource(root + "/" + src);
        return res;
    }
}
