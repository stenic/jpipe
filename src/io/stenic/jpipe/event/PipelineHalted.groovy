package io.stenic.jpipe.event

/**
 * Thrown by a plugin to stop the pipeline early without aborting the run.
 *
 * Pipeline.run() catches this and returns normally, so the build keeps
 * whatever result the plugin set (e.g. SUCCESS) and renders green. This is
 * deliberately NOT a FlowInterruptedException: letting that escape marks the
 * Jenkins run as aborted (gray/red) even when the current stage is green.
 *
 * The framework stays agnostic about why the pipeline was halted; the reason
 * is free-form and supplied by the caller (e.g. SkipCommitPlugin on [skip ci]).
 */
class PipelineHalted extends RuntimeException implements Serializable {
    PipelineHalted(String reason = 'Pipeline halted') {
        super(reason)
    }
}
