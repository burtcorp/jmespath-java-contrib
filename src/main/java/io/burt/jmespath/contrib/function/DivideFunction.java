package io.burt.jmespath.contrib.function;

public class DivideFunction extends MathBiFunction {
    @Override
    protected double performMathOperation(double x, double y) {
        return x / y;
    }
}