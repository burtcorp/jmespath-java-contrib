package io.burt.jmespath.contrib.function;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.JmesPathType;
import io.burt.jmespath.function.ArgumentConstraints;
import io.burt.jmespath.function.FunctionArgument;

public class TokenizeFunction extends RegularExpressionFunction {
  public TokenizeFunction() {
    super(ArgumentConstraints.listOf(1, 3, ArgumentConstraints.typeOf(JmesPathType.STRING)));
  }

  @Override
  protected <T> T callFunction(Adapter<T> runtime, List<FunctionArgument<T>> arguments) {
    List<T> result = new LinkedList<>();
    Pattern pattern = (arguments.size() > 1) ? getPattern(runtime, arguments) : Pattern.compile("\\s+");
    for (String parts: pattern.split(getInputString(runtime, arguments), -1)) {
      if (customPattern(arguments) || !isEmpty(parts)) {
        result.add(runtime.createString(parts));
      }
    }
    return runtime.createArray(result);
  }

  private <T> boolean customPattern(List<FunctionArgument<T>> arguments) {
    return 1 < arguments.size();
  }
}
