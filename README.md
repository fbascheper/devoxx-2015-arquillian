# Demo code for Devoxx session about Arquillian, Docker and Maven plugin

This is a simple setup building two docker containers, 

 * PostgreSQL
 * JBoss EAP

Prerequisites:

 1. JDK 8, preferably the latest build, currently 1.8.0_60
 2. Maven 3.3.x
 3. Docker / Boot2Docker, preferably the latest build, currently client / server 1.9.0
 
This test can be run by

    git clone https://github.com/fbascheper/devoxx-2015-arquillian
    cd devoxx-2015-arquillian
    mvn clean verify -Darquillian=remote

If you want to run it on a local JBoss EAP instance, first configure a datasource using

    export PSQL_HOST=localhost
    export PSQL_PORT=5432
    export PSQL_TST_DB=testdb
    export PSQL_TST_USER=testdb
    export PSQL_TST_PWD=testdb

    bin/jboss-cli.sh -c << EOCLI
        batch

    # Add Postgres driver
    /subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql.jdbc42,module-slot=main,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)

    # Add the postgresTestDS datasource
    data-source add --name=postgresTestDS --driver-name=postgresql --jndi-name=java:jboss/datasources/postgresTestDS --connection-url=jdbc:postgresql://$PSQL_HOST:$PSQL_PORT/$PSQL_TST_DB --user-name=$PSQL_TST_USER --password=$PSQL_TST_PWD --new-connection-sql="select 1;" --use-ccm=false --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true

    # Execute the batch
    run-batch
    EOCLI
