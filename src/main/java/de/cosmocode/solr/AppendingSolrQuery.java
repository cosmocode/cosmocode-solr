package de.cosmocode.solr;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * <p>
 * This is a shortcut for building Solr queries. 
 * It can be directly passed to the SearchClient, which then converts it as needed.
 * </p>
 * <p>
 * <strong>Important:</strong> The SolrQuery class is not thread-safe. It should be synchronized externally.
 * </p>
 * @author olorenz
 *
 */
public class AppendingSolrQuery implements SolrQuery {
    
    public static final int MAX = 10000000;
    
    private String dtype;
    private final StringBuilder queryArguments;
    private boolean wildCarded;
    private final Map<String, Object> requestArguments = new HashMap<String, Object>();
    
    private static final Logger logger = Logger.getLogger(AppendingSolrQuery.class);
    public static final double defaultFuzzyness = 0.5;
    
    public static final QueryModifier defaultModifier = new QueryModifier();
    
    /**
     * This one should be used for subqueries only.
     */
    public AppendingSolrQuery () {
        this.queryArguments = new StringBuilder();
        this.setStart(0);
        this.setMax(10);
        this.setWildCarded(true);
    }
    
    /**
     * Constructs a new SolrQuery from the given DType (DataType).
     * @param dtype
     */
    public AppendingSolrQuery (final String dtype) {
        this (dtype, true);
    }
    
    /**
     * Constructs a new SolrQuery from the given DType (DataType),false
     * and whether all arguments should be wildCarded or not.
     * @param dtype
     * @param wildCarded
     * @param args
     */
    public AppendingSolrQuery (final String dtype, final boolean wildCarded, final Object... args) {
        this.queryArguments = new StringBuilder();
        if (dtype != null) {
            addUnescapedField("dtype_s", dtype, true);
            this.dtype = dtype;
        } else {
            throw new IllegalArgumentException("SolrQuery needs a dtype");
        }
        this.setWildCarded(wildCarded);
        
        this.setStart(0);
        this.setMax(10);
        
        if (args != null && args.length > 0) {
            final QueryModifier mod = new QueryModifier(TermModifier.NONE, false, wildCarded, false);
            for (Object arg : args) {
                addArgument(arg, mod);
            }
        }
    }
    
    
    public boolean isWildCarded() {
        return wildCarded;
    }

    public void setWildCarded(boolean wildCarded) {
        this.wildCarded = wildCarded;
    }

    /**
     * @return the selected fields for the query - cannot be null, but "*" indicates that all are selected.
     */
    public String getSelectFields() {
        if (requestArguments.containsKey("fl")) {
            Object fl = requestArguments.get("fl");
            if (fl != null) return fl.toString();
        }
        return "*";
    }

    /**
     * @param selectFields the fields returned by the query - can be null; null and "*" indicate that all are selected.
     */
    public void setSelectFields(final String selectFields) {
        requestArguments.put("fl", selectFields);
    }
    
    public void selectFields(String... fields) {
        setSelectFields(StringUtils.join(fields, ","));
    }

    /**
     * @return the fields on which the result is sorted on - can be null.
     */
    public String getSortFields() {
        if (requestArguments.containsKey("sort")) {
            Object fl = requestArguments.get("sort");
            if (fl != null) return fl.toString();
        }
        return null;
    }

    /**
     * @param sortFields the fields on which the result is sorted on - can be null.
     */
    public void setSortFields(String sortFields) {
        requestArguments.put("sort", sortFields);
    }

    /**
     * @return the maximum number of results/documents that this query will return.
     */
    public int getMax() {
        return getRows();
    }

    /** 
     * @param max the maximum number of results/documents that this query will return.
     */
    public void setMax(final int max) {
        setRows(max);
    }

    /**
     * @return the maximum number of results/documents that this query will return.
     */
    public int getRows() {
        return (Integer)requestArguments.get("rows");
    }

    /**
     * @param rows the maximum number of results/documents that this query will return.
     */
    public void setRows(final int rows) {
        final int absMax = (rows < 0) ? Math.abs(rows) : rows;
        requestArguments.put("rows", absMax);
    }

    /**
     * @return the number of the first element returned
     */
    public int getStart() {
        return (Integer)requestArguments.get("start");
    }

