package io.burt.jmespath.contrib.function;

import java.util.Iterator;
import java.util.List;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.JmesPathType;
import io.burt.jmespath.function.ArgumentConstraints;
import io.burt.jmespath.function.BaseFunction;
import io.burt.jmespath.function.FunctionArgument;

public class ConcatFunction extends BaseFunction {
  public ConcatFunction() {
    super(ArgumentConstraints.listOf(2, ArgumentConstraints.anyValue()));
  }

  @Override
  protected <T> T callFunction(Adapter<T> runtime, List<FunctionArgument<T>> arguments) {
    StringBuilder sb = new StringBuilder();
    for (FunctionArgument<T> arg: arguments) {
      T value = arg.value();
      if (runtime.typeOf(value) != JmesPathType.NULL) {
        sb.append(runtime.toString(value));
      }
    }
    return runtime.createString(sb.toString());
  }
}
