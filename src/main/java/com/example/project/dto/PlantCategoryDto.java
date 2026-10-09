package com.example.project.dto;

import jakarta.validation.constraints.NotBlank;

public class PlantCategoryDto {

    public static class Request {
        @NotBlank(message = "Category name is required")
        private String name;
        private String description;

        public Request() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class Response {
        private Long id;
        private String name;
        private String description;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
