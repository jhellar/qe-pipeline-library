#!/usr/bin/env groovy

def call(Map params) {

    final String clonedRepositoryPath = params.clonedRepositoryPath
    final String openshiftProjectName = params.openshiftProjectName
    final String operatorContainerImageCandidateName = params.operatorContainerImageCandidateName
    final String operatorContainerImageName = params.operatorContainerImageName
    final String operatorContainerImageNameLatest = params.operatorContainerImageNameLatest
    final String containerRegistryCredentialsId = params.containerRegistryCredentialsId

    echo "Running oc-cluster-up"
    ocClusterUp()

    echo "Create a new OpenShift project"
    newOpenshiftProject "${openshiftProjectName}"

    dir("${clonedRepositoryPath}") {
        echo "Make code/compile"
        sh "make code/compile"

        echo "Build & push Operator SDK (candidate) image"
        dockerBuildAndPush(
            credentialsId: "${containerRegistryCredentialsId}",
            containerRegistryServerName: "quay.io",
            containerImageName: "${operatorContainerImageCandidateName}",
            pathToDockerfile: "build/Dockerfile"
        )

        echo "Compile test binary"
        sh "make test/compile"

        echo "Run e2e test"
        runOperatorTestWithImage (
            containerImageName: "${operatorContainerImageCandidateName}",
            namespace: "${openshiftProjectName}"
        )
    }

    echo "Retag the image if the test passed and delete an old tag"
    tagRemoteContainerImage (
        credentialsId: "${containerRegistryCredentialsId}",
        sourceImage: "${operatorContainerImageCandidateName}",
        targetImage: "${operatorContainerImageName}",
        deleteOriginalImage: true
    )

    if ("${env.BRANCH_NAME}" == "master") {
        echo "Create a 'latest' tag from 'master'"
        tagRemoteContainerImage (
            credentialsId: "${containerRegistryCredentialsId}",
            sourceImage: "${operatorContainerImageName}",
            targetImage: "${operatorContainerImageNameLatest}",
            deleteOriginalImage: false
        )
    }
}