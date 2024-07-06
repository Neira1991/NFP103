#!/bin/bash

BQ_BUILDER_FILE=./query_builder.so
QUERY_BUILDER_PATH=${QUERY_BUILDER_PATH:="gs://production-abtasty-shared-library-europe-west1/library/query-builder/query-builder-latest.so"}

if [ ! -f "$BQ_BUILDER_FILE" ]; then
  echo "$BQ_BUILDER_FILE does not exist"
  echo "load bq-builder $QUERY_BUILDER_PATH"
  gsutil cp $QUERY_BUILDER_PATH $BQ_BUILDER_FILE
fi


MANN_WHITNEY_FILE=./mann_whitney.so
MANN_WHITNEY_PATH=${MANN_WHITNEY_PATH:="gs://production-abtasty-shared-library-europe-west1/library/mann-whitney/mann-whitney-latest.so"}

if [ ! -f "$MANN_WHITNEY_FILE" ]; then
  echo "$MANN_WHITNEY_FILE does not exist"
  echo "load mann_whitney $MANN_WHITNEY_FILE"
  gsutil cp $MANN_WHITNEY_PATH $MANN_WHITNEY_FILE
fi


THOMSON_SAMPLING_FILE=./thomson_sampling.so
THOMSON_SAMPLING_PATH=${THOMSON_SAMPLING_PATH:="gs://production-abtasty-shared-library-europe-west1/library/thompson-sampling/thompson-sampling-latest.so"}

if [ ! -f "$THOMSON_SAMPLING_FILE" ]; then
  echo "$THOMSON_SAMPLING_FILE does not exist"
  echo "load thomson_sampling $THOMSON_SAMPLING_PATH"
  gsutil cp $THOMSON_SAMPLING_PATH $THOMSON_SAMPLING_FILE
fi


BAYESIAN_LIB_FILE=./bayesian_lib.so
BAYESIAN_LIB_PATH=${BAYESIAN_LIB_PATH:="gs://production-abtasty-shared-library-europe-west1/library/libbayesian/libbayesian-latest.so"}

if [ ! -f "$BAYESIAN_LIB_FILE" ]; then
  echo "$BAYESIAN_LIB_FILE does not exist"
  echo "load bayesian_lib $BAYESIAN_LIB_PATH"
  gsutil cp $BAYESIAN_LIB_PATH $BAYESIAN_LIB_FILE
fi


SEQUENTIAL_TESTING_LIB_FILE=./sequential_testing_lib.so
SEQUENTIAL_TESTING_LIB_PATH=${SEQUENTIAL_TESTING_LIB_PATH:="gs://production-abtasty-shared-library-europe-west1/library/sequential-testing/sequential-testing-v1.3.0.so"}

if [ ! -f "$SEQUENTIAL_TESTING_LIB_FILE" ]; then
  echo "$SEQUENTIAL_TESTING_LIB_FILE does not exist"
  echo "load sequential_testing_lib $SEQUENTIAL_TESTING_LIB_PATH"
  gsutil cp $SEQUENTIAL_TESTING_LIB_PATH $SEQUENTIAL_TESTING_LIB_FILE
fi
