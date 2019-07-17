#!/usr/bin/env groovy

// Run e2e test with operator-sdk with specified image and namespace
def call(Map params) {

    final String containerImageName = params.containerImageName
    final String namespace = params.namespace ?: sh(script: "oc project -q", returnStdout: true).trim()

    sh """
    yq w -i deploy/operator.yaml spec.template.spec.containers[0].image ${containerImageName}
    operator-sdk test local ./test/e2e --namespace ${namespace} --go-test-flags '-v'
    """
}
