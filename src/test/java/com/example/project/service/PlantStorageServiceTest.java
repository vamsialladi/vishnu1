package com.example.project.service;

import com.example.project.exception.ResourceNotFoundException;
import com.example.project.serviceimpl.LocalPlantStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlantStorageServiceTest {

    private LocalPlantStorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        storageService = new LocalPlantStorageServiceImpl();
    }

    @Test
    @DisplayName("Should store and retrieve plant image")
    void shouldStoreAndRetrieveImage() {
        byte[] data = "fake-image-bytes".getBytes();
        String path = storageService.storePlantImage("snake_plant.jpg", data, "image/jpeg");

        assertThat(path).startsWith("/api/v1/plants/images/");
        String key = path.substring("/api/v1/plants/images/".length());

        byte[] retrieved = storageService.getPlantImage(key);
        assertThat(retrieved).isEqualTo(data);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when image not found")
    void shouldThrowWhenImageNotFound() {
        assertThatThrownBy(() -> storageService.getPlantImage("non_existent.jpg"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
