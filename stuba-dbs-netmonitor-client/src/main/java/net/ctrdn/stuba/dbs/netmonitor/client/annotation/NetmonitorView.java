package net.ctrdn.stuba.dbs.netmonitor.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NetmonitorView {

    public String author();

    public String name();

    public String description();

    public String version();

    public int orderKey();

    public String displayName();

}
