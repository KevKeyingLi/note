- [Build Lifecycle](#Build-Lifecycle)
    * [Build Lifecycle Basics](#Build-Lifecycle-Basics)
        * [A Build Lifecycle is Made Up of Phases](#A-Build-Lifecycle-is-Made-Up-of-Phases)
        * [Usual Command Line Calls](#Usual-Command-Line-Calls)
        * [A Build Phase is Made Up of Plugin Goals](#A-Build-Phase-is-MadeUp-of-Plugin-Goals)
    * [Setting Up Your Project to Use the Build Lifecycle](#Setting-Up-Your-Project-to-Use-the-Build-Lifecycle)
        * [Packaging](#Packaging)

---

### Build Lifecycle
#### Build Lifecycle Basics
* Only necessary to learn a small set of commands to build any Maven project, and the POM will ensure they get the results they desired.
* There are three built-in build lifecycles: `default`, `clean` and `site`. The `default` lifecycle handles your project deployment, the `clean` lifecycle handles project cleaning, while the `site` lifecycle handles the creation of your project's site documentation.

##### A Build Lifecycle is Made Up of Phases
* Each of these build lifecycles is defined by a different list of build phases, wherein a build phase represents a stage in the lifecycle.
    1. `validate`: validate the project is correct and all necessary information is available。
    2. `compile`: compile the source code of the project。
    3. `test`: test the compiled source code using a suitable unit testing framework. These tests should not require the code be packaged or deployed。
    4. `package`: take the compiled code and package it in its distributable format, such as a JAR.
    5. `verify`: run any checks on results of integration tests to ensure quality criteria are met。
    6. `install`: install the package into the local repository, for use as a dependency in other projects locally。
    7. `deploy`: done in the build environment, copies the final package to the remote repository for sharing with other developers and projects.

##### Usual Command Line Calls
* You should select the phase that matches your outcome.
* If you want your jar, run `package`. If you want to run the unit tests, run `test`.
* If you are uncertain what you want, the preferred phase to call is: 
    ```shell
    mvn verify
    ```
    * This command executes each default lifecycle phase in order (`validate`, `compile`, `package`, etc.), before executing verify. 
* In a build environment, use the following call to cleanly build and deploy artifacts into the shared repository.
    ```shell
    mvn clean deploy
    ```

##### A Build Phase is Made Up of Plugin Goals
* However, even though a build phase is responsible for a specific step in the build lifecycle, the manner in which it carries out those responsibilities may vary. And this is done by declaring the plugin goals bound to those build phases.
* A plugin goal represents a specific task (finer than a build phase) which contributes to the building and managing of a project.
* It may be bound to zero or more build phases. A goal not bound to any build phase could be executed outside of the build lifecycle by direct invocation. 
* The order of execution depends on the order in which the goal(s) and the build phase(s) are invoked. 
* For example, consider the command below. The clean and package arguments are build phases, while the dependency:copy-dependencies is a goal (of a plugin).
    ```shell
    mvn clean dependency:copy-dependencies package
    ```
* If a goal is bound to one or more build phases, that goal will be called in all those phases.
* If a build phase has no goals bound to it, that build phase will not execute. But if it has one or more goals bound to it, it will execute all those goals.

#### Setting Up Your Project to Use the Build Lifecycle
##### Packaging
* The first, and most common way, is to set the packaging for your project via the equally named POM element `<packaging>`.



