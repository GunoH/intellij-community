package org.jetbrains.idea.maven.project;

import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.pom.java.LanguageLevel;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.facets.FacetImporter;

import java.util.List;
import java.util.Map;

public class MavenModuleConfigurator {
  private final Module myModule;
  private final ModifiableModuleModel myModuleModel;
  private final MavenProjectsTree myMavenTree;
  private final MavenProject myMavenProject;
  private final Map<MavenProject, String> myMavenProjectToModuleName;
  private final MavenImportingSettings mySettings;
  private final ModulesProvider myModulesProvider;
  private final ProjectLibrariesProvider myLibrariesProvider;
  private MavenRootModelAdapter myRootModelAdapter;

  public MavenModuleConfigurator(Module module,
                                 ModifiableModuleModel moduleModel,
                                 MavenProjectsTree mavenTree,
                                 MavenProject mavenProject,
                                 Map<MavenProject, String> mavenProjectToModuleName,
                                 MavenImportingSettings settings,
                                 ModulesProvider modulesProvider,
                                 ProjectLibrariesProvider librariesProvider) {
    myModule = module;
    myModuleModel = moduleModel;
    myMavenTree = mavenTree;
    myMavenProject = mavenProject;
    myMavenProjectToModuleName = mavenProjectToModuleName;
    mySettings = settings;
    myModulesProvider = modulesProvider;
    myLibrariesProvider = librariesProvider;
  }

  public ModifiableRootModel getRootModel() {
    return myRootModelAdapter.getRootModel();
  }

  public void config(boolean isNewlyCreatedModule) {
    myRootModelAdapter = new MavenRootModelAdapter(myModule, myModulesProvider);
    myRootModelAdapter.init(myMavenProject, isNewlyCreatedModule);

    configFolders();
    configDependencies();
    configLanguageLevel();
  }

  public void preConfigFacets() {
    for (FacetImporter importer : getSuitableFacetImporters()) {
      importer.preProcess(myModule, myMavenProject);
    }
  }

  public void configFacets(List<MavenProjectsProcessorPostConfigurationTask> postTasks) {
    for (FacetImporter importer : getSuitableFacetImporters()) {
      importer.process(myModuleModel,
                       myModule,
                       myRootModelAdapter,
                       myMavenTree,
                       myMavenProject,
                       myMavenProjectToModuleName,
                       postTasks);
    }
  }

  private List<FacetImporter> getSuitableFacetImporters() {
    return myMavenProject.getSuitableFacetImporters();
  }

  private void configFolders() {
    new MavenFoldersConfigurator(myMavenProject, mySettings, myRootModelAdapter).config();
  }

  private void configDependencies() {
    for (MavenArtifact artifact : myMavenProject.getDependencies()) {
      boolean isExportable = artifact.isExportable();
      MavenProject depProject = myMavenTree.findProject(artifact.getMavenId());
      if (depProject != null) {
        myRootModelAdapter.addModuleDependency(myMavenProjectToModuleName.get(depProject), isExportable);
      }
      else if (myMavenProject.isSupportedDependency(artifact)) {
        myRootModelAdapter.addLibraryDependency(artifact, isExportable, myLibrariesProvider);
      }
    }
  }

  private void configLanguageLevel() {
    LanguageLevel level = translateLanguageLevel(myMavenProject.getSourceLevel());
    myRootModelAdapter.setLanguageLevel(level);
  }

  @Nullable
  private LanguageLevel translateLanguageLevel(@Nullable String level) {
    if ("1.3".equals(level)) return LanguageLevel.JDK_1_3;
    if ("1.4".equals(level)) return LanguageLevel.JDK_1_4;
    if ("1.5".equals(level)) return LanguageLevel.JDK_1_5;
    if ("1.6".equals(level)) return LanguageLevel.JDK_1_6;

    return null;
  }
}
