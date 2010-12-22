/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * All rights reserved
 */

package de.cosmocode.solr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.solr.schema.IndexSchema;
import org.apache.solr.update.DocumentBuilder;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * <p> A class that extends {@link DocumentBuilder} and adds some utility methods,
 * specified in {@link SolrDocumentBuilder}.
 * </p>
 * <p> This class is not threadsafe because its superclass, DocumentBuilder, is neither.
 * </p> 
 *
 * @author Oliver Lorenz
 */
public final class UtilityDocumentBuilder extends DocumentBuilder implements SolrDocumentBuilder {
    
    public static final DateFormat SOLR_DATETIMEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    private static final Joiner JOINER = Joiner.on(' ').skipNulls();
    
    private String intSuffix = "_i";
    private String longSuffix = "_l";
    private String boolSuffix = "_b";
    private String doubleSuffix = "_d";
    private String floatSuffix = "_f";
    private String dateSuffix = "_dt";
    private String collectionSuffix = "_tw";
    private String stringSuffix = "_s";
    private String textSuffix = "_t";
    
    public UtilityDocumentBuilder(final IndexSchema schema) {
        super(schema);
    }

    @Override
    public String getIntSuffix() {
        return intSuffix;
    }

    @Override
    public void setIntSuffix(String intSuffix) {
        this.intSuffix = intSuffix;
    }

    @Override
    public String getLongSuffix() {
        return longSuffix;
    }

    @Override
    public void setLongSuffix(String longSuffix) {
        this.longSuffix = longSuffix;
    }

    @Override
    public String getBoolSuffix() {
        return boolSuffix;
    }

    @Override
    public void setBoolSuffix(String boolSuffix) {
        this.boolSuffix = boolSuffix;
    }

    @Override
    public String getDoubleSuffix() {
        return doubleSuffix;
    }

    @Override
    public void setDoubleSuffix(String doubleSuffix) {
        this.doubleSuffix = doubleSuffix;
    }

    @Override
    public String getFloatSuffix() {
        return floatSuffix;
    }

    @Override
    public void setFloatSuffix(String floatSuffix) {
        this.floatSuffix = floatSuffix;
    }

    @Override
    public String getDateSuffix() {
        return dateSuffix;
    }

    @Override
    public void setDateSuffix(String dateSuffix) {
        this.dateSuffix = dateSuffix;
    }

    @Override
    public String getCollectionSuffix() {
        return collectionSuffix;
    }

    @Override
    public void setCollectionSuffix(String collectionSuffix) {
        this.collectionSuffix = collectionSuffix;
    }

    @Override
    public String getStringSuffix() {
        return stringSuffix;
    }

    @Override
    public void setStringSuffix(String stringSuffix) {
        this.stringSuffix = stringSuffix;
    }

    @Override
    public String getTextSuffix() {
        return textSuffix;
    }

    @Override
    public void setTextSuffix(String textSuffix) {
        this.textSuffix = textSuffix;
    }

    @Override
    public void addBooleanField(final String name, final boolean b) {
        super.addField(name, Boolean.toString(b));
    }
    
    @Override
    public void addBooleanField(final String name, final Boolean b) {
        if (b == null) {
            super.addField(name, Boolean.FALSE.toString());
        } else {
            super.addField(name, b.toString());
        }
    }
    
    @Override
    public void addNumericField(final String name, final int i) {
        super.addField(name, Integer.toString(i));
    }
    
    @Override
    public void addNumericField(final String name, final long l) {
        super.addField(name, Long.toString(l));
    }
    
    @Override
    public void addNumericField(final String name, final double d) {
        super.addField(name, Double.toString(d));
    }
    
    @Override
    public boolean addDateField(final String name, final Date dt) {
        if (dt == null) {
            return false;
        } else {
            synchronized (SOLR_DATETIMEFORMAT) {
                final String dateFormatted = SOLR_DATETIMEFORMAT.format(dt);
                super.addField(name, dateFormatted);
            }
            return true;
        }
    }
    
