package org.example.quan_ao_f4k.service.common;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class IImageServiceImpl implements IImageService {

    //    Properties properties;
    @Value("${firebase.image-url}")
    private String imageUrl;

    @Value("${firebase.bucket-name}")
    String bucketName;

    @Override
    public String getImageUrl(String name) {
        return String.format(imageUrl, name);
    }

    public String save(MultipartFile file, String pathFolder) throws IOException {
        String path = "images/" + pathFolder + "/";
        Bucket bucket = StorageClient.getInstance().bucket();
        String name = generateFileName(file.getOriginalFilename());
        bucket.create(path + name, file.getBytes(), file.getContentType());
        return path + name;
    }

    @Override
    public String save(BufferedImage bufferedImage, String originalFileName) throws IOException {
        byte[] bytes = getByteArrays(bufferedImage, getExtension(originalFileName));
        Bucket bucket = StorageClient.getInstance().bucket();
        String name = generateFileName(originalFileName);
        bucket.create(name, bytes);
        return name;
    }

    @Override
    public void delete(String name) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        if (StringUtils.isEmpty(name)) {
            throw new IOException("invalid file name");
        }
        Blob blob = bucket.get(name);
        if (blob == null) {
            throw new IOException("file not found");
        }
        blob.delete();
    }

    @Override
    public String getPublicImageUrl(String fileName) {
        // Tạo URL công khai
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName,
                fileName.replace("/", "%2F"));
    }
}
