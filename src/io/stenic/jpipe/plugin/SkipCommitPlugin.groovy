package io.stenic.jpipe.plugin

import io.stenic.jpipe.event.Event
import io.stenic.jpipe.event.PipelineHalted

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

            // Carry the previous build's result forward onto this skipped run.
            // currentBuild.result starts null (treated as SUCCESS); we set it
            // explicitly so the run reflects the last real build's outcome.
            event.script.currentBuild.result = event.script.currentBuild.getPreviousBuild()?.result ?: 'SUCCESS'

            // Notify subscribers (e.g. SCM status notifiers) that this build is being
            // skipped, so they can publish a terminal commit status. Without this the
            // Jenkins GitHub Branch Source plugin's "pending" status on indexing never
            // gets overwritten, leaving the commit stuck on pending.
            // SkipCommitPlugin stays SCM-agnostic; concrete notifiers live in user code.
            event.data.reason = 'Skipped by [skip ci]'
            event.eventDispatcher?.dispatch(Event.SKIPPED, event)

            // Stop the pipeline. PipelineHalted is caught by Pipeline.run(), so the
            // run finishes cleanly with the result set above and still fires the
            // post-build status notifier for GitHub. We deliberately do NOT throw
            // FlowInterruptedException: that aborts the run (gray/red ball) even
            // though the Prepare stage is green, which is exactly the "stage green,
            // run not green" symptom for [skip ci] builds.
            throw new PipelineHalted('Skipped by [skip ci]')
        }

        return true
    }
}
