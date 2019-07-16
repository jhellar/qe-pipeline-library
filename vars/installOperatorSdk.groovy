#!/usr/bin/env groovy

/** Download Operator SDK binary and save it to location within PATH
 *
 * @param operatorSdkVersion version from https://github.com/operator-framework/operator-sdk/releases, e.g. "v0.9.0"
 * 
*/
def call(String operatorSdkVersion) {
    sh """
    sudo curl -Lso /usr/local/bin/operator-sdk https://github.com/operator-framework/operator-sdk/releases/download/${operatorSdkVersion}/operator-sdk-${operatorSdkVersion}-x86_64-linux-gnu
    sudo chmod +x /usr/local/bin/operator-sdk
    """
}