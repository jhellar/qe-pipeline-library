#!/usr/bin/env groovy

def call() {
  sh(returnStdout: true, script: '''
    git fetch origin
    LATEST_VERSION=$(git tag -l --sort=version:refname | tail -n1)
    NEXT_VERSION=$(echo $LATEST_VERSION | awk -F. '/[0-9]+\\./{$NF++;print}' OFS=.)
    NUM_COMMITS=$(git rev-list HEAD --count)
    LAST_COMMIT=$(git rev-parse --short HEAD)
    echo $NEXT_VERSION-dev.$NUM_COMMITS.$LAST_COMMIT
  ''')
}