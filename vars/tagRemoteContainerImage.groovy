#!/usr/bin/env groovy

// Copy (retag) remote container image and delete original one (if requested)
def call(Map params) {

    final String credentialsId = params.credentialsId
    final String sourceImage = params.sourceImage
    final String targetImage = params.targetImage

    final Boolean deleteOriginalImage = params.deleteOriginalImage ?: false


    withCredentials([usernameColonPassword(credentialsId: "${credentialsId}", variable: 'REGISTRY_CREDENTIALS')]) {
        retry(3) {
            sh """
                skopeo copy \
                    --src-creds ${env.REGISTRY_CREDENTIALS} \
                    --dest-creds ${env.REGISTRY_CREDENTIALS} \
                    docker://${sourceImage} \
                    docker://${targetImage}
            """
        }

        if (deleteOriginalImage) {
            retry(3) {
                sh """
                    skopeo delete \
                        --creds ${env.REGISTRY_CREDENTIALS} \
                        docker://${sourceImage} \
                    || sleep 10
                """
            }
        }
    }
}