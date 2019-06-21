#!/usr/bin/env groovy

// Create a new clean OpenShift project (if some with the same name exists, delete it first)
def call(String openshiftProjectName) {
    
    sh """
    oc delete project ${openshiftProjectName} || true
    oc new-project ${openshiftProjectName}
    """
}