/*
* Copyright 2015 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.capgemini.gregor;

import java.util.List;
import java.util.Map;

public class TestObject {
    private String stringValue;
    
    private int intValue;
    
    private boolean booleanValue;
    
    private List<String> listValue;
    
    private Map<String, String> mapValue;

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public List<String> getListValue() {
        return listValue;
    }

    public void setListValue(List<String> listValue) {
        this.listValue = listValue;
    }

    public Map<String, String> getMapValue() {
        return mapValue;
    }

    public void setMapValue(Map<String, String> mapValue) {
        this.mapValue = mapValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestObject that = (TestObject) o;

        if (getIntValue() != that.getIntValue()) return false;
        if (isBooleanValue() != that.isBooleanValue()) return false;
        if (getStringValue() != null ? !getStringValue().equals(that.getStringValue()) : that.getStringValue() != null)
            return false;
        if (getListValue() != null ? !getListValue().equals(that.getListValue()) : that.getListValue() != null)
            return false;
        return getMapValue() != null ? getMapValue().equals(that.getMapValue()) : that.getMapValue() == null;

    }

    @Override
    public int hashCode() {
        int result = getStringValue() != null ? getStringValue().hashCode() : 0;
        result = 31 * result + getIntValue();
        result = 31 * result + (isBooleanValue() ? 1 : 0);
        result = 31 * result + (getListValue() != null ? getListValue().hashCode() : 0);
        result = 31 * result + (getMapValue() != null ? getMapValue().hashCode() : 0);
        return result;
    }
}