    @Override
    public boolean addTextField(final String name, final String value) {
        if (value == null) {
            return false;
        } else {
            super.addField(name + textSuffix, value);
            return true;
        }
    }
    
    
    @Override
    public boolean addDynamicField(final String name, final boolean b) {
        addBooleanField(name + boolSuffix, b);
        return true;
    }
    
    @Override
    public boolean addDynamicField(final String name, final Boolean b) {
        addBooleanField(name + boolSuffix, b);
        return true;
    }
    
    @Override
    public boolean addDynamicField(final String name, final int i) {
        super.addField(name + intSuffix, Integer.toString(i));
        return true;
    }
    
    @Override
    public boolean addDynamicField(final String name, final Integer i) {
        if (i == null) {
            return false;
        } else {
            super.addField(name + intSuffix, i.toString());
            return true;
        }
    }
    
    @Override
    public boolean addDynamicField(final String name, final long l) {
        super.addField(name + longSuffix, Long.toString(l));
        return true;
    }
    
    @Override
    public boolean addDynamicField(final String name, final Long l) {
        if (l == null) {
            return false;
        } else {
            super.addField(name + longSuffix, l.toString());
            return true;
        }
    }
    
    @Override
    public boolean addDynamicField(final String name, final double d) {
        super.addField(name + doubleSuffix, Double.toString(d));
        return true;
    }
    
    @Override
    public boolean addDynamicField(final String name, final Double d) {
        if (d == null) {
            return false;
        } else {
            super.addField(name + doubleSuffix, d.toString());
            return true;
        }
    }
    
    @Override
    public boolean addDynamicField(final String name, final float f) {
        super.addField(name + floatSuffix, Float.toString(f));
        return true;
    }
    
    @Override
    public boolean addDynamicField(final String name, final Float f) {
        if (f == null) {
            return false;
        } else {
            super.addField(name + floatSuffix, f.toString());
            return true;
        }
    }
    
    @Override
    public boolean addDynamicField(final String name, final String value) {
        if (value == null) {
            return false;
        } else {
            super.addField(name + stringSuffix, value);
            return true;
        }
    }
    
    @Override
    public boolean addDynamicField(final String name, final String value, final StringMode mode) {
        if (value == null) {
            return false;
        } else {
            switch (mode) {
                case STRING: {
                    super.addField(name + stringSuffix, value);
                    return true;
                }
                case TEXT: {
                    super.addField(name + textSuffix, value);
                    return true;
                }
                case SPLIT: {
                    super.addField(name + collectionSuffix, value);
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    @Override
    public boolean addDynamicField(final String name, final Date dt) {
        return addDateField(name + dateSuffix, dt);
    }
    
    @Override
    public boolean addDynamicField(final String name, final Collection<?> c) {
        if (c == null) {
            return false;
        } else {
            super.addField(name + collectionSuffix, JOINER.join(c));
            return true;
        }
    }
    
    @Override
    public <K> boolean addDynamicField(
        final String name,
        final Collection<? extends K> c,
        final Function<? super K, String> function) {
        
        if (c == null || c.size() == 0 || function == null) {
            return false;
        } else {
            super.addField(name + collectionSuffix, JOINER.join(Iterables.transform(c, function)));
            return true;
        }
    }
    
    @Override
    public boolean addMultiField(final String name, final String value) {
        if (value == null) {
            return false;
        } else {
            return this.addMultiField(name, value.split(" "));
        }
    }
    
    @Override
    public <K> boolean addMultiField(final String name, final K[] values) {
        if (values == null) {
            return false;
        } else {
            boolean wasAdded = false;
            for (K value : values) {
                if (value == null) continue;
                super.addField(name, value.toString());
                wasAdded = true;
            }
            return wasAdded;
        }
    }
    
    @Override
    public boolean addMultiField(final String name, final Collection<?> values) {
        if (values == null) {
            return false;
        } else {
            boolean wasAdded = false;
            for (Object value : values) {
                if (value == null) continue;
                super.addField(name, value.toString());
                wasAdded = true;
            }
            return wasAdded;
        }
    }

}
