#!/usr/bin/env groovy

def call() {
    sh """
      curl https://password.corp.redhat.com/RH-IT-Root-CA.crt > ~/RH-IT-Root-CA.crt
      npm config set cafile ~/RH-IT-Root-CA.crt
      npm config set registry https://repository.engineering.redhat.com/nexus/repository/registry.npmjs.org
    """
}