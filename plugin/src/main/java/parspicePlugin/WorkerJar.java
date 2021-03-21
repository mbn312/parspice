package parspicePlugin;

import org.gradle.api.provider.Property;

abstract public class WorkerJar {
    private final String name;

    @javax.inject.Inject
    public WorkerJar(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract public Property<String> getMainClass();
}
