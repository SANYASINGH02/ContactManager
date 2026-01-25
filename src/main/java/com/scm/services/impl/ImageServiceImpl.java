package com.scm.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.helpers.AppConstants;
import com.scm.services.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    // Object of Cloudinary
    private Cloudinary cloudinary;

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // method which upload contact image to the server & it returns image location url
    @Override
    public String uploadImage(MultipartFile contactImage, String fileName) {

        try {
            /* contactImage.getInputStream().available() = it will fetch all bytes available in inputStream of file */
            // currently data is blank array
            byte[] data = new  byte[contactImage.getInputStream().available()];
            // now data array has the content of file
            contactImage.getInputStream().read(data);
            // now upload the image/file on cloudinary
            cloudinary.uploader().upload(data, ObjectUtils.asMap(
                    "public_id", fileName
            ));

            // currently we're not using/giving the original file name/public_id
            return this.getUrlFromPublicId(fileName);
        } catch (IOException e) {
//            throw new RuntimeException("Getting error while uploading image to Cloudinary");
            System.out.println("Getting error while uploading image to Cloudinary");
            return null;
        }
    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        return cloudinary
                .url()
                .transformation(
                        new Transformation<>()
                                .width(AppConstants.CONTACT_IMAGE_WIDTH)
                                .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                                .crop(AppConstants.CONTACT_IMAGE_CROP)
                )
                .generate(publicId);
    }
}
