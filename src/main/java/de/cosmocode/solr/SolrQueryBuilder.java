package de.cosmocode.solr;

import java.util.Collection;

public interface SolrQueryBuilder {
    
    public static final int MAX = 10000000;
    
    // TODO: JavaDoc
    
    
    //---------------------------
    //     addFuzzyArgument
    //---------------------------

    
    /**
     * Append a fuzzy argument with default fuzzyness of 0.5. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term.
     * 
     * @see #addFuzzyArgument(String, double, boolean)
     * @param value the value to search for
     * @param mandatory if true then the value must be found, otherwise it is just prioritized in the search results
     * @return this
     */
    public SolrQuery addFuzzyArgument (final String value, boolean mandatory);
    
    
    /**
     * Append a fuzzy argument with the given fuzzyness. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term.
     * 
     * @param value the value to search for
     * @param fuzzyness the fuzzyness; must be between 0 (inclusive) and 1 (exclusive), so that: 0 <= fuzzyness < 1
     * @param mandatory if true then the value must be found, otherwise it is just prioritized in the search results
     * @return this
     */
    public SolrQuery addFuzzyArgument (final String value, boolean mandatory, double fuzzyness);
    
    
    /**
     * Append a fuzzy argument with the given fuzzyness. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term.
     * 
     * @param value the value to search for
     * @param fuzzyness the fuzzyness; must be between 0 (inclusive) and 1 (exclusive), so that: 0 <= fuzzyness < 1
     * @param mandatory if true then the value must be found, otherwise it is just prioritized in the search results
     * @return this
     */
    public SolrQuery addFuzzyArgument (final String value, final QueryModifier modifiers, final double fuzzyness);

    
    
    //---------------------------
    //     addArgument
    //---------------------------
    
    
    /**
     * 
     * @param value
     * @param mandatory
     */
    public SolrQuery addArgument (final String value, final boolean mandatory);
    
    
    /**
     * 
     * @param value
     * @param mandatory
     */
    public SolrQuery addArgument (final String value, final QueryModifier modifiers);
    
    
    /**
     * This is a utility method, that determines what to call based on "instanceof".
     * 
     * @param value the query argument to add; null is omitted
     * @param mandatory whether the argument is mandatory or not
     * @return this
     */
    public SolrQuery addArgument (final Object value, final QueryModifier modifiers);
    
    
    /**
     * @param value
     * @param mandatory
     * @return this
     */
    public SolrQuery addArgument (Collection<?> value, boolean mandatory);
    
    
    /**
     * @param <K>
     * @param values
     * @param mandatory
     * @return this
     */
    public <K> SolrQuery addArgument (final K[] values, final QueryModifier modifiers);
    
    
    
    //---------------------------
    //     addArgumentAs...
    //---------------------------
    
    
    /**
     * @param values
     * @param mandatory
     * @return this
     */
    public SolrQuery addArgumentAsCollection (Collection<?> values, final QueryModifier modifiers);
    
    
    /**
     * 
     * @param <K>
     * @param values
     * @param modifiers
     * @return this
     */
    public <K> SolrQuery addArgumentAsArray (final K[] values, final QueryModifier modifiers);
    
    
    /**
     * 
     * @param values
     * @param modifiers
     * @return this
     */
    public SolrQuery addArgumentAsArray (Object values, final QueryModifier modifiers);
    
    
    
    //---------------------------
    //     addSubquery
    //---------------------------
    
    
    /**
     * @param value
     * @param mandatory
     * @return this
     */
    public SolrQuery addSubquery (final SolrQuery value, final boolean mandatory);
    
    
    /**
     * @param value
     * @param mandatory
     * @return this
     */
    public SolrQuery addSubquery (final SolrQuery value, final QueryModifier modifiers);
    
    
    
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
    public SolrQuery addUnescapedField (final String key, final CharSequence value, final boolean mandatory);
    
    
    /**
     * Add an argument unescaped.
     * If the parameter `value` is null then nothing happens.
     * <b>Attention</b>: Use with care, otherwise you get Solr-Exceptions on execution.
     * 
     * @param value the argument to add unescaped; omitted if null
     * @param mandatory whether the argument is mandatory or not
     * @return this
     */
    public SolrQuery addUnescaped (final CharSequence value, final boolean mandatory);
    
    
    
