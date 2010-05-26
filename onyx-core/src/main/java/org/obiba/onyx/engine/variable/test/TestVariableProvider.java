/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.engine.variable.test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.obiba.onyx.core.domain.participant.Participant;
import org.obiba.onyx.engine.variable.IVariablePathNamingStrategy;
import org.obiba.onyx.engine.variable.IVariableProvider;
import org.obiba.onyx.engine.variable.Variable;
import org.obiba.onyx.engine.variable.VariableData;
import org.obiba.onyx.engine.variable.VariableDataSet;
import org.obiba.onyx.engine.variable.util.VariableStreamer;
import org.obiba.onyx.util.data.RandomDataBuilder;

/**
 * 
 */
public class TestVariableProvider implements IVariableProvider {
  private IVariablePathNamingStrategy strategy;

  private List<Variable> testVariables = new LinkedList<Variable>();

  private Map<Participant, VariableDataSet> dataSets = new HashMap<Participant, VariableDataSet>();

  public TestVariableProvider(IVariablePathNamingStrategy strategy) {
    // Doing this will always generate the same random data.
    RandomDataBuilder.setRandomSeed(1);
    this.strategy = strategy;
  }

  public void addVariables(InputStream is) {
    Variable rootVar = VariableStreamer.fromXML(is);
    testVariables.add(rootVar);
  }

  public VariableData getVariableData(Participant participant, Variable variable, IVariablePathNamingStrategy variablePathNamingStrategy) {
    VariableDataSet dataSet = dataSets.get(participant);
    if(dataSet != null) {
      List<VariableData> datas = dataSet.getVariableDatas();
      for(VariableData variableData : datas) {
        String varPath = variablePathNamingStrategy.getPath(variable);
        if(variableData.getVariablePath().equals(varPath)) {
          return variableData;
        }
      }
    }
    return null;
  }

  public List<Variable> getVariables() {
    return testVariables;
  }

  public void createRandomData(Participant participant) {
    createRandomData(testVariables, participant);
  }

  public void createRandomData(List<Variable> variables, Participant participant) {
    if(variables != null) {
      for(Variable variable : variables) {
        createRandomData(variable, participant);
        createRandomData(variable.getVariables(), participant);
      }
    }
  }

  public void createRandomData(Variable variable, Participant participant) {
    if(variable != null) {
      if(variable.getDataType() != null) {
        VariableData data = new VariableData(strategy.getPath(variable));
        data.addData(RandomDataBuilder.buildRandom(variable.getDataType()));
        addToDataset(participant, data);
      }
    }
  }

  private void addToDataset(Participant participant, VariableData data) {
    VariableDataSet dataSet = dataSets.get(participant);
    if(dataSet == null) {
      dataSet = new VariableDataSet();
      dataSets.put(participant, dataSet);
    }
    dataSet.addVariableData(data);
  }

  public List<Variable> getContributedVariables(Variable root, IVariablePathNamingStrategy variablePathNamingStrategy) {
    return null;
  }
}