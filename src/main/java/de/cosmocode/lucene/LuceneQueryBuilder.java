package de.cosmocode.lucene;

import java.util.Collection;


/**
 * An interface that specifies a builder for Lucene queries.
 * An abstract implemententation, that takes care of the most simple 
 * 
 * @see AbstractLuceneQueryBuilder
 * 
 * @author olorenz
 *
 */
public interface LuceneQueryBuilder {
    
    /**
     * The default fuzzyness. It is used by
     * <ul>
     *  <li> {@link LuceneQueryBuilder#addFuzzyArgument(String)}</li>
     *  <li> {@link LuceneQueryBuilder#addFuzzyArgument(String, boolean)}</li>
     *  <li> {@link LuceneQueryBuilder#addFuzzyArgument(String, QueryModifier)}</li>
     *  <li> {@link LuceneQueryBuilder#addFuzzyField(String, String)}</li>
     *  <li> {@link LuceneQueryBuilder#addFuzzyField(String, String, boolean)}</li>
     *  <li> {@link LuceneQueryBuilder#addFuzzyField(String, String, QueryModifier)}</li>
     * </ul>
     */
    public static final double defaultFuzzyness = 0.5;
    
    // TODO: JavaDoc
    
    
    /**
     * Sets a default QueryModifier that is used
     * whenever a method is invoked without a QueryModifier parameter.
     */
    public void setDefaultQueryModifier(final QueryModifier mod);
    
    
    /**
     * Gets the default QueryModifier that is used
     * whenever a method is invoked without a QueryModifier parameter.
     * 
     * @return the default QueryModifier
     */
    public QueryModifier getDefaultQueryModifier();
    
    
    /**
     * @return the query which was built with the add...-methods
     */
    public String getQuery();
    
    
    //---------------------------
    //     addFuzzyArgument
    //---------------------------
    
    
    /**
     * Append a fuzzy term. <br>
     * fuzzy searches include terms that are in the levenshtein distance of the searched term.
     * <br><br>
     * This method uses the {@link #getDefaultQueryModifier()} and {@link #defaultFuzzyness}.
     * 
     * @param value the value to search for
     * @return this
     */
    public LuceneQueryBuilder addFuzzyArgument (String value);

    
    /**
     * Append a fuzzy term with default fuzzyness of 0.5. <br>
     * fuzzy searches include terms that are in the levenshtein distance of the searched term.
     * <br><br>
     * This method uses the {@link #defaultFuzzyness}.
     * 
     * @see #addFuzzyArgument(String, boolean, double)
     * @param value the value to search for
     * @param mandatory if true then the value must be found, otherwise it is just prioritized in the search results
     * @return this
     */
    public LuceneQueryBuilder addFuzzyArgument (String value, boolean mandatory);
    
    
    /**
     * Append a fuzzy argument with the given fuzzyness. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term.
     * 
     * @param value the value to search for
     * @param mandatory if true then the value must be found, otherwise it is just prioritized in the search results
     * @param fuzzyness the fuzzyness; must be between 0 (inclusive) and 1 (exclusive), so that: 0 <= fuzzyness < 1
     * @return this
     */
    public LuceneQueryBuilder addFuzzyArgument (String value, boolean mandatory, double fuzzyness);
    
    
    /**
     * Append a fuzzy term with default fuzzyness of 0.5. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term.
     * <br><br>
     * This method uses the {@link #defaultFuzzyness}.
     * 
     * @param value the value to search for
     * @param modifier the QueryModifier affects the way in that the argument is added.
     * @return this
     */
    public LuceneQueryBuilder addFuzzyArgument (String value, QueryModifier modifier);
    
    
    /**
     * Append a fuzzy argument with the given fuzzyness. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term.
     * 
     * @param value the value to search for
     * @param modifier the QueryModifier affects the way in that the argument is added.
     * @param fuzzyness the fuzzyness; must be between 0 (inclusive) and 1 (exclusive), so that: 0 <= fuzzyness < 1
     * @return this
     */
    public LuceneQueryBuilder addFuzzyArgument (String value, QueryModifier modifier, double fuzzyness);

    
    
