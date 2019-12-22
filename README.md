# Value Object

The value object project is a suite of tools designed to facilitate testing with value objects in Java.

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
