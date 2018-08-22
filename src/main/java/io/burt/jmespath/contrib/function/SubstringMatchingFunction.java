package io.burt.jmespath.contrib.function;

import io.burt.jmespath.function.ArgumentConstraint;
import io.burt.jmespath.function.BaseFunction;

abstract class SubstringMatchingFunction extends BaseFunction {
  SubstringMatchingFunction(ArgumentConstraint... argumentConstraints) {
    super(argumentConstraints);
  }

  static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }
}
