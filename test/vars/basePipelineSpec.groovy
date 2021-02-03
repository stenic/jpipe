import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification
import io.stenic.jpipe.plugin.CheckoutPlugin

class basePipelineSpec extends JenkinsPipelineSpecification {
    def basePipeline = null

    def setup() {
        basePipeline = loadPipelineScriptForTest("vars/basePipeline.groovy")
        basePipeline.getBinding().setVariable("scm", [branches: null, userRemoteConfigs: null])
        basePipeline.getBinding().setVariable("env", [BUILD_ID: "123"])
        basePipeline.getBinding().setVariable("currentBuild", [displayName: ""])
        getPipelineMock("sh")([script: "git log -n 1 HEAD", returnStdout: true]) >> {
            return "Nothing special"
        }
    }

    def "[basePipeline] stages"() {
        when:
            basePipeline plugins:[new CheckoutPlugin()]
        then:
            1 * getPipelineMock("stage")(_) >> { _arguments ->
                assert "Prepare" == _arguments[0][0] 
            }
            1 * getPipelineMock("stage")(_) >> { _arguments ->
                assert "Build" == _arguments[0][0] 
            }
            1 * getPipelineMock("stage")(_) >> { _arguments ->
                assert "Test" == _arguments[0][0] 
            }
            1 * getPipelineMock("stage")(_) >> { _arguments ->
                assert "Publish" == _arguments[0][0] 
            }
            1 * getPipelineMock("stage")(_) >> { _arguments ->
                assert "Deploy" == _arguments[0][0] 
            }
    }
}