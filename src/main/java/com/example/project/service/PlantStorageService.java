package com.example.project.service;

public interface PlantStorageService {

    String storePlantImage(String fileName, byte[] data, String contentType);

    byte[] getPlantImage(String fileName);

    void deletePlantImage(String fileName);
}