    /**
     * @param start the number of the first element returned
     */
    public void setStart(final int start) {
        if (start < 0) throw new IllegalArgumentException ("start must be a non-negative integer");
        requestArguments.put("start", start);
    }
    
    /**
     * Directly set a request argument.
     * <h4>The following request argument names are not allowed and must be set through their setters (or other methods):</h4>
     * <ul>
     * <li>q  --  use the addArgument-, addArrayArgument-, addUnescaped-, addField- and addFieldAsArray-methods
     * <li>start  --  {@link #setStart(int)}
     * <li>rows  --  {@link #setRows(int)}
     * </ul>
     * @param name the name of the request argument; must not be one of: q, start, rows
     * @param value the value of that argument
     */
    public void setRequestArgument(final String name, final String value) {
        if ("q".equalsIgnoreCase(name)) throw new IllegalArgumentException("the Request argument q cannot be directly assigned, use the addArgument-methods");
        if ("start".equalsIgnoreCase(name)) throw new IllegalArgumentException("the Request argument start cannot be directly assigned, use setStart(int)");
        if ("rows".equalsIgnoreCase(name)) throw new IllegalArgumentException("the Request argument rows cannot be directly assigned, use setRows(int)");
        requestArguments.put(name, value);
    }
    
    
    /**
     * Add a facet field to this query.<br>
     * A facet field is a list of the count of each of the multiple values of a field.
     * It can be fetched from a SearchResult via the getFacetFields() method.
     * @param facetFieldName the facet field to add to this query; null is omitted
     */
    @SuppressWarnings("unchecked")
    public void addFacetField (final String facetFieldName) {
        if (facetFieldName != null) {
            Set<String> facetFields = new HashSet<String>();
            if (!requestArguments.containsKey("facet.field")) {
                facetFields = new HashSet<String>();
                requestArguments.put("facet.field", facetFields);
                requestArguments.put("facet", true);
            } else {
                try {
                    facetFields = (Set<String>)requestArguments.get("facet.field");
                } catch (ClassCastException e) { logger.warn("Cannot get facet.field", e); }
            }
            facetFields.add(facetFieldName);
        }
    }
    
    /**
     * Add multiple facet fields to this query.<br>
     * A facet field is a list of the count of each of the multiple values of a field.
     * It can be fetched from a SearchResult via the getFacetFields() method.
     * @param facetFields the facet fields to add to this query; null is omitted
     */
    public void addFacetFields (final String... facetFields) {
        if (facetFields == null) return;
        
        for (final String facetField : facetFields) {
            this.addFacetField(facetField);
        }
    }
    
    /**
     * @return the query which is committed to solr, i.e. the request argument "q"
     */
    public String getQuery() {
        return this.queryArguments.toString();
    }
    
    /**
     * @param logRequestArgs if true, then SolrQuery logs the returned map in log4j. Otherwise not.
     * @return the request arguments in a new HashMap 
     * (no modifications on this SolrQuery possible)
     */
    public Map<String, Object> getRequestArguments(final boolean logRequestArgs) {
     // put requests into a separate map to prevent modification and to add the query
        final Map<String, Object> requestArgs = new HashMap<String, Object>(this.requestArguments);
        requestArgs.put("q", this.queryArguments);
        if (logRequestArgs && logger.isDebugEnabled()) logger.debug(requestArgs.toString());
        return requestArgs;
    }
    
    /**
     * @return the request arguments in a new HashMap 
     * (no modifications on this SolrQuery possible)
     */
    public Map<String, Object> getRequestArguments() {
        return this.getRequestArguments(true);
    }
    
    
    /**
     * @return the keys of the request arguments
     */
    public Set<Map.Entry<String, Object>> getRequestArgumentSet() {
        return getRequestArguments(true).entrySet();
    }
    

    /**
     * @return the dtype
     */
    public String getDtype() {
        return dtype;
    }
    
    
    
    //---------------------------
    //     addArgument-methods
    //---------------------------
    
    // shortcuts
    public AppendingSolrQuery and() {
        return this.addUnescaped("AND", false);
    }
    public AppendingSolrQuery or() {
        return this.addUnescaped("OR", false);
    }
    public AppendingSolrQuery not() {
        return this.addUnescaped("NOT", false);
    }
    

