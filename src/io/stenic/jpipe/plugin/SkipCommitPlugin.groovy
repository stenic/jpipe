package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException
import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException
import jenkins.model.CauseOfInterruption.UserInterruption
import hudson.model.Result

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
            // Check if the build was started by a user.
            if (event.script.currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause) != null) {
                event.script.println = 'Triggered by user, ignoring [skip ci]'
                return true
            }
            event.script.currentBuild.description = 'Skipped by [skip ci]'

            // try {
            //     // Try doing a cleanup, required the following methods being approved.
            //     // method hudson.model.Run delete
            //     // method org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper getRawBuild

            //     event.script.currentBuild.getRawBuild().delete()
            //     event.script.currentBuild.getRawBuild().setResult(Result.fromString(event.script.currentBuild.getPreviousBuild().result))
            // } catch (RejectedAccessException e) {}

            throw new FlowInterruptedException(Result.fromString(event.script.currentBuild.getPreviousBuild().result), true, new UserInterruption(event.script.env.BUILD_USER_ID))
        }

        return true
    }
}
