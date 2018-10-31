package com.susion.compiler2;

import com.squareup.javapoet.ClassName;

/**
 * Created by susion on 2018/10/27.
 */

public class ImplAnnotationInfo {
    public String name = "";
    public ClassName implClass;

    public ImplAnnotationInfo(String name, ClassName implClass) {
        this.name = name;
        this.implClass = implClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImplAnnotationInfo)) {
            return false;
        }
        return name.equals(((ImplAnnotationInfo) obj).name);
    }
}
