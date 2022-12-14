package com.jimmodel.services.service;

import com.jimmodel.services.exception.FileNotFoundException;
import com.jimmodel.services.exception.ResourceNotFoundException;
import com.jimmodel.services.exception.StorageReadException;
import com.jimmodel.services.exception.StorageWriteException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class StorageServiceImpl implements StorageService{

    @Value(value = "${file-storage.root-dir}")
    private Path ROOT_PATH;

    @Override
    public void save(String dirName, String fileName, MultipartFile file)  {

        File dir = new File(this.ROOT_PATH.resolve(dirName).toString());
        if (!dir.exists()){
            dir.mkdirs();
        }
        try{
            Path fullFilePath =  this.ROOT_PATH.resolve(dirName).resolve(fileName);
            Files.copy(file.getInputStream(), fullFilePath);
        }catch (IOException exception){
            throw new StorageWriteException(exception.getMessage());
        }
    }

    @Override
    public Resource load(String filePath) {
        try{
            Resource resource = new UrlResource(this.ROOT_PATH.resolve(filePath).toUri());
            if (!resource.exists()){
                throw new ResourceNotFoundException(String.format("File %s does not exist", filePath));
            }
            if (!resource.isReadable()){
                throw new StorageReadException(String.format("File %s is not readable", filePath));
            }
            return resource;

        }catch (MalformedURLException exception){
            throw new StorageReadException("Malformed URL occurred");
        }
    }

    @Override
    public void delete(String filePath){
        File file = new File(this.ROOT_PATH.resolve(filePath).toString());
        if(!file.exists() && !file.isFile()){
            throw new FileNotFoundException(String.format("%s does not exist", filePath));
        }
        file.delete();
    }
}