    //---------------------------
    //     addArgument
    //---------------------------
    

    
    /**
     * @param value
     * @return this
     */
    public LuceneQueryBuilder addArgument (String value);
    
    
    /**
     * 
     * @param value
     * @param mandatory
     * @return this
     */
    public LuceneQueryBuilder addArgument (String value, boolean mandatory);
    
    
    /**
     * 
     * @param value
     * @param modifier
     * @return this
     */
    public LuceneQueryBuilder addArgument (String value, QueryModifier modifier);
    
    
    /**
     * 
     * @param values
     * @return this
     */
    public LuceneQueryBuilder addArgument (Collection<?> values);
    
    
    /**
     * @param value
     * @param mandatory
     * @return this
     */
    public LuceneQueryBuilder addArgument (Collection<?> values, boolean mandatory);
    
    
    /**
     * 
     * @param values
     * @param modifier
     * @return this
     */
    public LuceneQueryBuilder addArgument (Collection<?> values, QueryModifier modifier);
    
    
    /**
     * 
     * @param <K>
     * @param values
     * @return this
     */
    public <K> LuceneQueryBuilder addArgument (K[] values);
    
    
    /**
     * @param <K>
     * @param values
     * @param mandatory
     * @return this
     */
    public <K> LuceneQueryBuilder addArgument (K[] values, boolean mandatory);
    
    
    /**
     * @param <K>
     * @param values
     * @param mandatory
     * @return this
     */
    public <K> LuceneQueryBuilder addArgument (K[] values, QueryModifier modifier);
    
    
    
    //---------------------------
    //     addArgumentAs...
    //---------------------------
    
    
    /**
     * @param values
     * @return this
     */
    public LuceneQueryBuilder addArgumentAsCollection (Collection<?> values);
    
    
    /**
     * 
     * @param values
     * @param modifier
     * @return this
     */
    public LuceneQueryBuilder addArgumentAsCollection (Collection<?> values, QueryModifier modifier);
    
    
    /**
     * Add an array of Terms to this QueryBuilder.
     * <br><br>
     * This method uses the {@link #getDefaultQueryModifier()}.
     * 
     * @param <K>
     * @param values
     * @return this
     */
    public <K> LuceneQueryBuilder addArgumentAsArray (K[] values);
    
    
    /**
     * 
     * @param <K>
     * @param values
     * @param modifier
     * @return this
     */
    public <K> LuceneQueryBuilder addArgumentAsArray (K[] values, QueryModifier modifier);
    
    
    /**
     * Add an array of Terms to this QueryBuilder.
     * <br><br>
     * This method uses the {@link #getDefaultQueryModifier()}.
     * 
     * @param values the array of terms to search for
     * @return this
     */
    public LuceneQueryBuilder addArgumentAsArray (Object values);
    
    
    /**
     * Add an array of Terms to this QueryBuilder.
     * 
     * @param values the array of terms to search for
     * @param modifier the modifier for the search of this term.
     * @return this
     */
    public LuceneQueryBuilder addArgumentAsArray (Object values, QueryModifier modifier);
    
    
    
    //---------------------------
    //     addSubquery
    //---------------------------
    
    
    /**
     * This method adds a LuceneQueryBuilder as a sub query to this QueryBuilder.
     * <br><br>
     * This method uses the {@link #getDefaultQueryModifier()}.
     * 
     * @param value the SubQuery to add
     * @return this
     */
    public LuceneQueryBuilder addSubquery (LuceneQueryBuilder value);
    
    
    /**
     * This method adds a LuceneQueryBuilder as a sub query to this QueryBuilder.
     * 
     * @param value
     * @param mandatory
     * @return this
     */
    public LuceneQueryBuilder addSubquery (LuceneQueryBuilder value, boolean mandatory);
    
    
    /**
     * This method adds a LuceneQueryBuilder as a sub query to this QueryBuilder.
     * 
     * @param value
     * @param modifiers
     * @return this
     */
    public LuceneQueryBuilder addSubquery (LuceneQueryBuilder value, QueryModifier modifiers);
    
    
    
