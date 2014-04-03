package net.ctrdn.stuba.dbs.netmonitor.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NetmonitorProbe {

    public String author();

    public String name();

    public String description();

    public String version();
}
