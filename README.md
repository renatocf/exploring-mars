# Exploring Mars

An API to explore Mars with Probes.

The following resources are available:

| HTTP Verb | Route                                | Description                      |
|-----------|--------------------------------------|----------------------------------|
| GET       | /maps                                | Get all maps                     |
| POST      | /maps                                | Create new map                   |
| GET       | /maps/:mapId                         | Get a specific map               |
| PUT       | /maps/:mapId                         | Replace a specific map           |
| DELETE    | /maps/:mapId                         | Delete a specific map            |
| GET       | /maps/:mapId/probes                  | Get all probes in a map          |
| POST      | /maps/:mapId/probes                  | Create new probe in a map        |
| GET       | /maps/:mapId/probes/:probeId         | Get a specific probe in a map    |
| DELETE    | /maps/:mapId/probes/:probeId         | Delete a specific probe in a map |
| POST      | /maps/:mapId/probes/:probeId/execute | Execute commands in a probe      |
| GET       | /probes                              | Get all probes                   |
| GET       | /probes/:probeId                     | Get a specific probe             |
| DELETE    | /probes/:probeId                     | Delete a specific probe          |
| POST      | /probes/:probeId/execute             | Execute commands in a probe      |

## Development

You can launch the development environment with docker-compose:
```
docker-compose up
```
To create a development database, just access the route `/database`
(in the standard setup, http://localhost:3000/database) to generate
the MarsDb schema.

## Testing

To run all tests, use docker-compose:
```
docker-compose run --rm api sbt test
```
There are currently **69 unit and integration tests** developed
mostly using test-driven development (TDD).

## Production

In order to run the API in production, build the container
for the corresponding target:
```
API_BUILD_TARGET=production docker-compose build
```
Remember to setup the env variables configured at .env
for your production enviroment.

## Known Issues

Here it is a non-extensive list of known issues found during
the development of the API:
- According to [this question][1] in StackOverflow, Swagger has
  problems with JSON4S Java type serializers, which are necessary
  to marshall UUIDs. This way, it was not possible to auto-generate
  an OpenAPI documentation for the routes.
- In development mode, there are lots of warning because Jetty
  JARs are being loaded from two locations: the project, stored
  at `./target/webapp/WEB-INF/`; and the Ivy2 cache, stored at
  `$HOME/.ivy2/cache/`. The project is configured so that this
  problem is harmless (check the related commit for further
  info). The warning don't happen in the production build
  since it uses a fat standalone JAR to run the application.

[1]: https://stackoverflow.com/questions/55682712