    //---------------------------
    //     addUnescaped-methods
    //---------------------------
    
    
    /**
     * Add a field with an argument unescaped.
     * <b>Attention</b>: Use with care, otherwise you get Solr-Exceptions on execution.
     * 
     * @param key the field name
     * @param value the value of the field; 
     * @param mandatory whether the field is mandatory or not
     * @return this
     */
    public LuceneQueryBuilder addUnescapedField (String key, CharSequence value, boolean mandatory);
    
    
    /**
     * Add an argument unescaped.
     * If the parameter `value` is null then nothing happens.
     * <b>Attention</b>: Use with care, otherwise you get Solr-Exceptions on execution.
     * 
     * @param value the argument to add unescaped; omitted if null
     * @param mandatory whether the argument is mandatory or not
     * @return this
     */
    public LuceneQueryBuilder addUnescaped (CharSequence value, boolean mandatory);
    
    
    
    //---------------------------
    //     addField(String, String, ...)
    //---------------------------
    
    
    /**
     * Add a field with the name `key` to the query.
     * The searched value is given as a String.
     * <br><br>
     * This method uses the {@link #getDefaultQueryModifier()}.
     * 
     * @param key the name of the field
     * @param value the (string)-value of the field
     * @return this
     */
    public LuceneQueryBuilder addField (String key, String value);
    
    
    /**
     * 
     * @param key
     * @param value
     * @param mandatoryKey
     * @return this
     */
    public LuceneQueryBuilder addField (String key, String value, boolean mandatoryKey);
    
    
    /**
     * Append a field with a string value, and apply a boost afterwards 
     * 
     * @see LuceneQueryBuilder#addField(String, String, boolean)
     * 
     * @param key
     * @param value
     * @param mandatoryKey
     * @param boostFactor
     * @return this
     */
    public LuceneQueryBuilder addField (String key, String value, boolean mandatoryKey, double boostFactor);
    
    
    /**
     * 
     * @param key
     * @param value
     * @param mandatoryKey
     * @param boostFactor
     * @return this
     */
    public LuceneQueryBuilder addField (String key, String value, QueryModifier modifier);
    
    
    
    //---------------------------
    //     addField(String, Collection, ...)
    //---------------------------
    
    
    /**
     * Append a field with a collection of values.
     * 
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @return this
     */
    public LuceneQueryBuilder addField (String key, boolean mandatoryKey, Collection<?> value, boolean mandatoryValue);
    
    
    /**
     * Append a field with a collection of values, and apply a boost afterwards 
     * 
     * @see LuceneQueryBuilder#addField(String, boolean, Collection, boolean)
     * 
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @param boostFactor
     * @return this
     */
    public LuceneQueryBuilder addField (String key, boolean mandatoryKey, Collection<?> value, boolean mandatoryValue, double boostFactor);
    

    /**
     * Add a field with the name `key` to the query.
     * The values to search for are given in a collection.
     * <br><br>
     * This method uses the {@link #getDefaultQueryModifier()}.
     * 
     * @param key the name of the field
     * @param value
     * @return this
     */
    public LuceneQueryBuilder addField (String key, Collection<?> value);
    

    /**
     * Add a field with the name `key` to the query.
     * The values to search for are given in a collection.
     * 
     * @param key
     * @param value
     * @param modifier
     * @return this
     */
    public LuceneQueryBuilder addField (String key, Collection<?> value, QueryModifier modifier);

    
    
