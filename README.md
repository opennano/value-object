# Reflection Assert

Reflection Assert is a lightweight Java testing library that performs deep comparisons of objects via field reflection.

## Features

* Performs a simple field-based comparison of nearly any two objects
* Supports lenient comparisons, such as ignoring date/time differences, collection order, and default values
* If found, a user-friendly summary of differences is reported in the assertion exception message
* Works even on objects that have a cycle in their object graph and has no inherent limit on field depth
* Uses difference caching to dramatically speed up the comparison process
* Zero transitive dependencies for painless integration into any project
* Compatible with Java 8+

## Why use this?

Because eventually you are going to maintain or author a test like this:

```
@Test 
public void myTest() {
  ...
  
  assertEquals(expected.getFieldA(), actual.getFieldA());
  assertEquals(expected.getFieldB().getField1(), actual.getFieldB().getField1());
  assertEquals(expected.getFieldB().getField2(), actual.getFieldB().getField2());
  assertEquals(expected.getFieldC(), actual.getFieldC());
  
  // et cetera ad nauseum
}
```
But it is a lot quicker to write and easier to maintain such a test as a single assertion:

```
@Test 
public void myTest() {
  ...
  
  assertReflectionEquals(expected, actual);
}
```

It's worth noting that there are already mature Java testing libraries available that support this kind of comparison, but none that work without pulling in many other dependencies ([Unitils](http://www.unitils.org)), or the need to write tests in a different language ([Spock](http://spockframework.org/)). Still other generic object comparison libraries exists, but are not built for testing per se.

## Getting Started

To learn how to use this library see the [User Guide](https://github.com/opennano/reflection-assert/wiki/User-Guide).

### Maven

To include this project on your Maven classpath, add this element to the dependencies element in your pom file:

```xml
<dependency>
  <groupId>com.github.opennano</groupId>
  <artifactId>reflection-assert</artifactId>
  <version>${reflection-assert.version}</version>
  <scope>test</scope>
</dependency>
```

Check [Maven Central](https://search.maven.org/search?q=a:reflection-assert) for the latest release version of this library.

## Contributing
Before starting, make sure the feature or fix you are contributing is first accepted as a valid issue. If no such issue exists, first create one [here](https://github.com/opennano/reflection-assert/issues).

Do plan on spending a good amount of time testing your changes, as all code must be 100% path covered with unit tests, and must include scenario tests (*IT.java) to regression-proof newly supported or updated use cases.

* [Create a fork](https://help.github.com/en/github/getting-started-with-github/fork-a-repo) of this repo
* Create a feature branch off master: `git checkout -b issue-123`
* Commit your changes: `git commit -am 'add new feature'`
* Push your changes to the remote repository: `git push origin issue-123`
* Submit a [pull request](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request) to merge your fork's branch back to master

### Formatter
Use the [Google Code Formatter](https://github.com/google/google-java-format) and [Eclipse defaults](https://stackoverflow.com/questions/14716283/is-it-possible-for-intellij-to-organize-imports-the-same-way-as-in-eclipse) for organizing imports. For json and other files, use the formatter as a guide (i.e. use a two-space indent).

### Testing
Start testing by writing scenario tests first. There are no coverage requirements at this level--these tests provide regression proofing for future changes, so just cover the use cases you are adding or changing, typically by thoroughly testing all happy paths but negative tests and edge cases only as needed.

Once you are satisfied with scenario coverage you should start writing unit tests. Unit tests should guarantee that each class is behaving as intended in isolation, which basically means we should cover all paths through the code and additionally provide test cases that could expose a problem. Mutation testing must catch at least 95% of bugs for the build to succeed, so you will need to be thorough. Use the existing tests as a guide.

### Review
Once the build is passing locally, create a pull request to master. Code review concerns and comments must be addressed before the PR will be merged. Once merged, your code will be included in the next release.

## Authors
* **[opennano](https://github.com/opennano)** - *Initial work*

See also the list of [contributors](https://github.com/opennano/reflection-assert/contributors) who participated in this project.

## License
This is free and unencumbered software released into the public domain - see the [LICENSE](LICENSE) file for details.

## Acknowledgments
* [Unitils](http://www.unitils.org), an excellent but more heavyweight alternative. This library borrows from their simple and intuitive API and solves for many of the same use cases.
