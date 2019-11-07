#!/usr/bin/env groovy

// Build container image with Docker and push it to container registry
def call(Map params) {

    final String credentialsId = params.credentialsId
    final String containerRegistryServerName = params.containerRegistryServerName
    final String containerImageName = params.containerImageName
    final String pathToDockerfile = params.pathToDockerfile
    final String[] additionalNames = params.additionalNames

    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'registryUsername', passwordVariable: "registryPassword")]) {
        sh """
        docker login -u ${registryUsername} -p ${registryPassword} ${containerRegistryServerName}
        docker build -t ${containerImageName} -f ${pathToDockerfile} .
        docker push ${containerImageName}
        """
        
        if(additionalNames) {
            additionalNames.each { name ->
                sh """
                docker tag ${containerImageName} ${name}
                docker push ${name}
                """
            }
        }
    }
}