    //---------------------------
    //     addField(String, Array, ...)
    //---------------------------
    

    
    /**
     * Add a field with the name `key` to the query.
     * The values to search for are given in an array.
     * <br><br>
     * This method uses the {@link #getDefaultQueryModifier()}.
     * 
     * @param <K>
     * @param key the name of the field
     * @param value the values to be searched in the field
     * @param modifier the query modifier
     * @return this
     */
    public <K> LuceneQueryBuilder addField (String key, K[] value);
    
    
    /**
     * Add a field with the name `key` to the query.
     * The values to search for are given in an array.
     * 
     * @param <K>
     * @param key the name of the field
     * @param value the values to be searched in the field
     * @param modifier the query modifier
     * @return this
     */
    public <K> LuceneQueryBuilder addField (String key, K[] value, QueryModifier modifier);
    
    

    //---------------------------
    //     addFuzzyField
    //---------------------------
    
    
    /**
     * Append a fuzzy search argument with the given fuzzyness for the given field. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term. <br>
     * Less fuzzyness (closer to 0) means less accuracy, and vice versa (the closer to 1, solr yields less but accurater results)
     * <br><br>
     * This method uses the {@link #getDefaultQueryModifier()} and {@link #defaultFuzzyness}.
     * 
     * @param key the name of the field
     * @param value the value to search for
     * @param mandatoryKey if true then the field must contain the given value, otherwise it is just prioritized in the search results
     * @param fuzzyness the fuzzyness; must be between 0 and 1, so that 0 <= fuzzyness < 1
     * @return this
     */
    public LuceneQueryBuilder addFuzzyField (String key, String value);
    
    
    /**
     * Append a fuzzy search argument with default fuzzyness (0.5) for the given field. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term.
     * <br><br>
     * This method uses the {@link #defaultFuzzyness}.
     * 
     * @param key the name of the field
     * @param value the value to search for
     * @param mandatoryKey if true then the field must contain the given value, otherwise it is just prioritized in the search results
     * @return this
     */
    public LuceneQueryBuilder addFuzzyField (String key, String value, boolean mandatoryKey);
    
    
    /**
     * Append a fuzzy search argument with the given fuzzyness for the given field. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term. <br>
     * Less fuzzyness (closer to 0) means less accuracy, and vice versa (the closer to 1, solr yields less but accurater results)
     * 
     * @param key the name of the field
     * @param value the value to search for
     * @param mandatoryKey if true then the field must contain the given value, otherwise it is just prioritized in the search results
     * @param fuzzyness the fuzzyness; must be between 0 and 1, so that 0 <= fuzzyness < 1
     * @return this
     */
    public LuceneQueryBuilder addFuzzyField (String key, String value, boolean mandatoryKey, double fuzzyness);
    
    
    /**
     * Append a fuzzy search argument with the given fuzzyness for the given field. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term. <br>
     * Less fuzzyness (closer to 0) means less accuracy, and vice versa (the closer to 1, solr yields less but accurater results)
     * <br><br>
     * This method uses the {@link #defaultFuzzyness}.
     * 
     * @param key the name of the field
     * @param value the value to search for
     * @param mod the modifiers to use
     * @return this
     */
    public LuceneQueryBuilder addFuzzyField (String key, String value, QueryModifier mod);
    
    
    /**
     * Append a fuzzy search argument with the given fuzzyness for the given field. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term. <br>
     * Less fuzzyness (closer to 0) means less accuracy, and vice versa (the closer to 1, solr yields less but accurater results)
     * 
     * @param key the name of the field
     * @param value the value to search for
     * @param mod the modifiers to use
     * @param fuzzyness the fuzzyness; must be between 0 and 1, so that 0 <= fuzzyness < 1
     * @return this
     */
    public LuceneQueryBuilder addFuzzyField (String key, String value, QueryModifier mod, double fuzzyness);

    

    //---------------------------
    //     addFieldAs...
    //---------------------------
    
