/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.quartz.editor.widget.sortable;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 *
 */
public abstract class SortableListCallback<T extends Serializable> implements Serializable {

  private static final long serialVersionUID = 1L;

  public abstract void onSave(List<T> orderedItems, AjaxRequestTarget target);
}
