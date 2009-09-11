/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.wicket.data.validation.converter;

import java.util.List;

import junit.framework.Assert;

import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.MaximumValidator;
import org.apache.wicket.validation.validator.MinimumValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.RangeValidator;
import org.junit.Before;
import org.junit.Test;
import org.obiba.onyx.util.data.DataType;
import org.obiba.onyx.wicket.data.DataValidator;
import org.obiba.onyx.wicket.data.IDataValidator;

import com.thoughtworks.xstream.XStream;

/**
 * 
 */
public class DataValidatorConverterTest {

  XStream xstream;

  @Before
  public void setup() throws ClassNotFoundException {
    xstream = new XStream();
    xstream.registerConverter(new DataValidatorConverter().createAliases(xstream));
  }

  @Test
  public void testUnmarshalIntegerRangeValidator() {
    String testData = "<rangeValidator type=\"integer\"><minimum>10</minimum><maximum>100</maximum></rangeValidator>";
    Object o = xstream.fromXML(testData);
    Assert.assertEquals(DataValidator.class, o.getClass());
    DataValidator validator = (DataValidator) o;
    Assert.assertEquals(DataType.INTEGER, validator.getDataType());

    RangeValidator rangeValidator = (RangeValidator) validator.getValidator();
    Assert.assertEquals(10l, rangeValidator.getMinimum());
    Assert.assertEquals(100l, rangeValidator.getMaximum());
  }

  @Test
  public void testUnmarshalIntegerMinimumValidator() {
    String testData = "<rangeValidator type=\"integer\"><minimum>10</minimum></rangeValidator>";
    Object o = xstream.fromXML(testData);
    Assert.assertEquals(DataValidator.class, o.getClass());
    DataValidator validator = (DataValidator) o;
    Assert.assertEquals(DataType.INTEGER, validator.getDataType());

    MinimumValidator minimumValidator = (MinimumValidator) validator.getValidator();
    Assert.assertEquals(10l, minimumValidator.getMinimum());
  }

  @Test
  public void testUnmarshalIntegerMaximumValidator() {
    String testData = "<rangeValidator type=\"integer\"><maximum>100</maximum></rangeValidator>";
    Object o = xstream.fromXML(testData);
    Assert.assertEquals(DataValidator.class, o.getClass());
    DataValidator validator = (DataValidator) o;
    Assert.assertEquals(DataType.INTEGER, validator.getDataType());

    MaximumValidator maximumValidator = (MaximumValidator) validator.getValidator();
    Assert.assertEquals(100l, maximumValidator.getMaximum());
  }

  @Test
  public void testUnmarshalDecimalRangeValidator() {
    String testData = "<rangeValidator type=\"DeCiMaL\"><minimum>10</minimum><maximum>100</maximum></rangeValidator>";
    Object o = xstream.fromXML(testData);
    Assert.assertEquals(DataValidator.class, o.getClass());
    DataValidator validator = (DataValidator) o;
    Assert.assertEquals(DataType.DECIMAL, validator.getDataType());

    RangeValidator rangeValidator = (RangeValidator) validator.getValidator();
    Assert.assertEquals(10.0, rangeValidator.getMinimum());
    Assert.assertEquals(100.0, rangeValidator.getMaximum());
  }

  @Test
  public void testUnmarshalDateRangeValidator() {
    String testData = "<rangeValidator type=\"date\"><minimum>1900-01-01</minimum><maximum>1999-12-31</maximum></rangeValidator>";
    Object o = xstream.fromXML(testData);
    Assert.assertEquals(DataValidator.class, o.getClass());
    DataValidator validator = (DataValidator) o;
    Assert.assertEquals(DataType.DATE, validator.getDataType());
    IValidator dateValidator = validator.getValidator();
    // TODO: Use reflection to get the minimum and maximum attributes

  }

  @Test
  public void testUnmarshalPatternValidator() {
    String testData = "<patternValidator>\\d+</patternValidator>";
    Object o = xstream.fromXML(testData);
    Assert.assertEquals(DataValidator.class, o.getClass());
    DataValidator validator = (DataValidator) o;
    Assert.assertEquals(DataType.TEXT, validator.getDataType());
    PatternValidator patternValidator = (PatternValidator) validator.getValidator();
    Assert.assertEquals("\\d+", patternValidator.getPattern().toString());
  }

  @Test
  public void testIDataValidatorAsAttributeOfBean() {
    xstream.alias("myBean", MyBean.class);
    String testData = "<myBean><dataValidator class=\"rangeValidator\" type=\"integer\"><minimum>10</minimum><maximum>100</maximum></dataValidator></myBean>";
    Object o = xstream.fromXML(testData);
    Assert.assertEquals(MyBean.class, o.getClass());
    MyBean bean = (MyBean) o;
    Assert.assertNotNull(bean.dataValidator);
    Assert.assertEquals(DataType.INTEGER, bean.dataValidator.getDataType());

    DataValidator validator = (DataValidator) bean.dataValidator;
    RangeValidator rangeValidator = (RangeValidator) validator.getValidator();
    Assert.assertEquals(10l, rangeValidator.getMinimum());
    Assert.assertEquals(100l, rangeValidator.getMaximum());
  }

  @Test
  public void testIDataValidatorListAsAttributeOfBean() {
    xstream.alias("myBean", MyBean.class);
    String testData = "<myBean><dataValidators><rangeValidator type=\"integer\"><minimum>10</minimum><maximum>100</maximum></rangeValidator><patternValidator>\\d+</patternValidator></dataValidators></myBean>";
    Object o = xstream.fromXML(testData);
    Assert.assertEquals(MyBean.class, o.getClass());
    MyBean bean = (MyBean) o;
    Assert.assertNotNull(bean.dataValidators);
    Assert.assertEquals(2, bean.dataValidators.size());

    DataValidator validator = (DataValidator) bean.dataValidators.get(0);
    RangeValidator rangeValidator = (RangeValidator) validator.getValidator();
    Assert.assertEquals(10l, rangeValidator.getMinimum());
    Assert.assertEquals(100l, rangeValidator.getMaximum());

    validator = (DataValidator) bean.dataValidators.get(1);
    Assert.assertEquals(DataType.TEXT, validator.getDataType());
    PatternValidator patternValidator = (PatternValidator) validator.getValidator();
    Assert.assertEquals("\\d+", patternValidator.getPattern().toString());
  }

  public static class MyBean {
    IDataValidator dataValidator;

    List<IDataValidator> dataValidators;
  }
}
