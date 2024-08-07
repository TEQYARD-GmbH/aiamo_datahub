package com.linkedin.metadata.utils.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.ArrayList;
import java.util.List;

/**
 * A Log Filter that can be configured to omit logs containing a specific message string. Configured
 * inside logback.xml.
 */
public class LogMessageFilter extends AbstractMatcherFilter<ILoggingEvent> {

  /** A set of messages to exclude. */
  private final List<String> excluded = new ArrayList<>();

  @Override
  public FilterReply decide(ILoggingEvent event) {
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }

    final String formattedMessage = event.getFormattedMessage();
    final IThrowableProxy throwableProxy = event.getThrowableProxy();

    String throwableString;
    if (throwableProxy != null) {
      throwableString = ThrowableProxyUtil.asString(throwableProxy);
    } else {
      throwableString = null;
    }

    if (this.excluded.stream()
        .anyMatch(
            message ->
                formattedMessage.contains(message)
                    || (throwableString != null && throwableString.contains(message)))) {
      return FilterReply.DENY;
    }
    return FilterReply.ACCEPT;
  }

  public void addExcluded(String message) {
    this.excluded.add(message);
  }
}
