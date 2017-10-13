#!/bin/bash

set -e +x

pushd moviefun
  echo "Packaging WAR"
  ./mvnw clean package -DskipTests
popd

jar_count=`find moviefun/target -type f -name *.war | wc -l`

if [ $jar_count -gt 1 ]; then
  echo "More than one war found, don't know which one to deploy. Exiting"
  exit 1
fi

echo "move the file to package output"
find moviefun/target -type f -name *.war -exec cp "{}" package-output/moviefun.war \;

echo "Done packaging"
exit 0
