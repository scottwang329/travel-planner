Travel planner server

# prerequsites
## install mongodb server on your device
please follow official mongodb tutorial:
https://docs.mongodb.com/manual/installation/#mongodb-community-edition-installation-tutorials

## IDE: 
Recommend using Intellij: https://www.jetbrains.com/idea/download

Follow the installation guide to setup and set Java SDK (Recommend 1.8 version)

After cloning the repo, open the project with Intellij and add maven framework support:

1. Right click root folder (travel-planner-server)
2. Select the second item: Add Framework support
3. In the open window, select Maven option from the left tab and click Ok.

After that, install Lombok plugin:

1. Right click File Tab in your Intellij menu and click Setting (For Mac User click Intellij IDEA icon and select preference)
2. In the left tab, find Plugins and click
3. Search for Lombok and click Install button
4. Restart your Intellij

# Add Google Map API Key
1. Get a Google Map API key: https://developers.google.com/maps/documentation/javascript/get-api-key
2. Enable Place API and Distance Matrix API services.

# Add your Google Map API Key as an environment variable in your run configuration in intellij
Follow official tutorial: https://www.jetbrains.com/help/objc/add-environment-variables-and-program-arguments.html#add-environment-variables

Add "GOOGLE_MAP_API_KEY:{your google map API key}"

# run in your local machine
1. First of all, run your local mongo database in default setting(localhost: 27017). Please refer to previous link guide to run your database: https://docs.mongodb.com/manual/installation/#mongodb-community-edition-installation-tutorials
2. You can run through your IDE (should add your google map api key as environment variable)
3. Run through your terminal under your project root: 
```shell script
// export your Google Map API key first
export GOOGLE_MAP_API_KEY={your google map API key}

// Build your project
mvn install

// Run your project locally
java -jar target/travelplanner_server-0.0.1-SNAPSHOT.jar

// Run your project remotely (in remote machine)
java -jar -Dspring.profiles.active=prod target/travelplanner_server-0.0.1-SNAPSHOT.jar

```

