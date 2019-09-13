#!/usr/bin/env groovy

// Run e2e test with operator-sdk with specified image and namespace
def call(Map params) {

    final String containerImageName = params.containerImageName
    final String namespace = params.namespace ?: sh(script: "oc project -q", returnStdout: true).trim()
    final Boolean namespacedManifest = params.namespacedManifest ?: false
    final Boolean globalManifest = params.globalManifest ?: false
    String namespacedManifestFilename = "''"
    String globalManifestFilename = "''"

    if (namespacedManifest) {
        namespacedManifestFilename = "namespaced_manifest.yaml"
        sh """
        for file in \$(find deploy -name "*" -not -path 'deploy/**/*' | grep -E 'service|role|operator'); 
        do
            echo '---' >> ${namespacedManifestFilename}
            cat \$file >> ${namespacedManifestFilename}
        done
        """
    }

    if (globalManifest) {
        globalManifestFilename = "global_manifest.yaml"
        sh """
        for file in \$(find deploy -name "*_crd*");
        do
            echo '---' >> ${globalManifestFilename}
            cat \$file >> ${globalManifestFilename}
        done
        """
    }

    sh """
    yq w -i deploy/operator.yaml spec.template.spec.containers[0].image ${containerImageName}
    operator-sdk test local ./test/e2e \
        --namespace ${namespace} \
        --namespaced-manifest ${namespacedManifestFilename} \
        --global-manifest ${globalManifestFilename} \
        --go-test-flags '-v'
    """
}
