package com.iyalynnyi.processors.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.iyalynnyi.processors.dto.ExtensionDto;
import com.iyalynnyi.processors.dto.IdentifierDto;
import com.iyalynnyi.processors.dto.PatientDto;
import com.iyalynnyi.processors.dto.ValueCodingDto;

import com.iyalynnyi.processors.dto.NameDto;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class XmlToJsonConverter {

  private static final ObjectMapper objectMapper;
  private static final XmlMapper xmlMapper;
  private static final DefaultIndenter DEFAULT_INDENTER = new DefaultIndenter();
  private static final Printer printer;
  public static final String BIRTH_SEX_EXTENSION = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-birthsex";
  public static final String ADMINISTRATIVE_GENDER = "http://terminology.hl7.org/CodeSystem/v3-AdministrativeGender";

  static {
    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
    xmlMapper = new XmlMapper();
    printer = new Printer();
  }

  public static String convertXmlToJson(String xmlString) throws JsonProcessingException {

    List<PatientDto> result = new ArrayList<>();

    JsonNode jsonNode = xmlMapper.readTree(xmlString);

    JsonNode member = jsonNode.get("member");

    processJsonNode(i -> result.add(process(i)), member);

    printer.indentArraysWith(DEFAULT_INDENTER);
    return objectMapper.writer(printer).writeValueAsString(result);
  }

  private static PatientDto process(JsonNode member) {
    JsonNode nameNode = member.get("names");

    List<NameDto> nameDtos = new ArrayList<>();

    processJsonNode(name -> {
      List<String> givenNames = new ArrayList<>();
      processJsonNode(given -> givenNames.add(given.asText()), name.get("firstName"));
      nameDtos.add(new NameDto(name.get("lastName").asText(), givenNames));
    }, nameNode.get("name"));

    return PatientDto.builder()
        .resourceType("Patient")
        .id(member.get("unique_record_identifier").asText())
        .extension(List.of(
            new ExtensionDto(BIRTH_SEX_EXTENSION,
                new ValueCodingDto(ADMINISTRATIVE_GENDER,
                    member.get("us_core_birth_sex").asText())
            )))
        .identifier(List.of(new IdentifierDto(member.get("member_id_system").asText(), member.get("member_id").asText())))
        .name(nameDtos)
        .gender(member.get("gender").asText())
        .birthDate(LocalDate.parse(member.get("birth_date").asText(), DateTimeFormatter.ofPattern("u-M-d")))
        .build();
  }

  private static void processJsonNode(Consumer<JsonNode> consumer, JsonNode jsonNode) {
    if (jsonNode.isArray()) {
      jsonNode.forEach(consumer);
    } else consumer.accept(jsonNode);
  }
}
