
## TST JBoss EAP 6.4.3 docker image
This project builds upon the fbascheper/redhat-jboss-eap:6.4.3 image, which contains a basic JBoss EAP 6.4.3 image.

The dockerfile for the base image can be found at GitHub:

       $ git clone https://github.com/fbascheper/JbossEAPDockerFile.git


Before running the build:

1. Install [Docker](https://www.docker.io/gettingstarted/#1)
2. Build the base image

	
Once you have completed steps 1..2 you can build an image using the following command:

		$ mvn clean verify



Before you can run the JBoss EAP container, a PostgreSQL instance must be running, i.e.

        $ docker run --name tst-db-daemon -e POSTGRES_USER=pguser -e POSTGRES_PASSWORD=pgpwd \
            -d fbascheper/tst-postgres-db:1.0-SNAPSHOT


Subsequently you can run the JBoss-EAP container and automatically start an EAP instance with the following command:

		$ docker run --link tst-db-daemon:postgres-tst -P -it --rm \
		    -e PSQL_TD_HOST=db -e PSQL_TD_PORT=5432 -e PSQL_TD_DB=tstpgdb -e PSQL_TD_USER=pguser -e PSQL_TD_PWD=pgpwd \
		    fbascheper/tst-jboss-eap:1.0-SNAPSHOT

Or you can run the container linked to postgres-tst container and start a bash shell or jboss-cli.sh (as user jboss)

       $ docker run --link tst-db-daemon:postgres-tst -P -it --rm \
            -e PSQL_TD_HOST=db -e PSQL_TD_PORT=5432 -e PSQL_TD_DB=tenderned -e PSQL_TD_USER=tenderned -e PSQL_TD_PWD=postgres \
            fbascheper/tst-jboss-eap:1.0-SNAPSHOT bash

       $ docker run --link tst-db-daemon:postgres-tst -P -it --rm \
            -e PSQL_TD_HOST=db -e PSQL_TD_PORT=5432 -e PSQL_TD_DB=tenderned -e PSQL_TD_USER=tenderned -e PSQL_TD_PWD=postgres \
            fbascheper/tst-jboss-eap:1.0-SNAPSHOT jboss-cli.sh -c


