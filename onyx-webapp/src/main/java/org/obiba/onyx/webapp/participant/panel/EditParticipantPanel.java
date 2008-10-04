package org.obiba.onyx.webapp.participant.panel;

import java.util.Arrays;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.obiba.onyx.core.domain.participant.Gender;
import org.obiba.onyx.core.domain.participant.Participant;
import org.obiba.onyx.core.domain.participant.Province;
import org.obiba.onyx.core.service.ParticipantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditParticipantPanel extends Panel {

  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(EditParticipantPanel.class);

  /**
   * Regular expression used to validate postal codes.
   * 
   * Note: - No postal code includes the letters D, F, I, O, Q, or U. - The letters W and Z are used, but are not
   * currently used as the first letter.
   */
  private static final String POSTAL_CODE_REGEX = "[A-Z&&[^DFIOQUWZ]]\\d[A-Z&&[^DFIOQU]] \\d[A-Z&&[^DFIOQU]]\\d";

  /**
   * Regular expression used to validate phone numbers.
   * 
   * Note: - Dashes are required between different parts of the phone number. - This is obviously very basic validation.
   * More could be done here to rule out numbers that confirm to this format but are invalid for other reasons (e.g.,
   * 000-000-0000 is not a valid phone number).
   */
  private static final String PHONE_REGEX = "\\d{3}-\\d{3}-\\d{4}";

  @SpringBean
  private ParticipantService participantService;

  private ModalWindow parentWindow;

  private FeedbackPanel feedbackPanel;

  public EditParticipantPanel(String id, IModel participantModel, ModalWindow parentWindow) {
    super(id);

    this.parentWindow = parentWindow;

    Form editParticipantForm = new EditParticipantForm("editParticipantForm", participantModel);
    add(editParticipantForm);

    feedbackPanel = new FeedbackPanel("feedback");
    feedbackPanel.setOutputMarkupId(true);
    add(feedbackPanel);
  }

  private class EditParticipantForm extends Form {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("serial")
    public EditParticipantForm(String id, final IModel participantModel) {
      super(id);
      setModel(participantModel);

      add(new TextField("firstName", new PropertyModel(getModel(), "firstName")).setRequired(true).setLabel(new ResourceModel("FirstName")));
      add(new TextField("lastName", new PropertyModel(getModel(), "lastName")).setRequired(true).setLabel(new ResourceModel("LastName")));
      add(createGenderDropDown());
      add(createBirthDateField());
      add(new TextField("street", new PropertyModel(getModel(), "street")));
      add(new TextField("apartment", new PropertyModel(getModel(), "apartment")));
      add(new TextField("city", new PropertyModel(getModel(), "city")));
      add(createProvinceDropDown());
      add(new TextField("country", new PropertyModel(getModel(), "country")));
      add(new TextField("postalCode", new PropertyModel(getModel(), "postalCode")).add(new PatternValidator(POSTAL_CODE_REGEX)));
      add(new TextField("phone", new PropertyModel(getModel(), "phone")).add(new PatternValidator(PHONE_REGEX)));

      @SuppressWarnings("serial")
      AjaxSubmitLink submitLink = new AjaxSubmitLink("saveAction") {
        protected void onSubmit(AjaxRequestTarget target, Form form) {
          Participant participant = (Participant) EditParticipantForm.this.getModelObject();
          participantService.updateParticipant(participant);
          EditParticipantPanel.this.parentWindow.close(target);
        }

        protected void onError(AjaxRequestTarget target, Form form) {
          target.addComponent(EditParticipantPanel.this.feedbackPanel);
        }
      };
      add(submitLink);

      @SuppressWarnings("serial")
      AjaxLink cancelLink = new AjaxLink("cancelAction") {
        @Override
        public void onClick(AjaxRequestTarget target) {
          EditParticipantPanel.this.parentWindow.close(target);
        }
      };
      add(cancelLink);
    }

    @SuppressWarnings("serial")
    private DateField createBirthDateField() {
      DateField birthDateField = new DateField("birthDate", new PropertyModel(getModel(), "birthDate")) {
        protected DateTextField newDateTextField(String id, PropertyModel dateFieldModel) {
          return DateTextField.forDatePattern(id, dateFieldModel, "yyyy-MM-dd");
        }
      };

      birthDateField.setRequired(true);
      birthDateField.setLabel(new ResourceModel("BirthDate"));

      return birthDateField;
    }

    @SuppressWarnings("serial")
    private DropDownChoice createGenderDropDown() {
      DropDownChoice genderDropDown = new DropDownChoice("gender", new PropertyModel(getModel(), "gender"), Arrays.asList(Gender.values()), new GenderRenderer()) {

        @Override
        protected boolean localizeDisplayValues() {
          // Returning true will make the parent class lookup the value returned by the call
          // to GenderRenderer.getDisplayValue() as a key in the localizer
          // (ie: localizer.getString(renderer.getDisplayValue())
          return true;
        }
      };
      genderDropDown.setRequired(true);
      genderDropDown.setLabel(new ResourceModel("Gender"));
      genderDropDown.setOutputMarkupId(true);

      return genderDropDown;
    }

    @SuppressWarnings("serial")
    private DropDownChoice createProvinceDropDown() {
      DropDownChoice provinceDropDown = new DropDownChoice("province", new PropertyModel(getModel(), "province"), Arrays.asList(Province.values()), new ProvinceRenderer());
      provinceDropDown.setRequired(true);
      provinceDropDown.setOutputMarkupId(true);

      return provinceDropDown;
    }
  }

  @SuppressWarnings("serial")
  private class GenderRenderer implements IChoiceRenderer {

    public Object getDisplayValue(Object object) {
      // Prepend "Gender." to generate the proper resource bundle key
      return "Gender." + object.toString();
    }

    public String getIdValue(Object object, int index) {
      return object.toString();
    }
  }

  @SuppressWarnings("serial")
  private class ProvinceRenderer implements IChoiceRenderer {

    public Object getDisplayValue(Object object) {
      return object.toString();
    }

    public String getIdValue(Object object, int index) {
      return object.toString();
    }
  }
}