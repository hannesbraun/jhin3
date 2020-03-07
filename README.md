# Jhin3

Jhin3 is a console soundboard with some additional time tracking utilities. The sounds are configured through a json file. The keyboard is used as input device.

![Jhin3 main screen](preview.png)

## Installation

Note: you need at least Java 8 to run Jhin3.

### Building from source

Requirements:
- JDK 8 or higher
- Maven

1. In your console, navigate to the root directory of this project.
1. Run
   ``` bash
   mvn package
   ```
1.  In the ```target``` directory, you will find your generated jar file named ```jhin3-<version>-jar-with-dependencies.jar```.

## License

Jhin3 is released under the GNU General Public License version 3. For more information see [LICENSE](LICENSE "GPL v3").

## Used libraries

- [Lanterna](https://github.com/mabe02/lanterna)
- [JSON-java](https://github.com/stleary/JSON-java)
- [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)
- [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/)
