package com.iyalynnyi.processors.converter;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;

public class Printer extends DefaultPrettyPrinter {

  public Printer() {
    this._arrayIndenter = new DefaultIndenter("  ", "\n");
    this._objectIndenter = new DefaultIndenter("  ", "\n");
  }

  @Override
  public DefaultPrettyPrinter createInstance() {
    return new Printer();
  }

  @Override
  public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
    jg.writeRaw(": ");
  }
}
