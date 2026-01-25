package com.scm.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    // Uploads image/file on the cloudinary
    String uploadImage(MultipartFile contactImage, String fileName);

    // it will give url of the image/file from its public id
    String getUrlFromPublicId(String publicId);
}
