package com.example.project.service;

import com.example.project.dto.PlantTrackingDto;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.serviceimpl.PlantTrackingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantTrackingServiceTest {

    @Mock
    private PlantInstanceRepository instanceRepository;

    @Mock
    private PlantHealthLogRepository healthLogRepository;

    @Mock
    private WateringRecordRepository wateringRepository;

    @Mock
    private FertilizerRecordRepository fertilizerRepository;

    @Mock
    private PlantReplacementRepository replacementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyLocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlantTrackingServiceImpl plantTrackingService;

    private User worker;
    private Company company;
    private Product product;
    private PlantInstance instance;

    @BeforeEach
    void setUp() {
        worker = new User("worker1", "worker@example.com", "pass", "Worker Bob", Role.ROLE_WORKER);
        worker.setId(3L);

        User owner = new User("owner1", "owner@example.com", "pass", "Owner Jane", Role.ROLE_CUSTOMER);
        owner.setId(2L);

        company = new Company("Acme Green", "REG-123", "owner@example.com", owner);
        company.setId(10L);

        product = new Product("Areca Palm", "Air purifier", BigDecimal.valueOf(800), 10, "Indoor", "SKU-ARECA-01");
        product.setId(20L);

        instance = new PlantInstance("PLANT-1234", "QR-1234", product, company, null, null, LocalDate.now());
        instance.setId(50L);
        instance.setHealthStatus(PlantInstance.HealthStatus.HEALTHY);
    }

    @Test
    @DisplayName("Should find plant by QR code")
    void shouldFindPlantByQrCode() {
        when(instanceRepository.findByQrCode("QR-1234")).thenReturn(Optional.of(instance));

        PlantTrackingDto.InstanceResponse resp = plantTrackingService.getPlantByQrCode("QR-1234");

        assertThat(resp).isNotNull();
        assertThat(resp.getQrCode()).isEqualTo("QR-1234");
        assertThat(resp.getPlantIdentifier()).isEqualTo("PLANT-1234");
    }

    @Test
    @DisplayName("Should record plant health update and log history")
    void shouldRecordHealth() {
        when(userRepository.findByEmail("worker@example.com")).thenReturn(Optional.of(worker));
        when(instanceRepository.findById(50L)).thenReturn(Optional.of(instance));

        PlantTrackingDto.HealthLogRequest request = new PlantTrackingDto.HealthLogRequest();
        request.setHealthStatus("NEEDS_ATTENTION");
        request.setGeneralNotes("Slight yellowing of leaves");

        plantTrackingService.recordHealth(50L, request, "worker@example.com");

        assertThat(instance.getHealthStatus()).isEqualTo(PlantInstance.HealthStatus.NEEDS_ATTENTION);
        verify(instanceRepository, times(1)).save(instance);
        verify(healthLogRepository, times(1)).save(any(PlantHealthLog.class));
    }

    @Test
    @DisplayName("Should record watering event")
    void shouldRecordWatering() {
        when(userRepository.findByEmail("worker@example.com")).thenReturn(Optional.of(worker));
        when(instanceRepository.findById(50L)).thenReturn(Optional.of(instance));

        PlantTrackingDto.WateringRequest request = new PlantTrackingDto.WateringRequest();
        request.setAmountLiters(1.5);
        request.setNotes("Regular watering");

        plantTrackingService.recordWatering(50L, request, "worker@example.com");

        verify(instanceRepository, times(1)).save(instance);
        verify(wateringRepository, times(1)).save(any(WateringRecord.class));
    }

    @Test
    @DisplayName("Should replace plant without deleting old record and create new plant instance")
    void shouldReplacePlantSafely() {
        when(userRepository.findByEmail("worker@example.com")).thenReturn(Optional.of(worker));
        when(instanceRepository.findById(50L)).thenReturn(Optional.of(instance));

        Product newProduct = new Product("Monstera Deliciosa", "Cheese plant", BigDecimal.valueOf(1200), 5, "Indoor", "SKU-MONSTERA-01");
        newProduct.setId(25L);
        when(productRepository.findById(25L)).thenReturn(Optional.of(newProduct));

        PlantInstance newInstance = new PlantInstance("PLANT-NEW", "QR-NEW", newProduct, company, null, null, LocalDate.now());
        newInstance.setId(51L);
        when(instanceRepository.save(any(PlantInstance.class))).thenReturn(newInstance);

        PlantTrackingDto.ReplacementRequest request = new PlantTrackingDto.ReplacementRequest();
        request.setNewProductId(25L);
        request.setReason("Root rot");
        request.setReplacementCost(BigDecimal.valueOf(500));
        request.setCoveredByPlan(true);

        PlantTrackingDto.InstanceResponse resp = plantTrackingService.replacePlant(50L, request, "worker@example.com");

        assertThat(instance.getHealthStatus()).isEqualTo(PlantInstance.HealthStatus.REPLACED);
        verify(replacementRepository, times(1)).save(any(PlantReplacement.class));
        verify(instanceRepository, never()).delete(any());
        verify(instanceRepository, never()).deleteById(anyLong());
        assertThat(resp).isNotNull();
    }
}
