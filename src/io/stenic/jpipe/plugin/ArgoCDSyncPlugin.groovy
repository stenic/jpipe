package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException

class ArgoCDSyncPlugin extends Plugin {

    private String credentialsId;
    private String argoCDServer;
    private String argoCDProject;
    private String cdBranch;
    private String cliOpts;

    ArgoCDSyncPlugin(Map opts = [:]) {
        this.credentialsId = opts.get('credentialsId', '');
        this.argoCDServer = opts.get('argoCDServer', 'argocd-server.argocd');
        this.argoCDProject = opts.get('argoCDProject', '');
        this.cliOpts = opts.get('cliOpts', '--grpc-web --insecure');
        this.cdBranch = opts.get('cdBranch', 'main');
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.DEPLOY}": [
                [{ event -> this.doSync(event) }, 20],
            ],
        ]
    }

    public Boolean doSync(Event event) {
        if (event.env.BRANCH_NAME != this.cdBranch) {
            event.script.println("Skipping ArgoCDSyncPlugin")
            return true;
        }
    
        event.script.docker.image("busybox").inside {
            event.script.withCredentials([event.script.string(credentialsId: this.credentialsId, variable: 'ARGOCD_AUTH_TOKEN')]) {
                event.script.sh """
                    export ARGOCD_CLI=./argocd
                    export ARGOCD_SERVER=${this.argoCDServer}
                    export ARGOCD_OPTS="${this.cliOpts}"

                    wget --no-verbose -O \$ARGOCD_CLI --no-check-certificate https://\${ARGOCD_SERVER}/download/argocd-linux-amd64
                    chmod +x \$ARGOCD_CLI
                    \$ARGOCD_CLI app sync ${this.argoCDProject}
                    \$ARGOCD_CLI app wait ${this.argoCDProject}
                """
            }
        }

        return true
    }
}
