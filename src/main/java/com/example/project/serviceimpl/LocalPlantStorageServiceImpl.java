package com.example.project.serviceimpl;

import com.example.project.exception.ResourceNotFoundException;
import com.example.project.service.PlantStorageService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalPlantStorageServiceImpl implements PlantStorageService {

    private final Map<String, byte[]> storageMap = new ConcurrentHashMap<>();

    @Override
    public String storePlantImage(String fileName, byte[] data, String contentType) {
        String key = UUID.randomUUID().toString() + "_" + fileName;
        storageMap.put(key, data);
        return "/api/v1/plants/images/" + key;
    }

    @Override
    public byte[] getPlantImage(String fileName) {
        byte[] data = storageMap.get(fileName);
        if (data == null) {
            throw new ResourceNotFoundException("Plant image not found: " + fileName);
        }
        return data;
    }

    @Override
    public void deletePlantImage(String fileName) {
        storageMap.remove(fileName);
    }
}
