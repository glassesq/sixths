package com.example.sixths.controller;

import com.example.sixths.interceptor.LoginInterceptor;
import com.example.sixths.model.User;
import com.example.sixths.service.ResourceService;
import com.example.sixths.service.UserService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping(path = "/resource")
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    @PostMapping(path = "/upload")
    public ResponseEntity<String> blockUser(HttpServletRequest req) {
        try {
            String type = req.getParameter("type");
            System.out.println(type);
            if (type != null) {
                String ret = resourceService.savePart(req.getPart(type), type);
                if (ret != null) {
                    return ResponseEntity.ok().body(ret);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("bad");
    }

    @GetMapping("/fetch")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(HttpServletRequest req) {
        String src = req.getParameter("src");
        Resource file = resourceService.loadAsResource(src);
        if (file != null) return ResponseEntity.ok()
               /* .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\""). */ .body(file);
        return ResponseEntity.badRequest().body(null);
    }


}
