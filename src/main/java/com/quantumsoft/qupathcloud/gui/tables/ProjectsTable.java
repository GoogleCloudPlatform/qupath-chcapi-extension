// Copyright (C) 2019 Google LLC
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.quantumsoft.qupathcloud.gui.tables;

import com.quantumsoft.qupathcloud.entities.Project;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * The ProjectsTable represents a table with a list of Projects.
 */
public class ProjectsTable {

  /**
   * Gets projects table.
   *
   * @param projects the projects
   * @return the projects table
   */
  public TableView getProjectsTable(List<Project> projects) {

    projects.sort(Comparator.comparing(Project::getProjectId));
    projects.sort(Comparator.comparing(Project::getName));
    ObservableList<Project> projectsObservableList = FXCollections.observableArrayList(projects);
    TableView<Project> projectsTable = new TableView<>(projectsObservableList);
    projectsTable.setId("projectsTable");

    TableColumn<Project, String> nameColumn = new TableColumn<>("NAME");
    nameColumn.prefWidthProperty().bind(projectsTable.widthProperty().multiply(0.498));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    projectsTable.getColumns().add(nameColumn);

    TableColumn<Project, String> idColumn = new TableColumn<>("ID");
    idColumn.prefWidthProperty().bind(projectsTable.widthProperty().multiply(0.498));
    idColumn.setCellValueFactory(new PropertyValueFactory<>("projectId"));
    projectsTable.getColumns().add(idColumn);

    return projectsTable;
  }
}
