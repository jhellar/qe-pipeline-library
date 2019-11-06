#!/usr/bin/env groovy

def call(String repoDir = '.') {
  dir(repoDir) {
    sh(returnStdout: true, script: '''
      git fetch origin
      LATEST_VERSION=$(git tag -l --sort=version:refname | tail -n1)
      VERSION_PARTS=(`echo "$LATEST_VERSION" | tr "." "\\n"`)
      NEXT_VERSION=${VERSION_PARTS[0]}.$((${VERSION_PARTS[1]} + 1)).0
      NUM_COMMITS=$(git rev-list HEAD --count)
      LAST_COMMIT=$(git rev-parse --short HEAD)
      echo $NEXT_VERSION-dev.$NUM_COMMITS.$LAST_COMMIT
    ''')
  }
}