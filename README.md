SVoNt
=====

Subversion and Eclipse plugins for semantic versioning of OWL-EL ontologies

## Usage

### Creating a SVoNt Project

Create a new project using the "New Project" wizard:

1. In any explorer view in Eclipse (e.g. Project Explorer or Java Package Explorer view), right click and select "New -> Project...".
2. In the upcoming window, select the wizard suiting the type of project you intend to create.

 ![Create a new project using the "New Project" wizard](README.img/create_project.png)
 
3. Create your ontology (using Eclipse or any external tool), and save it in your Eclipse Workspace.

 **Important!**
 * The file must be named `ontology.owl`.
 * The file must reside in a folder named `ontologies`. The folder must reside directly under the project root.
 * The serialization format of the ontology has to be RDF/XML.

 This is how things should look after the setup:

 ![The ontology file should be named "ontology.owl" and reside in a folder named "ontologies"](README.img/ontology_naming.png)

4. Load the project as an Ontology Project:

 ![Activate the context menu on the project root, select "Ontology", "Load as Ontology Project..."](README.img/ontology_project.png)
 
5. Share the project to SVN:
 
 ![Activate the context menu on the project root, select "Team", "Share Project..."](README.img/share_project.png)

 Please follow the instructions given by the Subclipse sharing wizard in order to select a repository for the new project.
 
6. **Before commiting for the first time**, load the project as a SVoNt Project:

 ![Activate the context menu on the project root, select "Ontology", "Load as SVoNt Project..."](README.img/load_as_svont_project.png)

7. In the upcoming dialog window, enter the WebDAV URL for the SVoNt change log repository:

 ![Changelog Repository URL dialog](README.img/locate_changelog_repo.png)

8. Perform an initial commit operation using the **SVoNt Commit** command:

 ![Activate the context menu on the project root, select "Team", "SVoNt Commit"](README.img/svont_commit.png)

 The regular SVN commit dialog will show. Proceed as usual.

9. In order to synchronize the newly created remote change log with the local change log, perform a **SVoNt Update** operation:

 ![Activate the context menu on the project root, select "Team", "SVoNt Update"](README.img/svont_update.png)

Finally, the project structure should look like this (the screenshot shows hidden files which should not be visible in your setup, unless you configured the explorer view to show hidden files): 

 ![Hidden folders containing ontology and change history meta-information](README.img/end_result.png)
