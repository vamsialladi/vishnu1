package com.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CompanyLocationDto {

    public static class Request {
        @NotNull(message = "Company ID is required")
        private Long companyId;

        @NotBlank(message = "Location name is required")
        private String locationName;

        @NotBlank(message = "Address is required")
        private String address;

        private String city;
        private String state;
        private String zipCode;
        private Double latitude;
        private Double longitude;
        private String locationType;
        private Boolean isIndoor;

        public Request() {}

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public String getLocationName() { return locationName; }
        public void setLocationName(String locationName) { this.locationName = locationName; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }

        public String getLocationType() { return locationType; }
        public void setLocationType(String locationType) { this.locationType = locationType; }

        public Boolean getIsIndoor() { return isIndoor; }
        public void setIsIndoor(Boolean indoor) { isIndoor = indoor; }
    }

    public static class Response {
        private Long id;
        private Long companyId;
        private String locationName;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private Double latitude;
        private Double longitude;
        private String locationType;
        private boolean isIndoor;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public String getLocationName() { return locationName; }
        public void setLocationName(String locationName) { this.locationName = locationName; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }

        public String getLocationType() { return locationType; }
        public void setLocationType(String locationType) { this.locationType = locationType; }

        public boolean isIndoor() { return isIndoor; }
        public void setIndoor(boolean indoor) { isIndoor = indoor; }
    }
}
