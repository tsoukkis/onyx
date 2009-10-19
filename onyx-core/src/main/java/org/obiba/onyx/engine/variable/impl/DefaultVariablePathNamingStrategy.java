/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.engine.variable.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.obiba.onyx.engine.variable.IVariablePathNamingStrategy;
import org.obiba.onyx.engine.variable.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * Default variable path is for instance : Onyx.Test.Toto?id=1&name=Vincent+Ferreti&path=tutu.tata
 */
public class DefaultVariablePathNamingStrategy implements IVariablePathNamingStrategy {

  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(DefaultVariablePathNamingStrategy.class);

  public static final String ENCODING = "ISO-8859-1";

  public static final String DEFAULT_PATH_SEPARATOR = ".";

  private String rootName = "Onyx";

  private String pathSeparator = DEFAULT_PATH_SEPARATOR;

  public static final String QUERY_STARTER = "?";

  public static final String QUERY_KEY_VALUE_SEPARATOR = "=";

  public static final String QUERY_STATEMENT_SEPARATOR = "&";

  private boolean startWithPathSeparator = false;

  public String getPath(Variable entity) {
    String name = normalizeName(entity.getName());
    if(entity.getParent() != null) {
      StringBuffer buffer = new StringBuffer();
      return getPath(entity.getParent(), buffer).append(getPathSeparator()).append(name).toString();
    } else if(startWithPathSeparator) {
      return getPathSeparator() + name;
    } else {
      return name;
    }
  }

  /**
   * Recursive path building using string buffer.
   * @param entity
   * @param buffer
   * @return
   */
  private StringBuffer getPath(Variable entity, StringBuffer buffer) {
    String name = normalizeName(entity.getName());
    if(entity.getParent() != null) {
      getPath(entity.getParent(), buffer).append(getPathSeparator()).append(name);
    } else if(startWithPathSeparator) {
      buffer.append(getPathSeparator()).append(name);
    } else {
      buffer.append(name);
    }
    return buffer;
  }

  public String getPath(Variable entity, String key, String value) {
    return addParameter(getPath(entity), key, value);
  }

  public String addParameter(String path, String key, String value) {
    if(path.contains("?")) {
      path += QUERY_STATEMENT_SEPARATOR;
    } else {
      path += QUERY_STARTER;
    }
    try {
      path += key + QUERY_KEY_VALUE_SEPARATOR + URLEncoder.encode(value, ENCODING);
    } catch(UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Value cannot be encoded in " + ENCODING + ": " + value, e);
    }

    return path;
  }

  public String getPath(Node entityNode) {
    String name = getEntityName(entityNode);
    if(name != null) {
      String path = name;
      if(entityNode.getParentNode() != null) {
        path = getPathSeparator() + name;
        String parentPath = getPath(entityNode.getParentNode());
        if(parentPath != null) {
          return parentPath + path;
        }
      } else if(startWithPathSeparator) {
        path = getPathSeparator() + name;
      }
      return path;
    }
    return null;
  }

  private String getEntityName(Node node) {
    if(node.getAttributes() != null) {
      Node nameAttr = node.getAttributes().getNamedItem("name");
      if(nameAttr != null) {
        return nameAttr.getNodeValue();
      }
    }
    return null;
  }

  public static DefaultVariablePathNamingStrategy getInstance(String rootName) {
    DefaultVariablePathNamingStrategy strategy = new DefaultVariablePathNamingStrategy();
    strategy.rootName = rootName;
    return strategy;
  }

  public String getPathSeparator() {
    return pathSeparator;
  }

  /**
   * Get the separator of entity names as a regular expression.
   * @return
   */
  private String getPathSeparatorRegex() {
    return pathSeparator.equals(".") ? "\\." : pathSeparator;
  }

  /**
   * Set the separator string that separates the path elements.
   * @param pathSeparator
   */
  public void setPathSeparator(String pathSeparator) {
    this.pathSeparator = pathSeparator;
  }

  /**
   * Set if the path should start with a path separator (before the root element).
   * @param startWithPathSeparator
   */
  public void setStartWithPathSeparator(boolean startWithPathSeparator) {
    this.startWithPathSeparator = startWithPathSeparator;
  }

  public String normalizeName(String name) {
    return name.replace(' ', '_');
  }

  public void setRootName(String rootName) {
    this.rootName = rootName;
  }

  public String getRootName() {
    return rootName;
  }

  public List<String> getNormalizedNames(String path) {
    List<String> entityNames = new ArrayList<String>();
    String noParamsPath = path;

    // remove query
    int pos = path.indexOf(QUERY_STARTER);
    if(pos >= 0) {
      noParamsPath = path.substring(0, pos);
    }

    // split the path
    for(String str : noParamsPath.split(getPathSeparatorRegex())) {
      if(str.length() > 0) {
        entityNames.add(str);
      }
    }
    if(entityNames.size() > 0 && !entityNames.get(0).equals(getRootName())) {
      entityNames.add(0, getRootName());
    }

    return entityNames;
  }

  public Map<String, String> getParameters(String path) {
    Map<String, String> map = new HashMap<String, String>();

    int pos = path.indexOf(QUERY_STARTER);
    if(pos >= 0 && path.length() - 1 > pos) {
      String paramPath = path.substring(pos + 1, path.length());
      log.info("params={}", paramPath);
      for(String pair : paramPath.split(QUERY_STATEMENT_SEPARATOR)) {
        if(pair.contains(QUERY_KEY_VALUE_SEPARATOR)) {
          String[] entry = pair.split(QUERY_KEY_VALUE_SEPARATOR);
          try {
            map.put(entry[0], URLDecoder.decode(entry[1], ENCODING));
          } catch(UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Value cannot be decoded in " + ENCODING + ": " + entry[1], e);
          }
        }
      }
    }

    return map;
  }

  public String getVariablePath(String path) {
    int pos = path.indexOf(QUERY_STARTER);
    if(pos == -1) return path;

    return path.substring(0, pos);
  }

  public boolean hasParameters(String path) {
    return (path.indexOf(QUERY_STARTER) > -1);
  }

}
