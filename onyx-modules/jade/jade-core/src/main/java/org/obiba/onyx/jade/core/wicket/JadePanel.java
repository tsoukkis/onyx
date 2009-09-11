/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.jade.core.wicket;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.obiba.onyx.engine.ModuleRegistry;
import org.obiba.onyx.engine.Stage;
import org.obiba.onyx.jade.core.domain.instrument.InstrumentType;
import org.obiba.onyx.jade.core.service.InstrumentService;
import org.obiba.onyx.jade.core.wicket.wizard.InstrumentWizardPanel;
import org.obiba.onyx.wicket.IEngineComponentAware;
import org.obiba.onyx.wicket.StageModel;
import org.obiba.onyx.wicket.action.ActionWindow;
import org.obiba.onyx.wicket.reusable.FeedbackWindow;

public class JadePanel extends Panel implements IEngineComponentAware {

  private static final long serialVersionUID = -6692482689347742363L;

  @SpringBean
  private ModuleRegistry moduleRegistry;

  @SpringBean
  private InstrumentService instrumentService;

  private ActionWindow actionWindow;

  private FeedbackWindow feedbackWindow;

  private JadeModel model;

  private InstrumentWizardPanel wizardPanel;

  @SuppressWarnings("serial")
  public JadePanel(String id, Stage stage, boolean resuming) {
    super(id);

    InstrumentType type = getInstrumentType(stage);
    setDefaultModel(model = new JadeModel(new StageModel(moduleRegistry, stage.getName()), new InstrumentTypeModel(type)));

    add(wizardPanel = new InstrumentWizardPanel("content", model.intrumentTypeModel, new StageModel(moduleRegistry, stage.getName()), resuming));
  }

  private InstrumentType getInstrumentType(Stage stage) {
    return instrumentService.getInstrumentType(stage.getName());
  }

  public void setActionWindow(ActionWindow window) {
    wizardPanel.setActionWindow(window);
  }

  public void setFeedbackWindow(FeedbackWindow feedbackWindow) {
    wizardPanel.setFeedbackWindow(feedbackWindow);
  }

  public FeedbackWindow getFeedbackWindow() {
    return feedbackWindow;
  }

  @SuppressWarnings("serial")
  private class JadeModel extends AbstractReadOnlyModel {

    private IModel intrumentTypeModel;

    private IModel stageModel;

    public JadeModel(IModel stageModel, IModel instrumentTypeModel) {
      this.intrumentTypeModel = instrumentTypeModel;
      this.stageModel = stageModel;
    }

    public InstrumentType getIntrumentType() {
      return (InstrumentType) intrumentTypeModel.getObject();
    }

    public Stage getStage() {
      return (Stage) stageModel.getObject();
    }

    public IModel getIntrumentTypeModel() {
      return intrumentTypeModel;
    }

    public IModel getStageModel() {
      return stageModel;
    }

    @Override
    public void detach() {
      this.stageModel.detach();
      this.intrumentTypeModel.detach();
      super.detach();
    }

    @Override
    public Object getObject() {
      return null;
    }
  }

}
