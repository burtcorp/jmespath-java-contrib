package io.burt.jmespath.contrib.function;

public class SubtractFunction extends MathBiFunction {
    @Override
    protected double performMathOperation(double x, double y) {
        return x - y;
    }
}