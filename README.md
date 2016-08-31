# one2another-dir-copier
Application monitors given directory and copies all the new/changed files from this directory to another one (with subdirectories).

## Configuration
Rename **integration.properties.sample** to **integration.properties** (default name - another one can be set in application.properties) and fill it with desired input/output paths.  

integration.properties file can be placed on classpath or in the same directory as jar file. The second one has higher priority - it will overwrite values from the one on classpath.

## Installing - Maven
```
mvn clean install
```
## Running
```
java -jar one-2-another-copier-0.0.1-SNAPSHOT.jar
```

## Examples
Assuming input directory: **/dirIn** and output directory: **/dirOut**

* File *z.txt* from path: **/dirIn/z.txt** will be copied to **/dirOut/z.txt** 
* File *x.txt* from path: **/dirIn/dir1/x.txt** will be copied to **/dirOut/dir1/x.txt**

## Author
**Mikołaj Matejko** - m.matejko@gmail.com

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE) file for details
