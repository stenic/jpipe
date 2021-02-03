
import spock.lang.Specification
import io.stenic.jpipe.plugin.Plugin
import io.stenic.jpipe.plugin.PluginManager

class PluginManagerSpec extends Specification {

    PluginManager pm = null;

    class MockPlugin extends Plugin {
        private String name;
        MockPlugin(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
        public Map getSubscribedEvents() {}
    }

    def setup() {
        pm = new PluginManager()
    }

    def "[PluginManager] register"() {
        when:
            pm.register(new MockPlugin("build"))
        then:
            assert pm.getPlugins().size() == 1
    }

    def "[PluginManager] findByName"() {
        when:
            pm.register(new MockPlugin("build"))
        then:
            assert pm.findByName("build") != null
            assert pm.findByName("notbuild") == null
    }

}