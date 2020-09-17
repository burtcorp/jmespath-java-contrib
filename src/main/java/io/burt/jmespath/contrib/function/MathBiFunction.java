package io.burt.jmespath.contrib.function;

import java.util.List;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.JmesPathType;
import io.burt.jmespath.function.ArgumentConstraints;
import io.burt.jmespath.function.BaseFunction;
import io.burt.jmespath.function.FunctionArgument;

/**
 * Helper base class for binary functions that perform operations on a two numerical
 * arguments, like calculating addition, division, multiplication, etc.
 */
public abstract class MathBiFunction extends BaseFunction {
  public MathBiFunction() {
    super(
        ArgumentConstraints.typeOf(JmesPathType.NUMBER),
        ArgumentConstraints.typeOf(JmesPathType.NUMBER)
    );
  }

  @Override
  protected <T> T callFunction(Adapter<T> runtime, List<FunctionArgument<T>> arguments) {
    T valueX = arguments.get(0).value();
    T valueY = arguments.get(1).value();
    double x = runtime.toNumber(valueX).doubleValue();
    double y = runtime.toNumber(valueY).doubleValue();
    double result = performMathOperation(x, y);
    if (!Double.isInfinite(result)) {
      return runtime.createNumber(result);
    } else {
      return runtime.createNull();
    }
  }

  /**
   * Subclasses implement this method.
   */
  protected abstract double performMathOperation(double x, double y);
}
