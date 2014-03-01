# cmdsnake #

cmdsnake is simple command line snake game. [Lanterna](https://code.google.com/p/lanterna/) library is used to work with command line interface.

To build application:

    mvn clean install

To run the game:

    mvn exec:java -Dexec.mainClass="cmdsnake.Main"

If you are in graphic environment this will start the game in swing-based command line. To start game in actual command line you are using, execute:

    mvn -Djava.awt.headless=true exec:java -Dexec.mainClass="cmdsnake.Main"

To stop and terminate game, press space key.

Player1 keys: a,s,d,w
Player2 keys: arrows
