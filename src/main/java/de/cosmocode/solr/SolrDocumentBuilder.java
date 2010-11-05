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

package de.cosmocode.solr;

import java.util.Collection;
import java.util.Date;

import org.apache.solr.update.DocumentBuilder;

import com.google.common.base.Function;

/**
 * <p> This is an abstract definition of {@link DocumentBuilder} that also adds some utility methods.
 * </p>
 *
 * @author Oliver Lorenz
 */
public interface SolrDocumentBuilder {

    /**
     * Returns the int suffix, as used by {@link #addDynamicField(String, Integer)}
     * and {@link #addDynamicField(String, int)}.
     * Default is "_i".
     * @return the int suffix, default "_i"
     */
    String getIntSuffix();

    /**
     * Sets the int suffix, as used by {@link #addDynamicField(String, Integer)}
     * and {@link #addDynamicField(String, int)}.
     * Default is "_i".
     * @param intSuffix the new int suffix
     */
    void setIntSuffix(String intSuffix);

    /**
     * Returns the long suffix, as used by {@link #addDynamicField(String, Long)}
     * and {@link #addDynamicField(String, long)}.
     * Default is "_l".
     * @return the long suffix, default "_l"
     */
    String getLongSuffix();

    /**
     * Sets the long suffix, as used by {@link #addDynamicField(String, Long)}
     * and {@link #addDynamicField(String, long)}.
     * Default is "_l".
     * @param longSuffix the new long suffix
     */
    void setLongSuffix(String longSuffix);

    /**
     * Returns the boolean suffix, as used by {@link #addDynamicField(String, Boolean)}
     * and {@link #addDynamicField(String, boolean)}.
     * Default is "_b".
     * @return the boolean suffix, default "_b"
     */
    String getBoolSuffix();

    /**
     * Sets the boolean suffix, as used by {@link #addDynamicField(String, Boolean)}
     * and {@link #addDynamicField(String, boolean)}.
     * Default is "_b".
     * @param boolSuffix the new boolean suffix
     */
    void setBoolSuffix(String boolSuffix);

    /**
     * Returns the double suffix, as used by {@link #addDynamicField(String, Double)}
     * and {@link #addDynamicField(String, double)}.
     * Default is "_d".
     * @return the double suffix, default "_d"
     */
    String getDoubleSuffix();

    /**
     * Sets the double suffix, as used by {@link #addDynamicField(String, Double)}
     * and {@link #addDynamicField(String, double)}.
     * Default is "_d".
     * @param doubleSuffix the new double suffix
     */
    void setDoubleSuffix(String doubleSuffix);

    /**
     * Returns the float suffix, as used by {@link #addDynamicField(String, Float)}
     * and {@link #addDynamicField(String, float)}.
     * Default is "_f".
     * @return the float suffix, default "_f"
     */
    String getFloatSuffix();

    /**
     * Sets the float suffix, as used by {@link #addDynamicField(String, Float)}
     * and {@link #addDynamicField(String, float)}.
     * Default is "_f".
     * @param floatSuffix the new float suffix
     */
    void setFloatSuffix(String floatSuffix);

    /**
     * Returns the date suffix, as used by {@link #addDynamicField(String, Date)}.
     * Default is "_dt".
     * @return the date suffix, default "_dt"
     */
    String getDateSuffix();

    /**
     * Sets the date suffix, as used by {@link #addDynamicField(String, Date)}.
     * Default is "_dt".
     * @param dateSuffix the new date suffix
     */
    void setDateSuffix(String dateSuffix);

    /**
     * Returns the collection suffix, as used by {@link #addDynamicField(String, Collection)}
     * {@link #addDynamicField(String, Collection, Function)} and
     * by {@link #addDynamicField(String, String, StringMode)} with {@value StringMode#SPLIT}.
     * Default is "_tw".
     * @return the collection suffix, default "_tw"
     */
    String getCollectionSuffix();

    /**
     * Sets the collection suffix, as used by {@link #addDynamicField(String, Collection)}
     * {@link #addDynamicField(String, Collection, Function)} and
     * by {@link #addDynamicField(String, String, StringMode)} with {@value StringMode#SPLIT}.
     * Default is "_tw".
     * @param collectionSuffix the new collection suffix
     */
    void setCollectionSuffix(String collectionSuffix);

    /**
     * Returns the String suffix, as used by {@link #addDynamicField(String, String)}
     * and {@link #addDynamicField(String, String, StringMode)} with {@value StringMode#STRING}.
     * Default is "_s".
     * @return the string suffix, default "_s"
     */
    String getStringSuffix();

    /**
     * Sets the String suffix, as used by {@link #addDynamicField(String, String)}
     * and {@link #addDynamicField(String, String, StringMode)} with {@value StringMode#STRING}.
     * Default is "_s".
     * @param stringSuffix the new string suffix
     */
    void setStringSuffix(String stringSuffix);

    /**
     * Returns the text suffix, as used by {@link #addDynamicField(String, String, StringMode)}
     * with {@value StringMode#TEXT}.
     * Default is "_t".
     * @return the text suffix, default "_t"
     */
    String getTextSuffix();

    /**
     * Sets the text suffix. This is used in {@link #addDynamicField(String, String, StringMode)}
     * with {@value StringMode#TEXT}.
     * The default text suffix is "_t".
     * @param textSuffix sets the new text suffix.
     */
    void setTextSuffix(String textSuffix);

    /**
     * <p>
     * Adds a field with the given name and the given boolean value to this builder.
     * </p>
     * <p>
     * This is the same as calling <code>builder.addField(name, Boolean.toString(b))</code>
     * </p>
     * @param name the name of the field
     * @param b the boolean value to add
     */
    void addBooleanField(final String name, final boolean b);

