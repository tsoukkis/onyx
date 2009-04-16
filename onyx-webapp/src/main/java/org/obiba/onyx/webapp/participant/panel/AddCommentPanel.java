/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.webapp.participant.panel;

import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.obiba.onyx.engine.Action;
import org.obiba.onyx.wicket.behavior.RequiredFormFieldBehavior;

/**
 * Content of InterviewPage's "Add Comment" dialog.
 */
public class AddCommentPanel extends Panel {
  //
  // Constants
  //

  private static final long serialVersionUID = 1L;

  //
  // Constructors
  //

  public AddCommentPanel(String id) {
    super(id);
    setModel(new Model(new Action()));

    initPanel();
  }

  //
  // Methods
  //

  private void initPanel() {
    add(new MultiLineLabel("instructions", new ResourceModel("AnonymousComments")));

    TextArea commentField = new TextArea("comment", new PropertyModel(getModelObject(), "comment"));
    commentField.add(new RequiredFormFieldBehavior());
    commentField.add(new StringValidator.MaximumLengthValidator(2000));
    add(commentField);
  }
}