    /**
     * Adds a field named `key`, with the values of `value`.
     * <br>Example:
     * <pre>
     *   LuceneQueryBuilder builder = SolrQueryFactory.getConsecutiveSolrQuery();  // or any other implementation
     *   builder.setDefaultQueryModifier(
     *   List&lt;String&gt; values = new ArrayList&lt;String&gt;();
     *   values.add("test1");
     *   values.add("test2");
     *   builder.addFieldAsCollection("test", values);
     *   builder.getQuery();  // +
     * </pre>
     * 
     * @param key the name of the field
     * @param value the collection of values for the field
     * @return this
     */
    public LuceneQueryBuilder addFieldAsCollection (String key, Collection<?> value);
    
    
    /**
     * Add a field with the name `key` to the query.
     * The values to search for are given in a collection.
     * 
     * @param key
     * @param value
     * @param modifier
     * @return this
     */
    public LuceneQueryBuilder addFieldAsCollection (String key, Collection<?> value, QueryModifier modifier);
    
    
    /**
     * Append a field with a collection of values, and apply a boost afterwards 
     * 
     * @see LuceneQueryBuilder#addFieldAsCollection(String, Collection, QueryModifier)
     * 
     * @param key
     * @param value
     * @param modifier
     * @param boost
     * @return this
     */
    public LuceneQueryBuilder addFieldAsCollection (String key, Collection<?> value, QueryModifier modifier, double boost);
    
    
    /**
     * Add a field with the name `key` to the query.
     * The values to search for are given in an array.
     * 
     * @param <K>
     * @param key
     * @param value
     * @return this
     */
    public <K> LuceneQueryBuilder addFieldAsArray (String key, K[] value);
    
    
    /**
     * Add a field with the name `key` to the query.
     * The values to search for are given in an array.
     * 
     * @param <K>
     * @param key
     * @param value
     * @param modifier
     * @return this
     */
    public <K> LuceneQueryBuilder addFieldAsArray (String key, K[] value, QueryModifier modifier);
    
    
    /**
     * Add a field with the name `key` to the query.
     * The values to search for are given in an array.
     * 
     * @param key
     * @param value
     * @param modifier
     * @return this
     */
    public LuceneQueryBuilder addFieldAsArray (String key, Object value);
    
    
    /**
     * Add a field with the name `key` to the query.
     * The values to search for are given in an array.
     * 
     * @param key the name of the field
     * @param value
     * @param modifier
     * @return this
     */
    public LuceneQueryBuilder addFieldAsArray (String key, Object value, QueryModifier modifier);
    
    
    
    //---------------------------------------
    //    startField, endField, addBoost
    //---------------------------------------
    
    
    /**
     * Starts a field with `key`:(.<br>
     * <b>Attention</b>: Use this method carefully and end all fields with 
     * {@link LuceneQueryBuilder#endField()},
     * or otherwise you get Solr-Exceptions on execution.
     * @param fieldName the name of the field; omitted if null
     * @param mandatory whether the field is mandatory for execution ("+" is prepended) or not.
     * @return this
     */
    public LuceneQueryBuilder startField (String fieldName, boolean mandatory);
    
    
    /**
     * Starts a field with `key`:(.<br>
     * <b>Attention</b>: Use this method carefully and end all fields with 
     * {@link LuceneQueryBuilder#endField()},
     * or otherwise you get Solr-Exceptions on execution.
     * @param fieldName the name of the field; omitted if null
     * @param modifier the modifiers for the field (see QueryModifier for more details)
     * @return this
     */
    public LuceneQueryBuilder startField (String fieldName, QueryModifier modifier);
    
    
    /**
     * Ends a previously started field. <br>
     * <b>Attention</b>: Use this method carefully and only end fields that have been started with
     * {@link LuceneQueryBuilder#startField(String, boolean)},
     * or otherwise you get Solr-Exceptions on execution.
     * @return this
     */
    public LuceneQueryBuilder endField ();
    
    
    /**
     * Add a boost factor to the current element. <br>
     * <b>Attention</b>: Don't use this method directly after calling startField(...), 
     * or otherwise you get Solr-Exceptions on execution.
     * @param boostFactor a positive double < 10.000.000 which boosts the previously added element
     * @return this
     */
    public LuceneQueryBuilder addBoost (double boostFactor);
    

}
