#!/bin/bash

mvn clean install -Darquillian=remote
mvn clean install -Darquillian=managed
