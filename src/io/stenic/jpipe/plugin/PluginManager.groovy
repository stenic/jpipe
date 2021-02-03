package io.stenic.jpipe.plugin

class PluginManager implements Serializable {
    private Collection pluginCollection = [];

    public void register(Plugin plugin) {
        this.pluginCollection += plugin;
    }

    public Plugin findByName(String name) {
        return this.pluginCollection
            .find { it.getName() == name }
    }

    public Collection getPlugins() {
        return this.pluginCollection;
    }
}
