package org.jetbrains.idea.maven.project;

import org.jetbrains.idea.maven.embedder.MavenConsole;
import com.intellij.openapi.project.Project;

public class MavenProjectsProcessorFoldersUpdatingTask extends MavenProjectsProcessorBasicTask {
  private MavenImportingSettings myImportingSettings;

  public MavenProjectsProcessorFoldersUpdatingTask(MavenProject project, MavenImportingSettings importingSettings, MavenProjectsTree tree) {
    super(project, tree);
    myImportingSettings = importingSettings;
  }

  public void perform(Project project, MavenEmbeddersManager embeddersManager, MavenConsole console, MavenProcess process)
    throws MavenProcessCanceledException {
    myTree.generateSources(myMavenProject, embeddersManager, myImportingSettings, console, process);
  }
}
