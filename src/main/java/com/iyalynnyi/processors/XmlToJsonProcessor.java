package com.iyalynnyi.processors;

import static com.iyalynnyi.processors.converter.XmlToJsonConverter.convertXmlToJson;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Tags({"Xml", "Json"})
@CapabilityDescription("Converts xml with patients to Json according to given rules")
public class XmlToJsonProcessor extends AbstractProcessor {

  public static final Relationship SUCCESS = new Relationship.Builder()
      .name("success")
      .description("Successfully converted XML to JSON")
      .build();

  public static final Relationship FAILURE = new Relationship.Builder()
      .name("failure")
      .description("Failed to convert XML to JSON")
      .build();

  private List<PropertyDescriptor> properties;
  private Set<Relationship> relationships;

  @Override
  protected void init(ProcessorInitializationContext context) {
    this.properties = new ArrayList<>();

    this.relationships = Set.of(SUCCESS, FAILURE);
  }

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return properties;
  }

  @Override
  public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
    FlowFile flowFile = session.get();
    if (flowFile == null) {
      return;
    }

    String xmlData;
    try (InputStream read = session.read(flowFile)) {
      xmlData = new String(read.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      getLogger().error("Failed to read XML data from FlowFile: " + e.getMessage(), e);
      session.transfer(flowFile, FAILURE);
      return;
    }

    String jsonData;
    try {
      jsonData = convertXmlToJson(xmlData);
    } catch (JsonProcessingException e) {
      session.transfer(flowFile, FAILURE);
      return;
    }

    //to use it in lambda
    String finalJsonData = jsonData;
    FlowFile finalFlowFile = flowFile;

    flowFile = session.write(flowFile, out -> {
      try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
        writer.write(finalJsonData);
      } catch (IOException e) {
        getLogger().error("Failed to write JSON data to FlowFile: " + e.getMessage(), e);
        session.transfer(finalFlowFile, FAILURE);
      }
    });

    session.transfer(flowFile, SUCCESS);
  }

}
