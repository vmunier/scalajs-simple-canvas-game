<a href="http://www.w3.org/html/logo/">
<img src="https://www.w3.org/html/logo/badge/html5-badge-h-css3-graphics-semantics.png" width="99" height="32" alt="HTML5 Powered with CSS3 / Styling, Graphics, 3D &amp; Effects, and Semantics" title="HTML5 Powered with CSS3 / Styling, Graphics, 3D &amp; Effects, and Semantics"></a>[![Scala.js](https://img.shields.io/badge/scala.js-0.6.10%2B-blue.svg?style=flat)](https://www.scala-js.org)
[![Build Status](https://travis-ci.org/amsterdam-scala/Sjs-Simple-HTML5-canvas-game.svg?branch=master_V2)](https://travis-ci.org/amsterdam-scala/Sjs-Simple-HTML5-canvas-game)

# Simple HTML5 Canvas game ported to Scala.js
**Featuring Scala.js "in browser testing" by ScalaTest 3.x**

A Scala hardcore action game where you possess and play as a Hero :smile:.

## Project
This "Simple HTML5 Canvas Game" is a [Scala.js](https://en.wikipedia.org/wiki/Scala.js) project which targets a browser capable displaying HTML5, especially the `<canvas>` element.
Stored on GitHub.com, due to [sbt](https://en.wikipedia.org/wiki/sbt_(software)) the code is also remote tested on Travis-CI. Also possible on an other continuous integration service.

This quite super simple game is heavily über engineered. It's certainly not the game that counts but the technology around it, it features:

1. [HTML5 Canvas](https://en.wikipedia.org/wiki/Canvas_element) controlled by Scala.js
1. Headless canvas [Selenium 2](https://en.wikipedia.org/wiki/Selenium_(software)) "in browser testing" with the recently released ScalaTest 3.x
1. [ScalaTest 3.x](http://www.scalatest.org) featuring "async" testing styles.
1. Scala 2.12 compiler.
1. Exhaustive use of a variety of Scala features, e.g.:
    * `Traits`, (`case`) `Class`es and `Object`s (singletons)
    * `Future`s sane way to dramatically reduce latency in web requests
    * [Generic[T] objects](https://en.wikipedia.org/wiki/Generic_programming) (even in the frenzied Ough).
    * [Algebraic Data Types](https://en.wikipedia.org/wiki/Algebraic_data_type)
    * [Pattern matching](https://en.wikipedia.org/wiki/Pattern_matching)
    * [Lazy evaluation](https://en.wikipedia.org/wiki/Lazy_evaluation)
1. Reactive design instead of continuous polling.
1. Eliminating a continuously redrawn of the canvas saves cpu time and (mobile) power.
1. Tackling [CORS](https://developer.mozilla.org/en-US/docs/Web/HTML/CORS_enabled_image) enabled images.
1. [Scala generated HTML](http://www.lihaoyi.com/scalatags/).
1. CSS Ribbon
1. [Scala 2.12 fresh Scaladoc look.](https://amsterdam-scala.github.io/Sjs-Simple-HTML5-canvas-game/docs/api/index.html).

## Motivation
Scala.js compile-to-Javascript language is by its compile phase ahead of runtime errors in production. It prevents you of nasty
runtime errors because everything must be ok in the compile phase, specially the types of the functions and variables.

In the original tutorial in Javascript: [How to make a simple HTML5 Canvas game](http://www.lostdecadegames.com/how-to-make-a-simple-html5-canvas-game/),
a continuous redraw of the canvas was made, which is a simple solution, but resource costly.
## Usage
Play the [live demo](http://goo.gl/oqSFCa). Scaladoc you will find [here](https://amsterdam-scala.github.io/Sjs-Simple-HTML5-canvas-game/docs/api/index.html#nl.amsscala.package).
[Installation instructions here](#installation-instructions)

## Architecture
![class diagram](https://raw.githubusercontent.com/amsterdam-scala/Sjs-Simple-HTML5-canvas-game/master/doc/HTML5CanvasGame.png)
#### Discussion:

By the initial call from `SimpleCanvas.main` to `Game.play` its (private) `gameLoop` will periodic started given its `framesPerSec` frequency.
There the status of eventually pressed arrow keys will be tested and per `GameState.keyEffect` converted to a move of the `Hero`.
In an instance of `GameState` the position of the `CanvasComponent`s are immutable recorded. When a change has to be made a new instance will be 
generated with only the changed variables adjusted and leaving the rest unchanged by copying the object.

With the changes in this `CanvasState` a render method of `Page` is only called if the instance is found changed.

The render method repaints the canvas completely. Successively the background, monster and hero will be painted, so the last image is at the foreground.
The images found are the respectively instances of `CanvasComponent` subclasses `Playground`, `Monster` and `Hero`.
They are asynchronously loaded once at startup by means of the use of `Future`s.

In spite of the fact that the application is technically one-tier in an MVC design pattern perspective, everything runs in the browser,
the following parts can be identified:

<table>
  <tr>
    <th>Part</th>
    <th>Class</th>
    <th>Auxiliary</th>
  </tr>
  <tr>
    <td>Model</td>
    <td>GameState</td>
    <td>Position</td>
  </tr>
  <tr>
    <td>View</td>
    <td>Page</td>
    <td>CanvasComponents (Playground, Monster and Hero)</td>
  </tr>
  <tr>
    <td>Controller</td>
    <td>Game</td>
    <td>GameState</td>
  </tr>
</table>

Communication from Game to Page is done by calling with a modified GameState to Page.

#### Unit Testing

Unit testing is done with [ScalaTest 3.x](http://www.scalatest.org/) which is completely detached from the JVM system and Java runtime.
Although running sbt, the test code will be executed in a browser.
This is enabled by a [Selenium environment](https://github.com/scala-js/scala-js-env-selenium) interface direct running to [Firefox](http://www.mozilla.org) and via Chrome Driver to a Google [Chrome browser](https://sites.google.com/a/chromium.org/chromedriver/).

The necessary resources are downloaded from a external server because the test environment lacks a server for this this task.

The test tasks can be invoked by `chrome:test` for the Google Chrome browser and `firefox:test`, separated configs constructed in `InbrowserTesting.scala`.
As proposed by this [article](http://japgolly.blogspot.nl/2016/03/scalajs-firefox-chrome-sbt.html).

Unfortunately at [Travis-CI](travis-ci.org/amsterdam-scala/Sjs-Simple-HTML5-canvas-game) it's not possible to run Google Chrome, so `firefox:test` is the only option.
Also unfortunately `chrome:test` fails on `test 10`, so if executed locally `test 10` must be comment out.
<table>
  <tr>
    <th>Test Class file</th>
    <th>Coverage</th>
    <th>Remarks</th>
  </tr>
  <tr>
    <td><code>CanvasComponentSuite</code></td>
    <td>canvasComponent</td>
    <td><code>Playground</code>, <code>Monster</code> and <code>Hero</code> are concrete classes of <code>CanvasComponent</code></td>
  </tr>
  <tr>
    <td><code>GameStateSuite</code></td>
    <td><code>GameState</code></td>
    <td></td>
  </tr>
  <tr>
    <td><code>GameSuite</code></td>
    <td><code>Game</code></td>
    <td></td>
  </tr>
  <tr>
    <td><code>PageSuite</code></td>
    <td><code>Page</code></td>
    <td></td>
  </tr>
</table>

##### CanvasComponentSuite
This excercises a Hero instance against border limitations.
##### GameStateSuite
E.g. GameState equality.
##### GameSuite
Test the effect of the arrow keys on the Hero moves.
##### PageSuite
This is the most interesting unit test, it features:
* Asynchronous non-blocking testing
* Canvas testing

###### Asynchronous non-blocking testing
ScalaTest 3.x supports asynchronous non-blocking testing by returning a `Future[Assertion]` type. With this the assertion is
postponed as long if the `Future` is not completed.
###### Canvas testing
Because of the difference of processing between various render engines in browsers, is it hard to test the content of a canvas.
One pixel difference becomes immediately a negative test result. However, a couple of techniques can be used by hashing the canvas to
a hash value. The techniques are:
* Exact comparison, only possible if a complete image is rendered in the same sized canvas. In this case no processing (cropping, resizing) is required and therefor not tainted. Only the pixels of original source are used which gives the same result, even in different browsers. Actual it's a property of the source.
* A different hash value per different browser. In this case there are multiple hash values valid, one per browser.
* Tainted canvas. E.g. text on a canvas gives sometimes a slightly different result in pixels by e.g. rounding errors. The only test we can do is to test if the canvas has changed.

## Installation instructions
1. Clone the Github project to a new directory. This is the project directory which become the working directory of current folder.
1. Naturally, at least a Java SE Runtime Environment (JRE) is installed on your platform and has a path to it enables execution.
1. (Optional) Test this by submitting a `java -version` command in a [Command Line Interface (CLI, terminal)](https://en.wikipedia.org/wiki/Command-line_interface). The output should look like this:
```
java version "1.8.0_102"
Java(TM) SE Runtime Environment (build 1.8.0_102-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14, mixed mode)
```
1. Make sure sbt is runnable from almost any work directory, use eventually one of the platform depended installers:
    1. [Installing sbt on Mac](http://www.scala-sbt.org/release/docs/Installing-sbt-on-Mac.html) or
    1. [Installing sbt on Windows](http://www.scala-sbt.org/release/docs/Installing-sbt-on-Windows.html) or
    1. [Installing sbt on Linux](http://www.scala-sbt.org/release/docs/Installing-sbt-on-Linux.html) or
    1. [Manual installation](http://www.scala-sbt.org/release/docs/Manual-Installation.html) (not recommended)
1. (Optional ) To test if sbt is effective submit the `sbt sbtVersion` command. The response could look like as this:
```
[info] Set current project to fransdev (in build file:/C:/Users/FransDev/)
[info] 0.13.12
```
Remember shells (CLI's) are not reactive. To pick up the new [environment variables](https://en.wikipedia.org/wiki/Environment_variable) the CLI must be closed and restarted.
1. Run sbt in one of the next modes in a CLI in the working directory or current folder, a compilation will be started and a local web server will be spinned up using:
    1. Inline mode on the command line: `sbt fastOptJS` or
    1. Interactive mode, start first the sbt by hitting in the CLI `sbt` followed by `fastOptJS` on the sbt prompt, or
    1. Triggered execution by a `~` before the command so `~fastOptJS`. This command will execute and wait after the target code is in time behind the source code (Auto build).
1.  sbt will give a notice that the server is listening by the message: `Bound to localhost/127.0.0.1:12345`
    (Ignore the dead letter notifications with the enter key.)
1. Open this application in a browser on [this given URL](http://localhost:12345/target/scala-2.12/classes/index-dev.html)

When running this way a tool ["workbench"](https://github.com/lihaoyi/workbench) also will be running in the browser, noticeable by opening the console of the browser.

<!-- #### Further Resources  -->
<!-- #### Notes  -->
<!-- #### Considerations -->

#### Licence
Licensed under the EUPL-1.1


```-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Scala                            6             96            113            261


game.js
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
JavaScript                       1             21             16             93

Scala.js minimal project
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
JavaScript                       1             26              1            572

-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
JavaScript                       2            795              1          15423
HTML                             2             13             25             51
CSS                              1             14              0             49
-------------------------------------------------------------------------------
SUM:                             5            822             26          15523
-------------------------------------------------------------------------------
```
