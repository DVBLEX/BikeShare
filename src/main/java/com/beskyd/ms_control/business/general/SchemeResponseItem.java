package com.beskyd.ms_control.business.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SchemeResponseItem {

    private String name;



    public SchemeResponseItem(Scheme original) {
        this.name = original.getName();
    }

    public Scheme toOriginal() {
        return new Scheme(getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SchemeResponseItem other = (SchemeResponseItem) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "SchemeResponseItem [name=" + name + "]";
    }
}
