package net.n2oapp.framework.api.ui;

import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.exception.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Сборка сообщения об ошибке в формате клиента
 */
public class ErrorMessageBuilder {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private MessageSourceAccessor messageSourceAccessor;
    private Boolean showStacktrace = true;

    public ErrorMessageBuilder(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    public ErrorMessageBuilder(MessageSourceAccessor messageSourceAccessor, Boolean showStacktrace) {
        this.messageSourceAccessor = messageSourceAccessor;
        this.showStacktrace = showStacktrace;
    }

    public ResponseMessage build(Exception e) {
        ResponseMessage resp = new ResponseMessage();
        resp.setText(buildText(e));
        if (showStacktrace && !(e instanceof N2oUserException))
            resp.setStacktrace(getStackFrames(getStackTrace(e)));
        if (e instanceof N2oException) {
            resp.setSeverityType(((N2oException) e).getSeverity());
            resp.setField(((N2oException) e).getField());
        } else {
            resp.setSeverityType(SeverityType.danger);
        }
        return resp;
    }

    private List<ResponseMessage> buildValidationMessages(N2oValidationException e) {
        List<ResponseMessage> messages = new ArrayList<>();
        if (e.getMessages() != null) {
            for (ValidationMessage message : e.getMessages()) {
                ResponseMessage resp = new ResponseMessage();
                //resp.setChoice(e.getChoice()); todo use dialog
                resp.setSeverityType(e.getSeverity());
                resp.setField(message.getFieldId());
                resp.setText(message.getMessage());
                messages.add(resp);
            }
        }
        return messages;
    }

    private String getStackTrace(Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    private List<String> getStackFrames(String stacktrace) {
        StringTokenizer frames = new StringTokenizer(stacktrace, LINE_SEPARATOR);
        List<String> list = new ArrayList<>();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return list;
    }

    private String buildText(Exception e) {
        String message = "n2o.exceptions.error.message";
        String userMessage = e instanceof N2oException ? ((N2oException) e).getUserMessage() : null;
        message = userMessage != null ? userMessage : message;
        String localizedMessage = messageSourceAccessor.getMessage(message, message);
        if (e instanceof N2oException)
            return StringUtils.resolveLinks(localizedMessage, ((N2oException) e).getData());
        else
            return localizedMessage;
    }

    public List<ResponseMessage> buildMessages(Exception e) {
        return e instanceof N2oValidationException
                ? buildValidationMessages((N2oValidationException) e)
                : Collections.singletonList(build(e));
    }

}
