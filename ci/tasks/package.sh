#!/bin/bash

set -e +x

pushd moviefun
  echo "Packaging JAR"
  mvn clean package -DskipTests
popd

jar_count=`find moviefun/target -type f -name *.jar | wc -l`

if [ $jar_count -gt 1 ]; then
  echo "More than one jar found, don't know which one to deploy. Exiting"
  exit 1
fi

find moviefun/target -type f -name *.jar -exec cp "{}" package-output/moviefun.jar \;

echo "Done packaging"
exit 0
