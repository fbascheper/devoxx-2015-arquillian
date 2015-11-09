#!/bin/bash

JBOSS_HOME_DIR=$HOME/EAP-6.4.0/jboss-eap-6.4


echo "=> Executing the commands"
echo "=> PSQL_HOST:           " $PSQL_HOST
echo "=> PSQL_PORT:           " $PSQL_PORT

echo "=> PSQL_TST_USER:        " $PSQL_TST_USER
echo "=> PSQL_TST_PWD:         " $PSQL_TST_PWD
echo "=> PSQL_TST_DB:          " $PSQL_TST_DB

#$JBOSS_CLI -c --file=`dirname "$0"`/commands.cli

gosu jboss mkdir $HOME/logs
gosu jboss chmod 770 $HOME/logs


gosu jboss $JBOSS_CLI -c << EOCLI
    batch

    # Add Postgres driver
    /subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql.jdbc42,module-slot=main,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)

    # Add the postgresTestDS datasource
    data-source add --name=postgresTestDS --driver-name=postgresql --jndi-name=java:jboss/datasources/postgresTestDS --connection-url=jdbc:postgresql://$PSQL_HOST:$PSQL_PORT/$PSQL_TST_DB --user-name=$PSQL_TST_USER --password=$PSQL_TST_PWD --new-connection-sql="select 1;" --use-ccm=false --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true

    # Add infinispan cache
    /subsystem=infinispan/cache-container=td-cache/:add(default-cache=resultset)
    /subsystem=infinispan/cache-container=td-cache/local-cache=resultset:add(batching=true)
    /subsystem=infinispan/cache-container=td-cache/local-cache=resultset/locking=LOCKING:add(isolation=READ_COMMITTED)
    /subsystem=infinispan/cache-container=td-cache/local-cache=resultset/expiration=EXPIRATION:add(lifespan=7200000)
    /subsystem=infinispan/cache-container=td-cache/local-cache=resultset/eviction=EVICTION:add(max-entries=1024, strategy=LIRS)
    /subsystem=infinispan/cache-container=td-cache/local-cache=resultset/transaction=TRANSACTION:add(mode=FULL_XA)
    /subsystem=infinispan/cache-container=td-cache:write-attribute(name=statistics-enabled,value=true)

    /subsystem=infinispan/cache-container=td-cache/local-cache=preparedplan:add(batching=true)
    /subsystem=infinispan/cache-container=td-cache/local-cache=preparedplan/locking=LOCKING:add(isolation=READ_COMMITTED)
    /subsystem=infinispan/cache-container=td-cache/local-cache=preparedplan/expiration=EXPIRATION:add(lifespan=28800)
    /subsystem=infinispan/cache-container=td-cache/local-cache=preparedplan/eviction=EVICTION:add(max-entries=512, strategy=LIRS)
    /subsystem=infinispan/cache-container=td-cache/local-cache=preparedplan/transaction=TRANSACTION:add(mode=FULL_XA)

    # Disable JCA validation for jackrabbit jcr adapter (see https://issues.apache.org/jira/browse/JCR-3241 )
    /subsystem=jca/archive-validation=archive-validation:write-attribute(name=enabled, value=false

    # enable gzip compression
    /system-property=org.apache.coyote.http11.Http11Protocol.COMPRESSION:add(value=on)
    /system-property=org.apache.coyote.http11.Http11Protocol.COMPRESSION_MIME_TYPES:add(value="text/html,text/xml,text/plain,text/css,text/javascript")

    # Execute the batch
    run-batch

EOCLI


#
# Setup logging
#
gosu jboss $JBOSS_CLI -c << EOCLI
    batch

    # Reconfigure logging
    # /subsystem=logging/root-logger=ROOT:read-resource
    # /subsystem=logging/console-handler=CONSOLE:read-resource
    # /subsystem=logging/periodic-rotating-file-handler=FILE:read-resource
    # /subsystem=logging/console-handler=CONSOLE:write-attribute(name=filter-spec,value="any(levelRange[WARN,FATAL] , match(\"JBAS\"))")

    /subsystem=logging/root-logger=ROOT:root-logger-unassign-handler(name="CONSOLE")
    /subsystem=logging/console-handler=CONSOLE:remove

    /subsystem=logging/periodic-rotating-file-handler=FILE:write-attribute(name=level,value="WARN")

    /subsystem=logging/console-handler=TST-consoleHandler:add(level="DEBUG", name="TST-console", formatter="%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n")
    /subsystem=logging/console-handler=TST-consoleHandler:write-attribute(name="encoding", value="UTF-8")

    # Set log levels for our application
    /subsystem=logging/logger=nl.famscheper:add(level="DEBUG", use-parent-handlers="false")

    # Set log levels for Infinispan
    /subsystem=logging/logger=org.infinispan:add(level="INFO", use-parent-handlers="false")
    /subsystem=logging/logger=org.jboss.as.clustering.infinispan:add(level="INFO", filter-spec="levelChange(DEBUG)")

    # Set log levels for Hibernate
    /subsystem=logging/logger=org.hibernate:add(level="INFO", use-parent-handlers="false")
    /subsystem=logging/logger=org.hibernate.SQL:add(level="DEBUG")
    /subsystem=logging/logger=org.hibernate.type.descriptor.sql.BasicBinder:add(level="TRACE", filter-spec="levelChange(DEBUG)")
    /subsystem=logging/logger=org.hibernate.type.descriptor.sql.BasicExtractor:add(level="TRACE")

    # Do not log all EJB deployments
    /subsystem=logging/logger=org.jboss.as.ejb3.deployment.processors.EjbJndiBindingsDeploymentUnitProcessor:add(level="WARN")

    # Execute the batch
    run-batch
EOCLI


echo "Assign log handlers outside batch (https://bugzilla.redhat.com/show_bug.cgi?id=1017881)"
gosu jboss $JBOSS_CLI -c << EOCLI
    /subsystem=logging/root-logger=ROOT:root-logger-assign-handler(name="TST-consoleHandler")
    /subsystem=logging/logger=nl.famscheper:assign-handler(name="TST-consoleHandler")
    /subsystem=logging/logger=org.infinispan:assign-handler(name="TST-consoleHandler")
    /subsystem=logging/logger=org.hibernate:assign-handler(name="TST-consoleHandler")
EOCLI

echo "Completed TST JBoss configuration"

