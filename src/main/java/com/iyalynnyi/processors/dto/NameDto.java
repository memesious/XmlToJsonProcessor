package com.iyalynnyi.processors.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NameDto {
  private String family;
  private List<String> given;
}
