package de.cosmocode.solr;

public enum QueryModifiers {
    
    // TODO: javadoc fuer alle elemente
    none(false),
    
    required(false),
    
    prohibited(false),
    
    wildcarded(true),
    
    required_wildcarded(true),
    
    prohibited_wildcarded(true),
    
    required_field(false),
    
    required_field_all_values(false),
    
    required_field_wildcarded(true),
    
    required_field_all_values_wildcarded(true),
    
    prohibited_field(false),
    
    prohibited_field_all_values(false),
    
    prohibited_field_wildcarded(true),
    
    prohibited_field_all_values_wildcarded(true),
    
    ;
    
    private final boolean _wildcarded;
    
    private QueryModifiers(boolean _wildcarded) {
        this._wildcarded = _wildcarded;
    }
    
    public boolean is_wildcarded() {
        return _wildcarded;
    }
    
    
    public static QueryModifiers parse(final boolean fieldRequired, final boolean fieldProhibited,
                                        final boolean valueRequired, final boolean valueProhibited,
                                        final boolean _wildcarded) {
        final Boolean field;
        final Boolean value;
        if (fieldRequired) field = Boolean.TRUE;
        else if (fieldProhibited) field = Boolean.FALSE;
        else field = null;
        if (valueRequired) value = Boolean.TRUE;
        else if (valueProhibited) value = Boolean.FALSE;
        else value = null;
        return parse(field, value, _wildcarded);
    }
    
    public static QueryModifiers parse(final Boolean field, final Boolean value, final Boolean _wildcarded) {
        if (field == null) {
            // field is null (i.e. not set)
            if (value == null) {
                // value is null (i.e. not set)
                if (Boolean.TRUE.equals(_wildcarded)) {
                    return wildcarded;
                } else {
                    return none;
                }
            } else if (Boolean.TRUE.equals(value)) {
                // value is true (required)
                if (Boolean.TRUE.equals(_wildcarded)) {
                    return required_wildcarded;
                } else {
                    return required;
                }
            } else {
                // value is false (prohibited)
                if (Boolean.TRUE.equals(_wildcarded)) {
                    return prohibited_wildcarded;
                } else {
                    return prohibited;
                }
            }
        } else if (Boolean.TRUE.equals(field)) {
            // field is true (required)
            if (value == null) {
                if (Boolean.TRUE.equals(_wildcarded)) {
                    return required_field_wildcarded;
                } else {
                    return required_field;
                }
            } else if (Boolean.TRUE.equals(value)) {
                // value is true (required)
                if (Boolean.TRUE.equals(_wildcarded)) {
                    return required_field_all_values_wildcarded;
                } else {
                    return required_field_all_values;
                }
            } else {
                // value is false (prohibited)
                // field is required, but values are prohibited;
                // that means: get all docs, where the field is set and has not the given values
                if (Boolean.TRUE.equals(_wildcarded)) {
                    return prohibited_field_wildcarded;
                } else {
                    return prohibited_field;
                }
            }
        } else {
            // field is false (prohibited)
            if (value == null) {
                if (Boolean.TRUE.equals(_wildcarded)) {
                    return prohibited_field_wildcarded;
                } else {
                    return prohibited_field;
                }
            } else if (Boolean.TRUE.equals(value)) {
                // value is true (required)
                if (Boolean.TRUE.equals(_wildcarded)) {
                    return prohibited_field_all_values_wildcarded;
                } else {
                    return prohibited_field_all_values;
                }
            } else {
                // value is false (prohibited)
                // field is prohibited, and values are prohibited;
                // that means: get all docs, where the field is set and has not the values not given in values,
                //             so, it's a double negation meaning: ... where the field has all the given values
                if (Boolean.TRUE.equals(_wildcarded)) {
                    return required_field_all_values_wildcarded;
                } else {
                    return required_field_all_values;
                }
            }
        }
    }

}