    @Override
    public AppendingSolrQuery addFuzzyArgument (final String value, boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = new QueryModifier(tm, false, wildCarded, false);
        return this.addFuzzyArgument(value, mod, defaultFuzzyness);
    }
    
    
    @Override
    public AppendingSolrQuery addFuzzyArgument (final String value, boolean mandatory, double fuzzyness) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = new QueryModifier(tm, false, wildCarded, false);
        return this.addFuzzyArgument(value, mod, fuzzyness);
    }
    
    
    @Override
    public AppendingSolrQuery addFuzzyArgument (final String value, final QueryModifier modifier, final double fuzzyness) {
        if (fuzzyness < 0.0 || fuzzyness >= 1.0)
            throw new IllegalArgumentException("fuzzyness must be greater than equals 0 and less than 1 (i.e. 0 <= fuzzyness < 1)");
        
        if (StringUtils.isNotBlank(value) && modifier != null) {
            queryArguments.append(modifier.getTermPrefix());
            
            queryArguments.append("(")
                .append(escapeQuotes(escapeInput(value, true)))
                .append("~").append(fuzzyness)
            .append(") ");
        }
        
        return this;
    }

    
    @Override
    public AppendingSolrQuery addArgument (final String value, final boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = new QueryModifier(tm, false, wildCarded, false);
        return this.addArgument(value, mod);
    }
    
    
    @Override
    public AppendingSolrQuery addArgument (final String value, final QueryModifier modifier) {
        // TODO: implement isSplit() of QueryModifier
        if (StringUtils.isNotBlank(value) && modifier != null) {
            queryArguments.append(modifier.getTermPrefix());
            
            if (modifier.isWildcarded()) {
                // search for input wildcarded (wildcard is appended at the end). 
                // the original input is added, too, because e.g. adidas* doesn't match "adidas" on text-fields
                queryArguments.append("(")
                    .append("\"").append(escapeQuotes(value)).append("\"^2")
                    .append(" ").append(escapeQuotes(escapeInput(value, true))).append("*")
                .append(")");
            } else {
                queryArguments.append(escapeQuotes(escapeInput(value, true)));
            }
            queryArguments.append(" ");
        }
        
        return this;
    }
    
    
    // add collection and array
    
    @Override
    public AppendingSolrQuery addArgument (final Collection<?> value, final boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = new QueryModifier(tm, false, wildCarded, false);
        return this.addArgumentAsCollection(value, mod);
    }
    
    
    @Override
    public <K> AppendingSolrQuery addArgument (final K[] values, final QueryModifier modifiers) {
        return this.addArgumentAsArray(values, modifiers);
    }
    
    
    @Override
    public AppendingSolrQuery addArgumentAsCollection (final Collection<?> values, final QueryModifier modifiers) {
        if (values != null && values.size() > 0) {
            // start
            queryArguments.append("(");

            // add items
            for (Object val : values) {
                addArgument(val, modifiers);
            }

            // end
            if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
                // if the just opened bracket is still the last character, then revert it
                queryArguments.setLength(queryArguments.length() - 1);
            } else {
                queryArguments.append(") ");
            }
        }
        
        return this;
    }
    
    
    @Override
    public <K> AppendingSolrQuery addArgumentAsArray (final K[] values, final QueryModifier modifiers) {
        if (values != null && values.length > 0) {
            // start
            queryArguments.append("(");
            
            // add items
            for (K val : values) {
                addArgument(val, modifiers);
            }
            
            // end
            if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
                // if the just opened bracket is still the last character, then revert it
                queryArguments.setLength(queryArguments.length() - 1);
            } else {
                queryArguments.append(") ");
            }
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addArgumentAsArray (Object values, final QueryModifier modifiers) {
        if (values != null && values.getClass().isArray() && Array.getLength(values) > 0) {
            final int arrayLength = Array.getLength(values);
            
            // start
            queryArguments.append("(");
            
            // add all items
            for (int i = 0; i < arrayLength; i++) {
                addArgument(Array.get(values, i), modifiers);
            }
            
            // end
            if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
                // if the just opened bracket is still the last character, then revert it
                queryArguments.setLength(queryArguments.length() - 1);
            } else {
                queryArguments.append(") ");
            }
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addSubquery (final SolrQuery value, final boolean mandatory) {
        if (value != null) {
            if (mandatory) queryArguments.append("+");
            queryArguments.append("(").append(value.getQuery()).append(") ");
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addSubquery (final SolrQuery value, final QueryModifier modifiers) {
        if (value != null) {
            queryArguments.append(modifiers.getTermPrefix());
            queryArguments.append("(").append(value.getQuery()).append(") ");
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addArgument (final Object value, final QueryModifier modifiers) {
        if (value == null) return this;
        
        if (value instanceof String) {
            return this.addArgument(value.toString(), modifiers);
        } else if (value instanceof Collection<?>) {
            return this.addArgumentAsCollection((Collection<?>)value, modifiers);
        } else if (value.getClass().isArray()) { 
            return this.addArgumentAsArray(value, modifiers);
        } else if (value instanceof AppendingSolrQuery) {
            return this.addSubquery((AppendingSolrQuery)value, modifiers);
        } else {
            return this.addArgument(value.toString(), modifiers);
        }
    }
    
    
    
    //---------------------------
    //     addUnescaped-methods
    //---------------------------
    
    
    @Override
    public AppendingSolrQuery addUnescapedField (final String key, final CharSequence value, final boolean mandatory) {
        if (key == null || value == null) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(key).append(":").append(value).append(" ");
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addUnescaped (final CharSequence value, final boolean mandatory) {
        if (value == null) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(value).append(" ");
        
        return this;
    }
    
    
    
    //---------------------------
    //     addField-methods
    //---------------------------
    
    
    @Override
    public AppendingSolrQuery addField (final String key, final Object value, final QueryModifier modifiers) {
        if (value instanceof Collection<?>) {
            return this.addFieldAsCollection(key, (Collection<?>)value, modifiers);
        } else if (value instanceof String) {
            return this.addField(key, (String)value, modifiers);
        } else if (value != null && value.getClass().isArray()) {
            return this.addFieldAsArray(key, value, modifiers);
        }
        
        // default implementation: no check possible
        if (StringUtils.isNotBlank(key) && value != null) {
            startField(key, modifiers);
            addArgument(value, modifiers);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addField (final String key, final String value, final boolean mandatoryKey) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = new QueryModifier(tm, false, wildCarded, false);
        return this.addField(key, value, mod);
    }
    
    
    @Override
    public AppendingSolrQuery addField (String key, String value, boolean mandatoryKey, double boostFactor) {
        return this.addField(key, value, mandatoryKey).addBoost(boostFactor);
    }
    
    
    @Override
    public AppendingSolrQuery addField (final String key, final String value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            this.startField(key, modifiers);
            this.addArgument(value, modifiers);
            if (value.contains(" ")) {
                queryArguments.append("(");
                for (String token : value.split(" ")) {
                    this.addArgument(token, modifiers);
                }
                queryArguments.append(")^0.5");
            }
            this.endField();
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addFuzzyField (final String key, final String value, final boolean mandatoryKey) {
        return this.addFuzzyField(key, value, mandatoryKey, defaultFuzzyness);
    }
    
    
    @Override
    public AppendingSolrQuery addFuzzyField (final String key, final String value, final boolean mandatoryKey, final double fuzzyness) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            this.startField(key, mandatoryKey);
            this.addFuzzyArgument(value, false, fuzzyness);
            if (value.contains(" ")) {
                queryArguments.append("(");
                for (String token : value.split(" ")) {
                    this.addFuzzyArgument(token, false, fuzzyness);
                }
                queryArguments.append(")^0.5");
            }
            this.endField();
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addField (String key, boolean mandatoryKey, Collection<?> value, boolean mandatoryValue) {
        if (StringUtils.isNotBlank(key) && value != null && value.size() > 0) {
            startField(key, mandatoryKey);
            addArgument(value, mandatoryValue);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addField (String key, boolean mandatoryKey, Collection<?> value, boolean mandatoryValue, double boostFactor) {
        return this.addField(key, mandatoryKey, value, mandatoryValue).addBoost(boostFactor);
    }
    
    
    @Override
    public <K> AppendingSolrQuery addField (final String key, final K[] value, final QueryModifier modifiers) {
        return this.addFieldAsArray(key, value, modifiers);
    }

    
    @Override
    public AppendingSolrQuery addFieldAsCollection (final String key, final Collection<?> value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && value != null && value.size() > 0) {
            startField(key, modifiers);
            addArgumentAsCollection(value, modifiers);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addFieldAsCollection (final String key, final Collection<?> value, final QueryModifier modifiers, final double boost) {
        return this.addFieldAsCollection(key, value, modifiers).addBoost(boost);
    }
    
    
    @Override
    public <K> AppendingSolrQuery addFieldAsArray (final String key, final K[] value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && value != null && value.length > 0) {
            startField(key, modifiers);
            addArgumentAsArray(value, modifiers);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addFieldAsArray (final String key, final Object value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && value != null && value.getClass().isArray() && Array.getLength(value) > 0) {
            startField(key, modifiers);
            addArgumentAsArray(value, modifiers);
            endField();
        }
        
        return this;
    }
    

    //---------------------------
    //  helper methods
    //---------------------------
    
    @Override
    public AppendingSolrQuery startField (final String fieldName, final boolean mandatory) {
        if (StringUtils.isBlank(fieldName)) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(fieldName).append(":(");
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery startField (final String fieldName, final QueryModifier modifier) {
        if (StringUtils.isBlank(fieldName)) return this;
        
        queryArguments.append(modifier.getTermPrefix());
        queryArguments.append(fieldName).append(":(");
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery endField () {
        if (queryArguments.charAt(queryArguments.length()-1) == '(') {
            // add an empty string, if the field was ended right after it was started
            queryArguments.append("\"\"");
        }
        queryArguments.append(") ");
        
        return this;
    }
    
    
    @Override
    public AppendingSolrQuery addBoost (final double boostFactor) {
        if (boostFactor <= 0.0 || boostFactor >= 10000000.0)
            throw new IllegalArgumentException("boostFactor must be greater than 0 and less than 10.000.000 (10 Mio.)");
        
        if (boostFactor != 1.0) { // optimization: only add boost factor if != 1
            final double rounded = ((int)(boostFactor * 100.0)) / 100.0;
            this.queryArguments.append("^").append(rounded).append(" ");
        }

        return this;
    }
    
    
    @Override
    public String toString() {
        return getRequestArguments(false).toString();
    }

    
    //---------------------------
    //   public helper methods
    //---------------------------
    
    // escape +,\,&,|,!,(,),{,},[,],^,~,?,*,: and blanks  with "\"
    public static final Pattern escapePattern         = Pattern.compile("[\\Q+\\&|!(){}[]^~?*:; \\E]");
    // escape +,\,&,|,!,(,),{,},[,],^,~,?,*,: with "\"
    public static final Pattern escapeNoBlanksPattern = Pattern.compile("[\\Q+\\&|!(){}[]^~?*:;\\E]");
    // remove -
    public static final Pattern removePattern         = Pattern.compile("[\\-]");
    // remove - and blanks
    public static final Pattern removeBlanksPattern   = Pattern.compile("[\\- ]");
    
    public static final Pattern quotesPattern         = Pattern.compile("[\"]");
    
    
    /**
     * Escapes quotes (") in a given input (" => \")
     * @param input
     * @return
     */
    public static String escapeQuotes (final String input) {
        if (input == null) return "";
        return quotesPattern.matcher(input).replaceAll("\\\\$0");
    }
    
    
    /**
     * Removes quotes (") from a given input
     * @param input
     * @return
     */
    public static String removeQuotes (final String input) {
        if (input == null) return "";
        return quotesPattern.matcher(input).replaceAll("");
    }
    
    
    /**
     * Escapes special chars for solr.<br>
     * Special chars are: +,\,&,|,!,(,),{,},[,],^,~,?,*,:.<br>
     * If `escapeBlanks` is true, then blanks are escaped, otherwise they are removed
     * They are escaped with "\".<br>
     * <br>
     * This function was taken (with heavy modifications) from
     * http://www.javalobby.org/java/forums/t86124.html
     * @param input the input to escape
     * @return the input, escaped for solr
     */
    public static String escapeInput (final String input, final boolean escapeBlanks) {
        if (input == null) return "";
        return 
        (escapeBlanks ? escapePattern : escapeNoBlanksPattern).matcher(
            (escapeBlanks ? removePattern : removeBlanksPattern).matcher(
                input
            ).replaceAll("")
        ).replaceAll("\\\\$0");
    }
    

}
