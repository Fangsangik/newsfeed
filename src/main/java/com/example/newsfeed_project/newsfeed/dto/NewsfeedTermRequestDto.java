package com.example.newsfeed_project.newsfeed.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class NewsfeedTermRequestDto {

  private LocalDate startDateTime;

  private LocalDate endDateTime;

  public NewsfeedTermRequestDto(LocalDate startDateTime, LocalDate endDateTime) {
    this.startDateTime = (startDateTime != null) ? startDateTime : LocalDate.MIN;
    this.endDateTime = (endDateTime != null) ? endDateTime : LocalDate.now();
  }
}
