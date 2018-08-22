package io.burt.jmespath.contrib.function;

import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonElement;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.RuntimeConfiguration;
import io.burt.jmespath.gson.GsonRuntime;
import io.burt.jmespath.jackson.JacksonRuntime;

@RunWith(Enclosed.class)
public class JmesPathFunctionTest {
  public static class JacksonStringFunctionTest extends JmesPathRuntimeWithStringFunctionTest<JsonNode> {
    @Override
    protected Adapter<JsonNode> createRuntime(RuntimeConfiguration configuration) { return new JacksonRuntime(configuration); }
  }

  public static class GsonStringFunctionTest extends JmesPathRuntimeWithStringFunctionTest<JsonElement> {
    @Override
    protected Adapter<JsonElement> createRuntime(RuntimeConfiguration configuration) { return new GsonRuntime(configuration); }
  }
}
