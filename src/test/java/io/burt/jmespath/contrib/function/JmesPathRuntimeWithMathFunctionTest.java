package io.burt.jmespath.contrib.function;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.RuntimeConfiguration;
import io.burt.jmespath.function.ArgumentTypeException;
import io.burt.jmespath.function.FunctionRegistry;
import io.burt.jmespath.parser.ParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public abstract class JmesPathRuntimeWithMathFunctionTest<T> extends JmesPathRuntimeTest<T>{
    private FunctionRegistry functionRegistry = FunctionRegistry.defaultRegistry()
            .extend(
                    new AddFunction(),
                    new SubtractFunction(),
                    new MultiplyFunction(),
                    new DivideFunction());

    private Adapter<T> runtime = createRuntime(RuntimeConfiguration.builder()
            .withFunctionRegistry(functionRegistry)
            .build());

    @Override
    protected Adapter<T> runtime() { return runtime; }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void addTwoConstants() {
        T result = check("add(`5`, `5`)");
        assertThat(result, is(jsonNumber(10)));
    }

    @Test
    public void addFieldToConstant() {
        T result = search("add(num, `5`)", parse("{ \"num\" : 5}"));
        assertThat(result, is(jsonNumber(10)));
    }

    @Test
    public void addTwoFields() {
        T result = search("add(num1, num2)", parse("{ \"num1\" : 5, \"num2\" : 5}"));
        assertThat(result, is(jsonNumber(10)));
    }

    @Test
    public void addRequiresTwoArgument() {
        thrown.expect(ParseException.class);
        thrown.expectMessage(containsString("invalid arity calling \"add\" (expected 2 but was 1)"));
        check("add(@)");
    }

    @Test
    public void addRequiresNumericArguments() {
        thrown.expect(ArgumentTypeException.class);
        thrown.expectMessage(containsString("expected number but was array"));
        search("add(@, `5`)", parse("[3]"));
    }

    @Test
    public void subtractTwoConstants() {
        T result = check("subtract(`5`, `5`)");
        assertThat(result, is(jsonNumber(0)));
    }

    @Test
    public void subtractFieldToConstant() {
        T result = search("subtract(num, `5`)", parse("{ \"num\" : 5}"));
        assertThat(result, is(jsonNumber(0)));
    }

    @Test
    public void subtractTwoFields() {
        T result = search("subtract(num1, num2)", parse("{ \"num1\" : 5, \"num2\" : 5}"));
        assertThat(result, is(jsonNumber(0)));
    }

    @Test
    public void subtractRequiresTwoArgument() {
        thrown.expect(ParseException.class);
        thrown.expectMessage(containsString("invalid arity calling \"subtract\" (expected 2 but was 1)"));
        check("subtract(@)");
    }

    @Test
    public void subtractRequiresNumericArguments() {
        thrown.expect(ArgumentTypeException.class);
        thrown.expectMessage(containsString("expected number but was array"));
        search("subtract(@, `5`)", parse("[3]"));
    }

    @Test
    public void multiplyTwoConstants() {
        T result = check("multiply(`5`, `5`)");
        assertThat(result, is(jsonNumber(25)));
    }

    @Test
    public void multiplyFieldToConstant() {
        T result = search("multiply(num, `5`)", parse("{ \"num\" : 5}"));
        assertThat(result, is(jsonNumber(25)));
    }

    @Test
    public void multiplyTwoFields() {
        T result = search("multiply(num1, num2)", parse("{ \"num1\" : 5, \"num2\" : 5}"));
        assertThat(result, is(jsonNumber(25)));
    }

    @Test
    public void multiplyRequiresTwoArgument() {
        thrown.expect(ParseException.class);
        thrown.expectMessage(containsString("invalid arity calling \"multiply\" (expected 2 but was 1)"));
        check("multiply(@)");
    }

    @Test
    public void multiplyRequiresNumericArguments() {
        thrown.expect(ArgumentTypeException.class);
        thrown.expectMessage(containsString("expected number but was array"));
        search("multiply(@, `5`)", parse("[3]"));
    }

    @Test
    public void divideTwoConstants() {
        T result = check("divide(`5`, `5`)");
        assertThat(result, is(jsonNumber(1)));
    }

    @Test
    public void divideFieldToConstant() {
        T result = search("divide(num, `5`)", parse("{ \"num\" : 5}"));
        assertThat(result, is(jsonNumber(1)));
    }

    @Test
    public void divideTwoFields() {
        T result = search("divide(num1, num2)", parse("{ \"num1\" : 5, \"num2\" : 5}"));
        assertThat(result, is(jsonNumber(1)));
    }

    @Test
    public void divideRequiresTwoArgument() {
        thrown.expect(ParseException.class);
        thrown.expectMessage(containsString("invalid arity calling \"divide\" (expected 2 but was 1)"));
        check("divide(@)");
    }

    @Test
    public void divideRequiresNumericArguments() {
        thrown.expect(ArgumentTypeException.class);
        thrown.expectMessage(containsString("expected number but was array"));
        search("divide(@, `5`)", parse("[3]"));
    }

    @Test
    public void divideByZeroReturnsNull() {
        T result = check("divide(`5`, `0`)");
        assertThat(result, is(jsonNull()));
    }
}
