#!/bin/bash
set -e

echo "#############################"
echo "Installing demo-db data"
echo "#############################"

export PG_PASSWORD=$POSTGRES_PASSWORD

echo "Creating database tstpgdb"
psql --username postgres <<-EOSQL
create database tstpgdb encoding 'UTF-8';
EOSQL

if [ "$TD_EXTRA_USER1_NAME" ]; then
    echo "Setting user $TD_EXTRA_USER1_NAME"
    psql --username postgres <<-EOSQL
CREATE USER $TD_EXTRA_USER1_NAME WITH PASSWORD '$TD_EXTRA_USER1_PWD' ;
EOSQL
fi

if [ "$TD_EXTRA_USER2_NAME" ]; then
    echo "Setting user $TD_EXTRA_USER2_NAME"
    psql --username postgres <<-EOSQL
CREATE USER $TD_EXTRA_USER2_NAME WITH PASSWORD '$TD_EXTRA_USER2_PWD' ;
EOSQL
fi

if [ -f /docker-entrypoint-initdb.d/testdata/testdata.dmp ]; then
   pg_restore --no-owner -d "$POSTGRES_USER" -U "$POSTGRES_USER" /docker-entrypoint-initdb.d/testdata/testdata.dmp
   psql -P pager=off -U "$POSTGRES_USER" -q -n -f /docker-entrypoint-initdb.d/refresh-gebruiker-password.sql
   echo "Testdata restored"

else
   if [ -d /docker-entrypoint-initdb.d ]; then
        echo "Running sql scripts"
        for f in /docker-entrypoint-initdb.d/sql-scripts/*.sql; do
            [ -f "$f" ] && echo $f; psql tstpgdb -P pager=off -U "$POSTGRES_USER" -q -n -f $f
        done
        echo "Finished running sql scripts"
   fi
fi

export PG_PASSWORD=

echo "#############################"
echo "Installing demo-db data complete"
echo "#############################"
