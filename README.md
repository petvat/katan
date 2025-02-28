# Katan

WIP

A mutliplayer *Settlers of Catan* implementation written in Kotlin using LibGDX/KTX. The TCP server is made with the `java.nio` library.

## Building

To build the project, navigate to the project directory and run:

`./gradlew build`

## Running

To start the client application run:

`./gradlew :lwjgl:run`

To start a simple CLI interface run:

`./gradlew :lwjgl:runCli`

To start the server application run:

`./gradlew :server:run`

Toggle logging level with:
`-PloggingLevel=<Level>`

## Platforms

- `core`: Main module with the LibGDX application logic.
- `lwjgl3`: Primary desktop platform using LWJGL3.
- `server`: Non-blocking I/O TCP server.
