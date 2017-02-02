#!/bin/sh

# The build order: (The command line arguments should be in this order as well)
    # platform.dependencies
    # platform.parent
    # platform.spec.model
    # platform.spec.model.test
    # platform.utils
    # platfrom.core.process
   
if [ $# -gt 0 ]; then
    PROJECTS=$1;
else
    PROJECTS=dependencies,parent,spec.model,spec.model.test,utils,core.process;
fi
IFS=',' read -ra NAMES <<< "$PROJECTS"

for i in "${NAMES[@]}"; do
  str="platform."$i;
  echo "####################################################################"
  echo "### Starting to build: $str "
  echo "####################################################################"
  mvn clean install -f $str/pom.xml -D skipTests=true
  echo "####################################################################"
  echo "### Finished building: $str "
  echo "####################################################################"
done