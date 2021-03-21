package parspicePlugin;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.model.ObjectFactory;
import org.gradle.jvm.tasks.Jar;

import java.util.stream.Collectors;

public class ParSPICEPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        ObjectFactory objects = project.getObjects();

        NamedDomainObjectContainer<WorkerJar> workerJarContainer =
                objects.domainObjectContainer(WorkerJar.class, name -> objects.newInstance(WorkerJar.class, name));
        project.getExtensions().add("workers", workerJarContainer);

        workerJarContainer.all(workerJar -> {
            String jar = workerJar.getName();
            String capitalizedJar = jar.substring(0, 1).toUpperCase() + jar.substring(1);
            String taskName = "workerJar" + capitalizedJar;

            project.getTasks().register(taskName, Jar.class,
                    it -> {
                        String mainClass = workerJar.getMainClass().get();
                        it.getManifest().getAttributes().put("Main-Class", mainClass);
                        it.doFirst(task -> System.out.println("hello there: " + mainClass));
                        it.dependsOn("build");
                        it.setArchiveName(workerJar.getName() + ".jar");
                        it.from(
                                project.getConfigurations().getByName("compileClasspath").resolve().stream().map(
                                        it2 -> (it2.isDirectory())?it2:project.zipTree(it2)
                                ).collect(Collectors.toSet())
                        );
                        it.with((CopySpec) project.getTasks().getByName("jar"));
                    }
            );
        });
    }
}

