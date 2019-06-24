###############################################################################
#                               DEVELOPMENT IMAGE                              #
################################################################################

# Base image with Scala + SBT for development
FROM spikerlabs/scala-sbt:scala-2.12.7-sbt-1.2.6 AS development

# Create working dir
WORKDIR /usr/src/api

# Define base environment variables for the api
ENV HOST=0.0.0.0 \
    PORT=8080

# Expose default port to connect with the service
EXPOSE $PORT

# Copy build.sbt and project/ to install dependencies
COPY project ./project/
COPY build.sbt ./

# Install dependencies
RUN sbt clean update

# Copy the application code
ADD . .

# Compile code
RUN sbt compile

# Run SBT console for development
CMD [ "sbt", "~;jetty:stop;jetty:start" ]

################################################################################
#                                  TEST IMAGE                                  #
################################################################################

# Use development image to run test
FROM development AS test

# Compile tests
RUN sbt test:compile

# Run SBT test to verify image
CMD [ "sbt", "test" ]

################################################################################
#                                BUILDER IMAGE                                 #
################################################################################

# Use development image to generate deployment artifacts
FROM development AS builder

# Generate standalone JAR
RUN sbt clean assembly

################################################################################
#                               PRODUCTION IMAGE                               #
################################################################################

# Use image with OpenJRE on Alpine Linux for production
FROM openjdk:12-alpine3.9 AS production

# Create working dir
WORKDIR /usr/src/api

# Copy JAR from the builder container
COPY --from=builder /usr/src/api/target/scala-*/*.jar .

# Define default command to execute when running the container
CMD java -jar ExploringMars-assembly-*.jar
