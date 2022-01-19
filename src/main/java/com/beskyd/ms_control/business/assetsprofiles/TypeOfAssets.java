package com.beskyd.ms_control.business.assetsprofiles;

import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "types_of_assets")
public class TypeOfAssets implements Serializable, JsonAware{
    
    public static final String FULFILLMENT_GROUP = "Fulfillment";
    public static final String FULFILLMENT_GROUP_LC = "fulfillment";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    private String assetGroup;
    
    @NotNull
    private String typeName;

    @NotNull
    private String groupName;

    @OneToMany(mappedBy = "type", fetch = FetchType.EAGER)
    private Set<Product> products;
    
    @Override
    public String toString() {
        return "TypeOfAssets [id=" + id + ", assetGroup=" + assetGroup + ", typeName=" + typeName + "]";
    }

    public TypeOfAssets(@NotNull String assetGroup, @NotNull String typeName, @NotNull String groupName) {
        this.assetGroup = assetGroup;
        this.typeName = typeName;
        this.groupName = groupName;
    }

    /**
     * Copy constructor
     * @param other object to copy from
     */
    public TypeOfAssets(TypeOfAssets other) {
        this.id = other.getId();
        this.assetGroup = other.getAssetGroup();
        this.typeName = other.getTypeName();
        this.groupName = other.getGroupName();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assetGroup == null) ? 0 : assetGroup.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypeOfAssets other = (TypeOfAssets) obj;
        if (assetGroup == null) {
            if (other.assetGroup != null)
                return false;
        } else if (!assetGroup.equals(other.assetGroup))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (groupName == null) {
            if (other.groupName != null)
                return false;
        } else if (!groupName.equals(other.groupName))
            return false;
        return true;
    }

    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("assetGroup", assetGroup);
        jo.put("typeName", typeName);
        jo.put("groupName", groupName);
        return jo;
    }
}
