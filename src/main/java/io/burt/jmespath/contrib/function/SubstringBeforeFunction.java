package io.burt.jmespath.contrib.function;

import java.util.List;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.JmesPathType;
import io.burt.jmespath.function.ArgumentConstraints;
import io.burt.jmespath.function.FunctionArgument;

public class SubstringBeforeFunction extends SubstringMatchingFunction {
  public SubstringBeforeFunction() {
    super(
      ArgumentConstraints.anyValue(),
      ArgumentConstraints.anyValue()
    );
  }

  @Override
  protected <T> T callFunction(Adapter<T> runtime, List<FunctionArgument<T>> arguments) {
    T arg1 = arguments.get(0).value();
    T arg2 = arguments.get(1).value();
    String haystack = runtime.typeOf(arg1) != JmesPathType.NULL ? runtime.toString(arg1) : "";
    String needle = runtime.typeOf(arg2) != JmesPathType.NULL ? runtime.toString(arg2) : "";

    if (isEmpty(haystack) || isEmpty(needle)) {
      return runtime.createString("");
    }
    final int index = haystack.indexOf(needle);
    if (-1 == index) {
      return runtime.createString("");
    } else {
      return runtime.createString(haystack.substring(0, index));
    }
  }
}
