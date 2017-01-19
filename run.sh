#!/bin/bash
DEFAULT_HADOOP_HOME=/opt/grakn/hadoop-2.6.0 #change to your hadoop folder

SCRIPTPATH=`cd "$(dirname "$0")" && pwd -P`
DEFAULT_LDBC_SNB_DATAGEN_HOME=$SCRIPTPATH #change to your ldbc_socialnet_dbgen folder

# allow overriding configuration from outside via environment variables
# i.e. you can do
#     HADOOP_HOME=/foo/bar LDBC_SNB_DATAGEN_HOME=/baz/quux ./run.sh
# instead of changing the contents of this file
HADOOP_HOME=${HADOOP_HOME:-$DEFAULT_HADOOP_HOME}
LDBC_SNB_DATAGEN_HOME=${LDBC_SNB_DATAGEN_HOME:-$DEFAULT_LDBC_SNB_DATAGEN_HOME}

export HADOOP_HOME
export LDBC_SNB_DATAGEN_HOME

mvn clean
mvn -DskipTests assembly:assembly

# hack because osx by default is not case sensitive
7z d -r $LDBC_SNB_DATAGEN_HOME/target/ldbc_snb_datagen-0.2.5-jar-with-dependencies.jar license

export HADOOP_CLIENT_OPTS="-Xmx1024m"
$HADOOP_HOME/bin/hadoop jar $LDBC_SNB_DATAGEN_HOME/target/ldbc_snb_datagen-0.2.5-jar-with-dependencies.jar $LDBC_SNB_DATAGEN_HOME/params.ini

rm -f m*personFactors*
rm -f .m*personFactors*
rm -f m*activityFactors*
rm -f .m*activityFactors*
rm -f m0friendList*
rm -f .m0friendList*
