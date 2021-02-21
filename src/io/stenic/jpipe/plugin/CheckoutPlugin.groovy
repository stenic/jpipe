package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event

class CheckoutPlugin extends Plugin {

    private String gitUser;
    private String gitEmail;

    CheckoutPlugin() {
        this.gitUser = 'jpipe-ci';
        this.gitEmail = 'jpipe@stenic.io'
    }

    CheckoutPlugin(String gitUser, String gitEmail) {
        this.gitUser = gitUser;
        this.gitEmail = gitEmail;
    }

    public Map getSubscribedEvents() {
        return [
            "${Event.PREPARE}": [
                [{ event -> this.doCheckout(event) }, 0],
                [{ event -> this.doGitConfig(event) }, 10],
            ],
        ]
    }

    public void doCheckout(Event event) {
        event.script.checkout([
            $class: 'GitSCM',
            branches: event.script.scm.branches,
            extensions: [[$class: 'LocalBranch', localBranch: "**"], [$class: 'CloneOption', noTags: false]],
            userRemoteConfigs: event.script.scm.userRemoteConfigs
        ])
    }

    // Do git configuration for handling tagging/commiting in the workflow.
    public void doGitConfig(Event event) {
        event.script.sh "git config user.email '${this.gitEmail}'"
        event.script.sh "git config user.name '${this.gitUser}'"
    }
}
