#######################################################################
# Creates the Arquillian demo database image                          #
#######################################################################

# Use the POSTGRES 9.4 base image
FROM postgres:9.4

MAINTAINER fbascheper <temp01@fam-scheper.nl>

ADD docker-entrypoint-initdb.d  /docker-entrypoint-initdb.d
RUN chmod 700 /docker-entrypoint-initdb.d/*.sh

# Install vim-tiny

# RUN apt-get update
# RUN apt-get install -y vim-tiny

ADD /tmp/deb /tmp/deb
RUN dpkg -i /tmp/deb/vim-common_7.3.547-7_amd64.deb
RUN dpkg -i /tmp/deb/vim-tiny_7.3.547-7_amd64.deb

# Unpack dependencies
RUN mkdir -p /docker-entrypoint-initdb.d/sql-scripts
RUN mkdir -p /docker-entrypoint-initdb.d/testdata

# RUN tar -xvzf /docker-entrypoint-initdb.d/dependencies/tst-database-testdata.tar.gz    -C /docker-entrypoint-initdb.d/testdata
RUN cp /docker-entrypoint-initdb.d/dependencies/*.sql                                       /docker-entrypoint-initdb.d/sql-scripts
