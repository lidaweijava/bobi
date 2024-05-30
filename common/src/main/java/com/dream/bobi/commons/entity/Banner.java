package com.dream.bobi.commons.entity;

import javax.persistence.Column;

public class Banner extends BaseEntity{
    private String uri;
    @Column(name = "`schema`")
    private String schema;

    private Integer type;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
