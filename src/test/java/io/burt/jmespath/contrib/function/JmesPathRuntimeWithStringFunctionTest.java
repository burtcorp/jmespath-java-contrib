package io.burt.jmespath.contrib.function;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.RuntimeConfiguration;
import io.burt.jmespath.function.ArgumentTypeException;
import io.burt.jmespath.function.FunctionRegistry;
import io.burt.jmespath.parser.ParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.regex.PatternSyntaxException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public abstract class JmesPathRuntimeWithStringFunctionTest<T> extends JmesPathRuntimeTest<T> {
  private FunctionRegistry functionRegistry = FunctionRegistry.defaultRegistry()
          .extend(new ConcatFunction(),
                  new LowerCaseFunction(),
                  new MatchesFunction(),
                  new NormalizeSpaceFunction(),
                  new ReplaceFunction(),
                  new SubstringAfterFunction(),
                  new SubstringBeforeFunction(),
                  new TokenizeFunction(),
                  new TranslateFunction(),
                  new UpperCaseFunction(),
                  new AddFunction(),
                  new SubtractFunction(),
                  new MultipleFunction(),
                  new DivideFunction());

  private Adapter<T> runtime = createRuntime(RuntimeConfiguration.builder()
          .withFunctionRegistry(functionRegistry)
          .build());

  @Override
  protected Adapter<T> runtime() { return runtime; }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void concatJoinsTheParts() {
    functionRegistry.extend();
    T result1 = check("concat('un', 'grateful')");
    T result2 = check("concat('Ingratitude, ', 'thou ', 'marble-hearted', ' fiend!')");
    assertThat(result1, is(jsonString("ungrateful")));
    assertThat(result2, is(jsonString("Ingratitude, thou marble-hearted fiend!")));
  }

  @Test
  public void concatFiltersOutNullTypes() {
    T result1 = check("concat('Thy ', [], 'old ', `\"groans\"`, \"\", ' ring', ' yet', ' in', ' my', ' ancient',' ears.')");
    T result2 = check("concat('Ciao!',[])");
    assertThat(result1, is(jsonString("Thy old groans ring yet in my ancient ears.")));
    assertThat(result2, is(jsonString("Ciao!")));
  }

  @Test
  public void concatLiterals() {
    T result1 = check("concat(`1`, `2`, `3`, `4`, `true`)");
    T result2 = search("concat(`1`, @)", parse("true"));
    assertThat(result1, is(jsonString("1234true")));
    assertThat(result2, is(jsonString("1true")));
  }

  @Test
  public void concatRequiresAtLeastTwoArguments() {
    thrown.expect(ParseException.class);
    thrown.expectMessage(containsString("invalid arity calling \"concat\" (expected at least 2 but was 1)"));
    check("concat(@)");
  }

  @Test
  public void lowerCaseTranslatesUpperCaseLetter() {
    T result = check("lower_case('ABc!D')");
    assertThat(result, is(jsonString("abc!d")));
  }

  @Test
  public void lowerCaseRequiresASingleArgument() {
    thrown.expect(ParseException.class);
    thrown.expectMessage(containsString("invalid arity calling \"lower_case\" (expected 1 but was 2)"));
    check("lower_case(@, @)");
  }

  @Test
  public void lowerCaseRequiresAStringAsArgument() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was array"));
    search("lower_case(@)", parse("[3]"));
  }

  @Test
  public void upperCaseTranslatesLowerCaseLetter() {
    T result = check("upper_case('abCd0')");
    assertThat(result, is(jsonString("ABCD0")));
  }

  @Test
  public void upperCaseRequiresASingleArgument() {
    thrown.expect(ParseException.class);
    thrown.expectMessage(containsString("invalid arity calling \"upper_case\" (expected 1 but was 2)"));
    check("upper_case(@, @)");
  }

  @Test
  public void upperCaseRequiresAStringAsArgument() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was object"));
    check("upper_case(@)");
  }

  @Test
  public void normalizeSpaceRemovesLeadingWhitespaces() {
    T result = search("normalize_space(str)", parse("{ \"str\" : \"\\n\\t \\tend\"}"));
    assertThat(result, is(jsonString("end")));
  }

  @Test
  public void normalizeSpaceRemovesTrailingWhitespaces() {
    T result = search("normalize_space(str)", parse("{ \"str\" : \"begin\\n \\t \\t\"}"));
    assertThat(result, is(jsonString("begin")));
  }

  @Test
  public void normalizeSpaceCollapseInnerWhitespaces() {
    T result = search("normalize_space(str)", parse("{ \"str\" : \"begin\\n\\t \\tend\"}"));
    assertThat(result, is(jsonString("begin end")));
  }

  @Test
  public void normalizeSpaceRequiresASingleArgument() {
    thrown.expect(ParseException.class);
    thrown.expectMessage(containsString("invalid arity calling \"normalize_space\" (expected 1 but was 2)"));
    check("normalize_space(@, @)");
  }

  @Test
  public void normalizeSpaceRequiresAStringAsArgument() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was object"));
    check("normalize_space(@)");
  }

  @Test
  public void translateReplacesMapStringCharactersWithTheirCorrespondingReplacements() {
    T result = check("translate('bar','abc','ABC')");
    assertThat(result, is(jsonString("BAr")));
  }

  @Test
  public void translateRemovesMapStringCharacterIfNoReplacemntCharacterIsGiven() {
    T result = check("translate('abcabc', 'abc', 'AB')");
    assertThat(result, is(jsonString("ABAB")));
  }

  @Test
  public void translateLeaveIntactCharactersNotInMapString() {
    T result = check("translate('foo.xyz', 'abc', '')");
    assertThat(result, is(jsonString("foo.xyz")));
  }

  @Test
  public void translateUsesFirstOccureneInMapString() {
    T result = check("translate('aaa', 'aaa', 'ABC')");
    assertThat(result, is(jsonString("AAA")));
  }

  @Test
  public void translateIgnoresSuperfluousReplacementCharacter() {
    T result = check("translate('aaa', 'a', 'ABC')");
    assertThat(result, is(jsonString("AAA")));
  }

  @Test
  public void translateRequiresAStringAsFirstArgument() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was object"));
    check("translate(@, 'foo', 'bar')");
  }

  @Test
  public void translateRequiresAStringAsSecondArgument() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was object"));
    check("translate('foo', @, 'bar')");
  }

  @Test
  public void translateRequiresAStringAsThirdArgument() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was object"));
    check("translate('foo', 'bar', @)");
  }

  @Test
  public void translateRequiresThreeArgumentsInsteadOfTwo() {
    thrown.expect(ParseException.class);
    thrown.expectMessage(containsString("invalid arity calling \"translate\" (expected 3 but was 2)"));
    check("translate('foo', 'bar')");
  }

  @Test
  public void translateRequiresThreeArgumentsInsteadOfFour() {
    thrown.expect(ParseException.class);
    thrown.expectMessage(containsString("invalid arity calling \"translate\" (expected 3 but was 4)"));
    check("translate('foo', 'bar', 'baz', 'woo')");
  }

  @Test
  public void translateRequiresAValue1() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("translate(&foo, 'bar', 'baz')");
  }

  @Test
  public void translateRequiresAValue2() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("translate(&foo, 'bar', 'baz')");
  }

  @Test
  public void translateRequiresAValue3() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("translate('foo', 'bar', &foo)");
  }

  @Test
  public void substringAfterExamplesFromXpathSpec() {
    T result1 = check("substring_after('tattoo','tat')");
    T result2 = check("substring_after('tattoo', 'tattoo')");
    T result3 = search("substring_after(@, @)", emptyObject);
    assertThat(result1, is(jsonString("too")));
    assertThat(result2, is(jsonString("")));
    assertThat(result3, is(jsonString("")));
  }

  @Test
  public void substringAfterDoesNotSupportCollation_deviationFromXPathSpec() {
    thrown.expect(ParseException.class);
    thrown.expectMessage(containsString("invalid arity calling \"substring_after\" (expected 2 but was 3)"));
    check("substring_after('abcdefgi','--d-e-', 'http://www.w3.org/2013/collation/UCA?lang=en;alternate=blanked;strength=primary')");
  }

  @Test
  public void substringAfterRequiresAValue1() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected any value but was expression"));
    check("substring_after(&foo, 'bar')");
  }

  @Test
  public void substringAfterRequiresAValue2() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected any value but was expression"));
    check("substring_after('foo', &foo)");
  }

  @Test
  public void substringBeforeExamplesFromXpathSpec() {
    T result1 = check("substring_before('tattoo','attoo')");
    T result2 = check("substring_before('tattoo', 'tatto')");
    T result3 = search("substring_before(@, @)", emptyObject);
    assertThat(result1, is(jsonString("t")));
    assertThat(result2, is(jsonString("")));
    assertThat(result3, is(jsonString("")));
  }

  @Test
  public void substringBeforeDoesNotSupportCollation_deviationFromXPathSpec() {
    thrown.expect(ParseException.class);
    thrown.expectMessage(containsString("invalid arity calling \"substring_before\" (expected 2 but was 3)"));
    check("substring_before('abcdefgi','--d-e-', 'http://www.w3.org/2013/collation/UCA?lang=en;alternate=blanked;strength=primary')");
  }

  @Test
  public void substringBeforeRequiresAValue1() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected any value but was expression"));
    check("substring_before(&foo, 'bar')");
  }

  @Test
  public void substringBeforeRequiresAValue2() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected any value but was expression"));
    check("substring_before('foo', &foo)");
  }

  @Test
  public void matchesExamplesFromXPathSpec() {
    T result1 = check("matches('abracadabra', 'bra')");
    T result2 = check("matches('abracadabra', '^a.*a$')");
    T result3 = check("matches('abracadabra', '^bra')");
    assertThat(result1, is(jsonBoolean(true)));
    assertThat(result2, is(jsonBoolean(true)));
    assertThat(result3, is(jsonBoolean(false)));
  }

  @Test
  public void matchesReturnsTrueCaseInsensitiveWithIFlag() {
    T withFlagI = check("matches('A', 'a', 'i')");
    T otherwise = check("matches('A', 'a')");
    assertThat(withFlagI, is(jsonBoolean(true)));
    assertThat(otherwise, is(jsonBoolean(false)));
  }

  @Test
  public void matchesReturnsTrueIfLiterallyMatchesWithQFlag() {
    T withFlagQ = check("matches('b^az', '^a', 'q')");
    T otherwise = check("matches('b^az', '^a')");
    assertThat(withFlagQ, is(jsonBoolean(true)));
    assertThat(otherwise, is(jsonBoolean(false)));
  }

  @Test
  public void matchesReturnsTrueMultilineWithMFlag() {
    T withFlagM = check("matches('a\nb\nc', '^b$', 'm')");
    T otherwise = check("matches('a\nb\nc', '^b$', '')");
    assertThat(withFlagM, is(jsonBoolean(true)));
    assertThat(otherwise, is(jsonBoolean(false)));
  }

  @Test
  public void matchesMatchesNewLineWithSFlag() {
    T withFlagS = check("matches('a\nb\nc', '.b.', 's')");
    T otherwise = check("matches('a\nb\nc', '.b.')");
    assertThat(withFlagS, is(jsonBoolean(true)));
    assertThat(otherwise, is(jsonBoolean(false)));
  }

  @Test
  public void matchesFlagsCanBeCombined() {
    T withFlagQ = check("matches('b^az', '^A', 'qi')");
    T otherwise = check("matches('b^az', '^a')");
    assertThat(withFlagQ, is(jsonBoolean(true)));
    assertThat(otherwise, is(jsonBoolean(false)));
  }

  @Test
  public void matchesThrowsPatternSyntaxExceptionOnInvalidPattern() {
    thrown.expect(PatternSyntaxException.class);
    check("matches('abba', '?')");
  }

  @Test
  public void matchesThrowsPatternSyntaxExceptionOnZeroMatchingPattern() {
    thrown.expect(PatternSyntaxException.class);
    thrown.expectMessage("pattern matches zero-length string");
    check("matches('abba', '.?')");
  }

  @Test
  public void matchesRequiresAStringValue1() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("matches(&foo, 'bar', 'baz')");
  }

  @Test
  public void matchesRequiresAStringValue2() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("matches('foo', &bar, 'baz')");
  }

  @Test
  public void matchesRequiresAStringValue3() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("matches('foo', 'bar', &baz)");
  }

  @Test
  public void replaceReplacesAllOccurrences() {
    T result1 = check("replace('abracadabra', 'bra', '*')");
    assertThat(result1, is(jsonString("a*cada*")));
  }

  @Test
  public void replaceRemovesMatchingPartsIfReplacementIsEmpty() {
    T result4 = check("replace('abracadabra', 'a', '')");
    assertThat(result4, is(jsonString("brcdbr")));
  }

  @Test
  public void replaceSupportsLazyQualifiers() {
    T most1 = check("replace('abracadabra', 'a.*a', '*')");
    T lazy1 = check("replace('abracadabra', 'a.*?a', '*') ");
    T most2 = check("replace('AAAA', 'A+', 'b')");
    T lazy2 = check("replace('AAAA', 'A+?', 'b')");
    assertThat(most1, is(jsonString("*")));
    assertThat(lazy1, is(jsonString("*c*bra")));
    assertThat(most2, is(jsonString("b")));
    assertThat(lazy2, is(jsonString("bbbb")));
  }

  @Test
  public void replaceCanReferCapturedGroups() {
    T result5 = check("replace('abracadabra', 'a(.)', 'a$1$1')");
    T result8 = check("replace('darted', '^(.*?)d(.*)$', '$1c$2')");
    assertThat(result5, is(jsonString("abbraccaddabbra")));
    assertThat(result8, is(jsonString("carted")));
  }

  @Test
  public void replaceMatchesCaseInsensitiveWithIFlag() {
    T withFlagI = check("replace('*A-b-C*', '[a-z]', '', 'i')");
    T otherwise = check("replace('*A-b-C*', '[a-z]', '')");
    assertThat(withFlagI, is(jsonString("*--*")));
    assertThat(otherwise, is(jsonString("*A--C*")));
  }

  @Test
  public void replaceMatchesLiterallyWithQFlag() {
    T withFlagQ = check("replace('a.b.c.d', '.', '', 'q')");
    T otherwise = check("replace('a.b.c.d', '.', '')");
    assertThat(withFlagQ, is(jsonString("abcd")));
    assertThat(otherwise, is(jsonString("")));
  }

  @Test
  public void replaceMatchesMultilineWithMFlag() {
    T withFlagM = check("replace('a\nb\nc', '^\\w$', '', 'm')");
    T otherwise = check("replace('a\nb\nc', '^\\w$', '')");
    assertThat(withFlagM, is(jsonString("\n\n")));
    assertThat(otherwise, is(jsonString("a\nb\nc")));
  }

  @Test
  public void replaceMatchesNewLineWithSFlag() {
    T withFlagS = check("replace('a\nb\nc', '.b.', '-B-', 's')");
    T otherwise = check("replace('a\nb\nc', '.b.', '-B-')");
    assertThat(withFlagS, is(jsonString("a-B-c")));
    assertThat(otherwise, is(jsonString("a\nb\nc")));
  }

  @Test
  public void replaceFlagsCanBeCombined() {
    T result = check("replace('a\nB\nc', '.b.', '-B-', 'si')");
    assertThat(result, is(jsonString("a-B-c")));
  }

  @Test
  public void replaceThrowsPatternSyntaxExceptionOnInvalidPattern() {
    thrown.expect(PatternSyntaxException.class);
    check("replace('abba', '?', '')");
  }

  @Test
  public void replaceThrowsPatternSyntaxExceptionOnZeroMatchingPattern() {
    thrown.expect(PatternSyntaxException.class);
    thrown.expectMessage("pattern matches zero-length string");
    check("replace('abracadabra', '.*?', '$1')");
  }

  @Test
  public void replaceRequiresAStringValue1() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("replace(&foo, 'bar', 'baz')");
  }

  @Test
  public void replaceRequiresAStringValue2() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("replace('foo', &bar, 'baz')");
  }

  @Test
  public void replaceRequiresAStringValue3() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("replace('foo', 'bar', &baz)");
  }

  @Test
  public void replaceRequiresAStringValue4() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("replace('foo', 'bar', 'baz', &woo)");
  }
  
  @Test
  public void tokenizeWithoutPatternRemovesSurroundingWhitespacesToo() {
    T result1 = check("tokenize(' red green blue ')");
    assertThat(result1, is(jsonArrayOfStrings("red", "green", "blue")));
  }

  @Test
  public void tokenizeWithWhitespacePatternSplitsIntoWords() {
    T result2 = check("tokenize('The cat sat on the mat', '\\s+')");
    assertThat(result2, is(jsonArrayOfStrings("The", "cat", "sat", "on", "the", "mat")));
  }

  @Test
  public void tokenizeWithWhitespacePatternMayProduceEmptyParts() {
    T result3 = check("tokenize(' red green blue ', '\\s+')");
    assertThat(result3, is(jsonArrayOfStrings("", "red", "green", "blue", "")));
  }

  @Test
  public void tokenizeAllowsCapturingButNotReflectedInResult() {
    T result7 = check("tokenize('abracadabra', '(ab)|(a)')");
    assertThat(result7, is(jsonArrayOfStrings("", "r", "c", "d", "r", "")));
  }

  @Test
  public void tokenizeMatchesCaseInsensitiveWithIFlag() {
    T withFlagI = check("tokenize('*A-b-C*', '[a-z]', 'i')");
    T otherwise = check("tokenize('*A-b-C*', '[a-z]')");
    assertThat(withFlagI, is(jsonArrayOfStrings("*", "-", "-", "*")));
    assertThat(otherwise, is(jsonArrayOfStrings("*A-", "-C*")));
  }

  @Test
  public void tokenizeMatchesLiterallyWithQFlag() {
    T withFlagQ = check("tokenize('a.b.c.d', '.', 'q')");
    T otherwise = check("tokenize('a.b.c.d', '.')");
    assertThat(withFlagQ, is(jsonArrayOfStrings("a", "b", "c", "d")));
    assertThat(otherwise, is(jsonArrayOfStrings("", "", "", "", "", "", "", "")));
  }

  @Test
  public void tokenizeMatchesMultilineWithMFlag() {
    T withFlagM = check("tokenize('a\nb\nc', '^\\w$', 'm')");
    T otherwise = check("tokenize('a\nb\nc', '^\\w$')");
    assertThat(withFlagM, is(jsonArrayOfStrings("", "\n", "\n", "")));
    assertThat(otherwise, is(jsonArrayOfStrings("a\nb\nc")));
  }

  @Test
  public void tokenizeMatchesNewLineWithSFlag() {
    T withFlagS = check("tokenize('a\nb\nc', '.b.', 's')");
    T otherwise = check("tokenize('a\nb\nc', '.b.')");
    assertThat(withFlagS, is(jsonArrayOfStrings("a", "c")));
    assertThat(otherwise, is(jsonArrayOfStrings("a\nb\nc")));
  }

  @Test
  public void tokenizeFlagsCanBeCombined() {
    T withFlags = check("tokenize('a\nb\nc', '.B.', 'sim')");
    T otherwise = check("tokenize('a\nb\nc', '.B.')");
    assertThat(withFlags, is(jsonArrayOfStrings("a", "c")));
    assertThat(otherwise, is(jsonArrayOfStrings("a\nb\nc")));
  }

  @Test
  public void tokenizeThrowsPatternSyntaxExceptionOnInvalidPattern() {
    thrown.expect(PatternSyntaxException.class);
    check("tokenize('abba', '?')");
  }

  @Test
  public void tokenizeThrowsPatternSyntaxExceptionOnZeroMatchingPattern() {
    thrown.expect(PatternSyntaxException.class);
    thrown.expectMessage("pattern matches zero-length string");
    check("tokenize('abba', '.?')");
  }

  @Test
  public void tokenizeRequiresAStringValue1() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("tokenize(&foo, 'bar', 'baz')");
  }

  @Test
  public void tokenizeRequiresAStringValue2() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("tokenize('foo', &bar, 'baz')");
  }

  @Test
  public void tokenizeRequiresAStringValue3() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected string but was expression"));
    check("tokenize('foo', 'bar', &baz)");
  }

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
  public void multipleTwoConstants() {
    T result = check("multiple(`5`, `5`)");
    assertThat(result, is(jsonNumber(25)));
  }

  @Test
  public void multipleFieldToConstant() {
    T result = search("multiple(num, `5`)", parse("{ \"num\" : 5}"));
    assertThat(result, is(jsonNumber(25)));
  }

  @Test
  public void multipleTwoFields() {
    T result = search("multiple(num1, num2)", parse("{ \"num1\" : 5, \"num2\" : 5}"));
    assertThat(result, is(jsonNumber(25)));
  }

  @Test
  public void multipleRequiresTwoArgument() {
    thrown.expect(ParseException.class);
    thrown.expectMessage(containsString("invalid arity calling \"multiple\" (expected 2 but was 1)"));
    check("multiple(@)");
  }

  @Test
  public void multipleRequiresNumericArguments() {
    thrown.expect(ArgumentTypeException.class);
    thrown.expectMessage(containsString("expected number but was array"));
    search("multiple(@, `5`)", parse("[3]"));
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
}
