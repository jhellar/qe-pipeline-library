#!/usr/bin/env groovy

/** Download Operator SDK binary and save it to location within PATH
 *
 * @param version           version from https://github.com/operator-framework/operator-sdk/releases, e.g. "v0.9.0"
 * @param outputDirectory   absolute path to folder which the binary should be downloaded to
 *
*/
def call(Map params) {
    final String outputDirectory = params.outputDirectory ?: "/usr/local/bin"
    final String version = params.version

    sh """
    sudo curl -Lso ${outputDirectory}/operator-sdk https://github.com/operator-framework/operator-sdk/releases/download/${version}/operator-sdk-${version}-x86_64-linux-gnu
    sudo chmod +x ${outputDirectory}/operator-sdk
    """
}