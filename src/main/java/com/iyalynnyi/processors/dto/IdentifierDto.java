package com.iyalynnyi.processors.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdentifierDto {
  private String system;
  private String value;
}
