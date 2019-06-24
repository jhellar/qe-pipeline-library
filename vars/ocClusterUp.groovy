#!/usr/bin/env groovy

def call() {
    // Test whether local instance of `oc cluster up` is already running
    def ocWhoamiStatuscode = sh (
        script: "oc whoami",
        returnStatus: true
    )

    if (ocWhoamiStatuscode == 0) {
        echo "Local OpenShift instance is already running..."
    } else {
        sh """
        export DEFAULT_CLUSTER_IP=${OPENSTACK_PUBLIC_IP}
        
        oc cluster down

        if [ -z "\${DEFAULT_CLUSTER_IP}" ]; then
            export DEFAULT_CLUSTER_IP=\$(ifconfig \$(netstat -nr | awk '{if ((\$1 == "0.0.0.0" || \$1 == "default") && \$2 != "0.0.0.0" && \$2 ~ /[0-9\\.]+{4}/){print \$NF;} }' | head -n1) | grep 'inet ' | awk '{print \$2}')
        fi

        oc cluster up \
            --public-hostname=\$DEFAULT_CLUSTER_IP.nip.io \
            --routing-suffix=\$DEFAULT_CLUSTER_IP.nip.io \
            --no-proxy=\$DEFAULT_CLUSTER_IP || exit 1
        
        oc login -u system:admin
        oc adm policy add-cluster-role-to-user cluster-admin developer
        
        export URL="\$(oc describe cm/webconsole-config -n openshift-web-console | grep "{" | python -m json.tool | grep masterPublicURL | awk '{print \$2}' | cut -c 2- | sed 's/..\$//')"
        oc login "\${URL}" -u developer -p developer --insecure-skip-tls-verify=true
        """
    }
}