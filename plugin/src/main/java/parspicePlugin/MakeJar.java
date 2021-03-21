package parspicePlugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.jvm.tasks.Jar;

abstract public class MakeJar extends Jar {
    @Input
    abstract public Property<String> getMainClass();

    @TaskAction
    public void makeJar() {

    }
}


