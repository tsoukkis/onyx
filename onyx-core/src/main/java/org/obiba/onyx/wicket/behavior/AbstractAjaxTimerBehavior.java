/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.obiba.onyx.wicket.behavior;

import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.util.time.Duration;

/**
 * A behavior that generates an AJAX update callback at a regular interval.
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class AbstractAjaxTimerBehavior extends AbstractDefaultAjaxBehavior {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /** The update interval */
  private Duration updateInterval;

  private boolean stopped = false;

  private boolean headRendered = false;

  /**
   * Construct.
   * 
   * @param updateInterval Duration between AJAX callbacks
   */
  public AbstractAjaxTimerBehavior(final Duration updateInterval) {
    if(updateInterval == null || updateInterval.getMilliseconds() <= 0) {
      throw new IllegalArgumentException("Invalid update interval");
    }
    this.updateInterval = updateInterval;
  }

  @Override
  protected IAjaxCallDecorator getAjaxCallDecorator() {
    return new AjaxCallDecorator() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      @Override
      public CharSequence decorateScript(CharSequence script) {
        return "var timeout;";
      }
    };
  }

  /**
   * Stops the timer
   */
  public final void stop() {
    stopped = true;
  }

  public final void start(final AjaxRequestTarget target) {
    stopped = false;
    target.getHeaderResponse().renderOnLoadJavascript(getJsTimeoutCall(updateInterval));
  }

  /**
   * Sets the update interval duration. This method should only be called within the {@link #onTimer(AjaxRequestTarget)}
   * method.
   * 
   * @param updateInterval
   */
  protected final void setUpdateInterval(Duration updateInterval) {
    if(updateInterval == null || updateInterval.getMilliseconds() <= 0) {
      throw new IllegalArgumentException("Invalid update interval");
    }
    this.updateInterval = updateInterval;
  }

  /**
   * Returns the update interval
   * 
   * @return The update interval
   */
  public final Duration getUpdateInterval() {
    return updateInterval;
  }

  /**
   * @see org.apache.wicket.behavior.AbstractAjaxBehavior#renderHead(org.apache.wicket.markup.html.IHeaderResponse)
   */
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);

    WebRequest request = (WebRequest) RequestCycle.get().getRequest();

    if(!stopped && (!headRendered || !request.isAjax())) {
      headRendered = true;
      response.renderOnLoadJavascript(getJsTimeoutCall(updateInterval));
    }
  }

  /**
   * @param updateInterval Duration between AJAX callbacks
   * @return JS script
   */
  protected final String getJsTimeoutCall(final Duration updateInterval) {
    // this might look strange, but it is necessary for IE not to leak :(
    return "timeout = setTimeout(\"" + getCallbackScript() + "\", " + updateInterval.getMilliseconds() + ");";
  }

  protected CharSequence getCallbackScript() {
    return generateCallbackScript("wicketAjaxGet('" + getCallbackUrl(onlyTargetActivePage()) + "'");
  }

  /**
   * @see org.apache.wicket.ajax.AbstractDefaultAjaxBehavior#getPreconditionScript()
   */
  protected CharSequence getPreconditionScript() {
    String precondition = null;
    if(!(getComponent() instanceof Page)) {
      String componentId = getComponent().getMarkupId();
      precondition = "var c = Wicket.$('" + componentId + "'); return typeof(c) != 'undefined' && c != null";
    }
    return precondition;
  }

  protected boolean onlyTargetActivePage() {
    return true;
  }

  /**
   * 
   * @see org.apache.wicket.ajax.AbstractDefaultAjaxBehavior#respond(org.apache.wicket.ajax.AjaxRequestTarget)
   */
  protected final void respond(final AjaxRequestTarget target) {
    onTimer(target);
    if(!stopped) {
      target.getHeaderResponse().renderOnLoadJavascript(getJsTimeoutCall(updateInterval));
    }
  }

  /**
   * Listener method for the AJAX timer event.
   * 
   * @param target The request target
   */
  protected abstract void onTimer(final AjaxRequestTarget target);
}
