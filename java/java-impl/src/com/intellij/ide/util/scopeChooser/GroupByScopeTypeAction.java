/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.ide.util.scopeChooser;

import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.packageDependencies.DependencyUISettings;
import com.intellij.packageDependencies.ui.ProjectPatternProvider;

public final class GroupByScopeTypeAction extends ToggleAction {
  private final Runnable myUpdate;

  public GroupByScopeTypeAction(Runnable update) {
    super(IdeBundle.message("action.group.by.scope.type"),
          IdeBundle.message("action.description.group.by.scope"), AllIcons.Actions.GroupByTestProduction);
    myUpdate = update;
  }

  public boolean isSelected(AnActionEvent event) {
    return DependencyUISettings.getInstance().UI_GROUP_BY_SCOPE_TYPE;
  }

  public void setSelected(AnActionEvent event, boolean flag) {
    DependencyUISettings.getInstance().UI_GROUP_BY_SCOPE_TYPE = flag;
    myUpdate.run();
  }

  public void update(final AnActionEvent e) {
    super.update(e);
    e.getPresentation().setVisible(!ProjectPatternProvider.FILE.equals(DependencyUISettings.getInstance().SCOPE_TYPE));
  }
}
