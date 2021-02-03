import io.stenic.jpipe.pipeline.Pipeline
import io.stenic.jpipe.plugin.CleanDirPlugin
import io.stenic.jpipe.plugin.CheckoutPlugin
import io.stenic.jpipe.plugin.SkipCommitPlugin
import io.stenic.jpipe.plugin.ConventionalCommitPlugin
import io.stenic.jpipe.plugin.DockerPlugin
import io.stenic.jpipe.plugin.SonarQubePlugin

def call(Map config = [:]) {
    node(config.get('node', 'docker')) {
        Pipeline pipeline = new Pipeline(this)

        pipeline.addPlugins(config.get('plugins', [
            new CleanDirPlugin(),
            new CheckoutPlugin(),
            new SkipCommitPlugin(),
            new ConventionalCommitPlugin([
                releaseBranches: config.get('releaseBranches', 'main'),
                prereleaseBranches: config.get('prereleaseBranches', 'develop'),
            ]),
            new DockerPlugin([
                credentialId: config.get('dockerCredentialId'),
                repository: config.get('dockerRepository'),
                server: config.get('dockerServer'),
                testScript: config.get('testScript', 'make test'),
            ]),
            new SonarQubePlugin([
                projectKey: config.get("projectName"),
            ]),
        ]))

        pipeline.run(config, env)
    }
}
