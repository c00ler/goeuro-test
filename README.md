# GoEuro Developer Test

Solution to the task described [here](https://github.com/goeuro/dev-test).

# Building project

As soon as project is using [maven wrapper](https://github.com/takari/maven-wrapper) the only requirement is ```Java 8```.

    ./mvnw clean package
    
As the result of the build fat jar named ```GoEuroTest.jar``` will be created.

# Running tests

    ./mvnw clean test
    
Test coverage report can be found in ```target/jacoco/jacoco-ut/index.html```.

# Usage

After building program can be executed using the following command:

    java -jar target/GoEuroTest.jar Berlin

By default results will be written into the file named ```result.csv``` in the current folder. Program
checks if the file with such a name already exists in the current folder. If file already exists program will
not run, to avoid data corruption. Name and location of the file and other settings can be changed in 
```application.properties```. 
