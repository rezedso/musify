package com.example.musify.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewDto {
    @NotEmpty(message = "Title must not be empty.")
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters long.")
    private String title;
    @NotEmpty(message = "Content must not be empty.")
    @Size(min = 6, message = "Content must be at least 6 characters long.")
    private String content;
    @DecimalMin(value = "0.5", message = "Rating must be at least 0.5")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
    private Double rating;
}