    /**
     * <p>
     * Adds a field with the given name and the given Boolean value to this builder.
     * </p>
     * <p>
     * If the given Boolean is null, then Boolean.FALSE.toString() is added.
     * Otherwise it behaves like <code>builder.addField(name, Boolean.toString(b))</code>
     * </p>
     * @param name the name of the field
     * @param b the Boolean value to add
     */
    void addBooleanField(final String name, final Boolean b);

    /**
     * <p>
     * Adds a field with the given name and the given int value to this builder.
     * This is the same as calling <code>builder.addField(name, Integer.toString(i));</code>
     * </p>
     * @param name the name of the field
     * @param i the int value to add
     */
    void addNumericField(final String name, final int i);

    /**
     * <p>
     * Adds a field with the given name and the given long value to this builder.
     * This is the same as calling <code>builder.addField(name, Long.toString(l));</code>
     * </p>
     * @param name the name of the field
     * @param l the long value to add
     */
    void addNumericField(final String name, final long l);

    /**
     * <p>
     * Adds a field with the given name and the given double value to this builder.
     * This is the same as calling <code>builder.addField(name, Double.toString(d));</code>
     * </p>
     * @param name the name of the field
     * @param d the double value to add
     */
    void addNumericField(final String name, final double d);
    
    /**
     * <p> This method adds a field with the given Date value.
     * It converts the given Date value into a String and takes the given name as the field name.
     * </p>
     * 
     * @param name the name of the field
     * @param dt the date to be added.
     * @return true if adding was successful, false otherwise
     */
    boolean addDateField(final String name, final Date dt);
    
    /**
     * <p> Add the Field and value to the document, invoking the copyField mechanism.
     * </p>
     * <p> Description copied from {@link DocumentBuilder#addField(String, String)}.
     * </p>
     * 
     * @param name The name of the field
     * @param val The value to add
     * 
     * @see #addField(String, String, float)
     */
    void addField(String name, String val);
    
    /**
     * <p> Add the Field and value to the document with the specified boost, invoking the copyField mechanism.
     * </p>
     * <p> Description copied from {@link DocumentBuilder#addField(String, String, float)}.
     * </p>
     * 
     * @param name The name of the field.
     * @param val The value to add
     * @param boost The boost
     *
     * @see #addField(String, String)
     */
    void addField(String name, String val, float boost);

    /**
     * <p> This method adds the given string value to a dynamic text field.
     * It takes the given name and appends the text suffix to the name, which is "_t" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param value the String value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addTextField(final String name, final String value);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the Boolean suffix, which is "_b" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param b the boolean value to add
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final boolean b);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the Boolean suffix, which is "_b" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param b the boolean value to add
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final Boolean b);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the Integer suffix, which is "_i" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param i the int value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final int i);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the Integer suffix, which is "_i" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param i the int value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final Integer i);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>joiner
     * <p> This method uses the Long suffix, which is "_l" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param l the long value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final long l);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the Long suffix, which is "_l" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param l the long value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final Long l);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the double suffix, which is "_d" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param d the double value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final double d);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the double suffix, which is "_d" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param d the double value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final Double d);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the float suffix, which is "_f" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param f the float value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final float f);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the float suffix, which is "_f" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param f the float value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final Float f);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the plain String suffix, which is "_s" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param value the String value to be added
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final String value);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses either the String, the text or the collection suffix,
     * dependent on the input.
     * The default values are:
     * </p>
     * <ul>
     *   <li> String suffix: _s </li>
     *   <li> Text suffix: _t </li>
     *   <li> Collection suffix: _tw </li>
     * </ul>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param value the String value to be added
     * @param mode the String mode to use; affects the suffix used
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final String value, final StringMode mode);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * <p> This method uses the date suffix, which is "_dt" by default.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param dt the date to be added.
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final Date dt);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * 
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param c the collection to be added.
     * @return true if adding was successful, false otherwise
     */
    boolean addDynamicField(final String name, final Collection<?> c);

    /**
     * <p> This method adds a given to value to a dynamic field.
     * It takes the given name and appends a previously known suffix to the name.
     * </p>
     * 
     * @param <K> the generic type of the collection to add
     * @param name the name of the dynamic field (without the suffix of the dynamic field)
     * @param c the collection to be added.
     * @param function the function that joins the Elements of the collection together
     * @return true if adding was successful, false otherwise
     */
    <K> boolean addDynamicField(final String name,
            final Collection<? extends K> c,
            final Function<? super K, String> function);

    /**
     * Splits the given value at blanks and calls {@link #addMultiField(String, Object[])}.
     * 
     * @param name the name of the multi-field
     * @param value the value to add
     * @return true if adding was successful, false otherwise
     * 
     * @see #addMultiField(String, Object[])
     */
    boolean addMultiField(final String name, final String value);

    /**
     * Adds the given values in an array to a multi-field.
     * This method does not check if the given field is able to handle multiple values.
     * If at least one value could be added then this method returns true, false otherwise.
     * 
     * @param <K> the generic type of the array.
     * @param name the name of the multi-field
     * @param values the values to add (as strings)
     * @return true if at least one value was added, false otherwise
     */
    <K> boolean addMultiField(final String name, final K[] values);

    /**
     * Adds the given values in a collection to a multi-field.
     * This method does not check if the given field is able to handle multiple values.
     * If at least one value could be added then this method returns true, false otherwise.
     * 
     * @param name the name of the multi-field
     * @param values the values to add (toString() is called on each for conversion)
     * @return true if at least one value was added, false otherwise
     */
    boolean addMultiField(final String name, final Collection<?> values);

}
