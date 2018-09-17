# jmespath-java-contrib

[![Build Status](https://travis-ci.org/burtcorp/jmespath-java-contrib.png?branch=master)](https://travis-ci.org/burtcorp/jmespath-java-contrib)

_If you're reading this on GitHub, please note that this is the readme for the development version and that some features described here might not yet have been released. You can find the readme for a specific version via the release tags ([here is an example](https://github.com/burtcorp/jmespath-java-contrib/releases/tag/0.3.0-SNAPSHOT))._

This is a companion library to [jmespath-java](https://github.com/burtcorp/jmespath-java) with functions and other features not in the JMESPath specification.

## Installation

Using Maven you can add this to your dependencies:

```xml
<dependency>
  <groupId>io.burt</groupId>
  <artifactId>jmespath-contrib</artifactId>
  <version>${jmespath.version}</version>
</dependency>
```

Check the [releases page](https://github.com/burtcorp/jmespath-java-contrib/releases) for the value of `${jmespath.version}`.

## Usage

The functions can be found in the `io.burt.jmespath.contrib` package. To use contributed functions you create a `FunctionRegistry`, register the functions you want to use, create a runtime configuration with the registry, and create a runtime with that configuration:

```java
import com.fasterxml.jackson.databind.JsonNode;

import io.burt.jmespath.JmesPath;
import io.burt.jmespath.function.FunctionRegistry;
import io.burt.jmespath.contrib.function.LowerCaseFunction;
import io.burt.jmespath.contrib.function.UpperCaseFunction;
import io.burt.jmespath.contrib.function.ConcatFunction;
import io.burt.jmespath.jackson.JacksonRuntime;

// There's a default registry that contains the built in JMESPath functions
FunctionRegistry defaultFunctions = FunctionRegistry.defaultRegistry();
// And we can create a new registry with additional functions by extending it
FunctionRegistry customFunctions = defaultFunctions.extend(new LowerCaseFunction(),
                                                           new UpperCaseFunction(),
                                                           new ConcatFunction());
// To configure the runtime with the registry we need to create a configuration
RuntimeConfiguration configuration = new RuntimeConfiguration.Builder()
                                       .withFunctionRegistry(customFunctions)
                                       .build();
// And then create a runtime with the configuration
JmesPath<JsonNode> runtime = new JacksonRuntime(configuration);
// Now the functions are available in expressions
JsonNode result = runtime.compile("concat(lower_case(first_name), ' ', upper_case(last_name))").search(input);
```

## How to build and run the tests

```
$ mvn test
```

And all dependencies should be installed, the code compiled and the tests run.

# Copyright

© 2016-2018 Burt AB and contributors, see LICENSE.txt (BSD 3-Clause).
