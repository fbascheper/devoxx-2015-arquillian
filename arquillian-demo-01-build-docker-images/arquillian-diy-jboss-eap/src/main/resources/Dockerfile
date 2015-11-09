#######################################################################
# Create a Arquillian demo JBoss image, based on JBoss EAP-6.4.4      #
#######################################################################

# Use the JBoss-EAP 6.4.4 image
FROM fbascheper/redhat-jboss-eap:6.4.4

MAINTAINER fbascheper <temp01@fam-scheper.nl>


############################################
# Copy our init scripts and modules to the provided hooks
############################################

ADD docker-entrypoint-initdb.d  /docker-entrypoint-initdb.d
RUN chown -R jboss:jboss /docker-entrypoint-initdb.d
RUN find /docker-entrypoint-initdb.d -type d -execdir chmod 770 {} \;
RUN find /docker-entrypoint-initdb.d -type f -execdir chmod 660 {} \;

ADD modules  $INSTALLDIR/modules
RUN chown -R jboss:jboss $INSTALLDIR/modules
RUN find $INSTALLDIR/modules -type d -execdir chmod 770 {} \;
RUN find $INSTALLDIR/modules -type f -execdir chmod 660 {} \;

ADD conf $INSTALLDIR/conf
RUN gosu jboss cp $HOME/EAP-6.4.0/conf/standalone.conf $HOME/EAP-6.4.0/jboss-eap-6.4/bin/standalone.conf

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["start-jboss"]
