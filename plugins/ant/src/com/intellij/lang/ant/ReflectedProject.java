// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.lang.ant;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.util.Pair;
import com.intellij.util.ExceptionUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
* @author Eugene Zhuravlev
*/
public final class ReflectedProject {
  private static final Logger LOG = Logger.getInstance(ReflectedProject.class);

  private static final List<SoftReference<Pair<ReflectedProject, ClassLoader>>> ourProjects =
    new ArrayList<>();

  private static final ReentrantLock ourProjectsLock = new ReentrantLock();
  public static final String ANT_PROJECT_CLASS = "org.apache.tools.ant.Project";

  private final Object myProject;
  private Map<String, Class<?>> myTaskDefinitions;
  private Map<String, Class<?>> myDataTypeDefinitions;
  private Map<String, String> myProperties;
  private Class<?> myTargetClass;

  public static ReflectedProject getProject(final ClassLoader classLoader) {
    ourProjectsLock.lock();
    try {
      for (Iterator<SoftReference<Pair<ReflectedProject, ClassLoader>>> iterator = ourProjects.iterator(); iterator.hasNext();) {
        final SoftReference<Pair<ReflectedProject, ClassLoader>> ref = iterator.next();
        final Pair<ReflectedProject, ClassLoader> pair = ref.get();
        if (pair == null) {
          iterator.remove();
        }
        else {
          if (pair.second == classLoader) {
            return pair.first;
          }
        }
      }
    }
    finally {
      ourProjectsLock.unlock();
    }
    final ReflectedProject reflectedProj = new ReflectedProject(classLoader);
    ourProjectsLock.lock();
    try {
      ourProjects.add(new SoftReference<>(
        Pair.create(reflectedProj, classLoader)
      ));
    }
    finally {
      ourProjectsLock.unlock();
    }
    return reflectedProj;
  }

  ReflectedProject(final ClassLoader classLoader) {
    Object project = null;
    try {
      final Class<?> projectClass = classLoader.loadClass(ANT_PROJECT_CLASS);
      if (projectClass != null) {
        project = projectClass.getConstructor().newInstance();
        Method method = projectClass.getMethod("init");
        method.invoke(project);
        method = projectClass.getMethod("getTaskDefinitions");
        //noinspection unchecked
        myTaskDefinitions = (Map<String, Class<?>>)method.invoke(project);
        method = projectClass.getMethod( "getDataTypeDefinitions");
        //noinspection unchecked
        myDataTypeDefinitions = (Map<String, Class<?>>)method.invoke(project);
        method = projectClass.getMethod( "getProperties");
        //noinspection unchecked
        myProperties = (Map<String, String>)method.invoke(project);
        myTargetClass = classLoader.loadClass("org.apache.tools.ant.Target");
      }
    }
    catch (ProcessCanceledException e) {
      throw e;
    }
    catch (Throwable e) {
      // rethrow PCE if it was the cause
      final Throwable cause = ExceptionUtil.getRootCause(e);
      if (cause instanceof ProcessCanceledException) {
        throw (ProcessCanceledException)cause;
      }
      LOG.info(e);
      project = null;
    }
    myProject = project;
  }

  @Nullable
  public Map<String, Class<?>> getTaskDefinitions() {
    return myTaskDefinitions;
  }

  @Nullable
  public Map<String, Class<?>> getDataTypeDefinitions() {
    return myDataTypeDefinitions;
  }

  public Map<String, String> getProperties() {
    return myProperties;
  }

  public Class<?> getTargetClass() {
    return myTargetClass;
  }

  @Nullable
  public Object getProject() {
    return myProject;
  }
}
