package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException

class SkipCommitPlugin extends Plugin {
    public Map getSubscribedEvents() {
        return [
            "${Event.PREPARE}": [
                [{ event -> this.doSkipCommit(event) }, 10],
            ],
        ]
    }

    public Boolean doSkipCommit(Event event) {
        // If the last commit includes [ci skip], do not proceed.
        def commitMsg = event.script.sh(script: "git log -n 1 HEAD", returnStdout: true)
        if (commitMsg.matches(/(?ms)(.*\[(skip ci|ci skip)\].*)/)) {
            event.script.currentBuild.description = 'Skipped by [skip ci]'
            event.script.currentBuild.result = event.script.currentBuild.getPreviousBuild().result

            try {
                // Try doing a cleanup, required the following methods being approved.
                // method hudson.model.Run delete
                // method org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper getRawBuild
                event.script.currentBuild.getRawBuild().delete()
            } catch (RejectedAccessException e) {}

            return false
        }

        return true
    }
}
