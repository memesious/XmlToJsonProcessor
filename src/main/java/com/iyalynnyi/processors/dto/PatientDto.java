package com.iyalynnyi.processors.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
  private String resourceType;

  private String id;
  private List<ExtensionDto> extension;
  private List<IdentifierDto> identifier;
  private List<NameDto> name;
  private String gender;
  private LocalDate birthDate;

}
