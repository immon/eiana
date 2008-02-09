package org.iana.rzm.web.common;

public class RzmApplicationError extends RuntimeException {

    private Throwable e;

    public RzmApplicationError(Throwable e, String message) {
        super(message);
        this.e = e;
    }

    public Throwable getError(){
        return e;
    }

    public String getResion(){
        if(e == null){
            return getMessage();
        }
        
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        StringBuilder builder = new StringBuilder();
        if(getMessage() != null){
            builder.append(getMessage()).append("\n");
        }
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            builder.append(stackTraceElement.toString()).append("\n");
        }

        return builder.toString();
    }
}
