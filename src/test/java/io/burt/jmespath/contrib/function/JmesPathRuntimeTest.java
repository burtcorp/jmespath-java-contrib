package io.burt.jmespath.contrib.function;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPathType;
import io.burt.jmespath.RuntimeConfiguration;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;

import java.util.List;

public abstract class JmesPathRuntimeTest<T> {
    protected abstract Adapter<T> runtime();

    protected abstract Adapter<T> createRuntime(RuntimeConfiguration configuration);

    protected T emptyObject;

    @Before
    public void setUp() {
      emptyObject = parse("{}");
    }

    protected T check(String query) {
      return search(query, emptyObject);
    }

    protected T search(String query, T input) {
      Expression<T> expression = runtime().compile(query);
      return expression.search(input);
    }

    protected T parse(String json) {
      return runtime().parseString(json);
    }

    protected Matcher<T> jsonBoolean(final boolean b) {
      return new BaseMatcher<T>() {
        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(final Object n) {
          T node = (T) n;
          return runtime().typeOf(node) == JmesPathType.BOOLEAN && runtime().isTruthy(node) == b;
        }

        @Override
        public void describeTo(Description description) {
          description.appendText("JSON boolean with value ").appendValue(b);
        }
      };
    }

    protected Matcher<T> jsonNumber(final Number e) {
      return new BaseMatcher<T>() {
        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(final Object n) {
          T actual = (T) n;
          T expected = runtime().createNumber(e.doubleValue());
          return runtime().typeOf(actual) == JmesPathType.NUMBER && runtime().compare(actual, expected) == 0;
        }

        @Override
        public void describeTo(Description description) {
          description.appendText("JSON number with value ").appendValue(e);
        }
      };
    }

    protected Matcher<T> jsonNull() {
      return new BaseMatcher<T>() {
        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(final Object n) {
          T node = (T) n;
          return runtime().typeOf(node) == JmesPathType.NULL;
        }

        @Override
        public void describeTo(Description description) {
          description.appendText("JSON null");
        }
      };
    }

    protected Matcher<T> jsonString(final String str) {
      return new BaseMatcher<T>() {
        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(final Object n) {
          T node = (T) n;
          return runtime().createString(str).equals(node);
        }

        @Override
        public void describeTo(Description description) {
          description.appendText("JSON string with value ").appendValue(str);
        }
      };
    }

    protected Matcher<T> jsonArrayOfStrings(final String... strs) {
      return new BaseMatcher<T>() {
        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(final Object n) {
          List<T> input = runtime().toList((T) n);
          if (input.size() != strs.length) {
            return false;
          }
          for (int i = 0; i < strs.length; i++) {
            if (!runtime().toString(input.get(i)).equals(strs[i])) {
              return false;
            }
          }
          return true;
        }

        @Override
        public void describeTo(Description description) {
          description.appendText("JSON array ").appendValue(strs);
        }
      };
    }
}