    //---------------------------
    //     addField
    //---------------------------
    
    
    /**
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @return this
     */
    public SolrQuery addField (final String key, final Object value, final QueryModifier modifiers);
    
    
    /**
     * 
     * @param key
     * @param value
     * @param mandatoryKey
     * @return this
     */
    public SolrQuery addField (final String key, final String value, final boolean mandatoryKey);
    
    
    /**
     * Append a field with a string value, and apply a boost afterwards 
     * 
     * @see SolrQuery#addField(String, String, boolean)
     * 
     * @param key
     * @param value
     * @param mandatoryKey
     * @param boostFactor
     * @return this
     */
    public SolrQuery addField (String key, String value, boolean mandatoryKey, double boostFactor);
    
    
    /**
     * 
     * @param key
     * @param value
     * @param mandatoryKey
     * @param boostFactor
     * @return this
     */
    public SolrQuery addField (final String key, final String value, final QueryModifier modifiers);
    
    
    /**
     * 
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @return this
     */
    public SolrQuery addField (final String key, final boolean mandatoryKey, final Collection<?> value, final boolean mandatoryValue);
    
    
    /**
     * Append a field with a collection of values, and apply a boost afterwards 
     * 
     * @see SolrQuery#addField(String, boolean, Collection, boolean)
     * 
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @param boostFactor
     * @return this
     */
    public SolrQuery addField (final String key, final boolean mandatoryKey, final Collection<?> value, final boolean mandatoryValue, final double boostFactor);

    
    
    /**
     * 
     * @param <K>
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @return this
     */
    public <K> SolrQuery addField (final String key, final K[] value, final QueryModifier modifiers);
    
    

    //---------------------------
    //     addFuzzyField
    //---------------------------
    
    
    /**
     * Append a fuzzy search argument with default fuzzyness (0.5) for the given field. <br>
     * fuzzy searches include arguments that are in the levenshtein distance of the searched term.
     * 
     * @param key the name of the field
     * @param value the value to search for
     * @param mandatoryKey if true then the field must contain the given value, otherwise it is just prioritized in the search results
     * @return this
     */
    public SolrQuery addFuzzyField (final String key, final String value, final boolean mandatoryKey);
    
    
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
    public SolrQuery addFuzzyField (final String key, final String value, final boolean mandatoryKey, final double fuzzyness);

    

    //---------------------------
    //     addFieldAs...
    //---------------------------
    
    /**
     * 
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @param boostFactor
     * @return this
     */
    public SolrQuery addFieldAsCollection (final String key, final Collection<?> value, final QueryModifier modifiers);
    
    
    /**
     * Append a field with a collection of values, and apply a boost afterwards 
     * 
     * @see SolrQuery#addFieldAsCollection(String, Collection, QueryModifier)
     * 
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @param boostFactor
     * @return this
     */
    public SolrQuery addFieldAsCollection (final String key, final Collection<?> value, final QueryModifier modifiers, final double boost);
    
    
    /**
     * 
     * @param <K>
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @return this
     */
    public <K> SolrQuery addFieldAsArray (final String key, final K[] value, final QueryModifier modifiers);
    
    
    /**
     * Add a field 
     * 
     * @param key
     * @param mandatoryKey
     * @param value
     * @param mandatoryValue
     * @return this
     */
    public SolrQuery addFieldAsArray (final String key, final Object value, final QueryModifier modifiers);
    
    
    
    //---------------------------------------
    //    startField, endField, addBoost
    //---------------------------------------
    
    
    /**
     * Starts a field with `key`:(.<br>
     * <b>Attention</b>: Use this method carefully and end all fields with 
     * {@link SolrQuery#endField()},
     * or otherwise you get Solr-Exceptions on execution.
     * @param fieldName the name of the field; omitted if null
     * @param mandatory whether the field is mandatory for execution ("+" is prepended) or not.
     * @return this
     */
    public SolrQuery startField (final String fieldName, final boolean mandatory);
    
    
    /**
     * Starts a field with `key`:(.<br>
     * <b>Attention</b>: Use this method carefully and end all fields with 
     * {@link SolrQuery#endField()},
     * or otherwise you get Solr-Exceptions on execution.
     * @param fieldName the name of the field; omitted if null
     * @param mandatory whether the field is mandatory for execution ("+" is prepended) or not.
     * @return this
     */
    public SolrQuery startField (final String fieldName, final QueryModifier modifiers);
    
    
    /**
     * Ends a previously started field. <br>
     * <b>Attention</b>: Use this method carefully and only end fields that have been started with
     * {@link SolrQuery#startField(String, boolean)},
     * or otherwise you get Solr-Exceptions on execution.
     * @return this
     */
    public SolrQuery endField ();
    
    
    /**
     * Add a boost factor to the current element. <br>
     * <b>Attention</b>: Don't use this method directly after calling startField(...), 
     * or otherwise you get Solr-Exceptions on execution.
     * @param boostFactor a positive double < 10.000.000 which boosts the previously added element
     * @return this
     */
    public SolrQuery addBoost (final double boostFactor);
    

}
