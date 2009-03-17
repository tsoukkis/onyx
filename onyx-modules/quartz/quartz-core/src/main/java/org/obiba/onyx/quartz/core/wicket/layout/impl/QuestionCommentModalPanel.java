/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.quartz.core.wicket.layout.impl;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Question;
import org.obiba.onyx.quartz.core.service.ActiveQuestionnaireAdministrationService;

public abstract class QuestionCommentModalPanel extends Panel {

  private static final long serialVersionUID = 1L;

  @SpringBean
  private ActiveQuestionnaireAdministrationService activeQuestionnaireAdministrationService;

  private ModalWindow commentWindow;

  private FeedbackPanel feedback;

  private String comment;

  public QuestionCommentModalPanel(String id, ModalWindow commentWindow, IModel questionModel, AjaxRequestTarget target) {
    super(id, questionModel);
    this.commentWindow = commentWindow;

    setComment(activeQuestionnaireAdministrationService.getComment((Question) getModelObject()));

    add(feedback = new FeedbackPanel("feedback"));
    feedback.setOutputMarkupId(true);

    add(new CommentForm("commentForm", target));

  }

  private class CommentForm extends Form {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("serial")
    public CommentForm(String id, AjaxRequestTarget target) {
      super(id);

      setModel(new Model(QuestionCommentModalPanel.this));

      // field is not required as comment may be removed
      final TextArea newComment = new TextArea("newComment", new PropertyModel(getModel(), "comment"));
      newComment.setOutputMarkupId(true);
      target.focusComponent(newComment);
      // see QuestionAnswer.comment column length
      newComment.add(new StringValidator.MaximumLengthValidator(2000));
      add(newComment);

      // Save a new comment.
      add(new AjaxButton("saveComment", this) {

        protected void onSubmit(AjaxRequestTarget target, Form form) {
          activeQuestionnaireAdministrationService.setComment((Question) QuestionCommentModalPanel.this.getModelObject(), comment);
          QuestionCommentModalPanel.this.onAddComment(target);
          commentWindow.close(target);
        }

        protected void onError(AjaxRequestTarget target, Form form) {
          target.addComponent(feedback);
        }

      });

      // Cancel comment.
      add(new AjaxLink("cancelComment") {

        @Override
        public void onClick(AjaxRequestTarget target) {
          commentWindow.close(target);
        }

      });

    }
  }

  public String getComment() {
    return comment;
  }

  protected abstract void onAddComment(AjaxRequestTarget target);

  public void setComment(String comment) {
    this.comment = comment;
  }

}
