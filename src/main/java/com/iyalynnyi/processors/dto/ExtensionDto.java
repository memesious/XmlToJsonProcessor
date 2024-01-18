package com.iyalynnyi.processors.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExtensionDto {
  private String url;
  private ValueCodingDto valueCoding;
}
